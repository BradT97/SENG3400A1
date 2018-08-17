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

    public void acknowledgeConnection(Socket connection){

    }

    public void chat(Socket connection){

    }

    public void disconnect(Socket connection){

    }

    public void acknowledgeDisconnect(Socket connection){

    }
}

