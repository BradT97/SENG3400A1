package scp_client;

import java.net.*;
import java.io.*;
import java.util.Date;


public class SCPClient implements SCPClientInterface {
    private Socket connection;
    private PrintWriter out;
    private BufferedReader in;
    private String hostname;
    private int port;

    public SCPClient() { hostname = "localhost";    port = 3400;}

    public SCPClient(String name, int port) { hostname = name;  this.port = port;}

    public boolean connect(String host, int port, String username){
        // Constructs header to send to SCPServer.
        long epoch = (new Date()).getTime();
        String input, header = "SCP CONNECT\nSERVERADDRESS " + host + "\nSERVERPORT " + port + "\nREQUESTCREATED " + epoch + "\nUSERNAME \"" + username + "\"\nSCP END";

        try {
            // Create a connection to hosting SCPServer.
            connection = (host.equals("localhost")) 
			? new Socket(InetAddress.getLocalHost(), port)
            : new Socket(InetAddress.getByName(host), port);
            
            // Establish input and output streams.
            out = new PrintWriter(connection.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            // Send constructed connection header to SCPServer.
            out.println(header);
            
            
            // Read input stream sent from server and send to relevant handler.
            while ((input = in.readLine()) != null){
                //System.out.println(input);
                if (input.equals("SCP REJECT")) {
                    handleReject();
                    return false;
                }
                else if (input.equals("SCP ACCEPT")) {
                    // handle accept
                    if (handleAccept()) return acknowledgeConnection();
                    return false;
                }
                else {
                    System.out.println("Expected: SCP ACCEPT or SCP REJECT; Received: " + input);
                    return false;
                }
            }
            return true;
        } catch (IOException e) { return false; }
    }

    private boolean handleAccept() throws IOException{
        String[] keywords = {"USERNAME","SERVERADDRESS","SERVERPORT"};
        String input;
        int i = 0;

        while ((input = in.readLine()) != null){
            System.out.println(input);
            if (input.equals("SCP END")) break;

            String[] splitInput = input.split(" ");
            if (!splitInput[0].equals(keywords[i])) {
                System.out.println("Expected: " + keywords[i] + "; Received: " + splitInput[i]);
                in.close();
                out.close();
                connection.close();
                return false;
            }
            i++;
        }

        System.out.println("--");
        return true;
    }

    private void handleReject() throws IOException {
        String[] keywords = {"TIMEDIFFERENTIAL","REMOTEADDRESS"};
        String input;
        int i = 0;

        while ((input = in.readLine()) != null){
            System.out.println(input);
            if (input.equals("SCP END")) break;

            String[] splitInput = input.split(" ");
            if (!splitInput[0].equals(keywords[i])) {
                System.out.println("Expected: " + keywords[i] + "; Received: " + splitInput[i]);
                break;
            }
            i++;
        }

        in.close();
        out.close();
        connection.close();
    }

    public boolean acknowledgeConnection()  {
        String username = "<user>", server = "<server ip>";
        int port = -1;
        String outString = "SCP ACKNOWLEDGE\nUSERNAME " + username + "\nSERVERADDRESS " + server + "\nSERVERPORT " + port + "\nSCP END";
        //outString += "\nSCP DISCONNECT\nSCP END";
        out.println(outString);
        return true;
    }

    public String waitMessage() {
        String input, output = "";
        boolean terminate = false, contentFlag = false;
        try {
            while ((input = in.readLine()) != null) {
                //System.out.println(input);
				if (input.equals("SCP DISCONNECT")) 	terminate = true;

				if (contentFlag && input.equals(""))	contentFlag = true;
				else if (contentFlag && !input.equals("SCP END")) {
					output += input;
					
				}
				if (input.equals("MESSAGECONTENT")) 	contentFlag = true;
				if (input.equals("SCP END")) break;
			}

            if (terminate) {
                acknowledgeDisconnect();
                return "DISCONNECT";
            }
            return output;
        } catch (IOException e){
            return "";
        }
    }

    public boolean chat(String message) {
        if (connection.isClosed()) return false;

        String remoteIp = "<remote ip>", remotePort = "<remote port>";
        String outString = "SCP CHAT\nREMOTEADDRESS " + remoteIp + "\nREMOTEPORT " + remotePort + "\nMESSAGECONTENT\n\n" + message + "\nSCP END";

        out.println(outString);
        return true;
    }

    public void disconnect(){
        try {
            out.println("SCP DISCONNECT\nSCP END");
            waitMessage();
            in.close();
            out.close();
            connection.close();
        } catch (IOException e) {

        }
    }

    public void acknowledgeDisconnect(){
        try {
            out.println("SCP ACKNOWLEDGE\nSCP END");
            in.close();
            out.close();
            connection.close();
            System.exit(0);
        } catch (IOException e) {

        }
    }

    public String getHost()
    {
        return hostname;
    }

    public int getPort()
    {
        return port;
    }
}

