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

	public void reject (){
		//SCP REJECT
		//TIMEDIFFERENTIAL <time difference>
		//REMOTEADDRESS <requesting clients hostname>
		//SCP END
	}
	
	public void accept (){
		//SCP ACCEPT
		//USERNAME <username as String with quotes>
		//CLIENTADDRESS <client hostname>
		//CLIENTPORT <client port>
		//SCP END
	}
	
	public void acknowledge (){
		//SCP ACKNOWLEDGE
		//SCP END
	}
	
	public void chat (){
		//SCP CHAT
		//REMOTEADDRESS <remote hostname>
		//REMOTEPORT <remote port>
		//MESSAGECONTENT
		//<line feed> ß This will cause 2 ‘\n’ characters to be sent!
		//<message contents>
		//SCP END
	}
	
	public void disconnect() {
		//SCP DISCONNECT
		//SCP END
	} 
}