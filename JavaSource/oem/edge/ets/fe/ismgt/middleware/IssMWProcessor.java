//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\IssMWProcessor.java

package oem.edge.ets.fe.ismgt.middleware;

import java.sql.SQLException;

import oem.edge.ets.fe.ismgt.model.ETSIssue;

public interface IssMWProcessor 
{
   
	public static final String VERSION = "1.0";
   
   /**
    * This is the interface operation that all clients will call
    * @param EtsIssObjKey
    * @return int
    * @roseuid 427130140064
    */
   public abstract boolean processRequest() throws SQLException, Exception;
   public abstract void setIssue(ETSIssue issue);
}

	
