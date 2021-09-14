package oem.edge.ets.fe.workflow.util.pdfutils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.w3c.dom.Document;


/*
 * Created on Mar 14, 2007
 *
 * TODO To change the template fo r this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Rapaljeb
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PdfGenerator {
	private static final String XHTML_TO_XSLFO_XSL = "xhtml-to-xslfo.xsl";
	
	public void generatePdf(InputStream xml, InputStream xslt, OutputStream pdfStream) throws TransformerException, TransformerConfigurationException, IOException, FOPException {
		/* fop-0.20.5 *
		Driver driver = new Driver();
		driver.setRenderer(Driver.RENDER_PDF);
		driver.setOutputStream(pdf);
		Result result = new SAXResult(driver.getContentHandler());
		*/
		// for fop-0.93
		FopFactory fopFactory = FopFactory.newInstance();
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfStream);
		Result result = new SAXResult(fop.getDefaultHandler());
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslt));
		transformer.transform(new StreamSource(xml), result);
	}
	
	public void generatePdf(Document doc, InputStream xslt, OutputStream pdfStream) throws TransformerException, TransformerConfigurationException, IOException, FOPException {
		FopFactory fopFactory = FopFactory.newInstance();
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, pdfStream);
		Result result = new SAXResult(fop.getDefaultHandler());
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslt));
		transformer.transform(new DOMSource(doc), result);
	}
	
	public static void main(String args[]) throws Exception {
		String file = "everything";
		PdfGenerator pdfGenerator = new PdfGenerator();
		pdfGenerator.generatePdf(
			new FileInputStream("/tmp/PdfGenerator/"+file+".html"),
			PdfGenerator.class.getResourceAsStream("/"+XHTML_TO_XSLFO_XSL),
			new FileOutputStream("/tmp/PdfGenerator/"+file+".pdf")
		);
		System.out.println("done.");
	}
}
