package oem.edge.ed.sd;

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
//                            Edge 3.1.1
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

import java.util.Date;
import java.io.*;
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

public class SdReader extends Thread {

    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    final static String version = "v050802.1: ";

    private static final String sourceServlet = "SdReader";

    private InputStream in;
    private byte[][] buffer;
    private boolean[] bufferFilled;
    private int[] numBytesFilled;
    private StringBuffer done;
    private long[] readStats;

    private int totalBuffers;
    private int lastBufferFilled = -1;


    SdReader(InputStream in, byte[][] buffer, boolean[] bufferFilled, int[] numBytesFilled, StringBuffer done, long[] readStats) {
        this.in = in;
        this.buffer = buffer;
        this.bufferFilled = bufferFilled;
        this.numBytesFilled = numBytesFilled;
        this.done = done;
	this.readStats = readStats;

        this.totalBuffers = buffer.length;
    }


    public void run() {

	try{
	    int bytesRead;
	    int curr = getNextEmptyBuffer();

	    long stopWatch = System.currentTimeMillis();

            while( (bytesRead = in.read(buffer[curr])) > 0 ) {

		readStats[0] += bytesRead;
		readStats[1] += System.currentTimeMillis() - stopWatch;

	        updateBufferStatus(curr, bytesRead);
                if( (curr = getNextEmptyBuffer()) == -1 ) 
                    throw new RuntimeException("SdWriter thread died. Exiting SdReader!");

	    	stopWatch = System.currentTimeMillis();
	    }

	    in.close();
	}
	catch(Throwable t) {
            System.err.println("\nException at " + SdUtils.formatter.format(new Date()) + "\n");
            SdUtils.sendAlert("ERROR! The following exception was thrown.\nStackTrace:\n" + SdUtils.getStackTrace(t));
	}
	finally {
	    synchronized(bufferFilled) {
	        done.append("Y");
	        bufferFilled.notifyAll();
	    }
	}

    }





    private int getNextEmptyBuffer() throws InterruptedException {

        int next = lastBufferFilled + 1;
        if(next == totalBuffers)
                next = 0;

        synchronized(bufferFilled) {

            while( bufferFilled[next]  && done.length() == 0 )
		bufferFilled.wait();

	    bufferFilled.notifyAll();

        }

        if( ! bufferFilled[next] )
           return next;
        else
            return -1;

    }



    private void updateBufferStatus(int buffer, int numBytes) {

	if(bufferFilled[buffer])
	    throw new RuntimeException("This should not happen. bufferFilled[" + buffer + "] is already true");

        synchronized(bufferFilled) {
	    bufferFilled[buffer] = true;
	    numBytesFilled[buffer] = numBytes;
	    bufferFilled.notifyAll();
	}

	lastBufferFilled = buffer;

    }

}



