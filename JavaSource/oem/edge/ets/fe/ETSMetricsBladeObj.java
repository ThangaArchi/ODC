/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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


package oem.edge.ets.fe;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;


//import com.ibm.as400.webaccess.common.*;

public class ETSMetricsBladeObj extends ETSMetricsObj{

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.0";
	protected static SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

	
	protected int hitCount;
	protected int intHitCount;
	protected int extHitCount;
	
	protected String sal;
	protected String fname;
	protected String lname;
	protected String suffix;
	protected String email;
	protected String dayPhone;
	protected String fax;
	protected String evenPhone;
	protected String pagmobPhone;
	protected String jobTitle;
	protected String stAddr1;
	protected String stAddr2;
	protected String city;
	protected String stprov;
	protected String postcode;
	protected String country;
	protected String company;
	protected String userid;
	protected long licenseDate;
	protected boolean isRevoked; 
	
    public ETSMetricsBladeObj(){
		super();
    }
	public ETSMetricsBladeObj(ETSMetricsObj m){
		super();
	}


	
	public String getFieldByName(String name) {
		String s  = "not found";
		
		try{
			Method m = getClass().getMethod("get"+name,null);
			Object o = m.invoke(this,null);
		 	s =String.valueOf(o); 
			 
		}
		catch(Exception e){
			System.out.println("ERROR in getting field"+e);
			e.printStackTrace();
		}
		
		return s;
	}




	/**
	 * @return
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * @return
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @return
	 */
	public String getDayPhone() {
		return dayPhone;
	}

	/**
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return
	 */
	public String getEvenPhone() {
		return evenPhone;
	}

	/**
	 * @return
	 */
	public int getExtHitCount() {
		return extHitCount;
	}

	/**
	 * @return
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @return
	 */
	public String getFname() {
		return fname;
	}

	/**
	 * @return
	 */
	public int getHitCount() {
		return hitCount;
	}

	/**
	 * @return
	 */
	public int getIntHitCount() {
		return intHitCount;
	}

	/**
	 * @return
	 */
	public boolean isRevoked() {
		return isRevoked;
	}
	public String getRevoked(){
		if(isRevoked)
			return "Revoked";
		else
			return "";	
	}

	/**
	 * @return
	 */
	public String getJobTitle() {
		return jobTitle;
	}

	/**
	 * @return
	 */
	public long getLicenseDate() {
		return licenseDate;
	}
	public String getLicenseDateStr(){
		return df.format(new java.util.Date(licenseDate));		
	}

	/**
	 * @return
	 */
	public String getLname() {
		return lname;
	}

	/**
	 * @return
	 */
	public String getPagmobPhone() {
		return pagmobPhone;
	}

	/**
	 * @return
	 */
	public String getPostcode() {
		return postcode;
	}

	/**
	 * @return
	 */
	public String getSal() {
		return sal;
	}

	/**
	 * @return
	 */
	public String getStAddr1() {
		return stAddr1;
	}

	/**
	 * @return
	 */
	public String getStAddr2() {
		return stAddr2;
	}

	/**
	 * @return
	 */
	public String getStprov() {
		return stprov;
	}

	/**
	 * @return
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @return
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param string
	 */
	public void setCity(String string) {
		city = string;
	}

	/**
	 * @param string
	 */
	public void setCompany(String string) {
		company = string;
	}

	/**
	 * @param string
	 */
	public void setCountry(String string) {
		country = string;
	}

	/**
	 * @param string
	 */
	public void setDayPhone(String string) {
		dayPhone = string;
	}

	/**
	 * @param string
	 */
	public void setEmail(String string) {
		email = string;
	}

	/**
	 * @param string
	 */
	public void setEvenPhone(String string) {
		evenPhone = string;
	}

	/**
	 * @param i
	 */
	public void setExtHitCount(int i) {
		extHitCount = i;
	}

	/**
	 * @param string
	 */
	public void setFax(String string) {
		fax = string;
	}

	/**
	 * @param string
	 */
	public void setFname(String string) {
		fname = string;
	}

	/**
	 * @param i
	 */
	public void setHitCount(int i) {
		hitCount = i;
	}

	/**
	 * @param i
	 */
	public void setIntHitCount(int i) {
		intHitCount = i;
	}

	/**
	 * @param b
	 */
	public void setRevoked(boolean b) {
		isRevoked = b;
	}

	/**
	 * @param string
	 */
	public void setJobTitle(String string) {
		jobTitle = string;
	}

	/**
	 * @param l
	 */
	public void setLicenseDate(long l) {
		licenseDate = l;
	}
	public void setLicenseDate(java.sql.Timestamp l) {
		licenseDate = l.getTime();
	}

	/**
	 * @param string
	 */
	public void setLname(String string) {
		lname = string;
	}

	/**
	 * @param string
	 */
	public void setPagmobPhone(String string) {
		pagmobPhone = string;
	}

	/**
	 * @param string
	 */
	public void setPostcode(String string) {
		postcode = string;
	}

	/**
	 * @param string
	 */
	public void setSal(String string) {
		sal = string;
	}

	/**
	 * @param string
	 */
	public void setStAddr1(String string) {
		stAddr1 = string;
	}

	/**
	 * @param string
	 */
	public void setStAddr2(String string) {
		stAddr2 = string;
	}

	/**
	 * @param string
	 */
	public void setStprov(String string) {
		stprov = string;
	}

	/**
	 * @param string
	 */
	public void setSuffix(String string) {
		suffix = string;
	}

	/**
	 * @param string
	 */
	public void setUserid(String string) {
		userid = string;
	}

}


