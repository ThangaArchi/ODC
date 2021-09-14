package oem.edge.ed.odc.dropbox.client.soa;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;
import java.lang.reflect.*;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import  oem.edge.ed.odc.dropbox.service.DropboxAccess;
import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.util.*;
import  oem.edge.ed.odc.util.*;
import  oem.edge.common.cipher.*;

import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.*;

public class testservice {

   ConnectionFactory factory = null;
   DropboxAccess srv = null;
   
   public void setSessionId(DropboxAccess obj, HashMap sessionmap) throws Exception {
      factory.setSessionId(obj, sessionmap);
   }
   
   
   public static void main(String args[]) {
      new testservice().process(args);
   }
   
   public void process(String args[]) {
   
      String user2 = "zarnick@us.ibm.com";
      
      try {
      
         try {
         
            String facClassS = null;
            if (args.length > 2) {
               System.out.println("Doing LOCAL thing");
               facClassS = "oem.edge.ed.odc.dropbox.client.soa.DirectConnectFactory";
            //} else if (useSoap) {
            // facClassS = "oem.edge.ed.odc.dropbox.client.soa.JAXRPCConnectFactory";
            } else {
               facClassS = "oem.edge.ed.odc.dropbox.client.soa.HessianConnectFactory";
            }
            
            
            Class facClass = Class.forName(facClassS);
            factory = (ConnectionFactory)facClass.newInstance();
            String topurl = "http://edesign4.fishkill.ibm.com/technologyconnect/odc";
            
            factory.setTopURL(new java.net.URL(topurl));
            srv = factory.getProxy();
            
         } catch(Exception e) {
            e.printStackTrace(System.out);
            throw new Error("Whoops");
         }
      
      
         HashMap sessionmap = srv.createSession(args[0], "pw");
         
         System.out.println("--------- Sessionmap ---------");
         System.out.println(sessionmap.toString());
         
         setSessionId(srv, sessionmap);
         
         System.out.println("--------- Refresh Sessionmap ---------");
         sessionmap = srv.refreshSession();
         System.out.println(sessionmap.toString());
         
         setSessionId(srv, sessionmap);
         
         System.out.println("\n======= Projects =======");
         System.out.println(srv.getProjectList().toString());
         
         System.out.println("\n======= Banner =======");
         System.out.println(srv.getLoginMessage());
         
         
         System.out.println("\n======= Delete invalid package =======");
         try {
            srv.deletePackage(297498749723L);
            System.out.println("Delete of invalid package worked incorrectly!!");
            throw new Error("Bad delete");
         } catch(DboxException ee) {
            System.out.println("Delete failed correctly ;-)");
         }
         
         System.out.println("\n======= Owned Packages =======");
         Vector packages = srv.queryPackages(null, false, true, false, false, true);
         System.out.println(packages.toString());
         
         
         System.out.println("\n======= Inbox Packages =======");
         packages = srv.queryPackages(null, false, false, false, false, true);
         System.out.println(packages.toString());
         
         
         System.out.println("\n======= Create Package =======");
         String packnamemid = ""+(new Random()).nextInt();
         String packnamep1 = args[0] + packnamemid;
         String packname = packnamep1+"_%theend";
         long packid = srv.createPackage(packname);
         System.out.println("PackageID = " + packid);
         
         System.out.println("\n======= SetPackageFalgs =======");
         try {
            srv.setPackageFlags(packid, 0x255, 0x255);
            throw new Error("setPackageFlagsw SHOULD have failed!!");
         } catch(DboxException dbe) {
            System.out.println("setPackageFlags failed correctly ;-)");
         }
         
         System.out.println("\n======= list package using fullname =======");
         packages = srv.queryPackages(packname, false, true, false, false, true);
         if (packages.size() != 1) {
            throw new DboxException("Error querying package by name: " + 
                                    packname + " size = " + packages.size());
         }
         
         
         System.out.println("\n======= list package using p1 prefix =======");
         packages = srv.queryPackages(packnamep1, false, true, false, false, true);
         if (packages.size() != 1) {
            throw new DboxException("Error querying package by name: " + 
                                    packname + " size = " + packages.size());
         }
         
         
         System.out.println("\n======= list package using prefix regexp =======");
         packages = srv.queryPackages(packnamep1 + "*", true, true, false, false, true);
         if (packages.size() != 1) {
            throw new DboxException("Error querying package by name: " + 
                                    packname + " size = " + packages.size());
         }
         
         
         System.out.println("\n======= list package using middle of name regexp =======");
         packages = srv.queryPackages("*" + packnamemid + "*", true, true, false, false, true);
         if (packages.size() != 1) {
            throw new DboxException("Error querying package by name: " + 
                                    packname + " size = " + packages.size());
         }
         
         
         System.out.println("\n======= Add Acl =======");
         srv.addUserAcl(packid, args[0]);
         
         
         System.out.println("\n======= Create Files =======");
         File testfile = new File("testfile");
         long size = 34248759847L;
         if (testfile.exists()) {
            size = testfile.length();
         }
         long fid1 = srv.uploadFileToPackage(packid, "thefile1", size);
         long fid2 = srv.uploadFileToPackage(packid, "thefile2", 56456456);
         long fid3 = srv.uploadFileToPackage(packid, "thefile3", 1);
         long fid4 = srv.uploadFileToPackage(packid, "thefile4", 0);
         
         
         System.out.println("\n======= Allocate Slots f1 =======");
         FileSlot fs1 = srv.allocateUploadFileSlot(packid, fid1, 4);
         FileSlot fs2 = srv.allocateUploadFileSlot(packid, fid1, 4);
         FileSlot fs3 = srv.allocateUploadFileSlot(packid, fid1, 4);
         FileSlot fs4 = srv.allocateUploadFileSlot(packid, fid1, 4);
         
         if (fs2 != null) srv.releaseFileSlot(packid, fid1, fs2.getSlotId());
         if (fs3 != null) srv.removeFileSlot(packid, fid1, fs3.getSlotId());
         if (fs4 != null) srv.releaseFileSlot(packid, fid1, fs4.getSlotId());
                  
         System.out.println("\n======= Show Slots f1 =======");
         Vector slots = srv.queryFileSlots(packid, fid1);
         Iterator it = slots.iterator();
         while(it.hasNext()) {
            FileSlot fs = (FileSlot)it.next();
            System.out.println(fs.toString());
         }
         
         System.out.println("\n======= Upload to slot fs1 =======");
         
         srv.releaseFileSlot(packid, fid1, fs1.getSlotId());
         
         if (testfile.exists()) {
         
            System.out.println("\n\nDoing testfile upload\n\n");
            
            UploadOperation uploader = new UploadOperation(srv, factory, 
                                                           testfile, packid, fid1);
            uploader.process();
            uploader.waitForCompletion();
            if (!uploader.validate()) {
               throw new Exception("Error must have occured during operation");
            }
            
            System.out.println("Transfer rate = " + uploader.getXferRate());
            
            try {
               srv.downloadPackageItem(packid, fid1, size, 1);
               throw new Exception("Should have gotten exception for downloading past end of file!");
            } catch(DboxException dbe1) {
               System.out.println("Correctly got error for trying to download past end of file");
            }
            
            try {
               srv.downloadPackageItem(packid, fid1, size-1, 1);
               throw new Exception("Should have gotten exception for downloading last byte too soon");
            } catch(DboxException dbe1) {
               System.out.println("Correctly got error for trying to download last byte too soon");
            }
            
            FileOutputStream fos = new FileOutputStream("outfile");
            int ofs = 0;
            byte buf[] = new byte[100*1024];
            while((buf=srv.downloadPackageItem(packid, fid1, ofs, 100000)) != null) {
               fos.write(buf);
               ofs += buf.length;
               if (ofs == size) break;
            }
            
            fos.close();
            
            String md5src = SearchEtc.calculateMD5(new File("testfile"));
            String md5dst = SearchEtc.calculateMD5(new File("outfile"));
            
            if (!md5src.equals(md5dst)) {
               throw new DboxException("MD5 value for downloaded file differs!!");
            }
            
         } else {       
            srv.uploadFileSlotToPackage(packid, fid1, fs1.getSlotId(), false, 
                                        "this is the test data".getBytes());
         }
         
         System.out.println("\n======= Show Slots f1 AGAIN =======");
         slots = srv.queryFileSlots(packid, fid1);
         it = slots.iterator();
         while(it.hasNext()) {
            FileSlot fs = (FileSlot)it.next();
            System.out.println(fs.toString());
         }
         
         System.out.println("\n======= Show Slots f2 =======");
         slots = srv.queryFileSlots(packid, fid2);
         it = slots.iterator();
         while(it.hasNext()) {
            FileSlot fs = (FileSlot)it.next();
            System.out.println(fs.toString());
         }
         
         System.out.println("\n======= Show Slots f3 =======");
         slots = srv.queryFileSlots(packid, fid3);
         it = slots.iterator();
         while(it.hasNext()) {
            FileSlot fs = (FileSlot)it.next();
            System.out.println(fs.toString());
         }
         
         System.out.println("\n======= Show Slots f4 =======");
         slots = srv.queryFileSlots(packid, fid4);
         it = slots.iterator();
         while(it.hasNext()) {
            FileSlot fs = (FileSlot)it.next();
            System.out.println(fs.toString());
         }
         
         
         
         System.out.println("\n======= delete files 3-4 =======");
        //srv.removeItemFromPackage(packid, fid1);
         srv.removeItemFromPackage(packid, fid2);
         srv.removeItemFromPackage(packid, fid3);
         srv.removeItemFromPackage(packid, fid4);
         
         System.out.println("\n======= Commit Package =======");
         srv.commitPackage(packid);
         
         
         System.out.println("\n======= Owned Packages =======");
         packages = srv.queryPackages(null, false, true, false, false, true);
         System.out.println(packages.toString());
         
         
         System.out.println("\n======= Delete Package =======");
         srv.deletePackage(packid);
         
         
         System.out.println("\n======= Owned Packages =======");
         packages = srv.queryPackages(null, false, true, false, false, true);
         System.out.println(packages.toString());
         
         
         System.out.println("\n======= Create Group =======");
         String gnamemid = ""+(new Random()).nextInt();
         String gnamep1 = "testgrp_" + gnamemid;
         String gname = gnamep1 + "_%theend";
         srv.createGroup(gname);
         
         
         System.out.println("\n======= List new group all ways =======");
         
         Map map = srv.queryGroups(gname, false, true, true);
         GroupInfo gi = (GroupInfo)map.get(gname);
         if (gi == null) {
            throw new DboxException("Group not found " + gname);
         } else if (map.size() != 1) {
            throw new DboxException("Group not found: FULL no regex" + gname);
         }
         
         map = srv.queryGroups(gname, true, true, true);
         gi = (GroupInfo)map.get(gname);
         if (gi == null) {
            throw new DboxException("Group not found " + gname);
         } else if (map.size() != 1) {
            throw new DboxException("Group not found: FULL regex" + gname);
         }
         
         map = srv.queryGroups(gnamep1, false, true, true);
         gi = (GroupInfo)map.get(gname);
         if (gi != null) {
            throw new DboxException("Group found! " + gname);
         } else if (map.size() != 0) {
            throw new DboxException("Group found: prefix no regex!" + gname);
         }
         
         map = srv.queryGroups(gnamep1 + "*", true, true, true);
         gi = (GroupInfo)map.get(gname);
         if (gi == null) {
            throw new DboxException("Group not found " + gname);
         } else if (map.size() != 1) {
            throw new DboxException("Group not found: prefix regex" + gname);
         }
         
         map = srv.queryGroups("*" + gnamemid + "*", true, true, true);
         gi = (GroupInfo)map.get(gname);
         if (gi == null) {
            throw new DboxException("Group not found " + gname);
         } else if (map.size() != 1) {
            throw new DboxException("Group not found: mid regex" + gname);
         }
         
         
         System.out.println("\n======= Add member to group =======");
         srv.addGroupAcl(gname, user2, true);
         
          map = srv.queryGroups(gname, false, true, true);
         gi = (GroupInfo)map.get(gname);
         if (gi == null) {
            throw new DboxException("Group not found " + gname);
         }
         
         System.out.println(gi.toString());
         
         if (!gi.getGroupMembers().contains(user2)) {
            throw new DboxException("Member not in group!");
         }
         
         if (gi.getGroupAccess().contains(user2)) {
            throw new DboxException("ACCESS contains user!");
         }
         
         
         System.out.println("\n======= Add Access to group =======");
         srv.addGroupAcl(gname, user2, false);
         
         map = srv.queryGroups(gname, false, true, true);
         gi = (GroupInfo)map.get(gname);
         if (gi == null) {
            throw new DboxException("Group not found " + gname);
         }
         
         System.out.println(gi.toString());
         
         if (!gi.getGroupMembers().contains(user2)) {
            throw new DboxException("Member not in group!");
         }
         
         if (!gi.getGroupAccess().contains(user2)) {
            throw new DboxException("ACCESS does NOT contain user!");
         }
         
         
         System.out.println("\n======= delete group =======");
         srv.deleteGroup(gname);
         
         map = srv.queryGroups(gname, false, true, true);
         gi = (GroupInfo)map.get(gname);
         if (gi != null) {
            throw new DboxException("Group still defined! " + gname);
         }
         
         System.out.println("--------- Close Session ---------");
         srv.closeSession();
               
         try {
            System.out.println("--------- Refresh Session ---------");
            sessionmap = srv.refreshSession();
            throw new DboxException("Refresh SHOULD have failed: SessionClosed");
         } catch(DboxException dbe) {
            System.out.println("Refresh failed CORRECTLY ... session is closed");
         }
         
      } catch(DboxException e) {
         System.out.println("Exception occured");
         e.printStackTrace(System.out);
      } catch(Exception eee) {
         System.out.println("Exception occured");
         eee.printStackTrace(System.out);
      }
   }
}
