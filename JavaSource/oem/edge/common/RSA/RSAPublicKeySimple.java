package oem.edge.common.RSA;
import java.math.BigInteger;
import java.io.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

public class RSAPublicKeySimple implements java.io.Serializable, RSAPublicKey {

    // e: public exponent
    // n: modulus
    private BigInteger e,n;


    public RSAPublicKeySimple(BigInteger e, BigInteger n) {
	this.e = e;
	this.n = n;
    }

    public RSAPublicKeySimple() { }


    public BigInteger encipher(BigInteger message) {
	if(message.bitLength() >= getKeyLength()) {
	    System.err.println("WARNING: message length >= modulus length. Returning zero");
	    return BigInteger.ZERO;
	}
	else
	    return (message.modPow(e,n));
    }


    public boolean checkSignature(BigInteger sig, BigInteger message) {
	return (message.equals(sig.modPow(e,n)));
    }


    public static RSAPublicKey load(String file) throws IOException {
	ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
	RSAPublicKey key = null;
	try {
	    key = (RSAPublicKey)in.readObject();
	}
	catch(ClassNotFoundException e) {
	    e.printStackTrace();
	}
        in.close();
        return key;
    }


    public void store(String file) throws IOException {
	ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
	out.writeObject(this);
	out.close();
    }


    public int getKeyLength() {
        return n.bitLength();
    }


    public String toString() {
	return ("Modulus: " + n + "\nPublic Exponent: " + e);
    }

}
