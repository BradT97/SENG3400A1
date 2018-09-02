/*  
    SCPClientInterface.java
    
    *Date: 2/09/2018
    *Descripion:
    *   Interface for a Client class.
 */

package scp_client;

import java.net.*;
import java.io.IOException;
import java.io.PrintWriter;

public interface SCPClientInterface {
    /**
     * connect establishes a connection to the relevant SCPServer
     * it waits for a response from the server to confirm successful connection or
     * failure to connect. returns null for failure.
     * @param host IPv4 address as string.
     * @param port IPv4 port.
     * @param username String which is sent to client name.
     */
    public boolean connect(String host, int port, String username);

    /** acknowledgeConnection()
     * Simply prints the connection acknowledge to the client
     */
    public boolean acknowledgeConnection();
    
    /** chat(String message)
     * Sends a out string to the client side.
     * @param message String which is sent to client on chat protocol.
     */
    public boolean chat(String message);
    
    /** waitMessage()
     * recieves input from the client and decides if to terminate or continue chatting
     */
    public String waitMessage();
    
    /** disconnect()
     * disconnects the client from the server
     */
    public void disconnect();
    
    /** acknowledgeDisconnect()
     * Acknowledges a disconnect from the server and closes
     */
    public void acknowledgeDisconnect();
}