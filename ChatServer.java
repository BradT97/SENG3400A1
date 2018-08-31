import java.util.Scanner;
import scp_server.SCPServer;
import java.lang.*;

public class ChatServer extends Thread {
	private static SCPServer scp;
	static Scanner scanner;
	static boolean isAlive = false, notBlank = true, server = true, clientDisconnect = true;

	static Thread read, write;
	
	public static void main(String[] args){
		if 	(args.length > 3) {
			System.out.println("Too many arguements specified.");
			return;
		}	
		else if (args.length == 3){
			if 	(!configureServer(args)){
				System.out.println("Invalid arguements specified.");
				return;
			}
		}
		
		// Start server listening on port.
		System.out.println("Starting up a new SCP Server!");
		scanner = new Scanner(System.in);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				System.out.println("Successfully shut down the server.");
				return;
			}
		}));

		try {
			scp = new SCPServer();
			while(server)
			{
				
				System.out.println("Server is waiting for a connection on port " + scp.getPort() + "...");
				scp.start();
				
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
							    while(isAlive){
							        received = scp.waitInput();  
									if (!received.equals("DISCONNECT")){
										System.out.println("\n"+scp.getUser() + ": " + received.replace("\\n", System.lineSeparator() + scp.getUser() + ": ") );						
									}
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
								while(isAlive){
									
									notBlank = true;
									while(notBlank)
									{
										System.out.print("Message to " + scp.getUser() + ": ");
										//try{
										//}catch(InterruptedException e){}
										input = scanner.nextLine(); // when client closes connection, it still sits idle on this line BUG.
										if(input.equals("")){}
										else 	notBlank = false;
									}


									if (input.equals("DISCONNECT"))	{
										System.out.println("Disconnecting " + scp.getUser() + " from this session.");
										scp.disconnect();
										isAlive = false;
										server = false;
									}
									else scp.chat(input);
								}
								clientDisconnect = true;
						    }
						};

						// begins the threads.
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
	
	private static boolean configureServer(String[] config){
		if (!validateHostname(config[0])){
			System.out.println("CONFIGURE_SERVER: Invalid host address.");
			return false;
		}	
		try {
			int port = Integer.parseInt(config[1]);
			if (!validatePort(port)){
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
	
	/** validateHostname(String hostname)
	  * preconditions:	None.
	  * postconditions:	Returns a boolean value, indicating whether the host name is valid or not. True => valid.
	  */
	private static boolean validateHostname(String hostname){
		return true;
	}
	
	/** validateHostname(int port)
	  * preconditions:	None.
	  * postconditions:	Returns a boolean value, indicating whether the port is valid or not. True => valid.
	  */
	private static boolean validatePort(int port) {
		if(port > 1023)
			return true;
		
		return false;
	}	
}