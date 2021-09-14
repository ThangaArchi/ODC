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

import java.util.Date;
import java.util.Arrays;
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

public class Mutex {

   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    private static int numBins;
    private static String binsPath;
    private static boolean[] binLocked;
    private static String shellScript;
    private static long minSpaceRequired;


    static void initialize(String path, int n, String shellScript, long minSpaceRequired) {

        if(path.endsWith("/"))
            binsPath = path + "bin";
        else
            binsPath = path + "/" + "bin";

        numBins = n;
        binLocked = new boolean[n+1];
	Mutex.shellScript = shellScript;
	Mutex.minSpaceRequired = minSpaceRequired;
        
    }


    private synchronized static boolean lockBin(int n) {
        if(n >= binLocked.length) {
            OrderProcessor.print("Tried to LOCK non-existent bin (#" + n + ") in CHIPS. Number of bins is " + numBins, OrderProcessor.WARN);
            return false;
        }
        else if(binLocked[n])
            return false;
        else {
            binLocked[n] = true;
            return true;
        }
    }


    static void unlockBin(String path) {
	unlockBins(new String[][] {{path}});
    }


    static void unlockBins(String[][] path) {

	for(int i = 0; i < path.length; i++) {
            int index = path[i][0].lastIndexOf("/bin");
            int bin = Integer.parseInt(path[i][0].substring(index+4));
            unlockBin(bin);
	}

    }



    private synchronized static void unlockBin(int n) {
        if(n >= binLocked.length)
            OrderProcessor.print("Tried to UNLOCK non-existent bin (#" + n + ") in CHIPS. Number of bins is " + numBins, OrderProcessor.WARN);
        else if( ! binLocked[n])
            OrderProcessor.print("Tried to UNLOCK previously unlocked bin (#" + n + ") in CHIPS", OrderProcessor.WARN);
        else
            binLocked[n] = false;
    }


    synchronized static String getLockedBinsList() {
        StringBuffer s = new StringBuffer("[ ");
        for(int i = 1; i <= numBins; i++)
            if(binLocked[i])
                s.append(i + ",");
        s.setCharAt(s.length() - 1, ']');
        return s.toString();
    }


    static long[] getFreeSpace(int[] bins, MQHandler callingThread) throws IOException, InterruptedException {

	String cmd = shellScript;
	for(int i = 0; i < bins.length; i++)
	    cmd += " " + binsPath + bins[i];

	String result = callingThread.executeAndGetStream(cmd);

	BufferedReader in = new BufferedReader(new StringReader(result));

	long[] available = new long[bins.length]; 
	String line;
	int i = 0;

	while( (line=in.readLine()) != null) {
	    if(line.trim().length() == 0)
		continue;
	    available[i++] = Long.parseLong(line) * 1024;
	}

	if( i != bins.length )
	    throw new RuntimeException(shellScript + " was given " + bins.length + " arguments, but returned " + i + " results");

	return available;

    }



    private synchronized static String[][] lockFreeBins(long spaceRequired, MQHandler callingThread, boolean allowMultipleBins) throws IOException, InterruptedException {

	int numUnlockedBins = 0;
        for(int i = 1; i <= numBins; i++)
            if( ! binLocked[i] )
		numUnlockedBins++;

	int[] unlockedBins = new int[numUnlockedBins];
	int j=0;
	for(int i = 1; i <= numBins; i++)
            if( ! binLocked[i] )
		unlockedBins[j++] = i;

	long[] freeSpace = getFreeSpace(unlockedBins, callingThread);

	int bestBin = -1;
	long bestAvail = Long.MAX_VALUE;
	for(int i = 0; i < freeSpace.length; i++) {
	    if(freeSpace[i] > spaceRequired && freeSpace[i] < bestAvail) {
		bestBin = unlockedBins[i];
		bestAvail = freeSpace[i];
	    }
	}

	if(bestBin > 0) {
            if( lockBin(bestBin) )
		return new String[][] { {binsPath + bestBin, String.valueOf(bestAvail)} };
	    else
		throw new RuntimeException("Unable to lock bin: " + bestBin);
	}
	else if( ! allowMultipleBins )
	    return null;
	else {
	    int[] orderList = sort(freeSpace);
	    int i;
	    long totalSpace = 0;
	    for(i=orderList.length-1; i >= 0; i--) {
		long space = freeSpace[orderList[i]];
		if(space < minSpaceRequired)
		    break;
		totalSpace += space;
	        if(totalSpace >= spaceRequired)
		    break;
	    }

	    if(totalSpace < spaceRequired)
		return null;
	    else {
		int numRequiredBins = orderList.length - i;
		String[][] freeBins = new String[numRequiredBins][2];
		for(i=0; i < numRequiredBins; i++) {
		    int bin = orderList[orderList.length-1-i];
		    if( lockBin(unlockedBins[bin]) ) {
			freeBins[i][0] = binsPath + unlockedBins[bin];
			freeBins[i][1] = String.valueOf(freeSpace[bin]);
		    }
		    else
		        throw new RuntimeException("Unable to lock bin: " + bestBin);
		}
		return freeBins;
	    }
	}

    }



