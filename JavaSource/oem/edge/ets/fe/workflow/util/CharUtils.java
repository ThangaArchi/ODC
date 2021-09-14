/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.util;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Class       : CharUtils
 * Package     : oem.edge.ets.fe.workflow.util
 * Description : 
 * Date		   : Oct 13, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class CharUtils {

	public static ArrayList getStringErrors(String s, int maxLength, boolean emptyCheck, String fieldName)
	{
		ArrayList errs = new ArrayList();
		if(s==null)
			{errs.add(fieldName+" cannot be null"); return errs;}
		if(SQLize(s).length()>maxLength)
			errs.add(fieldName+" must not exceed "+maxLength+" characters.");
		if(emptyCheck && s.trim().length()==0)
			errs.add(fieldName+" must be provided");
		return errs;
	}
	
	public static String SQLize(String s)
	{
		if(s==null)return null;
		return s.replaceAll("'","''");
		
	}
	public static String HTMLize(String s)
	{
		if(s==null)return null;
		String temp = null;
		temp = s.replaceAll("&","&amp;");
		temp = temp.replaceAll("<","&lt;");
		temp = temp.replaceAll(">","&gt;");
		return temp;
	}
	public static String HTMLizeNoNull(String s)
	{
		if(s==null)return "";
		String temp = null;
		temp = s.replaceAll("&","&amp;");
		temp = temp.replaceAll("<","&lt;");
		temp = temp.replaceAll(">","&gt;");
		return temp;
	}
	public static String deHTMLize(String s)
	{
		if(s==null)return null;
		String temp = s;
		temp = temp.replaceAll("&lt;","<");
		temp = temp.replaceAll("&gt;",">");
		temp = temp.replaceAll("&newline;","\n");
		temp = temp.replaceAll("&amp;","&");
		return temp;
	}
	public static void main(String[] args)
	{
		System.out.println(SQLize("D''souza/Alan"));
		System.out.println(HTMLize("Jack&Jill<br />went up<&<>>&>the hill!"));
		System.out.println(deHTMLize(HTMLizeNoNull("/\\.")));
	}
	public static boolean isAlNum(String s)
	{
		/*
		 * THIS METHOD IS DISABLED.
		 */
		return true;
		
		/*char[] chars = s.toCharArray();
		for(int i =0; i< chars.length; i++)
		{
			//System.out.println("Legal char: "+chars[i]+" value:"+(int)chars[i]);
			if(!isAllowed(chars[i]))
			{
				//System.out.println("Illegal char: "+chars[i]+" value:"+(int)chars[i]);
				return false;
			}
		}
		return true;*/
	}
	
	private static boolean isAllowed(char b)
	{
		String allowed ="\n\t !\"#$%&'()*+,-./0123456789:;<=>?@ACDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
		//allowed+="ÇüéâäàåçêëèïîìÄÅæÆôöòûùÿÖÜ¢£¥ƒáíóúñÑªº¿¬½¼¡«»µ±²°";
		char[] allowedChars = allowed.toCharArray();
		
		/*char[] llim = {'\n', '\t', ' ', 20, 21, 32, 128, 145, 159, 170, 230, 253, 250};
		char[] ulim = {'\n', '\t', ' ',20, 21, 126, 143, 157, 168, 175, 230, 253, 250};
		boolean passed = false;
		for(int i = 0; i< llim.length; i++)
		{
			if(b >=llim[i] && b <= ulim[i])
				passed = true;
		}*/
		for(int i = 0; i< allowedChars.length; i++)
		{
			if((int)b == (int)allowedChars[i] || (int)b==13 || (int)b==30)
				{return true;}
		}
		return false;
	}

}

