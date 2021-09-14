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
/**
 * @author Ravi K. Ravipati
 * Date: Dec 17, 2003
 * File: ETSQuickLinkSetup.java
 *
 */
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Hashtable;
import java.util.Vector;

import oem.edge.amt.AMTException;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;

public class ETSQuickLinks {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.12";
	public static final String VERSION = "1.12";

    public String ALL_LINKS = "ALL";
    public String ESSENTIAL_LINKS = "E";
    public String OPTIONAL_LINKS = "O";

    public ETSQuickLinks() {
        this._linkVect = new Vector();
        this._linkType = "ALL";
        this._projectId="";
        this.userId="";
        this.unionELinks=false;
        }

    public ETSQuickLink getLink(int i){
        return (ETSQuickLink)this._linkVect.elementAt(i);
    }
    public void setLinkType (String linkType){this._linkType = linkType;}
    public String getLinkType () {return this._linkType;}

    public void setProjectId(String projectId){_projectId = projectId;}
    public String getProjectId(){ return _projectId;}

    public void setUnionELinks(boolean unionELinks) {this.unionELinks = unionELinks;}

    private void setLinkVect(Vector _linkVect) {this._linkVect = _linkVect; }
    public Vector getLinkVect() {return _linkVect;  }

    public void setUserId(String userId) {this.userId = userId; }
    public String getUserId() { return userId;  }

    public void extractQuickLinks(Connection conn)
        throws SQLException, Exception {
        StringBuffer extQry = new StringBuffer();
            extQry.append("select project_id, link_id, link_name, link_url ")
                .append(", link_type, link_seq, user_doc")
                .append(", value(display,'N') as display, popup,last_user, last_timestamp ")
                .append(" from ets.quick_links ")
                .append(" where project_id = '"+ this.getProjectId()+"'");
//        if (isUnionELinks()){
//            Vector entLinks = es.getUserResource(conn, "NAV_LINK", true);
//            String accLinkQry="";
//
//            extQry.append(" union ").append(accLinkQry);
//        }
        extQry.append(" order by link_name");

        Statement extStmt = null;
        ResultSet extRs  = null;
        try {
            extStmt = conn.createStatement();
            extRs = extStmt.executeQuery(extQry.toString());
            while (extRs.next()) {
                ETSQuickLink quickLink = new ETSQuickLink();
                quickLink.setProjectId(extRs.getString("project_id"));
                quickLink.setLinkId(extRs.getString("link_id"));
                quickLink.setLinkName(extRs.getString("link_name"));
                quickLink.setLinkURL(extRs.getString("link_url"));
                quickLink.setLinkType(extRs.getString("link_type"));
                quickLink.setLinkSeq(extRs.getString("link_type"));
                quickLink.setUserDoc(extRs.getString("user_doc"));
                quickLink.setShowLink(extRs.getString("display"));
                String popUp = extRs.getString("popup");
                quickLink.setPopUp((popUp==null?"N":popUp));
                quickLink.setLastUser(extRs.getString("last_user"));
                quickLink.setLastUser(extRs.getString("last_timestamp"));
                if (this.getLinkType().equals("ALL")){
                    this._linkVect.add(quickLink);
                } else {
                    if (quickLink.getLinkType().trim().startsWith(this.getLinkType())){
                        this._linkVect.add(quickLink);
                    }
                }
            }
        } catch (Exception eX){
        }finally {
            try {extRs.close(); } catch (SQLException sqlEx){}
            try {extStmt.close(); } catch (SQLException sqlEx){}
        }

    }
    public String getNextLinkId(Connection conn)
    throws SQLException, Exception{
    	
    	Statement stLnkId=null;
		ResultSet rsLnkId =null;
    	
        String nxtLnkId = "";
        String strLnkId = "select max(integer(link_id))+1 as nxtlnk "
                        + " from ets.quick_links"
                        + " where project_id = '"+this.getProjectId()+"'";
                        
          try {              
          
        stLnkId  = conn.createStatement();
        rsLnkId = stLnkId.executeQuery(strLnkId);
        if (rsLnkId.next()){nxtLnkId = rsLnkId.getString(1);}
        
          }
          
          finally {
          	
			ETSDBUtils.close(rsLnkId);
			ETSDBUtils.close(stLnkId);
          }
        return (nxtLnkId);
    }
    public void addLink(Connection conn, ETSQuickLink qkLink){
        try {
        String strAdd = "insert into ets.quick_links "
                            +" (project_id, link_id, link_name"
                            +" , link_url, link_type "
                            +" , display, last_user, last_timestamp,popup)"
                            +" values"
                            +" ('"+qkLink.getProjectId()+"','"+qkLink.getLinkId()+"','"+qkLink.getLinkName()+"','"+qkLink.getLinkURL()+"','O','"+(qkLink.isShowLink()?"Y":"N")+"','"+qkLink.getLastUser()+"',current timestamp,'"+(qkLink.isPopUp()?"Y":"N")+"')";
        EntitledStatic.safeInsert(conn, strAdd);
        } catch (Exception eX){
        }
    }
    public void updateLink(Connection conn, ETSQuickLink qkLink){
        try {
            EntitledStatic.fireUpdate(conn,"update ets.quick_links set link_name='"+qkLink.getLinkName()+"', link_url='"+qkLink.getLinkURL()+"', display='"+(qkLink.isShowLink()?"Y":"N")+"', popup='"+(qkLink.isPopUp()?"Y":"N")+"' where project_id='"+qkLink.getProjectId()+"' and link_id = '"+qkLink.getLinkId()+"'");
        } catch (Exception eX) {
        }
    }
    public void deleteLink(Connection conn, ETSQuickLink qkLink){
        try {
            EntitledStatic.fireUpdate(conn,"delete from ets.quick_links where project_id='"+qkLink.getProjectId()+"' and link_id='"+qkLink.getLinkId()+"'");
        } catch (Exception eX){
        }
    }

