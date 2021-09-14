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

package oem.edge.ets.fe.dealtracker;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * @author v2srikau
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class UploadTaskForm extends ActionForm {
    
    public String linkid;
    public String cc;
    public String tc;
    public String proj;
    
    private FormFile taskList;

    /**
     * @return Returns the cc.
     */
    public String getCc() {
        return cc;
    }
    /**
     * @param cc The cc to set.
     */
    public void setCc(String cc) {
        this.cc = cc;
    }
    /**
     * @return Returns the linkid.
     */
    public String getLinkid() {
        return linkid;
    }
    /**
     * @param linkid The linkid to set.
     */
    public void setLinkid(String linkid) {
        this.linkid = linkid;
    }
    /**
     * @return Returns the proj.
     */
    public String getProj() {
        return proj;
    }
    /**
     * @param proj The proj to set.
     */
    public void setProj(String proj) {
        this.proj = proj;
    }
    /**
     * @return Returns the tc.
     */
    public String getTc() {
        return tc;
    }
    /**
     * @param tc The tc to set.
     */
    public void setTc(String tc) {
        this.tc = tc;
    }
    
    public FormFile getTaskList() {
        return taskList;
    }
    
    public void setTaskList(FormFile taskList) {
        this.taskList = taskList;
    }
}
