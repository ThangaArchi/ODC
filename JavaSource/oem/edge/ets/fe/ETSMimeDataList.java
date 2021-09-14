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

public class ETSMimeDataList
{
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
 
	static private final ETSMimeDataType[] mimelist = {
		new ETSMimeDataType("Binary", "application/octect-stream","bin,class,exe"),
		new ETSMimeDataType("Plain Text", "text/plain","txt,asc,c,c++,cpp,h,jav,java,log,lst,text,sh"),
		new ETSMimeDataType("ZIP", "application/x-zip-compressed","zip"),
		new ETSMimeDataType("Hypertext Markup Language (HTML)", "text/html","htm,html"),
		new ETSMimeDataType("Unix Tape Archive (TAR)", "application/x-tar","tar"),
		new ETSMimeDataType("tar-gz", "application/x-compressed","tgz"),
		new ETSMimeDataType("Acrobat (PDF)", "application/pdf","pdf"),
		new ETSMimeDataType("Bitmap Image", "image/bmp","bmp"),
		new ETSMimeDataType("Compressed Data", "application/x-compress","z"),
		new ETSMimeDataType("Custom Style Sheet (CSS)", "text/css","css"),				
		new ETSMimeDataType("GIF Image", "image/gif","gif"),
		new ETSMimeDataType("GZIP", "application/x-gzip","gz"),
		//new ETSMimeDataType("Java Archive (JAR)", "application/java-archive"),
		new ETSMimeDataType("Java Archive (JAR)", "application/octect-stream","jar"),
		new ETSMimeDataType("Javascript Program", "application/x-javascript","js,ls,mocha"),
		new ETSMimeDataType("JPEG Image", "image/jpeg","jpe,jpeg,jpg"),
		new ETSMimeDataType("Lotus 1-2-3 Document", "application/vnd.lotus-1-2-3","wks,wk1,wk3,wk4,123"),
		new ETSMimeDataType("Lotus Approach Document", "application/vnd.lotus-approach","apr,vew"),
		new ETSMimeDataType("Lotus Freelance Document", "application/vnd.lotus-freelance","pre,prz"),
		new ETSMimeDataType("Lotus Notes", "application/vnd.lotus-notes","ndl,ns2,ns3,ns4,nsf,nsg,nsh,ntf"),
		new ETSMimeDataType("Lotus Organizer Document", "application/vnd.lotus-organizer","or2,or3,org"),
		new ETSMimeDataType("Lotus ScreenCam Movie", "application/vnd.lotus-screencam","scm"),
		new ETSMimeDataType("Lotus WordPro Document", "application/vnd.lotus-wordpro","lwp"),
		new ETSMimeDataType("Microsoft Excel", "application/vnd.ms-excel","xls,xlw,xla,xlc,xlm,xlt"),
		new ETSMimeDataType("Microsoft PowerPoint", "application/vnd.ms-powerpoint","ppt,pps,pot"),
		new ETSMimeDataType("Microsoft Project Document", "application/vnd.ms-project","mpp"),		
		new ETSMimeDataType("Microsoft Word Document", "application/msword","doc"),
		new ETSMimeDataType("Windows Write Document", "application/mswrite","wri"),
		new ETSMimeDataType("MPEG Audio", "audio/x-mpeg","mp2,mpga,mp3"),
		new ETSMimeDataType("MPEG Video", "video/mpeg","mpe,mpeg,mpg"),
		//new ETSMimeDataType("PageMaker document", "application/x-pagemaker","?????"),
		//new ETSMimeDataType("PGP Encrypted", "application/pgp-encrypted","?????"),
		new ETSMimeDataType("PNG Image", "image/png","png"),
		new ETSMimeDataType("Postscript Document", "application/postscript","ai,ps,eps"),
		new ETSMimeDataType("QuickTime for Windows", "video/quicktime","mov,qt"),
		new ETSMimeDataType("Rich Text Format", "application/rtf","rtf"),
		new ETSMimeDataType("TIFF Image", "image/tiff","tif,tiff"),
		new ETSMimeDataType("Video for Windows (AVI)", "video/x-msvideo","avi"),
		new ETSMimeDataType("WAV", "audio/wav","wav"),
		new ETSMimeDataType("X Bitmap", "image/x-xbitmap","xbm"),
		new ETSMimeDataType("XML", "text/xml","xml")        
	};
		
	static public String getDescription(int index)
	{
		return mimelist[index].getDescription();
	}
	
	static public String getMimeType(int index)
	{
		return mimelist[index].getMimeType();
	}
	
	static public Vector getExtension(int index)
	{
		return mimelist[index].getExtension();
	}
	
	static public int getCount()
	{
		return mimelist.length;
	}
	
	static public ETSMimeDataType[] getMimeList()
	{
		return mimelist;
	}
    
    static public int getIndex(String mimetype)
    {
        for (int i = 0; i<mimelist.length; i++)
        {           
            if (mimetype.equalsIgnoreCase(mimelist[i].getMimeType()))
            {
                return i;
            }
        }
        
        return 0;
    }
    
    static public int getIndexByDescription(String description)
    {
        for (int i = 0; i<mimelist.length; i++)
        {
            if (description.equalsIgnoreCase(mimelist[i].getDescription()))
            {
                return i;
            }
        }
        
        return 0;
    }

    static public String getMimeTypeByExtension(String ext){

      for (int i = 0; i<mimelist.length; i++)
        {
            if ((mimelist[i].getExtension()).contains(ext))
            {
                return mimelist[i].getMimeType();
            }
        }
      return "application/octect-stream";
    }
}
