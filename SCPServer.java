import java.io.*;
import java.net.*;
import java.util.Date;

public class SCPServer implements SCPServerInterface {
	private String host, message;
	private int port;
	private ServerSocket server;
	private Socket connection;

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
			
			connection = server.accept();
			
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String input; boolean terminate = false;
			while ((input = in.readLine()) != null) {
				if (terminate)	{
					System.out.println(input);
					break;
				}
				if (input.equals("SCP CONNECT")) {
					System.out.println(input);
					String[] connectionData = connect(in);
					
					if (connectionData != null) {
						long requestCreated = Long.parseLong(connectionData[0]);
						long timeDiff = (new Date()).getTime() - requestCreated;

						if (timeDiff < 5000) {
							String outString = "SCP ACCEPT\nUSERNAME " + connectionData[1] + "\nSERVERADDRESS " + host + "\nSERVERPORT " + port + "\nSCP END";
							out.println(outString);
						}	
						else {
							String outString = "SCP REJECT\nTIMEDIFFERENTIAL " + timeDiff + "\nREMOTEADDRESS " + connection.getRemoteSocketAddress().toString() + "\nSCP END";
							out.println(outString);
							in.close();
							out.close();
							connection.close();
						}
					}
					else { /*throw some error*/ }
				}
				else if (input.equals("SCP ACKNOWLEDGE")) {
					System.out.println(input);
					while ((input = in.readLine()) != null) {
						System.out.println(input);
						if (input.equals("SCP END")) break;
					}
					System.out.println("--");
					chat(message);
					return;
				}
				else if (input.equals("SCP DISCONNECT")){
					System.out.println(input);
					terminate = true;
				}
				else {}
			}
			in.close();
			out.close();
			connection.close();
		}
		catch (IOException e) {}
		finally {
			try { server.close(); }
			catch(IOException e) { System.out.println("Could not terminate the connection"); }	
		}
	}
	
	public void configure(String host, int port, String message){
		this.host = host;
		this.port = port;
		this.message = message;
	}

	public String[] connect(BufferedReader in) throws IOException {
		String[] keywords = {"SERVERADDRESS","SERVERPORT","REQUESTCREATED", "USERNAME"};
		String[] output = new String[2];
		
		int i = 0;
		String input;
		while((input = in.readLine()) != null && i < keywords.length + 1){
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

			// REQUESTCREATED
			if (splitInput[0].equals(keywords[2])) {
				output[0] = String.valueOf(splitInput[1]); // test for empty input or " ".
			}

			// USERNAME
			if (splitInput[0].equals(keywords[3])) {
				System.out.println(input);
				output[1] = String.valueOf(input.replace(splitInput[0], "")); // test for empty input or " ".
			}

			else System.out.println(input);
			i++;
		}
		System.out.println("--");
		return output;
	} 

	public boolean isConnected(){
		if(connection.isClosed()) return false;
		return true;
	}


	
	public void acknowledge (){
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
	
	public boolean chat (String msg){
		//SCP CHAT
		//REMOTEADDRESS <remote hostname>
		//REMOTEPORT <remote port>
		//MESSAGECONTENT
		//<line feed> This will cause 2 ‘\n’ characters to be sent!
		//<message contents>
		//SCP END

		try{
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			//BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String outString = "SCP CHAT\nREMOTEADDRESS <remote>\nREMOTEPORT <port>\nMESSAGECONTENT\n\n" + msg + "\nSCP END";
			out.println(outString);
			
			return true;
		}
		catch(IOException e)
		{
			return false;
		}
	}
	
	public void disconnect() {
		//SCP DISCONNECT
		//SCP END
		//System.out.println("trying to dc");
		try {
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			String outString = "SCP DISCONNECT\nSCP END";
			out.println(outString);
			out.close();
			connection.close();
			//System.out.println("connection closed: " + connection.isClosed() + "\nconnection details: " + connection.toString());
		}
		catch (IOException e) {
			System.out.println("error closing the connection");
		}
		
	} 

	public boolean waitMessage() {
		String input;
		boolean terminate = false;
		
        try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((input = in.readLine()) != null){
                System.out.println(input);
                if (input.equals("SCP DISCONNECT")) {
                    terminate = true;
                    System.out.println("terminate true");
                }
                else if (input.equals("SCP CHAT")){
                    //handle chat
                }
                else if (input.equals("SCP END")) break;
            }  
            System.out.println("--");

            if (terminate) {
                disconnect();
                return false;
            }
            return true;
        } catch (IOException e){
            return false;
        }
	}
}