    private static int[] sort(long[] arr) {

        int length = arr.length;

        long[] sorted = (long[]) arr.clone();

        Arrays.sort(sorted);

        int[] orderList = new int[length];

        for(int i = 0; i < length; i++) {
            for(int j = 0; j < length; j++) {
                if(arr[j] == sorted[i]) {
                    orderList[i] = j;
                    break;
                }
            }
        }

        return orderList;
    }



    static String getFreeBin(long spaceRequired, MQHandler callingThread) {
	String[][] freeBins = getFreeBins(spaceRequired, callingThread, false);
	if(freeBins.length != 1)
	    throw new RuntimeException("Got " + freeBins.length + " bins even though allowMultipleBins was false");
	return freeBins[0][0];
    }


    static String[][] getFreeBins(long spaceRequired, MQHandler callingThread, boolean allowMultipleBins) {

        callingThread.print("Starting getFreeBin()...", MQHandler.V_IMP);

        long sleepTime = 10 * 60 * 1000; // 10 minutes
	int numTries = 6;

	while(true) {

            try {
	        String[][] freeBins = lockFreeBins(spaceRequired, callingThread, allowMultipleBins);

	        if(freeBins != null) {
                    callingThread.print("Exiting getFreeBin()", MQHandler.V_IMP);
		    return freeBins;
	        }
	        else {
		    if(numTries >=6) {
			numTries=0;
			String msg = "Going into wait for free bin in " + binsPath + " for " + spaceRequired + " bytes at " + new Date();
                        callingThread.print(msg, MQHandler.V_IMP);
                        callingThread.flushDisplay();
			callingThread.mail(MQHandler.UNEXP_WARNING, msg);
		    }
                    Thread.sleep(sleepTime);
		    numTries++;
                }
            }
            catch(Throwable t) {
                callingThread.handleException(t, "thrown in getFreeBins(" + spaceRequired + ",ref," + allowMultipleBins + ")");
            }

	}

    }

}



/*
    static String[][] getFreeBins(long spaceRequired, MQHandler callingThread, boolean allowMultipleBins) {

        callingThread.print("Starting getFreeBin()...", MQHandler.V_IMP);
        int bin = 0;
        String volumeName = "";
        long sleepTime = 10 * 60 * 1000;
	int numTries = 6;

        while(true) {
            bin++;
            if(bin > numBins) {
                bin = 1;
                try {
		    if(numTries >=6) {
			numTries=0;
			String msg = "Going into wait for free bin in " + binsPath + " for " + spaceRequired + " KB at " + new Date();
                        callingThread.print(msg, MQHandler.V_IMP);
                        callingThread.flushDisplay();
			callingThread.mail(MQHandler.UNEXP_WARNING, msg);
		    }
                    Thread.sleep(sleepTime);
		    numTries++;
                }
                catch(InterruptedException ie) {
                    callingThread.handleException("Wait for CHIPS download space to appear was interrupted");
                }
            }
            if( ! lockBin(bin))
                continue;

            volumeName = binsPath + bin;
            
            try {
                if(callingThread.execute(shellScript + " " + volumeName + " " + spaceRequired, MQHandler.SILENT) == 0)
                    break;
                else
                    unlockBin(bin);
            }
            catch(Exception e) {
                unlockBin(bin);
            }
        }

        callingThread.print("Exiting getFreeBin(). Using bin: " + bin, MQHandler.V_IMP);

        return volumeName;

    }

*/

