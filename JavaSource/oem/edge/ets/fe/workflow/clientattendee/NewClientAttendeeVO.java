/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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

package oem.edge.ets.fe.workflow.clientattendee;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.workflow.core.WorkflowObject;


/**
 * Class       : NewClientAttendeeVO
 * Package     : oem.edge.ets.fe.workflow.clientattendee
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewClientAttendeeVO extends WorkflowObject {

	 private String title = null;

	    private String fname = null;
	    private String lname = null;

	    private String company = null;
	    
	    private String id = null;
	    
	    private String[] attList = null;
	    
	    private String[] Afname  = null;
	    private String[] Alname = null;
	    private String[] Acompany = null;
	    private String[] Atitle = null;
	    
	    
		/**
		 * @return Returns the attList.
		 */
		public String[] getAttList() {
			return attList;
		}
		/**
		 * @param attList The attList to set.
		 */
		public void setAttList(String[] attList) {
			this.attList = attList;
			Afname = new String[attList.length];
			Alname = new String[attList.length]; 
			Acompany = new String[attList.length];
			Atitle = new String[attList.length];
			System.out.println("In the setter of attList. attList has length "+attList.length);
			try{
			for (int i =0; i<attList.length; i++)
			{
			    System.out.println(attList[i]);
			    System.out.println(attList[i].split("\\$").length);
			    if(attList[i].split("\\$").length < i+1)
			    {
			    	Afname[i] = null;
			    	Alname[i] = null;
			    	Atitle[i] = null;
			    	Acompany[i] = null;
			    }
			    else{
			    	if(attList[i].split("\\$").length>0)
			    		Afname[i]=URLDecoder.decode(attList[i].split("\\$")[0]);
			    	if(attList[i].split("\\$").length>1)
			    		Alname[i]=URLDecoder.decode(attList[i].split("\\$")[1]);
			    	if(attList[i].split("\\$").length>2)
			    		Atitle[i]=URLDecoder.decode(attList[i].split("\\$")[2]);
			    	if(attList[i].split("\\$").length>3)
			    		Acompany[i]=URLDecoder.decode(attList[i].split("\\$")[3]);
			    }
			    System.out.println(Afname[i]+" "+Alname[i]+" "+Atitle[i]+" "+Acompany[i]);
			}   
			}catch(Exception e)
			{
				System.err.println(e);
			}
			System.out.println("........Done parsing attList");
		}
		
        /**
         * @return Returns the atitle.
         */
        public String[] getAtitle() {
            return Atitle;
        }
        /**
         * @param atitle The atitle to set.
         */
        public void setAtitle(String[] atitle) {
            Atitle = atitle;
        }
	    /**
	     * Get title
	     * @return String[]
	     */
	    public String getTitle() {
	        return title;
	    }

	    /**
	     * Set title
	     * @param <code>String[]</code>
	     */
	    public void setTitle(String t) {
	        this.title = t;
	    }

	    
	    
	    /**
	     * Get company
	     * @return String[]
	     */
	    public String getCompany() {
	        return company;
	    }

	    /**
	     * Set company
	     * @param <code>String[]</code>
	     */
	    public void setCompany(String c) {
	        this.company = c;
	    }

		/**
		 * @return Returns the id.
		 */
		public String getId() {
			return id;
		}
		/**
		 * @param id The id to set.
		 */
		public void setId(String id) {
			this.id = id;
		}
		/**
		 * @return Returns the fname.
		 */
		public String getFname() {
			return fname;
		}
		/**
		 * @param fname The fname to set.
		 */
		public void setFname(String fname) {
			this.fname = fname;
		}
		/**
		 * @return Returns the lname.
		 */
		public String getLname() {
			return lname;
		}
		/**
		 * @param lname The lname to set.
		 */
		public void setLname(String lname) {
			this.lname = lname;
		}
		
		
		
        /**
         * @return Returns the acompany.
         */
        public String[] getAcompany() {
            return Acompany;
        }
        /**
         * @param acompany The acompany to set.
         */
        public void setAcompany(String[] acompany) {
            Acompany = acompany;
        }
        /**
         * @return Returns the afname.
         */
        public String[] getAfname() {
            return Afname;
        }
        /**
         * @param afname The afname to set.
         */
        public void setAfname(String[] afname) {
            Afname = afname;
        }
        /**
         * @return Returns the alname.
         */
        public String[] getAlname() {
            return Alname;
        }
        /**
         * @param alname The alname to set.
         */
        public void setAlname(String[] alname) {
            Alname = alname;
        }
        

	    
	    public void reset() {

	        // Reset values are provided as samples only. Change as appropriate.

	        title = null;
	        fname = null;
	        lname = null;
	        company = null;

	    }

	    public ActionErrors validate(ActionMapping mapping,
	            HttpServletRequest request) {

	        ActionErrors errors = new ActionErrors();
	        // Validate the fields in your form, adding
	        // adding each error to this.errors as found, e.g.

	        // if ((field == null) || (field.length() == 0)) {
	        //   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
	        // }
	        return errors;

	    }
		

}
