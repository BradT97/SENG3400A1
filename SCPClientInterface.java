import java.net.*;
import java.io.PrintWriter;

public interface SCPClientInterface {
    /**
     * connect establishes a connection to the relevant SCPServer
     * it waits for a response from the server to confirm successful connection or
     * failure to connect. returns null for failure.
     */
    public Socket connect(String host, int port, String username);
    public void acknowledgeConnection(PrintWriter out);
    public void chat(Socket connection);
    public void disconnect(Socket connection);
    public void acknowledgeDisconnect(PrintWriter out);
}