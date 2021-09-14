package oem.edge.ed.sd.mq;

import com.ibm.mq.*;
import java.net.*;
import java.util.*;
import java.io.*;

public class SecurityExit implements com.ibm.mq.MQSecurityExit {
   
    private boolean verbose = false;
    private String userName = null;
   
    /**
    * SecurityExit constructor comment.
    */
    public SecurityExit() {
	super();



	    if (verbose) {
		System.out.println("channel exit: verbose = " + verbose);
	    }

    }

    /**
    * This method was created in VisualAge.
    * @return java.lang.String
    * @param stringVar java.lang.String
    */
    private String decryptString(String stringVar) {
	return null;
    }

    /**
    * This method was created in VisualAge.
    * @return java.lang.String
    * @param stringVar java.lang.String
    */
    private String encryptString(String stringVar) {
	return stringVar;
    }

    /**
    * securityExit method comment.
    */
    public byte[] securityExit(com.ibm.mq.MQChannelExit arg1,
	    com.ibm.mq.MQChannelDefinition arg2, byte[] arg3) {

	switch (arg1.exitReason) {
	    case MQChannelExit.MQXR_INIT :
		if (verbose) {
		    System.out.println("channel exit: Channel INIT state");
		}

                userName = System.getProperty("user.name");
		if (verbose) {
		    System.out.println("channel exit: user name = "+userName);
		  
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
}
