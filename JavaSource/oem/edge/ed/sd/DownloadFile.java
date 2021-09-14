package oem.edge.ed.sd;

import java.io.*;
import oem.edge.common.cipher.ODCrc;
/**
 * Insert the type's description here.
 * Creation date: (9/10/2001 4:22:55 PM)
 * @author: Administrator
 */
public class DownloadFile implements Runnable{
       private long filesize;
       private long currentFilesize;
       private long startTime;     //MPZ
       private long pollTime;      //MPZ
       private long startFilesize; //MPZ
       private boolean working;    //MPZ
       private int percentDownload;
       public java.lang.String fileDestination;
       public long fileId;
       private java.lang.String fileName;
       private java.lang.String servletURL;
       private org.w3c.dom.Node fileNode = null;
       private ODCrc crcChecker;
       protected transient java.beans.PropertyChangeSupport propertyChange;
       private java.lang.String fieldStatus = "Idle";
       public long totalFilesize;
       public boolean stop = false;
       public boolean createFolder = false;
/**
 * DownloadFile constructor comment.
 */
public DownloadFile() {
       super();
}
/**
 * The addPropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
       getPropertyChange().addPropertyChangeListener(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (10/30/2001 2:46:42 PM)
 */
private void computePercent() {
       int newPercent = 0;
       long currentTime = 0;
       Long filesize = null;//MPZ
       //	System.out.println("percent Download" + percentDownload);
       // compute the percent downloaded
       if (fileNode!=null && currentFilesize!=0) {
	       //	System.out.println("First If block");
              String size = fileNode.getAttributes().getNamedItem("size").getNodeValue();
             	filesize = new Long(size);
              Long percent =new Long(100*(currentFilesize)/filesize.longValue());
              newPercent = percent.intValue() ;
              currentTime = System.currentTimeMillis(); //MPZ
              }

       // Update download label? MPZ
       if (currentTime - pollTime >= 1000 || newPercent != percentDownload) {
              // update progress bar?
                	//System.out.println("Second If block");
              if (newPercent != percentDownload){
                  //	System.out.println("Second Inner FIrst IF");    
	              	percentDownload = newPercent;
                    firePropertyChange("percentDownload",new Integer(101),new Integer(percentDownload));
                    }
              long cfs = currentFilesize;
            //  System.out.println("polltime " + pollTime + "starttime " + startTime);
              if (cfs > startFilesize && pollTime > startTime) {
                	//System.out.println("polltime " + pollTime + "starttime " + startTime);    
	              	
                    long bps = (cfs - startFilesize) / ((currentTime - startTime) / 1000);
                    //System.out.println("bps: " + bps);
                    long timeLeft = (filesize.longValue()- cfs) / bps;
                    long hours = timeLeft/3600;
                    timeLeft -= hours * 3600;
                    long minutes = timeLeft/60;
                    timeLeft -= minutes * 60;
                    //System.out.println("bps: " + bps + " hours: " + hours + " minutes: " + minutes + " sec: " + timeLeft);
                    fieldStatus = getFilename() +" Time remaining: " + ((hours < 10) ? "0" + Long.toString(hours) : Long.toString(hours)) + ":" + ((minutes < 10) ? "0" + Long.toString(minutes) : Long.toString(minutes)) + ":" + ((timeLeft < 10) ? "0" + Long.toString(timeLeft) : Long.toString(timeLeft) + " ," + bps + " bps )");
                    }
              else
                    fieldStatus = getFilename();
              
              pollTime = currentTime;
             // System.out.println("fieldStatus is " + fieldStatus);s
              firePropertyChange("status","Download",fieldStatus);
              }
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 1:22:57 PM)
 * @return boolean
 */
public boolean copyFile(String copyordernumber,HttpsConnect connection) {
       
       int retries = 0;
long totalFilesize = new Long(fileNode.getAttributes().getNamedItem("size").getNodeValue()).longValue();
       File destfolder =  new File(fileDestination);
       if (! destfolder.exists())
              if(! destfolder.mkdirs()){
                     fieldStatus = "Unable to create destination folder, download stopped!";
                    // System.out.println("status is " + fieldStatus);
                     firePropertyChange("status","Download",fieldStatus);
                     stop();
                     return false;
              }
               
       boolean returnValue = true;
       
       // boolean value to decide to write to the file from begining or in append mode
       boolean append  = false;       
       
       synchronized (this) {
                     currentFilesize =0;
       }
       
       System.out.println("Inside Download getting file name");
//       String destination = fileNode.getAttributes().getNamedItem("destination").getNodeValue();
       String filename = fileNode.getAttributes().getNamedItem("name").getNodeValue();
       String orderNumber = fileNode.getParentNode().getAttributes().getNamedItem("number").getNodeValue().trim();
       File currentFilename= null;
       File copyFilename= null;
       // Create Subfolders ??
       if( ! createFolder){
              //currentFilename = fileDestination + orderNumber  +  filename;
              copyFilename = new File(destfolder,copyordernumber  +  filename);
              currentFilename = new File(destfolder,orderNumber  +  filename);
       }
       else{
              java.io.File file = new java.io.File(destfolder,orderNumber);
              java.io.File copyfile = new java.io.File(destfolder,copyordernumber);
              if(! file.exists())
                     if(!file.mkdir()){
                            fieldStatus = "Unable to create subfolder, download stopped!";
                            //System.out.println("status is " + fieldStatus);
                            firePropertyChange("status","Download",fieldStatus);
                            stop();
                            return false;
                     }
                     //currentFilename = file.getPath()+ File.separator + filename;
                     currentFilename = new File(file,filename);
                     copyFilename = new File(copyfile,filename);
       }       
       
       //java.io.File file = new java.io.File(currentFilename);
       java.io.File file = currentFilename;
       java.io.File copyfile = copyFilename;
       // Initializing the download label and progress bar
       if(isStopping())
       return false;
       fieldStatus = "Preparing to download " + filename;
       //System.out.println("status is " + fieldStatus);
       firePropertyChange("status","Download",fieldStatus);
       
       // checking to see if the file exists and processing
       
              java.io.FileInputStream fileInputStream = null;
                     //opening the existing file
                     try{
                            fileInputStream= new java.io.FileInputStream(copyfile);
                     }
                     catch(Exception e){
                            System.out.println("File IO " + e.getMessage());
                     }
              
              
                     // build and send url to server
                     if(isStopping())
                            return false;
              
                            // writing contents to the file once successfull connection is established
              java.io.FileOutputStream filewrite = null;
              try{
                     //filewrite = new java.io.FileOutputStream(currentFilename,append);
                     filewrite = new java.io.FileOutputStream(currentFilename.getPath(),append);
              }
              catch( Exception e){
                      System.out.println("In download method in trying to open file for Output"+ e.getMessage());
                     e.printStackTrace();
              }
                     
              byte[] filebuffer = new byte[4096];
              int amount=0;

              percentDownload = -1;
              startFilesize = currentFilesize;     //MPZ
              startTime = System.currentTimeMillis(); //MPZ
              pollTime = startTime;                //MPZ
              working = true;                      //MPZ
              Thread t = new Thread(this);         //MPZ
              t.start();                           //MPZ

              try{
                     while((amount=fileInputStream.read(filebuffer))!=-1){
                                          
                            filewrite.write(filebuffer,0,amount);
                            synchronized (this) {
                                   currentFilesize += amount;       
                            }
                            if(isStopping()){
                                   try{
                                          filewrite.close();
                                   }
                                   catch( OutOfMemoryError error){
                                          filewrite.close();
                                          System.out.println("In download method in out of memory error"+error.getMessage());
                                          error.printStackTrace();
                                   }
                                   working = false; //MPZ
                                   return false;
                            }
                            // computePercent(); MPZ
                     }
                     working = false; //MPZ
                     //Closing the connection after completing the file download
                     filewrite.close();
              }
              catch( Exception e){
                     working = false; //MPZ
                     try{ filewrite.close();}
                     catch(IOException ioE){ioE.printStackTrace();}
                     fieldStatus = "Download of " + filename + " interrupted , Trying to Reconnect";
                    // System.out.println("status is " + fieldStatus);
                     firePropertyChange("status","Download",fieldStatus);
                     System.out.println("In download method in trying to read the inputstream"+e.getMessage());
                     e.printStackTrace();
              }
       
       
       // Sending Download Confirmation to the server
       if(connection.confirmDownload(fileNode.getFirstChild().getNodeValue())){
              byte[] buffer = new byte[4096];
                     int amount1=0;
                     try{
                            while((amount1=connection.getData().read(buffer))!=-1);
                                    System.out.println(buffer);
                            connection.getData().close();
                     }
                     catch( Exception e){
                             System.out.println("In download method in trying to confirm download"+e.getMessage());
                            e.printStackTrace();
                     }
                     
                     
       }
       
       // Updating the label and Progress Bar       
       fieldStatus = "Download of " + filename + " completed";
      // System.out.println("status is " + fieldStatus);
       firePropertyChange("status","Download",fieldStatus);
       return returnValue ;
}
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 9:44:06 PM)
 */
