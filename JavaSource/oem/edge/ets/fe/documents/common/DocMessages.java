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

package oem.edge.ets.fe.documents.common;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author v2srikau
 */
public class DocMessages {
    
    private static Properties m_pdDocMessages;

    /**
     * Private constructor
     */
    private DocMessages() {
        
    }
    
    /**
     * @param strMessageKey
     * @return
     */
    public static String getMessage(String strMessageKey) {
        String strMessage = null;
        if (m_pdDocMessages == null) {
            m_pdDocMessages = new Properties();
            ResourceBundle rb = 
                ResourceBundle.getBundle("oem.edge.ets.fe.DocumentMessages");
            Enumeration keys = rb.getKeys();
            while (keys.hasMoreElements()) {
                String strKey = (String) keys.nextElement();
                String strValue = rb.getString(strKey);
                m_pdDocMessages.setProperty(strKey, strValue);
            }
        }

        strMessage = m_pdDocMessages.getProperty(strMessageKey);
        
        return strMessage;
    }
}
