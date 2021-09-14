package oem.edge.ed.odc.webdropbox.reports;

public class OptionBean {
   protected String label = "";
   protected String value = "";
   
   public OptionBean() {}
   public OptionBean(String l, String v) { setLabel(l); setValue(v); }
   
   public String getLabel() { return label; }
   public String getValue() { return value; }
   
   public void   setLabel(String v) { label = v; }
   public void   setValue(String v) { value = v; }
}
