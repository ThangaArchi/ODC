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
 * File: ETSQuickLink.java
 *
 */
public class ETSQuickLink {
    public ETSQuickLink(){
        _projectId = "";
        _linkId = "";
        _linkName = "";
        _linkURL = "";
        _linkType = "";
        _linkSeq = "";
        _userDoc = "";
        _isPopUp = false;
        _linkShow = true;
        _lastUser = "";
        _lastTime = "current timestamp";
    }

    public void setLinkValues(String projectId,
                            String linkId, String linkName,String linkURL,
                            String linkType, String linkSeq, String userDoc,
                            String linkShow,
                            String lastUser, String lastTime) {
        this.setProjectId(projectId);
        this.setLinkId(linkId);
        this.setLinkName(linkName);
        this.setLinkURL(linkURL);
        this.setLinkType(linkType);
        this.setLinkSeq(linkSeq);
        this.setUserDoc(userDoc);
        this.setShowLink(linkShow);
        this.setLastUser(lastUser);
        this.setLastTime(lastTime);
    }
    // decession methods
    public boolean isEditable(){return (_linkType.equals("E"))?true:false;}
    public boolean isShowLink(){  return _linkShow;   }
    public boolean isPopUp() {return _isPopUp;}
    public boolean hasUserDoc(){
        return (_userDoc==null||_userDoc.equals(""))?false:true;
    }

    // access methods
    public void setProjectId(String projectId){_projectId = projectId;}
    public String getProjectId(){return _projectId;}

    public void setLinkId(String linkId){_linkId = linkId;}
    public String getLinkId() {return _linkId;}

    public void setLinkName(String linkName){_linkName = linkName;}
    public String getLinkName(){return _linkName;}

    public void setLinkURL (String linkURL) {_linkURL = linkURL;}
    public String getLinkURL () {return _linkURL;}

    public void setLinkType (String linkType) {_linkType = linkType;}
    public String getLinkType () {return _linkType;}

    public void setLinkSeq (String linkSeq) {_linkSeq = linkSeq;}
    public String getLinkSeq () {return _linkSeq;}

    public void setUserDoc (String userDoc) {_userDoc = userDoc;}
    public String getUserDoc () {return _userDoc;}

    public void setShowLink(boolean show){_linkShow =show;}
    public void setShowLink(String show){ _linkShow = show.equals("Y")?true:false;  }

    public void setLastUser (String lastUser) {_lastUser = lastUser;}
    public String getLastUser(){ return _lastUser;}

    public void setLastTime (String lastTime) {_lastTime = lastTime;}
    public void setLastTime () { this.setLastTime("current timestamp");}
    public String getLastTime(){ return _lastTime;}

    public void setPopUp(boolean popUp) {_isPopUp = popUp;}
    public void setPopUp(String popup){ _isPopUp = popup.equals("Y")?true:false;  }


    // private variables
    String _projectId ;
    String _linkId ;
    String _linkName ;
    String _linkURL;
    String _linkType;
    String _linkSeq;
    String _userDoc;
    boolean _linkShow;
    String _lastUser;
    String _lastTime;
    boolean _isPopUp;
}
