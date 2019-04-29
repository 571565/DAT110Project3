package no.hvl.dat110.util;

/**
 * exercise/demo purpose in dat110
 * @author tdoy
 *
 */

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;





public class Hash {
	
	public static int mbit = 4;			// using SHA-1 compresses/hashes to 160bits	
	
	public static int sbit = 4;			// we use this for the size of the finger table
	
	private static BigInteger hashint;
	
	public static BigInteger hashOf(String entity) {		
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			byte[] digest = md.digest(entity.getBytes("utf8"));
			
			mbit = digest.length*8;			// number of bits used by the hash function
			
			// use DatatypeConverter
			String hashvalue = DatatypeConverter.printHexBinary(digest);
			
			hashint = new BigInteger(hashvalue, 16);

			
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		return hashint;
	}
	
	public static BigInteger getHashInt() {
		
		return hashint;
	}
	
	/*public static BigInteger customHash(String entity) {
		
		BigInteger modulos = addressSize();
		
		try {
			byte[] ebyte = entity.getBytes("utf8");
			
			// use DatatypeConverter
			String hashvalue = DatatypeConverter.printHexBinary(ebyte);
			
			hashint = new BigInteger(hashvalue, 16);
			
			// this is a basic modulo operation
			hashint = hashint.mod(modulos);
			
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return hashint;
	}*/
	
	public static BigInteger addressSize() {
		
		BigInteger modulos = new BigInteger("2");
		modulos = modulos.pow(mbit);				// 2^mbit
		
		return modulos;
	}
	
	public static void main(String[] args) throws UnknownHostException {
		
		System.out.println(InetAddress.getLocalHost().getHostAddress());
		
		String[] ips = {"192.168.1.0", "192.168.1.1", "192.168.1.2", "192.168.1.3", "192.168.1.4", "192.168.1.5",
				"192.168.1.6", "192.168.1.7", "192.168.1.8", "192.168.1.9", "158.37.70.149"};
		
		for (int i=0; i<ips.length; i++) {
			//System.out.println(ips[i]+" | "+Hash.customHash(ips[i]));
		}
		//System.out.println(Hash.customHash("158.37.70.149"));
	}

}
