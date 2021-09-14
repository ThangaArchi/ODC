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

package oem.edge.ets.fe.ismgt.middleware;

import java.util.List;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;

/**
 * @author V2PHANI
 */
public class EmailRuleFilter {

	public static final String VERSION = "1.2";

	public EmailRuleFilter() {
		super();
		
	}

	/**
	 * The rule is remove the submitter/owners from the notifylist,as they will be notified anyway
	 */

	public static String filterNotifyListStr(String submitterEmail, String ownerEmailStr, String etsCCListStr) {

		List ownerList = EtsIssFilterUtils.getArrayListFromStringTok(ownerEmailStr, ",");
		List etsNotifyList = EtsIssFilterUtils.getArrayListFromStringTok(etsCCListStr, ",");
		int subsize = 0;
		String tempId = "";
		
		//remove submitter from notify list, if any
		try{	
			if (etsNotifyList != null && !etsNotifyList.isEmpty()) {
				for (int i = 0; i < etsNotifyList.size(); i++) { 
					tempId = AmtCommonUtils.getTrimStr((String) etsNotifyList.get(i));	
					if (tempId.equals(submitterEmail)) {
						etsNotifyList.remove(tempId); 
						i--; 
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();			
		}
		
		//remove owner from notify list, if any
		try{
		if (etsNotifyList != null && !etsNotifyList.isEmpty()) {
			for (int k = 0; k < etsNotifyList.size(); k++) {
				tempId = AmtCommonUtils.getTrimStr((String) etsNotifyList.get(k));
				if (ownerList.contains(tempId)) {
					etsNotifyList.remove(tempId);
					k--;
				}
			}
		}
		}catch(Exception e)
		{
			e.printStackTrace();			
		}

		String updatedNotifyStr = EtsIssFilterUtils.getCommSepStrFromStrList(etsNotifyList);

		return updatedNotifyStr;

	}

	public static List filterNotifyList(String submitterEmail, String ownerEmailStr, String etsCCListStr) {

		List ownerList = EtsIssFilterUtils.getArrayListFromStringTok(ownerEmailStr, ",");

		List etsNotifyList = EtsIssFilterUtils.getArrayListFromStringTok(etsCCListStr, ",");

		//delete submitter/owner frm notify list, if they put their ids once again

		String tempId = "";
		try{

			if (etsNotifyList != null && !etsNotifyList.isEmpty()) {
	
				for (int i = 0; i < etsNotifyList.size(); i++) {
	
					tempId = AmtCommonUtils.getTrimStr((String) etsNotifyList.get(i));
	
					if (tempId.equals(submitterEmail)) {
	
						etsNotifyList.remove(tempId);
						i--;
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();					
		}

		try{

			if (etsNotifyList != null && !etsNotifyList.isEmpty()) {
	
				for (int k = 0; k < etsNotifyList.size(); k++) {
	
					tempId = AmtCommonUtils.getTrimStr((String) etsNotifyList.get(k));
	
					if (ownerList.contains(tempId)) {
						etsNotifyList.remove(tempId);
						k--;
					}
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();						
		}
		
		return etsNotifyList;
	}

	public static String filterSubscriptionList(String filtNotifyListStr, String subscrListStr,String submitterEmail, String ownerEmailStr) {
		
		List filtNotifyList = EtsIssFilterUtils.getArrayListFromStringTok(filtNotifyListStr, ",");
		List subscrList = EtsIssFilterUtils.getArrayListFromStringTok(subscrListStr, ",");
		String tempId = "";
		
		// v2sagar
		// remove submitter from notify list, if any
		try{
			if (subscrList != null && !subscrList.isEmpty()) {
	
				for (int i = 0; i < subscrList.size(); i++) {
	
					tempId = AmtCommonUtils.getTrimStr((String) subscrList.get(i));//bug here v2sagar
	
					if (filtNotifyList.contains(tempId)) {
	
						subscrList.remove(tempId);
						i--;
	
					}
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();			
		}
		//get comma sep string
		String updatedSubscrStr = EtsIssFilterUtils.getCommSepStrFromStrList(subscrList);
		
		//exclude submiiter, owner from subscription list
		
		return filterNotifyListStr(submitterEmail, ownerEmailStr, updatedSubscrStr);
		}

}