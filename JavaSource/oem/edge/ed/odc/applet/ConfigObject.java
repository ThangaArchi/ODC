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

public class ConfigObject
{       private Hashtable sections;
   private Properties properties;

   public ConfigObject()
      {
         sections = new Hashtable();
         properties = new Properties();
      }
   public void addSection(ConfigSection section)
      {
        // first, check to see if we have sections by that name already
         Vector v = (Vector)sections.get(section.getName().toUpperCase());
         if (v == null)
         {
           // commented out by Navneet Gupta on 12/28/2000
           // System.out.println("creating new section");

           // create a new one if one doesn't already exist
            v = new Vector();
         }
         v.addElement(section);

        // commented out by Navneet Gupta on 12/28/2000
        // System.out.println("adding " + section.getName());

         sections.put(section.getName().toUpperCase(), v);
      }
  // JMC 5/30/01 - No URLDecoder function!
   public static String decode(String s) {
      if (s.indexOf('%') >= 0) {
         StringBuffer sb = new StringBuffer();
         char arr[] = s.toCharArray();
         int len = arr.length;
         for(int i=0; i < len; i++) {
            if (arr[i] == '%') {
               if (i+2 < len) {
                  int d1 = Character.digit(arr[++i], 16);
                  int d2 = Character.digit(arr[++i], 16);
                  if (d1 == -1 || d2 == -1) {
                     System.out.println("ConfigObject: Error parsing [" + 
                                        s + "] idx = " + (i-2));
                  }
                  d1 = (d1 << 4) | d2;
                  sb.append((char)d1);
               } else {
                  System.out.println("ConfigObject: Error parsing [" + 
                                     s + "] idx = " + i);
                  break;
               }
            } else {
               sb.append(arr[i]);
            }
         }
         s = sb.toString();
      }
      return s;
   }
  // JMC 5/30/01 - Was using URLEncode ... change to a more conservative algo
   public static String encode(String s) {
     // return java.net.URLEncoder.encode(s);
     /*
     ** The only encoded chars are "<>%=/", chars < space and chars >= 127
     **                        (including cr/lf, ...)
     ** A leading and/or trailing space is also encoded, all others are
     ** left alone
     **
     ** Encoding is %dd where dd are hex digits for the hex value encode
     **
     ** Check if needs encoding first, if not, simply return original string
     */
      char arr[] = s.toCharArray();
      int len = arr.length;
      for(int i=0; i < len; i++) {
         char ch = arr[i];
         if (ch < ' '  || ch > (char)127 || ch == '%' ||
             ch == '<' || ch == '>'      || ch == '=' || 
             ch == '/' || 
             ((ch == ' ') && (i == 0 || i == len-1))) {
            
            StringBuffer sb = new StringBuffer();
            for(i=0; i < len; i++) {
               ch = arr[i];
               if (ch < ' '  || ch > (char)127 || ch == '%' ||
                   ch == '<' || ch == '>'      || ch == '=' || 
                   ch == '/' || 
                   ((ch == ' ') && (i == 0 || i == len-1))) {
                  
                  byte b = (byte)arr[i];
                  sb.append('%');
                  char c = Character.forDigit((b >> 4) & 0xf, 16);
                  sb.append(c);
                  c = Character.forDigit(b & 0xf, 16);
                  sb.append(c);
               } else {
                  sb.append(arr[i]);
               }
            }
            s = sb.toString();
            break;
         }
      }
      return s;
   }
   public void fromString(String inp) throws IOException {
      load(new BufferedReader(new StringReader(inp)));
   }
   public boolean getBoolProperty(String key, boolean defValue)
      {
         String val = getProperty(key);
         if (val != null)
         {
            return Boolean.valueOf(val).booleanValue();
         }
         else
         {
            return defValue;
         }
      }
   public int getIntProperty(String key, int defValue)
      throws NumberFormatException
      {
         String val = getProperty(key);
         if (val != null)
         {
            return Integer.parseInt(val);
         }
         else
         {
            return defValue;
         }
      }
   public long getLongProperty(String key, long defValue)
      throws NumberFormatException
      {
         String val = getProperty(key);
         if (val != null)
         {
            return Long.parseLong(val);
         }
         else
         {
            return defValue;
         }
      }
   public String getProperty(String key)
      {
         return properties.getProperty(key.toUpperCase());
      }
   public String getProperty(String key, String defValue)
      {
         return properties.getProperty(key.toUpperCase(), defValue);
      }
   public Enumeration getPropertyNames()
      {
         return properties.propertyNames();
      }
   public Vector getSection(String sectionName)
      {
        //System.out.println("getting section " + sectionName);
         Vector v = (Vector)sections.get(sectionName.toUpperCase());
         if (v == null)
         {
            return new Vector();
         }

         return v;
      }
   public Enumeration getSectionNames()
      {
         return sections.keys();
      }
   protected boolean isCurrentSection(String name)
      {
         return false;
      }
   protected void load(BufferedReader rdr)
      throws IOException
      {
         String sectionName = null;
         ConfigSection section = null;

         String line;
         rdr.mark(512);
         while ((line = rdr.readLine()) != null)
         {
           // first remove leading and trailing blanks
            line = line.trim();
           //System.out.println(line);

            if (line.length() > 0)
            {
               if (line.startsWith("</"))
               {
                 // end current child section
                  sectionName = line.substring(2, line.length()-1).trim().toUpperCase();
                  sectionName = decode(sectionName);

                  if (isCurrentSection(sectionName))
                  {
                    // end of current section, exit
                    //System.out.println("ending " + sectionName);
                     break;
                  }
                  else
                  {
                    // not current section, close current section, reset to previous mark
                    // and get out - assume end of parent section...
                    //System.out.println("ending " + sectionName + " resetting mark - current section ");
                     rdr.reset();
                     break;
                  }
               }
               else if (line.startsWith("<"))
               {
                 // new section
                  sectionName = line.substring(1, line.length()-1).trim().toUpperCase();
                  sectionName = decode(sectionName);

                  Vector v = (Vector)sections.get(sectionName);
                  if (v == null)
                  {
                     v = new Vector();
                  }
                  section = new ConfigSection(sectionName);
                  v.addElement(section);
                  sections.put(sectionName, v);

                 //System.out.println("starting section " + sectionName);

                  section.load(rdr);
               }
               else
               {
                 // section data
                  String l = line.trim();
                  if (l.length() > 0)
                  {
                     if (!l.startsWith("#"))
                     {
                        int index = line.indexOf("=");
                        if (index != -1)
                        {
                           String key = line.substring(0, index).trim();
                           String value = line.substring(index+1).trim();
                           key = decode(key).toUpperCase();
                           value = decode(value);

                          //stem.out.println("reading " + key + " = " + value);
                           properties.put(key, value);
                        }
                     }
                  }
               }
               rdr.mark(512);
            }
         }
        //System.out.println("finished load");
      }
   public void removeProperty(String key)
      {
         properties.remove(key);
      }
   public boolean removeSection(String sectionName)
      {
         Object obj = sections.remove(sectionName.toUpperCase());
         if (obj == null)
         {
            return false;
         }
         else
         {
            return true;
         }
      }
   public boolean removeSection(ConfigSection section)
      {
        // get the list of sections
         Vector v = (Vector)sections.get(section.getName().toUpperCase());
         if (v != null)
         {
            return v.removeElement(section);
         }

         return false;
      }
   public void save(OutputStream out)
      {
         PrintWriter ps = new PrintWriter(out, true);
         Enumeration keys = properties.propertyNames();
         while (keys.hasMoreElements())
         {
            String key = (String)keys.nextElement();
            String value = properties.getProperty(key, "");
                        
           // JMC 5/30/01 - Encode it
            key   = encode(key);
            value = encode(value);

            ps.println(key + "=" + value);
         }

         Enumeration sectionNames = sections.keys();
         while (sectionNames.hasMoreElements())
         {
            String key = (String)sectionNames.nextElement();
            Vector v = (Vector)sections.get(key);
            for (int i=0; i<v.size(); i++)
            {
               ConfigSection section = (ConfigSection)v.elementAt(i);
               section.save(out);
            }
         }
      }
   public Object setBoolProperty(String key, boolean value)
      {
         Boolean b = new Boolean(value);
         return setProperty(key, b.toString());
      }
   public Object setIntProperty(String key, int value)
      {
         return setProperty(key, Integer.toString(value));
      }
   public Object setLongProperty(String key, long value)
      {
         return setProperty(key, Long.toString(value));
      }
   public Object setProperty(String key, String value)
      {
         return properties.put(key.toUpperCase(), value);
      }
   public String toString() {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      save(out);
      return out.toString();
   }
}
