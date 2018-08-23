import java.util.Scanner;
import scp_server.SCPServer;

public class ChatServer {
	private static SCPServer scp;
	
	public static void main(String[] args){
		Scanner scanner; 
		String input;

		scp = new SCPServer();
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
		
		//start server listening on port 
		System.out.println("Starting up a new SCP Server!");
		scp.start();

		scanner = new Scanner(System.in);
		if (scp.isConnected()) System.out.println("Successfully established connection to " + scp.getUser() + ".");
		while(scp.isConnected())
		{
			scp.waitInput();
			System.out.print("Message to client: ");
			input = scanner.nextLine();
			if (input.equals("DISCONNECT"))	{
				System.out.println("Disconnecting " + scp.getUser() + " from this session.");
				scp.disconnect();
			}
			else scp.chat(input);
			//scp.waitMessage();
			
		}

		scanner.close();
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
		return true;
	}
	
	
	
	
}