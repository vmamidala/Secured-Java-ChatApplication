/* 
 * Alice
 */

import java.net.*;
import java.security.KeyPair;
import java.security.PublicKey;
import java.io.*;
import java.util.Scanner;
import javax.crypto.SecretKey;

public class Alice extends Thread {

    private static String serverName;
    private static int PORT,PORT1;
    BufferedReader br;
    Socket server,server1;
    boolean readContinue;
	byte[] toSend;
	KeyPair AliceKeyPair;
    private ObjectOutputStream output,output1;
	private ObjectInputStream input,input1;
	String firstCommand = ":ka"+" "+"ecdh-secp224r1+x509+aes128/gcm128";
	String secondCommand = ":kaok"+" "+"ecdh-secp224r1+x509+aes128/gcm128";
	String thirdCommand =":ka1";
	public byte[] AlicePublicKey;
	public  SecretKey AliceSecretKey;  //To save AliceSecretKey for further use
	public String[] command;
	public String str,ctr,Finalconf;
	Alice client1;
    public Alice(int p) {
        this.serverName = "localhost";
        this.PORT = p;

    }

    public void run() {
        try {
        	
        	
       
            System.out.println("Connecting to " + serverName + " on port " + PORT + "...");
            server = new Socket(serverName, PORT);
            System.out.println("Connected to " + server.getRemoteSocketAddress());
            input = new ObjectInputStream(server.getInputStream());
    		output = new ObjectOutputStream(server.getOutputStream());
            Thread r = new Read();
            Thread w = new Write();
            
            r.start();
         	w.start();
        	
        	
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String run1(String alias,byte [] Certreq,byte[] pKey) throws InterruptedException, ClassNotFoundException{
    	byte[] k = null;
    	try {
        	
        	
            System.out.println("Connecting to " + serverName + " on port " + PORT + "...");
            server1 = new Socket(serverName, PORT);
            System.out.println("Connected to " + server1.getRemoteSocketAddress());
            input1= new ObjectInputStream(server1.getInputStream());
    		output1 = new ObjectOutputStream(server1.getOutputStream());
    		String s =":cert"+" "+new String(Certreq);
     		output1.writeObject(s.getBytes());
     		//byte[] s1 = pKey.getEncoded();
     		output1.writeObject(pKey);
     		System.out.println("AliceToChatHUB>>  "+":cert"+" "+Certreq+" "+pKey);
     		// k = (byte[]) input1.readObject();	
         	
         	
         			
    		
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        return "hi";
		
    }
    

    public static void main(String[] args) throws IOException {

        Alice client = new Alice(Integer.parseInt(args[0]));
        client.run(); 
        PORT1 = Integer.parseInt(args[1]);
	        
    }

    
    class Read extends Thread {
    	int i=4;
        byte[] certContent =null;
    	public void run(){
			while(true){
        try{
        	
         
        	byte[] k = (byte[]) input.readObject();
        	
       	 
			 if(i==0){		
					byte[] plainText = Conversation.Decryption(AliceSecretKey,k);		
				    System.out.println("Bob>>  "+new String(plainText));
				    
				    
				    if(new String(plainText).equalsIgnoreCase(":fail")) {
				    	System.out.println("I have trouble with connection,Please restrat the conversation");
				    	 break;
				    }
				    if(new String(plainText).equalsIgnoreCase(":err")) {
				    	System.out.println("Found recoverable error");
				    	break;
				      }
		            }
        	
        	  
            if (i==1 && str.contentEquals(":ka1")){
            	   System.out.println("Bob>>  :ka1"+" "+k);  
        		   i--;
        		  
        		   AliceSecretKey= ManageKeys.generateSecretKey(AliceKeyPair.getPrivate(),ManageKeys.loadPublicKey(k));
        		   System.out.println("Shared Secret key is generated");
        		   System.out.println("STATE - Estalished");
        		   output.reset();
	        	   System.out.println("____________________________________________________________________");
	        	   System.out.println("                  LETS BEGIN CONVERSATION                           ");
	        	   System.out.println("____________________________________________________________________");
                   }	     
        	
        	
            if(i==2){
			      String string = new String(k);
			      command = string.split(" ");
			      String cmd = command[0]; 
			      if(cmd.contentEquals(":ka")) {
			    	 
			    	      System.out.println("STATE - Intitial state");
			    	      System.out.println("Bob>>  "+firstCommand);
	   		        	  System.out.println("Alice>>  "+secondCommand);
			        	  toSend = secondCommand.getBytes();			
			        	  output.writeObject(toSend);
			        	  AliceKeyPair = ManageKeys.KeyPairGeneration();
			        	  PublicKey  a = AliceKeyPair.getPublic();
			        	  AlicePublicKey = a.getEncoded();
			        	  String s = ":ka1";
			        	  output.writeObject(s.getBytes());
			        	  System.out.println("Alice>>  "+":ka1"+" "+AlicePublicKey  );
			        	  System.out.println("STATE - Waiting for Key Agreement");
			        	  output.writeObject(AlicePublicKey); 	
		                     }
			             if(cmd.contentEquals(":ka1")) {  str = ":ka1";i--;}
			                 
                    }
            
            
            if(i==3 && ctr.equalsIgnoreCase(":cert")) {
            	 System.out.println("____________________________________________________________________");
	        	   System.out.println("			Alice WINDOW						  ");
	        	   System.out.println("____________________________________________________________________");
            	 System.out.println("RecievedFromBob>>  :cert"+" "+certContent+" "+k);

			    	client1 = new Alice(PORT1);
					Finalconf = client1.run1("mykey-Bob",certContent,k);
				
					if(Finalconf.equals("hi")) {
						System.out.println("Bob is authenticated");
					i--;
					}
					else
						System.out.println("Bob is not authenticated");
					
					
				
		        		
		        		AliceKeyPair = ManageKeys.KeyPairGeneration();    	
		        		byte[] p = CertSignHandler.CertificateGeneration("mykey-bob");
		                byte[] sin = CertSignHandler.SignWithPrivateKey(p,AliceKeyPair.getPrivate());
		                String s =":cert"+" "+new String(sin);
		        		output.writeObject(s.getBytes());
		        		AlicePublicKey = AliceKeyPair.getPublic().getEncoded();
		        		output.writeObject(AlicePublicKey);
		        		System.out.println("AliceTOBob>>  "+":cert"+" "+sin+" "+AlicePublicKey  );
		        	
		        				
            }
               
            
            if(i==4) {
            	 String string = new String(k);
			      command = string.split(" ");
			      String cmd = command[0]; 
			      if(cmd.contentEquals(":cert")) 
			      {
			    	  certContent = command[1].getBytes();	
			    	  ctr=":cert";
			    	  
						
			      }
			      i--;
            	
            }
        	}     	
        	
        
       catch (Exception e){
       		e.printStackTrace();
              System.out.println("connection closed");
                }
      	}
	}
  }
    
    class Write extends Thread {
    	int d=0;
    	 public void run(){
         	while(true){
         try{  
 	            
				Scanner sc = new Scanner(System.in);
				String s = sc.nextLine();
				toSend = null;
				toSend = s.getBytes();
				byte[] cipherText = Conversation.Encryption(AliceSecretKey,toSend);  		
 			    output.writeObject(cipherText);
 			    System.out.println("Alice >>");
 	             
         	
         } catch (Exception e){
               e.printStackTrace();
                 System.out.println("No message sent to server");
                 break;
                 }
         	}
         }
    
}


    
     
   
}

