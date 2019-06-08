import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ManageKeys {
	
    
	public static KeyPair KeyPairGeneration() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
			
		        /*EC is supporting in my system, and I can use both EC and DH instances but in my friend's system
		       EC not supported. Here I am using EC instance along with ECDH - Elliptic Curve Diffie-Hellman Key agreement.
		         */
	        	KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC"); 
	    		ECGenParameterSpec ecsp;
	    		ecsp = new ECGenParameterSpec("secp256r1");
	    		kpg.initialize(ecsp);
   		        KeyPair keyPair = kpg.genKeyPair();
   		        return keyPair;
	     }
    
	
	public static SecretKey generateSecretKey(PrivateKey privateKey,
            PublicKey publicKey) {
        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(publicKey, true); //ECDH Key agreement is done till this step.
            byte[] sharedSecret = keyAgreement.generateSecret(); //Generated Same shared key
            //System.out.println("Shareed Secrety"+sharedSecret);
            /*Generating 16bytes SharedSecrekey
             * I am not using base64encoded form so, generating 16byte shared key directly with below code.
             * and adding ivspec parameters with respect to cipher block size using ivParameters method defined at 
             * the end.
             */
            SecretKeySpec SharedAESKey = new SecretKeySpec(sharedSecret, 0, 16, "AES");  
            return SharedAESKey;
                   
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
	
	
	public static PublicKey loadPublicKey(byte[] bs) throws GeneralSecurityException {
		 KeyFactory kf = KeyFactory.getInstance("EC");
		 X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bs);
         PublicKey publicKey2 = kf.generatePublic(publicKeySpec);
		return publicKey2;

		
	}
	
	public static PublicKey loadPublicKeyDSA(byte[] bs) throws GeneralSecurityException {
		 KeyFactory kf = KeyFactory.getInstance("EC");
		 X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bs);
        PublicKey publicKey2 = kf.generatePublic(publicKeySpec);
		return publicKey2;

		
	}
	
	public static IvParameterSpec ivParameters(Cipher c)
	{
		 byte[] iv = new byte[c.getBlockSize()];
		 IvParameterSpec ivspec = new IvParameterSpec(iv);
		 return ivspec;
	}

	
	
	
	
}

	
	
	