public boolean download(HttpsConnect connection) {
int retries = 0;
long totalFilesize = new Long(fileNode.getAttributes().getNamedItem("size").getNodeValue()).longValue();
       File destfolder =  new File(fileDestination);
       if (! destfolder.exists())
              if(! destfolder.mkdirs()){
                     fieldStatus = "Unable to create destination folder, download stopped!";
                    // System.out.println("status is " + fieldStatus);
                     firePropertyChange("status","Download",fieldStatus);
                     stop();
                     return false;
              }
               
       boolean returnValue = true;
       
       // boolean value to decide to write to the file from begining or in append mode
       boolean append  = true;       
       
       synchronized (this) {
                     currentFilesize =0;
       }
       
       String filename = fileNode.getAttributes().getNamedItem("name").getNodeValue();
       String orderNumber = fileNode.getParentNode().getAttributes().getNamedItem("number").getNodeValue().trim();
       File currentFilename= null;
       
       // Create Subfolders ??
       if( ! createFolder)
              //currentFilename = fileDestination + orderNumber  +  filename;
              currentFilename = new File(destfolder,orderNumber  +  filename);
       else{
              java.io.File file = new java.io.File(destfolder,orderNumber);
              if(! file.exists())
                     if(!file.mkdir()){
                            fieldStatus = "Unable to create subfolder, download stopped!";
                           // System.out.println("status is " + fieldStatus);
                            firePropertyChange("status","Download",fieldStatus);
                            stop();
                            return false;
                     }
                     //currentFilename = file.getPath()+ File.separator + filename;
                     currentFilename = new File(file,filename);
       }       
       
       java.io.File file = currentFilename;

       // Initializing the download label and progress bar
       if(isStopping())
       return false;
       fieldStatus = "Preparing to download " + filename;
       //System.out.println("status is " + fieldStatus);
       firePropertyChange("status","Download",fieldStatus);
       
       // checking to see if the file exists and processing
       while((file.length()<totalFilesize)){       
              if( file.exists()){
                     System.out.println("The File Already exists using skip");
                     synchronized(this) {
                     currentFilesize = file.length();       
                     }
                     System.out.println("the existing file is " + file.getPath());
                     // System.out.println("set current filesize");
                     java.io.FileInputStream fileInputStream = null;
              
                     //opening the existing file
                     try{
                            fileInputStream= new java.io.FileInputStream(file);
                     }
                     catch(Exception e){
                            System.out.println("File IO " + e.getMessage());
                     }
              
                     // checking the crc for the partially downloaded file
                     System.out.println("current file size" + currentFilesize);
                     int checksize = (int)currentFilesize/20;
                     //while(checksize>20000000)
                     //checksize =(int) checksize/2;
                     System.out.println("Checksize "+checksize);
                     byte[] crcArray = new byte[4096];
                     byte[] skipArray = new byte[8192];

                     
              			int crcValue=0;
                     try{
							long tobeSkipped = 	currentFilesize-checksize;	                     
	 						System.out.println("To be skipped = " + tobeSkipped);
	                     	long skipped = 0;
	                     	
	 						while(tobeSkipped>8192){
		 						skipped = fileInputStream.skip(8192);
		 						//System.out.println("Skipped Now" +skipped);
		 						tobeSkipped = tobeSkipped - skipped;
		 						//Thread.sleep(4000);
		 						//System.out.println("To be skipped in new code" +tobeSkipped);
	 						}
	 						int finalskip = (int)tobeSkipped;
	 						fileInputStream.skip(finalskip);
                           	if(crcChecker==null)
                            	crcChecker= new ODCrc();
              
                     		crcChecker.resetCRC();
							int bytesRead=0;
                     		while((bytesRead = fileInputStream.read(crcArray))>0)
                     			crcChecker.generateCRC(crcArray,0,bytesRead);
                     		fileInputStream.close();
                     		crcValue = crcChecker.getCRC();
                     }
                     catch(Exception e){
                             System.out.println("In download method in trying to read partial file"+e.getMessage());
                            e.printStackTrace();
                     }
                     
                     // Initialize CRCchecker if not initialized already 
                     
              
                     System.out.println("CRC value"+ crcValue);
              

                     // build and send url to server
                     if(isStopping())
                            return false;
              
                     if(!connection.getFile(fileNode.getFirstChild().getNodeValue(),Integer.toString(checksize),Long.toString(currentFilesize),fileNode.getAttributes().getNamedItem("size").getNodeValue(),fileNode.getAttributes().getNamedItem("time").getNodeValue(),Integer.toString(crcValue))){
                            System.out.println("CRC check failed");
                            synchronized (this) {
                                   currentFilesize =0;
                            }
                            append = false;
                            if(isStopping())
                                   return false;
                            if(!connection.getFile(fileNode.getFirstChild().getNodeValue())){
                                   System.out.println("Connection Failed in partial download method ");
                                   return false;
                            }
                     }
                     // System.out.println("Connection Succeeded in partial download method ");              
              }
       
              else{ // if the file to be downloaded did not exist in the specified path 
                     if(isStopping())
                            return false;
                     if(!connection.getFile(fileNode.getFirstChild().getNodeValue())){
                            System.out.println("Connection Failed in downloading Complete File");
                            return false;
                     }
              }

              // writing contents to the file once successfull connection is established
              java.io.FileOutputStream filewrite = null;
              try{
                     //filewrite = new java.io.FileOutputStream(currentFilename,append);
                     filewrite = new java.io.FileOutputStream(currentFilename.getPath(),append);
              }
              catch( Exception e){
                      System.out.println("In download method in trying to open file for Output"+ e.getMessage());
                     e.printStackTrace();
              }
                     
              byte[] filebuffer = new byte[4096];
              int amount=0;

              percentDownload = -1;
              startFilesize = currentFilesize;     //MPZ
              startTime = System.currentTimeMillis(); //MPZ
              pollTime = startTime;                //MPZ
              working = true;                      //MPZ
              Thread t = new Thread(this);         //MPZ
              t.start();                           //MPZ

              try{
                     while((amount=connection.getData().read(filebuffer))!=-1){
                                          
                            filewrite.write(filebuffer,0,amount);
                            synchronized (this) {
                                   currentFilesize += amount;       
                            }
                            if(isStopping()){
                                   try{
                                          connection.getData().close();
                                          filewrite.close();
                                   }
                                   catch( OutOfMemoryError error){
                                          filewrite.close();
                                          System.out.println("In download method in out of memory error"+error.getMessage());
                                          error.printStackTrace();
                                   }
                                   working = false; //MPZ
                                   return false;
                            }
                            //computePercent(); MPZ
                     }
                     //Closing the connection after completing the file download
                     working = false; //MPZ
                     connection.getData().close();
                     filewrite.close();
              }
              catch( Exception e){
                     working = false; //MPZ
                     try{ filewrite.close();}
                     catch(IOException ioE){ioE.printStackTrace();}
                     fieldStatus = "Download of " + filename + " interrupted , Trying to Reconnect";
                     //System.out.println("status is " + fieldStatus);
                     firePropertyChange("status","Download",fieldStatus);
                     //System.out.println("In download method in trying to read the inputstream"+e.getMessage());
                     e.printStackTrace();
              }
       if(retries >=10)
              return false;
              System.out.println("Retry Number "+ retries); 
       retries++;
}
       
       // Sending Download Confirmation to the server
       if(connection.confirmDownload(fileNode.getFirstChild().getNodeValue())){
              byte[] buffer = new byte[4096];
                     int amount1=0;
                     try{
                            while((amount1=connection.getData().read(buffer))!=-1);
                                    System.out.println(buffer);
                            connection.getData().close();
                     }
                     catch( Exception e){
                             System.out.println("In download method in trying to confirm download"+e.getMessage());
                            e.printStackTrace();
                     }
                     
                     
       }
       
       // Updating the label and Progress Bar       
       fieldStatus = "Download of " + filename + " completed";
       //System.out.println("status is " + fieldStatus);
       firePropertyChange("status","Download",fieldStatus);
       return returnValue ;
}
/**
 * The firePropertyChange method was generated to support the propertyChange field.
 */
