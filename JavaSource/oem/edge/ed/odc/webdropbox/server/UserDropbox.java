package oem.edge.ed.odc.webdropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.ftp.client.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.*;

import javax.servlet.http.*;

import java.text.DateFormat;
import java.util.*;
import java.io.*;
import java.util.zip.*;

import oem.edge.ed.odc.dropbox.client.sftpDropbox;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/**
 * UserDropbox.java hosts all the important utilities for processing
 * Inbox,drafts,Sent,Options for the Web Based Dropbox.
 *  
 **/
public class UserDropbox extends sftpDropbox implements HttpSessionBindingListener {

   private WebDboxInboxInfo inboxInfoBean;
   private DboxPackageInfo inboxPackBean;
   private DboxFileInfo inboxFileBean;
   
   private WebDboxTrashInfo trashInfoBean;
   private DboxPackageInfo trashPackBean;
   private DboxFileInfo trashFileBean;
   
   
   
   private WebDboxDraftPkgInfo draftInfoBean;
   private DboxPackageInfo draftPackBean;
   private DboxFileInfo draftFileBean;
   
   private WebDboxSentPkgInfo sentInfoBean;
   private DboxPackageInfo sentPackBean;
   private DboxFileInfo sentFileBean;
   
   
   
   private WebDboxCreateDraftPkgInfo creatDftInfoBean;
   private WebDboxCreateSentPkgInfo creatSntInfoBean;
   
   private WebDboxCreateGroupInfo createGroupInfoBean;
   
   private WebDboxDispGroupInfo groupInfoBean;
   
  // JMC
   private PoolInfo selectPool;
   public PoolInfo getSelectedPoolInfo() throws Exception {
     // If no pool selected, get the public pool
      if (selectPool == null) selectPool = getStoragePoolInstance(0);
      return selectPool;
   }
   public void setSelectedPoolInfo(PoolInfo pi) {
      selectPool = pi;
   }
	
   protected Date startTime = new Date();
        
   private long timeout = 300*1000;
   public void setTimeout(long to) { timeout = to;   }
   public long getTimeout()        { return timeout; }
   public Date getStartTime() {
      return startTime;
   }
   
  
 

   boolean firstTime=true;
   boolean trashFirstTime=true;
   
   boolean draftFirstTime=true;
   boolean sentFirstTime=true;
   
   boolean createDraftFirstTime=true;
   boolean createGroupsFirstTime=true;
   boolean createSentFirstTime=true;
   
   boolean itarSessionCertified=false;
   boolean itarEntitled=false;
   
   boolean SendNotificationDefault=false;
   boolean ReturnReceiptDefault=false;
   boolean NewPackageEmailNotification=false;
   boolean NagNotification=false;
   
            
  // Don't need to do anything when value is bound
   public void valueBound(HttpSessionBindingEvent event) {
   }
   
