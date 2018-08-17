import java.net.*;

public interface SCPClientInterface {
    public Socket connect(String host, int port, String username);
    public void acknowledgeConnection(Socket connection);
    public void chat(Socket connection);
    public void disconnect(Socket connection);
    public void acknowledgeDisconnect(Socket connection);
}