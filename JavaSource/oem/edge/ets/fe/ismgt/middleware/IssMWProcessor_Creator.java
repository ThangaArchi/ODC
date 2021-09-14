//Source file: C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\IMRearch\\JavaSource\\oem\\edge\\ets\\fe\\ismgt\\middleware\\IssMWProcessor_Creator.java

package oem.edge.ets.fe.ismgt.middleware;

import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;


public class IssMWProcessor_Creator 
{
	public static final String VERSION = "1.0";
   /**
    * @roseuid 4276545B0183
    */
   public IssMWProcessor_Creator() 
   {
     
   }
   
   /**
    * @return oem.edge.ets.fe.ismgt.middleware.IssMWProcessor_Product
    * @roseuid 4271026F0102
    */
   public  oem.edge.ets.fe.ismgt.middleware.IssMWProcessor factoryMethod(EtsIssObjectKey _issobjkey){
   	
   	IssMWProcessor instance =  new ETS_IssMWProcessor(_issobjkey);
   	return instance;
   	
   }
}