  // When unbound from session, just do a disconnect
   public void valueUnbound(HttpSessionBindingEvent event) {
      try {
         disconnect();
      } catch(Exception e) {}
   }
   
   
  /**
   * Constructor for UserDropbox.
   */
   public UserDropbox() {
      super(false, false);
      setClientType("web");
      setMaxUploadBufferSize(200*1024);
      setMaxDownloadBufferSize(200*1024);
   }

  

/**
 * @return Returns the itarSessionCertified.
 */
public boolean isItarSessionCertified() {
	return itarSessionCertified;
}
/**
 * @param itarSessionCertified The itarSessionCertified to set.
 */
public void setItarSessionCertified(boolean itarSessionCertified) {
	this.itarSessionCertified = itarSessionCertified;
}

/**
 * @return Returns the itarEntitled.
 */
public boolean isItarEntitled() {
	return itarEntitled;
}
/**
 * @param itarEntitled The itarEntitled to set.
 */
public void setItarEntitled(boolean itarEntitled) {
	this.itarEntitled = itarEntitled;
}
/**
 * @return Returns the df.
 */
public DateFormat getDf() {
	return df;
}
/**
 * @param df The df to set.
 */
public void setDf(DateFormat df) {
	this.df = df;
}
/**
 * @return Returns the nagNotification.
 */
public boolean isNagNotification() {
	return NagNotification;
}
/**
 * @param nagNotification The nagNotification to set.
 */
public void setNagNotification(boolean nagNotification) {
	NagNotification = nagNotification;
}
/**
 * @return Returns the newPackageEmailNotification.
 */
public boolean isNewPackageEmailNotification() {
	return NewPackageEmailNotification;
}
/**
 * @param newPackageEmailNotification The newPackageEmailNotification to set.
 */
public void setNewPackageEmailNotification(boolean newPackageEmailNotification) {
	NewPackageEmailNotification = newPackageEmailNotification;
}
/**
 * @return Returns the returnReceiptDefault.
 */
public boolean isReturnReceiptDefault() {
	return ReturnReceiptDefault;
}
/**
 * @param returnReceiptDefault The returnReceiptDefault to set.
 */
public void setReturnReceiptDefault(boolean returnReceiptDefault) {
	ReturnReceiptDefault = returnReceiptDefault;
}
/**
 * @return Returns the selectPool.
 */
public PoolInfo getSelectPool() {
	return selectPool;
}
/**
 * @param selectPool The selectPool to set.
 */
public void setSelectPool(PoolInfo selectPool) {
	this.selectPool = selectPool;
}
/**
 * @return Returns the sendNotificationDefault.
 */
public boolean isSendNotificationDefault() {
	return SendNotificationDefault;
}
/**
 * @param sendNotificationDefault The sendNotificationDefault to set.
 */
public void setSendNotificationDefault(boolean sendNotificationDefault) {
	SendNotificationDefault = sendNotificationDefault;
}
/**
 * @return Returns the sentFileBean.
 */
public DboxFileInfo getSentFileBean() {
	return sentFileBean;
}
/**
 * @param sentFileBean The sentFileBean to set.
 */
public void setSentFileBean(DboxFileInfo sentFileBean) {
	this.sentFileBean = sentFileBean;
}
/**
 * @return Returns the sentPackBean.
 */
public DboxPackageInfo getSentPackBean() {
	return sentPackBean;
}
/**
 * @param sentPackBean The sentPackBean to set.
 */
public void setSentPackBean(DboxPackageInfo sentPackBean) {
	this.sentPackBean = sentPackBean;
}
/**
 * @param startTime The startTime to set.
 */
public void setStartTime(Date startTime) {
	this.startTime = startTime;
}
	   	/**
		  * Method retreiveInboxContents updates the inboxVec vector for desired package. This info is
		  * available in the PackageInfo object.
		  * 
		  */
		  public int retreiveInboxContents(WebDboxInboxInfo inbxInfo) throws Exception {
		
			 
			
			 Enumeration inboxEnum=null;
			 String temp = null;
			 long size =0;
			 Vector pkgVect=inbxInfo.getInboxPackages();
			 
			 
			 try {
			
				inboxEnum=listInOutSandBox(UserDropbox.INBOX_N);
			
			
				while(inboxEnum.hasMoreElements())
				{
				  
				   DboxPackageInfo packInfo = new DboxPackageInfo();	
				   PackageInfo pinfo=(PackageInfo)inboxEnum.nextElement();
			
				   long pkgID=pinfo.getPackageId();  
				   temp=String.valueOf(pkgID);
				   packInfo.setPackageId(temp);
				   
				   Vector fileCont=(Vector)(queryPackageContentsForpkgId(pkgID));
				   
				   
			       packInfo.setPackageName(pinfo.getPackageName());
				   
				   size=(long)(pinfo.getPackageSize()); // /(long)1024);
				   packInfo.setPackageSize(new Long(pinfo.getPackageSize()).toString());				   
			   
				   packInfo.setPackageOwner(pinfo.getPackageOwner());	// store the package owner   
				   packInfo.setPackageCompany(pinfo.getPackageCompany());	// store the package company  
				    
				    
				   java.text.SimpleDateFormat formatterExpDate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
				   Date expDate = new Date(pinfo.getPackageExpiration());
				  
				   packInfo.setPackageExpiration(expDate.toGMTString()); // store the package exp date in GMT
				  
				   
				   java.text.SimpleDateFormat formatCommitDate= new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
				   Date commDate = new Date(pinfo.getPackageCommitted());
				   			
				   packInfo.setPackageCommitted(commDate.toGMTString()); // store the package commit date in GMT
				 	
				   packInfo.setPkgDesc(pinfo.getPackageDescription());
				   
				   packInfo.setFileContents(fileCont);
				   
				   				   
				   if (packInfo.getPackageHidden() == false )
				   pkgVect.add(packInfo);
				
				
			 
				}		
				
			 } 
			 catch (Exception e) 
			 {
				
				//e.printStackTrace();
				throw e;
			 }
		
			
			return pkgVect.size();
			
		  }

		
	
		public Vector queryPackageContentsForpkgId(long pkgID) {
		
				
			   
			   Vector fileVect = new Vector();  //Vector of un-sorted DboxFileInfo objects
			
			   Enumeration dispEnum=null;			  
			   String temp = null;
			   long size =0;
		
			   try {
			
				   dispEnum=listPackageContents(pkgID);
			
				  while(dispEnum.hasMoreElements())
				  {
				  		
					 DboxFileInfo fileObj = new DboxFileInfo(); 	
					 FileInfo finfo=(FileInfo)dispEnum.nextElement();		
				
					 long fileID=finfo.getFileId();  
					 temp=String.valueOf(fileID).trim();
					 fileObj.setFileId(temp);
			   
					
					 fileObj.setFileName(finfo.getFileName()); //Store the File name
					 
					 size=(long)(finfo.getFileSize()); // /(long)1024); 
					
					 fileObj.setFileSize((new Long(size)).toString()); // store the File Size  
					           
					          
					 fileObj.setFileMD5(finfo.getFileMD5()); // store the File MD5    
				  	
					 fileVect.addElement(fileObj);
				 
			 
				  }		
				   
				
			   } 
			   catch (Exception e) 
			   {
				 
				  //e.printStackTrace();
			   }
		
			  return fileVect;
		}
		
		
		
