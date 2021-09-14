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
// File Name:    ByteArrayWrapper.java
//
// Classes:      ByteArrayWrapper
//
////////////////////////////////////////////////////////////////////////
//
// CHANGE ACTIVITY:
//   Flag PTR/DCR   Release  Date        Userid    Comments
//    $D0=P9A23760  v5r3m0   01/30/2003  csmith    Created.
// END CHANGE ACTIVITY
//
////////////////////////////////////////////////////////////////////////

package oem.edge.ets.fe;

//import com.ibm.as400.webaccess.util.*;


/**
 * This class is useful for handling byte arrays as keys inside a Hashtable.
**/
final class ByteArrayWrapper
{
  private byte[] bytes_;

  public ByteArrayWrapper(byte[] bytes)
  {
    bytes_ = bytes;
  }

  public byte byteAt(int index)
  {
    return bytes_[index];
  }

  public boolean equals(Object o)
  {
    ByteArrayWrapper comp = (ByteArrayWrapper)o;
    if (comp.bytes_.length == bytes_.length)
    {
      for (int i=0; i<comp.bytes_.length; ++i)
      {
        if (comp.bytes_[i] != bytes_[i]) return false;
      }
      return true;
    }
    return false;
  }

  public byte[] getBytes()
  {
    return bytes_;
  }

  public int hashCode()
  {
    return bytes_.length;
  }

  public int length()
  {
    return bytes_.length;
  }
}

