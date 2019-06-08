/* 
 * Bob
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.security.KeyPair;
import java.util.Scanner;
import javax.crypto.SecretKey;



public class Bob extends Thread {

	private static String serverName;   
	private static int PORT,PORT1;
    private ServerSocket listener = null;
    private ObjectOutputStream output,output1;
	private ObjectInputStream input,input1;
    String name;
	byte[] toSend;
	String firstCommand = ":ka"+" "+"ecdh-secp224r1+x509+aes128/gcm128";
	KeyPair BobKeyPair;
	public byte[] BobPublicKey;
	public  SecretKey BobSecretKey; //To save BobSecretKey for further use
	public String[] command;
	public String str=null;
	Bob client1;
	String Finalconf,ctr;
	KeyPair KeyPairforSign;
	Socket server,server1;
    Bob(int port) throws IOException {
    	PORT = port;
        listener = new ServerSocket(PORT);
        serverName = "localhost";
    }

    public void run() {
        System.out.println("Server Listening on port : " + listener.getLocalPort());
        try {
            Socket cliListener = listener.accept();
            System.out.println("Connected to " + cliListener.getRemoteSocketAddress());
            output = new ObjectOutputStream(cliListener.getOutputStream());
			input = new ObjectInputStream(cliListener.getInputStream());
			
			Thread r = new Read();
		     Thread w = new Write();
            r.start();
            w.start();
   

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    
    public String run1(String alias,byte [] Certreq,byte[] pKey) throws InterruptedException, ClassNotFoundException{
    	
    	try {
        	
        	
            System.out.println("Connecting to " + serverName + " on port " + PORT + "...");
            server1 = new Socket(serverName, PORT);
            System.out.println("Connected to " + server1.getRemoteSocketAddress());
            input1= new ObjectInputStream(server1.getInputStream());
    		output1 = new ObjectOutputStream(server1.getOutputStream());
    		String s =":cert"+" "+new String(Certreq);
     		output1.writeObject(s.getBytes());
        	output1.writeObject(pKey);
     		System.out.println("BobToChatHUB>>  "+":cert"+" "+Certreq+" "+pKey);
     		
         	
         	
         			
    		
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        return "hi";
		
    }
    

    public static void main(String[] args) throws IOException {
        Thread t = new Bob(Integer.parseInt(args[0]));
        PORT1 = Integer.parseInt(args[1]);
        t.start();
    }
    
    
    
    class Read extends Thread {
    	public void run(){
			
			int i=4;
	        byte[] certContent =null;
			while(true){
			try {
				
				
				 byte[] k = (byte[]) input.readObject();
				
				 
				 if(i==0) {				
				    byte[] plainText = Conversation.Decryption(BobSecretKey,k);		
			     	System.out.println("Alice>>  "+new String(plainText));
			     	
			     	 
			     	
			     	 if(new String(plainText).equalsIgnoreCase(":fail")) {
					    	System.out.println("I have trouble with connection,Please restrat the conversation");
					    	
					    }
					    if(new String(plainText).equalsIgnoreCase(":err")) {
					    	System.out.println("Found recoverable error");
					    	break;
					    }
			        }
				 
				 
				 if (i==1 && str.contentEquals(":ka1")){
		        		System.out.println("Alice>>  :ka1"+" "+k); 
		        	      		
		        		BobPublicKey = BobKeyPair.getPublic().getEncoded();
		        		String s = ":ka1";
		        		output.writeObject(s.getBytes());
		        		System.out.println("bob>>  "+":ka1"+" "+BobPublicKey  );
		        		output.writeObject(BobPublicKey); 
		        		i--;
		        		
		        		//it will create BobSecrekey with Bob's Private key and Public key sent by Alice
		        		BobSecretKey= ManageKeys.generateSecretKey(BobKeyPair.getPrivate(),ManageKeys.loadPublicKey(k));
		        		System.out.println("Shared Secret key is generated");
		        	    System.out.println("STATE - Estalished");
		        	    
		        		output.reset();
		        		System.out.println("____________________________________________________________________");
		        		System.out.println("                  LETS BEGIN CONVERSATION                          ");
			        	System.out.println("____________________________________________________________________");
		        			
		            }
				 
				
				 if(i==2){  
			            String string = new String(k);
			            command = string.split(" ");
			            String cmd = command[0]; 		           
			            if(cmd.contentEquals(":kaok")) {
			            	System.out.println("Alice>>  "+command[0]+" "+command[1]);
			            	System.out.println("STATE-waiting for Key Agreement ");
			            	 
			            }
			            if(cmd.contentEquals(":ka1")) { str = ":ka1"; i--;}
				 }   
				 
				 
				 
				 if(i==3 && ctr.equalsIgnoreCase(":cert")) {
	            	 System.out.println("RecievedFromAlice>>  :cert"+" "+certContent+" "+k);

				    	client1 = new Bob(PORT1);
						Finalconf = client1.run1("mykey-alice",certContent,k);
					
						if(Finalconf.equals("hi")) {
							System.out.println("Alice is authenticated");
						i--;
						}
						else
							System.out.println("Alice is not authenticated");
						
						
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
				 
				 
			       
				
			} catch (ClassNotFoundException e) {
				System.out.println("Class not found while reading the message object");
			} catch (IOException e) {e.printStackTrace();
		} catch (Exception e) {
				e.printStackTrace();
				System.out.println("connection closed");
			}
			
		  }
		}
   }

    
    class Write extends Thread {
    	
    	int j=2;
    	 public void run(){
	        	while(true){
	        try{
	        	
	        	if(j==0) {
	        		Scanner sc = new Scanner(System.in);
					String s = sc.nextLine();
					toSend = null;
					toSend = s.getBytes();
					 byte[] cipherText = Conversation.Encryption(BobSecretKey,toSend);  			 
	 			    output.writeObject(cipherText);
	 			    System.out.println("Bob >>");
	        	}
	        	
	        	
	        	
	        	if(j==1) {
	        	
	        		System.out.println("STATE - Intitial state");
	        		System.out.println("Bob>>  "+firstCommand);
	        		toSend = firstCommand.getBytes();		
	        		output.writeObject(toSend);
	        		
	        		System.out.println("STATE-waiting for cipher suite confirmation");
	        		j--;
	        	 }
	        	     
	        	
	        	if(j==2) {
	        		
	        		System.out.println("____________________________________________________________________");
		        	System.out.println("			Bob WINDOW						  ");
		        	System.out.println("____________________________________________________________________");
	        		BobKeyPair = ManageKeys.KeyPairGeneration();    	
	        		byte[] p = CertSignHandler.CertificateGeneration("mykey-bob");
	        		
	                byte[] sin = CertSignHandler.SignWithPrivateKey(p,BobKeyPair.getPrivate());
	                String s =":cert"+" "+new String(sin);
	        		output.writeObject(s.getBytes());
	        		BobPublicKey = BobKeyPair.getPublic().getEncoded();
	        		output.writeObject(BobPublicKey);
	        		System.out.println("BobtoAlice>>  "+":cert"+" "+sin+" "+BobPublicKey  );
	        	     j--;
	        		
	        		
	        	}
	        	
	        	
				 
	        }
	        	
	         catch (Exception e){	
	              e.printStackTrace();
	                System.out.println("No message sent to server");
	                break;
	                }
	        	}
	        }
	       
    }

    
}
