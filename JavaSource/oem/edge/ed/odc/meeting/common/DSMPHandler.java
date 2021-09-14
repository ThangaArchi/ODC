package oem.edge.ed.odc.meeting.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/*
** TODO!! This DSMPHandler is polluted with knowledge of the protocol for 
**        performance reasons. When time permits, make a subclass of this 
**        handler which manages that special knowledge.
*/
public class DSMPHandler extends DSMPBaseHandler implements ProtocolFactory {
   
   class ProtoWrapper {
      DSMPProto proto;
      public ProtoWrapper(DSMPProto p) {
         proto = p;
      }
      public DSMPProto getProto() { return proto; }
   }
   
   
  /* For Held FrameUpdates to group them together */
   protected Vector heldProto      = new Vector();
   protected int    heldMeetingId  = -1;
   public    int    getHeldMeetingId()      { return heldMeetingId; }
   public    void   setHeldMeetingId(int v) { heldMeetingId=v;      }
   public    Vector getHeldProtoVector()    { return heldProto;     }
   
   public int lastline = -1;
   
  /*
  ** This is cool
  **
  **  When adding a new FrameUpdate to the 'tosend' Q, we first check if the 
  **  quadsUsed hash already has a Proto object for that Quad. If so, simply 
  **  replace the Proto obj with the new one, and DON'T ADD to the tosend
  **  vector. The original location on the Q of the Proto object is preserved 
  **  ... when processing FRAMEUPDATE protocol, always use the Proto from this
  **  hash. Replacement can happen many times before Proto object actually
  **  gets pushed out the door. 
  **
  **  Actually, all frameupdates go ONLY in the quadsUsed hash. The Integer
  **  quadrant is what goes in tosend
  */
   private   Hashtable quadsUsed = new Hashtable();
   private   int cullcnt         = 0;
   
   
   public DSMPHandler(DSMPDispatchBase disp) {
      super(disp);
      setDoCompression(true);
      setProtocolFactory(this);
   }
   
  /* Overrides DSMPBaseHandler getNextProtoPacket
  **
  ** getNextProtoPacket - Used only by writer thread. 'tosend' should NOT be 
  ** touched by others.
  **
  **  Consumes packets from addInfo Vector if tosend is empty.
  **  These are processed by adding them to the 'tosend' vector, which 
  **  is where we pull things off to actually send. 
  **
  **  FrameUpdate(Event) and ImageResize(Event) protos are treated special. 
  **  To avoid sending 'old data', we keep tabs on what quadrants are 
  **  currently on the list to be sent, and replace the older version with 
  **  newer. The oldest version is the one on the tosend vector, the newest 
  **  is in the quadsUsed hash (hashed with Integer quad number).
  **
  **   ImageResize(Event) cause all FrameUpdate data to be dumped.
  */ 
  
