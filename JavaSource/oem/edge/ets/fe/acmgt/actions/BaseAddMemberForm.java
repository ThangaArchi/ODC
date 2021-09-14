/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.acmgt.actions;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;
import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * @author Suresh
 */
public class BaseAddMemberForm extends ActionForm {

	private Vector m_vtUserList;
	
	private String[] m_pdibmUserList;
	private String[] m_pdExtUserList;
	private String[] m_pdInvEmailIds;
	private String[] m_pdChgdInvEmailIds;
	private String[] m_pdChkEmailIds;
	private String[] m_pdChkWarnIds;
	private String[] m_pdChkIncompWarnIds;
	private String[] m_pdExtIncompIds;
	
	private AddMembrUserDetails m_udAddMembrDetails = new AddMembrUserDetails();
	
	private Vector m_vtUserIdsinWS;
	private Vector m_vtInviteUserIds;
	private Vector m_vtIdsTobeConfmd; 
	private Vector m_vtIntIdsTobeAdded;
	private Vector m_vtExtIdsTobeAdded;
	private Vector m_vtIntUsrsWthMltiEmailId;
	private Vector m_vtExtUsrsWthMltiEmailId;
	private Vector m_vtIntUsrPrvlgs;
	private Vector m_vtExtUsrPrvlgs;
	private Vector m_vtCountryList;
	private Vector m_vtWarnExtIds;
	private Vector m_vtChkdExtIds;
	private Vector m_vtIncompWarnExtIds;
	private Vector m_vtChkdIncompExtIds;
	
	private Vector m_vtAddedSuccess;
	private Vector m_vtIntAdedReqEntPen;
	private Vector m_vtExtReqWo ;
	private Vector m_vtExtHasPendReq;
	private Vector m_vtExtAdedReqEnt;
	private Vector m_vtExtAdedReqEntPen;
	private Vector m_vtAddError ;
	private Vector m_vtAicExtIbmOnly;
	private Vector m_vtNoMultiPOCEntforWO;
	
	private Vector m_vtInvitedUsers;
	private Vector m_vtInviteError;
	private Vector m_vtIncompMailSentIds;
	private Vector m_vtIncompMailErrorIds;
	
	private Vector m_vtFinalVerifyIds;
	private Vector m_vtAddedVerifyIds;
	private Vector m_vtVrfyReqEntlList;
	
	private Vector m_vtChgdIdsTobeConfmd;
	private Vector m_vtFinalToBeConfrmdIds;
	private Vector m_vtChgdInviteUserIds;
	private Vector m_vtFinalInviteIds;
	
	private ArrayList m_alCompanyList;
	
	private String m_vtUsersDetails;
	private String m_strFormContext;
	private String m_strProjectId;
	private String m_strLinkId;
	private String m_strTopCatId;
	private String m_strMultIds;
	
	private String m_strInvEmailId;
	private String m_strChkEmailId;
	
	private String wsCompany;
	private String forward;
		
	private Vector m_vtFinalPendingIds;
	private Vector m_vtFinalIncompExtIds;
	private Vector m_vtPendingIds;
	private Vector m_vtIncompExtIds;
	
	private Vector m_vtIntIdsinWS;
	private Vector m_vtExtIdsinWS;
	private Vector m_vtImportUsers;
			
	private int userIdsNull=0;
	private int noResults=0;
	private int aicAllUsers = 0;
		
	private FormFile importList;
	int noFile = 0;
	int invalidHeader = 0;
	int dataError = 0;
	int noValidData = 0;
	int fileEmpty = 0;
	int woHasMultiPOC = 1;
	List errorList;
	
	String from;
	String fromInput;
	String fromImport;
	String fromVerify;
	String fromInternal;
	String fromExternal;
	String fromWarning;
	
	private String propAppName = "";
	
