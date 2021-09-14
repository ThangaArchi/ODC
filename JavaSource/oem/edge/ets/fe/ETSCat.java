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


package oem.edge.ets.fe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;

public class ETSCat extends ETSDetailedObj implements ETSObj{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.8";


    protected int id;
    //spn 0312 projid
    //protected int projectid;
    protected String projectid;
    protected String userid;
	protected String username;
    protected String name;
    protected int parentid;
    protected int cattype;
    protected String description;
    protected int order;
    protected int viewtype;
    protected int projdesc;
    protected int projmsg;
    protected Vector privs;
    //protected Vector roles;
    protected long last_timestamp;
    protected long first_timestamp;
    protected char ibmOnly;  //0,1,x
	
	protected String cprivate;
	protected String m_strDisplayFlag;
	protected String m_strVisibleFlag;
	
	protected Vector m_lstSubCats = new Vector();
	
    public ETSCat(){
	//id = NameManager.getUniqueID();
	id = 0;
	projectid = "";
	userid = "";
	name = "";
	parentid = -1;
	cattype = 0;
	description = "";
	order = 0;
	viewtype = 0;
	projdesc = 0;
	projmsg = 0;
	ibmOnly = Defines.ETS_PUBLIC;
	privs = new Vector();
	cprivate = "0";
	
    }

    public void setId(int id){
	this.id = id;
    }
    public int getId(){
	return id;
    }

    //spn 0312 projid
    public void setProjectId(String projectid){
	this.projectid = projectid;
    }
    public String getProjectId(){
	return projectid;
    }

    public void setUserId(String userid){
	this.userid = userid;
    }
    public String getUserId(){
	return userid;
    }

    public void setName(String name){
	this.name = name;
    }
    public String getName(){
	return name;
    }

    public void setParentId(int parentid){
	this.parentid = parentid;
    }
    public int getParentId(){
	return parentid;
    }

    public void setCatType(int cattype){
	this.cattype = cattype;
    }
    public int getCatType(){
	return cattype;
    }

    public void setDescription(String desc){
	this.description = desc;
    }
    public String getDescription(){
	return description;
    }

    public void setOrder(int order){
	this.order = order;
    }
    public int getOrder(){
	return order;
    }

    public void setViewType(int type){
	this.viewtype = type;
    }
    public int getViewType(){
	return viewtype;
    }

    public void setProjDesc(int projdesc){
	this.projdesc = projdesc;
    }
    public int getProjDesc(){
	return projdesc;
    }

    public void setProjMsg(int projmsg){
	this.projmsg = projmsg;
    }
    public int getProjMsg(){
	return projmsg;
    }


    public void setPrivs(Vector privsList){
	privs = privsList;
    }
    public void setPrivs(String privsList){
	if (privsList != null){
	    StringTokenizer st = new StringTokenizer(privsList, ",");
	    privs = new Vector();
	    while (st.hasMoreTokens()){
		String priv = st.nextToken();
		privs.addElement(priv);
	    }
	}
    }
    public Vector getPrivs(){
	return privs;
    }
    public String getPrivsString(){
	String s = "";
	for (int i = 0; i<privs.size(); i++){
	    if (i > 0){
		s += ",";
	    }

	    s += (String)privs.elementAt(i);
	}

	return s;
    }
    public Vector getPrivsInts(){
	Vector s = new Vector();
	for (int i = 0; i<privs.size(); i++){
	    s.addElement(new Integer((String)privs.elementAt(i)));
	}

	return s;
    }


	public String getFormattedFirstTimeStamp() {
		if(first_timestamp == 0)
			return "NA";
		else
		{
			Date dtFirstTimeStamp = new Date(first_timestamp);
			SimpleDateFormat pdDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			return pdDateFormat.format(dtFirstTimeStamp);
		}
	}
	

	public String getFormattedLastTimeStamp() {
		Date dtLastTimeStamp = new Date(last_timestamp);
		SimpleDateFormat pdDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return pdDateFormat.format(dtLastTimeStamp);
	}
	

