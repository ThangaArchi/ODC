package oem.edge.ed.sd.ordproc;

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
//                            Edge 2.10
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

import java.util.StringTokenizer;
import java.io.*;

public class ShipManager {
    
    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    /**
     * This expects a file that looks like this:
     * TECH_ALIAS;
     * PM_NAME;
     * PM_PHONE;
     * PM_EMAIL;
     * VERSION_NO;
     * FCS_DATE;
     * TECHNOLOGY;
     * TECH_NAME;
     * REL_TYPE;
     * REL_MGR_NAME;
     * SHIP_MGR;
     * SHIP_MGR_EMAIL;
     * "EDGE";
     * EXTERNAL_TECH_NAME

     * and returns:
     * an array of records that look like: {TECH_NAME, TECH_ALIAS, PM_EMAIL, SHIP_MGR_EMAIL, EXTERNAL_TECH_NAME}
     **/
    
    static String[][] loadFile(String TechInfoFileName) throws IOException {

        int techAliasField = 1;
        int techField = 13;
        int pmEmailField = 7;
        int smEmailField = 23;
        int extTechField = 27;
    
        String delim = ";";
        int j = 0;
        
        BufferedReader TechInfoFile = new BufferedReader(new FileReader(TechInfoFileName));
        
        while (TechInfoFile.ready()) {

            String line = TechInfoFile.readLine();

            if(line != null)
               j++;

            if(line == null || line.length() == 0)
                continue;
        }
        
        
        String[][] table = new String[j][5];//new for 5.3.1, update String[6][5] to [8][5], for CU65LP, CU65HP

        int n = 0;

        TechInfoFile = new BufferedReader(new FileReader(TechInfoFileName));

        while (TechInfoFile.ready()) {

            String line = TechInfoFile.readLine();

            if(line != null)
                line = line.trim();

            if(line == null || line.length() == 0)
                continue;

            StringTokenizer st = new StringTokenizer(line, delim, true);

            boolean lastDelim = true;

            String token;
            
            for(int i = 1; i <= 28; i++) {

                if(st.hasMoreTokens())
                    token = st.nextToken().trim();
                else
                    break;

                if(lastDelim) {

                    if(token.equals(delim))
                        i++;

                    else {

                        lastDelim = false;

                        if(i == techField)
                            table[n][0] = token;

                        else if(i == techAliasField)
                            table[n][1] = token;

                        else if(i == pmEmailField)
                            table[n][2] = token;

                        else if(i == smEmailField)
                            table[n][3] = token;

                        else if(i == extTechField)
                            table[n][4] = token;

                    }
                }
                else
                    lastDelim = true;
            }

            n++;

        }

        TechInfoFile.close();

        return table;

    }
}