    // populate the madatory links - if none
    public boolean hasQuickLinks(Connection conn){
        boolean hasLinks = false;
        String qry = "select count(*) from ets.quick_links where project_id = '"+this.getProjectId()+"'";
	    try {
            String cnt = EntitledStatic.getValue(conn, qry);
            if (Integer.parseInt(cnt)>0) {hasLinks = true;} else { hasLinks = false;}
        }catch (AMTException amtEx){
        }catch (SQLException sqlEx){
        }
        return hasLinks;
    }

    public void populateLinksFirstTime(Connection conn){
        String qry = "insert into ets.quick_links (project_id, link_id, link_name, link_url, link_type, link_seq, user_doc, display, popup, last_user, last_timestamp) "+
                    " select '"+this.getProjectId()+"',link_id, link_name, link_url, link_type, link_seq, user_doc, display, popup, '"+userId+"', (current timestamp) from ets.quick_links where project_id = 'PRJALL' and link_type like '"+this.ESSENTIAL_LINKS+"%'";
        try{
            EntitledStatic.safeInsert(conn, qry);
        } catch (SQLException sqlEx){
        }
    }

    public String showQuickLinks(ETSParams params){
      StringBuffer sBuff = new StringBuffer();
      try {
            Connection conn = params.getConnection();
            PrintWriter out = params.getWriter();
            int iTopCat = params.getTopCat();
            EdgeAccessCntrl es = params.getEdgeAccessCntrl();
            ETSProj proj = params.getETSProj();

            String projectId = params.getETSProj().getProjectId();
            
            if (proj.getProjectType().equals("AIC")) {
            	boolean bUserType = ETSUtils.isIBMer(es.gIR_USERN,conn);
            	if (!bUserType) {
            		projectId = "AIC_EXT_LINKS";
            	}
            }

            this.setProjectId(projectId);
            if (!this.hasQuickLinks(conn)){
                // first time .. populate the mandatory links
                this.populateLinksFirstTime(conn);
            }
            sBuff.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

            ETSDatabaseManager dbManager = new ETSDatabaseManager();
			if (ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.ETS_ADMIN) 
             || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_OWNER)
             || ETSUtils.checkUserRole(es,proj.getProjectId()).equals(Defines.WORKSPACE_MANAGER)
             ) {
                // if the user is a work space manager or super workspace admin... changed for 4.4.1
                // show edit link
                sBuff.append("<tr><td headers=\"\" class=\"tblue\"height=\"18\" width=\"221\"><b>&nbsp;Quick links</b></td><td headers=\"\" class=\"tblue\"height=\"18\" width=\"222\" align=\"right\">");
                sBuff.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpading=\"0\"><tr valign=\"top\">");
                sBuff.append("<td width=\"16\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
                sBuff.append("<td width=\"30\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSQuickLinksEditServlet.wss?project_id="+projectId+"&tc=" + String.valueOf(iTopCat) + "\" class=\"fbox\">Edit</a></td>");
                sBuff.append("</tr></table>");
                sBuff.append("</td></tr></table>");
            } else {
                sBuff.append("<tr><td headers=\"\" class=\"tblue\"height=\"18\" width=\"443\"><b>&nbsp;Quick links</b></td></tr></table>");
            }

