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
package oem.edge.authentication;

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;
import org.apache.commons.logging.*;

/**
 * @author bjr
 */
public class PoolableDirContextFactory extends BaseKeyedPoolableObjectFactory {
	public final static String VERSION = "1.4";
	private static Log log = LogFactory.getLog(PoolableDirContextFactory.class);
	private DirContextPool pool = null;

	public PoolableDirContextFactory(DirContextPool pool) {
		this.pool = pool;
	}
		
	public void destroyObject(Object key, Object o) {
		log.debug("destroyObject("+o+")");
		if (o == null) {
			log.info("Tried to destroy null");
			return;
		}
		DirContext ctx = null;
		if (o instanceof DirContext) {
			ctx = (DirContext) o;
		} else {
			log.warn("Tried to destroy "+o.getClass());
			return;
		}
		try {
			ctx.close();
		} catch (Exception x) {
			log.error(x);
		}
	}
	
	public Object makeObject(Object key) throws Exception {
		log.debug("makeObject()");
		DirContext ctx = null;
		Hashtable env = new Hashtable();
		LDAPAuthenticatorConfiguration config = DirContextPool.getConfig(key);
		try {
			ctx = new InitialDirContext(config.getEnv()); // env);
		} catch (Exception x) {
			throw new DirContextPoolException("Exception in makeObject("+key+")", x);
		}		
		return ctx;
	}
		
}
