//
// JScript: launch dropbox or web conferences.
// Arguments are: { -dropbox | -conf } [-url url]
//

function LaunchDS(args) {
  // Are we running in command line mode?
  var PROC = WScript.FullName;
  var isCmdLine = false;

  if (PROC.lastIndexOf("cscript.exe") == PROC.length - 11) {
    isCmdLine = true;
  }

  // Determine installation point from script location.
  var scriptPath = WScript.ScriptFullName;
  var installPoint = scriptPath.substring(0,scriptPath.lastIndexOf(WScript.ScriptName));

  // Process arguments args parameter is a WScript.Arguments object)
  // var args = WScript.Arguments;

  // Need at least -dropbox or -conf.
  if (args.length == 0) {
    // Error and exit.
    WScript.echo("Missing required parameter: -dropbox or -conf\nUsage: startds.js {-dropbox | -conf} [-url URL]");
    return 1;
  }

  var CMD;
  var URL = "";
  var URLVAR;

  if (args(0) == "-dropbox") {
    CMD = "XFR";
    URLVAR = "DBOXURL";
  }
  else if (args(0) == "-conf") {
    CMD = "NEWODC";
    URLVAR = "CONFURL";
  }
  else {
    // Error and exit.
    WScript.echo("Unknown parameter: " + args(0));
    WScript.echo("Usage: startds.js {-dropbox | -conf} [-url URL]");
    return 1;
  }

  // Handle -url if present, otherwise get current url.
  if (args.length > 1) {
    if (args(1) == "-url") {
      if (args.length > 2) {
        URL = args(2);
        URLVAR = "";
      }
      else {
        // Error and exit.
        WScript.echo("-url requires a value.");
        WScript.echo("Usage: startds.js {-dropbox | -conf} [-url URL]");
        return 1;
      }
    }
    else {
      // Error and exit.
      WScript.echo("Unknown parameter: " + args(1));
      WScript.echo("Usage: startds.js {-dropbox | -conf} [-url URL]");
      return 1;
    }
  }

  // edesign.ini properties we need to launch the tunnel.
  var JREVAR = "WINJREPATH";
  var JREPARMVAR = "WINJREPARMS";
  var CLASSVAR = "TUNNELCLASS";
  var CLASSPATHVAR = "TUNNELCLASSPATH";

  // Property values.
  var JREPATH = "";
  var JREPARMS = "";
  var CLASS = "";
  var CLASSPATH = "";

  // Read the edesign.ini file and extract the needed property values.
  var FSO = new ActiveXObject("Scripting.FileSystemObject");

  try {
     var f = FSO.OpenTextFile(installPoint + "/edesign.ini", 1, false);

     while (!f.AtEndOfStream) {
        var s = f.readline();
        var i = s.indexOf("=");
        if (i > 0) {
           var key = s.substring(0, i);
           var val = s.substring(i+1);
           // Need to convert %2f to '/'
           i = val.indexOf("%2f");
           while (i != -1) {
             val = val.substring(0,i) + "/" + val.substring(i+3,val.length);
             i = val.indexOf("%2f");
           }
           key = key.toUpperCase();
           if (key == JREVAR) JREPATH = val;
           else if (key == JREPARMVAR) JREPARMS = val;
           else if (key == CLASSVAR) CLASS = val;
           else if (key == CLASSPATHVAR) CLASSPATH = val;
           else if (key == URLVAR) URL = val;
        }
     }
     f.close();
  } catch (e) {
    // Problems while access edesign.ini file. Error and exit.
    WScript.echo("Problem encountered while accessing edesign.ini");
    WScript.echo(e);
    return 1;
  }

  if (JREPATH == "" || CLASS == "" || CLASSPATH == "" || URL == "") {
    // Missing variables in edesign.ini, Error and exit.
    WScript.echo("edesign.ini missing " + JREVAR + ", " + CLASSVAR + ", " + CLASSPATHVAR + ", or " + URLVAR + ".");
    return 1;
  }

  // Get a shell to run JVM.
  var shell = WScript.CreateObject("WScript.Shell");
  var env = shell.Environment("PROCESS");
  var CPPARMS = "";

  // Change to determined directory
  shell.CurrentDirectory = installPoint;

  // If JREPATH references jre.exe, we need to use -cp option.
  if (JREPATH.lastIndexOf("jre.exe") == JREPATH.length - 7) {
    CPPARMS = " -cp " + CLASSPATH;
  }
  // Otherwise, we use the CLASSPATH environment variable.
  else {
    env("CLASSPATH") = CLASSPATH;
  }

  // For window mode, we'd really like the w.exe version of the JVM.
  // Otherwise, we'll get a DOS window.
  var exePos = JREPATH.lastIndexOf(".exe");

  if (! isCmdLine && exePos == JREPATH.length - 4) {
    var wJrePath = JREPATH.substring(0,exePos) + "w.exe";
    if (FSO.FileExists(wJrePath)) {
      JREPATH = wJrePath;
    }
  }

  // Write the input parameters to a temporary file.
  var TEMP = FSO.GetSpecialFolder(2);
  var tname = FSO.GetTempName();
  var tfile = TEMP.CreateTextFile(tname);
  tfile.WriteLine("Tunnel");
  tfile.WriteLine("-URL " + URL);
  tfile.WriteLine("-CH_TUNNELCOMMAND " + CMD);
  tfile.WriteLine("-THE_END");
  tfile.Close();

  var tpath = TEMP.Path + "\\" + tname;

  // Start the tunnel using JREPATH CPPARMS JREPARMS CLASS

  // Use exec if cmdline mode, and we use run otherwise.
  var PGM = "\"" + JREPATH + "\"" + CPPARMS + " " + JREPARMS + " " + CLASS + " " + tpath;
  var rc;

  if (isCmdLine) {
    var tunnel = shell.exec(PGM);

    // Now wait for the program to end.
    while (tunnel.Status == 0) {
      WScript.Sleep(100);
    }

    rc = tunnel.ExitCode;
  }
  else {
    rc = shell.run(PGM, 10, true);
  }

  // Delete the temporary file.
  FSO.GetFile(tpath).Delete(true);

  // Exit with applicable return code.
  return rc;
}