		public void sortPkgInfo(byte sortOnCol, byte sortOrder, WebDboxInboxInfo inbxInfo) {
	
			   Vector pkgVect = inbxInfo.getInboxPackages();  //Vector of un-sorted DboxPackageInfo objects
			   Vector sortedPkgVect = inbxInfo.getSortedPackages(); //Vector to store sorted DboxPackageInfo objects
			   Vector toSort = new Vector(); 
			   
				try
				{ 
						   
						   Enumeration eInfo = pkgVect.elements(); //DboxPackageInfo types
						   Enumeration sortedSizeEnum = null;
							 
						   
						   while( eInfo.hasMoreElements()) {
						   	
								DboxPackageInfo pinfo = (DboxPackageInfo)(eInfo.nextElement());
								String idStr=pinfo.getPackageId().trim();
							
						   		
						  
					
								switch(sortOnCol)
								{
									case ETSComparator.SORT_BY_NAME :  
																		break;
											
									case ETSComparator.SORT_BY_SIZE : 
																		break;
																		
									case ETSComparator.SORT_BY_OWNER : 
																		break;
																		
									case ETSComparator.SORT_BY_COMPANY : 
																		break;
																		
									case ETSComparator.SORT_BY_DATE : 
																		break;
																		
									case ETSComparator.SORT_BY_DATE_COMMIT : 
																			String dateCommitStr = (String)(pinfo.getPackageCommitted().trim());
																			long dateCommLongVal = new java.util.Date().parse(dateCommitStr);
																			toSort.addElement(	Long.toString(dateCommLongVal)+":"+idStr.toString());
																 
																		    break;
									
								
								}
					
						   }
					
										Collections.sort( toSort,new ETSComparator(sortOnCol,sortOrder) );
									   
										sortedSizeEnum = toSort.elements();
						
						
										while ( sortedSizeEnum.hasMoreElements())   
										{
										   String elmnt=(String)sortedSizeEnum.nextElement();
										   
						 
										   String sortedPkgID = getSortedPkgID(elmnt);
										   String sortedItem = getSortedItem(elmnt);
							
										   						 
											if (pkgVect.size() > 0) {
												 Enumeration enum = pkgVect.elements();
												 while(enum.hasMoreElements()) {
													DboxPackageInfo finfo = (DboxPackageInfo)enum.nextElement();
													if (finfo.getPackageId().equals(sortedPkgID)) {
														sortedPkgVect.addElement(finfo);
													   break;
													}
												 }
											  }
											 
											 
											 
								
							
										}
			
										
									inbxInfo.setSortedPackages(sortedPkgVect); //Vector of sorted DboxPackageInfo objects
					}catch(Exception e)
					{
			
					   
					   //e.printStackTrace(); 
					 
					}
			
					
			
			
		
		
		
		
	
	}


	/**
		  * Method retreiveTrashContents updates the trashVec vector for desired package. This info is
		  * available in the PackageInfo object.
		  * 
		  */
		  public int retreiveTrashContents(WebDboxTrashInfo trashInfo) throws Exception {
		
			 
			
			 Enumeration trashEnum=null;
			 String temp = null;
			 long size =0;
			 Vector pkgVect=trashInfo.getTrashPackages();
			 
			 
			 try {
			
				trashEnum=listInOutSandBox(UserDropbox.TRASH_N);
				
			
				while(trashEnum.hasMoreElements())
				{
				  
				   DboxPackageInfo packInfo = new DboxPackageInfo();	
				   PackageInfo pinfo=(PackageInfo)trashEnum.nextElement();
			
				   long pkgID=pinfo.getPackageId();  
				   temp=String.valueOf(pkgID);
				   packInfo.setPackageId(temp);
				   
				   Vector fileCont=(Vector)(queryPackageContentsForpkgId(pkgID));
				   
				   
				   packInfo.setPackageName(pinfo.getPackageName());
				   
				   size=(long)(pinfo.getPackageSize()); // /(long)1024);
				   packInfo.setPackageSize(new Long(pinfo.getPackageSize()).toString());				   
			   
				   packInfo.setPackageOwner(pinfo.getPackageOwner());	// store the package owner   
				   packInfo.setPackageCompany(pinfo.getPackageCompany());	// store the package company  
				    
				    
				   java.text.SimpleDateFormat formatterExpDate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
				   Date expDate = new Date(pinfo.getPackageExpiration());
				  
				   packInfo.setPackageExpiration(expDate.toGMTString()); // store the package exp date in GMT
				  
				   
				   java.text.SimpleDateFormat formatCommitDate= new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
				   Date commDate = new Date(pinfo.getPackageCommitted());
				   			
				   packInfo.setPackageCommitted(commDate.toGMTString()); // store the package commit date in GMT
				   packInfo.setPkgDesc(pinfo.getPackageDescription());			   
				   packInfo.setFileContents(fileCont);
				   
				   				   
				   if (packInfo.getPackageHidden() == false )
				   pkgVect.add(packInfo);
				
				
			 
				}		
				
			 } 
			 catch (Exception e) 
			 {
				
				//e.printStackTrace();
				throw e;
			 }
		
			
			return pkgVect.size();
			
		  }


	public void sortTrashPkgInfo(byte sortOnCol, byte sortOrder, WebDboxTrashInfo trashInfo) {
	
				   Vector pkgVect = trashInfo.getTrashPackages();  //Vector of un-sorted DboxPackageInfo objects
				   Vector sortedPkgVect = trashInfo.getSortedPackages(); //Vector to store sorted DboxPackageInfo objects
				   Vector toSort = new Vector(); 
			   
					try
					{ 
						   
							   Enumeration eInfo = pkgVect.elements(); //DboxPackageInfo types
							   Enumeration sortedSizeEnum = null;
							 
						   
							   while( eInfo.hasMoreElements()) {
						   	
									DboxPackageInfo pinfo = (DboxPackageInfo)(eInfo.nextElement());
									String idStr=pinfo.getPackageId().trim();
							
						   		
						  
					
									switch(sortOnCol)
									{
										case ETSComparator.SORT_BY_NAME :  
																			break;
											
										case ETSComparator.SORT_BY_SIZE : 
																			break;
																		
										case ETSComparator.SORT_BY_OWNER : 
																			break;
																		
										case ETSComparator.SORT_BY_COMPANY : 
																			break;
																		
										case ETSComparator.SORT_BY_DATE : 
																			break;
																		
										case ETSComparator.SORT_BY_DATE_COMMIT : 
																				String dateCommitStr = (String)(pinfo.getPackageExpiration().trim());
																				long dateCommLongVal = new java.util.Date().parse(dateCommitStr);
																				toSort.addElement(	Long.toString(dateCommLongVal)+":"+idStr.toString());
																 
																				break;
									
								
									}
					
							   }
					
											Collections.sort( toSort,new ETSComparator(sortOnCol,sortOrder) );
									   
											sortedSizeEnum = toSort.elements();
						
						
											while ( sortedSizeEnum.hasMoreElements())   
											{
											   String elmnt=(String)sortedSizeEnum.nextElement();
										   
						 
											   String sortedPkgID = getSortedPkgID(elmnt);
											   String sortedItem = getSortedItem(elmnt);
							
										   						 
												if (pkgVect.size() > 0) {
													 Enumeration enum = pkgVect.elements();
													 while(enum.hasMoreElements()) {
														DboxPackageInfo finfo = (DboxPackageInfo)enum.nextElement();
														if (finfo.getPackageId().equals(sortedPkgID)) {
															sortedPkgVect.addElement(finfo);
														   break;
														}
													 }
												  }
											 
											 
											 
								
							
											}
			
										
						     trashInfo.setSortedPackages(sortedPkgVect); //Vector of sorted DboxPackageInfo objects
						}catch(Exception e)
						{
			
						   
						   //e.printStackTrace(); 
					 
						}
			
					
			
			
		
		
		
		
	
		}
   

