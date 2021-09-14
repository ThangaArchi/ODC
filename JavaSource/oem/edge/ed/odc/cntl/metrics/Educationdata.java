package oem.edge.ed.odc.cntl.metrics;
import java.sql.Connection;
public class Educationdata extends CommonProtodata {
   protected String    hostname = null;
   protected String    classname = null;
   
   public void   setHostname(String v)    { hostname   = v;   }
   public String getHostname()            { return hostname;  }
   public void   setClassname(String v)    { classname   = v;   }
   public String getClassname()            { return classname;  }
   
   public void dooutput(StringBuffer ret, Connection conn, String instance, 
                        String table, int nest) {
      super.dooutput(ret, conn, instance, Desktopdata.ServiceTable, nest);
      write(ret, conn, nest, "servicetype",      "EDU");
      write(ret, conn, nest, "classname", classname);
      write(ret, conn, nest, "hostname",    hostname);
      flushDB2(conn, instance, Desktopdata.ServiceTable);
   }
}