  /*
  ** processAddInfo 
  **
  **  Consumes packets from addInfo and places them on tosend vector, which 
  **  is where we pull things off to actually send (in getNextProtoPacket).
  **
  **  This is more interesting in some subclasses (DSMPHandler). Also,
  **  addLock (legacy, could use addInfo directly now) being separate from 
  **  tosend keeps writer and protogenerator from butting heads as frequently
  */
   protected void processAddInfo() {
  
     // Get elements in enum for processing
      Enumeration enum = null;
      synchronized(addLock) {
         if (addInfo.size() > 0) { 
            enum = ((Vector)addInfo.clone()).elements();
            addInfo.removeAllElements();
         } else {
            return;
         }
      }
      
     // All proto comes thru addInfo ... and has been 'charged' for via the
     //  queuedProtoByteCount. When adding to tosend, we don't have to charge
     //  again ... but special cases exist when culling code, and resizing
     //  (below) where we will adjust the bytecount accordingly.
      int bytesRemoved = 0;
      
      synchronized(tosend) {
         while(enum.hasMoreElements()) {
            DSMPProto p = (DSMPProto)enum.nextElement();
            byte op = p.getOpcode();
            if (op == DSMPGenerator.OP_FRAMEUPDATE ||
                op == DSMPGenerator.OP_FRAMEUPDATE_EVENT) {
                
               Integer myquad = p.getVirtualQuadrant();
               
               DSMPProto lastproto = (DSMPProto)quadsUsed.get(myquad);
               
              // If there is no entry in quadsUsed for this quad, then simply
              //  place this proto 'p' there, and add the Integer quad to 
              //  'tosend' to mark this
               if (lastproto == null) {
                  tosend.addElement(myquad);
               } else {
               
                 // We are culling ... proto already exists, and 
                 //  we have an update
                  if ((dispatch == null || dispatch.getMinorDebug())) {
                     System.out.println("$$$$$$$$$$ Culling " + (++cullcnt) + 
                                        " vq = " + myquad     + 
                                        " [" + getIdentifier() + "] " + 
                                        (new Date().toString()));
                  }
                  
                 // lastproto is history from this queuing system now
                  bytesRemoved += lastproto.memoryFootprint(); 
                  
                 // Keep original time for proto add
                  p.setTime(lastproto.getTime());
               }
               
               quadsUsed.put(myquad, p);
               
            } else if (op == DSMPGenerator.OP_IMAGERESIZE) {
               
              // If IMAGERESIZE, remove ALL FRAMEUPDATE and MULTIFRAMEUPDATE
              // proto starting from back of vec until all are process OR
              // hit another IMAGERESIZE
               int idx = tosend.size();
               while(--idx >= 0) {
               
                  DSMPProto lp = null;
                  
                  Object obj = tosend.elementAt(idx);
                  
                 //case DSMPGenerator.OP_FRAMEUPDATE:
                 //case DSMPGenerator.OP_FRAMEUPDATE_EVENT:
                  if (obj instanceof Integer) {
                     
                    // remove Integer quad from tosend, remove Proto from 
                    //  quadsUsed, and adjust byte count
                     tosend.removeElementAt(idx);
                     lp = (DSMPProto)quadsUsed.remove(obj);
                     if (lp != null) {
                        bytesRemoved += lp.memoryFootprint(); 
                     } else {
                        System.out.println("Zoinks! DSMPHandler: quadsUsed was NULL for quad=" + obj.toString());
                     }
                     
                  } else {
                     lp = (DSMPProto)obj;
                     switch(lp.getOpcode()) {
                        case DSMPGenerator.OP_MULTIFRAMEUPDATE:
                        case DSMPGenerator.OP_MULTIFRAMEUPDATE_EVENT:
                           bytesRemoved += lp.memoryFootprint();
                           tosend.removeElementAt(idx);
                           break;
                           
                        case DSMPGenerator.OP_IMAGERESIZE:
                        case DSMPGenerator.OP_IMAGERESIZE_EVENT:
                           idx=0;
                           break;
                     }
                  }
               }
               tosend.addElement(p);
            } else {
               tosend.addElement(p);
            }
         }
      }
      
     // possibly free up someone blocked adding protocol
      if (bytesRemoved > 0) {
         synchronized(addLock) {
            boolean wasOver = queuedProtoByteCount >= maxProtoByteCount;
               
            queuedProtoByteCount -= bytesRemoved;
            if (wasOver && queuedProtoByteCount < maxProtoByteCount) {
               addLock.notifyAll();
            }
            
            if (queuedProtoByteCount < 0) {
               System.out.println("DSMPHandler: procAdd !!! qdprotoByteCnt = "
                                  + queuedProtoByteCount +
                                  " tosend.size=" + tosend.size() + 
                                  " ainfo.size=" + addInfo.size());
                                  
               queuedProtoByteCount = 0;
            }                  
         }
      }
   }
  
  
   protected DSMPBaseProto getNextProtoPacket() {
     /*
     ** Check to see if there is new data avail. If so, process it 
     ** accordingly. This means simply moving the data to the tosend vector 
     ** if its NOT FRAMEUPDATE or IMAGERESIZE. If it IS one of these, then 
     ** special processing takes place to help speed things up.
     */ 
      
     // process EVERY time so we get the latest and greatest. Might have
     //  frame updates which are fresher.
      processAddInfo();                  
      
      DSMPProto ret = null;
      
     // using firstElement and removeElementAt SHOULD synchronized on tosend, 
     // but have to make sure, since processAddInfo might be done by 
     // others (adders). Also, we want to make sure processAddInfo is not
     // called in between the two calls
      synchronized(tosend) {
         try {
            Object obj = tosend.firstElement();
            tosend.removeElementAt(0);
            
           // If an Integer, get the real object from quadsUsed
            if (obj instanceof Integer) {
               ret = (DSMPProto)quadsUsed.remove(obj);
            } else {
               ret = (DSMPProto)obj;
            }
         } catch(Exception eee) {}
      }
      
      if (ret != null) {
         
         synchronized(addLock) {
         
            boolean wasOver = queuedProtoByteCount >= maxProtoByteCount;
            
           // possibly free up someone blocked adding protocol
            queuedProtoByteCount -= ret.memoryFootprint();
            if (wasOver && queuedProtoByteCount < maxProtoByteCount) {
               addLock.notifyAll();
            }
            
            if (queuedProtoByteCount < 0) {
               System.out.println("DSMPHandler: getnp !!! qdprotoByteCnt = " + 
                                  queuedProtoByteCount +
                                  " tosend.size=" + tosend.size() + 
                                  " ainfo.size=" + addInfo.size());
                                  
               queuedProtoByteCount = 0;
            }
         }
         
         long tt = ret.getDeltaTime();
         if (tt > 500 && (dispatch == null || dispatch.getMinorDebug())) {
            System.out.println("TimeOnQ = " + tt + " QSize = " + 
                               tosend.size() + " [" + getIdentifier() + "] " + 
                               (new Date().toString()));
         }
      }
      return ret;
   }
   