	public void sortDraftPkgInfo(byte sortOnCol, byte sortOrder, WebDboxDraftPkgInfo draftInfo) {
	
					///need to provide a fix for this -- sorting on Package creation
					//we are getting creating a vector for sorting. vect toSort stores creation date+pkidstr
					//we then a sorted tab of pkgids
				   Vector pkgVect = draftInfo.getDraftPackages();  //Vector of un-sorted DboxPackageInfo objects
				   Vector sortedPkgVect = draftInfo.getSortedDraftPackages(); //Vector to store sorted DboxPackageInfo objects
				   Vector toSort = new Vector(); 
			   
					try
					{ 
						   
							   Enumeration eInfo = pkgVect.elements(); //DboxPackageInfo types
							   Enumeration sortedSizeEnum = null;
							 
						   
							   while( eInfo.hasMoreElements()) {
						   	
									DboxPackageInfo pinfo = (DboxPackageInfo)(eInfo.nextElement());
									String idStr=pinfo.getPackageId().trim();
							
						   		
						  
					
									switch(sortOnCol)
									{
										case ETSComparator.SORT_BY_NAME :  
																			break;
											
										case ETSComparator.SORT_BY_SIZE : 
																			break;
																		
										case ETSComparator.SORT_BY_OWNER : 
																			break;
																		
										case ETSComparator.SORT_BY_COMPANY : 
																			break;
																		
										case ETSComparator.SORT_BY_DATE : 
																			break;
																		
										case ETSComparator.SORT_BY_DATE_COMMIT : 
																				String dateCommitStr = (String)(pinfo.getPackageCreated().trim());
																				
																				long dateCommLongVal = new java.util.Date().parse(dateCommitStr);
																				toSort.addElement(	Long.toString(dateCommLongVal)+":"+idStr.toString());
																 
																				break;
									
								
									}
					
							   }
					
											Collections.sort( toSort,new ETSComparator(sortOnCol,sortOrder) );
										   
											sortedSizeEnum = toSort.elements();
						
						
											while ( sortedSizeEnum.hasMoreElements())   
											{
											   String elmnt=(String)sortedSizeEnum.nextElement();
											   
						 
											   String sortedPkgID = getSortedPkgID(elmnt);
											   String sortedItem = getSortedItem(elmnt);
							
											   						 
												if (pkgVect.size() > 0) {
													 Enumeration enum = pkgVect.elements();
													 while(enum.hasMoreElements()) {
														DboxPackageInfo finfo = (DboxPackageInfo)enum.nextElement();
														if (finfo.getPackageId().equals(sortedPkgID)) {
															sortedPkgVect.addElement(finfo);
														   break;
														}
													 }
												  }
											 
											 
											 
								
							
											}
			
										
						draftInfo.setSortedDraftPackages(sortedPkgVect); //Vector of sorted DboxPackageInfo objects
						}catch(Exception e)
						{
			
						   
						   //e.printStackTrace(); 
					 
						}
			
					
			
			
		
		
		
		
	
		}


	public void sortSentPkgInfo(byte sortOnCol, byte sortOrder, WebDboxSentPkgInfo sentInfo) {
	
						///need to provide a fix for this -- sorting on Package creation
						//we are getting creating a vector for sorting. vect toSort stores creation date+pkidstr
						//we then a sorted tab of pkgids
					   Vector pkgVect = sentInfo.getSentPackages();  //Vector of un-sorted DboxPackageInfo objects
					   Vector sortedPkgVect = sentInfo.getSortedSentPackages(); //Vector to store sorted DboxPackageInfo objects
					   Vector toSort = new Vector(); 
			   
						try
						{ 
						   
								   Enumeration eInfo = pkgVect.elements(); //DboxPackageInfo types
								   Enumeration sortedSizeEnum = null;
							 
						   
								   while( eInfo.hasMoreElements()) {
						   	
										DboxPackageInfo pinfo = (DboxPackageInfo)(eInfo.nextElement());
										String idStr=pinfo.getPackageId().trim();
							
						   		
						  
					
										switch(sortOnCol)
										{
											case ETSComparator.SORT_BY_NAME :  
											                                    toSort.addElement(	(String)(pinfo.getPackageName().trim())+":"+idStr.toString());  
																				break;
											
											case ETSComparator.SORT_BY_SIZE : 
																				break;
																		
											case ETSComparator.SORT_BY_OWNER : 
																				break;
																		
											case ETSComparator.SORT_BY_COMPANY : 
																				break;
																		
											case ETSComparator.SORT_BY_DATE : 
																				break;
																		
											case ETSComparator.SORT_BY_DATE_COMMIT :
																				
																					break;
									
								
										}
					
								   }
					
												Collections.sort( toSort,new ETSComparator(sortOnCol,sortOrder) );
										   
												sortedSizeEnum = toSort.elements();
						
						
												while ( sortedSizeEnum.hasMoreElements())   
												{
												   String elmnt=(String)sortedSizeEnum.nextElement();
											   
						 
												   String sortedPkgID = getSortedPkgID(elmnt);
												   String sortedItem = getSortedItem(elmnt);
							
											   						 
													if (pkgVect.size() > 0) {
														 Enumeration enum = pkgVect.elements();
														 while(enum.hasMoreElements()) {
															DboxPackageInfo finfo = (DboxPackageInfo)enum.nextElement();
															if (finfo.getPackageId().equals(sortedPkgID)) {
																sortedPkgVect.addElement(finfo);
															   break;
															}
														 }
													  }
											 
											 
											 
								
							
												}
			
										
							sentInfo.setSortedSentPackages(sortedPkgVect); //Vector of sorted DboxPackageInfo objects
							}catch(Exception e)
							{
			
							   
							   //e.printStackTrace(); 
					 
							}
			
					
			
			
		
		
		
		
	
}






