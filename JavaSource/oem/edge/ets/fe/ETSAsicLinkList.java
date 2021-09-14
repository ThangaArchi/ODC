/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */
package oem.edge.ets.fe;

public class ETSAsicLinkList{
	
	static private final ETSAsicLinkListValue[] ieList = {
		new ETSAsicLinkListValue("ASIC Connect Main Page", "","AsicLandPageServlet.wss?linkid=110000&pghead=ASIC+Connect&pgtitle=ASIC+Connect",false,"",false),
		new ETSAsicLinkListValue("Libraries and Toolkits","","EdesignMainMenuServlet.wss?linkid=111000&sub_func=products",true,"111000",false),
		new ETSAsicLinkListValue("ASIC Documentation","","tgcm/TGCMServlet.wss?alias=newASICdocumentation&linkid=112000&cat_jsp=asicdoc.jsp",true,"112000",false),
		new ETSAsicLinkListValue("PDTAT","","PdtatMainServlet.wss?linkid=1L0000",true,"1L0000",true),
		new ETSAsicLinkListValue("Die Size Estimator","","EdesignDieSizerOrderServlet.wss?sub_func=products&linkid=111000&role=EDSGN_FSE",true,"FS0004",true),
		new ETSAsicLinkListValue("Pricing Tool","","ptool/PtoolMainMenuServlet.wss?linkid=190000&skipappln=Y",true,"190000",false),
		new ETSAsicLinkListValue("Manage client entitlements","","DecafUserPocScopeSetup.wss?sub_func=uadmin&opn=mul&linkid=175100",true,"175100",true)				        
	};
	
	static private final ETSAsicLinkListValue[] drList = {
		new ETSAsicLinkListValue("Dropbox", "","ets/ETSServicesServlet.wss?servicesop=7",false,"",false),
		//removed new ETSAsicLinkListValue("Dropbox reporting","","ets/ETSServicesServlet.wss?op=7&sc=webox:op:r:q",false,"",false),
		new ETSAsicLinkListValue("Web conferences","","ets/ETSServicesServlet.wss?servicesop=5",false,"",false),
		//new ETSAsicLinkListValue("Design Kit Inbox","","EdesignInboxServlet.wss?sub_func=account1&linkid=11B000",false,"",false),
		new ETSAsicLinkListValue("My Preferences","","EdesignProfileServlet.wss?sub_func=account1&linkid=11B000",false,"",false),
		new ETSAsicLinkListValue("IBM Engineering Contacts","","EdesignFSEInfoServlet.wss?sub_func=account1&linkid=11B000",true,"111000",false),
		//new ETSAsicLinkListValue("ASIC Projects","","EdesignMyProjectsServlet.wss?sub_func=account1&linkid=11B000",true,"111000",false),
		new ETSAsicLinkListValue("Customer DK Administration","","EdesignFSEAdminServlet.wss?sub_func=admin&linkid=118000",true,"118000",true),
		new ETSAsicLinkListValue("ASIC Help and Support","","tgcm/TGCMServlet.wss?alias=helpandsupport&linkid=1R0000",false,"",false)
						        
	};
	
	static private final ETSAsicLinkListValue[] ppList = {
		new ETSAsicLinkListValue("WIP tracking","","DecafGenReport.wss?linkid=150000&FUNC=WIP",true,"150000",false),
		new ETSAsicLinkListValue("Order management","","ordmgt/OrderStatLaunch.wss?OP=OsQry&oper=STATUS&linkid=133000",true,"133000",false),
		//new ETSAsicLinkListValue("Yield management","","",false,""),
		new ETSAsicLinkListValue("Lot parentage","","jsp/parentage/ParentageReq.jsp?linkid=1J0000",true,"1J0000",false),
		new ETSAsicLinkListValue("Product change notification","","ProdcnMainMenuServlet.wss?param1=1&param2=2&linkid=160000",true,"160000",false),				        
		new ETSAsicLinkListValue("MD quality information","","tgcm/TGCMServlet.wss?alias=Quality&linkid=1Q0000",false,"",false)
	};
	
