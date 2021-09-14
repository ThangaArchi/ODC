package oem.edge.ets_pmo.datastore.document;

import java.io.*;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import oem.edge.ets_pmo.util.Base64Decoder;

import org.apache.log4j.Logger;
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


public class Doc 
{
	private static String CLASS_VERSION = "4.5.1";
	static Logger logger = Logger.getLogger(Doc.class);
   private String id;
   private String element_Name;
   private String Attachment;
   private String Summary;
   private byte[] blob_data;
   private String revision;
   private Timestamp creation_Date;
   private Timestamp last_Checkin;
   private int compressed_size;
   private int document_Size;
   private int doc_Type;
   private Vector vDocs=null;
   private String owner_Id = "default";
   private String securityLevel = "default";
   private Timestamp uploadDate;
   private String trail_hh_mm_ss_ffffffff = " 00:00:00.00000000";
   private char isCompressed = 'N';
   private String temp_filename;
   
   private ZipInputStream  	  zis   = null;
   private BufferedInputStream   bis   = null;
   private FileInputStream       fis   = null;
    
   /**
   Returns the attachment.
   @return String
   @roseuid 412E09B903D9
    */
   public String getAttachment() 
   {
		return Attachment;    
   }
   
   
   public InputStream getBlob_data_stream(boolean IsCompressed)
   {

    try
    {
        fis   = new FileInputStream( temp_filename );
        //bis   = new BufferedInputStream( fis );
        zis  = new  ZipInputStream( fis ); //bis );
        //      Get the first entry
        ZipEntry entry = zis.getNextEntry();
    }   // end try
    catch( java.io.IOException e )
    {
		logger.error("getBlob_data_stream(boolean)", e);
    }   // end catch
    
    return zis;
   }
   
   
   
	public void close_data_stream() {
		
		   try{   
		   	zis.closeEntry();
	        zis.close();  
	        //bis.close();   
	        fis.close();   
	       } catch( Exception e ){		logger.error("close_data_stream()", e);}
		
	}
	
	
   /**
   Returns the blob_data. Checks if the ISCOMPRESSED is true or false.
   If true, returns the compressed doc. Otherwise, returns the expanded doc.
   @param IsCOMPRESSED
   @return byte[]
   @roseuid 412E09B903E3
    */
   public byte[] getBlob_data(boolean IsCOMPRESSED) 
   {
   	byte [] retBytes ;
	if(IsCOMPRESSED == false){
			
			return DocZIPDecompressBytes(this.blob_data, this.getDocument_Size());
		}
	return this.blob_data;    
   }
   
   /**
   Returns the compressed_size.
   @return String
   @roseuid 412E09BA0005
    */
   public int getCompressed_size() 
   {
		return compressed_size;    
   }
   
   /**
   Returns the creation_Date.
   @return String
   @roseuid 412E09BA000F
    */
   public Timestamp getCreation_Date() 
   {
		return creation_Date;    
   }
   
   /**
   Returns the document_Size.
   @return String
   @roseuid 412E09BA002E
    */
   public int getDocument_Size() 
   {
		return document_Size;    
   }
   
   /**
   Returns the last_Checkin.
   @return String
   @roseuid 412E09BA0038
    */
   public Timestamp getLast_Checkin() 
   {
		return last_Checkin;    
   }
   
   /**
   Returns the revision.
   @return String
   @roseuid 412E09BA0042
    */
   public String getRevision() 
   {
		return revision;    
   }
   
   /**
   Returns the summary.
   @return String
   @roseuid 412E09BA0056
    */
   public String getSummary() 
   {
		return Summary;    
   }
   
   /**
   Sets the attachment.
   @param attachment The attachment to set
   @roseuid 412E09BA0060
    */
   public void setAttachment(String attachment) 
   {
		Attachment = attachment;    
   }
   
   /**
   Sets the blob_data.
   The data is always store in compressed state. But, when we retrieve the data, we retrieve the compressed 
   bytes/ uncompressed bytes depending upon what is asked for. See getBlob_data()
   @param blob_data The blob_data to set
   @param IsCompress 
   @roseuid 412E09BA006A
    */
   public void setBlob_data(String blob_data, boolean IsCompress) 
   {
  
   	/*
   	temp_filename = ""+ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir() + "temp_" +  System.currentTimeMillis();	
   	BufferedWriter out;
	try {
		out = new BufferedWriter(new FileWriter(temp_filename));
		out.write(blob_data);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    */
   	
	Base64Decoder BDecode = new Base64Decoder();

		this.blob_data = BDecode.decodeInBytes(blob_data);
	

		if(IsCompress == true){
			this.setIsCompressed('Y');
			
		}
		else { //to implement an Uncompression algorithm
			this.setIsCompressed('N');
			
		}    
   }
   
