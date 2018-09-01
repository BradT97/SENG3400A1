package scp_server;

import java.io.*;
import java.net.*;
import java.util.Date;

public class SCPServer implements SCPServerInterface {
	private ServerSocket server;
	private Socket connection;
	private Writer fileWriter;
	private String host, message, user = "";
	private int port;
	
	// Default Constructor.
	public SCPServer() {
		host = "localhost";
		port = 3400;
		message = "Welcome to SCP";

		setup();
	}
	
	public SCPServer(String host, int port, String message) 	{ configure(host, port, message); setup();}

	public void setup()
	{
		// Creates logs folder if not already created and opens file server_log for output.
		File log_folder = new File("./logs");
		if (!log_folder.exists()) {
			if (!log_folder.mkdir()) System.out.println("Could not locate log files.");
		}
		try {
			fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("logs/server_log.txt"), "utf-8"));
		} catch (IOException e) {
			System.out.println("Could not create server_log file.");
		}
		// Adds an emergency shutdown procedure to close connections.
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					if(isConnected())	disconnect();
					server.close();
					fileWriter.write("SYSTEM INTERRUPT: Successfully server shutdown.");
					fileWriter.flush();
					fileWriter.close();
					
				} catch (IOException e) {
					System.out.println("Unable to close server socket and filewriter.");
				}
			}
		}));
	}
	
	public void configure(String host, int port, String message){
		this.host = host;
		this.port = port;
		this.message = message;
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
					fileWriter.write(input + "\n");
					fileWriter.flush();
					break;
				}
				if (input.equals("SCP CONNECT")) {
					fileWriter.write("<< Received\n" + input + "\n");
					if (!handleConnect(in, out)){
						System.out.println("Could not establish a connection.");
					}
				}
				else if (input.equals("SCP ACKNOWLEDGE")) {
					fileWriter.write("<< Received\n" + input + "\n");
					while ((input = in.readLine()) != null) {
						fileWriter.write(input + "\n");
						if (input.equals("SCP END")) break;
					}
					fileWriter.write("\n");
					fileWriter.flush();
					chat(message);
					return;
				}
				else if (input.equals("SCP DISCONNECT" )){
					fileWriter.write("<< Received\n" + input + "\n");
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
			try { 
				server.close(); 
			}
			catch(IOException e) { System.out.println("Could not terminate the connection"); }	
		}
	}
	
	public int getPort() {
		return this.port;
	}

	public boolean isConnected(){
		if(connection.isClosed()) return false;
		return true;
	}
	
	public String getUser() {
		return (user.trim()).replace("\"", "");
	}

	public boolean chat (String msg){
		try {
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			String outString = "SCP CHAT\nREMOTEADDRESS <remote>\nREMOTEPORT <port>\nMESSAGECONTENT\n\n" + msg + "\nSCP END";

			out.println(outString);
			fileWriter.write(">> SENT\n" + outString + "\n\n");
			fileWriter.flush();
			
			return true;
		}
		catch(IOException e)	{return false;}
	}
	
	public void disconnect() {
		String outString = "SCP DISCONNECT\nSCP END";
		try {
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			out.println(outString);
			fileWriter.write(">> SENT\n" + outString + "\n\n");
			waitInput();
			out.close();
		}
		catch (IOException e) { /*Client has closed the connection*/} 
		try {
			fileWriter.flush();
			connection.close();
			user = "";
		}
		catch (IOException e) 	{System.out.println("error closing the connection: ");}
	} 

	public void acknowledgeDisconnect() {
		String outString = "SCP ACKNOWLEDGE\nSCP END";
		try {
			PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
			out.println(outString);
			fileWriter.write(">> SENT\n" + outString + "\n\n");
			out.close();
		}
		catch (IOException e) { /*Client has closed the connection*/} 
		try {
			fileWriter.flush();
			connection.close();
			user = "";
		}
		catch (IOException e) 	{System.out.println("error closing the connection: ");}
	}

	public String waitInput() {
		//should wait for scp chat or scp disconnect streams
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String input, output = "";
			boolean terminate = false, contentFlag = false;
			fileWriter.write("<< RECEIVED\n");
			while ((input = in.readLine()) != null) {
				fileWriter.write(input + "\n");
				if (input.equals("SCP DISCONNECT")) 	terminate = true;

				if (contentFlag && input.equals(""))	contentFlag = true;
				else if (contentFlag && !input.equals("SCP END")) {
					output += input;
				}
				if (input.equals("MESSAGECONTENT")) 	contentFlag = true;
				if (input.equals("SCP END")) break;
			} 
			fileWriter.write("\n");

			if (terminate)	return "DISCONNECT";
			return output;
		}
		catch (IOException e) {
			return "DISCONNECT";
		}
	}

	private boolean handleConnect(BufferedReader in, PrintWriter out) throws IOException {
		String[] splitInput, keywords = {"SERVERADDRESS","SERVERPORT","REQUESTCREATED", "USERNAME"};
		String input, created = "";
		int i = 0;

		// Reads the incoming data and ensures headers are correct.
		while((input = in.readLine()) != null && i < keywords.length + 1){
			if (input.equals("SCP END")) {
				fileWriter.write(input + "\n\n");
				fileWriter.flush();
				break;
			}

			splitInput = input.split(" ");
			if (!(splitInput[0]).equals(keywords[i]) && !input.equals(keywords[i])) {
				fileWriter.write("TERMINATING CONNECTION: expected value of " + keywords[i] + " did not match received value " + input + "." + "\n\n");
				fileWriter.flush();
				return false;
			}
			
			if (splitInput[0].equals(keywords[2])) { 	//REQUESTCREATED
				created = String.valueOf(splitInput[1]); // test for empty input or " ".
			}

			if (splitInput[0].equals(keywords[3])) { 	// USERNAME
				fileWriter.write(input + "\n");
				user = String.valueOf(input.replace(splitInput[0], "")); // test for empty input or " ".
			}
			else fileWriter.write(input + "\n");
			i++;
		}
		if (!sendConnectResponse(out, created)) {
			in.close();
			out.close();
			connection.close();
			return false;
		}
		return true;
	}

	private boolean sendConnectResponse(PrintWriter out, String created) throws IOException {
		// Sends relevant SCP ACCEPT or SCP REJECT headers.
		if (!created.equals("") && !user.equals("")) {
			long requestCreated = Long.parseLong(created);
			long timeDiff = (new Date()).getTime() - requestCreated;

			if (timeDiff < 5000) {
				String outString = "SCP ACCEPT\nUSERNAME " + this.user + "\nSERVERADDRESS " + this.host + "\nSERVERPORT " + this.port + "\nSCP END";
				out.println(outString);
				fileWriter.write(">> SENT\n" + outString + "\n\n");
				fileWriter.flush();
				return true;
			}	
			else {
				String outString = "SCP REJECT\nTIMEDIFFERENTIAL " + timeDiff + "\nREMOTEADDRESS " + connection.getRemoteSocketAddress().toString() + "\nSCP END";
				out.println(outString);
				fileWriter.write(">> SENT\n" + outString + "\n\n");
				fileWriter.flush();
				
				return false;
			}
		}
		else return false;
	}
}