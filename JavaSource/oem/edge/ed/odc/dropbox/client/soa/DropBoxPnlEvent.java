package oem.edge.ed.odc.dropbox.client.soa;

/**
 * Insert the type's description here.
 * Creation date: (10/29/2002 1:09:19 PM)
 * @author: Mike Zarnick
 */
public class DropBoxPnlEvent {
	static public int TITLE = 0;
	static public int SHOW = 1;
	static public int SAVE_PREFERENCES = 2;
	static public int EXIT = 3;

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
	public boolean isReadyToShow() {
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