public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
       getPropertyChange().firePropertyChange(propertyName, oldValue, newValue);
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 7:25:49 PM)
 * @return java.lang.String
 */
public String getFilename() {
       //System.out.println("getFilename called ");
       if(fileNode!=null){
             // System.out.println("getFilename called in if block");
              return ("Downloaded " + getPercentDownload() + "% of " + fileNode.getAttributes().getNamedItem("name").getNodeValue());
       }
       else
       return "Idle";
}
/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 1:31:21 PM)
 * @return int
 */
public int getPercentDownload() {
       return percentDownload ;
}
/**
 * Accessor for the propertyChange field.
 */
protected java.beans.PropertyChangeSupport getPropertyChange() {
       if (propertyChange == null) {
              propertyChange = new java.beans.PropertyChangeSupport(this);
       };
       return propertyChange;
}
/**
 * Gets the status property (java.lang.String) value.
 * @return The status property value.
 */
public java.lang.String getStatus() {
       return fieldStatus;
}
/**
 * The hasListeners method was generated to support the propertyChange field.
public synchronized boolean hasListeners(String propertyName) {
       return getPropertyChange().hasListeners(propertyName);
}
 */
/**
 * Insert the method's description here.
 * Creation date: (11/5/2001 4:54:03 PM)
 * @return boolean
 */
public synchronized boolean isStopping() {
       return stop;
}
/**
 * The removePropertyChangeListener method was generated to support the propertyChange field.
 */
public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
       getPropertyChange().removePropertyChangeListener(listener);
}
//MPZ
public void run() {
       while (working) {
             computePercent();
             try { Thread.currentThread().sleep(2000); }
             catch (Exception e) {}
       }
}
//MPZ
/**
 * Insert the method's description here.
 * Creation date: (10/18/2001 10:26:31 PM)
 * @param filenode org.w3c.dom.Node
 */
public void setFilenode(org.w3c.dom.Node filenode) {
      // System.out.println("Set filenode called getting file name");
       fileNode = filenode;
       
       }
/**
 * Insert the method's description here.
 * Creation date: (11/5/2001 4:53:26 PM)
 */
public synchronized void stop() {
       stop = true;
       fieldStatus = "Stopping ....";
       firePropertyChange("status","Download",fieldStatus);
}
}