			/**
			  * Method retreiveInboxContents updates the inboxVec vector for desired package. This info is
			  * available in the PackageInfo object.
			  * 
			  */
			  public int retreiveDraftContents(WebDboxDraftPkgInfo draftsInfo){
		
				 
			
				 Enumeration draftsEnum=null;
				 String temp = null;
				 long size =0;
				 Vector pkgVect=draftsInfo.getDraftPackages();
			 
			 
				 try {
			
					draftsEnum=listInOutSandBox(UserDropbox.DRAFTS_N);
			
					while(draftsEnum.hasMoreElements())
					{
				  
					   DboxPackageInfo packInfo = new DboxPackageInfo();	
					   PackageInfo pinfo=(PackageInfo)draftsEnum.nextElement();
			
					   long pkgID=pinfo.getPackageId();  
					   temp=String.valueOf(pkgID);
					   packInfo.setPackageId(temp);
				   
					   Vector fileCont=(Vector)(queryDraftPackageContentsForpkgId(pkgID,packInfo));
				   
				   
					   packInfo.setPackageName(pinfo.getPackageName());
				   
					   size=(long)(pinfo.getPackageSize()); // /(long)1024);
					   packInfo.setPackageSize(new Long(pinfo.getPackageSize()).toString());				   
			   
			   		   
			   		  // packInfo.setPackageState(new Byte( pinfo.getPackageStatus()).toString());
					  // usin a packInfo
					   packInfo.setPackageState( packInfo.getPackageState());
			   
					   packInfo.setPackageOwner(pinfo.getPackageOwner());	// store the package owner   
					   packInfo.setPackageCompany(pinfo.getPackageCompany());	// store the package company  
				    
				    
					   java.text.SimpleDateFormat formatterExpDate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
					   Date expDate = new Date(pinfo.getPackageExpiration());
				  
					   packInfo.setPackageExpiration(expDate.toGMTString()); // store the package exp date in GMT
				
				   
					   java.text.SimpleDateFormat formatCommitDate= new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
					   Date commDate = new Date(pinfo.getPackageCommitted());
				   			
					   packInfo.setPackageCommitted(commDate.toGMTString()); // store the package commit date in GMT
					   
					   java.text.SimpleDateFormat formatCreateDate= new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
						Date createDate = new Date(pinfo.getPackageCreation());
					
						packInfo.setPackageCreated(createDate.toGMTString()); // store the package commit date in GMT
					   
						packInfo.setPkgDesc(pinfo.getPackageDescription());
				   
					   packInfo.setFileContents(fileCont);
				   
					   pkgVect.add(packInfo);
					   
					   
					   packInfo.setPackageSendNotification(pinfo.getPackageSendNotification());
					   packInfo.setPackageReturnReceipt(pinfo.getPackageReturnReceipt());
					   
					
			 
					}		
				
				 } 
				 catch (Exception e) 
				 {
					
					//e.printStackTrace();
				 }
		
			
				return pkgVect.size();
			
			  }


	             /**
				  * Method retreiveInboxContents updates the inboxVec vector for desired package. This info is
				  * available in the PackageInfo object.
				  * 
				  */
				  public int retreiveSentContents(WebDboxSentPkgInfo sentInfo){
		
				 
			
					 Enumeration sentEnum=null;
					 String temp = null;
					 long size =0;
					 Vector pkgVect=sentInfo.getSentPackages();
			 
			 
					 try {
			
						sentEnum=listInOutSandBox(UserDropbox.SENT_N);
			
						while(sentEnum.hasMoreElements())
						{
				  
						   DboxPackageInfo packInfo = new DboxPackageInfo();	
						   PackageInfo pinfo=(PackageInfo)sentEnum.nextElement();
			
						   long pkgID=pinfo.getPackageId();  
						   temp=String.valueOf(pkgID);
						   packInfo.setPackageId(temp);
				   
						   Vector fileCont=(Vector)(querySentPackageContentsForpkgId(pkgID,packInfo));
				   
				   
						   packInfo.setPackageName(pinfo.getPackageName());
				   
						   size=(long)(pinfo.getPackageSize()); // /(long)1024);
						   packInfo.setPackageSize(new Long(pinfo.getPackageSize()).toString());				   
			   
			   		   
						  // packInfo.setPackageState(new Byte( pinfo.getPackageStatus()).toString());
						  // usin a packInfo
						   packInfo.setPackageState( packInfo.getPackageState());
			   
						   packInfo.setPackageOwner(pinfo.getPackageOwner());	// store the package owner   
						   packInfo.setPackageCompany(pinfo.getPackageCompany());	// store the package company  
				    
				    
						   java.text.SimpleDateFormat formatterExpDate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
						   Date expDate = new Date(pinfo.getPackageExpiration());
				  
						   packInfo.setPackageExpiration(expDate.toGMTString()); // store the package exp date in GMT
				
				   
						   java.text.SimpleDateFormat formatCommitDate= new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
						   Date commDate = new Date(pinfo.getPackageCommitted());
				   			
						   packInfo.setPackageCommitted(commDate.toGMTString()); // store the package commit date in GMT
					   
						   java.text.SimpleDateFormat formatCreateDate= new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
							Date createDate = new Date(pinfo.getPackageCreation());
					
							packInfo.setPackageCreated(createDate.toGMTString()); // store the package commit date in GMT
					   
							packInfo.setPkgDesc(pinfo.getPackageDescription());
						   packInfo.setFileContents(fileCont);
				   
						   pkgVect.add(packInfo);
					   
					   
						   packInfo.setPackageSendNotification(pinfo.getPackageSendNotification());
						   packInfo.setPackageReturnReceipt(pinfo.getPackageReturnReceipt());
					   
					
			 
						}		
				
					 } 
					 catch (Exception e) 
					 {
						
						//e.printStackTrace();
					 }
		
			
					return pkgVect.size();
			
				  }


