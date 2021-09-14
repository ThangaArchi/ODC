package oem.edge.ed.sd;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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



import java.util.Date;
import java.util.Hashtable;

import java.sql.SQLException;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class SdDownloadServlet extends HttpServlet {

    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    final static String version = "v091903.1: ";

    private static final String sourceServlet = "SdDownloadServlet";
    private String propsFile;

    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        this.propsFile = config.getInitParameter(SdUtils.PROPS_FILE_TAG);

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }



    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	String id = "";

        try {

            if( ! SdUtils.initialize(this.propsFile) ) {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.INIT_FAILED);
                SdUtils.sendImpAlert("SdUtils failed to initialize");
                return;
            }

            id = request.getParameter("id");
            String token = request.getParameter("token");

            if(token != null) {
                String[] userInfo = SdUtils.checkToken(token, sourceServlet);
		downloadFile(request, response, userInfo);
            }
            else {
                SdUtils.sendForbidden(request, response, SdUtils.AUTH_NULL);
                return;
            }

        }
        catch (Throwable t) {

            System.err.println("\nException at " + SdUtils.formatter.format(new Date()) + "\n");

            SdUtils.sendImpAlert("The following exception was thrown for ID: " + id + "\nStackTrace:\n" + SdUtils.getStackTrace(t));

            try {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.EXCEPTION);
            }
            catch(Throwable t1) {
                print("ERROR! The following exception was thrown for ID: " + id + "\nStackTrace:\n" + SdUtils.getStackTrace(t1));
                throw new ServletException("503 Service Unavailable");
            }

        }

    }



    static void downloadFile(HttpServletRequest request, HttpServletResponse response, String[] userInfo) throws ServletException, IOException {

        long fileID = 0;

	String id = "";

        long receivedTime = System.currentTimeMillis();

        try {

          /*  if( ! SdUtils.authenticate() ) {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.INTERNAL_AUTH_FAILURE);
                SdUtils.sendImpAlert("SdUtils failed to authenticate");
                return;
                }*/

	    boolean debugMode = false;
	    String debug = request.getParameter("debug");
	    if(debug != null && debug.equals("yes"))
	    	debugMode = true;

	    int blockSize = SdUtils.blockSizeInKB * 1024;
	    int numBlocks = SdUtils.numBlocks;
	    boolean useMultipleThreads = SdUtils.useMultipleThreads;


	    if(debugMode) {

	        String blockSizeStr = request.getParameter("blockSize");
	        if(blockSizeStr != null)
	    	    blockSize = Integer.parseInt(blockSizeStr) * 1024;

                String numBlocksStr = request.getParameter("numBlocks");
                if(numBlocksStr != null)
                    numBlocks = Integer.parseInt(numBlocksStr);

		String umt = request.getParameter("umt");
		if(umt != null) {
		    if(umt.equals("yes"))
		   	useMultipleThreads = true;
		    else if(umt.equals("no"))
		   	useMultipleThreads = false;
		}

	    }


            String hasCompleted = "N";
            String isDSClient = "N";
            id = request.getParameter("id");

            String rdclient = "";
            if(request.getParameter("rdclient") != null) {
                rdclient = " (DesignSolutionsClient)";
                isDSClient = "Y";
            }

	    String user = null;
	    String order = null;
	    String email = null;

            boolean hasValidToken = false;


            if(userInfo != null) {

		user = userInfo[0];
		order = userInfo[2];
		email = userInfo[5];

                if(user != null) {
                    if(user.length() == 0) {
                        SdUtils.sendForbidden(request, response, SdUtils.TOKEN_EXPIRED);
                        return;
                    }
                    else
                        hasValidToken = true;
                }

            }
            else {
                SdUtils.sendForbidden(request, response, SdUtils.AUTH_NULL);
                return;
            }


            if( ! hasValidToken ) {
                SdUtils.sendForbidden(request, response, SdUtils.AUTH_FAILED);
                SdUtils.sendAlert(sourceServlet + ": WARNING! user authorization failed for id: " + id);
                return;
            }


            if (id == null) {
                SdUtils.sendForbidden(request, response, SdUtils.ID_NULL);
                print("Null ID sent by user: " + user + ". Query String: " + request.getQueryString());
                return;
            }

            try {
                fileID = Long.parseLong(id);
            }
            catch(NumberFormatException nfe) {
                SdUtils.sendForbidden(request, response, SdUtils.ID_NOT_PARSABLE);
                SdUtils.sendAlert(sourceServlet + ": WARNING! NumberFormatException thrown while parsing id: " + id);
                return;
            }




            Hashtable fileEntry = SdUtils.getFileEntry(fileID);

            if(fileEntry == null) {
                SdUtils.sendForbidden(request, response, SdUtils.ID_NOT_FOUND);
                print("fileID: " + fileID + " user: " + user + ": Not Found in filesTable");
                return;
            }


            Hashtable orderEntry = SdUtils.getOrderEntry(fileID);


            String fileOwner = (String)orderEntry.get(SdUtils.USER_ID);
            String numFiles = (String)orderEntry.get(SdUtils.NUM_FILES);
// String email = (String)orderEntry.get(SdUtils.EMAIL);
            long expirationTime = Long.parseLong((String)orderEntry.get(SdUtils.EXPIRATION_TIME));


	    String orderID = (String)fileEntry.get(SdUtils.ORDER_ID);
            String filePath = (String)fileEntry.get(SdUtils.FILE_PATH);
            String fileName = (String)fileEntry.get(SdUtils.FILE_NAME);
            String mimeType = (String)fileEntry.get(SdUtils.MIME_TYPE);
            String num = (String)fileEntry.get(SdUtils.NUM);



            if(fileOwner == null || num == null || numFiles == null || orderID == null || expirationTime == 0 || filePath == null || mimeType == null) {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.REQUIRED_FIELD_MISSING);
                SdUtils.sendImpAlert("fileID: " + fileID + " user: " + user + ": Required Field Missing");
                return;
            }
            
            for (int k=0; k<userInfo.length; k++){
               System.out.println (userInfo[k]+" ");
            }
            
	    String orderIdPrefix = orderID.substring(0, orderID.lastIndexOf('-'));
	    String orderPrefix = "";
	    if(order.length() != 0 && order.indexOf('-')>0)//modified for 3.12.1 fixpack in case order='NA'
		orderPrefix = order.substring(0, order.lastIndexOf('-'));

	    if( ! userInfo[3].equals("su")  &&  ! orderPrefix.equals(orderIdPrefix)  &&  ! user.equals(fileOwner) ) {
                SdUtils.sendForbidden(request, response, SdUtils.USER_MISMATCH);
                SdUtils.sendImpAlert("User mismatch for order: " + orderID + " user: " + user + " order: " + order + " entitlement: " + userInfo[1] + " fileOwner: " + fileOwner);
                return;
            }


	    if(receivedTime > expirationTime) {
                SdUtils.sendForbidden(request, response, SdUtils.ORDER_EXPIRED, expirationTime);
                print("Order: " + orderID + " user: " + user + ": Order Expired");
                return;
            }


	    if( ! SdUtils.allowNewConnection()) {
		SdUtils.sendServiceUnavailable(request, response, SdUtils.SERVER_BUSY);
		SdUtils.sendImpAlert("Bottleneck interface congested. Sending away user: " + user + " for order: " + orderID);
		return;
	    }




            if(filePath.indexOf("PreviewKit") != -1) {

                String acceptSLA = request.getParameter("acceptSLA");

                if(acceptSLA == null) {
                    String url = SdUtils.getBaseURL(request) + request.getServletPath();
                    String pathInfo = request.getPathInfo();
                    if(pathInfo != null)
                        url += pathInfo;
                    SdUtils.sendSLA(request, response, url, id);
                    return;
                }
                else if( ! acceptSLA.equals("ACCEPT") ) {
                    SdUtils.sendForbidden(request, response, SdUtils.MUST_ACCEPT_SLA);
                    print("ID: " + id + " user: " + user + ": Did not accept SLA");
                    return;
                }

            }




            File f = new File(filePath);
            long length = f.length();
            long timestamp = f.lastModified();
            
            if(length <= 0) {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.FILE_NOT_FOUND);
                SdUtils.sendImpAlert(filePath + ": File Not Found for ID: " + id);
                return;
            }


            long skipBytes = 0;
            String skipStr = "";
            boolean sendPartialFile = false;

            String crcStr = request.getParameter("crc");

            if(crcStr != null) {

                long remoteLength = Long.parseLong(request.getParameter("actSize"));
                long remoteTime = Long.parseLong(request.getParameter("time"));

                if(remoteLength != length || remoteTime != timestamp) {
                    SdUtils.sendForbidden(request, response, SdUtils.INVALID_CRC);
                    SdUtils.print("File length or time mismatch in crc request for fileID: " + fileID + " clientLength: " + remoteLength + " serverLength: " + length + " clientTime: " + remoteTime + " serverTime: " + timestamp);
                    return;
                }

                int crc = Integer.parseInt(crcStr);
                long currSize = Long.parseLong(request.getParameter("currSize"));
                long numBytes = Long.parseLong(request.getParameter("numBytes"));

                sendPartialFile = SdUtils.checkCRC(filePath, crc, (currSize - numBytes), numBytes);

                if(sendPartialFile)
                    skipBytes = currSize;
                else {
                    SdUtils.sendForbidden(request, response, SdUtils.INVALID_CRC);
                    SdUtils.print("CRC check on file returned negative for fileID: " + fileID);
                    return;
                }
            }



	    String userAgent = request.getHeader("user-agent");
	    String pathInfo = request.getPathInfo();
	    String queryString = new StringBuffer("?id=" + id).append("&file_name=" + fileName).toString();
	    if(order.length() != 0)
		queryString += "&order=" + order;

            FileInputStream in = new FileInputStream(filePath);

            if(in == null) {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.NULL_INPUT_STREAM);
                SdUtils.sendImpAlert(filePath + ": Null Input Stream for ID: " + id);
                return;
            }


            if(sendPartialFile) {

                long beforeSkip = System.currentTimeMillis();
                long actuallySkipped = in.skip(skipBytes);
                long timeForSkip = System.currentTimeMillis() - beforeSkip;

                if(actuallySkipped != skipBytes)
                    throw new RuntimeException("Tried to skip over " + skipBytes + "bytes. Was able to skip over only " + actuallySkipped + " bytes in " + timeForSkip + " ms");
                else {
                    skipStr = " (skipped " + skipBytes + " bytes)";
                    SdUtils.print("Skipped over " + skipBytes + " bytes for fileID: " + fileID + " in " + timeForSkip + " ms");
                }

            }



            response.setContentType(mimeType);

            response.setContentLength((int) (length - skipBytes));

            OutputStream out = response.getOutputStream();

            if(out == null) {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.NULL_OUTPUT_STREAM);
                SdUtils.sendImpAlert("Null Output Stream for ID: " + id);
                return;
            }



            long startXfer = System.currentTimeMillis();
            String remoteAddr = request.getRemoteAddr();

            SdWriter stats = new SdWriter(userInfo, fileOwner, remoteAddr, id, orderID, length, skipBytes, isDSClient, email, startXfer); 

            if(useMultipleThreads) {	

                byte[][] buffer = new byte[numBlocks][blockSize];
                boolean[] bufferFilled = new boolean[numBlocks];
                int[] numBytesFilled = new int[numBlocks];
                StringBuffer done = new StringBuffer();
                long[] readStats = new long[2];
		int lastBufferEmptied = -1;

                StringBuffer debugStr = new StringBuffer();

                new SdReader(in, buffer, bufferFilled, numBytesFilled, done, readStats).start();


                SdUtils.addRunningDownload(stats);
		SdMonitor.addThread(stats);


                int totalBytesRead = 0;
                long totalWaitTime = 0;
                long waitTime;
                long maxTimeBetweenWrites = -1;

                long stopWatch1 = System.currentTimeMillis();
                long stopWatch2;

                try {
                    int curr;

                    while( (curr = getNextFullBuffer(bufferFilled, done, lastBufferEmptied, numBlocks)) != -1) {

                        stopWatch2 = System.currentTimeMillis();

                        waitTime = stopWatch2 - stopWatch1;
			totalWaitTime += waitTime;
                        if(waitTime > maxTimeBetweenWrites)
                            maxTimeBetweenWrites = waitTime;

			if(debugMode)
			    debugStr.append(waitTime + " ");

                        out.write(buffer[curr], 0, numBytesFilled[curr]);

                        stats.totalBytesRead = totalBytesRead += numBytesFilled[curr];
			SdMonitor.updateThreadInfo(stats, numBytesFilled[curr]);

                        updateBufferStatus(bufferFilled, numBytesFilled, curr);
			lastBufferEmptied = curr;

                        stopWatch1 = System.currentTimeMillis();

                    }

                }
                catch(Exception ex) {
		    String exStr = ex.toString();
                    if(exStr.indexOf("java.io.IOException: There is no process to read data written to a pipe") >= 0 ||
                       exStr.indexOf("java.io.IOException: A system call received a parameter that is not valid") >= 0 ||
                       exStr.indexOf("java.io.IOException: Error during native write operation! Status Code: -1") >= 0 ||
                       exStr.indexOf("com.ibm.servlet.engine.srt.ClientClosedConnectionException") >= 0) {
                        System.err.println(new Date() + ": ERROR! connection to client broken!");
                        print(new Date() + ": ERROR! connection to client broken!");
                    }
                    else
                        throw ex;
                }
                finally {

                    synchronized(bufferFilled) {
                        done.append("Y");
                        bufferFilled.notifyAll();
                    }

                    SdUtils.removeRunningDownload(stats);
		    SdMonitor.markThreadForDeletion(stats);

                    try {
                        out.flush();
                        out.close();
                    }
                    catch(IOException ioe) {
                        String errMsg = "Thrown trying to flush or close ServletOutputStream:\n" + SdUtils.getStackTrace(ioe);
                        System.err.println(errMsg);
                        print(errMsg);
                    }


                    long endXfer = System.currentTimeMillis();
                    long xferTime = endXfer - startXfer;
		    long writeTime = xferTime - totalWaitTime;

                    long xferRate = totalBytesRead / (xferTime + 1);
                    long readRate = readStats[0] / (readStats[1] + 1);
                    long writeRate = totalBytesRead / (writeTime + 1);


                    if(totalBytesRead != (length - skipBytes) && isDSClient.equals("N")) // if not downloaded using Design Solutions Client
                        SdUtils.mailUser(email, pathInfo, orderID, length, queryString);

                    SdUtils.log(userInfo, fileOwner, remoteAddr, userAgent, id, fileName, orderID, num, numFiles, length, totalBytesRead, skipBytes, xferRate, readRate, writeRate, startXfer, endXfer, isDSClient, numBlocks, blockSize, (int)maxTimeBetweenWrites, totalWaitTime, debugMode, debugStr.toString()); //new for 5.4.1:fileName

                }

                return;
            }

	    else {

                try {

                    byte[] buffer = new byte[blockSize];

                    int totalBytesRead = 0;
                    long readTime = 0;

                    long stopWatch1, stopWatch2, maxBlockReadTime = -1;

                    stopWatch1 = startXfer;

                    SdUtils.addRunningDownload(stats);
		    SdMonitor.addThread(stats);

                    StringBuffer debugStr = new StringBuffer();

                    try {
                        
                        int bytesRead = 0;
			long blockReadTime;

                        while ((bytesRead = in.read(buffer)) != -1) {

                            stopWatch2 = System.currentTimeMillis();
                            blockReadTime = stopWatch2 - stopWatch1;
			    readTime += blockReadTime;

                            if(debugMode)
                                debugStr.append(blockReadTime + " ");

                            if(blockReadTime > maxBlockReadTime)
                                maxBlockReadTime = blockReadTime;

                            out.write(buffer, 0, bytesRead);

                            stopWatch1 = System.currentTimeMillis();

                            stats.totalBytesRead = totalBytesRead += bytesRead;
			    SdMonitor.updateThreadInfo(stats, bytesRead);

                        }
                    }

                    catch(Exception ex) {

		        String exStr = ex.toString();
                        if(exStr.indexOf("java.io.IOException: There is no process to read data written to a pipe") >= 0 ||
                           exStr.indexOf("java.io.IOException: A system call received a parameter that is not valid") >= 0 ||
                           exStr.indexOf("java.io.IOException: Error during native write operation! Status Code: -1") >= 0 ||
                           exStr.indexOf("com.ibm.servlet.engine.srt.ClientClosedConnectionException") >= 0) {
                            System.err.println(new Date() + ": ERROR! connection to client broken!");
                            print(new Date() + ": ERROR! connection to client broken!");
                        }
                        else
                            throw ex;

                    }

                    finally {

                        SdUtils.removeRunningDownload(stats);
			SdMonitor.markThreadForDeletion(stats);

                        
                        try {
                            out.flush();
                            out.close();
                        }
                        catch(IOException ioe) {
                            String errMsg = "Thrown trying to flush or close ServletOutputStream:\n" + SdUtils.getStackTrace(ioe);
                            System.err.println(errMsg);
                            print(errMsg);
                        }


                        long endXfer = System.currentTimeMillis();
                        long xferTime = endXfer - startXfer;
                        long writeTime = xferTime - readTime;
                        
                        long readRate = totalBytesRead / (readTime + 1);
                        long writeRate = totalBytesRead / (writeTime + 1);
                        long xferRate = totalBytesRead / (xferTime + 1);

                        // TBW: Time Between (consecutive) Writes (~ same as BlockReadTime)


                        if(totalBytesRead != (length - skipBytes))
                            if(rdclient.length() == 0) // if not downloaded using Design Solutions Client
                                SdUtils.mailUser(email, pathInfo, orderID, length, queryString);

                        SdUtils.log(userInfo, fileOwner, remoteAddr, userAgent, id, fileName, orderID, num, numFiles, length, totalBytesRead, skipBytes, xferRate, readRate, writeRate, startXfer, endXfer, isDSClient, 1, blockSize, (int)maxBlockReadTime, readTime, debugMode, debugStr.toString());//new for 5.4.1:fileName

                    }

                }
                finally {
                    in.close();
                }

            }
            
        }
        catch (Throwable t) {

            System.err.println("\nException at " + SdUtils.formatter.format(new Date()) + "\n");

            SdUtils.sendImpAlert("The following exception was thrown for ID: " + id + "\nStackTrace:\n" + SdUtils.getStackTrace(t));

            try {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.EXCEPTION);
            }
            catch(Throwable t1) {
                print("ERROR! The following exception was thrown for ID: " + id + "\nStackTrace:\n" + SdUtils.getStackTrace(t1));
                throw new ServletException("503 Service Unavailable");
            }
            
        }
    }



    /*
      private void updateDB2(String IR_USERID, String IP, String FILE_ID, String USER_AGENT, String ORDER_ID, String FILE_NUM, String TOTAL_FILE_NUM, long FILE_SIZE, long BYTES_DOWNLOADED, long BYTES_SKIPPED, long DOWNLOAD_RATE, long READ_RATE, long WRITE_RATE, long START_TIME, long END_TIME, String COMPLETED, String DS_CLIENT, String SERVER, int NUM_BLOCKS, int BLOCK_SIZE, int MAX_BLOCK_READ_TIME) {

      StringBuffer sqlbuff = new StringBuffer();

      sqlbuff.append ("INSERT INTO INST1.SW_DOWNLOAD ");

      sqlbuff.append (" ( FILE_ID, START_TIME, END_TIME, IR_USERID, IP, USER_AGENT, ORDER_ID, FILE_NUM, TOTAL_FILE_NUM, FILE_SIZE, BYTES_DOWNLOADED, BYTES_SKIPPED, DOWNLOAD_RATE, READ_RATE, WRITE_RATE, COMPLETED, DS_CLIENT, SERVER, NUM_BLOCKS, BLOCK_SIZE, MAX_BLOCK_READ_TIME ) ");

      sqlbuff.append ("VALUES ( ");

      sqlbuff.append (quoteAndDelimit(FILE_ID));
      sqlbuff.append (delimitStr(SdUtils.db2Formatter.format(new Date(START_TIME))));
      sqlbuff.append (delimitStr(SdUtils.db2Formatter.format(new Date(END_TIME))));
      sqlbuff.append (quoteAndDelimit(IR_USERID));
      sqlbuff.append (quoteAndDelimit(IP));

      sqlbuff.append (quoteAndDelimit(USER_AGENT));

      sqlbuff.append (quoteAndDelimit(ORDER_ID));
      sqlbuff.append (delimitStr(FILE_NUM));
      sqlbuff.append (delimitStr(TOTAL_FILE_NUM));
      sqlbuff.append (delimit(FILE_SIZE));
      sqlbuff.append (delimit(BYTES_DOWNLOADED));
      sqlbuff.append (delimit(BYTES_SKIPPED));
      sqlbuff.append (delimit(DOWNLOAD_RATE));
      sqlbuff.append (delimit(READ_RATE));
      sqlbuff.append (delimit(WRITE_RATE));
      sqlbuff.append (quoteAndDelimit(COMPLETED));
      sqlbuff.append (quoteAndDelimit(DS_CLIENT));
      sqlbuff.append (quoteAndDelimit(SERVER));

      sqlbuff.append (delimit(NUM_BLOCKS));
      sqlbuff.append (delimit(BLOCK_SIZE));
      sqlbuff.append (MAX_BLOCK_READ_TIME);


      sqlbuff.append (")");

      try {
      int rowcount = SdUtils.updateDB2(sqlbuff.toString());
      if(rowcount != 1)
      SdUtils.sendImpAlert("got a rowcount of " + rowcount + " while updating DB2 with the following statement: " + sqlbuff.toString());
      }
      catch(SQLException e) {
      SdUtils.sendImpAlert("thrown while updating DB2 with the following statement: " + sqlbuff.toString() + "\n\n" + SdUtils.getStackTrace(e));
      }

      }

    */


    private String quote(String s) {

        return "\'" + s + "\' ";

    }

    private String quoteAndDelimit(String s) {

        return "\'" + s + "\' , ";

    }

    private String delimitStr(String s) {

        return s + " , ";

    }

    private String delimit(long l) {

        return l + " , ";

    }
    
    private static void print(String s) {

        SdUtils.print(s, sourceServlet);

    }


