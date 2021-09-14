package oem.edge.tools;

import java.util.ResourceBundle;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.MissingResourceException;

public class PftResourceBundle extends ResourceBundle {
	public static final String VERSION = "1.2";
	private Hashtable htbl = new Hashtable();
	
	public void setProperty(String key, String value) {
		System.out.println("setProperty("+key+", "+value+")");
		htbl.put(key, value);
	}
	
	public Enumeration getKeys() {
		System.out.println("getKeys()");
		return htbl.keys();
	}
	
	public Object handleGetObject(String key) throws MissingResourceException {
		System.out.println("handleGetObject("+key+")");
		if (key == null)
			throw new NullPointerException();
		Object o = htbl.get(key);
		if (o == null)
			throw new MissingResourceException("MissingResource", "oem.edge.tools.PtfResourceBundle", key);
		return o;
	}
	
	public static void main(String args[]) {
		try {
			ResourceBundle rb = new PftResourceBundle();
			((PftResourceBundle) rb).setProperty("key", "value");
			String s = rb.getString("key");
			System.out.println("s=>"+s);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}

