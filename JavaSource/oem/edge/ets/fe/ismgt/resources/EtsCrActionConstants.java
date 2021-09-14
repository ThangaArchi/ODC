/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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

/*
 * Created on May 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.ismgt.resources;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface EtsCrActionConstants {

	public static final int FATALERROR = -1;

	///MAINPAGE

	public static final int MAINPAGE = 50;

	//DEFAULT 1 PAGE
	public static final int NEWINITIAL = 1;

	//CONTINUE AND CANCEL 1ST PAGE
	public static final int CONTDESCR = 11; //CONT DESCR

	public static final int CANCDESCR = 12; //CANC DESCR

	public static final int VALIDERRCONTDESCR = 111; // VALIDATION ERROR CONT DESCR

	//EDIT,CONTINUE AND CANCEL 1ST PAGE
	public static final int EDITDESCR = 13; //EDIT ISSUE DESCRIPTION

	//DEFAULT FILE ATTACH
	public static final int FILEATTACH = 18;
	public static final int DELETEFILE = 19;

	//ADD FILE ATTACH
	public static final int ADDFILEATTACH = 20;
	public static final int EDITFILEATTACH = 21;
	public static final int CANCFILEATTACH = 123;

	//submit to db
	public static final int SUBMITTODB = 26;
	public static final int CANCELSUBMITTODB = 126;

	//
	public static final int ERRINACTION = 40;

	//	//view issues constants
	public static final int VIEWCRDETS = 60;
	public static final int VIEWCRRTF = 61;
	
	//add comments
	public static final int COMMENTSCRFIRSTPAGE = 500;
	public static final int COMMENTSCRSUBMITTODB = 526;
	public static final int COMMENTSCRCANCELDB = 527;
	public static final int COMMENTSCRCONFIRMPAGE = 528;
	//	DEFAULT FILE ATTACH
	public static final int COMMENTSCREDITFILEATTACH = 521; //file attach from view
	public static final int COMMENTSCRFILEATTACH = 518;
	public static final int COMMENTSCRDELETEFILE = 519;
	public static final int COMMENTSCRSUBTFILEATTACH = 520;
	public static final int COMMENTSCRCANCFILEATTACH = 5123;
	public static final int FILEATTACHSCRCONFIRMPAGE = 522;
	
	
	

}
