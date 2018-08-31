import scp_client.SCPClient;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient extends Thread {
 
	private static String username, hostname;
	private static int port;
	private static SCPClient scp;
	private static boolean notBlank = true, isAlive = true;
	static Scanner scanner;

	static Thread read = new Thread(){
	    public void run(){
	        String recieved;
			while (isAlive) {
				recieved = scp.waitMessage();

				System.out.println("\nSERVER: " + recieved.replace("\\n", System.lineSeparator() + "SERVER: ") );
				
				if(recieved.equals("DISCONNECT")){
					scp.acknowledgeDisconnect();
					isAlive = false;
					break;
				}
			}
	    }
	};

	static Thread write = new Thread(){
	    public void run(){
	    	try{
	    	Thread.sleep(200);
	    	String message = "";
	        String recieved;
			while (isAlive) {
				notBlank = true;
				while(notBlank)
				{
					System.out.print("Message to Server: ");
					message = scanner.nextLine(); // when server closes connection, it still sits idle on this line BUG.
					if(message.equals("")){}
					else 	notBlank = false;
				}


				if (message.equals("DISCONNECT")) {
					scp.disconnect();
					isAlive = false;
					break;
				}
				else scp.chat(message);
			}
			scanner.close();        
	    }catch(InterruptedException e){}
	    }
	};
	
	
	public ChatClient(){
		hostname = "localhost";
		port = 3400;
	}

    public static void main(String[] args) {
		String message = "";
		hostname = "localhost";
		port = 3400;
		scanner = new Scanner(System.in);

		scp = new SCPClient();
		
		// Get a username for the user.
		System.out.print("Please enter a username: ");
		username = scanner.nextLine();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println("Successfully shut down the client.");
			}
		}));
		
		
		if (!scp.connect(hostname, port, username)) System.out.println("Could not connect to server " + hostname + ":" + port);
		else {

			// Start running read/write threads for full duplex
			write.start();
			read.start();
		}
	}
}