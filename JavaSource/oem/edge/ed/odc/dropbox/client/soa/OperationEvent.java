package oem.edge.ed.odc.dropbox.client.soa;

/**
 * Insert the type's description here.
 * Creation date: (10/29/2002 1:09:19 PM)
 * @author: Mike Zarnick
 */
public class OperationEvent {
	static public int DATA = 0;
	static public int ENDED = 1;
	static public int MD5 = 2;
	public Operation source;
	public int eventType;

	public OperationEvent(int type, Operation source) {
		this.eventType = type;
		this.source = source;
	}

	public boolean isEnded() {
		return eventType == ENDED;
	}
	public boolean isData() {
		return eventType == DATA;
	}
	public boolean isMD5() {
		return eventType == MD5;
	}
	
	public Operation getSource() {
		return source;
	}
}