  // For protocolFactory interface - We want DSMPProto objects not Base
   public DSMPBaseProto createInstance() {
      return new DSMPProto();
   }
   
  /* 
  ** Called by super class when rest period comes. I want to generate some
  **  proto ... here it is
  */
   protected void generateRestProtocol() throws Exception {
   
     // Send a rest proto ... This is useful ... trust me
     // This was in synchronized block below ... but it could 
     //  cause a blocking condition for someone posting to our Q.
      DSMPProto restproto = DSMPGenerator.protocolRest();
      restproto.write(ostream);
      ostream.flush();
   }
   
   public String toString() {
      
      StringBuffer sb = new StringBuffer("DSMPHandler: ");
      
      int wqsz = addInfo.size();
      
      Vector nv = (Vector)tosend.clone();
      int sz = nv.size();
      
      sb.append(getIdentifier()).append(" id[").append(handlerid).append("] ");
      sb.append("\n   AvgWriteThruput=" + (currSendInfo/NUMSENT_INIT));
      sb.append(" Awaiting copy: ").append(wqsz);
      sb.append(" Awaiting write: ").append(sz);
      Enumeration enum = nv.elements();
      while(enum.hasMoreElements()) {
         Object ibp = enum.nextElement();
         DSMPBaseProto bp = null;
         if (ibp instanceof Integer) {
            bp = (DSMPBaseProto)quadsUsed.get(ibp);
         } else {
            bp = (DSMPProto)ibp;
         }
         sb.append("\n      Opcode[").append(bp.getOpcode()).append("]");
         sb.append(" timeonq[").append(bp.getDeltaTime()).append("] ms");
         sb.append(" size[").append(bp.getNonHeaderSize()).append("]");
      }
      return sb.toString();
   }
   
}
