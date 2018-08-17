import java.net.*;
import java.io.*;
 

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
        scp.connect("localhost", 3400, "BradT");
		//if(args.length < 2)	ChatClient client = new ChatClient();
		
		/* else {
			hostname = args[0];
			port = Integer.Intparse(args[1]);
			ChatClient client = new ChatClient(hostname, port);
		}
		
		
		
		// get username from client
		System.out.println("Please enter your username:")
		username = console.next();
 
        try (socket = new Socket(hostname, port)) {
 
			String request = 
				"SCP CONNECT " +
				"SERVERADDRESS " + hostname +
				"SERVERPORT " + Integer.toString(port) +
				"REQUESTCREATED " + System.currentTimeMillis() +
				"USERNAME " + username +
				" SCP END";
 
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
		}
		
		boolean connected = true;
		
		while(connected)
		{
			
			
			
			
			
		} */
		
		
		
	}
}