	public Vector queryDraftPackageContentsForpkgId(long pkgID , DboxPackageInfo packInfo) {
		
				
			   
				   Vector fileVect = new Vector();  //Vector of un-sorted DboxFileInfo objects
			
				   Enumeration dispEnum=null;			  
				   String temp = null;
				   long size =0;
		
					 int complete = 0;
					 int incomplete = 0;
					 int none = 0;
				
		
				   try {
			
					   dispEnum=listPackageContents(pkgID);
			
					  while(dispEnum.hasMoreElements())
					  {
				  		
						 DboxFileInfo fileObj = new DboxFileInfo(); 	
						 FileInfo finfo=(FileInfo)dispEnum.nextElement();
						 
							 //need to check the status of files to update status of corresp pkg
							switch(finfo.getFileStatus()) {
								 case DropboxGenerator.STATUS_COMPLETE:
									complete++;
									break;
								 case DropboxGenerator.STATUS_NONE:
									none++;
									break;
								 case DropboxGenerator.STATUS_INCOMPLETE:
								 default:
									incomplete++;
									break;
						    }
						 
						 long fileID=finfo.getFileId();  
						 temp=String.valueOf(fileID).trim();
						 fileObj.setFileId(temp);
			   
					
						 fileObj.setFileName(finfo.getFileName()); //Store the File name
					 
						 size=(long)(finfo.getFileSize()); // /(long)1024); 
					
						 fileObj.setFileSize((new Long(size)).toString()); // store the File Size  
					           
					          
						 fileObj.setFileMD5(finfo.getFileMD5()); // store the File MD5    
				  	
						 fileVect.addElement(fileObj);
						 
				 
			 
					  }		
					  
						 if (incomplete != 0) {
								packInfo.setPackagestatus(DropboxGenerator.STATUS_FAIL);
								packInfo.setPackageState("File errors");
						 } else if (none != 0 || complete != 0) {
								packInfo.setPackagestatus(DropboxGenerator.STATUS_PARTIAL);
								packInfo.setPackageState("Ready to send");
						 } else {
								packInfo.setPackagestatus(DropboxGenerator.STATUS_NONE);
								packInfo.setPackageState("Empty");
								
							    packInfo.setPackageSize("0");
								
								
						 }
							 
						 
				   
				
				   } 
				   catch (Exception e) 
				   {
					 
					  //e.printStackTrace();
				   }
		
				  return fileVect;
			}

	private Vector querySentPackageContentsForpkgId(long pkgID , DboxPackageInfo packInfo) {
		
				
			   
					   Vector fileVect = new Vector();  //Vector of un-sorted DboxFileInfo objects
			
					   Enumeration dispEnum=null;			  
					   String temp = null;
					   long size =0;
		
						 int complete = 0;
						 int incomplete = 0;
						 int none = 0;
				
		
					   try {
			
						   dispEnum=listPackageContents(pkgID);
			
						  while(dispEnum.hasMoreElements())
						  {
				  		
							 DboxFileInfo fileObj = new DboxFileInfo(); 	
							 FileInfo finfo=(FileInfo)dispEnum.nextElement();
						 
								 //need to check the status of files to update status of corresp pkg
								switch(finfo.getFileStatus()) {
									 case DropboxGenerator.STATUS_COMPLETE:
										complete++;
										break;
									 case DropboxGenerator.STATUS_NONE:
										none++;
										break;
									 case DropboxGenerator.STATUS_INCOMPLETE:
									 default:
										incomplete++;
										break;
								}
						 
							 long fileID=finfo.getFileId();  
							 temp=String.valueOf(fileID).trim();
							 fileObj.setFileId(temp);
			   
					
							 fileObj.setFileName(finfo.getFileName()); //Store the File name
					 
							 size=(long)(finfo.getFileSize()); // /(long)1024); 
					
							 fileObj.setFileSize((new Long(size)).toString()); // store the File Size  
					           
					          
							 fileObj.setFileMD5(finfo.getFileMD5()); // store the File MD5    
				  	
							 fileVect.addElement(fileObj);
						 
				 
			 
						  }		
					  
							 if (incomplete != 0) {
									packInfo.setPackagestatus(DropboxGenerator.STATUS_FAIL);
									packInfo.setPackageState("File errors");
							 } else if (none != 0 || complete != 0) {
									packInfo.setPackagestatus(DropboxGenerator.STATUS_PARTIAL);
									packInfo.setPackageState("Ready to send");
							 } else {
									packInfo.setPackagestatus(DropboxGenerator.STATUS_NONE);
									packInfo.setPackageState("Empty");
							 }
							 
						 
				   
				
					   } 
					   catch (Exception e) 
					   {
						 
						  //e.printStackTrace();
					   }
		
					  return fileVect;
				}



	
	
	
			/**
			 * @param string
			 * @return
			 */
			public long getPkIdForSelectdPkg(String pkgName) {
		
				
				
				Enumeration enum=null;
				PackageInfo pi = null;
				long packId=0;
			
				try
				{
					//long packId=0;
					enum=listInOutSandBox(UserDropbox.INBOX_N);
					while(enum.hasMoreElements())
					{
						
						pi = (PackageInfo)enum.nextElement();
						String str=(pi.getPackageName()).toString().trim();
						
						if(str.equalsIgnoreCase(pkgName.trim()))
						 {  
						
							packId=(long)(pi.getPackageId()); 
							break;
						 } 
						
					}
					
				}catch (Exception e) {
				
				}
				
				
				return packId;
						 	
			   
		
				
			}
	
	
	
	
	public Hashtable retreivePackageContents(long pkgID,WebDboxInboxInfo inbxInfo){
		
			   Hashtable dispHash=new Hashtable();
			   Vector fileVect = inbxInfo.getInboxFileContents();  //Vector of un-sorted DboxFileInfo objects
			
			   Enumeration dispEnum=null;
			   Vector dispVect=null;
			   String temp = null;
			   long size =0;
		
			   try {
			
				   dispEnum=listPackageContents(pkgID);
			
				  while(dispEnum.hasMoreElements())
				  {
				  		
					 FileInfo finfo=(FileInfo)dispEnum.nextElement();
					 dispVect=new Vector();
				
				
					 long fileID=finfo.getFileId();  
					 temp=String.valueOf(fileID).trim();
			   
					 dispVect.add(finfo.getFileName());   			//Store the File name
					 size=(long)(finfo.getFileSize()); // /(long)1024); 
			   
					 dispVect.add(new Long(size));                   // store the File size           
					 dispVect.add(finfo.getFileMD5());              // store the File MD5    
				  
				
					 dispHash.put(temp,dispVect);                      //store the vector along with fileID in a Hashtable in fmt "pkgID" "name,size,owner,company,expdate,commdate"
			   
				 
			 
				  }		
				   
				
			   } 
			   catch (Exception e) 
			   {
				  //e.printStackTrace();
			   }
		
			  return dispHash;
			}



  	

