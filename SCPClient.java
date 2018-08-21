import java.net.*;
import java.io.*;
import java.util.Date;


public class SCPClient implements SCPClientInterface {

    public Socket connect(String host, int port, String username){
        // Constructs header to send to SCPServer.
        long epoch = (new Date()).getTime();
        String header = "SCP CONNECT\nSERVERADDRESS " + host + "\nSERVERPORT " + port + "\nREQUESTCREATED " + epoch + "\nUSERNAME \"" + username + "\"\nSCP END";

        try {
            // Create a connection to hosting SCPServer.
            Socket connection = (host.equals("localhost")) 
			? new Socket(InetAddress.getLocalHost(), port)
            : new Socket(InetAddress.getByName(host), port);
            
            // Establish input and output streams.
            PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            
            // Send constructed connection header to SCPServer.
            out.println(header);
            
            String input;
            boolean terminate = false;

            // Read input stream sent from server and send to relevant handler.
            while ((input = in.readLine()) != null){
                System.out.println(input);
                if (input.equals("SCP REJECT")) {
                    //handle reject
                    System.out.println("handle reject");
                    handleReject(in, out);
                    connection.close();
                    return null;
                }
                else if (input.equals("SCP ACCEPT")) {
                    // handle accept
                    if (!handleAccept(in, out)) {
                        connection.close();
                        return null;
                    }
                    else acknowledgeConnection(out);
                }
                //else
            }
            
            /* in.close();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream())); */
            terminate = true;
            while((input = in.readLine()) != null){
                System.out.println(input);
                if (terminate && input.equals("SCP END")) break;
                if (terminate && !input.equals("SCP END")){
                    System.out.println("INVALID SCP HEADER: SCP DISCONNECT not followed by SCP END");
                    in.close();
                    out.close();
                    connection.close();
                    return null;
                }
				if (input.equals("SCP DISCONNECT")) {
                    terminate = true;
                }
                
			}

            return connection;
        } catch (IOException e) {
            return null;
        }
    }

    private boolean handleAccept(BufferedReader in, PrintWriter out) throws IOException{
        String[] keywords = {"USERNAME","SERVERADDRESS","SERVERPORT"};
        String input;
        int i = 0;

        while ((input = in.readLine()) != null){
            System.out.println(input);
            if (input.equals("SCP END")) break;

            String[] splitInput = input.split(" ");
            if (!splitInput[0].equals(keywords[i])) {
                in.close();
                out.close();
                return false;
            }
            // I guess we could run some checks to see if the server matches etc.
            i++;
        }
        return true;
    }

    private void handleReject(BufferedReader in, PrintWriter out) throws IOException {
        String[] keywords = {"TIMEDIFFERENTIAL","REMOTEADDRESS"};
        String input;
        int i = 0;

        while ((input = in.readLine()) != null){
            System.out.println(input);
            if (input.equals("SCP END")) break;

            String[] splitInput = input.split(" ");
            if (!splitInput[0].equals(keywords[i])) {
                in.close();
                out.close();
                break;
            }
            // I guess we could run some checks to see if the server matches etc.
            i++;
        }
    }

    public void acknowledgeConnection(PrintWriter out)  {
        String username = "<user>", server = "<server ip>";
        int port = -1;
        String outString = "SCP ACKNOWLEDGE\nUSERNAME " + username + "\nSERVERADDRESS " + server + "\nSERVERPORT " + port + "\nSCP END";
        
        out.println(outString);
    }

    public void chat(Socket connection, String message) throws IOException {
        PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
        String remoteIp = "<remote ip>", remotePort = "<remote port>";
        String outString = "SCP CHAT\nREMOTEADDRESS " + remoteIp + "\nREMOTEPORT " + remotePort + "\nMESSAGECONTENT\n\n" + message + "\nSCP END";

        //DEBUG
        outString += "\nSCP DISCONNECT\nSCP END";

        out.println(outString);
    }

    public void disconnect(Socket connection){

    }

    public void acknowledgeDisconnect(PrintWriter out){

    }
}

