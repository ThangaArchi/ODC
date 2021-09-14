/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2005                                     */
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
package oem.edge.ets.fe.common;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.common.RSA.RSAKeyPair;
import oem.edge.common.cipher.ODCipherData;
import oem.edge.common.cipher.ODCipherRSA;
import oem.edge.common.cipher.ODCipherRSAFactory;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import org.apache.commons.logging.Log;

import com.ibm.as400.webaccess.common.ConfigObject;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EncodeUtils implements EtsIssFilterConstants {

	private static Log logger = EtsLogger.getLogger(EncodeUtils.class);
	public static final String VERSION = "1.3";

	/**
	 *
	 */
	public EncodeUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param strDocId
	 * @param strProjectId
	 * @param strEdgeId
	 * @param strTopCatId
	 * @param strCurrCatId
	 * @return
	 * @throws IOException
	 */
	public static String encode(String strDocId, String strProjectId, String strEdgeId, String strTopCatId, String strCurrCatId) throws Exception {

		if (!Global.loaded) {
			Global.Init();
		}

        //RSAKeyPair keypair = RSAKeyPair.load(Global.encode_keypath + "boulder.key");

		//ODCipherRSA edgecipher = new ODCipherRSA(keypair);
		ODCipherRSA edgecipher = null;

		ODCipherRSAFactory fac = ODCipherRSAFactory.newFactoryInstance();
		try {
			edgecipher = fac.newInstance(Global.encode_keypath + "boulder.key");
		} catch(Throwable t) {
			System.out.println("Error loading CipherFile! [" + Global.encode_keypath + "boulder.key" + "]");
		}

		ConfigObject pdConfigObject = new ConfigObject();
		pdConfigObject.setProperty("DOCID", strDocId);
		pdConfigObject.setProperty("PROJID", strProjectId);
		pdConfigObject.setProperty("TOPCATID", strTopCatId);
		pdConfigObject.setProperty("CURCATID", strCurrCatId);
		pdConfigObject.setProperty("EDGEID", strEdgeId);

		String sToEncode = pdConfigObject.toString();

		ResourceBundle pdResources = ResourceBundle.getBundle("oem.edge.ets.fe.ets-itar");
		String strTokenExpiry = pdResources.getString("ets.doc.itar.token.expiry");

		ODCipherData cipherdata = edgecipher.encode(60 * Integer.parseInt(strTokenExpiry), sToEncode);

		String sEncodedString = cipherdata.getExportString();

		return sEncodedString;
	}

	public static String getITARDocUploadPath() {

		String strPostAction = "";

		try {

			ResourceBundle pdResources = ResourceBundle.getBundle("oem.edge.ets.fe.ets-itar");
			String strBTV = AmtCommonUtils.getTrimStr(pdResources.getString("ets.doc.btv.server"));

			strPostAction = strBTV + "addDocFilesITAR.wss";

		} catch (MissingResourceException mrEx) {

			AmtCommonUtils.LogGenExpMsg(mrEx, "General Exception in building EncodeUtils", ETSLSTUSR);

			if (mrEx != null) {
				SysLog.log(SysLog.ERR, "EncodeUtils", mrEx);
				mrEx.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building EncodeUtils", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, "EncodeUtils", ex);
				ex.printStackTrace();

			}

		}

		return strPostAction;

	}

} //end of class