  /**
   * Method getSortedItem returns the attribute portion such as packagename,sizes etc  from the string 
   * @param elmnt
   * @return String
   */
   private String getSortedItem(String elmnt) {
		
      return elmnt.substring(0,elmnt.lastIndexOf(':'));
   }


  /**
   * Method getSortedPkgID returns the ID portion from the string 
   * @param elmnt
   * @return String
   */
   private String getSortedPkgID(String elmnt) {
      return elmnt.substring(elmnt.indexOf(':')+1,elmnt.length());
   }


/**
 * @return
 */
public boolean isFirstTime() {
	return firstTime;
}

/**
 * @param b
 */
public void setFirstTime(boolean b) {
	firstTime = b;
}

/**
 * @return
 */
public boolean isTrashFirstTime() {
	return trashFirstTime;
}

/**
 * @param b
 */
public void setTrashFirstTime(boolean b) {
	// TODO Auto-generated method stub
	trashFirstTime = b;
}





/**
 * @return
 */
public boolean isDraftFirstTime() {
	return draftFirstTime;
}

/**
 * @param b
 */
public void setDraftFirstTime(boolean b) {
	draftFirstTime = b;
}


public void setInboxInfoBean(WebDboxInboxInfo inbxInfo) {
	
	this.inboxInfoBean=inbxInfo;
}

public WebDboxInboxInfo getInboxInfoBean() {
	
	return this.inboxInfoBean;
}

/**
 * @return
 */
public DboxPackageInfo getInboxPackBean() {
	return inboxPackBean;
}

/**
 * @param info
 */
public void setInboxPackBean(DboxPackageInfo info) {
	inboxPackBean = info;
}

/**
 * @return
 */
public DboxFileInfo getInboxFileBean() {
	return inboxFileBean;
}

/**
 * @param info
 */
public void setInboxFileBean(DboxFileInfo info) {
	inboxFileBean = info;
}

public void setTrashInfoBean(WebDboxTrashInfo trashInfo) {
	
	this.trashInfoBean=trashInfo;
}

public WebDboxTrashInfo getTrashInfoBean() {
	
	return this.trashInfoBean;
}

/**
 * @return
 */
public DboxPackageInfo getTrashPackBean() {
	return trashPackBean;
}

/**
 * @param info
 */
public void setTrashPackBean(DboxPackageInfo info) {
	trashPackBean = info;
}

/**
 * @return
 */
public DboxFileInfo getTrashFileBean() {
	return trashFileBean;
}

/**
 * @param info
 */
public void setTrashFileBean(DboxFileInfo info) {
	trashFileBean = info;
}






public void setDraftInfoBean(WebDboxDraftPkgInfo dftInfo) {
	
	this.draftInfoBean=dftInfo;
}

public WebDboxDraftPkgInfo getDraftInfoBean() {
	
	return this.draftInfoBean;
}

/**
 * @return
 */
public DboxPackageInfo getDraftPackBean() {
	return draftPackBean;
}

/**
 * @param info
 */
public void setDraftPackBean(DboxPackageInfo info) {
	draftPackBean = info;
}

/**
 * @return
 */
public DboxFileInfo getDraftFileBean() {
	return draftFileBean;
}

/**
 * @param info
 */
public void setDraftFileBean(DboxFileInfo info) {
	draftFileBean = info;
}










/**
 * @return
 */
public WebDboxCreateDraftPkgInfo getCreatDftInfoBean() {
	return creatDftInfoBean;
}

/**
 * @param info
 */
public void setCreatDftInfoBean(WebDboxCreateDraftPkgInfo info) {
	creatDftInfoBean = info;
}

/**
 * @return
 */
public boolean isCreateDraftFirstTime() {
	return createDraftFirstTime;
}

/**
 * @param b
 */
public void setCreateDraftFirstTime(boolean b) {
	createDraftFirstTime = b;
}

/**
 * @param pkid
 */
public void deleteAclsForPackage(long pkid) {
	
	try {
		
		Enumeration enumAcls = queryAcls(pkid);
		while(enumAcls.hasMoreElements())
		removeAcl(pkid,(String)(enumAcls.nextElement()));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}

/**
 * @return
 */
public WebDboxCreateGroupInfo getCreateGroupInfoBean() {
	return createGroupInfoBean;
}

/**
 * @param info
 */
public void setCreateGroupInfoBean(WebDboxCreateGroupInfo info) {
	createGroupInfoBean = info;
}



/**
 * @return
 */
public WebDboxDispGroupInfo getGroupInfoBean() {
	return groupInfoBean;
}

/**
 * @param info
 */
public void setGroupInfoBean(WebDboxDispGroupInfo info) {
	groupInfoBean = info;
}



/**
 * @return
 */
public boolean isCreateGroupsFirstTime() {
	return createGroupsFirstTime;
}

/**
 * @param b
 */
public void setCreateGroupsFirstTime(boolean b) {
	createGroupsFirstTime = b;
}

/**
 * @return
 */
public boolean isSentFirstTime() {
	return sentFirstTime;
}

/**
 * @param b
 */
public void setSentFirstTime(boolean b) {
	sentFirstTime = b;
}

/**
 * @return
 */
public WebDboxSentPkgInfo getSentInfoBean() {
	return sentInfoBean;
}

/**
 * @param info
 */
public void setSentInfoBean(WebDboxSentPkgInfo info) {
	sentInfoBean = info;
}

/**
 * @return
 */
public WebDboxCreateSentPkgInfo getCreatSntInfoBean() {
	return creatSntInfoBean;
}

/**
 * @param info
 */
public void setCreatSntInfoBean(WebDboxCreateSentPkgInfo info) {
	creatSntInfoBean = info;
}

/**
 * @return
 */
public boolean isCreateSentFirstTime() {
	return createSentFirstTime;
}

/**
 * @param b
 */
public void setCreateSentFirstTime(boolean b) {
	createSentFirstTime = b;
}


/**
 * @param pkid
 */
public void deleteAclsForGroup(String groupname) {
	
	try {
		
		GroupInfo ginfo = listGroup(groupname);
		Vector memForGrp=(Vector)(ginfo.getGroupMembers());
									
		Enumeration members=(Enumeration)(memForGrp.elements());
		while(members.hasMoreElements()){
			removeGroupMemberAccess(groupname,((String)(members.nextElement())).trim().toLowerCase(),true); 
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}

public Vector deleteEditorsForGroup(String groupname,String currentUserAsEditor,String[] editorsToAdd) {

	Vector toBeDel=new Vector();
	
	try {
		
		GroupInfo ginfo = listGroup(groupname);
		Vector memForGrp=(Vector)(ginfo.getGroupAccess());
		toBeDel=(Vector)(listGroup(groupname).getGroupAccess());
		
		
		
		for(int i=1;i<editorsToAdd.length;i++)
		{
			
			if (toBeDel.contains(editorsToAdd[i].substring(3))){
				
				toBeDel.remove(editorsToAdd[i].substring(3));
			}
			
		}
		
									
		Enumeration editors=(Enumeration)(memForGrp.elements());
		while(editors.hasMoreElements()){
			
			String str=((String)(editors.nextElement())).trim().toLowerCase();
			
			//dont del current editor who is also logged on user
			if (!currentUserAsEditor.equalsIgnoreCase(str))
			{
				removeGroupMemberAccess(groupname,str,false);
		  	    
		    }
		}
		
	} catch (Exception e) {
		
		//e.printStackTrace();
	}
	return toBeDel;
	
	
}

public String mmddyyyy(String dateString) {
  	
  		 
  			
	     Date d = new java.util.Date(dateString);
		 Calendar sc = Calendar.getInstance();
		 sc.setTime(d);
		 int yr  = sc.get(Calendar.YEAR);
		 int mon = sc.get(Calendar.MONTH)+1;
		 int day = sc.get(Calendar.DAY_OF_MONTH);
		 int hr  = sc.get(Calendar.HOUR_OF_DAY);
		 int min = sc.get(Calendar.MINUTE);
		 int sec = sc.get(Calendar.SECOND);
  
		 String scS1=((mon < 10)?"0":"")+ mon + ((day < 10)?"/0":"/")+day + "/"+yr;
	
	 
	 	
		 return scS1;
	  }
private DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
boolean crossCompEdit=false;
/**
 * @param poolMaxDays
 * @param poolDefaultDays
 * @return
 */
public String prepareExpiration(int poolMaxDays, int poolDefaultDays) {
	
//	 Get the current time.
	Date now = new Date(System.currentTimeMillis());
	//now=new Date(now.getYear()-1900, now.getMonth()-1, now.getDate()); //timeanddate.com

	// Create a calendar.
	GregorianCalendar gc = new GregorianCalendar();
	gc.setTime(now);
	gc.add(Calendar.DAY_OF_MONTH,poolMaxDays);
	
	java.text.SimpleDateFormat formatterExpDate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
	Date dt = gc.getTime();
	
	
	//String fmtdDateStr= df.format(dt);
	String fmtdDateStr= mmddyyyy(dt.toGMTString());
	
	 return fmtdDateStr;
	
}
/**
 * @param b
 */
public void setCrossCompEdit(boolean b) {
	// TODO Auto-generated method stub
	crossCompEdit=b;
}






/**
 * @return Returns the crossCompEdit.
 */
public boolean isCrossCompEdit() {
	return crossCompEdit;
}
}

