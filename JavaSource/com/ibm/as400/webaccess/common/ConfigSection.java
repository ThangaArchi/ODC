package com.ibm.as400.webaccess.common;

import java.io.*;
import java.util.*;

public class ConfigSection extends ConfigObject
{
        private String name;

        public ConfigSection(String name)
        {
                super();
                this.name = name;
        }

        public String getName()
        {
                return name;
        }

        public void setName(String name)
        {
                this.name = name;
        }

        public void save(OutputStream out)
        {
                PrintWriter ps = new PrintWriter(out, true);
                String s = encode(name);
                ps.println("<" + s + ">");

                super.save(out);

                ps.println("</" + s + ">");
        }

        public boolean isCurrentSection(String sectionName)
        {
                return sectionName.equalsIgnoreCase(name);
        }
}
