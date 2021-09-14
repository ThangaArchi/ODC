//
// JScript: parse edesign.ini and output batch statements with Dropbox parms
//
found = false;
try {
   fso = new ActiveXObject("Scripting.FileSystemObject");
   f = fso.opentextfile(WScript.Arguments.item(0), 1, false);

   // Write out curdir, if ends in backslash, append dot. This because
   // using "%curdir%" in bat file, then ending backslash escapes the quote!!
   folder = fso.GetFolder(".");
   folderpath = folder.Path;
   if (folderpath.substring(folderpath.length-1) == "\\") {
      folderpath = folderpath + ".";
   }
   WScript.Echo("set curdir=" + folderpath);
   javaparms="set javaparms=";
   classp="set CLASSPATH=";
   while (!f.AtEndOfStream) {
      s = f.readline();
      i = s.indexOf("=");
      if (i > 0) {
         key = s.substring(0, i);
         val = s.substring(i+1);
         key = key.toUpperCase();
         if (key == "WINJREPATH" || key == "DROPCMDLINECLASS" || key == "DROPCMDLINECLASSPATH") {
            WScript.Echo("set " + key + "="+ val);
            if (key == "WINJREPATH") {
               if (val.indexOf("jre.exe") == val.length - 7) {
                  javaparms="set javaparms=-cp \"%DROPCMDLINECLASSPATH%\"";
               } else {
                  classp="set CLASSPATH=\"%DROPCMDLINECLASSPATH%\"";
               }
            }
            found = true
         }
      }
   }
   WScript.Echo(javaparms);
   WScript.Echo(classp);
   WScript.Echo("set DBOXDRIVE=" + fso.getDriveName(WScript.Arguments(0)));
   WScript.Echo("%DBOXDRIVE%");
   WScript.Echo("cd \"%INSTALLPOINT%\"");
   WScript.Echo("\"%WINJREPATH%\" %javaparms% \"%DROPCMDLINECLASS%\" -startdir \"%curdir%\" %1 %2 %3 %4 %5 %6 %7 %8 %9");
   f.close()
} catch (e) {
   // WScript.Echo(e.description);
}

if (!found) WScript.Echo("echo \"Java/file[" + WScript.Arguments.item(0) + "] not found!\"");
