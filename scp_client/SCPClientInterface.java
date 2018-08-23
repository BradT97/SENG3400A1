package scp_client;

import java.net.*;
import java.io.IOException;
import java.io.PrintWriter;

public interface SCPClientInterface {
    /**
     * connect establishes a connection to the relevant SCPServer
     * it waits for a response from the server to confirm successful connection or
     * failure to connect. returns null for failure.
     */
    public boolean connect(String host, int port, String username);
    public boolean acknowledgeConnection();
    public boolean chat(String message);
    public boolean waitMessage();
    public void disconnect();
    public void acknowledgeDisconnect(PrintWriter out);
}