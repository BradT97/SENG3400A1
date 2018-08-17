import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient {
 
	private Socket socket;
	private String hostname, username;
	private int port;
	private static SCPClient scp;
	//private Scanner console = new Scanner(System.in);
	
	
	public ChatClient(){
		hostname = "localhost";
		port = 3400;
	}
	
	public ChatClient(String hostname, int port)
	{
		hostname = this.hostname;
		port = this.port;
	}

    public static void main(String[] args) {
		scp = new SCPClient();
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Welcome to SCP Client!\nPlease enter a username: ");
		String username = sc.nextLine();
        scp.connect("localhost", 3400, username);
		sc.close();
	}
}