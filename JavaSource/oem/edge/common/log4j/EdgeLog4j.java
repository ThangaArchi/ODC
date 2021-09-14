package oem.edge.common.log4j;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.servlet.http.*;
import org.apache.log4j.*;
import org.apache.log4j.helpers.*;


public class EdgeLog4j
{
  /**
     get an instance of EdgeCategory
   */
    public static boolean IsFirstInstanceForAll=true;
    public static String EDGE_CONFIGURATION_FILE = "log4j.properties";
    public static String EDGE_CONFIGURATOR_CLASS_KEY="log4j.configuratorClass";
    static final public String EDGE_CONFIGURATION_KEY="log4j.configuration";

    public static EdgeCategory getCategory(String catName)
    {
	EdgeCategory cat = (EdgeCategory)EdgeCategory.getInstance(catName);

	// Only the first instance for any category read the config 
	if (IsFirstInstanceForAll==true) { IsFirstInstanceForAll=false; }
	else return cat;

	String resource = System.getProperty(EDGE_CONFIGURATION_KEY, EDGE_CONFIGURATION_FILE);
	URL url = null;

	url = EdgeCategory.class.getResource(resource);
	if(url == null) {
	  // if that doen't work, then try again in a slightly
	  // different way
	  ClassLoader loader = EdgeCategory.class.getClassLoader();
	  if(loader != null) {
	    url = loader.getResource(resource);	  
	  }
	}	
	if(url != null) {
String configuratorClassName = OptionConverter.getSystemProperty(EDGE_CONFIGURATOR_CLASS_KEY,null);

	    OptionConverter.selectAndConfigure(url, configuratorClassName, LogManager.getLoggerRepository());
	} else {
	    BasicConfigurator.configure();
	    //Category.getRoot().addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));

	}

	/*
	String logCfgName = catName.replace('.', '_');
	logCfgName += "_log4j.cfg";

	File f = new File(logCfgName);
	if (f.exists())
	    PropertyConfigurator.configure(logCfgName);
	*/
	return cat;

    }

 /**
    Shorthand for <code>getCategory(clazz.getName())</code>.

    @param clazz The name of <code>clazz</code> will be used as the
    name of the category to retrieve.  See {@link
    #getCategory(String)} for more detailed information.

    */
  public static Category getCategory(Class clazz) {
    return getCategory(clazz.getName());
  }    

    /**
      set the NDC info
    */
    public static void setNDC(String msg)
    {
	NDC.push(msg);
    }

    /**
      remove the NDC info
    */
    public static void removeNDC()
    {
	NDC.remove();
    }

  /**
    get Edge username from cookie 
  */
    public static String getEdgeUser(HttpServletRequest request)
    {
	Cookie[] tokens = request.getCookies();
	for (int i = 0; i < tokens.length; i++) {
	    Cookie token = tokens[i];
	    if (token.getName().equals("IR_USER")) {
		return token.getValue();
	    }
	    else if (token.getName().equals("user")) {
		return token.getValue();
	    }
	}
	return "";
    }

  /**
    get Edge username from cookie and put to NDC
  */
   public static void logEdgeUser(HttpServletRequest request)
    {
	setNDC("user="+getEdgeUser(request));
    }
  

  /**
    get IP address of the user
  */
    public static String getEdgeUserIP(HttpServletRequest request)
    {
	return request.getRemoteAddr();
    }
  
 /**
    get Hostname
  */
    public static String getHostname()
    {
	InetAddress inet 	= null;
	String str="";
	try {
	    inet 	= InetAddress.getLocalHost();
		str = inet.getHostName();
	}
	catch (UnknownHostException e) {
	    str="";
	}
	return str;
    }

 /**
    get SessionID
  */
    public static String getSid(HttpServletRequest request)
    {
    HttpSession session = request.getSession(false);
    String sessID=( session == null )? "" : session.getId();
    return sessID;
    }

    /**
     log browser info
    */
    public static void logBrowserInfo(EdgeCategory cat, HttpServletRequest request)
    {
	if (cat.isEnabledFor(Priority.WARN))
	    cat.warn("user-agent="+request.getHeader("user-agent"));
    }

    /**
      log page_start
    */
    public static void page_start(EdgeCategory cat, HttpServletRequest request, String message)
    {
        if (cat.isEnabledFor(Priority.WARN))
	    {
        NDC.push("user="+getEdgeUser(request));
        NDC.push("from="+request.getRemoteHost());
        NDC.push("pathinfo="+request.getPathInfo());
        NDC.push("query="+request.getQueryString());	
        cat.warn("PAGE_START:"+message);
	    }
    }

    /**
      log page_stop
    */
    public static void page_stop(EdgeCategory cat, HttpServletRequest request, String message)
    {
        if (cat.isEnabledFor(Priority.WARN))
	    {
        cat.warn("PAGE_STOP:"+message);
        NDC.remove();	
	    }
    }

    /**
      log func_start
    */
    public static void func_start(EdgeCategory cat, String message)
    {
        if (cat.isInfoEnabled())
	    cat.info("FUNC_START:"+message);
    }

    /**
      log func_stop
    */
    public static void func_stop(EdgeCategory cat, String message)
    {
        if (cat.isInfoEnabled())
	    cat.info("FUNC_STOP:"+message);
    }


}
