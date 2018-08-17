import java.io.*;
import java.net.*;
import java.util.Date;

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
			
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String input = in.readLine();
			if (input.equals("SCP CONNECT")) {
				System.out.println(input);
				String[] connectionData = connect(in);
				if (connectionData != null)
				{
					long requestCreated = Long.parseLong(connectionData[0]);
					long timeDiff = (new Date()).getTime() - requestCreated;
					
					if (timeDiff < 5000) accept(connection, null);	
					else 				 reject(connection, null);
					System.out.println("time diff = " + timeDiff + " ms");
				}
				else {
					//throw some error
				}
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
			
			in.close();
			out.close();
			connection.close();
		}
		catch (IOException e) {}
		finally {
			try {server.close();}
			catch(IOException e) {System.out.println("Could not terminate the connection");}	
		}
	}
	
	public void configure(String host, int port, String message){
		this.host = host;
		this.port = port;
		this.message = message;
	}

	public String[] connect(BufferedReader in) throws IOException {
		String[] keywords = {"SERVERADDRESS","SERVERPORT","REQUESTCREATED", "USERNAME", "SCP END"};
		String[] output = new String[2];
		int i = 0;
		String input;
		while((input = in.readLine()) != null && i < keywords.length){
			if (input.equals("SCP END")) {
				System.out.println(input);
				break;
			}
			String[] splitInput = input.split(" ");
			if (!(splitInput[0]).equals(keywords[i]) && !input.equals(keywords[i])) {
				System.out.println("TERMINATING CONNECTION: expected value of " + keywords[i] + " did not match received value " + input + ".");
				// return an indicating value
				return null;
			}
			if (splitInput[0].equals(keywords[2])) {
				output[0] = String.valueOf(splitInput[1]); // test for empty input or " ".
			}
			if (splitInput[0].equals(keywords[3])) {
				System.out.println(input);
				output[1] = String.valueOf(splitInput[1]); // test for empty input or " ".
			}
			else System.out.println(input);
			i++;
		}
		return output;
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