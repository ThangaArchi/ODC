package oem.edge.ed.sd.ordproc;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: PROFIT                                                        */
/* (C) Copyright IBM Corp. 2000                                              */
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

import java.io.*;
import java.util.Hashtable;

public class EDSDContentDeliveryTable {

    private static String filepath;
    private static long lastID;

    private static long firstEntryTime;
    private final static long EXP_DURATION = 11 * 24 * 60 * 60 * 1000; // 11 days  (buffer of 1 day)


    public static final String FILE_ID         = "FILE_ID";
    public static final String ORDER_ID        = "ORDER_ID";
    public static final String USER_ID         = "USER_ID";

    // file-specific props
    public static final String FILE_NAME       = "FILE_NAME";
    public static final String FILE_DESC       = "FILE_DESC";
    public static final String FILE_PATH       = "FILE_PATH";
    public static final String MIME_TYPE       = "MIME_TYPE";
    public static final String NUM             = "NUM";

    // order-specific props
    public static final String NUM_FILES       = "NUM_FILES";
    public static final String EMAIL           = "EMAIL";
    public static final String EXPIRATION_TIME = "EXPIRATION_TIME";

    public static final String FILE_START      = "<file>";
    public static final String FILE_STOP       = "</file>";

    public static final String ORDER_START     = "<order>";
    public static final String ORDER_STOP      = "</order>";


    static synchronized void setConfigPath(String filepath) throws IOException {

        File f = new File(filepath);

        if(f.isFile())
            EDSDContentDeliveryTable.filepath = filepath;
        // make sure we can write to that filepath
        else {
            FileOutputStream out = new FileOutputStream(filepath, true);
            out.close();
            EDSDContentDeliveryTable.filepath = filepath;
        }

        firstEntryTime = getFirstEntryTime();
    }




    private static long getFirstEntryTime() throws IOException {

        File f = null;

        if(filepath == null)
            throw new RuntimeException("config path not set!");

        else {
            f = new File(filepath);
            if( ! f.isFile() || f.length() == 0 )
                return 0;
        }

        long time = 0;
        String line;

        BufferedReader in = new BufferedReader(new FileReader(filepath));

        while((line = in.readLine()) != null) {
            if(line.indexOf(FILE_ID) >= 0) {
                int index = line.indexOf('=');
                time = Long.parseLong(line.substring(index + 1));
                break;
            }
        }
        
        in.close();

        return time;
    }




    private static String getNameValuePair(String name, String value) {

        return name + "=" + value + "\n";

    }



    static String getFileEntry(long fileID, String fileName, String fileDesc, String filePath, String mimeType, int num) {

        StringBuffer s = new StringBuffer();

        s.append(FILE_START);
        s.append("\n");

        s.append(getNameValuePair(FILE_ID,   String.valueOf(fileID)));
        s.append(getNameValuePair(FILE_NAME, fileName));
        s.append(getNameValuePair(FILE_DESC, fileDesc));
        s.append(getNameValuePair(FILE_PATH, filePath));
        s.append(getNameValuePair(MIME_TYPE, mimeType));
        s.append(getNameValuePair(NUM,       String.valueOf(num)));

        s.append(FILE_STOP);
        s.append("\n");

        return s.toString();

    }




    static synchronized void addOrderEntry(String orderID, String userID, String email, long expirationTime, int numFiles, String[] fileEntries)  throws IOException {


        if(filepath == null)
            throw new RuntimeException("config path not set!");

        File f = new File(filepath);
        boolean updateFirstEntryTime = false;



        StringBuffer s = new StringBuffer();

        s.append(ORDER_START);
        s.append("\n");

        s.append(getNameValuePair(ORDER_ID, orderID));
        s.append(getNameValuePair(USER_ID, userID));
        s.append(getNameValuePair(EMAIL, email));
        s.append(getNameValuePair(EXPIRATION_TIME, String.valueOf(expirationTime)));
        s.append(getNameValuePair(NUM_FILES, String.valueOf(numFiles)));

        for(int i = 0; i < fileEntries.length; i++)
            s.append(fileEntries[i]);

        s.append(ORDER_STOP);
        s.append("\n"); 
        s.append("\n"); 



        if( firstEntryTime < (System.currentTimeMillis() - EXP_DURATION) ) {

            updateFirstEntryTime = true;
            
            // if it is time to move existing table to table.old
            if( firstEntryTime > 0 )
                f.renameTo(new File(filepath + ".old"));
        }


        FileOutputStream out = new FileOutputStream(filepath, true);
        out.write(s.toString().getBytes());
        out.close();
        
        if(updateFirstEntryTime) {

            firstEntryTime = getFirstEntryTime();

        }

    }



    static synchronized long generateFileIds(int numFiles) {

        long ID = System.currentTimeMillis();

        while (ID <= lastID)
            ID++;
        
        lastID = ID + numFiles - 1;

        return ID;

    }



    static synchronized Hashtable getTable() throws IOException {
        
        if(filepath == null)
            throw new RuntimeException("config path not set!");

        Hashtable table = new Hashtable();
        String line;

        String input = readFile(filepath) + readFile(filepath + ".old");

        BufferedReader in = new BufferedReader(new StringReader(input));

        while((line = in.readLine()) != null) {

            line = line.trim();

            if(line.charAt(0) == '<') {
                long ID = Long.parseLong(line.substring(1, line.length()-1));
                Hashtable entry = new Hashtable();
                
                while((line = in.readLine()) != null) {

                    line = line.trim();

                    if(line.charAt(0) == '<')
                        break;

                    else {
                        int index = line.indexOf("=");
                        if(index != -1) {
                            String key = line.substring(0, index).trim();
                            String value = line.substring(index+1).trim();
                            entry.put(key, value);
                        }
                    }
                }

                if(line.equals("</" + ID + ">"))
                    table.put(new Long(ID), entry);
                else
                    throw new RuntimeException("Hashtable has incorrect format");

            }
        }

        in.close();

        return table;
        
    }




    private static String readFile(String filename) throws IOException {

        if( ! new File(filename).isFile())
            return "";

        int arraySize = 10240;
        String content = "";
        int bytesRead = 0;
        byte[] arr = new byte[arraySize];

        FileInputStream in = new FileInputStream(filename);
        while(bytesRead >= 0) {
            content += new String(arr, 0, bytesRead);
            bytesRead = in.read(arr, 0, arraySize);
        }

        in.close();

        return content;
    }

}
