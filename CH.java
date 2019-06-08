/* 
 * CH
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.security.KeyPair;
import java.util.Scanner;
import javax.crypto.SecretKey;



public class CH extends Thread {

    private static int PORT,PORT1;
    private ServerSocket listener = null;
    private ObjectOutputStream output;
	private ObjectInputStream input;
    String name;
	byte[] toSend;
	KeyPair BobKeyPair;
	public byte[] BobPublicKey;
	public  SecretKey BobSecretKey; //To save BobSecretKey for further use
	public String[] command;
	public String str=null;
	Socket cliListener;
	Thread r2,w2;
	static Thread t;
	String ctr;
	byte[] certContent;
    CH(int port) throws IOException {
    	PORT = port;
        listener = new ServerSocket(PORT);
    }

    public void run() {
        System.out.println("Server Listening on port : " + listener.getLocalPort());
        try {
        	
            cliListener = listener.accept();
            System.out.println("Connected to " + cliListener.getRemoteSocketAddress());
            output = new ObjectOutputStream(cliListener.getOutputStream());
			input = new ObjectInputStream(cliListener.getInputStream());
			 r2 = new Read();
		     w2 = new Write();
            r2.start();
            w2.start();
   

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        t = new CH(Integer.parseInt(args[0]));
        PORT1 = Integer.parseInt(args[1]);
        t.start();
    }
    
    
    
    class Read extends Thread {
    	int i=2;
    	int j=0;
    	public void run(){
			
			while(true){
			try {
				
				
				byte[] k = (byte[]) input.readObject();	
				       
                        //Port verification.				
 						 if(listener.getLocalPort()==PORT)
				     	 {
				     		
				     	 System.out.println("Alice Verified");
				      	 }
						 if(i==1 && ctr.equalsIgnoreCase(":cert")) {		
						 j++;
											 
						 t = new CH(PORT1);
				         t.start();
								
						 }
			     	
			     	 if(listener.getLocalPort()==PORT1)
			     	 {
			     		
			     	 }
		            
		            if(i==2) {
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
		            
		            //Signatures verification respect to recieved alias.
		            if(i==3 && ctr.equalsIgnoreCase(":cert")) {
		            	String b;
						if(ctr=="Bob")
		            	 b = CertSignHandler.VerifySignature("mykey-bob",certContent,ManageKeys.loadPublicKeyDSA(k));	
						else
		            	 b = CertSignHandler.VerifySignature("mykey-Alice",certContent,ManageKeys.loadPublicKeyDSA(k));	
		            	output.writeObject(b.getBytes());
		            	
		            	
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
    	int j=1;
    	 public void run(){
	        	while(true){
	        try{
	        	
	        	
	        	if(j==0) {
	        		Scanner sc = new Scanner(System.in);
					String s = sc.nextLine();
					toSend = null;
					toSend = s.getBytes();
		     	    output.writeObject(toSend);
	 			    System.out.println("ChatHUB >>");
	        	}
	        	
	        	
	        	
	        	if(j==1) {
	        		String h = "hi, I am chatHUB";
	        		toSend = h.getBytes();		
	        		output.writeObject(toSend);
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
