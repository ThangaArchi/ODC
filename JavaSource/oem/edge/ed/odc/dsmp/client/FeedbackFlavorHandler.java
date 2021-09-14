package oem.edge.ed.odc.dsmp.client;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
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
 * A FeedbackFlavorHandler is the drag source object that is called when the user
 * completes the drop operation. Normally, the drag source provides the data
 * to the drop target by way of a Transferable. With some of our DSMP clients,
 * this technique is not possible. For example, the Dropbox GUI can't provide
 * the data for files that exist on the dropbox server. Normally the Transferable
 * would execute the download of the data, but the current APIs make this awkward.
 * So, the drop target accepts the data type of FeedbackFlavor and feeds the
 * drop target location back to the drop source by way of the dropOnTarget method.
 */
public interface FeedbackFlavorHandler {
	public void dropOnTarget(Object data, Object source);
}