   /**
   Sets the compressed_size.
   @param compressed_size The compressed_size to set
   @roseuid 412E09BA007E
    */
   public void setCompressed_size(String compressed_size) 
   {
		this.compressed_size = Integer.parseInt(compressed_size);    
   }
   
   /**
   Sets the creation_Date.
   @param creation_Date The creation_Date to set
   @roseuid 412E09BA0088
    */
   public void setCreation_Date(String creation_Date) 
   {
		
		if(creation_Date.trim().equalsIgnoreCase(""))
			return;
		try{
			this.creation_Date = Timestamp.valueOf(creation_Date);	
		}
		catch(IllegalArgumentException ie){
			creation_Date = creation_Date + this.trail_hh_mm_ss_ffffffff;
			this.creation_Date = Timestamp.valueOf(creation_Date);
		}    
   }
   
   /**
   Sets the document_Size.
   @param document_Size The document_Size to set
   @roseuid 412E09BA0093
    */
   public void setDocument_Size(String document_Size) 
   {
		this.document_Size = Integer.parseInt(document_Size);    
   }
   
   /**
   Sets the last_Checkin.
   @param last_Checkin The last_Checkin to set
   @roseuid 412E09BA00A6
    */
   public void setLast_Checkin(String last_Checkin) 
   {
		if(last_Checkin.trim().equalsIgnoreCase(""))
			return;
		try{
			this.last_Checkin = Timestamp.valueOf(last_Checkin);	
		}
		catch(IllegalArgumentException ie){
			last_Checkin = last_Checkin + this.trail_hh_mm_ss_ffffffff;
			this.last_Checkin = Timestamp.valueOf(last_Checkin);
		}    
   }
   
   /**
   Sets the revision.
   @param revision The revision to set
   @roseuid 412E09BA00B1
    */
   public void setRevision(String revision) 
   {
		this.revision = revision;    
   }
   
   /**
   Sets the summary.
   @param summary The summary to set
   @roseuid 412E09BA00C4
    */
   public void setSummary(String summary) 
   {
		Summary = summary;    
   }
   
   /**
   Returns the id.
   @return String
   @roseuid 412E09BA00CF
    */
   public String getId() 
   {
		return id;    
   }
   
   /**
   Sets the id.
   @param id The id to set
   @roseuid 412E09BA00D8
    */
   public void setId(String id) 
   {
		this.id = id;    
   }
   
   /**
   Returns the doc_Type.
   @return String
   @roseuid 412E09BA00EC
    */
   public int getDoc_Type() 
   {
		return doc_Type;    
   }
   
   /**
   Sets the doc_Type.
   @param doc_Type The doc_Type to set
   @roseuid 412E09BA00F6
    */
   public void setDoc_Type(String doc_Type) 
   {
		if( doc_Type.equalsIgnoreCase("DOCUMENTFOLDER") || doc_Type.equalsIgnoreCase("NOTEMINUTES") ){
			this.doc_Type = 1;
		}
		else if( doc_Type.equalsIgnoreCase("DOCUMENT")){
			this.doc_Type = 2;
		}
		else this.doc_Type = -1;    
   }
   
   /**
   Returns the element_Name.
   @return String
   @roseuid 412E09BA0101
    */
   public String getElement_Name() 
   {
		return element_Name;    
   }
   
   /**
   Sets the element_Name.
   @param element_Name The element_Name to set
   @roseuid 412E09BA0114
    */
   public void setElement_Name(String element_Name) 
   {
		this.element_Name = element_Name;    
   }
   
   /**
   @return int
   @roseuid 412E09BA011F
    */
   public int RetrievePopulationOfDocs() 
   {
		if(vDocs == null)
			return -1;
		return vDocs.size();    
   }
   
   /**
   @param doc
   @roseuid 412E09BA0128
    */
   public void populateVDocs(Doc doc) 
   {
		if(this.vDocs == null){
			vDocs = new Vector();
		}
		vDocs.add(doc);    
   }
   
