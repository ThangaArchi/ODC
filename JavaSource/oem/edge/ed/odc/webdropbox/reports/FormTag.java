package oem.edge.ed.odc.webdropbox.reports;

import java.lang.*;
import java.io.*;

public class FormTag extends org.apache.struts.taglib.html.FormTag {
   protected String anchor = null;
   
   public String getAnchor()         { return anchor; }
   public void   setAnchor(String a) { anchor = a; }
   
  /* 1.2 and above
   // We actually don't render anchor here ... we append to action
   protected void renderOtherAttributes(StringBuffer b) {
   
      super.renderOtherAttributes(b);  // Let mom&pop do it too (really no need)
      renderAttribute(b, "anchor", getAnchor());
      
   }
  */
  // For 1.1, have to catch it here
   protected String renderFormStartElement() {
      String s = super.renderFormStartElement();
      if (s != null && anchor != null) {
         int idx = s.indexOf("action=\"");
         if (idx >= 0) {
            String head = s.substring(0,idx+8);
            int idx2 = s.indexOf('"', idx+8);
            if (idx2 > 0) {
               String tail = s.substring(idx2);
               String mid  = s.substring(idx+8, idx2);
               
               String newmid = mid + "#" + anchor;
               s = head + newmid + tail;
            }
         }
      }
      return s;
   }

   
  // If an anchor was specified, add it to URL  (sigh ... 1.2 and above)
  /*
   protected void renderAction(StringBuffer results) {
      
      HttpServletResponse response =
         (HttpServletResponse) this.pageContext.getResponse();
      
      results.append(" action=\"");
      results.append(
         response.encodeURL(
            TagUtils.getInstance().getActionMappingURL(
               this.action,
               this.pageContext)  
            + (anchor!=null?"#"+anchor:"")));  // This is the line that changed
      
      results.append("\"");
   }
  */
}

