package oem.edge.ed.sd.ff2db;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/*******************************************************/
/* File: StatusFile.java                               */
/* Author: Athar Tayyab / Jesse Vitrone                */
/* Date: 8/24/00                                       */
/* Updates:                                            */
/*      01/07/01                                       */
/*      03/14/01                                       */
/*******************************************************/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.String;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import oem.edge.ed.sd.util.*;
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

public class StatusFile {
    
    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    short status;
    Hashtable fileStatus = new Hashtable();
    Hashtable statusFileLog = new Hashtable();
    ResourceBundle prop; /* TODO: read files from bundle - dont' hardcode */
    String fileName, baseDir;

    static final short NOTHING_CHANGED       = 1;
    static final short SOMETHING_CHANGED     = 2;
    static final short READ_ERROR            = 3;
    static final short WRITE_ERROR           = 4;
    static final short SUCCESS               = 5;

    static final short STATUS_GOOD           = 6;
    static final short STATUS_FILE_NOT_FOUND = 7;
    static final short STATUS_BAD_FILE       = 8;

    public StatusFile (String dirName) {
	if (dirName.charAt(dirName.length()-1) != '/')
	    dirName += "/";

	this.baseDir = dirName;
	this.fileName = dirName + "status.file";

	status = readFile();
    }


    public String getFileName () {
	return fileName;
    }


    public short readFile () {
	String line;
	SimpleStringTokenizer tok;

	try {
	    BufferedReader in = new BufferedReader(new FileReader(fileName));
	    fileStatus.clear();
	    while (in.ready()) {
		line = in.readLine().trim();
		tok = new SimpleStringTokenizer(line, ';');
		fileStatus.put(tok.nextToken(), tok.nextToken());
	    }
	    in.close();
	} catch (java.io.IOException ioe) {
	    return STATUS_FILE_NOT_FOUND;
	} catch (java.util.NoSuchElementException nse) {
	    return STATUS_BAD_FILE;
	}
	return STATUS_GOOD;
    }

    public short getStatus () {
	return status;
    }

    public Vector getFilesWithZeroTimestamp () {
	Enumeration keys = fileStatus.keys();
	long lastModNum;
	String lastMod, key;
	Vector badFiles = new Vector();
	
	while (keys.hasMoreElements()) {
	    key = (String) keys.nextElement();
	    lastMod = (String) fileStatus.get(key);
	    try {
		lastModNum = Long.parseLong(lastMod);
	    } catch (java.lang.NumberFormatException nfe) { lastModNum = -1; }

	    if (lastModNum == 0)
		badFiles.addElement(key);
	}
	return badFiles;
    }

    public boolean hasFileChanged (String fileName) {

	Enumeration keys = fileStatus.keys();
	File        file;
	String      key, lastMod, lastModFromStatus;

	while (keys.hasMoreElements()) {

	    key = (String) keys.nextElement();
	    if (key.equals(fileName)) {

			lastModFromStatus = (String) fileStatus.get(key);
			file = new File(key);
			lastMod = new Long(file.lastModified()).toString();
			if (lastModFromStatus.compareTo(lastMod) != 0)
		    	return true;
			else 
		    	return false;
	    }
	}

	return false;
    }

//     public long getTimeStamp (String fileName) {
// 	Enumeration keys = fileStatus.keys();
// 	String lastMod, key;
// 	long   lastModNum;

// 	while (keys.hasMoreElements()) {
// 	    key = (String) keys.nextElement();
// 	    if (key.equals(fileName)) {
// 		try {
// 		    lastMod = (String) fileStatus.get(key);
// 		    lastModNum = Long.parseLong(lastMod);
// 		    return lastModNum;
// 		} catch (java.lang.NumberFormatException nfe) {
// 		    return -1;
// 		}	    
// 	    }
// 	}
// 	return -1;
//     }
    

    public void initFile (Vector files) {
	File        file;
	Enumeration enum = files.elements();
	
	try {
	    BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
	    while (enum.hasMoreElements()) {
			file = new File(baseDir + (String) enum.nextElement());
			System.out.println("StatusFile->initFile: " + file + "\n");
			out.write(file.getAbsolutePath() + ";1\n");
	    }
	    out.close();
	} catch (java.io.IOException ioe) {
	    status = STATUS_BAD_FILE;
	} 
	
	status = readFile();
    }

    public short updateFile () {
	Enumeration enum = fileStatus.keys();	
	String      fn;
	File        file;
	try {
	    BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
	    while (enum.hasMoreElements()) {
		fn = (String) enum.nextElement();
		file = new File(fn);
		out.write(file.getAbsolutePath() + ";" + 
			  file.lastModified() + "\n");
	    }
	    out.close();
	} catch (java.io.IOException ioe) {
	    return WRITE_ERROR;
	}
	readFile();
	return SUCCESS;
    }
    
    
	public short updateTechInfoStatusFile () {
	 Enumeration enum = fileStatus.keys();	
	 String      fn;
	 File        file;
	 try {
		 BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		 while (enum.hasMoreElements()) {
		 fn = (String) enum.nextElement();
		 int keyIndex = fn.lastIndexOf("/");
		 String temp = fn.substring(keyIndex + 1);
		 File f = new File(baseDir + temp);
		 out.write(fn + ";" + f.lastModified() + "\n");
		 
		 statusFileLog.put(" updateTechInfoStatusFile " + temp, f.getAbsolutePath() + " lastMod " + f.lastModified());
		 
		 }
		 out.close();
	 } catch (java.io.IOException ioe) {
		 return WRITE_ERROR;
	 }
	 readFile();
	 return SUCCESS;
	 }
    
    

    public void addKeyToStatusFile(String key)
    {
    	fileStatus.put(key, "1");
    }
    
    public Hashtable getStatusFileDetails()
    {
    	return fileStatus;
    }
    
    public Hashtable getStatusFileLog()
    {
    	return statusFileLog;
    }
    
    
}
