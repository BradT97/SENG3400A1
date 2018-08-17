import java.io.*;
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
			
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String input = in.readLine();
			if (input.equals("SCP CONNECT")) {
				System.out.println(input);
				connect(in);
				out.println("SCP DISCONNECT\nSCP END");
			}
			else if (input.equals("SCP ACKNOWLEDGE")) {

			}
			else if (input.equals("SCP CHAT")) {

			}
			else if (input.equals("SCP DISCONNECT")){

			}
			else {

			}
			

			//out.println(client.toString() + "\nMESSAGE:" + this.message);
			in.close();
			out.close();
			connection.close();
		}
		catch (IOException e) {

		}
		finally {
			try {
				server.close();
			}
			catch(IOException e) {
				System.out.println("Could not terminate the connection");
			}
			
		}
		
		
	}
	
	public void configure(String host, int port, String message){
		this.host = host;
		this.port = port;
		this.message = message;
	}

	public void connect(BufferedReader in) {
		String[] keywords = {"SERVERADDRESS","SERVERPORT","REQUESTCREATED", "USERNAME", "SCP END"};
		try {
			int i = 0;
			String input;
			while((input = in.readLine()) != null && i < keywords.length){
				if (input.equals("SCP END")) break;
				if (!(input.split(" ")[0]).equals(keywords[i]) && !input.equals(keywords[i])) {
					System.out.println("TERMINATING CONNECTION: expected value of " + keywords[i] + " did not match received value " + input + ".");
					// return an indicating value
					return;
				}
				else System.out.println(input);
				i++;
			}
		} 
		catch (IOException e) {}
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

		/* String[] keywords = {"USERNAME","SERVERADDRESS","SERVERPORT", "SCP END"};
		try {
			int i = 0;
			String input;
			while((input = in.readLine()) != null && i < keywords.length){
				if (input.equals("SCP END")) break;
				if (!(input.split(" ")[0]).equals(keywords[i]) && !input.equals(keywords[i])) {
					System.out.println("TERMINATING CONNECTION: expected value of " + keywords[i] + " did not match received value " + input + ".");
					// return an indicating value
					return;
				}
				if (input.equals("USERNAME")) {} 
				else System.out.println(input);
				i++;
			}
		} 
		catch (IOException e) {} */

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