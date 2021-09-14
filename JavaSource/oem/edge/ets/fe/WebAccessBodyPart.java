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
// File Name:    WebAccessBodyPart.java
//
// Classes:      WebAccessBodyPart
//
////////////////////////////////////////////////////////////////////////
//
// CHANGE ACTIVITY:
//   Flag PTR/DCR   Release  Date        Userid    Comments
//   $D0                     01/24/2003  smshao    Created.
//        P9A23760           01/24/2003  csmith    Fixed up.
// END CHANGE ACTIVITY
//
////////////////////////////////////////////////////////////////////////

package oem.edge.ets.fe;

import java.io.*;
import java.util.*;

//import oem.edge.etd.fe.upload.util.*;


/**
 * Represents a section in multi-part MIME content.  Used by MimeMultipartParser.
**/
public final class WebAccessBodyPart
{
  private int offset_;
  private int length_;
  private byte[] data_;

  private final Hashtable dispositions_;
  private final Hashtable headers_;

  static int fnum = 0;
//  private static final ByteArrayWrapper FORM_CHARSET_BYTES = new ByteArrayWrapper(getISOBytes("form-charset"));
  private static final ByteArrayWrapper CONTENT_DISPOSITION_BYTES = new ByteArrayWrapper(getISOBytes(Defines.FILE_MULTIPART_CONTENT_DISPOSITION));
  private static final byte[] EMPTY_BYTES = new byte[0];

  private static final byte[] getISOBytes(String s)
  {
    try
    {
      return s.getBytes(Defines.FILE_MULTIPART_ENCODING_ISO);
    }
    catch(UnsupportedEncodingException e)
    {
      return EMPTY_BYTES;
    }
  }

  // The bytes are assumed to be in ISO-8859-1, pursuant to the various MIME RFCs.
  public WebAccessBodyPart(byte[] bodyPart, int offset, int length)
  {
    data_ = bodyPart;
    headers_ = new Hashtable();
    dispositions_ = new Hashtable();

    // Parse out the header fields.
    // The header ends when we find a blank line. After that comes the body, followed by
    // one terminating blank line.
    int current = offset;
    int end = offset+length;
    final ByteArrayOutputStream sb = new ByteArrayOutputStream();
    boolean startingNewLine = false;
    boolean foundEndOfHeader = false;
    byte[] headerName = null;
    byte[] headerValue = null;
    boolean inQuote = false;
    while (current < end && !foundEndOfHeader)
    {
      int c = (int)(bodyPart[current] & 0x00FF);
      switch (c)
      {
        case 0x22: // double-quote
          inQuote = !inQuote;
          sb.write(c);
          break;
        case 0x0D: // CR
          if (current+1 < end && bodyPart[current+1] == 0x0A) // CRLF
          {
            // Found newline.
            if (startingNewLine) foundEndOfHeader = true;
            ++current;
            startingNewLine = true;
          }
          else
          {
            sb.write(c);
            startingNewLine = false;
          }
          break;
        case 0x0A: // LF
          if (startingNewLine) foundEndOfHeader = true;
          startingNewLine = true;
          break;
        case 0x20: // space
        case 0x09: // tab
          if (startingNewLine)
          {
            // Continuance of previous header line
            startingNewLine = false;
            while (current < end && bodyPart[current] == 0x20 || bodyPart[current] == 0x09) ++current;
          }
          else
          {
            sb.write(c);
          }
          break;
        case 0x3A: // colon
          if (!inQuote)
          {
            headerName = sb.toByteArray();
            sb.reset();
          }
          else
          {
            sb.write(c);
          }
          break;
        default:
          if (startingNewLine)
          {
            headerValue = sb.toByteArray();
            headers_.put(new ByteArrayWrapper(headerName), headerValue);
            sb.reset();
            headerValue = null;
            headerName = null;
          }
          sb.write(c);
          startingNewLine = false;
          break;
      }
      ++current;
    }
    if (headerName != null)
    {
      headerValue = sb.toByteArray();
      headers_.put(new ByteArrayWrapper(headerName), headerValue);
    }

    offset_ = current; // Starting offset of content.
    length_ = end - offset_; // Length of content.

    // now trim off the last cr and lf since that is part of the datastream
    int index = offset_ + length_ - 1;
    if (bodyPart[index] == 0x0A) // LF
    {
      --length_;
      if (bodyPart[index-1] == 0x0D) // CR
      {
        --length_;
      }
    }

    // Parse the disposition.
    byte[] disposition = (byte[])headers_.get(CONTENT_DISPOSITION_BYTES);
    if (disposition != null)
    {
      int dispOffset = 0;
      sb.reset();
      boolean inQuotes = false;
      while (dispOffset < disposition.length)
      {
        switch (disposition[dispOffset])
        {
          case 0x20: // space
          case 0x09: // tab
            if (inQuotes) sb.write(0x20);
            break;
          case 0x22: // double-quote
            inQuotes = !inQuotes;
            break;
          case 0x3B: // semicolon
            if (!inQuotes)
            {
              byte[] pair = sb.toByteArray();
              sb.reset();
              int equalIndex = -1;
              for (int i=0; i<pair.length && equalIndex == -1; ++i)
              {
                if (pair[i] == 0x3D) equalIndex = i; // equal sign
              }
              byte[] name = null;
              byte[] value = null;
              if (equalIndex > 0)
              {
                name = new byte[equalIndex];
                System.arraycopy(pair, 0, name, 0, equalIndex);
                value = new byte[pair.length-equalIndex-1];
                System.arraycopy(pair, equalIndex+1, value, 0, value.length);
              }
              else
              {
                name = pair;
                value = EMPTY_BYTES;
              }
              dispositions_.put(new ByteArrayWrapper(name), value);
            }
            else
            {
              sb.write(disposition[dispOffset]);
            }
            break;
          default:
            sb.write(disposition[dispOffset]);
            break;
        }
        ++dispOffset;
      }
      // Do last disposition entry.
      if (sb.size() > 0)
      {
        byte[] pair = sb.toByteArray();
        int equalIndex = -1;
        for (int i=0; i<pair.length && equalIndex == -1; ++i)
        {
          if (pair[i] == 0x3D) equalIndex = i; // equal sign
        }
        byte[] name = null;
        byte[] value = null;
        if (equalIndex > 0)
        {
          name = new byte[equalIndex];
          System.arraycopy(pair, 0, name, 0, equalIndex);
          value = new byte[pair.length-equalIndex-1];
          System.arraycopy(pair, equalIndex+1, value, 0, value.length);
        }
        else
        {
          name = pair;
          value = EMPTY_BYTES;
        }
        dispositions_.put(new ByteArrayWrapper(name), value);
      }
    }
  }

