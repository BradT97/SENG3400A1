import java.util.*;
import scp_server.SCPServer;
import java.lang.*;

public class ChatServer extends Thread {
	private static SCPServer scp;
	static Scanner scanner;
	static boolean isAlive = false, notBlank = true, server = true, clientDisconnect = true;

	static Thread read, write;
	
	public static void main(String[] args){
		scp = new SCPServer();

		// get args from command line
		if 	(args.length > 3) {
			System.out.println("Too many arguements specified.");
			return;
		}	
		else if (args.length == 3){
			if 	(!configureServer(args)){
				System.out.println("Invalid arguements specified.");
				return;
			}
			else
			{
				scp = new SCPServer(args[0], Integer.parseInt(args[1]), args[2]);
			}
		}
		
		// Start server listening on port.
		System.out.println("Starting up a new SCP Server!");
		scanner = new Scanner(System.in);

		// Shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				server = false;
				System.out.println("Successfully shut down the server.");
				return;
			}
		}));

		try {
			
			while(server)
			{
				System.out.println("why are you here again");
				System.out.println("Server is waiting for a connection on port " + scp.getPort() + "...");
				// start listening on the server
				scp.start();

				// Once connected enter main loop.
				if (scp.isConnected()) System.out.println("New connection from " + scp.getUser() + "!");
				while(scp.isConnected())
				{
					if(clientDisconnect)
					{
						clientDisconnect = false;
						isAlive = true;

						//Thread is put inside loop to instantiate new thread each new client.
						read = new Thread(){
						    public void run(){

							    String received;
							    while(isAlive){ // while Thread is still active
							        // waits for input from the client
							        received = scp.waitInput();

							        // chat protocol over multiple lines.
									if (!received.equals("DISCONNECT")){
										System.out.println("\n"+scp.getUser() + ": " + received.replace("\\n", System.lineSeparator() + scp.getUser() + ": ") );						
									}

									// user disconnected from the server
									else {
										System.out.println(scp.getUser() + " terminated the connection.");
										isAlive = false;
										scp.acknowledgeDisconnect();
										clientDisconnect = true;
										break;
									}
								}
						    }
						};

						//Thread is put inside loop to instantiate new thread each new client.
						write = new Thread(){
						    public void run(){

								String input = "";
								while(isAlive){ // while Thread is still active
									
									// checks if the user put an empty string
									notBlank = true;
									while(notBlank) 
									{
										try{
											System.out.print("Message to " + scp.getUser() + ": ");
											// message to send the the client
											input = scanner.nextLine(); // BUG when client closes connection, it still sits idle on this line BUG. ////////////////////////////////////
											if(input.equals("")){}
											else 	notBlank = false;
										}catch(NoSuchElementException e){}
									}

									// server disconnects
									if (input.equals("DISCONNECT"))	{
										System.out.println("Disconnecting " + scp.getUser() + " from this session.");
										scp.disconnect();
										isAlive = false;
										server = false;
										break;
									}
									// chats to the client
									else scp.chat(input);
								}
								clientDisconnect = true;
						    }
						};

						// begins the reading and writing threads.
						read.start();
						write.start();
					}
				}
			}
			scanner.close();
			}
		catch (NullPointerException e) {
			// This catches a null pointer exception caused by CTRL + C from CLI.
		}
	}
	
	/** configureServer(String[] config)
	  * preconditions:	None.
	  * postconditions:	Returns a boolean value, indicating whether the port within a certain range or not and sets the SCP Server values. True => valid.
	  */
	private static boolean configureServer(String[] config){	
		try {
			int port = Integer.parseInt(config[1]);
			if (port < 1023){
				System.out.println("CONFIGURE_SERVER: Invalid port specified.");
				return false;
			}

			scp.configure(config[0], port, config[2]);
			return true;
		}
		catch 	(NumberFormatException e) 	{
			System.out.println("CONFIGURE_SERVER: Type conversion error - @param port cannot convert to int.");
			return false;
		}
	}
}