	public String getIBMOnlyStr() {
		char []ibmonly = new char[1];
		ibmonly[0] = this.ibmOnly;
		return new String(ibmonly);
	}
	public void setIBMOnlyStr(String strIBMOnly) {
		ibmOnly = strIBMOnly.charAt(0);
	}
	
	public long getLastTimestamp(){
	return last_timestamp;
    }
	public void setLastTimestamp(){
	this.last_timestamp = new Date().getTime();
    }
	public void setLastTimestamp(long l_timestamp){
	this.last_timestamp = l_timestamp;
    }
	public void setLastTimestamp(java.sql.Timestamp d){
	this.last_timestamp = d.getTime();
    }


	
	
	

	
	
	
	public long getFirstTimestamp(){
	return first_timestamp;
    }

	public void setFirstTimestamp(){
	this.first_timestamp = new Date().getTime();
    }
	public void setFirstTimestamp(long l_timestamp){
	this.first_timestamp = l_timestamp;
    }
	public void setFirstTimestamp(java.sql.Timestamp d){
	this.first_timestamp = d.getTime();
    }


	
	
	
	
	
	public void setIbmOnly(char c) {
		ibmOnly = c;
    }
	public void setIbmOnly(String s){
		if (s != null && !s.equals("")){
			char c = s.charAt(0);
			if (c == Defines.NOT_SET_FLAG){
				c = Defines.ETS_PUBLIC;	
			}
			ibmOnly = c;
		}
		else{
			ibmOnly = Defines.ETS_PUBLIC;
		}
	}
	public char getIbmOnly() {
		return ibmOnly;
	}
	public boolean isIbmOnlyOrConf() {
		if(ibmOnly == Defines.ETS_IBM_CONF || ibmOnly == Defines.ETS_IBM_ONLY)
			return true;
		else if (ibmOnly == Defines.ETS_PUBLIC)
			return false;
		else
			throw new RuntimeException("flag: ibmOnly: not ibmonly, ibm conf, or public");
	}   


	public void setCPrivate(String b){
		if (b==null)
			cprivate = "0";
		else if(b.equals("1"))
			this.cprivate = b;	
		else
			this.cprivate = "0";
	}
	public void setCPrivate(boolean b){
		if(b)
			this.cprivate = "1";	
		else
			this.cprivate = "0";
	}
	public String getCPrivate(){
		return cprivate;	
	}
	public boolean IsCPrivate(){
		if (cprivate == null)
			return false;
		else if(cprivate.equals("1"))
			return true; 	
		else
			return false;
	}
	
	
public String getStringKey(String key){
	if (key.equals(Defines.SORT_BY_NAME_STR))
		return name;
	else if (key.equals(Defines.SORT_BY_TYPE_STR))
		return "";
	else if (key.equals(Defines.SORT_BY_AUTH_STR))
		return userid;
	else
		return "";
			
}
public int getIntKey(String key){
	return 0;
}

	public void setUserName(String username){
	this.username = username;
	}
	public String getUserName(){
	return username;
	}
	/**
	 * @return
	 */
	public String getDisplayFlag() {
		if (StringUtil.isNullorEmpty(m_strDisplayFlag)) {
			return StringUtil.EMPTY_STRING;
		}
		return m_strDisplayFlag;
	}

	/**
	 * @param strDisplayFlag
	 */
	public void setDisplayFlag(String strDisplayFlag) {
		m_strDisplayFlag = strDisplayFlag;
	}

	/**
	 * @param strDisplayFlag
	 */
	public void setVisibleFlag(String strVisibleFlag) {
		m_strVisibleFlag = strVisibleFlag;
	}

	/**
	 * @return
	 */
	public String getVisibleFlag() {
		return m_strVisibleFlag;
	}
    /**
     * @return Returns the m_lstSubCats.
     */
    public Vector getSubCats() {
        return m_lstSubCats;
    }
    /**
     * @param subCats The m_lstSubCats to set.
     */
    public void setSubCats(Vector subCats) {
        m_lstSubCats = subCats;
    }
}


