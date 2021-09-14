package oem.edge.ed.odc.dropbox.testcases;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.util.*;
import oem.edge.ed.odc.dropbox.service.helper.*;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.dropbox.service.*;
import java.util.*;
import java.io.*;
import junit.framework.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006                                         */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

//
// Keep adding tests as we move forward
//
// Note, we create a proxy as part of setup and closeSession as part of teardown
//
public class testsync extends TestCase {

   String groupname;

   protected DropboxAccess dropbox;
   protected ConnectionFactory factory;
   String userid,  password;
   
   protected DropboxAccess dropboxu2;
   String useridu2, passwordu2;
   
   String task, testcaseName;
   long pkgid;
   boolean debug = true;
   
   boolean showGoodFailureMessages = false;
   boolean pauseOnTearDown         = false;
   boolean propsloaded = false;
   ResourceBundle bundle;
   
   String fileToUpload;
   String localDownloadFileDir;
   int maxTransferTime;
   
  // Search for package in vector by name ... return first one
   PackageInfo findByName(Vector v, String name) {
      PackageInfo ret = null;
      if (name != null && v != null && v.size() > 0) {
         Iterator it = v.iterator();
         while(it.hasNext()) {
            ret = (PackageInfo)it.next();
            if (ret.getPackageName().equals(name)) {
               return ret;
            }
         }
      }
      return null;
   }
   
   public testsync(String s) { super(s); }
   
   protected void tearDown() {
   
      if (pauseOnTearDown) {
         System.out.print("In tear down ... Hit enter to continue ...");
         try {
            int ch;
            while((ch = System.in.read()) > -1) {
               if (ch == '\n') break;
            }
         } catch(Exception ee) {}
      }
   
      if (pkgid != -1) {
         try {dropbox.deletePackage(pkgid);} catch(Exception e) {}
      }
      try {dropbox.deleteGroup(groupname);} catch(Exception e) {}
      try {dropbox.closeSession();} catch(Exception e) {}
      try {dropboxu2.closeSession();} catch(Exception e) {}
      
      dropboxu2 = null;
      dropbox   = null;
      factory   = null;
   }
   
   String getProperty(String v, String def) {
      String ret = null;
      try {
         if (!propsloaded) {
            propsloaded = true;
            try {
               bundle = ResourceBundle.getBundle("testsync");
               if (debug) {
                  System.out.println("testsync bundle loaded");
               }
            } catch(Exception ee) {
               System.out.println("Error loading testsync.properties " + ee.toString());
            }
         }
         if (bundle != null) {
            try {
               ret = bundle.getString(v);
            } catch(Exception eee) {
            }
         }
         if (ret == null) {
            ret = System.getProperty(v);
         }
         if (ret == null) ret = def;
      } catch(Exception e) {}
      return ret;
   }
   
   void goodFailure(Exception e) {
      if (showGoodFailureMessages) {
         System.out.println(Nester.nest(SearchEtc.getStackTrace(e), 2));
      }
   }
   
   protected void setUp() {
   
     // Consider putting this info in a file
      userid               = getProperty("userid",   "testuser");
      password             = getProperty("password", "t3stus3r");
      groupname            = getProperty("groupname", "junittestgroup12011963_" + userid);
      
      useridu2             = getProperty("userid2",   "testuser2");
      passwordu2           = getProperty("password2", "t3stus3r");
      
      fileToUpload         = getProperty("filetoupload", "/afs/eda/u/crichton/.profile");
      localDownloadFileDir = getProperty("localdownloaddir", "/tmp");
      maxTransferTime      = 30;
      showGoodFailureMessages = getProperty("showGoodFailureMessages", 
                                            "TRUE").equalsIgnoreCase("TRUE");
      
      pauseOnTearDown         = getProperty("pauseOnTearDown", 
                                            "TRUE").equalsIgnoreCase("TRUE");
      
      task                 = "NA";
      testcaseName         = "NA";
      pkgid                = -1;
      
      String facClassS = null;
      String topURL = getProperty("topurl", 
                                  "http://edesign4.fishkill.ibm.com/technologyconnect/odc");
     //facClassS = "oem.edge.ed.odc.dropbox.service.helper.DirectConnectFactory";
     //facClassS = "oem.edge.ed.odc.dropbox.service.helper.JAXRPCConnectFactory";
      facClassS = getProperty("factoryclass", 
                              "oem.edge.ed.odc.dropbox.service.helper.HessianConnectFactory");
      
      System.out.println("Using fac class = " + facClassS);
      try {
         Class facClass = Class.forName(facClassS);
         factory = (ConnectionFactory)facClass.newInstance();
         factory.setTopURL(new java.net.URL(topURL));
         dropbox = factory.getProxy();
         dropboxu2 = factory.getProxy();
         
      } catch(Exception e) {
         fail(task + ": Exception during setup: " + e.toString());
      }
   }
   
