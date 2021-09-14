package oem.edge.ed.odc.stm;

public class AuthenticateUser {
	private static String UnAuthorizedStatusCode="401  UnAuthorized";
	public static String RTSPmethods[]={ "DESCRIBE", "ANNOUNCE", "GET_PARAMETER",
					     "OPTIONS", "PAUSE", "PLAY",
				   	     "SETUP", "SET_PARAMETER", "TEARDOWN"};
        private String mediaPath;
        private boolean trust ;

	public String protocol;
	public String sequence;

	public static String getUnAuthCode(){return UnAuthorizedStatusCode;}

	public String getProtocol(){ return protocol;}

	public String getSequence(){ return sequence; }

	public boolean Authenticate(){ return trust; }

	public void checkAuthenticity(String str, String prot, String Seq){
//System.out.println("path obtained from user is:"+ str + " protocol is:" + prot +
//         " sequence is :" + Seq+" media path:"+ mediaPath);
		protocol = prot;
		sequence = Seq;
		str=str.trim();
		mediaPath=mediaPath.trim();

		if(str.equalsIgnoreCase(mediaPath))
			trust = true;
		else trust = false;


	}
        public void setMediaPath(String mediaPath){
		this.mediaPath = mediaPath ;
		}

	public String getMediaPath(){
		return mediaPath;
		}
	
	public void setTrust(boolean trust){
		this.trust = trust;
		}

	public boolean getTrust(){
		return trust;
		}

}