/*

    private void runSdWriter(OutputStream out, byte[][] buffer, boolean[] bufferFilled, int[] numBytesFilled, StringBuffer done, long[] readStats, HttpServletRequest request, HttpServletResponse response, String fileOwner, String orderID, String num, String numFiles, int blockSize, boolean debugMode, String email, String userAgent, String pathInfo, String requestURL) {

	try {

            SdUtils.addRunningDownload(this);

            long startXfer = System.currentTimeMillis();

	    int totalBytesRead = 0;
	    long writeTime = 0;
	    long stopWatch1 = System.currentTimeMillis();
	    long stopWatch2;
	    long maxTimeBetweenWrites = -1;
	    long waitTime;

	    try {
	        int curr;

                while( (curr = getNextFullBuffer()) != -1) {

		    stopWatch2 = System.currentTimeMillis();
		    waitTime = stopWatch2 - stopWatch1;
		    if(waitTime > maxTimeBetweenWrites)
			maxTimeBetweenWrites = waitTime;

	            out.write(buffer[curr], 0, numBytesFilled[curr]);

		    totalBytesRead += numBytesFilled[curr];
		    stopWatch1 = System.currentTimeMillis();
		    writeTime += stopWatch1 - stopWatch2;

	            updateBufferStatus(curr);
	        }

	    }
	    catch(Exception ex) {
		String exStr = ex.toString();
                if(exStr.indexOf("java.io.IOException: There is no process to read data written to a pipe") >= 0 ||
                   exStr.indexOf("java.io.IOException: A system call received a parameter that is not valid") >= 0 ||
                   exStr.indexOf("java.io.IOException: Error during native write operation! Status Code: -1") >= 0 ||
                   exStr.indexOf("com.ibm.servlet.engine.srt.ClientClosedConnectionException") >= 0) {
                    System.err.println(new Date() + ": ERROR! connection to client broken!");
                    print(new Date() + ": ERROR! connection to client broken!");
                }
                else
                    throw ex;
	    }
	    finally {

	        SdUtils.removeRunningDownload(this);

                try {
                    out.close();
                }
                catch(IOException ioe) { }



                long endXfer = System.currentTimeMillis();
                long xferTime = endXfer - startXfer + 1;
                long xferRate = totalBytesRead / xferTime;

		long readRate = readStats[0] / (readStats[1] + 1);
		long writeRate = totalBytesRead / (writeTime + 1);


                if(totalBytesRead != (length - skipBytes) && isDSClient.equals("N")) // if not downloaded using Design Solutions Client
                    SdUtils.mailUser(email, pathInfo, orderID, length, queryString);

                SdUtils.log(fileOwner, remoteAddr, userAgent, id, orderID, num, numFiles, length, totalBytesRead, skipBytes, xferRate, readRate, writeRate, startXfer, endXfer, isDSClient, numBlocks, blockSize, (int)maxTimeBetweenWrites, debugMode, "");

	    }

        }
        catch (Throwable t) {

            System.err.println("\nException at " + SdUtils.formatter.format(new Date()) + "\n");

            SdUtils.sendImpAlert("The following exception was thrown for ID: " + id + "\nStackTrace:\n" + SdUtils.getStackTrace(t));

            try {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.EXCEPTION);
            }
            catch(Throwable t1) {
                print("ERROR! The following exception was thrown for ID: " + id + "\nStackTrace:\n" + SdUtils.getStackTrace(t1));
                throw new RuntimeException("503 Service Unavailable");
            }

        }
	finally {
            synchronized(bufferFilled) {
                done.append("Y");
                bufferFilled.notifyAll();
            }
        }

    }

*/




    private static int getNextFullBuffer(boolean[] bufferFilled, StringBuffer done, int lastBufferEmptied, int totalBuffers) throws InterruptedException {

        int next = lastBufferEmptied + 1;
        if(next == totalBuffers)
            next = 0;

        synchronized(bufferFilled) {

            while( ! bufferFilled[next] && done.length() == 0 )
		bufferFilled.wait();

	    bufferFilled.notifyAll();

        }

	if(bufferFilled[next])
       	    return next;
	else
	    return -1;

    }



    private static void updateBufferStatus(boolean[] bufferFilled, int[] numBytesFilled, int buffer) {

	if( ! bufferFilled[buffer] )
	    throw new RuntimeException("This should not happen. bufferFilled[" + buffer + "] is already false");

        synchronized(bufferFilled) {
	    bufferFilled[buffer] = false;
	    numBytesFilled[buffer] = 0;
	    bufferFilled.notifyAll();
	}

    }



}
