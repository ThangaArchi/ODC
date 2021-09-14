/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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
package oem.edge.ets_pmo.domain;
/**
 * @author shingte
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransObject {
	String id;
	String version;
	String source;
	String destination;
	String app;
	String timestamp;
	OperObject operation;
	
	/**
	 * @return Returns the operation.
	 */
	public OperObject getOperation() {
		return operation;
	}
	/**
	 * @param operation The operation to set.
	 */
	public void setOperation(OperObject operation) {
		this.operation = operation;
	}
	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * @return Returns the app.
	 */
	public String getApp() {
		return app;
	}
	/**
	 * @param app The app to set.
	 */
	public void setApp(String app) {
		this.app = app;
	}
	/**
	 * @return Returns the destination.
	 */
	public String getDestination() {
		return destination;
	}
	/**
	 * @param destination The destination to set.
	 */
	public void setDestination(String destination) {
		this.destination = destination;
	}
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return Returns the source.
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source The source to set.
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	
	public String getTimestamp() {
		return timestamp;
	}
	/**
	 * @param finish_start_date The finish_start_date to set.
	 */
	public void setTimestamp(String str) {
		this.timestamp = str; //Helper.toTimestamp(str);
	}
	
	public String toString() {
		return new StringBuffer()
				.append("\n  source="+this.source)
				.append("\n  id="+this.id)
				.append("\n  destination="+this.destination)
				.append("\n  version="+this.version)
				.append("\n  app="+this.app)
				.append("\n  operation="+this.operation)
				.toString();
	}
}
