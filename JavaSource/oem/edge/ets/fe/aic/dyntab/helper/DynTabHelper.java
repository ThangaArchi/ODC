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

package oem.edge.ets.fe.aic.dyntab.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.aic.dyntab.vo.AICTableDataLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.BaseDocumentForm;

import org.apache.commons.logging.Log;


/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DynTabHelper {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
		private static final String CLASS_VERSION = "1.3";
		private static Log logger =
						EtsLogger.getLogger(DynTabHelper.class);
	
	public static HttpServletRequest  setAttributesForDocumentTAB(HttpServletRequest request,BaseDocumentForm form)
	{				
		if (logger.isInfoEnabled()) {
			logger.info("-> setAttributesForDocumentTAB");
		}	
		request.setAttribute("proj",form.getProj());
		request.setAttribute("tc",form.getTc());
		request.setAttribute("linkid",form.getLinkid());
		request.setAttribute("docid",form.getDocid());
		request.setAttribute("cc",form.getCc());
		if (logger.isInfoEnabled()) {
			logger.info("<- setAttributesForDocumentTAB");
		}
		return request;
	}
	
	public static StringBuffer getCSVResults(AICTemplateVO objAICTemplateVO, AICTableVO objAICTableVO)
	{
		StringBuffer buf = new StringBuffer();
		Collection collTemplate = objAICTemplateVO.getTemplateColVOCollection();
		Iterator itrTemplate = collTemplate.iterator();
		AICTemplateColumnVO objAICTemplateColumnVO = null;
		int intColumnSize = collTemplate.size();
		int intIncr = 0;
		while(itrTemplate.hasNext())
		{
			intIncr ++;
			objAICTemplateColumnVO = (AICTemplateColumnVO)itrTemplate.next();
			buf.append(objAICTemplateColumnVO.getColumnName());
			if(intIncr != intColumnSize)
			{
				buf.append(",");
			}
						
		}
		
		buf.append("\n");
		
		
		Collection rowList = objAICTableVO.getAICTableRowsDataCollection();
		List resultList = convertDataToRow((List)rowList);
		Iterator itrRow = resultList.iterator();
		int intRowSize = resultList.size();
		int intIncrRow = 0;
		
		AICTableRowLineItemVO objAICTableRowLineItemVO = null;
		while(itrRow.hasNext())
		{
			intIncrRow++;
			objAICTableRowLineItemVO = (AICTableRowLineItemVO)itrRow.next();
			AICTableDataLineItemVO[] arrayAICTableDataLineItemVO= objAICTableRowLineItemVO.getDataList();
			for(int i=0;i<arrayAICTableDataLineItemVO.length;i++)
			{
				AICTableDataLineItemVO objAICTableDataLineItemVO = arrayAICTableDataLineItemVO[i];
				AICTableRowsDataVO objAICTableRowsDataVO= objAICTableDataLineItemVO.getAICTableRowsDataVO();
				buf.append(objAICTableRowsDataVO.getDataValue());
				if(i != arrayAICTableDataLineItemVO.length)
				{
					buf.append(",");
				}
			}
			if(intIncrRow != intRowSize)
			{			
				buf.append("\n");
			}
			
		}
					
		return buf;
	}
	
	public static List convertDataToRow(List rowList)
	{
		Iterator iterRow = rowList.iterator();
				
		AICTableRowsDataVO objAICTableRowsDataVO = null;
		AICTableRowsDataVO objAICTableRowsDataVOInside = null;
		
		HashMap hm = new HashMap();
		
		List singleRow = null;
		List actualRowList = new ArrayList();
		
		while(iterRow.hasNext())
		{
			objAICTableRowsDataVO = (AICTableRowsDataVO)iterRow.next();
			String strRowId = objAICTableRowsDataVO.getRowId();
			singleRow = new ArrayList();
			AICTableRowLineItemVO objAICTableRowLineItemVO = new AICTableRowLineItemVO();
			if(!hm.containsKey(strRowId))
			{			
				Iterator iterRowInside = rowList.iterator();			
				while(iterRowInside.hasNext())
				{
					objAICTableRowsDataVOInside = (AICTableRowsDataVO)iterRowInside.next();
					String strRowIdInside = objAICTableRowsDataVOInside.getRowId();
					AICTableDataLineItemVO objAICTableDataLineItemVO = new AICTableDataLineItemVO();
					if(strRowId.equals(strRowIdInside))
					{
						
						objAICTableDataLineItemVO.setAICTableRowsDataVO(objAICTableRowsDataVOInside);
						singleRow.add(objAICTableDataLineItemVO);
						hm.put(strRowIdInside,strRowIdInside);					
					}
						
				}
				objAICTableRowLineItemVO.setDataList(singleRow);
				actualRowList.add(objAICTableRowLineItemVO);
			}
			
		}
		
		List newRowList = new ArrayList(actualRowList.size());

		Iterator iter = actualRowList.iterator();
		while (iter.hasNext())
		{
			AICTableRowLineItemVO objAICTableRowLineItemVO = (AICTableRowLineItemVO) iter.next();
			newRowList.add(objAICTableRowLineItemVO);
		}
		
		return newRowList;
	}
		

}