  public InputStream getContentInputStream()
  {
    if (length_ == 0) return null; // Need this so BaseUpload knows to display an error page.
    return new ByteArrayInputStream(data_, offset_, length_);
  }

    
    public String getContentAsString(String charset)
  {
    try
    {
      return new String(data_, offset_, length_, charset);
    }
    catch (UnsupportedEncodingException e)
    {
	// spn WebAccessTrace.log(WebAccessTrace.ERROR, "Unsupported encoding " + charset, e);
      try
      {
        return new String(data_, offset_, length_, Defines.FILE_MULTIPART_ENCODING_ISO);
      }
      catch(UnsupportedEncodingException e2)
      {
        // ISO is always supported.
        return "";
      }
    }
  }

  public byte[] getDisposition(String key)
  {
    try
    {
	//System.out.println("key="+key);
      byte[] value = (byte[])dispositions_.get(new ByteArrayWrapper(key.getBytes(Defines.FILE_MULTIPART_ENCODING_ISO)));
      /*
      if (value != null){
	  System.out.println("value="+ new String(value));
      }
      else{
	  System.out.println("null value=null");
      }
      */
      return value == null ? EMPTY_BYTES : value;
    }
    catch(UnsupportedEncodingException e)
    {
      // ISO is always supported.
      return EMPTY_BYTES;
    }
  }

  public String getDisposition(String key, String charset)
  {
    byte[] value = getDisposition(key);
    try
    {
      return new String(value, charset);
    }
    catch(UnsupportedEncodingException e)
    {
	// spn WebAccessTrace.log(WebAccessTrace.ERROR, "Unsupported encoding " + charset, e);
      try
      {
        return new String(value, Defines.FILE_MULTIPART_ENCODING_ISO);
      }
      catch(UnsupportedEncodingException e2)
      {
        // ISO is always supported.
        return "";
      }
    }
  }

  public byte[] getHeader(String key)
  {
    try
    {
      byte[] value = (byte[])headers_.get(new ByteArrayWrapper(key.getBytes(Defines.FILE_MULTIPART_ENCODING_ISO)));
      return value == null ? EMPTY_BYTES : value;
    }
    catch(UnsupportedEncodingException e)
    {
      // ISO is always supported.
      return EMPTY_BYTES;
    }
  }

}
