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
package oem.edge.ets_pmo.mq;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import com.ibm.mq.MQChannelExit;
import com.ibm.mq.MQSecurityExit;


public class SecurityExit implements MQSecurityExit
{

public SecurityExit()
{
    this("");
}

public SecurityExit(String secString)
{
    verbose = false;
    userName = null;
    hostName = null;
}

private String decryptString(String stringVar)
{
    return null;
}

private String encryptString(String stringVar)
{
    int localKey = 24425;
    char orgBytes[] = stringVar.toCharArray();
    char newBytes[] = stringVar.toCharArray();
    for(int i = 0; i < newBytes.length; i++)
    {
        newBytes[i] ^= localKey >> 8;
        localKey = (orgBytes[i] + localKey) * 52845 + 22719;
    }

    if(verbose)
    {
        System.out.println("orgBytes = " + String.copyValueOf(orgBytes));
        System.out.println("newBytes = " + new String(newBytes));
    }
    return String.copyValueOf(newBytes);
}


public byte[] securityExit(com.ibm.mq.MQChannelExit arg1,
        com.ibm.mq.MQChannelDefinition arg2, byte[] arg3) {

    switch (arg1.exitReason) {
        case MQChannelExit.MQXR_INIT :
            if (verbose) {
                System.out.println("channel exit: Channel INIT state");
            }

            // get user name
            userName = System.getProperty("user.name");
		    //userName = "iccadm";
            // get host name
            // hostName = exitProps.getProperty("MQSeries.Host.Name","junkHost");
            try
	        {
	            InetAddress addr = InetAddress.getLocalHost();
	            hostName = addr.getHostName();
	        }
	        catch(UnknownHostException _ex)
	        {
	            hostName = "unknown hostname";
	        }
            if (verbose) {
                System.out.println("channel exit: user name = " + userName);
                System.out.println("channel exit: host name = " + hostName);
            }
            break;

        case MQChannelExit.MQXR_INIT_SEC :
            if (verbose) {
                System.out.println("channel exit: Channel INIT_SEC state");
            }
            break;

        case MQChannelExit.MQXR_SEC_MSG :
            if (verbose) {
                System.out.println("channel exit: Channel SEC_MSG state");
                System.out.println("channel exit: rcvd msg = " + new String(arg3));
            }

            arg1.exitResponse = MQChannelExit.MQXCC_SEND_SEC_MSG;
            //return encryptString(userName).getBytes();
            return userName.getBytes();
            //return arg3;
        case MQChannelExit.MQXR_TERM :
            if (verbose) {
                System.out.println("channel exit: Channel TERM state");
            }
            break;
    }

    // conditions that fall through return OK and no data
    arg1.exitResponse = MQChannelExit.MQXCC_OK;
    return null;
}
/**
* This method was created in VisualAge.
* @param verbosity boolean
*/
private void setVerbose(String verbosity) {
    verbose = Boolean.valueOf(verbosity).booleanValue();
}


public String toString()
{
    return "GWA MQSeries Channel Security Exit version " + getVersion();
}

public String getVersion()
{
    return "1.0.2";
}

static final String description = "GWA MQSeries Channel Security Exit";
static final String version = "1.0.2";
private final int C1 = 52845;
private final int C2 = 22719;
private final int key = 24425;
private boolean verbose;
private String userName;
private String hostName;
private Properties exitProps;

}