   /**
   @param index
   @return oem.edge.ets_pmo.datastore.document.Doc
   @throws java.lang.IndexOutOfBoundsException
   @roseuid 412E09BA013C
    */
   public Doc retrieveDoc(int index) throws java.lang.IndexOutOfBoundsException 
   {
	Doc doc = null;
	if(this.vDocs != null &&
		!this.vDocs.isEmpty()){
			if(index >= vDocs.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vDocs");
			}
	doc = (Doc)vDocs.get(index);
	}
	return doc;    
   }
   
   /**
   Returns the owner_Id.
   @return String
   @roseuid 412E09BA01BE
    */
   public String getOwner_Id() 
   {
		return owner_Id;    
   }
   
   /**
   Sets the owner_Id.
   @param owner_Id The owner_Id to set
   @roseuid 412E09BA01C8
    */
   public void setOwner_Id(String owner_Id) 
   {
		this.owner_Id = owner_Id;    
   }
   
   /**
   Returns the securityLevel.
   @return String
   @roseuid 412E09BA01DC
    */
   public String getSecurityLevel() 
   {
		return securityLevel;    
   }
   
   /**
   Sets the securityLevel.
   @param securityLevel The securityLevel to set
   @roseuid 412E09BA01E6
    */
   public void setSecurityLevel(String securityLevel) 
   {
		this.securityLevel = securityLevel;    
   }
   
   /**
   Returns the uploadDate.
   @return Timestamp
   @roseuid 412E09BA01FA
    */
   public Timestamp getUploadDate() 
   {
		return uploadDate;    
   }
   
   /**
   Sets the uploadDate.
   @param uploadDate The uploadDate to set
   @roseuid 412E09BA020E
    */
   public void setUploadDate(Timestamp uploadDate) 
   {
		this.uploadDate = uploadDate;    
   }
   
   /**
   Returns the isCompressed.
   @return Character
   @roseuid 412E09BA0222
    */
   public char getIsCompressed() 
   {
		return isCompressed;    
   }
   
   /**
   Sets the isCompressed.
   @param isCompressed The isCompressed to set
   @roseuid 412E09BA022C
    */
   public void setIsCompressed(char isCompressed) 
   {
		this.isCompressed = isCompressed;    
   }
   
   /**
   @return java.lang.String
   @roseuid 412E09BA0240
    */
   public String toString() 
   {
		String str = "DOC DATA \n" + 
						" id : " + id + " \n" +	
						"elementname :" + this.element_Name + " \n" +	
						"attachment: " + this.Attachment  + " \n" +	
						"summary:"   + " \n" +	
						"blob_data:"    + " \n" +	
						"revision: "    + " \n" +	
						"creation_date: "      + " \n" +	
						"last_Checkin: " + this.last_Checkin   + " \n" +	
						"compressed_size: " + this.compressed_size   + " \n" +	
						"document_Size: " +this.document_Size   + " \n" +	
						"doc_Type: " + this.doc_Type   + " \n" +	
						"owner_Id: " + this.owner_Id   + " \n" +	
						"securityLevel: " + this.securityLevel   + " \n" +	
						" isCompressed: " + this.isCompressed + " \n";
		return str;    
   }
   
   /**
   Returns the vDocs.
   @return Vector
   @roseuid 412E09BA024A
    */
   public Vector getVDocs() 
   {
		return vDocs;    
   }
   public byte[] DocZIPDecompressBytes(byte[] compressedBytes, int EXPAND_SIZE){   
   	ZipInputStream zis = null;
	   ByteArrayOutputStream out = null;
	   try{
		   ByteArrayInputStream bArrIs= new ByteArrayInputStream(compressedBytes);
		   zis = new ZipInputStream(bArrIs);
		   ZipEntry entry;
			
		   if((entry = zis.getNextEntry()) != null){
				   logger.debug("Extracting: " + entry);
				   byte[] data = new byte[EXPAND_SIZE];
				   int count = -1;
				   out  = new ByteArrayOutputStream();
				   while ((count = zis.read(data, 0, EXPAND_SIZE)) != -1) {
						   out.write(data, 0, count);
				   }
			   }
		   }
	   catch(IOException e){
		   logger.error("IOException caused while decompressing a doc",e);
		   //System.out.println("IOException caused while decompressing a doc");
		   //e.printStackTrace();
		   }
	   
		   return out.toByteArray();
   }


	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

	public String getFilename() {
		return temp_filename;
	}
	public void setFilename(String str) {
		temp_filename = str;
		
	}


	

}