            sBuff.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\" width=\"443\">");
            sBuff.append("<tr>");
            sBuff.append("<td headers=\"\" valign=\"top\" width=\"100%\" style=\"background-color:#ccc\">");
            sBuff.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"background-color:#ffffff\">");
            sBuff.append("<tr valign=\"top\">");
            sBuff.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
            sBuff.append("</tr>");

            // get the quick links
            this.setUnionELinks(true);
            this.extractQuickLinks(conn);
            Vector displayLinks = new Vector();
            int numOfLinks = this.getLinkVect().size();

            for (int j=0; j<numOfLinks ; j++){
                   ETSQuickLink qkLnk = new ETSQuickLink();
                    qkLnk= this.getLink(j);
                    if (qkLnk.isShowLink()){
                        String count = EntitledStatic.getValue(conn,"select count(*) from ets.quick_links_access where project_id = '"+qkLnk.getProjectId()+"' and link_id='"+qkLnk.getLinkId()+"'");
                        if (Integer.parseInt(count)==0){
                            // no entitlement found - add to the list
                            displayLinks.add(qkLnk);
                        } else {
                            // there is an entitlement check
                            String entChk = EntitledStatic.getValue(conn,"select count(*) from ets.quick_links_access a, amt.s_user_access_view b where a.project_id = '"+qkLnk.getProjectId()+"' and a.link_id='"+qkLnk.getLinkId()+"' and a.entitlement = b.entitlement and b.userid = '"+es.gUSERN+"'");
                            if (Integer.parseInt(entChk)>0){
                                // user has entitlement.. add
                                displayLinks.add(qkLnk);
                            } else {
                                // do nothing
                            }
                        }
                    }
            }
                boolean greyline= true;
                for (int i = 0; i < displayLinks.size(); i++){
                    ETSQuickLink qkLnk = (ETSQuickLink)displayLinks.get(i);
                    String linkName = qkLnk.getLinkName();
                    String linkURL =  qkLnk.getLinkURL();

                    if (i%2==0){

                        if (i > 1) {
                            sBuff.append("<tr valign=\"top\">");
                            sBuff.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" width=\"441\" height=\"1\" /></td>");
                            sBuff.append("</tr>");
                            sBuff.append("<tr valign=\"top\">");
                            sBuff.append("<td colspan=\"5\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"3\" /></td>");
                            sBuff.append("</tr>");
                        }

                        sBuff.append("<tr valign=\"top\">");
                        greyline = true;
                    }
                    if (qkLnk != null && qkLnk.isShowLink()){
                        String target=""; String popIcon="";
                        if (qkLnk.isPopUp()){ target = " target=\"new\" "; popIcon="popup.gif";}
                        else {popIcon="fw.gif";}

                        sBuff.append("<td width=\"16\" height=\"21\" align=\"center\"><img src=\"" + Defines.ICON_ROOT + ""+popIcon+"\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></td>");
                        if (qkLnk.getLinkType().trim().equals("E")){
                            sBuff.append("<td width=\"202\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + linkURL+"\" "+target+" class=\"fbox\">"+linkName+"</a>");
                        } else if (qkLnk.getLinkType().trim().equals("E1")){
                            sBuff.append("<td width=\"202\" align=\"left\"><a href=\"" + linkURL+"\" "+target+" class=\"fbox\">"+linkName+"</a>");
                        } else if (qkLnk.getLinkType().trim().equals("E2")){
                            sBuff.append("<td width=\"202\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + linkURL+"?proj=" + projectId + "&tc=" + ETSUtils.getTopCatId(conn,projectId,3) + "&linkid=" + params.getLinkId() + "\" "+target+" class=\"fbox\">"+linkName+"</a>");
                        } else if (qkLnk.getLinkType().trim().equals("E4")){
                            sBuff.append("<td width=\"202\" align=\"left\"><a href=\"" + "/technologyconnect/" + linkURL+ "\" "+target+" class=\"fbox\">"+linkName+"</a>");
                        } else {
                            sBuff.append("<td width=\"202\" align=\"left\"><a href=\""+ linkURL+"\" "+target+" class=\"fbox\">"+linkName+"</a>");
                        }
                        // if link has userdoc - show it
                        if (qkLnk.hasUserDoc()){
                            String userDoc = qkLnk.getUserDoc();
                            if (userDoc.indexOf("{WEBCONF}") > 0) {
                                int iWebDocId = ETSDatabaseManager.getUserGuide(Defines.WEB_USERGUIDE,conn);
                                userDoc = userDoc.substring(0,userDoc.indexOf("{")) + String.valueOf(iWebDocId) + userDoc.substring(userDoc.indexOf("}") + 1,userDoc.length());
                            } else if (userDoc.indexOf("{DROPBOX}") > 0) {
                                int iDropDocId = ETSDatabaseManager.getUserGuide(Defines.DROP_USERGUIDE,conn);
                                userDoc = userDoc.substring(0,userDoc.indexOf("{")) + String.valueOf(iDropDocId) + userDoc.substring(userDoc.indexOf("}") + 1,userDoc.length());
                            }


                            sBuff.append("<br /><span class=\"small\">[<a href=\""+userDoc+"\" target=\"new\" class=\"fbox\" target=\"new\">User Guide</a>]</span>");
                        }
                        sBuff.append("</td>");
                        if (greyline){
                            sBuff.append("<td width=\"7\" align=\"center\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" width=\"1\" height=\"18\" alt=\"\" border=\"0\" /></td>");
                            greyline = false;
                        }
                    }

                    if (i%2!=0){
                        sBuff.append("</tr>");
                        // spacer

                    } else {
                        // logic to take care of odd number of links
                        if (i==numOfLinks){
                            sBuff.append("<td></td><td colspan=\"2\" width=\"218\"></td>");
                            sBuff.append("</tr>");
                        }
                    }
                }
            sBuff.append("</table>");
            sBuff.append("</td></tr></table>");
      } catch (SQLException sqlEx){
      } catch (Exception eX){
      }
        return sBuff.toString();
    }

    // private variables
    private Vector _linkVect;
    private String _linkType;
    private String _projectId;
    private String userId;
    boolean unionELinks;

    private boolean isUnionELinks() { return unionELinks;}


}
