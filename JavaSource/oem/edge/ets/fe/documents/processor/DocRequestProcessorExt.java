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

package oem.edge.ets.fe.documents.processor;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.tiles.TilesRequestProcessor;

/**
 * Custom Request Processor Plugin for the Documents Module
 * @author v2srikau
 */
public class DocRequestProcessorExt extends TilesRequestProcessor {

	/** Stores the Logger object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DocRequestProcessorExt.class);

}
