/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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

import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsOpModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface WrkSpcTeamMgrIF {
	
	//////////actions of WO/WM/SUPER ADMIN on Team 
	public WrkSpcTeamActionsOpModel addMemberToWrkSpc(WrkSpcTeamActionsInpModel actInpModel);
	public WrkSpcTeamActionsOpModel delMemberFromWrkSpc(WrkSpcTeamActionsInpModel actInpModel);
	public WrkSpcTeamActionsOpModel invMemberToWrkSpc(WrkSpcTeamActionsInpModel actInpModel);
	public WrkSpcTeamActionsOpModel editMemberOnWrkSpc(WrkSpcTeamActionsInpModel actInpModel);
	public WrkSpcTeamActionsOpModel chgWrkSpcPrimContact(WrkSpcTeamActionsInpModel actInpModel);
	
	//actions of Member on Team
	public WrkSpcTeamActionsOpModel requestAddMemberToWrkSpc(WrkSpcTeamActionsInpModel actInpModel);
	public WrkSpcTeamActionsOpModel requestDelMemberFromWrkSpc(WrkSpcTeamActionsInpModel actInpModel);
	
	
	

}
