import java.io.*;
import java.security.*;
import java.util.Enumeration;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.*;
import java.security.cert.*;
import java.security.cert.Certificate;

public class CertSignHandler {

	
	public static byte[] CertificateGeneration(String aliasname) throws Exception, NoSuchPaddingException
	{
		
		 byte sin[]=null;
	      
	try {

		// File file = new File("C:/Users/vijay/vmamidala.ks");, ///mnt/f/KeyStoreFile.bin
		File file = new File("vmamidala.ks");
        FileInputStream is = new FileInputStream(file);
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        String password = "alicepwd";
        keystore.load(is, password.toCharArray());


        Enumeration<String> enumeration = keystore.aliases();
        while(enumeration.hasMoreElements()) {
            String alias = (String)enumeration.nextElement();
            if(alias.equals(aliasname)){
            	Certificate certificate = keystore.getCertificate(alias);
            
            sin = certificate.getEncoded();
        
            
            }

        }

    } catch (java.security.cert.CertificateException e) {
        e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (KeyStoreException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
	return sin;
    }
 
	//Singing the certificate with the privatekey
	
	public static byte[] SignWithPrivateKey(byte[] buf,PrivateKey pkey) throws Exception{
		SecureRandom secureRandom = new SecureRandom();
		Signature signature = Signature.getInstance("SHA1WithECDSA");
		signature.initSign(pkey,secureRandom);
		signature.update(buf);
		byte[] digitalSignature = signature.sign();
		return digitalSignature;
		
	}
	
	//verifying signature
	
	public static String VerifySignature(String alias,byte[] cert,PublicKey pkey) throws NoSuchPaddingException, Exception {
		
		Signature signature1 = Signature.getInstance("SHA1WithECDSA");
		System.out.println("alsias"+alias);
		byte[] data = CertificateGeneration(alias);
		signature1.initVerify(pkey);
		signature1.update(data);
		boolean verified = signature1.verify(cert);
		if(verified == false)
		return "bye";
		else 
			return "hi";
	
	}
	
	
	
}