  // 
  // Use the testTemplate to create a new test ... and add here
  //
   static public Test suite() {
      TestSuite suite = new TestSuite();
     // suite.addTest(new testsync("testLoginFail"));
      suite.addTest(new testsync("testGroups"));
      suite.addTest(new testsync("testLookup"));
      suite.addTest(new testsync("testRegexPackageSearch"));
      suite.addTest(new testsync("testPackageCycle"));
      return suite;
   }
   
  // Work routine to get a logged in session
   public void login() {
      boolean worked = true;
      try {
         HashMap map = dropbox.createSession(userid, password);
         factory.setSessionId(dropbox, map);
      } catch(Exception e) {
         e.printStackTrace(System.out);
         worked = false;
      }
      assertTrue(worked);
   }
   
  // Login yuk for user2
   public void loginu2() {
      boolean worked = true;
      try {
         HashMap map = dropboxu2.createSession(useridu2, passwordu2);
         factory.setSessionId(dropboxu2, map);
      } catch(Exception e) {
         e.printStackTrace(System.out);
         worked = false;
      }
      assertTrue(worked);
   }
   
  // Test failed login
   public void testLoginFail() {
      setTestcase("testLoginFail");
      boolean worked = true;
      try {
         dropbox.createSession("bogususer", "boguspassword");
      } catch(Exception e) {
         worked = false;
      }
      assertFalse(worked);
   }
   
   
   void deletePackage(String name) throws Exception {
      for (int i = 0; i < 1000; i++) {
         Vector v = dropbox.queryPackages(name, false, true, false, false, false);
         PackageInfo pinfo = findByName(v, name);
         if (pinfo != null) {
            dropbox.deletePackage(pinfo.getPackageId()); 
         } else {
            return;
         }
      }
      fail(task + ": Delete package [" + name + "] looped for 1000!");
   }
   
   void setTask(String t) {
      task = t;
      if (debug) {
         System.out.println("Current task: " + t);
      }
   }
   
