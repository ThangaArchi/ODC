/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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
package oem.edge.ed.sd;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/************************** EOF : HEADER *************************************/
/////////////////////////////////////////////////////////////////////////////
//
//                            Edge 3.1.1
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////


public class SdWriter {

    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    final static String version = "v070202.1: ";

    String[] userInfo;
    String fileOwner, remoteAddr, id, orderID, isDSClient, email;
    long length, skipBytes, totalBytesRead, startXfer;

    SdWriter(String[] userInfo, String fileOwner, String remoteAddr, String id, String orderID, long length, long skipBytes, String isDSClient, String email, long startXfer) {

	this.userInfo = userInfo;
	this.fileOwner = fileOwner;
	this.remoteAddr = remoteAddr;
	this.id = id;
	this.orderID = orderID;
	this.length = length;
	this.skipBytes = skipBytes;
	this.isDSClient = isDSClient;
	this.email = email;
	this.startXfer = startXfer;

    }

}

