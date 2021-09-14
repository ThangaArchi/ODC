package oem.edge.ed.Edsurvey;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import oem.edge.ed.util.EDCMafsFile;
import oem.edge.ed.util.PasswordUtils;

 

public class Edsurveys extends HttpServlet {
	//private static SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
	private static String afsCell = null;
	private static String uid = null;
	private static String Webmaster = null;
	private static String SurveyDir = null;
	private static String PWD_DIR = null;
	private static ResourceBundle rb;
	private String pwd = null;

	public class Data {
		private EDCMafsFile authenticator;
		private ServletOutputStream  out;
		private OutputStream pout, plock;
		private InputStream pin;

		private String classdirectory = null;
		private String sectiondir = null;
		private String classname = null;
		private String section = null;
		private String student = null;
		private String email = null;

		private String comment1 = null;
		private String comment2 = null;
		private String comment3 = null;
		private String comment4 = null;
		private String comment5 = null;
		private String comment6 = null;
		private String comment7 = null;
		private String comment8 = null;
		private String comment9 = null;

		private String Q0 = null;
		private String Q1 = null;
		private String Q2 = null;
		private String Q3 = null;
		private String Q4 = null;
		private String Q5 = null;
		private String Q6 = null;
		private String Q7 = null;
		private String Q8 = null;
		private String Q9 = null;
		private String Q10 = null;
		private String Q11 = null;
		private String Q12 = null;
		private String Q13 = null;
		private String Q14 = null;