   void setTestcase(String t) {
      testcaseName = t;
      if (debug) {
         System.out.println("------- Start testcase : " + testcaseName + "-------");
      }
   }
   
   
   public void testPackageCycle() {
      setTestcase("testPackageCycle");
      
      login();
      loginu2();
      
      try { 
         Random r = new Random();
         String name = "jojotest" + r.nextLong();
         String filename = "myfile." + name;
         
        // Delete the package if it already exists
         setTask("Cleanup any packages with same name that are lying around");
         deletePackage(name);
         
        // -----------------
         setTask("Query Storage Pools");
         Vector v = dropbox.queryStoragePoolInformation();
         if (v.size() == 0) {
            fail(task + ": Query storage pool info returned NO entries!");
         }
         
        // -----------------
         long largestpoolid = 0;
         PoolInfo pubpool = null;
         Iterator it = v.iterator();
         while(it.hasNext()) {
            PoolInfo pi1 = (PoolInfo)it.next();
            
            setTask("Query Storage Pool Instance: " + pi1.toString());
            
            PoolInfo pi2 = dropbox.getStoragePoolInstance(pi1.getPoolId());
            if (!pi2.equals(pi1)) {
               fail(task);
            }
            if (largestpoolid < pi1.getPoolId()) {
               largestpoolid = pi1.getPoolId();
            }
            if (pi1.getPoolId() == DropboxAccess.PUBLIC_POOL_ID) {
               pubpool = pi1;
            }
         }
         
         if (pubpool == null) {
            fail("Public pool not returned from query storage pools");
         }
         
         
        // -----------------
         String initdesc   = "initial desc";
         String adjustdesc = "adjust  desc";
         setTask("Creating uniq package. Should fail with invalid pool id");
         try {
            pkgid = dropbox.createPackage(name, initdesc,
                                          largestpoolid+1, 
                                          pubpool.getPoolDefaultDays(), 
                                          null, 0, 0);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
            
        // -----------------
         setTask("Creating uniq package. Should fail with invalid expiration");
         try {
            pkgid = dropbox.createPackage(name, initdesc,
                                          pubpool.getPoolId(),
                                          pubpool.getPoolMaxDays()+1, 
                                          null, 0, 0);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
            
        // -----------------
         setTask("Creating uniq package. Should fail with invalid ACL");
         try {
            v = new Vector();
            AclInfo acl = new AclInfo();
            acl.setAclName("NoWayIsItReal");
            acl.setAclStatus(DropboxAccess.STATUS_USER);
            v.add(acl);
            pkgid = dropbox.createPackage(name, initdesc,
                                          pubpool.getPoolId(),
                                          pubpool.getPoolDefaultDays(), 
                                          v, 0, 0);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
            
        // -----------------
         setTask("Creating uniq package. Should fail with invalid Package Options");
         try {
            v = new Vector();
            AclInfo acl = new AclInfo();
            acl.setAclName(userid);
            acl.setAclStatus(DropboxAccess.STATUS_USER);
            v.add(acl);
            pkgid = dropbox.createPackage(name, initdesc,
                                          pubpool.getPoolId(),
                                          pubpool.getPoolDefaultDays(), 
                                          v, -1, -1);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
            
        // -----------------
         setTask("Creating uniq package");
         v = new Vector();
         AclInfo acl = new AclInfo();
         acl.setAclName(userid);
         acl.setAclStatus(DropboxAccess.STATUS_USER);
         v.add(acl);
         
        // All opts on ... so package will be hidden
         int optsmask = 
            DropboxAccess.RETURNRECEIPT | DropboxAccess.SENDNOTIFY | DropboxAccess.HIDDEN;
         optsmask &= 255;
         pkgid = dropbox.createPackage(name, initdesc,
                                       pubpool.getPoolId(),
                                       pubpool.getPoolDefaultDays(), 
                                       v, optsmask, -1);
                                       
        // -----------------
         setTask("Create duplicate package in Drafts ... should fail");
         try {
            dropbox.createPackage(name, initdesc,
                                  pubpool.getPoolId(),
                                  pubpool.getPoolDefaultDays(), 
                                  v, optsmask, -1);
            fail(task);
         } catch(Exception e) {
            goodFailure(e);
         }

        // -----------------
         setTask("query new package");
         PackageInfo pinfo1 = dropbox.queryPackage(pkgid, false);
         if (pinfo1 == null || pinfo1.getPackageId() != pkgid) {
            fail(task);
         }
         
         
        // -----------------
         setTask("Set Expiration. Should fail with invalid packageid");
         try {
            dropbox.changePackageExpiration(-1, 0);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Set Expiration. Should fail with invalid expiration");
         try {
            dropbox.changePackageExpiration(pkgid, pubpool.getPoolMaxDays()+1);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Set Expiration 1");
         dropbox.changePackageExpiration(pkgid, pubpool.getPoolMaxDays());
         
         
        // -----------------
         setTask("Set Expiration 2");
         long expires = System.currentTimeMillis() +
            ((pubpool.getPoolDefaultDays()-1)*24*60*60*1000);
         dropbox.changePackageExpiration(pkgid, expires);
         
         
        // -----------------
         setTask("Set Package Flags. Should fail with invalid package");
         try {
            dropbox.setPackageFlags(-1, 0, 0);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Set Package Flags. Should fail with invalid Flags");
         try {
            dropbox.setPackageFlags(pkgid, -1, -1);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Set Package Flags");
        // Turn all off but HIDDEN
         dropbox.setPackageFlags(pkgid, (optsmask ^ DropboxAccess.HIDDEN), 0);
         
         
        // -----------------
         setTask("Set Package Description. Should fail with invalid package");
         try {
            dropbox.setPackageDescription(-1, adjustdesc);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Set Package Description");
         dropbox.setPackageDescription(pkgid, adjustdesc);
         
         
        // -----------------
         setTask("Query package again to check expiration, description and flags");
         PackageInfo pinfo2 = dropbox.queryPackage(pkgid, false);
         
         
         
         if ((pinfo1.getPackageFlags() & optsmask)  != optsmask) {
            fail("Package flags after create did not end up with ALL set: " + 
                 pinfo1.toString());
         }
         
         if ((pinfo2.getPackageFlags() & DropboxAccess.HIDDEN) != DropboxAccess.HIDDEN) {
            fail("Package flags did not end up with just HIDDEN: " + pinfo2.toString());
         }
         
         long expdiff = pinfo1.getPackageExpiration() - pinfo2.getPackageExpiration();
         if (expdiff <= 0) {
            fail("Original package expiration date NOT greater than adjusted");
         }
         
         if (expdiff > 25*60*60*1000) {
            fail("Original package expiration MORE than 25 hours later than adjust");
         }
         
         if (pinfo1.getPackageDescription() == null || 
             !pinfo1.getPackageDescription().equals(initdesc)) {
            fail("Initial description set at package create is not correct");
         }
         
         if (pinfo2.getPackageDescription() == null || 
             !pinfo2.getPackageDescription().equals(adjustdesc)) {
            fail("Adjusted description set with setPackageDescription is not correct");
         }
         
        // -----------------
         setTask("Add Valid USER ACL. Should work even though its already in list");
         dropbox.addUserAcl(pkgid, userid);
         
         
        // -----------------
         setTask("Remove Valid USER ACL. Should fail with invalid package");
         try {
            dropbox.removeUserAcl(-1, userid);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Remove Valid USER ACL");
         dropbox.removeUserAcl(pkgid, userid);
         
        // -----------------
         setTask("Remove Valid USER ACL. Should fail with user not in acl list");
         try {
            dropbox.removeUserAcl(pkgid, userid);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Add Valid USER ACL. Should fail with invalid package");
         try {
            dropbox.addUserAcl(-1, userid);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Commit package - SHOULD FAIL invalid package id");
         try {
            dropbox.commitPackage(-1);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Commit package - SHOULD FAIL cause no valid acls");
         try {
            dropbox.commitPackage(pkgid);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Create new group");
         
        // remove the group incase its there from a previous run
         try {
            dropbox.deleteGroup(groupname);
         } catch(Exception e) {}
         
         dropbox.createGroup(groupname);
         
        // -----------------
         setTask("Add Valid GROUP ACL - Should fail with invalid package");
         try {
            dropbox.addGroupAcl(-1, groupname);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Add Invalid GROUP ACL - Should fail as group does not exist");
         try {
            dropbox.addGroupAcl(pkgid, "GroupDoesNotExist123");
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Add Valid GROUP ACL");
         dropbox.addGroupAcl(pkgid, groupname);
         
        // -----------------
         setTask("Commit package - SHOULD FAIL cause no valid acls (empty group)");
         try {
            dropbox.commitPackage(pkgid);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Query package acl companies");
         Vector repc = dropbox.queryPackageAclCompanies(pkgid);
         if (repc.size() > 0) {
            fail("No valid acls on package. No acls should have been returned");
         }
         
        // -----------------
         setTask("Add user to group");
         dropbox.addGroupAcl(groupname, userid, true);
         
        // -----------------
         setTask("Add user2 to group");
         dropbox.addGroupAcl(groupname, useridu2, true);
         
        // -----------------
         setTask("Query package acl companies r2");
         repc = dropbox.queryPackageAclCompanies(pkgid);
         if (repc.size() == 0) {
            fail("Valid acls on package. At least one company should have been returned");
         }
         
        // -----------------
         setTask("Query represented companies on same group");
         Vector acls = new Vector();
         AclInfo aclinfo = new AclInfo();
         aclinfo.setAclName(groupname);
         aclinfo.setAclType(dropbox.STATUS_GROUP);
         acls.add(aclinfo);
         Vector repc2 = dropbox.queryRepresentedCompanies(acls, false);
         if (repc.size() != repc2.size()) {
            fail("Represented companies for group should have been the same as pkg");
         }
         
        // -----------------
         setTask("Query represented companies on same group");
         aclinfo = new AclInfo();
         aclinfo.setAclName("noway_this_name_is_good");
         aclinfo.setAclType(dropbox.STATUS_NONE);
         acls.add(aclinfo);
         try {
            dropbox.queryRepresentedCompanies(acls, false);
            fail("Should have failed as bad username acl was entered");
         } catch(DboxException dbe) {
            goodFailure(dbe);
         }
         
        // -----------------
         setTask("Query package as user2 - Should fail as its not committed yet");
         try {
            dropboxu2.queryPackage(pkgid, false);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Upload file - should fail as invalid package");
         
         try {
            File f = new File(fileToUpload);
            dropbox.uploadFileToPackage(-1, filename, f.length());
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         
        // -----------------
         setTask("Upload file");
         
         File f = new File(fileToUpload);
         long fileid = dropbox.uploadFileToPackage(pkgid, filename, f.length());
         Operation op = new UploadOperation(dropbox, f, pkgid, fileid);
         op.process();
         op.waitForCompletion(maxTransferTime*1000);
            
         if (op.getStatus() != op.STATUS_FINISHED) {
            op.abort();
            fail(task);
         }
         
        // -----------------
         setTask("Try to commit package as user2 - Should fail as its not his pack");
         try {
            dropboxu2.commitPackage(pkgid);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Commit package");
         dropbox.commitPackage(pkgid);
         
        // -----------------
         setTask("Try to commit package again - Should fail as its already comitted");
         try {
            dropbox.commitPackage(pkgid);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Query package as user2 - Should WORK as its committed and in grp");
         try {
            dropboxu2.queryPackage(pkgid, false);
         } catch(DboxException dbe) {}
         
        // -----------------
         setTask("Searching for committed package in my inbox - SHOULD FAIL as hidden");
         v = dropbox.queryPackages(name, false, false, false, false, false);
         PackageInfo pinfo = findByName(v, name);
         if (pinfo != null) {
            fail(task);
         }
                  
        // -----------------
         setTask("Set SHOWHIDDEN option");
         dropbox.setOption(DropboxAccess.ShowHidden, "true");
         
        // -----------------
         setTask("Searching for committed (hidden) package in my inbox.");
         v = dropbox.queryPackages(name, false, false, false, false, false);
         pinfo = findByName(v, name);
         if (pinfo == null) {
            fail(task);
         }
        
        // -----------------
         setTask("Check that package size == filesize");
         if (pinfo.getPackageSize() != f.length()) {
            fail(task);
         }
        
         setTask("Check that the package is COMMITTED");
         if (pinfo.getPackageStatus() != DropboxGenerator.STATUS_COMPLETE) {
            fail(task);
         }
        
        // -----------------
         setTask("Searching for committed package in my u2 inbox - SHOULD FAIL as hidden");
         v = dropboxu2.queryPackages(name, false, false, false, false, false);
         pinfo = findByName(v, name);
         if (pinfo != null) {
            fail(task);
         }
        
        // -----------------
         setTask("Set package option so its NOT hidden");
         dropbox.setPackageFlags(pkgid, DropboxAccess.HIDDEN & 0xff, 0);
         
        // -----------------
         setTask("Searching for committed package in my u2 inbox");
         v = dropboxu2.queryPackages(name, false, false, false, false, false);
         pinfo = findByName(v, name);
         if (pinfo == null) {
            fail(task);
         }
        
        // -----------------
         setTask("Remove ACL from group");
         dropbox.removeGroupAcl(groupname, userid, true);
         
        // -----------------
         setTask("Searching for package in my inbox. Should Fail as not in group");
         v = dropbox.queryPackages(name, false, false, false, false, false);
         pinfo = findByName(v, name);
         if (pinfo != null) {
            fail(task);
         }
        
         setTask("Add Valid USER ACL");
         dropbox.addUserAcl(pkgid, userid);
         
        // -----------------
         setTask("Searching for package in my inbox filter complete/marked - should WORK");
         v = dropbox.queryPackages(name, false, false, true, true, false);
         pinfo = findByName(v, name);
         if (pinfo == null) {
            fail(task);
         }
         
        // -----------------
         setTask("Mark package");
         dropbox.markPackage(pkgid, true);
         
        // -----------------
         setTask("Searching for package in my inbox filter complete/marked - should FAIL");
         v = dropbox.queryPackages(name, false, false, true, true, false);
         pinfo = findByName(v, name);
         if (pinfo != null) {
            fail(task);
         }
         
         
        // -----------------
         setTask("Download file - Should fail invalid package");
         f = new File(localDownloadFileDir + File.separator + filename);
         try {
            op = new DownloadOperation(dropbox, f, -1, fileid);
            op.process();
            op.waitForCompletion(maxTransferTime*1000);
            if (op.getStatus() == op.STATUS_FINISHED) {
               op.abort();
               fail(task);
            }
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         f.delete();
         
        // -----------------
         setTask("Download file - Should fail invalid file");
         f = new File(localDownloadFileDir + File.separator + filename);
         try {
            op = new DownloadOperation(dropbox, f, pkgid, -1);
            op.process();
            op.waitForCompletion(maxTransferTime*1000);
            if (op.getStatus() == op.STATUS_FINISHED) {
               op.abort();
               fail(task);
            }
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         f.delete();
         
        // -----------------
         setTask("Download file");
         f = new File(localDownloadFileDir + File.separator + filename);
         op = new DownloadOperation(dropbox, f, pkgid, fileid);
         op.process();
         op.waitForCompletion(maxTransferTime*1000);
            
         if (op.getStatus() != op.STATUS_FINISHED) {
            op.abort();
            fail(task);
         }
         
         f.delete();
         
        // -----------------
         setTask("Searching for package in my inbox filter complete only - should FAIL");
         v = dropbox.queryPackages(name, false, false, true, false, false);
         pinfo = findByName(v, name);
         if (pinfo != null) {
            fail(task);
         }
         
        // -----------------
         setTask("Download Package - should FAIL as invalid package ");
         
         f = new File(localDownloadFileDir + File.separator + filename);
         FileOutputStream ostream = new FileOutputStream(f.getAbsolutePath(), 
                                                         false);
         try {
            op = new PackageDownloadOperation(dropbox, ostream, 
                                              -1, "zip");
            op.process();
            op.waitForCompletion(maxTransferTime*1000);
            
            if (op.getStatus() == op.STATUS_FINISHED) {
               op.abort();
               fail(task);
            }
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         f.delete();
         
        // -----------------
         setTask("Download Package - should FAIL as invalid encoding ");
         
         f = new File(localDownloadFileDir + File.separator + filename);
         ostream = new FileOutputStream(f.getAbsolutePath(), false);
         try {
            op = new PackageDownloadOperation(dropbox, ostream, 
                                              pkgid, "badenc");
            op.process();
            op.waitForCompletion(maxTransferTime*1000);
            
            if (op.getStatus() == op.STATUS_FINISHED) {
               op.abort();
               fail(task);
            }
         } catch(DboxException dbe) { goodFailure(dbe); }
         
         f.delete();
         
        // -----------------
         setTask("Download Package as zip");
         
         f = new File(localDownloadFileDir + File.separator + filename);
         ostream = new FileOutputStream(f.getAbsolutePath(), false);
         op = new PackageDownloadOperation(dropbox, ostream, 
                                           pkgid, "zip");
         op.process();
         op.waitForCompletion(maxTransferTime*1000);
            
         if (op.getStatus() != op.STATUS_FINISHED) {
            op.abort();
            fail(task);
         }
         
         f.delete();
         
         
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
         fail("Exception during task [" + task + "]: " + ee.toString());
      } finally {
      }
   }
   
   
   public void testRegexPackageSearch() {
      setTestcase("testRegexPackageSearch");
      login();
      try { 
         Random r = new Random();
         long lval = r.nextLong();
         String name = "jojotest" + lval;
         
        // -----------------
         deletePackage(name);
         
        // -----------------
         setTask("Create unique package");
         pkgid = dropbox.createPackage(name);
            
        // -----------------
         setTask("Find package using regexp");
         String arr[] = new String[] {"jo*" + lval, "*", "*"+lval, "*o*", "jojo*"};
         for(int i=0; i < arr.length; i++) {
            String regex = arr[i];
            Vector v = dropbox.queryPackages(regex, true, true, false, false, false);
            if (findByName(v, name) == null) {
               fail("Name [" + name + "] not found using regx srch " + regex);
            }
         }
         
        // -----------------
         setTask("Find package using regexp ... non should match");
         arr = new String[] {"*p*" + lval, "*t", "jojo", ""+lval, "jogo*"};
         for(int i=0; i < arr.length; i++) {
            String regex = arr[i];
            Vector v = dropbox.queryPackages(regex, true, true, false, false, false);
            if (findByName(v, name) != null) {
               fail("Name [" + name + "] found using regx srch " + regex);
            }
         }
         
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
         fail("Exception during task [" + task + "]: " + ee.toString());
      } finally {
      }
   }
   
   public void testGroups() {
      setTestcase("testGroups");
      login();
      loginu2();
      try { 
      
        // -----------------
         setTask("Create new group");
         
        // remove the group incase its there from a previous run
         try {
            dropbox.deleteGroup(groupname);
         } catch(Exception e) {}
         
         dropbox.createGroup(groupname);
         
        // -----------------
         setTask("Create group again ... should fail as already exists");
         try {
            dropbox.createGroup(groupname);
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
        // -----------------
         setTask("Add invalid ACL to group - Should FAIL");
         try {
            dropbox.addGroupAcl(groupname, "nowayisreal@nothing.com", true);
            fail(task);
         } catch(Exception e) { goodFailure(e); }
        
        // -----------------
         setTask("Add user2 ACL to group as member");
         dropbox.addGroupAcl(groupname, useridu2, true);
         
        // -----------------
         setTask("Query groups as user2 looking for new group - Should fail");
         HashMap ht = dropboxu2.queryGroups(groupname, false, true, true);
         if (ht != null  && ht.size() > 0) {
            fail(task);
         }
         
        // -----------------
         setTask("Set visibility to member");
         dropbox.modifyGroupAttributes(groupname, 
                                       DropboxAccess.GROUP_SCOPE_MEMBER,
                                       DropboxAccess.GROUP_SCOPE_NONE);
                                       
        // -----------------
         setTask("Query groups as user2 looking for new group");
         ht = dropboxu2.queryGroups(groupname, false, true, true);
         if (ht == null || ht.size() == 0) {
            fail(task);
         }
         
         GroupInfo ginfo = (GroupInfo)ht.get(groupname);
         
        // -----------------
         setTask("Check group settings for member NOT valid");
         if (ginfo.isGroupMembersValid()) {
            fail(task + ": groupmembers should NOT be valid!");
         }
         
        // -----------------
         setTask("Check group settings for access NOT valid");
         if (ginfo.isGroupAccessValid()) {
            fail(task + ": groupaccess should NOT be valid!");
         }
         
        // -----------------
         setTask("Check group owner setting is correct");
         if (!ginfo.getGroupOwner().equals(userid)) {
            fail(task + ": Owner not " + userid);
         }
         
        // -----------------
         setTask("Check time delta is 'recent' with slush for clock differences");
         long timedelt = System.currentTimeMillis() - ginfo.getGroupCreated() ;
         if (timedelt > (1000*60*10) || timedelt < -(1000*60*10)) {
            fail(task + ": createtime too far out of whack: " + timedelt);
         }
         
        // -----------------
         setTask("Check visibility/listability. Should both be NONE");
         if (ginfo.getGroupVisibility()  != DropboxAccess.GROUP_SCOPE_NONE ||
             ginfo.getGroupListability() != DropboxAccess.GROUP_SCOPE_NONE) {
            fail(task + ": group listability/visibility not NONE");
         }
         
        // -----------------
         setTask("Set listability to member");
         dropbox.modifyGroupAttributes(groupname, 
                                       DropboxAccess.GROUP_SCOPE_NONE,
                                       DropboxAccess.GROUP_SCOPE_MEMBER);
                                       
        // -----------------
         setTask("Query groups as user2 looking for new group");
         ht = dropboxu2.queryGroups(groupname, false, true, true);
         if (ht == null || ht.size() == 0) {
            fail(task);
         }
         
         ginfo = (GroupInfo)ht.get(groupname);
         
        // -----------------
         setTask("Check group settings for member valid");
         if (!ginfo.isGroupMembersValid()) {
            fail(task + ": groupmembers should be valid!");
         }
         
        // -----------------
         setTask("Check group settings for access NOT valid");
         if (ginfo.isGroupAccessValid()) {
            fail(task + ": groupaccess should NOT be valid!");
         }
         
        // -----------------
         setTask("Check group visibility/listability are both valid");
         if (ginfo.getGroupVisibility()  != DropboxAccess.GROUP_SCOPE_NONE ||
             ginfo.getGroupListability() != DropboxAccess.GROUP_SCOPE_NONE) {
            fail(task + ": group listability/visibility not NONE");
         }
         
        // -----------------
         setTask("Check group members list correct");
         Vector v = ginfo.getGroupMembers();
         if (v == null || v.size() != 1 || !((String)v.elementAt(0)).equals(useridu2)) {
            fail(task + ": user2 not in groupmember list");
         }
         
        // -----------------
         setTask("add user2 as access member");
         dropbox.addGroupAcl(groupname, useridu2, false);
         
        // -----------------
         setTask("Query groups as user2 looking for new group");
         ht = dropboxu2.queryGroups(groupname, false, true, true);
         if (ht == null || ht.size() == 0) {
            fail(task);
         }
         
         ginfo = (GroupInfo)ht.get(groupname);
         
        // -----------------
         setTask("Check group settings for member valid");
         if (!ginfo.isGroupMembersValid()) {
            fail(task + ": groupmembers should be valid!");
         }
         
        // -----------------
         setTask("Check group settings for access valid");
         if (!ginfo.isGroupAccessValid()) {
            fail(task + ": groupaccess should be valid!");
         }
         
        // -----------------
         setTask("Check group listability/visibility set to MEMBER");
         if (ginfo.getGroupVisibility()  != DropboxAccess.GROUP_SCOPE_MEMBER ||
             ginfo.getGroupListability() != DropboxAccess.GROUP_SCOPE_MEMBER) {
            fail(task + ": group listability/visibility not MEMBER");
         }
         
        // -----------------
         setTask("Check that group access list has only user2");
         v = ginfo.getGroupAccess();
         if (v == null || v.size() != 1 || !((String)v.elementAt(0)).equals(useridu2)) {
            fail(task + ": user2 not in groupaccess list");
         }
         
        // -----------------
         setTask("user2 tries to delete group - should fail");
         try {
            dropboxu2.deleteGroup(groupname);
            fail(task);
         } catch(DboxException dbe) {}
         
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
         fail("Exception during task [" + task + "]: " + ee.toString());
      } finally {
      }
   }
   
   public void testLookup() {
      setTestcase("testLookup");
      login();
      loginu2();
      try { 
      
        // -----------------
         setTask("Lookup user1 id as user1");
         Vector v = dropbox.lookupUser(userid, false);
         if (v.size() == 0) fail("No element returned from lookup of " + userid);
         AclInfo aclinfo = ((AclInfo)v.firstElement());
         if (!aclinfo.getAclName().equals(userid)) {
            fail("Userid in aclinfo does not match: " + userid + " " + aclinfo.toString());
         }
         
        // -----------------
         setTask("Lookup user2 id as user1");
         v = dropbox.lookupUser(useridu2, false);
         if (v.size() == 0) fail("No element returned from lookup of " + userid);
         aclinfo = ((AclInfo)v.firstElement());
         if (!aclinfo.getAclName().equals(useridu2)) {
            fail("Userid in aclinfo does not match: " + useridu2 + " " + aclinfo.toString());
         }
         
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
         fail("Exception during task [" + task + "]: " + ee.toString());
      } finally {
      }
   }
   
   
  // All new tests should use this as a startup template
   public void testTemplate() {
      setTestcase("testTemplate");
      login();
      try { 
        // -----------------
         setTask("fake task name for positive test");
        // dropbox action

         setTask("fake task name for negative test");
         try {
           // dropbox action
            if (userid != null) throw new DboxException("test");
            fail(task);
         } catch(DboxException dbe) { goodFailure(dbe); }
         
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
         fail("Exception during task [" + task + "]: " + ee.toString());
      } finally {
      }
   }
}
