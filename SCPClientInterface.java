import java.net.*;

public interface SCPClientInterface {
    public void connect();
    public void acknowledgeConnection();
    public void chat();
    public void disconnect();
    public void acknowledgeDisconnect();
}