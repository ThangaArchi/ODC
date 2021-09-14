package oem.edge.ed.odc.cntl.metrics;
import java.sql.Connection;
public class Hostingdata extends CommonProtodata {
   protected String    hostname = null;
   
   public void   setHostname(String v)    { hostname   = v;   }
   public String getHostname()            { return hostname;  }
   
   public void dooutput(StringBuffer ret, Connection conn, String instance,
                        String table, int nest) {
      super.dooutput(ret, conn, instance, Desktopdata.ServiceTable, nest);
      write(ret, conn, nest, "servicetype", "DSH");
      write(ret, conn, nest, "hostname",    hostname);
      flushDB2(conn, instance, Desktopdata.ServiceTable);
   }
}
