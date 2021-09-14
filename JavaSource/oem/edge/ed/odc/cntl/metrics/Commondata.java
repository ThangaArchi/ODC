package oem.edge.ed.odc.cntl.metrics;

import oem.edge.ed.odc.tunnel.common.DebugPrint;
import java.util.*;
import java.sql.*;

public class Commondata {

   protected Vector    localVec  = null;
   
   protected String    key       = null;
   
   protected long      startTime = 0;
   protected long      endTime   = 0;
   
   protected long      totProtoFromClient = 0;
   protected long      totProtoToClient   = 0;
   
  // Augmented in subclasses
   public void fixEndTime(long t) {
      if (endTime == 0) {
         endTime = t;
      }
   }
   
   public void setProtoFromClient(long v)  { totProtoFromClient = v;    }
   public void incrProtoFromClient(long v) { totProtoFromClient += v;   }
   public void decrProtoFromClient(long v) { totProtoFromClient -= v;   }
   public void setProtoToClient(long v)    { totProtoToClient   = v;    }
   public void incrProtoToClient(long v)   { totProtoToClient   += v;   }
   public void decrProtoToClient(long v)   { totProtoToClient   -= v;   }
   public long getProtoFromClient()        { return totProtoFromClient; }
   public long getProtoToClient()          { return totProtoToClient;   }
   
   public void   setKey(String v)     { key       = v;    }
   public String getKey()             { return key;       }
   
   public void   setStartTime(long v) { startTime = v;    }
   public long   getStartTime()       { return startTime; }
   
   public void   setEndTime(long v)   { endTime   = v;    }
   public long   getEndTime()         { return endTime;   }
   
   public int    getTimeDelta()       { return (int)(endTime-startTime);   }
   
   private Vector getLocalVector() {
      if (localVec == null) localVec = new Vector();
      return localVec;
   }
   
   protected void flushDB2(Connection conn, String instance, String table) {
      if (conn != null) {
         try {
            if (localVec != null) {
               StringBuffer sb = new StringBuffer();
               StringBuffer vb = new StringBuffer();
               sb.append("insert into ").append(instance).append(".")
                  .append(table).append(" (");
               vb.append(" values (");
               for (int i=0; i < localVec.size(); i+=2) {
                  if (i > 0) { sb.append(","); vb.append(","); }
                  String ss = localVec.elementAt(i).toString().toUpperCase();
                  sb.append(ss);
                  
                 // Bogus special case code. Make UKEY a function value
                  if (!ss.equals("UKEY")) {
                     vb.append("?");
                  } else {
                     vb.append("GENERATE_UNIQUE()");
                  }
               }
               sb.append(")").append(vb.toString()).append(")");
               
               PreparedStatement stmt = conn.prepareStatement(sb.toString());
               
               int idx = 0;
               for (int i=0; i < localVec.size(); i+=2) {
                  Object o = localVec.elementAt(i+1);
                  if (o instanceof String) {
                    // Only add it if != GENERATE_UNIQUE()
                     if (!((String)o).equals("GENERATE_UNIQUE()")) {
                        stmt.setString(++idx, (String)o); 
                     }
                  } else if (o instanceof Timestamp) {
                     stmt.setTimestamp(++idx, (Timestamp)o); 
                  } else if (o instanceof Integer) {
                     stmt.setInt(++idx, ((Integer)o).intValue()); 
                  } else if (o instanceof Long) {
                     stmt.setLong(++idx, ((Long)o).longValue()); 
                  } else {
                     stmt.setString(++idx, o.toString());
                  }
                  
                  DebugPrint.println(DebugPrint.DEBUG3, 
                                     "\tP[" + idx + "] = " + o.toString());
               }
               
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG3, 
                                     "flushDB2: About to Execute: " + 
                                     sb.toString());
               }
               int changed = stmt.executeUpdate();
               DebugPrint.println(DebugPrint.DEBUG3, "Result: " + changed);
               
              // Release JDBC resources
               try {stmt.close();} catch(Exception ee) {}
            } else {
               DebugPrint.println(DebugPrint.ERROR, 
                                  "Asked to flushDB2, but no localVec!");
            }
         } catch(Throwable t) {
            DebugPrint.println(DebugPrint.ERROR, 
                               "Exception while flushDB2 to " + 
                               instance + "." + table + "!");
            DebugPrint.println(DebugPrint.ERROR, t);
         }
         
         localVec = null;
      }
   }
      
   public static String getNestString(int nest) { 
      String ret = "";
      for (int i=0; i < nest; i++) {
         ret += "  ";
      }
      return ret;
   }
   
   public void showElements(StringBuffer ret, Hashtable t, Connection conn, 
                            String instance, String table, 
                            int nest, String name) {
                              
      if (t != null && t.size() != 0) { 
         if (conn != null) {
            Enumeration enum = t.elements();
            int idx = 0;
            while(enum.hasMoreElements()) {
               String eval = "_Elm_" + (idx++);
               Commondata cd = (Commondata)enum.nextElement();
               cd.dooutput(ret, conn, instance, table, nest+2);
            }
         } else {
            String ns    = getNestString(nest);
            String nsp1  = getNestString(nest+1);
            ret.append(ns).append("<").append(name).append(">\n");
            Enumeration enum = t.elements();
            int idx = 0;
            while(enum.hasMoreElements()) {
               String eval = "_Elm_" + (idx++);
               ret.append(nsp1).append("<").append(eval).append(">\n");
               Commondata cd = (Commondata)enum.nextElement();
               cd.dooutput(ret, conn, instance, table, nest+2);
               ret.append(nsp1).append("</").append(eval).append(">\n");
            }
            ret.append(ns).append("</").append(name).append(">\n");
         }
      }
   }
      
  //
  // This writes the data in a more intelligent table manner
  //
   protected void write(StringBuffer ret, Connection conn, 
                        int nest, String name, Object val) {
      
      if (val == null) return;
      
      if (conn != null) {
         getLocalVector().addElement(name);
         getLocalVector().addElement(val);
      } else {
         String ns = getNestString(nest);
         ret.append(ns) .append("<") .append(name).append(">")
            .append(val.toString()).append("</").append(name).append(">\n");
      }
   }
      
   public void dooutput(StringBuffer ret, Connection conn, String instance,
                        String table, int nest) {
                        
      if (key == null) {
         System.out.println("!!!!!!!!! Zoinks: Key is null!");
      }
      
      write(ret, conn, nest, "key", key);
      write(ret, conn, nest, "ukey", "GENERATE_UNIQUE()");
      
      if (startTime != 0) {
         write(ret, conn, nest, "starttime", new Timestamp(startTime));
         if (endTime == 0) {
            endTime = System.currentTimeMillis();
         }
      }
      if (endTime != 0) {
         write(ret, conn, nest, "endtime", new Timestamp(endTime));
      }
             
      if (startTime != 0 && endTime != 0) {
         write(ret, conn, nest, "deltaTime", 
               new Integer((int)(endTime-startTime)));
      }
      if (totProtoToClient > 0) {
         write(ret, conn, nest, "toclient", new Long(totProtoToClient));
      }
      if (totProtoFromClient > 0) {
         write(ret, conn, nest, "fromclient", new Long(totProtoFromClient));
      }
   }   
   
   public String toString() { return toString(0); }
   public String toString(int nest) {
      StringBuffer sb = new StringBuffer();
      
     // Send null in for preface, cause we use it and don't check
      dooutput(sb, null, null, null, nest);
      return sb.toString();
   }
   
}
