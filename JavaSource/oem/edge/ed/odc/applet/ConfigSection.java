package oem.edge.ed.odc.applet;
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

import java.io.*;
import java.util.*;

public class ConfigSection extends ConfigObject
{
        private String name;

        public ConfigSection(String name)
        {
                super();
                this.name = name;
        }
        public String getName()
        {
                return name;
        }
        public boolean isCurrentSection(String sectionName)
        {
                return sectionName.equalsIgnoreCase(name);
        }
        public void save(OutputStream out)
        {
                PrintWriter ps = new PrintWriter(out, true);
                String s = encode(name);
                ps.println("<" + s + ">");

                super.save(out);

                ps.println("</" + s + ">");
        }
        public void setName(String name)
        {
                this.name = name;
        }
}
