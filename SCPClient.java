import java.net.*;
import java.io.*;

public class SCPClient implements SCPClientInterface {

    public Socket connect(String host, int port, String username){
        // Constructs header to send to SCPServer.
        int epoch = 10000;
        String header = "SCP CONNECT\nSERVERADDRESS " + host + "\nSERVERPORT " + port + "\nREQUESTCREATED " + epoch + "\nUSERNAME \"" + username + "\"\nSCP END";
        System.out.println(header);

        try {
            Socket connection = (host.equals("localhost")) 
			? new Socket(InetAddress.getLocalHost(), port)
            : new Socket(InetAddress.getByName(host), port);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;
            while((input = in.readLine()) != null) {
                System.out.println("Server: " + input);
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

