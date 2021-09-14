//////////////////////////////////////////////////////////////////////
//
// IBM Confidential
//
// OCO Source Materials
//
// The Source code for this program is not published or otherwise
// divested of its trade secrets, irrespective of what has been
// deposited with the U.S. Copyright Office
//
// 5722-XH2
// (C) Copyright IBM Corp. 2003
//
////////////////////////////////////////////////////////////////////////
//
// File Name:    MimeMultipartParser.java
//
// Classes:      MimeMultipartParser
//
////////////////////////////////////////////////////////////////////////
//
// CHANGE ACTIVITY:
//   Flag PTR/DCR   Release  Date        Userid    Comments
//   $D0                     01/24/2003  smshao    Created.
//        P9A23760  v5r3m0   01/24/2003  csmith    Fixed up.
// END CHANGE ACTIVITY
//
////////////////////////////////////////////////////////////////////////

package oem.edge.ets.fe;

import java.io.*;
import java.util.*;

//import oem.edge.ets.fe.upload.util.*;


/**
 * Parses multi-part MIME content. Used by file upload.
**/
public final class MimeMultipartParser
{
  private MimeMultipartParser()
  {
  }

  public static final Vector getBodyParts(InputStream in, int contentLength) throws IOException
  {
    Vector bodyParts = new Vector();
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    // Read the InputStream contents into a byte array.
    final byte[] buf = new byte[32768];
    int len = 0;
	int totalLength = 0;
/*
   while ((len = in.read(buf)) != -1){
    	 out.write(buf, 0, len);
    }
 */
 /**/
 	System.out.println("ETS:getBodyParts:: contentLength="+contentLength);
 	
	while (((len = in.read(buf)) != -1) && ((totalLength+len) <= contentLength)) {
	   out.write(buf, 0, len);
	   totalLength += len;
	 }
/**/
	System.out.println("ETS:getBodyParts:: totalLength="+totalLength);
    
    byte[] dataIn = out.toByteArray();

    // now parse the file for sections
    // find the first EOL and assume that terminates the separator string
    int offset = 0;
    while ((dataIn[offset] != 0x0a) && (dataIn[offset] != 0x0d)) ++offset;

    final byte[] separatorLine = new byte[offset]; // This is our separator string pattern.
    System.arraycopy(dataIn, 0, separatorLine, 0, offset);

    // index past end of line characters
    while ((dataIn[offset] == 0x0a) || (dataIn[offset] == 0x0d)) ++offset;

    // Find the next section in the multi-part content. The section
    // will start with the separator line.
    int beginSection = offset;
    while (offset + separatorLine.length < dataIn.length)
    {
      boolean foundSeparator = true;
      for (int i=0; foundSeparator && i<separatorLine.length; ++i)
      {
        int index = offset + i;
        if (index >= dataIn.length || separatorLine[i] != dataIn[index])
        {
          foundSeparator = false;
        }
      }
      if (foundSeparator)
      {
        int sectionLength = offset - beginSection;
        if (sectionLength > 0)
        {
          WebAccessBodyPart part = new WebAccessBodyPart(dataIn, beginSection, sectionLength);
          bodyParts.addElement(part);
        }
        offset += separatorLine.length;

        // Skip remaining EOLs.
        while (offset < dataIn.length && (dataIn[offset] == 0x0a) || (dataIn[offset] == 0x0d)) ++offset;

        // Start of next section.
        beginSection = offset;
      }
      else
      {
        ++offset;
      }
    }

    // The last section in the multi-part content should be terminated by the separator line plus
    // extra chars, thus there is no important data, so we don't need to worry about adding
    // it to our Vector.

    return bodyParts;
  }

  public static final Vector getBodyParts(InputStream in) throws IOException
  {
	Vector bodyParts = new Vector();
	ByteArrayOutputStream out = new ByteArrayOutputStream();

	// Read the InputStream contents into a byte array.
	final byte[] buf = new byte[32768];
	int len = 0;
	int totalLength = 0;

	while ((len = in.read(buf)) != -1){
		 out.write(buf, 0, len);
	}

	byte[] dataIn = out.toByteArray();

	// now parse the file for sections
	// find the first EOL and assume that terminates the separator string
	int offset = 0;
	while ((dataIn[offset] != 0x0a) && (dataIn[offset] != 0x0d)) ++offset;

	final byte[] separatorLine = new byte[offset]; // This is our separator string pattern.
	System.arraycopy(dataIn, 0, separatorLine, 0, offset);

	// index past end of line characters
	while ((dataIn[offset] == 0x0a) || (dataIn[offset] == 0x0d)) ++offset;

	// Find the next section in the multi-part content. The section
	// will start with the separator line.
	int beginSection = offset;
	while (offset + separatorLine.length < dataIn.length)
	{
	  boolean foundSeparator = true;
	  for (int i=0; foundSeparator && i<separatorLine.length; ++i)
	  {
		int index = offset + i;
		if (index >= dataIn.length || separatorLine[i] != dataIn[index])
		{
		  foundSeparator = false;
		}
	  }
	  if (foundSeparator)
	  {
		int sectionLength = offset - beginSection;
		if (sectionLength > 0)
		{
		  WebAccessBodyPart part = new WebAccessBodyPart(dataIn, beginSection, sectionLength);
		  bodyParts.addElement(part);
		}
		offset += separatorLine.length;

		// Skip remaining EOLs.
		while (offset < dataIn.length && (dataIn[offset] == 0x0a) || (dataIn[offset] == 0x0d)) ++offset;

		// Start of next section.
		beginSection = offset;
	  }
	  else
	  {
		++offset;
	  }
	}

	// The last section in the multi-part content should be terminated by the separator line plus
	// extra chars, thus there is no important data, so we don't need to worry about adding
	// it to our Vector.

	return bodyParts;
  }

}
