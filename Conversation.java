import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;


public class Conversation {
	

		public String CipherSuite1 = "ecdh-secp224r1+X509+AES128/GCM";
	   public String CipherSuite2 = "ecdh-secp56r1+X509+AES128/GCM";
		 public static int TAG_BIT = 128 ; //GCM tab bit 128
		 public static int IV_SIZE = 12 ;
		 
		
	/*	
		public static byte[] Encryption(SecretKey SecretKey,byte[] plainText) throws Exception, NoSuchPaddingException
		{
			 byte[] cipherText=null;
			try {
			 Cipher cipher = Cipher.getInstance("AES/GCM/PKCS5Padding"); 
	         byte iv[] = new byte[IV_SIZE];
	         SecureRandom secRandom = SecureRandom.getInstanceStrong(); ;
	         secRandom.nextBytes(iv); 
			 GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT, iv) ;
			 cipher.init(Cipher.ENCRYPT_MODE,SecretKey,gcmParamSpec,new SecureRandom());
			 cipherText = cipher.doFinal(plainText);
			 }
			 catch (javax.crypto.AEADBadTagException e) {
			        e.printStackTrace();}
			return cipherText;
			
	 
		}

		public static byte[] Decryption(SecretKey SecretKey,byte[] cipherText) throws  Exception, NoSuchPaddingException
		{
			 byte[] plainText=null;
			try {
			 Cipher desCipher = Cipher.getInstance("AES/GCM/PKCS5Padding");;
			 byte iv[] = new byte[IV_SIZE];
			  SecureRandom secRandom = SecureRandom.getInstanceStrong(); ;
	         secRandom.nextBytes(iv); 
			 GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT, iv) ;
			 desCipher.init(Cipher.DECRYPT_MODE,SecretKey,gcmParamSpec,new SecureRandom());
			 plainText = desCipher.doFinal(cipherText);
			 }
			 catch (javax.crypto.AEADBadTagException e) {
			        e.printStackTrace();}
			return plainText;
		}*/
		
		 /*Here I have tried with GCM mode with GCM Parameters, but we were getting problem 
		   called javax.crypto.AEADBadTagException: Tag mismatch!
		   This is the only part we failed to finish and debug the problem.
		   We used CBC mode again which supports multiple ciphersuites.
		   public String CipherSuite11 = "ecdh-secp224r1+X509+AES128/CBC";
	       public String CipherSuite22 = "ecdh-secp56r1+X509+AES128/CBC";
		 */
		  public String CipherSuite11 = "ecdh-secp224r1+X509+AES128/CBC";
	       public String CipherSuite22 = "ecdh-secp56r1+X509+AES128/CBC";
		
		public static byte[] Encryption(SecretKey SecretKey,byte[] plainText) throws Exception, NoSuchPaddingException
		{
			 Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");;
		      
			// SecretKeySpec sKey =   ManageKeys.Keys(); 
			 IvParameterSpec ivspec = ManageKeys.ivParameters(cipher);
			 cipher.init(Cipher.ENCRYPT_MODE, SecretKey,ivspec);
			 byte[] cipherText = cipher.doFinal(plainText);
			// System.out.println("Encrypted  " +cipherText);
			 return cipherText;
			
	 
		}

		public static byte[] Decryption(SecretKey SecretKey,byte[] cipherText) throws  Exception, NoSuchPaddingException
		{
			 Cipher desCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");;
	        // SecretKeySpec sKey =  ManageKeys.Keys();
	         IvParameterSpec ivspec = ManageKeys.ivParameters(desCipher);
			 desCipher.init(Cipher.DECRYPT_MODE, SecretKey,ivspec);
			 byte[] plainText = desCipher.doFinal(cipherText);
			// System.out.println("Decrypted  " +plainText);
			 return plainText;
		}
		
	
	
	

}
