/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.util.pdfutils;
import java.util.ArrayList;
/**
 * Class       : QBRScorecardSummaryPDF
 * Package     : oem.edge.ets.fe.workflow.util.pdfutils
 * Description : 
 * Date		   : Mar 1, 2007
 * Note		   : This implementation does not support comments greater than 2500 characters
 * 				 It also does not split table rows across pages.
 * 
 * @author     : Pradyumna Achar
 */
public class QBRScorecardSummaryPDF {
	private int currentY = 0;
public String generatePDF(QBRCommonData qbrcomm, ArrayList scores)
{
	String pdfFile = getPDFHeader();
	int currentPage = 0;
	pdfFile += getPage(currentPage);
	String streamContent = getStreamContentTop(qbrcomm.getQbrName(), qbrcomm.getQuarter(), qbrcomm.getYear());
	streamContent += getTopTable(qbrcomm);
	streamContent += getClientAttendeesArea(qbrcomm.getClient_attendees());
	streamContent += getOverallCommentsArea(qbrcomm.getOverall_comments());
	streamContent += getBottomTableHeader(true);
	//currentY = 420;
	
	for(int i=0; i<scores.size(); i++)
	{
		QBRScore q = (QBRScore)scores.get(i);
		
		String s = getDataRow(q);
		
		if(s==null)
		{
			pdfFile += getContent(streamContent,currentPage);

			currentPage++;
			pdfFile += getPage(currentPage);
			
			streamContent = getStreamContentTop(qbrcomm.getQbrName(), qbrcomm.getQuarter(), qbrcomm.getYear());
			streamContent += getBottomTableHeader(false);
			streamContent += getDataRow(q);
			
		}
		else
		{
			streamContent += s;
		}
	}
	pdfFile += getContent(streamContent,currentPage);
	pdfFile += getPageDescriptor(currentPage+1);
	pdfFile += getPDFFooter();
	//System.out.println(pdfFile);
	return pdfFile;
}

private String getPDFHeader()
{
	return
		"%PDF-1.0\n"+
		"1 0 obj\n"+
		"<<\n"+
		"/Type /Catalog\n"+
		"/Pages 3 0 R\n"+
		"/Outlines 2 0 R\n"+
		">>\n"+
		"endobj\n"+
		"2 0 obj\n"+
		"<<\n"+
		"/Type /Outlines\n"+
		"/Count 0\n"+
		">>\n"+
		"endobj\n"+
		"6 0 obj\n"+
		"[/PDF /Text]\n"+
		"endobj\n"+
		"7 0 obj\n"+
		"<<\n"+
		"/Type /Font\n"+
		"/Subtype /Type1\n"+
		"/Name /F1\n"+
		"/BaseFont /Helvetica-Bold\n"+
		"/Encoding /MacRomanEncoding\n"+
		">>\n"+
		"endobj\n"+
		"8 0 obj\n"+
		"<<\n"+
		"/Type /Font\n"+
		"/Subtype /Type1\n"+
		"/Name /F2\n"+
		"/BaseFont /Helvetica\n"+
		"/Encoding /MacRomanEncoding\n"+
		">>\n"+
		"endobj\n"
		;
}
private String getPDFFooter()
{
	return
	"xref\n"+
	"0 8\n"+
	"0000000000 65535 f\n"+
	"0000000009 00000 n\n"+
	"0000000074 00000 n\n"+
	"0000000120 00000 n\n"+
	"0000000179 00000 n\n"+
	"0000000454 00000 n\n"+
	"0000000318 00000 n\n"+
	"0000000346 00000 n\n"+
	"trailer\n"+
	"<<\n"+
	"/Size 8\n"+
	"/Root 1 0 R\n"+
	">>\n"+
	"startxref\n"+
	"553\n"+
	"%%EOF"
	;	
}
private String getPageDescriptor(int nPages)
{
	String s = 
	"% Pages begin here\n"+
	"\n"+
	"3 0 obj\n"+
	"<<\n"+
	"/Type /Pages\n"+
	"/Count "+(nPages)+"\n"+
	"/Kids [4 0 R ";
	for(int i=0; i<nPages-1; i++)
	{
		s = s+ Integer.toString(2*i+9) + " 0 R ";
	}
	
	s +=
	"]\n"+
	">>\n"+
	"endobj\n"
	;
	return s;
}
/**
 * Page Numbers start from 0
 */
private String getPage(int pageNum)
{
	int objectNum = getObjectNum(pageNum);
	String s = Integer.toString(objectNum)+ 
	" 0 obj\n"+
	"<<\n"+
	"/Type /Page\n"+
	"/Parent 3 0 R\n"+
	"/Resources << /Font << /F1 7 0 R /F2 8 0 R>> /ProcSet 6 0 R >>\n"+
	"/MediaBox [0 0 612 792]\n"+
	"/Contents "+(objectNum+1)+" 0 R\n"+
	">>\n"+
	"endobj\n";
	return s;
}
private String getContent(String streamContent, int pageNum)
{
	int objectNum = getObjectNum(pageNum)+1;
	String s=Integer.toString(objectNum)+
	" 0 obj\n"+
	"<< /Length "+streamContent.getBytes().length+" >>\n"+
	"stream\n"+
	streamContent+
	"endstream\n"+
	"endobj\n";

	return s;
}
private int getObjectNum(int pageNum)
{
	int objectNum = 4;
	if(pageNum==0)objectNum = 4; else objectNum=(pageNum-1)*2+9;
	return objectNum;
}
private String getStreamContentTop(String qbrName, String quarter, String year)
{
	return 
	"BT\n"+
	"/F1 12 Tf\n"+
	"100 700 Td (IBM Customer Connect - Collaboration Center) Tj\n"+
	"ET\n"+
	"BT\n"+
	"/F2 14 Tf\n"+
	"100 670 Td (QBR Scoring Summary) Tj\n"+
	"ET\n"+
	"1 0 0 RG\n"+
	"1 0 0 rg\n"+
	"BT\n"+
	"/F1 10 Tf\n"+
	"460 670 Td (IBM Confidential)Tj\n"+
	"ET\n"+
	"0 0 0 RG\n"+
	"0 0 0 rg\n"+
	"100 660 m\n"+
	"600 660 l\n"+
	"S\n"+
	"\n"+
	"BT\n"+
	"100 630 Td /F1 11 Tf (QBR:  )Tj /F2 11 Tf("+ep(qbrName)+")Tj\n"+
	"ET\n"+
	"BT\n"+
	"300 630 Td /F1 11 Tf (Quarter/Year:  )Tj /F2 11 Tf("+ep(quarter)+"Q"+ep(year)+")Tj\n"+
	"ET\n"+
	"\n"
	;

}
private String getTopTable(QBRCommonData qbrComm)
{
	return
	"%FIRST ROW OF TOP TABLE\n"+
	"\n"+
	"%Rectangle for label Client\n"+
	"0.8 0.8 0.8 rg\n"+
	"100 590 80 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"101 600 Td /F1 11 Tf (Client:)Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for value Client\n"+
	".9 .9 .9 rg\n"+
	"180 590 120 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"181 600 Td /F2 11 Tf ("+ep(qbrComm.getClient())+")Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for label Area Rated\n"+
	"0.8 0.8 0.8 rg\n"+
	"300 590 80 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"301 600 Td /F1 11 Tf (Area Rated:)Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for value Area Rated\n"+
	".9 .9 .9 rg\n"+
	"380 590 120 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"381 600 Td /F2 11 Tf ("+ep(qbrComm.getAreaRated())+")Tj\n"+
	"ET\n"+
	"\n"+
	"%SECOND ROW OF TOP TABLE\n"+
	"\n"+
	"%Rectangle for label Segment\n"+
	"0.8 0.8 0.8 rg\n"+
	"100 560 80 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"101 570 Td /F1 11 Tf (Segment:)Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for value Segment\n"+
	".9 .9 .9 rg\n"+
	"180 560 120 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"181 570 Td /F2 11 Tf ("+ep(qbrComm.getSegment())+")Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for label Rating Period\n"+
	"0.8 0.8 0.8 rg\n"+
	"300 560 80 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"301 570 Td /F1 11 Tf (Rating Period:)Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for value Rating Period\n"+
	".9 .9 .9 rg\n"+
	"380 560 120 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"381 570 Td /F2 11 Tf ("+ep(qbrComm.getRatingPeriod())+")Tj\n"+
	"ET\n"+
	"\n"+
	"\n"+
	"%THIRD ROW OF TOP TABLE\n"+
	"\n"+
	"%Rectangle for label Current Score\n"+
	"0.8 0.8 0.8 rg\n"+
	"100 530 80 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"101 550 Td /F1 11 Tf (Most Recent)Tj 0 -12 Td (QBR Score:)Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for value Current Score\n"+
	".9 .9 .9 rg\n"+
	"180 530 120 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"181 540 Td /F2 11 Tf ("+ep(qbrComm.getCurrentScore())+")Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for label Previos score\n"+
	"0.8 0.8 0.8 rg\n"+
	"300 530 80 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"301 550 Td /F1 11 Tf (Previous QBR)Tj 0 -12 Td (Score:)Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for value Previous score\n"+
	".9 .9 .9 rg\n"+
	"380 530 120 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"381 540 Td /F2 11 Tf ("+ep(qbrComm.getOldScore())+")Tj\n"+
	"ET\n"+
	"\n"+
	"\n"+
	"%FOURTH ROW OF TOP TABLE\n"+
	"\n"+
	"%Rectangle for label Rank\n"+
	"0.8 0.8 0.8 rg\n"+
	"100 500 80 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"101 510 Td /F1 11 Tf (Rank:)Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for value Rank\n"+
	".9 .9 .9 rg\n"+
	"180 500 120 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"181 510 Td /F2 11 Tf ("+ep(qbrComm.getRank())+")Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for label Change\n"+
	"0.8 0.8 0.8 rg\n"+
	"300 500 80 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"301 510 Td /F1 11 Tf (Change:)Tj\n"+
	"ET\n"+
	"\n"+
	"%Rectangle for value Change\n"+
	".9 .9 .9 rg\n"+
	"380 500 120 30 re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"381 510 Td /F2 11 Tf ("+ep(qbrComm.getChange())+")Tj\n"+
	"ET\n"+
	"\n"+
	"% END OF THE TOP TABLE :-( Huh.\n"
	;
}
private String getClientAttendeesArea(String comments)
{
	String[] commentLines = getEightyCharLines(comments);
	String s = 
	"BT\n"+
	"/F1 11 Tf\n"+
	"100 480 Td (Client Attendees) Tj\n"+
	"ET\n"+
	"%Overall Comments follow.\n"+
	"%Limit each line to 80 Chars. First Y-axis coordinate is 465. Use -10 delta\n";
	currentY = 465;
	for(int i=0; i<commentLines.length; i++)
	{
    int yCoord = 465 - 10*i;
	s+="BT\n"+
	"/F2 10 Tf\n"+
	"100 "+yCoord+" Td (" +
	ep(commentLines[i])+
	") Tj\n"+
	"ET\n";
	currentY = yCoord;
	}
	return s;
}
private String getOverallCommentsArea(String comments)
{
	String[] commentLines = getEightyCharLines(comments);
	currentY -=20;
	String s = 
	"BT\n"+
	"/F1 11 Tf\n"+
	"100 "+currentY+" Td (Overall Comments) Tj\n"+
	"ET\n"+
	"%Overall Comments follow.\n"+
	"%Limit each line to 80 Chars. First Y-axis coordinate is 465. Use -10 delta\n";
	currentY -= 15;
	for(int i=0; i<commentLines.length; i++)
	{
    int yCoord = currentY - 10*i;
	s+="BT\n"+
	"/F2 10 Tf\n"+
	"100 "+yCoord+" Td (" +
	ep(commentLines[i])+
	") Tj\n"+
	"ET\n";
	currentY = yCoord;
	}
	return s;
}
private String[] getFiftyCharLines(String s)
{
	return getNCharLines(s,50);
}
private String[] getTwelveCharLines(String s)
{
	return getNCharLines(s,12);
}
private String[] getEightyCharLines(String s)
{
	return getNCharLines(s,80);
}
private String[] getNCharLines(String s, int n)
{
	String[] temp = new String[1 + s.length()/n];
	for(int i=0; i<s.length(); i+=n)
	{
		if(i+n < s.length())
			temp[i/n] = s.substring(i,i+n);
		else
			temp[i/n] = s.substring(i,s.length());
	}
	return temp;
}
private String getBottomTableHeader(boolean isFirstPage)
{
	int yOffset = 590;
	if(isFirstPage)
		yOffset = currentY - 35;
	int secondY = yOffset + 5;
	currentY = yOffset;
	
	if(isFirstPage)
		return
			"%Header row. Y coordinate is last_Y_coordinate - 35\n"+
			"0.8 0.8 0.8 rg\n"+
			"100 "+yOffset+" 80 20 re\n"+
			"f\n"+
			"BT\n"+
			"0 0 0 rg\n"+
			"101 "+secondY+" Td /F1 10 Tf (Client Attribute)Tj\n"+
			"ET\n"+
			"0.8 0.8 0.8 rg\n"+
			"182 "+yOffset+" 70 20 re\n"+
			"f\n"+
			"BT\n"+
			"0 0 0 rg\n"+
			"193 "+secondY+" Td /F1 10 Tf (Score %)Tj\n"+
			"ET\n"+
			"0.8 0.8 0.8 rg\n"+
			"255 "+yOffset+" 320 20 re\n"+
			"f\n"+
			"BT\n"+
			"0 0 0 rg\n"+
			"350 "+secondY+" Td /F1 10 Tf (Comments provided)Tj\n"+
			"ET\n"+
			"\n";
	else
		return
			"%Header row. Y coordinate is 590\n"+
			"0.8 0.8 0.8 rg\n"+
			"100 590 80 20 re\n"+
			"f\n"+
			"BT\n"+
			"0 0 0 rg\n"+
			"101 595 Td /F1 10 Tf (Client Attribute)Tj\n"+
			"ET\n"+
			"0.8 0.8 0.8 rg\n"+
			"182 590 70 20 re\n"+
			"f\n"+
			"BT\n"+
			"0 0 0 rg\n"+
			"193 595 Td /F1 10 Tf (Score %)Tj\n"+
			"ET\n"+
			"0.8 0.8 0.8 rg\n"+
			"255 590 320 20 re\n"+
			"f\n"+
			"BT\n"+
			"0 0 0 rg\n"+
			"350 595 Td /F1 10 Tf (Comments provided)Tj\n"+
			"ET\n";

}
/**
 * This returns null if it is unable to find enough space in the page to print the whole row
 */
private String getDataRow(QBRScore q)
{
	
	String s = null;
	String[] commentLines = getFiftyCharLines(q.getCommentsProvided());
	String[] attrLines = getTwelveCharLines(q.getClientAttribute());
	int nCommentLines = commentLines.length;
	int nLines = nCommentLines;
	if(attrLines.length>nCommentLines)
		nLines = attrLines.length;
	
	int nextY = currentY - (nLines*10 + 12);
	int secondY = nextY + (nLines*10 + 10) - 10;
	if(nextY < 50 || currentY<50)
	{
		currentY = 590;
		return null;
	}
	int height = nLines*10+10;
	
	s=
	"%DATA ROW BEGINS\n" +
	"%Comment lines = "+nCommentLines+"\n"+
	"0.9 0.9 0.9 rg\n"+
	"100 "+nextY+" 80 "+height+" re\n"+
	"f\n";
	
	for(int i=0; i<attrLines.length;i++)
	{
	s+=
	"BT\n"+
	"0 0 0 rg\n"+
	"101 "+(secondY-i*10)+" Td /F2 10 Tf ("+ep(attrLines[i])+")Tj\n"+
	"ET\n";
	}
	s+=
	"0.9 0.9 0.9 rg\n"+
	"182 "+nextY+" 70 "+height+" re\n"+
	"f\n"+
	"BT\n"+
	"0 0 0 rg\n"+
	"183 "+secondY+" Td /F2 10 Tf ("+ep(q.getOldScorePercent())+")Tj /F2 13 Tf (-\335)Tj /F2 10 Tf ( "+q.getNewScorePercent()+")Tj\n"+
	"ET\n"+
	"0.9 0.9 0.9 rg\n"+
	"255 "+nextY+" 320 "+height+" re\n"+
	"f\n";
	for(int i=0; i<commentLines.length;i++)
	{
	s+=
	"BT\n"+
	"0 0 0 rg\n"+
	"256 "+secondY+" Td /F2 10 Tf (" +
	
	ep(commentLines[i])+
		
	")Tj\n"+
	"ET\n";
	secondY -= 10;
	}
	s+="%DATA ROW ENDS\n";
	currentY = nextY;
	
	
	return s;
	
}
/**
 * Escapes PDF-unsafe chars
 * 
 */
private String ep(String s)
{
	if(s==null)return "";
	s=s.replaceAll("\\(","\\(");
	s=s.replaceAll("\\)","\\)");
	return s;
}

public static void main(String[] args)
{
	QBRScorecardSummaryPDF pdf = new QBRScorecardSummaryPDF();
	QBRCommonData d = new QBRCommonData();
	d.setAreaRated("Foundry");
	d.setChange("-2.5");
	d.setClient("Micerosoft");
	d.setCurrentScore("97.5");
	d.setOldScore("92.4");
	d.setOverall_comments("It's fine");
	d.setQbrName("QBR-Microsoft-121212");
	d.setQuarter("1");
	d.setRank("NA");
	d.setRatingPeriod("2332");
	d.setSegment("Line");
	d.setYear("2007");
	
	ArrayList a = new ArrayList();
	QBRScore q = null;
	
	q = new QBRScore();
	q.setClientAttribute("Att1");
	q.setCommentsProvided("Competitiveness\\(Technology\\)\\(()");
	q.setNewScorePercent("12.1");
	q.setOldScorePercent("43.1");
	a.add(q);
	
	q = new QBRScore();
	q.setClientAttribute("");
	q.setCommentsProvided("Competitiveness(Technology)");
	q.setNewScorePercent("12.2");
	q.setOldScorePercent("43.2");
	a.add(q);
	
for(int i=0; i<2; i++)
{
	q = new QBRScore();
	q.setClientAttribute("Att3");
	q.setCommentsProvided("1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890 1234567890\n1234567890" +
			"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890");
	q.setNewScorePercent("12.3");
	q.setOldScorePercent("43.3");
	a.add(q);
}
q = new QBRScore();
q.setClientAttribute("Att3");
q.setCommentsProvided("1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890 1234567890\n1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"");
q.setNewScorePercent("12.3");
q.setOldScorePercent("43.3");
a.add(q);

q = new QBRScore();
q.setClientAttribute("Att3");
q.setCommentsProvided("1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890 1234567890\n1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890\n1234567890 1234567890\n1234567890 1234567890" +
		"");
q.setNewScorePercent("12.3");
q.setOldScorePercent("43.3");
a.add(q);

	System.out.println(pdf.generatePDF(d,a));
	
}
}

