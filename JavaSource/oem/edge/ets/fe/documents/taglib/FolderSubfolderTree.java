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

package oem.edge.ets.fe.documents.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.documents.BaseDocumentForm;
import oem.edge.ets.fe.documents.DocumentsHelper;
import oem.edge.ets.fe.documents.common.StringUtil;

/**
 * @author v2srikau
 */
public class FolderSubfolderTree extends BodyTagSupport {

    private String m_strMoveCategory;

    /**
     * @return int
     * @throws javax.servlet.jsp.JspException
     */
    public int doEndTag() throws JspException {
        return 0;
    }

    /**
     * @return int
     * @throws javax.servlet.jsp.JspException
     */
    public int doStartTag() throws JspException {
        JspWriter pdWriter = pageContext.getOut();
        if (pdWriter == null) {
            return SKIP_BODY;
        }
        try {
            ServletRequest pdRequest = pageContext.getRequest();
            BaseDocumentForm udForm = DocumentsHelper
                    .getDocumentForm(pdRequest);
            String strTopCatID = udForm.getTc();
            String strLinkID = udForm.getLinkid();
            String strProjectId = udForm.getProj();
            Vector vtCats = udForm.getCategories();
            StringBuffer strBuffer = new StringBuffer(StringUtil.EMPTY_STRING);
            List lstExps = new ArrayList();
            List lstDivs = new ArrayList();
            strBuffer.append("\n<table width=\"100%\">");
            printCats(
                    vtCats,
                    strBuffer,
                    lstExps,
                    lstDivs,
                    strTopCatID,
                    strLinkID,
                    strProjectId);
            strBuffer.append("\n</table>");
            strBuffer.append("\n<script>");
            strBuffer.append("\nvar folderExps = [" + getItems(lstExps) + "];");
            strBuffer.append("\nvar folderDivs = [" + getItems(lstDivs) + "];");
            strBuffer.append("\n</script>");
            pdWriter.write(strBuffer.toString());
            pdWriter.flush();
        }
        catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return SKIP_BODY;
    }

    /**
     * @param vtCats
     * @param strBuffer
     * @param lstExps
     * @param lstDivs
     */
    private void printCats(Vector vtCats, StringBuffer strBuffer, List lstExps,
            List lstDivs, String strTopCatId, String strLinkID,
            String strProjectId) {
        String strParams 
        	= "displayDocumentList.wss?proj="
                + strProjectId
                + "&tc="
                + strTopCatId
                + "&linkid="
                + strLinkID
                + "&docAction=fromCatTree";
        if (vtCats != null && vtCats.size() > 0) {
            for (int i = 0; i < vtCats.size(); i++) {
                ETSCat udCat = (ETSCat) vtCats.get(i);
                Vector vtSubCats = udCat.getSubCats();
                boolean bHasSubCats = false;
                if (vtSubCats != null && vtSubCats.size() > 0) {
                    bHasSubCats = true;
                }
                strBuffer.append("\n<tr><td align=\"left\" width=\"2%\">\n");
                if (bHasSubCats) {
                    strBuffer.append("\n<div id=\"exp" + udCat.getId() + "\">");
                    strBuffer.append("\n<a style=\"text-decoration: none\" href=\"javascript:doExpand('exp"
                            + udCat.getId()
                            + "','div"
                            + udCat.getId()
                            + "')\"><b>+</b></a>");
                    strBuffer.append("\n</div>");
                    lstExps.add(new String("exp" + udCat.getId()));
                }
                else {
                    strBuffer.append("<b>&nbsp;</b>");
                }
                strBuffer.append("\n</td>");
                strBuffer.append("\n<td align=\"left\">\n");
                strBuffer
                        .append("\n&nbsp;<img src=\""
                                + Defines.SERVLET_PATH
                                + "ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" width=\"13\" height=\"9\" alt=\"folder\" />\n");
                String strCC = "&cc=" + udCat.getId();
                strBuffer.append("<a href=\" " + strParams + strCC + "\">");
                strBuffer.append(udCat.getName());
                strBuffer.append("</a>");
                strBuffer.append("\n</td></tr>");
                if (bHasSubCats) {
                    strBuffer
                            .append("\n<tr><td align=\"left\" width=\"2%\"></td><td align=\"right\">");
                    strBuffer.append("\n<div id=\"div" + udCat.getId() + "\">");
                    strBuffer.append("\n<table width=\"95%\">");
                    printCats(vtSubCats, strBuffer, lstExps, lstDivs, strTopCatId, strLinkID, strProjectId);
                    strBuffer.append("\n</table>");
                    strBuffer.append("\n</div>");
                    lstDivs.add(new String("div" + udCat.getId()));
                    strBuffer.append("\n<script>");
                    strBuffer.append("\ndocument.getElementById(\"div"
                            + udCat.getId()
                            + "\").style.display = \"none\";");
                    strBuffer.append("\n</script>");
                    strBuffer.append("\n</td></tr>");
                }
            }
        }
    }

    /**
     * @param lstItems
     * @return
     */
    private String getItems(List lstItems) {
        StringBuffer strItems = new StringBuffer(StringUtil.EMPTY_STRING);
        if (lstItems != null && lstItems.size() > 0) {
            for (int i = 0; i < lstItems.size(); i++) {
                strItems.append("\"");
                strItems.append(lstItems.get(i));
                strItems.append("\"");
                if (i < (lstItems.size() - 1)) {
                    strItems.append(StringUtil.COMMA);
                }
            }
        }

        return strItems.toString();
    }
}
