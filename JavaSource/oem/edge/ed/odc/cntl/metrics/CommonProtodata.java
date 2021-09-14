package oem.edge.ed.odc.cntl.metrics;
import oem.edge.ed.odc.tunnel.common.*;
import java.util.*;
import java.sql.Connection;

public class CommonProtodata extends Commondata implements TunnelEarListener {
   protected String    project            = null;
   protected int       numConns           = 0;
   protected Vector    tsockets           = null;
   
   public CommonProtodata() {
      long ctm = System.currentTimeMillis();
      setStartTime(ctm);
   }
         
   public void setNumConnections(int v)    { numConns = v;    }
   public void incrNumConnections(int v)   { numConns++;      }
   public int  getNumConnections()         { return numConns; }
   
   public void setProject(String v)        { project = v;     }
   public String getProject()              { return project;  }
   
   public void earDestroyed(TunnelEarInfo te) {
      setEndTime(System.currentTimeMillis());
   }
   public void socketCreated(TunnelSocket ts) {
      if (tsockets == null) tsockets = new Vector();
      tsockets.addElement(ts);
      incrNumConnections(1);
   }
   public void socketDestroyed(TunnelSocket ts) {
      for(int i=0; i < tsockets.size(); i++) {
         if ((TunnelSocket)tsockets.elementAt(i) == ts) {
            tsockets.removeElementAt(i);
            break;
         }
      }
      incrProtoFromClient(ts.getOutputBuffer().getTotalCount());
      incrProtoToClient(ts.getInputBuffer().getTotalCount());
   }
   
   public synchronized long getTotalProto() {
      long totto = totProtoToClient;
      long totfr = totProtoFromClient;
      if (tsockets != null) {
         for(int i=0; i < tsockets.size(); i++) {
            TunnelSocket ts = (TunnelSocket)tsockets.elementAt(i);
            totfr += ts.getOutputBuffer().getTotalCount();
            totto += ts.getInputBuffer().getTotalCount();
         }
      }
      return totto + totfr;
   }
   
   public void dooutput(StringBuffer ret, Connection conn, String instance, 
                        String table, int nest) {
                        
      if (tsockets != null) {
         long totto = 0;
         long totfr = 0;
         for(int i=0; i < tsockets.size(); i++) {
            TunnelSocket ts = (TunnelSocket)tsockets.elementAt(i);
            totfr += ts.getOutputBuffer().getTotalCount();
            totto += ts.getInputBuffer().getTotalCount();
         }
         incrProtoFromClient(totfr);
         incrProtoToClient(totto);
         super.dooutput(ret, conn, instance, table,nest);
         decrProtoFromClient(totfr);
         decrProtoToClient(totto);
      } else {
         super.dooutput(ret, conn, instance, table, nest);
      }
      
      if (numConns != 0) {
         write(ret, conn, nest, "numconns", new Integer(numConns));
      }
      write(ret, conn, nest, "project", project);
   }
}
