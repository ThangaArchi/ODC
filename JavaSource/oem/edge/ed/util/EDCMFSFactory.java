package oem.edge.ed.util;

import java.lang.*;
import java.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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
** EDCMFSFactory
**
** Moving from AFS to GSA env. Create a factory to help select the correct 
**  access type.  If the cell name ends in "gsa", then use GSA, otherwise AFS.
**  Setting  -DEDCMFSFactory.force=xxx on the JVM, where xxx is either GSA or is not,
**  will FORCE the issue.
*/
public class EDCMFSFactory {
   static public EDCMafsFile createFileObject(String cell) {
   
      Hashtable p = System.getProperties();
      
      EDCMafsFile ret = null;
      
      String forced = (String)p.get("EDCMFSFactory.force");
      
      if (forced != null) cell = forced;
      
      int idx = cell.toLowerCase().lastIndexOf("gsa");
      
      if (idx > 0 && idx == cell.length()-4) {
         ret = new EDCMgsaFile();
      } else {
         ret = new EDCMafsFile();
      }
      
      return ret;
   }
}
