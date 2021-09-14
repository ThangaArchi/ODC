/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.helpers;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ismgt.bdlg.EtsDropDownDataPrepBean;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssUserRoleFilter;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterResource;
import oem.edge.ets.fe.ismgt.resources.EtsPcrResource;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssActionObjKeyPrep implements EtsIssFilterConstants {

	public static final String VERSION = "1.37";

	EdgeAccessCntrl es = null;
	Hashtable params = null;
	ETSProj proj = null;
	int topCatId = 0;
	String sLink = null;
	Hashtable actionMap = null;
	HttpServletRequest request = null;
	HttpServletResponse response = null;

	/**
	 * 
	 */
	
	public EtsIssActionObjKeyPrep(Hashtable Params, ETSProj Proj, int TopCatId, EdgeAccessCntrl Es, String Linkid, HttpServletRequest Request, HttpServletResponse Response) {

		super();
		this.params = Params;
		this.proj = Proj;
		this.topCatId = TopCatId;
		this.es = Es;
		this.sLink = Linkid;
		initActionMap();
		this.request = Request;
		this.response = Response;

	}

	/**
		 *  get action map for each  of the issue/change
		 * 
		 */

	private void initActionMap() {

		actionMap = new Hashtable();

		actionMap.put("submitIssue", "1");
		actionMap.put("modifyIssue", "2");
		actionMap.put("resolveIssue", "3");
		actionMap.put("viewIssue", "4");
		actionMap.put("rejectIssue", "5");
		actionMap.put("closeIssue", "6");
		actionMap.put("submitChange", "11");
		actionMap.put("modifyChange", "12");
		actionMap.put("resolveChange", "13");
		actionMap.put("viewChange", "14");
		actionMap.put("rejectChange", "15");
		actionMap.put("closeChange", "16");
		actionMap.put("acceptChange", "17");
		actionMap.put("feedback", "18");
		actionMap.put("commentIssue", "19");
		actionMap.put("commentChange", "20");
		actionMap.put("reqNewIssTyp","21");
		actionMap.put("chgOwner","22");
		actionMap.put("withDrwIssue","23");
		actionMap.put("addIssType","24");
		actionMap.put("delIssType","25");
		actionMap.put("updIssType","26");
		actionMap.put("subsIssType","27");
		actionMap.put("subscrIssue","28");
		actionMap.put("unSubscrIssue","29");
		actionMap.put("searchByNum","30");
		actionMap.put("addCustField","31");
		actionMap.put("removeCustField","32");
		actionMap.put("updateCustField","33");

	}

	//	details
	//0 = prob_class
	//1 = prob_severity
	//2 = prob_title
	//3 = prob_type
	//4 = prob_desc

	/**
	 * 
	 * get check submit value
	 */

	public String checkSubmitValue() {
		String value = null;
		Enumeration keys = params.keys();
		while (keys.hasMoreElements()) {
			String name = (String) keys.nextElement();
			System.out.println("name=====" + name + "   value====" + params.get(name) );
			
			if (name.indexOf("delete") == 0 && (name.indexOf(".") > 0)) {
				String posXorY = (String) params.get(name);
				System.out.println("posXorY=====" + posXorY);
				int pos = 0;
				if (posXorY != null || posXorY != "")
					pos = Integer.valueOf(posXorY).intValue();
				if (pos > 0) {
					value = name.substring(0, name.indexOf("."));
					break;
				}
			}
		}
		return value;
	}

	/**
		 * get action key 
		 */

	private int getactionKey(String actiontype) {
		int actionKey = 0;
		try {
			if (actiontype == null);
			else
				actionKey = Integer.valueOf((String) actionMap.get(actiontype)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return actionKey;

	}

	/**
		 * get Issue Access
		 */

	private String getIssueAccess(EdgeAccessCntrl es) {

		String userType = es.gDECAFTYPE;

		if (userType.equals("I")) {

			return "IBM";
		}

		return "ALL";

	}

	/**
		 * get Issue class
		 */

	private String getIssueClass(String actionType) {

		if (actionType.equals("submitIssue") || actionType.equals("modifyIssue")) {

			return ETSISSUESUBTYPE;

		}

		if (actionType.equals("submitChange") || actionType.equals("modifyChange")) {

			return ETSCHANGESUBTYPE;

		}

		return ETSISSUESUBTYPE;

	}

	/**
		 * get parameter value 
		 */

	private String getParameterValue(String paramName) {
		String value = null;
		try {
			params.get(paramName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public EtsIssObjectKey getEtsIssActionObjKey() throws SQLException, Exception {

		System.out.println("inside processing Request");
		String actiontype = null;
		String viewType = null;

		try {
			actiontype = AmtCommonUtils.getTrimStr((String) params.get("actionType"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		String subactiontype = null;
		String submitvalue = null;
		try {
			subactiontype = AmtCommonUtils.getTrimStr((String) params.get("subactionType"));
			System.out.println("subaction ---- ---- " + subactiontype);

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			viewType = AmtCommonUtils.getTrimStr((String) params.get("viewType"));
			System.out.println("viewtype");

		} catch (Exception e) {
			e.printStackTrace();
		}

		String submitstring = checkSubmitValue();
		int filenum = 0;
		if (submitstring != null) {
			try {
				if (submitstring.indexOf("delete") == 0) {

					submitvalue = "delete";
					String num = submitstring.substring(6, submitstring.length());
					if (num != "")
						filenum = Integer.valueOf(num).intValue();

				} else if (submitstring.indexOf("download") == 0) {

					submitvalue = "download";
					String num = submitstring.substring(8, submitstring.length());
					if (num != "")
						filenum = Integer.valueOf(num).intValue();

				} else if (submitstring.indexOf("view") == 0) {

					submitvalue = "view";
					String num = submitstring.substring(4, submitstring.length());
					if (num != "")
						filenum = Integer.valueOf(num).intValue();

				}

				System.out.println(submitvalue);
				System.out.println(filenum);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			submitvalue = "";

		if (viewType == null || viewType.equalsIgnoreCase("")) {

			viewType = "Open";

		}

		int actionkey = getactionKey(actiontype);
		System.out.println("actiontype ---- " + actiontype + "  actionkey  ----- " + actionkey);

		//set the basic params for the object
		EtsIssObjectKey etsIssObjKey = new EtsIssObjectKey();

		etsIssObjKey.setActionkey(actionkey);
		etsIssObjKey.setEs(es);
		etsIssObjKey.setFilenum(filenum);
		etsIssObjKey.setParams(params);
		etsIssObjKey.setProj(proj);
		etsIssObjKey.setRequest(request);
		etsIssObjKey.setResponse(response);
		etsIssObjKey.setSLink(sLink);
		etsIssObjKey.setSubActionType(subactiontype);
		etsIssObjKey.setSubmitValue(submitvalue);
		etsIssObjKey.setTopCatId(topCatId);
		etsIssObjKey.setIssueClass(getIssueClass(actiontype));
		etsIssObjKey.setIssueAccess(getIssueAccess(es));
		
//		set the properties in key obj in form of HaspMap//
		HashMap issPropMap = EtsIssFilterResource.getInstance().getFilterPropMap();
		etsIssObjKey.setPropMap(issPropMap);
		
		
//		set the PCR properties in key obj in form of HaspMap//
		HashMap pcrPropMap = EtsPcrResource.getInstance().getPcrPropMap();
		etsIssObjKey.setPcrPropMap(pcrPropMap);

		///for label form map
		EtsDropDownDataPrepBean dropDataPrep = new EtsDropDownDataPrepBean(etsIssObjKey);
		HashMap labelMap = dropDataPrep.getFormLabelData();

		//set labelmap
		etsIssObjKey.setFormLabelMap(labelMap);
		
		//flop is the filtered opn state, which tells form which state the page has come
		String filopn = AmtCommonUtils.getTrimStr((String) params.get("flop"));
		etsIssObjKey.setFilopn(filopn);
		
		//istype, if it is issue/change request
		String istyp = AmtCommonUtils.getTrimStr((String) params.get("istyp"));
		etsIssObjKey.setIstyp(istyp);
		
		//set issue owner
		etsIssObjKey.setShowIssueOwner(EtsIssFilterUtils.isShowIssueOwner(proj));
		
		etsIssObjKey.setProjBladeType(proj.isProjBladeType());
		
		//set user/roles matrix
		//	get user/roles matrix
		EtsIssUserRoleFilter userFilter = new EtsIssUserRoleFilter();
		EtsIssUserRolesModel usrRolesModel = userFilter.getUserRoleMatrix(etsIssObjKey);
		
		etsIssObjKey.setUsrRolesModel(usrRolesModel);
		
		//set the project pmo enabled
		etsIssObjKey.setProjPmoEnabled(isProjectPmoEnabled((String)etsIssObjKey.getProj().getPmo_project_id()));
						
		return etsIssObjKey;

	}
	
	/**
	 * 
	 * @param pmoProjectId
	 * @return
	 */
	
	public boolean isProjectPmoEnabled(String pmoProjectId) {
		
		if(AmtCommonUtils.isResourceDefined(pmoProjectId)) {
			
			return true;
		}
		
		return false;
	}
	
	
} //end of class
