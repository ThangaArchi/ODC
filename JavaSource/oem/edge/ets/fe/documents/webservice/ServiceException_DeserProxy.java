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

package oem.edge.ets.fe.documents.webservice;

/**
 * @author v2srikau
 */
public class ServiceException_DeserProxy
	extends java.lang.Exception
	implements java.io.Serializable {
	
	private java.lang.String message;

	private transient java.lang.ThreadLocal __history;
	
	private transient java.lang.ThreadLocal __hashHistory;
	
	/**
	 * Constructor 
	 */
	public ServiceException_DeserProxy() {
	}

	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	public java.lang.String getMessage() {
		return message;
	}

	/**
	 * @param message
	 */
	public void setMessage(java.lang.String message) {
		this.message = message;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(java.lang.Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		ServiceException_DeserProxy other = (ServiceException_DeserProxy) obj;
		boolean _equals;
		_equals =
			true
				&& ((this.message == null && other.getMessage() == null)
					|| (this.message != null
						&& this.message.equals(other.getMessage())));
		if (!_equals) {
			return false;
		}
		if (__history == null) {
			synchronized (this) {
				if (__history == null) {
					__history = new java.lang.ThreadLocal();
				}
			}
		}
		ServiceException_DeserProxy history =
			(ServiceException_DeserProxy) __history.get();
		if (history != null) {
			return (history == obj);
		}
		if (this == obj)
			return true;
		__history.set(obj);
		__history.set(null);
		return true;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (__hashHistory == null) {
			synchronized (this) {
				if (__hashHistory == null) {
					__hashHistory = new java.lang.ThreadLocal();
				}
			}
		}
		ServiceException_DeserProxy history =
			(ServiceException_DeserProxy) __hashHistory.get();
		if (history != null) {
			return 0;
		}
		__hashHistory.set(this);
		int _hashCode = 1;
		if (getMessage() != null) {
			_hashCode += getMessage().hashCode();
		}
		__hashHistory.set(null);
		return _hashCode;
	}

	/**
	 * @return
	 */
	public java.lang.Object convert() {
		return new oem.edge.ets.fe.documents.webservice.ServiceException(
			getMessage());
	}
}