	static private final ETSAsicLinkListValue[] grList = {
		new ETSAsicLinkListValue("What's new and user tips","","tgcm/TGCMServlet.wss?alias=Whatsnewusertips&linkid=117000",false,"",false),
		new ETSAsicLinkListValue("Education","","tgcm/TGCMServlet.wss?alias=ASICEDAeducation&linkid=115000",false,"",false),
		//new ETSAsicLinkListValue("Teamroom","","",false),
		new ETSAsicLinkListValue("Technical library","","tgcm/TGCMServlet.wss?alias=Technicallibrary&linkid=1G0000",false,"",false),
		//new ETSAsicLinkListValue("More about ASIC Connect","","",false)				        
	};
	
	static public String getDescription(int index,ETSAsicLinkListValue[] list){
		return list[index].getDescription();
	}
	
	static public String getTitle(int index,ETSAsicLinkListValue[] list){
		return list[index].getTitle();
	}
	
	static public String getLink(int index,ETSAsicLinkListValue[] list){
		return list[index].getLink();
	}
	
	
	static public boolean isIbmOnly(int index,ETSAsicLinkListValue[] list){
		return list[index].isRestriced();
	}

	static public String getLinkId(int index,ETSAsicLinkListValue[] list){
		return list[index].getLinkId();
	}
	
	
	
	static public int getCount(ETSAsicLinkListValue[] list){
		return list.length;
	}
	
	static public ETSAsicLinkListValue[] getInitEngagementList(){
		return ieList;
	}
    
	static public ETSAsicLinkListValue[] getDesignReleaseList(){
		return drList;
	}

	static public ETSAsicLinkListValue[] getPrototypeProductionList(){
		return ppList;
	}

	static public ETSAsicLinkListValue[] getGeneralResourcesList(){
		return grList;
	}



}

/*	static private final ETSAsicLinkListValue[] ieList = {
		new ETSAsicLinkListValue("ASIC Connect Main Page", "","",false),
		new ETSAsicLinkListValue("Libraries and Toolkits","ASIC Design Kit, RAM/RA","",false),
		new ETSAsicLinkListValue("ASIC Documentation","ASIC Databooks, app notes, etc","",false),
		new ETSAsicLinkListValue("PDTAT","Physical Design Turn Around Tool","",true),
		new ETSAsicLinkListValue("Dis Size Estimator","","",true),
		new ETSAsicLinkListValue("Pricing Tool","Budgetary Pricing","",true),
		new ETSAsicLinkListValue("Customer Administration","Manage client entitlements","",true)				        
	};
	
	static private final ETSAsicLinkListValue[] drList = {
		new ETSAsicLinkListValue("Dropbox", "Securely exchange files and data","",false),
		new ETSAsicLinkListValue("Web conferences","Real time screen sharing","",false),
		new ETSAsicLinkListValue("ASIC Help and Support","ASIC Technical Support and docs","",false),
		new ETSAsicLinkListValue("Design Kit Inbox","Download previous DK orders","",false),
		new ETSAsicLinkListValue("My Preferences","","",false),
		new ETSAsicLinkListValue("IBM Engineering Contacts","ASIC Support Personnel","",false),
		new ETSAsicLinkListValue("ASIC Projects","Lists entitled ASIC Projects","",false)
						        
	};
	
	static private final ETSAsicLinkListValue[] ppList = {
		new ETSAsicLinkListValue("WIP tracking","Manufacturing Work In Progress","",false),
		new ETSAsicLinkListValue("Order management","Supply Chain information","",false),
		new ETSAsicLinkListValue("Yield management","Testing and yield data for wafers","",false),
		new ETSAsicLinkListValue("Lot percentage","Relationships of lot identifiers","",false),
		new ETSAsicLinkListValue("Product change notification","","",false)				        
	};
	
	static private final ETSAsicLinkListValue[] grList = {
		new ETSAsicLinkListValue("What's new and user tips","","",false),
		new ETSAsicLinkListValue("Education","Enroll, Descriptions, schedules","",false),
		//new ETSAsicLinkListValue("Teamroom","","",false),
		new ETSAsicLinkListValue("MD quality information","Data, reports, and standards","",false),
		new ETSAsicLinkListValue("Technical library","Product marketing information","",false),
		//new ETSAsicLinkListValue("More about ASIC Connect","","",false)				        
	};*/