		Date mydate = new Date();
		String id = "id" + mydate.getTime();
	}
public boolean authenticate(Data data) {
	boolean authOK = false;

	debug(data,"Get new AFS mediator.");

	data.authenticator = new EDCMafsFile();

	debug(data,"Retrieve AFS password.");

	synchronized(this) {
		pwd = PasswordUtils.getPassword(PWD_DIR);
	}

	debug(data,"Authenticate to AFS");

	if (data.authenticator.afsDo(afsCell, uid, pwd)) {
		debug(data,"Authenticated.");
		data.authenticator.setVerbose(true);
		authOK=true;
	}
	else {
		debug(data,"Failed to authenticate.");
		authOK=false;
	}

	return authOK;
}
public void bar(StringBuffer myoutput,int qnum,String question,String sscore,String comment,String id) {
	byte score = 0;
	if (sscore != null && (!sscore.equals(""))) {
		byte b[] = sscore.getBytes();
		//  System.out.println("In bar: "+question+"  score : "+sscore);
		//  System.out.println("Id : "+id);
		score = (byte)(b[0]-48);// we are assuming that we will get only
								// single digit numbers here
	}

	String font;
	switch (score) {
		case 6:
			font = "red";
			break;
		case 5:
			font = "ff6600";
			break;
		case 4:
			font = "orange";
			break;
		case 3:
			font = "yellow";
			break;
		case 2:
			font = "#aaff44";
			break;
		case 1:
		default:
			font = "00ff00";
			break;
	}
	//System.out.println("In bar : "+score);

	byte barscore = (byte)(7 - score);

	myoutput.append("        <tr>\n");
	if (comment != null && (!comment.equals(""))) {
		myoutput.append("          <td valign=top><A NAME=\""+id+" "+qnum+" Bar\"></A><A HREF=\"#"+id+" "+qnum+" Comment\"><font color=#0000ff>"+question+"</font></A></td>\n");
	}
	else {
		myoutput.append("          <td valign=top><A NAME=\""+id+" "+qnum+" Bar\"></A>"+question+"</td>\n");
	}

	if (score > 0) {
		myoutput.append("          <td valign=top colspan="+barscore+" bgcolor="+font+"><hr noshade size=5></td>\n");
		myoutput.append("          <td valign=top colspan=6></td>\n");
	}

	myoutput.append("        </tr>\n");
} // end bar()
/**
 * Insert the method's description here.
 * Creation date: (11/3/2004 8:45:50 AM)
 * @param msg java.lang.String
 */
public void debug(Data data,String msg) {
	//data.mydate.setTime(System.currentTimeMillis());
	System.out.print(Thread.currentThread().getName());
	//System.out.print(" ");
	//System.out.print(FORMAT.format(data.mydate));
	System.out.print(": ");
	System.out.println(msg);
}
public void doGet (HttpServletRequest req, HttpServletResponse res) throws IOException {
	Data data = new Data();

	String name = data.toString();
	int j = name.indexOf('@');
	Thread.currentThread().setName((j == -1) ? name : name.substring(j+1));

	debug(data,"Process survey post request.");

	data.out = res.getOutputStream();
	res.setContentType("text/html");

	data.student = req.getParameter("student");
	data.email = req.getParameter("email");

	data.Q0 = req.getParameter("Q0");         
	data.Q1 = req.getParameter("Q1");
	data.comment1 = req.getParameter("Q1Comment");
	data.Q2 = req.getParameter("Q2");
	data.comment2 = req.getParameter("Q2Comment");
	data.Q3 = req.getParameter("Q3");
	data.comment3 = req.getParameter("Q3Comment");
	data.Q4 = req.getParameter("Q4");
	data.comment4 = req.getParameter("Q4Comment");
	data.Q5 = req.getParameter("Q5");
	data.comment5 = req.getParameter("Q5Comment");
	data.Q6 = req.getParameter("Q6");
	data.comment6 = req.getParameter("Q6Comment");
	data.Q7 = req.getParameter("Q7");
	data.comment7 = req.getParameter("Q7Comment");
	data.Q8 = req.getParameter("Q8");
	data.comment8 = req.getParameter("Q8Comment");

	data.Q9 = req.getParameter("Q9");
	data.comment9 = req.getParameter("Q9Comment");

	data.Q10 = req.getParameter("Q10");
	data.Q11 = req.getParameter("Q11");
	data.Q12 = req.getParameter("Q12");
	data.Q13 = req.getParameter("Q13");
	data.Q14 = req.getParameter("Q14");

	// MPZ student must be 51 characters long
	// MPZ if (student == null) student = "";
	StringBuffer b = new StringBuffer(51);
	b.setLength(0);
	if (data.student != null) b.append(data.student);
	while (b.length() < 51) b.append(" ");
	data.student = b.toString();
	if(data.email == null) data.email = " ";

	// MPZ set to single space for Q0 to Q9 if null
	if (data.Q0 == null) data.Q0 = " ";
	if (data.Q1 == null) data.Q1 = " ";
	if (data.Q2 == null) data.Q2 = " ";
	if (data.Q3 == null) data.Q3 = " ";
	if (data.Q4 == null) data.Q4 = " ";
	if (data.Q5 == null) data.Q5 = " ";
	if (data.Q6 == null) data.Q6 = " ";
	if (data.Q7 == null) data.Q7 = " ";
	if (data.Q8 == null) data.Q8 = " ";
	if (data.Q9 == null) data.Q9 = " ";
	if (data.Q10 == null) data.Q10 = "";
	if (data.Q11 == null) data.Q11 = "";
	if (data.Q12 == null) data.Q12 = "";

	// MPZ set Q13 to be 19 characters total.
	// MPZ if (Q13 == null) Q13 = "";
	b.setLength(0);
	if (data.Q13 != null) b.append(data.Q13);
	while (b.length() < 19) b.append(" ");
	data.Q13 = b.substring(0,19);

	// MPZ set Q14 to be 24 characters total.
	// MPZ if (Q14 == null) Q14 = "";
	b.setLength(0);
	if (data.Q14 != null) b.append(data.Q14);
	while (b.length() < 24) b.append(" ");
	data.Q14 = b.substring(0,24);

	if (data.comment1 == null) data.comment1 = "";
	if (data.comment2 == null) data.comment2 = "";
	if (data.comment3 == null) data.comment3 = "";
	if (data.comment4 == null) data.comment4 = "";
	if (data.comment5 == null) data.comment5 = "";
	if (data.comment6 == null) data.comment6 = "";
	if (data.comment7 == null) data.comment7 = "";
	if (data.comment8 == null) data.comment8 = "";
	if (data.comment9 == null) data.comment9 = "";

	data.classname = req.getParameter("class");
	data.section = req.getParameter("section");

	debug(data,"Extracted survey responses.");

	if (! data.classname.equals("") && ! data.section.equals("") && 
		! data.classname.substring(0,0).equals("*")) {
		char letter[] = {' ', '-', '&', '(', ')', ';', ','};

		for (int i = 0; i < letter.length; i++) {
			data.classname = data.classname.replace(letter[i], '_');
			data.section = data.section.replace(letter[i],'_');
		}

		debug(data,"Class name is " + data.classname);
		debug(data,"Section is " + data.section);

		data.classdirectory = SurveyDir + File.separator + data.classname;
		data.sectiondir = data.classdirectory + File.separator + data.section;

		String detail = GenDetailFileString(data);
		String detail_html = GenDetailHtmlString(data);
		String summary = GenSummaryFileString(data);

		debug(data,"Built file contents, authenticating to filesystem.");

		try {
			if (authenticate(data)) {
				debug(data,"Create class directory to ensure it exists.");
				data.authenticator.doMkdir(data.classdirectory);

				/* write survey results */
				if (writeData(data,detail,detail_html,summary)) {
					/* print out survey result */
					data.out.println("<html><HEAD><TITLE>Survey Respoonses Accepted</TITLE></HEAD>");
					data.out.println("<body>");
					data.out.println("<hr>\n");
					data.out.println("<h1>Thank you for your feedback.</h1><br>");
					data.out.println("<h2>We appreciate you filling out the survey form. </h2>");
					data.out.println("<h2>We hope you enjoyed the class!</h2>");
					data.out.println("<hr>\n");
					data.out.println("</body></html>");
				}
			}

			else {
				debug(data,"Could not authentiate to filesystem.");
				data.out.println("<html><HEAD><TITLE>Survey Error</TITLE></HEAD>");
				data.out.println("<body>");
				data.out.println("<hr>\n");
				data.out.println("<h2>Internal error! Unable to get necessary authentication.</h2><br>");
				data.out.println("<h2>Please contact your instructor.</h2>");
				data.out.println("<hr>\n");
				data.out.println("</body></html>");
			}
		}
		finally {
			debug(data,"Release filesystem authentication.");
			data.authenticator.afsDone();
		}
	}

	else {
		debug(data,"Class and/or section is missing.");
		data.out.println("<html><head><title>Survey Error</title></head>");
		data.out.println("<body>");
		data.out.println("<hr>\n");
		data.out.println("<h2>You must specify BOTH a class name and a section number, so the survey can be recorded.</h2>");
		data.out.println("<h2>Please use the BACK button in your viewer to go back to the survey form.</h2>");
		data.out.println("<hr>\n");
		data.out.println("</body></html>");
	}

	debug(data,"Processed survey post request.");
}
public void doPOST (HttpServletRequest req,HttpServletResponse res)	throws ServletException, IOException {
	doGet(req, res);
}
public String GenDetailFileString(Data data) {
	StringBuffer sb = new StringBuffer();

	sb.append("Student:  " + data.student + "        " + data.email + "\n");
	sb.append(data.Q13 + "                 " + data.Q14 + "\n");
	sb.append("\n");
	sb.append("Overall Satisfaction?           " + data.Q9 + "\n");
	sb.append("Objectives Clear?               " + data.Q0 + "\n");
	sb.append("Met Objectives?                 " + data.Q1 + "\n");
	sb.append("Quality of Content?             " + data.Q2 + "\n");
	sb.append("Labs Effective?                 " + data.Q3 + "\n");
	sb.append("Materials Useful?               " + data.Q4 + "\n");
	sb.append("Oganization?                    " + data.Q5 + "\n");
	sb.append("Instructor?                     " + data.Q6 + "\n");
	sb.append("Clear Pace?                     " + data.Q7 + "\n");
	sb.append("Time for questions?             " + data.Q8 + "\n");
	sb.append("\n");

	if (data.Q10.length() > 0) {
		sb.append("--------------Most Helpful?-----------------------------\n");
		sb.append(data.Q10 + "\n");
	}

	if (data.Q11.length() > 0) {
		sb.append("--------------Least Helpful?-----------------------------\n");
		sb.append(data.Q11 + "\n");
	}

	if (data.comment1.length() > 0) {
		sb.append("--------------Met Objectives?----------------------------\n");
		sb.append(data.comment1 + "\n");
	}

	if (data.comment2.length() > 0) {
		sb.append("--------------Quality of Content-------------------------\n");
		sb.append(data.comment2 + "\n");
	}

	if (data.comment3.length() > 0) {
		sb.append("--------------Labs Effective?----------------------------\n");
		sb.append(data.comment3 + "\n");
	}

	if (data.comment4.length() > 0) {
		sb.append("--------------Materials Useful?--------------------------\n");
		sb.append(data.comment4 + "\n");
	}

	if (data.comment5.length() > 0) {
		sb.append("--------------Oganization?-------------------------------\n");
		sb.append(data.comment5 + "\n");
	}

	if (data.comment6.length() > 0) {
		sb.append("--------------Instructor---------------------------------\n");
		sb.append(data.comment6 + "\n");
	}

	if (data.comment7.length() > 0) {
		sb.append("--------------Class Pace?--------------------------------\n");
		sb.append(data.comment7 + "\n");
	}

	if (data.comment8.length() > 0) {
		sb.append("--------------Time for Questions?------------------------\n");
		sb.append(data.comment8 + "\n");
	}

	if (data.comment9.length() > 0) {
		sb.append("--------------Overall Sat--------------------------------\n");
		sb.append(data.comment9 + "\n");
	}

	if (data.Q12.length() > 0) {
		sb.append("--------------Additional Comments------------------------\n");
		sb.append(data.Q12 + "\n");
	}

	sb.append("==============================================================\n");

	return sb.toString();
}
public String GenDetailHtmlString(Data data) {
	StringBuffer myoutput = new StringBuffer();

	if (data.student != "") {
		myoutput.append("<h2>" + data.student + " <font size=-1>" + data.Q13 + " - <font size=-1>" + data.Q14 + " - " + data.email + "</font></h2>\n");
	}
	else {
		myoutput.append("<h2>Anonymous <font size=-1>" + data.Q13 + " - <font size=-1>" + data.Q14 + "</font></h2>\n");
	}

	myoutput.append("<table border=3 cellpadding=3 cellspacing=3>\n");
	myoutput.append("<caption></caption>\n");
	myoutput.append("  <tr>\n");
	myoutput.append("    <td>\n");
	myoutput.append("      <table>\n");
	myoutput.append("        <tr>\n");
	myoutput.append("          <th align=left>Question</th>\n");
	myoutput.append("          <th align=left>&nbsp;&nbsp;&nbsp;&nbsp;6&nbsp;&nbsp;&nbsp;&nbsp;</th>\n");
	myoutput.append("          <th align=left>&nbsp;&nbsp;&nbsp;&nbsp;5&nbsp;&nbsp;&nbsp;&nbsp;</th>\n");
	myoutput.append("          <th align=left>&nbsp;&nbsp;&nbsp;&nbsp;4&nbsp;&nbsp;&nbsp;&nbsp;</th>\n");
	myoutput.append("          <th align=left>&nbsp;&nbsp;&nbsp;&nbsp;3&nbsp;&nbsp;&nbsp;&nbsp;</th>\n");
	myoutput.append("          <th align=left>&nbsp;&nbsp;&nbsp;&nbsp;2&nbsp;&nbsp;&nbsp;&nbsp;</th>\n");
	myoutput.append("          <th align=left>&nbsp;&nbsp;&nbsp;&nbsp;1&nbsp;&nbsp;&nbsp;&nbsp;</th>\n");
	myoutput.append("        </tr>\n");
	myoutput.append("        <tr>\n");
	myoutput.append("          <td valign=top><hr></td>\n");
	myoutput.append("          <td valign=top><hr></td>\n");
	myoutput.append("          <td valign=top><hr></td>\n");
	myoutput.append("          <td valign=top><hr></td>\n");
	myoutput.append("          <td valign=top><hr></td>\n");
	myoutput.append("          <td valign=top><hr></td>\n");
	myoutput.append("          <td valign=top><hr></td>\n");
	myoutput.append("        </tr>\n");

	String question;
	question = "Overall satisfaction with the class?";
	bar(myoutput,9,question,data.Q9,data.comment9,data.id);

	question = "Class objectives clearly stated (Yes=1,No=6)?";
	bar(myoutput,0,question,data.Q0,"",data.id);

	question = "Course met the stated objectives?";
	bar(myoutput,1,question,data.Q1,data.comment1,data.id);

	question = "Quality of the course content?";
	bar(myoutput,2,question,data.Q2,data.comment2,data.id);

	question = "Effectiveness of the lab content?";
	bar(myoutput,3,question,data.Q3,data.comment3,data.id);

	question = "Usefulness of handouts and course materials?";
	bar(myoutput,4,question,data.Q4,data.comment4,data.id);

	question = "Organization of the class?";
	bar(myoutput,5,question,data.Q5,data.comment5,data.id);

	question = "Instructor's preparation and delivery?";
	bar(myoutput,6,question,data.Q6,data.comment6,data.id);

	question = "Pace of the class?";
	bar(myoutput,7,question,data.Q7,data.comment7,data.id);

	question = "Amount of time spent answering questions?";
	bar(myoutput,8,question,data.Q8,data.comment8,data.id);

	myoutput.append("      </table>\n");
	myoutput.append("    </td>\n");
	myoutput.append("  </tr>\n");
	myoutput.append("</table>\n");

	question = "Overall satisfaction with the class?";
	question(myoutput,9,question,data.Q9,data.comment9,data.id);

	question = "Course met the stated objectives?";
	question(myoutput,1,question,data.Q1,data.comment1,data.id);

	question = "Quality of the course content?";
	question(myoutput,2,question,data.Q2,data.comment2,data.id);

	question = "Effectiveness of the lab content?";
	question(myoutput,3,question,data.Q3,data.comment3,data.id);

	question = "Usefulness of handouts and course materials?";
	question(myoutput,4,question,data.Q4,data.comment4,data.id);

	question = "Organization of the class?";
	question(myoutput,5,question,data.Q5,data.comment5,data.id);

	question = "Instructor's preparation and delivery?";
	question(myoutput,6,question,data.Q6,data.comment6,data.id);

	question = "Pace of the class?";
	question(myoutput,7,question,data.Q7,data.comment7,data.id);

	question = "Amount of time spent answering questions?";
	question(myoutput,8,question,data.Q8,data.comment8,data.id);

	question = "Most helpful topics";
	question(myoutput,10,question,"0",data.Q10,data.id);

	question = "Least helpful topics";
	question(myoutput,11,question,"0",data.Q11,data.id);

	question = "Additional comments";
	question(myoutput,12,question,"0",data.Q12,data.id);

	return myoutput.toString();
}
public String GenSummaryFileString(Data data) {
	StringBuffer sb = new StringBuffer();
	sb.append(data.Q9);
	sb.append(" ");
	sb.append(data.Q0);
	sb.append(" ");
	sb.append(data.Q1);
	sb.append(" ");
	sb.append(data.Q2);
	sb.append(" ");
	sb.append(data.Q3);
	sb.append(" ");
	sb.append(data.Q4);
	sb.append(" ");
	sb.append(data.Q5);
	sb.append(" ");
	sb.append(data.Q6);
	sb.append(" ");
	sb.append(data.Q7);
	sb.append(" ");
	sb.append(data.Q8);
	sb.append(" ");
	sb.append(data.Q13);
	sb.append(" ");
	sb.append(data.Q14);
	sb.append(" ");
	sb.append(data.student);
	sb.append(" ");
	sb.append(data.email);

	return sb.toString();
}
public void init(javax.servlet.ServletConfig config) throws ServletException {
	super.init(config);
	
	this.Webmaster = config.getInitParameter("masterEmail");

	try {
		rb = ResourceBundle.getBundle("Edsurveys");
		this.afsCell = rb.getString("AFS_CELL").trim();
		this.uid = rb.getString("AFS_ID").trim();
		this.SurveyDir = rb.getString("HOME_DIR").trim();
		this.PWD_DIR = rb.getString("PWD_DIR").trim();
		System.out.println("afsCell "+afsCell+" uid "+uid+" SurveyDir "+SurveyDir);
	}
	catch (java.util.MissingResourceException ex) {
		ex.printStackTrace();
	}
}
public void question(StringBuffer myoutput,int qnum,String question,String sscore,String comment,String id) {
	byte score = 0;
	if (sscore != null && (!sscore.equals("")) && (!sscore.equals("0"))) {
		byte b[] = sscore.getBytes();
		score = (byte)(b[0]-48);// we are assuming that we will get only
								// single digit numbers here
	}

	if (sscore.equals("")) {
		score = 1;	// a hack to allow links from comments to
	}				// be provided even if score is not chosen

	if (comment != null && (!comment.equals(""))) {
		if (score > 0) {
			myoutput.append("<A NAME=\""+id+" "+qnum+" Comment\">\n");
			myoutput.append("<p><font size=+1 color=#0000ff><A HREF=\"#"+id+" "+qnum+" Bar\">"+question+"</a></font>\n");
			myoutput.append("<p>"+comment);
		}
		else {
			myoutput.append("<p><font size=+1 color=#0000ff>"+question+"</font>\n");
			myoutput.append("<p>"+comment);
		}
	}
}
public boolean writeData(Data data,String detail,String html,String summary) throws IOException {
	OutputStream plock = null;
	OutputStream pout = null;
	int phase = 0;

	boolean ret = false;

	String detail_file = data.sectiondir + ".Detail";
	String detail_html = data.sectiondir + ".Detail.html";
	String summary_file = data.sectiondir + ".Summary";
	String lock = data.sectiondir + "_lock";

	try {
		int i = 0;
		debug(data,"Wait for data file lock.");
		plock = data.authenticator.doFileWriteExclusive(lock);

		// Wait for up to 30 seconds (150 times @ 200ms) to get a lock.
		while (plock == null && i < 150) {
			debug(data,"File lock unavailable, retry in 200ms.");
			try {
				Thread.currentThread().sleep(200);
			}
			catch (Exception sleep) {
			}

			i++;
			plock = data.authenticator.doFileWriteExclusive(lock);
		}

		if (plock == null) {
			throw new Exception ("File lock failed.");
		}

		plock.close();

		debug(data,"Have data file lock.");

		// Got past locking, move to phase 1.
		phase = 1;

		pout = data.authenticator.doFileWrite(detail_file, true);

		if (pout == null) {
			throw new Exception ("Append to detail file failed.");
		}

		debug(data,"Append data to detail file.");

		PrintStream ps = new PrintStream(new BufferedOutputStream(pout));

		if (data.authenticator.getFileSize(detail_file) <= 0) {
			ps.println("=================================================================");
			ps.println("Classname:  " + data.classname + "    Section #:  " + data.section);
			ps.println("=================================================================");
		}

		ps.println(detail);
		ps.close();

		debug(data,"Data appended to detail file.");

		// Got past detail file, move to phase 2.
		phase = 2;

		pout = data.authenticator.doFileWrite(detail_html, true);

		if (pout == null) {
			throw new Exception ("Append to detail.html file failed.");
		}

		debug(data,"Append data to detail html file.");

		ps = new PrintStream(new BufferedOutputStream(pout));

		if (data.authenticator.getFileSize(detail_html) <= 0) {
			ps.println("<HTML>");
			ps.println("<HEAD>");
			ps.println("<TITLE>classname - Section " + data.section + "</TITLE>");
			ps.println("</HEAD>");
			ps.println("<BODY>");

			ps.println("<table width=100%>");
			ps.println("<tr><th align=left>");
			ps.println("</th>");
			ps.println("<th align=right>");
			ps.println("<script language=JavaScript>");
			ps.println("document.write(\"<b>\"");
			ps.println("+document.lastModified+\"</b>\")");
			ps.println("</script>");
			ps.println("</th></tr></table>");
			ps.println("<hr><h1>" + data.classname + "   - Section " + data.section + "</h1>");
			ps.println("<table><tr><td>Legend:</td>");
			ps.println("<td><table><tr>");
			ps.println("<th width=16% align=left bgcolor=\"red\"><center>6=Very Dissatisfied</center></th>");
			ps.println("<th width=16% align=left bgcolor=\"#ff6600\"><center>5=Dissatisfied</center></th>");
			ps.println("<th width=16% align=left bgcolor=\"orange\"><center>4=Somewhat Dissatisfied</center></th>");
			ps.println("<th width=16% align=left bgcolor=\"yellow\"><center>3=Somewhat Satisfied</center></th>");
			ps.println("<th width=16% align=left bgcolor=\"#aaff44\"><center>2=Satisfied</center></th>");
			ps.println("<th width=16% align=left bgcolor=\"00ff00\"><center>1=Very Satisfied</center></th>");
			ps.println("</tr></table></td></tr></table>");
		}

		ps.println(html);
		ps.close();

		debug(data,"Data appended to detail html file.");

		// Got past html file, move to phase 3.
		phase = 3;

		pout = data.authenticator.doFileWrite(summary_file, true);

		if (pout == null) {
			throw new Exception ("Append to summary file failed.");
		}

		debug(data,"Append data to summary file.");

		ps = new PrintStream(new BufferedOutputStream(pout));
		ps.println(summary);
		ps.close();

		debug(data,"Data appended to summary file.");

		ret = true;
	}
	catch (IOException e) {
		debug(data,e.getMessage());
		debug(data,"I/O Error in phase " + phase);

		data.out.println("<html><HEAD><TITLE>Survey Error</TITLE></HEAD>");
		data.out.println("<body>");
		data.out.println("<hr>\n");
		data.out.println("<h2>Our apologies... Due to a server error, we could not save your responses.</h2><br>");
		data.out.println("<h2>I/O Error in phase " + phase + ".</h2>");
		data.out.println("<h2>Please contact your instructor.</h2>");
		data.out.println("<hr>\n");
		data.out.println("</body></html>");
	}
	catch (Exception e1) {
		debug(data,e1.getMessage());

		if (e1.getMessage().equals("File lock failed.")) {
			data.out.println("<html><HEAD><TITLE>Survey Error</TITLE></HEAD>");
			data.out.println("<body>");
			data.out.println("<hr>\n");
			data.out.println("<h2>Our apologies, another student is posting survey results.</h2><br>");
			data.out.println("<h2>Please use the back button of your browser and try to submit your responses again.</h2>");
			data.out.println("<hr>\n");
			data.out.println("</body></html>");
		}
		else {
			data.out.println("<html><HEAD><TITLE>Survey Error</TITLE></HEAD>");
			data.out.println("<body>");
			data.out.println("<hr>\n");
			data.out.println("<h2>Our apologies... Due to a server error, we could not save your responses.</h2><br>");
			data.out.println("<h2>" + e1.getMessage() + "</h2>");
			data.out.println("<h2>Please contact your instructor.</h2>");
			data.out.println("<hr>\n");
			data.out.println("</body></html>");
		}
	}
	finally {
		if (plock != null) {
			debug(data,"Release file lock.");
			data.authenticator.doDeleteFile(lock);
			debug(data,"File lock released.");
		}
	}

	return ret;
}
}
