package oem.edge.ed.odc.dropbox.client.soa;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (1/31/2003 9:20:25 AM)
 * @author: Mike Zarnick
 */

import oem.edge.ed.odc.dropbox.common.AclInfo;
import oem.edge.ed.odc.dropbox.common.PackageInfo;
import java.text.*;
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

public class DisplayPackageInfo extends PackageInfo {
	private Long commitDate;
	private Long createDate;
	private Long expDate;
	private Long size;
	private String descriptionAsToolTip;
	private Vector recipients = new Vector();
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:21:14 AM)
 */
public DisplayPackageInfo() {
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:21:14 AM)
 */
public DisplayPackageInfo(PackageInfo p) {
	copyPackageInfo(p);
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 10:07:41 AM)
 * @param a oem.edge.ed.odc.dropbox.common.AclInfo
 */
public void addRecipient(AclInfo a) {
	recipients.addElement(a);
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:23:34 AM)
 * @param p oem.edge.ed.odc.dropbox.common.PackageInfo
 */
public void copyPackageInfo(PackageInfo p) {
	// Copy standard information
	setPackageCompany(p.getPackageCompany());
	setPackageCommitted(p.getPackageCommitted());
	setPackageCreation(p.getPackageCreation());
	setPackageDescription(p.getPackageDescription());
	setPackageExpiration(p.getPackageExpiration());
	setPackageFlags(p.getPackageFlags());
	setPackageId(p.getPackageId());
	setPackageName(p.getPackageName());
	setPackageNumElements(p.getPackageNumElements());
	setPackageOwner(p.getPackageOwner());
	setPackageSize(p.getPackageSize());
	setPackageStatus(p.getPackageStatus());

	// Create displayable information.
	commitDate = new Long(p.getPackageCommitted());
	createDate = new Long(p.getPackageCreation());
	expDate = new Long(p.getPackageExpiration());
	size = new Long(p.getPackageSize());
	
	// Convert the description to a form for a tooltip.
	if (p.getPackageDescription() != null && p.getPackageDescription().length() > 0) {
		// This means ripping the description into a <br> (html) delimited
		// block of text that is at most 60 characters wide and only 8 lines
		// deep. End with ellipse (...) if necessary.
		
		// Break the description on its natural boundaries (\n).
		StringTokenizer t = new StringTokenizer(p.getPackageDescription(),"\n");
		StringBuffer s = new StringBuffer();
		
		// Count lines.
		int lcnt = 0;

		// For each natural line...
		while (t.hasMoreTokens() && lcnt < 8) {
			String l = t.nextToken();
			int c = 0;
			
			// If the line is greater than 60 characters, break it.
			while (l.length() - c > 60 && lcnt < 8) {
				// Find the last space.
				int spc = l.lastIndexOf(' ',c+60);
				
				// Put a break in, if necessary.
				if (s.length() > 0) s.append("<br>");

				// If there is no natural break in the last 1/4, then use the
				// whole thing.
				if (spc < 45) {
					s.append(l.substring(c,60));
					c += 61;
				}
				else {
					s.append(l.substring(c,spc));
					c = spc + 1;
				}
				lcnt++;
			}

			// Have a remaining fragment?
			if (l.length() >= c) {
				// Can handle more lines?
				if (lcnt < 8) {
					if (s.length() > 0) s.append("<br>");
					s.append(l.substring(c));
					lcnt ++;
				}
				// Too many lines already.
				else {
					s.append("<br>...");
				}
			}
			// No fragment, but more stuff and too many lines?
			else if (t.hasMoreTokens() && lcnt == 8) {
				s.append("<br>...");
			}
		}

		descriptionAsToolTip = s.toString();
	}
	else {
		descriptionAsToolTip = null;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:56:46 AM)
 * @return java.lang.String
 */
public Long getCommitDate() {
	return commitDate;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:56:46 AM)
 * @return java.lang.String
 */
public Long getCreationDate() {
	return createDate;
}
public String getDescriptionAsToolTip() {
	return descriptionAsToolTip;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:56:46 AM)
 * @return java.lang.String
 */
public Long getExpirationDate() {
	return expDate;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:56:46 AM)
 * @return java.lang.String
 */
public Vector getRecipients() {
	return recipients;
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 9:56:46 AM)
 * @return java.lang.String
 */
public Long getSize() {
	return size;
}
/**
 * Insert the method's description here.
 * Creation date: (2/4/2003 10:09:40 AM)
 * @param a oem.edge.ed.odc.dropbox.common.AclInfo
 */
public void removeRecipient(AclInfo a) {
	recipients.removeElement(a);
}
}
