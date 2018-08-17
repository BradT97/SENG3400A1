import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient {
 
	private static Socket socket;
	private static String username, hostname;
	private static int port;
	private static SCPClient scp;
	//private Scanner console = new Scanner(System.in);
	
	
	public ChatClient(){
		hostname = "localhost";
		port = 3400;
	}

    public static void main(String[] args) {
		scp = new SCPClient();
		Scanner sc = new Scanner(System.in);
		
		hostname = "localhost";
		port = 3400;

		// Get a username for the user.
		System.out.print("Please enter a username: ");
		username = sc.nextLine();

		sc.close();
		socket = scp.connect(hostname, port, username);
		
	}
}