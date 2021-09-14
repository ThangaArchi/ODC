package oem.edge.ed.odc.dropbox.client;

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

public class DropBoxPnlEvent {
	static public int TITLE = 0;
	static public int SHOW = 1;
	static public int SAVE_PREFERENCES = 2;
	static public int EXIT = 3;
	static public int HIDE = 4;

	public String title;
	public int eventType;

	public DropBoxPnlEvent(String title) {
		this.eventType = TITLE;
		this.title = title;
	}
	public DropBoxPnlEvent(int eventType) {
		this.eventType = eventType;
	}

	public boolean isTitle() {
		return eventType == TITLE;
	}
	public boolean isHide() {
		return eventType == HIDE;
	}
	public boolean isShow() {
		return eventType == SHOW;
	}
	public boolean isSavePreferences() {
		return eventType == SAVE_PREFERENCES;
	}
	public boolean isExit() {
		return eventType == EXIT;
	}
	
	public String getTitle() {
		return title;
	}
}
