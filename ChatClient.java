import scp_client.SCPClient;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient {
 
	private static String username, hostname;
	private static int port;
	private static SCPClient scp;
	//private Scanner console = new Scanner(System.in);
	
	
	public ChatClient(){
		hostname = "localhost";
		port = 3400;
	}

    public static void main(String[] args) {
		Scanner scanner;
		String message;

		scp = new SCPClient();
		scanner = new Scanner(System.in);
		
		hostname = "localhost";
		port = 3400;

		// Get a username for the user.
		System.out.print("Please enter a username: ");
		username = scanner.nextLine();
		
		
		if (!scp.connect(hostname, port, username)) System.out.println("Could not connect to server " + hostname + ":" + port);
		else {
			boolean keepAlive = true;
			while (keepAlive) {
				keepAlive = scp.waitMessage();
				
				if (keepAlive) {
					System.out.print("Message to Server: ");
					message = scanner.nextLine();
					if (message.equals("DISCONNECT")) {
						scp.disconnect();
					}
					else keepAlive = scp.chat(message);
				}

				/* if (keepAlive) {
					System.out.print("Message: ");
					message = scanner.nextLine();
					if (!scp.chat(message)) System.out.println("Error sending message at this time.");
				} */
				
			}
			scanner.close();
			//scp.chat(socket, message);
				
			//}
			//catch (IOException e) {
			//	System.out.println("socket is closed");
			//}
			
		}
	}
}