	/**
	 * @return Returns the woHasMultiPOC.
	 */
	public int getWoHasMultiPOC() {
		return woHasMultiPOC;
	}
	/**
	 * @param woHasMultiPOC The woHasMultiPOC to set.
	 */
	public void setWoHasMultiPOC(int woHasMultiPOC) {
		this.woHasMultiPOC = woHasMultiPOC;
	}
	/**
	 * @return Returns the m_vtNoMultiPOCEntforWO.
	 */
	public Vector getNoMultiPOCEntforWO() {
		return m_vtNoMultiPOCEntforWO;
	}
	/**
	 * @param noMultiPOCEntforWO The m_vtNoMultiPOCEntforWO to set.
	 */
	public void setNoMultiPOCEntforWO(Vector noMultiPOCEntforWO) {
		m_vtNoMultiPOCEntforWO = noMultiPOCEntforWO;
	}
	
	/**
	 * @return Returns the aicAllUsers.
	 */
	public int getAicAllUsers() {
		return aicAllUsers;
	}
	/**
	 * @param aicAllUsers The aicAllUsers to set.
	 */
	public void setAicAllUsers(int aicAllUsers) {
		this.aicAllUsers = aicAllUsers;
	}
	/**
	 * @return Returns the fileEmpty.
	 */
	public int getFileEmpty() {
		return fileEmpty;
	}
	/**
	 * @param fileEmpty The fileEmpty to set.
	 */
	public void setFileEmpty(int fileEmpty) {
		this.fileEmpty = fileEmpty;
	}
	/**
	 * @return Returns the prop.
	 */
	public String getPropAppName() {
		return propAppName;
	}
	/**
	 * @param prop The prop to set.
	 */
	public void setPropAppName(String propAppName) {
		this.propAppName = propAppName;
	}
	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return from;
	}
	/**
	 * @param from The from to set.
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	/**
	 * @return Returns the fromExternal.
	 */
	public String getFromExternal() {
		return fromExternal;
	}
	/**
	 * @param fromExternal The fromExternal to set.
	 */
	public void setFromExternal(String fromExternal) {
		this.fromExternal = fromExternal;
	}
	/**
	 * @return Returns the fromImport.
	 */
	public String getFromImport() {
		return fromImport;
	}
	/**
	 * @param fromImport The fromImport to set.
	 */
	public void setFromImport(String fromImport) {
		this.fromImport = fromImport;
	}
	/**
	 * @return Returns the fromInput.
	 */
	public String getFromInput() {
		return fromInput;
	}
	/**
	 * @param fromInput The fromInput to set.
	 */
	public void setFromInput(String fromInput) {
		this.fromInput = fromInput;
	}
	/**
	 * @return Returns the fromInternal.
	 */
	public String getFromInternal() {
		return fromInternal;
	}
	/**
	 * @param fromInternal The fromInternal to set.
	 */
	public void setFromInternal(String fromInternal) {
		this.fromInternal = fromInternal;
	}
	/**
	 * @return Returns the fromVerify.
	 */
	public String getFromVerify() {
		return fromVerify;
	}
	/**
	 * @param fromVerify The fromVerify to set.
	 */
	public void setFromVerify(String fromVerify) {
		this.fromVerify = fromVerify;
	}
	/**
	 * @return Returns the fromWarning.
	 */
	public String getFromWarning() {
		return fromWarning;
	}
	/**
	 * @param fromWarning The fromWarning to set.
	 */
	public void setFromWarning(String fromWarning) {
		this.fromWarning = fromWarning;
	}
		/**
		 * @return Returns the noValidData.
		 */
		public int getNoValidData() {
			return noValidData;
		}
		/**
		 * @param noValidData The noValidData to set.
		 */
		public void setNoValidData(int noValidData) {
			this.noValidData = noValidData;
		}
		/**
		 * @return Returns the errorList.
		 */
		public List getErrorList() {
			return errorList;
		}
		/**
		 * @param errorList The errorList to set.
		 */
		public void setErrorList(List errorList) {
			this.errorList = errorList;
		}
		/**
		 * @return Returns the dataError.
		 */
		public int getDataError() {
			return dataError;
		}
		/**
		 * @param dataError The dataError to set.
		 */
		public void setDataError(int dataError) {
			this.dataError = dataError;
		}
		/**
		 * @return Returns the invalidHeader.
		 */
		public int getInvalidHeader() {
			return invalidHeader;
		}
		/**
		 * @param invalidHeader The invalidHeader to set.
		 */
		public void setInvalidHeader(int invalidHeader) {
			this.invalidHeader = invalidHeader;
		}
		/**
		 * @return Returns the noFile.
		 */
		public int getNoFile() {
			return noFile;
		}
		/**
		 * @param noFile The noFile to set.
		 */
		public void setNoFile(int noFile) {
			this.noFile = noFile;
		}
	    /**
	     * @return Returns the Form File.
	     */
	       public FormFile getImportList() {
	        return importList;
	    }
	    
	    public void setImportList(FormFile importList) {
	        this.importList = importList;
	    }
	   	
	/**
	 * @return Returns the m_vtImportUsers.
	 */
	public Vector getImportUsers() {
		return m_vtImportUsers;
	}
	/**
	 * @param importUsers The m_vtImportUsers to set.
	 */
	public void setImportUsers(Vector importUsers) {
		m_vtImportUsers = importUsers;
	}
	/**
	 * @return Returns the m_pdChgdInvEmailIds.
	 */
	public String[] getChgdInvEmailIds() {
		return m_pdChgdInvEmailIds;
	}
	/**
	 * @param chgdInvEmailIds The m_pdChgdInvEmailIds to set.
	 */
	public void setChgdInvEmailIds(String[] chgdInvEmailIds) {
		m_pdChgdInvEmailIds = chgdInvEmailIds;
	}
	/**
	 * @return Returns the m_vtIncompMailErrorIds.
	 */
	public Vector getIncompMailErrorIds() {
		return m_vtIncompMailErrorIds;
	}
	/**
	 * @param incompMailErrorIds The m_vtIncompMailErrorIds to set.
	 */
	public void setIncompMailErrorIds(Vector incompMailErrorIds) {
		m_vtIncompMailErrorIds = incompMailErrorIds;
	}
	/**
	 * @return Returns the m_vtIncompMailSentIds.
	 */
	public Vector getIncompMailSentIds() {
		return m_vtIncompMailSentIds;
	}
	/**
	 * @param incompMailSentIds The m_vtIncompMailSentIds to set.
	 */
	public void setIncompMailSentIds(Vector incompMailSentIds) {
		m_vtIncompMailSentIds = incompMailSentIds;
	}
	/**
	 * @return Returns the m_pdChkIncompWarnIds.
	 */
	public String[] getChkIncompWarnIds() {
		return m_pdChkIncompWarnIds;
	}
	/**
	 * @param chkIncompWarnIds The m_pdChkIncompWarnIds to set.
	 */
	public void setChkIncompWarnIds(String[] chkIncompWarnIds) {
		m_pdChkIncompWarnIds = chkIncompWarnIds;
	}
	/**
	 * @return Returns the m_vtChkdIncompExtIds.
	 */
	public Vector getChkdIncompExtIds() {
		return m_vtChkdIncompExtIds;
	}
	/**
	 * @param chkdIncompExtIds The m_vtChkdIncompExtIds to set.
	 */
	public void setChkdIncompExtIds(Vector chkdIncompExtIds) {
		m_vtChkdIncompExtIds = chkdIncompExtIds;
	}
	/**
	 * @return Returns the m_vtIncompWarnExtIds.
	 */
	public Vector getIncompWarnExtIds() {
		return m_vtIncompWarnExtIds;
	}
	/**
	 * @param incompWarnExtIds The m_vtIncompWarnExtIds to set.
	 */
	public void setIncompWarnExtIds(Vector incompWarnExtIds) {
		m_vtIncompWarnExtIds = incompWarnExtIds;
	}
	/**
	 * @return Returns the m_pdExtIncompIds.
	 */
	public String[] getExtIncompIds() {
		return m_pdExtIncompIds;
	}
	/**
	 * @param extIncompIds The m_pdExtIncompIds to set.
	 */
	public void setExtIncompIds(String[] extIncompIds) {
		m_pdExtIncompIds = extIncompIds;
	}
	/**
	 * @return Returns the m_vtFinalInviteIds.
	 */
	public Vector getFinalInviteIds() {
		return m_vtFinalInviteIds;
	}
	/**
	 * @param finalInviteIds The m_vtFinalInviteIds to set.
	 */
	public void setFinalInviteIds(Vector finalInviteIds) {
		m_vtFinalInviteIds = finalInviteIds;
	}
	/**
	 * @return Returns the m_vtChgdIdsTobeConfmd.
	 */
	public Vector getChgdIdsTobeConfmd() {
		return m_vtChgdIdsTobeConfmd;
	}
	/**
	 * @param chgdIdsTobeConfmd The m_vtChgdIdsTobeConfmd to set.
	 */
	public void setChgdIdsTobeConfmd(Vector chgdIdsTobeConfmd) {
		m_vtChgdIdsTobeConfmd = chgdIdsTobeConfmd;
	}
	/**
	 * @return Returns the m_vtChgdInviteUserIds.
	 */
	public Vector getChgdInviteUserIds() {
		return m_vtChgdInviteUserIds;
	}
	/**
	 * @param chgdInviteUserIds The m_vtChgdInviteUserIds to set.
	 */
	public void setChgdInviteUserIds(Vector chgdInviteUserIds) {
		m_vtChgdInviteUserIds = chgdInviteUserIds;
	}
	/**
	 * @return Returns the m_vtFinalToBeConfrmdIds.
	 */
	public Vector getFinalToBeConfrmdIds() {
		return m_vtFinalToBeConfrmdIds;
	}
	/**
	 * @param finalToBeConfrmdIds The m_vtFinalToBeConfrmdIds to set.
	 */
	public void setFinalToBeConfrmdIds(Vector finalToBeConfrmdIds) {
		m_vtFinalToBeConfrmdIds = finalToBeConfrmdIds;
	}
	/**
	 * @return Returns the m_vtExtIdsinWS.
	 */
	public Vector getExtIdsinWS() {
		return m_vtExtIdsinWS;
	}
	/**
	 * @param extIdsinWS The m_vtExtIdsinWS to set.
	 */
	public void setExtIdsinWS(Vector extIdsinWS) {
		m_vtExtIdsinWS = extIdsinWS;
	}
	/**
	 * @return Returns the m_vtIntIdsinWS.
	 */
	public Vector getIntIdsinWS() {
		return m_vtIntIdsinWS;
	}
	/**
	 * @param intIdsinWS The m_vtIntIdsinWS to set.
	 */
	public void setIntIdsinWS(Vector intIdsinWS) {
		m_vtIntIdsinWS = intIdsinWS;
	}
	/**
	 * @return Returns the m_vtFinalIncompExtIds.
	 */
	public Vector getFinalIncompExtIds() {
		return m_vtFinalIncompExtIds;
	}
	/**
	 * @param finalIncompExtIds The m_vtFinalIncompExtIds to set.
	 */
	public void setFinalIncompExtIds(Vector finalIncompExtIds) {
		m_vtFinalIncompExtIds = finalIncompExtIds;
	}
	/**
	 * @return Returns the m_vtFinalPendingIds.
	 */
	public Vector getFinalPendingIds() {
		return m_vtFinalPendingIds;
	}
	/**
	 * @param finalPendingIds The m_vtFinalPendingIds to set.
	 */
	public void setFinalPendingIds(Vector finalPendingIds) {
		m_vtFinalPendingIds = finalPendingIds;
	}
	/**
	 * @return Returns the m_vtIncompExtIds.
	 */
	public Vector getIncompExtIds() {
		return m_vtIncompExtIds;
	}
	/**
	 * @param incompExtIds The m_vtIncompExtIds to set.
	 */
	public void setIncompExtIds(Vector incompExtIds) {
		m_vtIncompExtIds = incompExtIds;
	}
	/**
	 * @return Returns the m_vtPendingIds.
	 */
	public Vector getPendingIds() {
		return m_vtPendingIds;
	}
	/**
	 * @param pendingIds The m_vtPendingIds to set.
	 */
	public void setPendingIds(Vector pendingIds) {
		m_vtPendingIds = pendingIds;
	}
	/**
	 * @return Returns the m_vtExtHasPendReq.
	 */
	public Vector getExtHasPendReq() {
		return m_vtExtHasPendReq;
	}
	/**
	 * @param extHasPendReq The m_vtExtHasPendReq to set.
	 */
	public void setExtHasPendReq(Vector extHasPendReq) {
		m_vtExtHasPendReq = extHasPendReq;
	}
	/**
	 * @return Returns the forward.
	 */
	public String getForward() {
		return forward;
	}
	/**
	 * @param forward The forward to set.
	 */
	public void setForward(String forward) {
		this.forward = forward;
	}
	/**
	 * @return Returns the noResults.
	 */
	public int getNoResults() {
		return noResults;
	}
	/**
	 * @param noResults The noResults to set.
	 */
	public void setNoResults(int noResults) {
		this.noResults = noResults;
	}
	/**
	 * @return Returns the m_vtInviteError.
	 */
	public Vector getInviteError() {
		return m_vtInviteError;
	}
	/**
	 * @param inviteError The m_vtInviteError to set.
	 */
	public void setInviteError(Vector inviteError) {
		m_vtInviteError = inviteError;
	}
	/**
	 * @return Returns the m_udAddMembrDetails.
	 */
	public AddMembrUserDetails getAddMembrDetails() {
		return m_udAddMembrDetails;
	}
	/**
	 * @param addMembrDetails The m_udAddMembrDetails to set.
	 */
	public void setAddMembrDetails(AddMembrUserDetails addMembrDetails) {
		m_udAddMembrDetails = addMembrDetails;
	}
	/**
	 * @return Returns the m_vtAddedSuccess.
	 */
	public Vector getAddedSuccess() {
		return m_vtAddedSuccess;
	}
	/**
	 * @param addedSuccess The m_vtAddedSuccess to set.
	 */
	public void setAddedSuccess(Vector addedSuccess) {
		m_vtAddedSuccess = addedSuccess;
	}
	/**
	 * @return Returns the m_vtAddError.
	 */
	public Vector getAddError() {
		return m_vtAddError;
	}
	/**
	 * @param addError The m_vtAddError to set.
	 */
	public void setAddError(Vector addError) {
		m_vtAddError = addError;
	}
	/**
	 * @return Returns the m_vtAicExtIbmOnly.
	 */
	public Vector getAicExtIbmOnly() {
		return m_vtAicExtIbmOnly;
	}
	/**
	 * @param aicExtIbmOnly The m_vtAicExtIbmOnly to set.
	 */
	public void setAicExtIbmOnly(Vector aicExtIbmOnly) {
		m_vtAicExtIbmOnly = aicExtIbmOnly;
	}
	/**
	 * @return Returns the m_vtExtAdedReqEnt.
	 */
	public Vector getExtAdedReqEnt() {
		return m_vtExtAdedReqEnt;
	}
	/**
	 * @param extAdedReqEnt The m_vtExtAdedReqEnt to set.
	 */
	public void setExtAdedReqEnt(Vector extAdedReqEnt) {
		m_vtExtAdedReqEnt = extAdedReqEnt;
	}
	/**
	 * @return Returns the m_vtExtAdedReqEntPen.
	 */
	public Vector getExtAdedReqEntPen() {
		return m_vtExtAdedReqEntPen;
	}
	/**
	 * @param extAdedReqEntPen The m_vtExtAdedReqEntPen to set.
	 */
	public void setExtAdedReqEntPen(Vector extAdedReqEntPen) {
		m_vtExtAdedReqEntPen = extAdedReqEntPen;
	}
	/**
	 * @return Returns the m_vtExtReqWo.
	 */
	public Vector getExtReqWo() {
		return m_vtExtReqWo;
	}
	/**
	 * @param extReqWo The m_vtExtReqWo to set.
	 */
	public void setExtReqWo(Vector extReqWo) {
		m_vtExtReqWo = extReqWo;
	}
	/**
	 * @return Returns the m_vtIntAdedReqEntPen.
	 */
	public Vector getIntAdedReqEntPen() {
		return m_vtIntAdedReqEntPen;
	}
	/**
	 * @param intAdedReqEntPen The m_vtIntAdedReqEntPen to set.
	 */
	public void setIntAdedReqEntPen(Vector intAdedReqEntPen) {
		m_vtIntAdedReqEntPen = intAdedReqEntPen;
	}
	/**
	 * @return Returns the m_vtAddedVerifyIds.
	 */
	public Vector getAddedVerifyIds() {
		return m_vtAddedVerifyIds;
	}
	/**
	 * @param addedVerifyIds The m_vtAddedVerifyIds to set.
	 */
	public void setAddedVerifyIds(Vector addedVerifyIds) {
		m_vtAddedVerifyIds = addedVerifyIds;
	}
	/**
	 * @return Returns the m_vtChkdExtIds.
	 */
	public Vector getChkdExtIds() {
		return m_vtChkdExtIds;
	}
	/**
	 * @param chkdExtIds The m_vtChkdExtIds to set.
	 */
	public void setChkdExtIds(Vector chkdExtIds) {
		m_vtChkdExtIds = chkdExtIds;
	}
	/**
	 * @return Returns the m_vtFinalVerifyIds.
	 */
	public Vector getFinalVerifyIds() {
		return m_vtFinalVerifyIds;
	}
	/**
	 * @param finalVerifyIds The m_vtFinalVerifyIds to set.
	 */
	public void setFinalVerifyIds(Vector finalVerifyIds) {
		m_vtFinalVerifyIds = finalVerifyIds;
	}
	/**
	 * @return Returns the m_vtInvitedUsers.
	 */
	public Vector getInvitedUsers() {
		return m_vtInvitedUsers;
	}
	/**
	 * @param invitedUsers The m_vtInvitedUsers to set.
	 */
	public void setInvitedUsers(Vector invitedUsers) {
		m_vtInvitedUsers = invitedUsers;
	}
	/**
	 * @return Returns the m_vtVrfyReqEntlList.
	 */
	public Vector getVrfyReqEntlList() {
		return m_vtVrfyReqEntlList;
	}
	/**
	 * @param vrfyReqEntlList The m_vtVrfyReqEntlList to set.
	 */
	public void setVrfyReqEntlList(Vector vrfyReqEntlList) {
		m_vtVrfyReqEntlList = vrfyReqEntlList;
	}
	/**
	 * @return Returns the m_vtChkWarnIds.
	 */
	public String[] getChkWarnIds() {
		return m_pdChkWarnIds;
	}
	/**
	 * @param chkWarnIds The m_vtChkWarnIds to set.
	 */
	public void setChkWarnIds(String[] chkWarnIds) {
		m_pdChkWarnIds = chkWarnIds;
	}
	/**
	 * @return Returns the m_vtWarnExtIds.
	 */
	public Vector getWarnExtIds() {
		return m_vtWarnExtIds;
	}
	/**
	 * @param m_vtWarnExtIds The warnExtIds to set.
	 */
	public void setWarnExtIds(Vector warnExtIds) {
		this.m_vtWarnExtIds = warnExtIds;
	}
	/**
	 * @return Returns the wsCompany.
	 */
	public String getWsCompany() {
		return wsCompany;
	}
	/**
	 * @param wsCompany The wsCompany to set.
	 */
	public void setWsCompany(String wsCompany) {
		this.wsCompany = wsCompany;
	}
	/**
	 * @return Returns the m_alCompanyList.
	 */
	public ArrayList getCompanyList() {
		return m_alCompanyList;
	}
	/**
	 * @param companyList The m_alCompanyList to set.
	 */
	public void setCompanyList(ArrayList companyList) {
		m_alCompanyList = companyList;
	}
	/**
	 * @return Returns the m_vtCountryList.
	 */
	public Vector getCountryList() {
		return m_vtCountryList;
	}
	/**
	 * @param countryList The m_vtCountryList to set.
	 */
	public void setCountryList(Vector countryList) {
		m_vtCountryList = countryList;
	}
	/**
	 * @return Returns the m_vtExtUsrPrvlgs.
	 */
	public Vector getExtUsrPrvlgs() {
		return m_vtExtUsrPrvlgs;
	}
	/**
	 * @param extUsrPrvlgs The m_vtExtUsrPrvlgs to set.
	 */
	public void setExtUsrPrvlgs(Vector extUsrPrvlgs) {
		m_vtExtUsrPrvlgs = extUsrPrvlgs;
	}
	/**
	 * @return Returns the m_vtIntUsrPrvlgs.
	 */
	public Vector getIntUsrPrvlgs() {
		return m_vtIntUsrPrvlgs;
	}
	/**
	 * @param intUsrPrvlgs The m_vtIntUsrPrvlgs to set.
	 */
	public void setIntUsrPrvlgs(Vector intUsrPrvlgs) {
		m_vtIntUsrPrvlgs = intUsrPrvlgs;
	}
	/**
	 * @return Returns the IntUsrsWthMltiEmailId.
	 */
	public Vector getIntUsrsWthMltiEmailId() {
		return m_vtIntUsrsWthMltiEmailId;
	}
	/**
	 * @param IntUsrsWthMltiEmailId The IntUsrsWthMltiEmailId to set.
	 */
	public void setIntUsrsWthMltiEmailId(Vector IntUsrsWthMltiEmailId) {
		this.m_vtIntUsrsWthMltiEmailId = IntUsrsWthMltiEmailId;
	}
	/**
	 * @return Returns the IntIdsTobeAdded.
	 */
	public Vector getIntIdsTobeAdded() {
		return m_vtIntIdsTobeAdded;
	}
	/**
	 * @param IntIdsTobeAdded The IntIdsTobeAdded to set.
	 */
	public void setIntIdsTobeAdded(Vector IntIdsTobeAdded) {
		this.m_vtIntIdsTobeAdded = IntIdsTobeAdded;
	}
	/**
	 * @return Returns the ExtUsrsWthMltiEmailId.
	 */
	public Vector getExtUsrsWthMltiEmailId() {
		return m_vtExtUsrsWthMltiEmailId;
	}
	/**
	 * @param ExtUsrsWthMltiEmailId The ExtUsrsWthMltiEmailId to set.
	 */
	public void setExtUsrsWthMltiEmailId(Vector ExtUsrsWthMltiEmailId) {
		this.m_vtExtUsrsWthMltiEmailId = ExtUsrsWthMltiEmailId;
	}
	/**
	 * @return Returns the ExtIdsTobeAdded.
	 */
	public Vector getExtIdsTobeAdded() {
		return m_vtExtIdsTobeAdded;
	}
	/**
	 * @param ExtIdsTobeAdded The ExtIdsTobeAdded to set.
	 */
	public void setExtIdsTobeAdded(Vector ExtIdsTobeAdded) {
		this.m_vtExtIdsTobeAdded = ExtIdsTobeAdded;
	}
	/**
	 * @return Returns the idsTobeConfmd.
	 */
	public Vector getIdsTobeConfmd() {
		return m_vtIdsTobeConfmd;
	}
	/**
	 * @param idsTobeConfmd The idsTobeConfmd to set.
	 */
	public void setIdsTobeConfmd(Vector idsTobeConfmd) {
		this.m_vtIdsTobeConfmd = idsTobeConfmd;
	}
	/**
	 * @return Returns the inviteUserIds.
	 */
	public Vector getInviteUserIds() {
		return m_vtInviteUserIds;
	}
	/**
	 * @param inviteUserIds The inviteUserIds to set.
	 */
	public void setInviteUserIds(Vector inviteUserIds) {
		this.m_vtInviteUserIds = inviteUserIds;
	}
	/**
	 * @return Returns the userIdsinWS.
	 */
	public Vector getUserIdsinWS() {
		return m_vtUserIdsinWS;
	}
	/**
	 * @param userIdsinWS The userIdsinWS to set.
	 */
	public void setUserIdsinWS(Vector userIdsinWS) {
		this.m_vtUserIdsinWS = userIdsinWS;
	}
	/**
	 * @return Returns the isUserIdsNull.
	 */
	public int getUserIdsNull() {
		return userIdsNull;
	}
	/**
	 * @param isUserIdsNull The isUserIdsNull to set.
	 */
	public void setUserIdsNull(int isUserIdsNull) {
		this.userIdsNull = isUserIdsNull;
	}
	/**
	 * @return m_vtInvEmailIds
	 */
	public String[] getInvEmailIds() {
		return m_pdInvEmailIds;
	}

	/**
	 * @param vtEmailIds
	 */
	public void setInvEmailIds(String[] vtEmailIds) {
		this.m_pdInvEmailIds = vtEmailIds;
	}
	
	/**
	 * @return m_vtChkEmailIds
	 */
	public String[] getChkEmailIds() {
		return m_pdChkEmailIds;
	}

	/**
	 * @param vtEmailIds
	 */
	public void setChkEmailIds(String[] vtEmailIds) {
		this.m_pdChkEmailIds = vtEmailIds;
	}
	
	/**
	 * @return m_strMultIds
	 */
	public String getMultIds() {
		return m_strMultIds;
	}

	/**
	 * @param strMultIds
	 */
	public void setMultIds(String strMultIds) {
		m_strMultIds = strMultIds;
	}
	
	/**
	 * @return m_strInvEmailId
	 */
	public String getInvEmailId() {
		return m_strInvEmailId;
	}

	/**
	 * @param strEmailId
	 */
	public void setInvEmailId(String strEmailId) {
		m_strInvEmailId = strEmailId;
	}

	/**
	 * @return m_strChkEmailId
	 */
	public String getChkEmailId() {
		return m_strChkEmailId;
	}

	/**
	 * @param strEmailId
	 */
	public void setChkEmailId(String strEmailId) {
		m_strChkEmailId = strEmailId;
	} 
	
	/**
	 * @return
	 */
	public String getFormContext() {
		return m_strFormContext;
	}

	/**
	 * @param strFormContext
	 */
	public void setFormContext(String strFormContext) {
		m_strFormContext = strFormContext;
	}
	
	/**
	 * @return
	 */
	public String getLinkid() {
		return m_strLinkId;
	}

	/**
	 * @return
	 */
	public String getProj() {
		return m_strProjectId;
	}

	/**
	 * @param strLinkId
	 */
	public void setLinkid(String strLinkId) {
		m_strLinkId = strLinkId;
	}

	/**
	 * @param strProjectId
	 */
	public void setProj(String strProjectId) {
		m_strProjectId = strProjectId;
	}

	/**
	 * @return
	 */
	public String getTc() {
		return m_strTopCatId;
	}

	/**
	 * @param strTopCatId
	 */
	public void setTc(String strTopCatId) {
		m_strTopCatId = strTopCatId;
	}


	/**
	 * @return Returns the m_vtExtUserList.
	 */
	public String[] getExtUserList() {
		return m_pdExtUserList;
	}
	/**
	 * @param extUserList The m_vtExtUserList to set.
	 */
	public void setExtUserList(String[] extUserList) {
		m_pdExtUserList = extUserList;
	}
	/**
	 * @return Returns the m_vtIBMUserList.
	 */
	public String[] getIbmUserList() {
		return m_pdibmUserList;
	}
	/**
	 * @param userList The m_vtIBMUserList to set.
	 */
	public void setIbmUserList(String[] userList) {
		m_pdibmUserList = userList;
	}
	/**
	 * @return Returns the m_vtUserList.
	 */
	public Vector getUserList() {
		return m_vtUserList;
	}
	/**
	 * @param userList The m_vtUserList to set.
	 */
	public void setUserList(Vector userList) {
		m_vtUserList = userList;
	}
	/**
	 * @return Returns the m_vtUsersDetails.
	 */
	public String getUsersDetails() {
		return m_vtUsersDetails;
	}
	/**
	 * @param usersDetails The m_vtUsersDetails to set.
	 */
	public void setUsersDetails(String usersDetails) {
		m_vtUsersDetails = usersDetails;
	}
}
