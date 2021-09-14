package oem.edge.ets_pmo.util;

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
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

public class GenerationSructOfCRIssue {
	private static String CLASS_VERSION = "4.5.1";
/* To be used for updating new issues and crs */
	private String GenerationParentID;
	private String GenerationParentType;
	

	/**
	 * Returns the generationParentID.
	 * @return String
	 */
	public String getGenerationParentID() {
		return GenerationParentID;
	}

	/**
	 * Returns the generationParentType.
	 * @return String
	 */
	public String getGenerationParentType() {
		return GenerationParentType;
	}

	/**
	 * Sets the generationParentID.
	 * @param generationParentID The generationParentID to set
	 */
	public void setGenerationParentID(String generationParentID) {
		GenerationParentID = generationParentID;
	}

	/**
	 * Sets the generationParentType.
	 * @param generationParentType The generationParentType to set
	 */
	public void setGenerationParentType(String generationParentType) {
		GenerationParentType = generationParentType;
	}


	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
