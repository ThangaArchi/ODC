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
 * FeedbackFlavor is a drag/drop data format. It allows the drop target to
 * communicate the target location back to the drag source. This allows the
 * drag source to manage the data transfer. Normally, the drop target manages
 * the transfer by getting the data from the transferable. For some DSMP clients,
 * the data resides on remote servers and is not readily available. The drop target
 * sends the destination back by calling sendBack. This allows the drag source to
 * initiate the appropriate downloads.
 */
public class FeedbackFlavor {
	private FeedbackFlavorHandler handler = null;
	private Object dragSource = null;
	public FeedbackFlavor(Object dragSource, FeedbackFlavorHandler h) {
		this.dragSource = dragSource;
		this.handler = h;
	}
	public void sendBack(Object data) {
		handler.dropOnTarget(data,dragSource);
	}
}
