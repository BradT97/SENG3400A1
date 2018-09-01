import scp_client.SCPClient;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient extends Thread {
 
	private static String username, hostname, recieved;
	private static int port;
	private static SCPClient scp;
	private static boolean notBlank = true, isAlive = true;
	static Scanner scanner;

	// Read thread to constantly read the servers input
	static Thread read = new Thread(){
	    public void run(){
	    	// while Thread is still active
			while (isAlive) {

				// waits for server messages
				recieved = scp.waitMessage();
				System.out.println("\nSERVER: " + recieved.replace("\\n", System.lineSeparator() + "SERVER: ") );
				
				// server disconnected from the client so shutdown
				if(recieved.equals("DISCONNECT")){
					scp.acknowledgeDisconnect();
					isAlive = false;
					break;
				}
			}
	    }
	};

	// Read thread to constantly write to the server
	static Thread write = new Thread(){
	    public void run(){

	    	try{
	    	Thread.sleep(200);
	    	String message = "";
			// while Thread is still active
			while (isAlive) {

				// checks for empty string messages
				notBlank = true;
				while(notBlank)
				{
					System.out.print("Message to Server: ");
					message = scanner.nextLine();
					if(message.equals("")){}
					else 	notBlank = false;
				}

				// server disconnected
				if (message.equals("DISCONNECT")) {
					scp.disconnect();
					isAlive = false;
					break;
				}
				// chat with the server
				else scp.chat(message);
			}
			scanner.close();        
	    }catch(InterruptedException e){}
	    }
	};
	
	/** ChatClient()
	  * preconditions:	None.
	  * postconditions:	a new Chat Client is initialised with default hostname and port.
	  */
	public ChatClient(){
		hostname = "localhost";
		port = 3400;
	}

	// main
    public static void main(String[] args) {
		scp = new SCPClient();

		// checks for args on commandline
		if 	(args.length > 2) {
			System.out.println("Too many arguements specified.");
			return;
		}	
		else if (args.length == 2){
			try {
				int port = Integer.parseInt(args[1]);
				if (port < 1023){
					System.out.println("CONFIGURE_SERVER: Invalid port specified.");
					System.exit(0);
				}
				else
					scp = new SCPClient(args[0], Integer.parseInt(args[1]));
			}
			catch 	(NumberFormatException e) 	{
				System.out.println("CONFIGURE_SERVER: Type conversion error - @param port cannot convert to int.");
				System.exit(0);
			}
		}

		String message = "";
		scanner = new Scanner(System.in);
		
		// Get a username for the user.
		System.out.print("Please enter a username: ");
		username = scanner.nextLine();

		// shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println("Successfully shut down the client.");
			}
		}));
		
		// connects to the server with hostname, port and username.
		if (!scp.connect(scp.getHost(), scp.getPort(), username)) System.out.println("Could not connect to server " + hostname + ":" + port);
		else {

			// Start running read/write threads for full duplex
			write.start();
			read.start();
		}
	}
}