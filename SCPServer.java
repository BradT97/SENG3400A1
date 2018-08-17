import java.io.IOException;
import java.net.*;


public class SCPServer implements SCPServerInterface {
	private String host, message;
	private int port;
	private ServerSocket server;
	
	public SCPServer() {
		host = "localhost";
		port = 3400;
		message = "Welcome to SCP";
	}
	
	public void start(){
		try {
			server = (host.equals("localhost")) 
			? new ServerSocket(port, 1, InetAddress.getLocalHost())
			: new ServerSocket(port, 1, InetAddress.getByName(host));
			System.out.println("server configured");
			Socket connection = server.accept();
			Client client = new Client(connection.getInetAddress().getHostAddress(), connection.getPort());
			System.out.println(client.toString() + "\nMESSAGE:" + this.message);
		}
		catch (IOException e) {

		}
		
		
	}
	
	public void configure(String host, int port, String message){
		this.host = host;
		this.port = port;
		this.message = message;
	}

	public void reject (Socket client, Client data){
		//SCP REJECT
		//TIMEDIFFERENTIAL <time difference>
		//REMOTEADDRESS <requesting clients hostname>
		//SCP END
	}
	
	public void accept (Socket client, Client data){
		//SCP ACCEPT
		//USERNAME <username as String with quotes>
		//CLIENTADDRESS <client hostname>
		//CLIENTPORT <client port>
		//SCP END
	}
	
	public void acknowledge (Socket client, Client data){
		//SCP ACKNOWLEDGE
		//SCP END
	}
	
	public void chat (Socket client, Client data){
		//SCP CHAT
		//REMOTEADDRESS <remote hostname>
		//REMOTEPORT <remote port>
		//MESSAGECONTENT
		//<line feed> ß This will cause 2 ‘\n’ characters to be sent!
		//<message contents>
		//SCP END
	}
	
	public void disconnect(Socket client, Client data) {
		//SCP DISCONNECT
		//SCP END
	} 
}