package oem.edge.ed.odc.cntl.metrics;
import java.sql.Connection;
public class GenericService extends CommonProtodata {
   protected String    hostname = null;
   protected String    svctype;
   
   public GenericService(String t) { 
      super();
      svctype = t;
   }
   public void   setHostname(String v)    { hostname   = v;   }
   public String getHostname()            { return hostname;  }
   public String getServiceType()         { return svctype;   }
   
   public void dooutput(StringBuffer ret, Connection conn, String instance,
                        String table, int nest) {
      super.dooutput(ret, conn, instance, Desktopdata.ServiceTable, nest);
      write(ret, conn, nest, "servicetype", svctype);
      write(ret, conn, nest, "hostname",    (hostname==null)?"":hostname);
      flushDB2(conn, instance, Desktopdata.ServiceTable);
   }
}
