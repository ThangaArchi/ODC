/*   ------------------------------------------------------------------          */
/*   IBM                                                                                     */
/*                                                                                               */  
/*   OCO Source Materials                                                          */
/*                                                                                               */
/*   Product(s): ICC/PROFIT                                                       */
/*                                                                                               */
/*   (C)Copyright IBM Corp. 2002,2003 		              */ 
/*                                                                                               */  
/*   The source code for this program is not published or otherwise */
/*   divested of its trade secrets, irrespective of what has been        */
/*   deposited with the US Copyright Office.                                  */
/*   ------------------------------------------------------------------           */
// sandra nava to get resp.contentType() from ETSFileContentHandler.java

package oem.edge.ets.fe;

import java.util.*;
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

public class ETSMimeDataType
{
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
 
    private String mimetype;
    private String desc;
    private Vector extension;
	
    public ETSMimeDataType(String desc, String mimetype, String ext)
    {
	this.desc = desc;
	this.mimetype = mimetype;
	this.extension = setExtension(ext);
    }
    
    public String getMimeType()
    {
	return mimetype;
    }
    
    public String getDescription()
    {
	return desc;
    }
    
    private Vector setExtension(String ext){
	if (ext != null){ 
	    StringTokenizer st = new StringTokenizer(ext, ",");
	    extension = new Vector();
	    while (st.hasMoreTokens()){
		String e = st.nextToken();
		extension.addElement(e);
	    }
	}
	return extension;
    }

    public Vector getExtension(){
	return extension;
    }
}
