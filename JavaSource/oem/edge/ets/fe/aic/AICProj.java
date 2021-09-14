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


package oem.edge.ets.fe.aic;

import java.util.*;
import oem.edge.ets.fe.ETSProj;

public class AICProj extends ETSProj {
  public final static String Copyright = "(C) Copyright IBM Corp.  2002-2004";
  public static final String VERSION = "1.3";
  
  private String sce_sector;
  private String sector;
  private String sub_sector;
  private String process;
  private String brand;
  private boolean ibmOnly;
  
  
/**
 * @return
 */
public String getBrand() {
	return brand;
}

/**
 * @return
 */
public String getProcess() {
	return process;
}

/**
 * @return
 */
public String getSce_sector() {
	return sce_sector;
}

/**
 * @return
 */
public String getSector() {
	return sector;
}

/**
 * @return
 */
public String getSub_sector() {
	return sub_sector;
}

/**
 * @param string
 */
public void setBrand(String string) {
	brand = string;
}

/**
 * @param string
 */
public void setProcess(String string) {
	process = string;
}

/**
 * @param string
 */
public void setSce_sector(String string) {
	sce_sector = string;
}

/**
 * @param string
 */
public void setSector(String string) {
	sector = string;
}

/**
 * @param string
 */
public void setSub_sector(String string) {
	sub_sector = string;
}

/**
 * @return Returns the ibmOnly.
 */
public boolean isIbmOnly() {
	return ibmOnly;
}
/**
 * @param ibmOnly The ibmOnly to set.
 */
public void setIbmOnly(boolean ibmOnly) {
	this.ibmOnly = ibmOnly;
}
}
