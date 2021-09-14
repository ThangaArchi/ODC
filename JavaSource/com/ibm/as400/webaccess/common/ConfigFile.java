package com.ibm.as400.webaccess.common;

import java.io.*;
import java.util.*;

public class ConfigFile extends ConfigObject
{
        public ConfigFile()
        {
                super();
        }

        public ConfigFile(String filename)
                throws IOException
        {
                this();
                load(filename);
        }

        public ConfigFile(InputStream in)
                throws IOException
        {
                this();
                load(in);
        }

        public void load(String filename)
                throws IOException
        {
                FileInputStream in = new FileInputStream(filename);
                load(in);
                in.close();
        }

        public void load(InputStream in)
                throws IOException
        {
                BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
                load(rdr);
        }

        public void store(String filename)
                throws IOException
        {
                FileOutputStream out = new FileOutputStream(filename);
                store(out);
        }

        public void store(OutputStream out)
                throws IOException
        {
                save(out);
        }

        static public void main(String[] args) {
           
           try {
              ConfigFile cfgfile = new ConfigFile();
              String key1val = "value1<>#@!</> %34 with spaces\nand many spaces\n\nto boot";
              
              System.out.println("file created");
              ConfigSection test1 = new ConfigSection("test section 1");
              System.out.println("section created");
              test1.setProperty("key1<>%4", key1val);
              test1.setIntProperty("key2", 12345);
              test1.setBoolProperty("key3", true);
              test1.setBoolProperty("key4", false);
              
              ConfigSection test11 = new ConfigSection("test section 1-1");
              test11.setProperty("key3", "value3");
              test11.setProperty("key4", "value4");
              
              ConfigSection test2 = new ConfigSection("test section 2");
              test2.setProperty("key5", "value5");
              test2.setProperty(" key6 ", " value6 ");
              
              test1.addSection(test11);
              test1.addSection(test11);
              
              cfgfile.addSection(test1);
              cfgfile.addSection(test2);
              cfgfile.addSection(test1);
              cfgfile.addSection(test2);
              
              System.out.println("writing");
              cfgfile.store(args[0]);
              
              ConfigFile cfile = new ConfigFile(args[0]);
              
              Enumeration sections = cfile.getSectionNames();
              while (sections.hasMoreElements())
              {
                 String sectionName = (String)sections.nextElement();
                 System.out.println(sectionName);
                 Vector v = cfile.getSection(sectionName);
                 for (int i=0; i<v.size(); i++)
                 {
                    ConfigSection section = (ConfigSection)v.elementAt(i);
                    System.out.println("section = " + sectionName);
                    section.save(System.out);
                 }
              }
              
              Vector v = cfile.getSection("name");
              if (v == null)
              {
                 System.out.println("non-existant section test ok");
              }
              else
              {
                 System.out.println("non-existant section test failed");
              }
              
              v = cfile.getSection("Test Section 1");
              for (int i=0; i<v.size(); i++)
              {
                 ConfigSection testSection = (ConfigSection)v.elementAt(i);
                 if (testSection.getProperty("key1<>%4", "").equals(key1val))
                 {
                    System.out.println("ok");
                 }
                 else
                 {
                    System.out.println("failed");
                 }
                 if (testSection.getBoolProperty("key3", false))
                 {
                    System.out.println("ok");
                 }
                 else
                 {
                    System.out.println("failed");
                 }
                 
                 if (testSection.getBoolProperty("key4", true))
                 {
                    System.out.println("failed");
                 }
                 else
                 {
                    System.out.println("ok");
                 }
                 
                 if (testSection.getIntProperty("key2", 0) == 12345)
                 {
                    System.out.println("ok");
                 }
                 else
                 {
                    System.out.println("failed");
                 }
              }
              
              System.out.println("\n\ntoString output -->\n" + cfile.toString());
           }
           catch (Exception e)
           {
              e.printStackTrace();
           }
        }
}
