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
			String recieved;
			while (true) {
				recieved = scp.waitMessage();
				
				System.out.println("SERVER: " + recieved.replace("\\n", System.lineSeparator() + "SERVER: ") );
				
				if (!recieved.equals("")) {
					System.out.print("Message to Server: ");
					message = scanner.nextLine();
					if (message.equals("DISCONNECT")) {
						scp.disconnect();
						break;
					}
					else scp.chat(message);
				}
			}
			scanner.close();
				
			//}
			//catch (IOException e) {
			//	System.out.println("socket is closed");
			//}
			
		}
	}
}