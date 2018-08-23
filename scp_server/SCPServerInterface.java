package scp_server;

import java.net.*;
import java.io.*;


public interface SCPServerInterface {
	/**
	 * Sets relative private variables to values of host, port and message.
	 * @param host IPv4 address as string.
	 * @param port IPv4 port.
	 * @param message String which is sent to client on server startup.
	 */
	public void configure(String host, int port, String message);

	/**
	 * Performs the main server cycle over three steps:
	 * 1. 	Creates ServerSocket from configured variables.
	 * 2. 	Listens for client connection.
	 * 3. 	Loops and handles relevant received inputs.
	 */
	public void start();

	/**
	 * Function which indicates if the client is still connected.
	 * @return true if connected; false if connection is closed.
	 */
	public boolean isConnected();

	/**
	 * Function which attempts to send a SCP CHAT message to the client.
	 * @param msg string message sent to the client
	 * @return true if successful, false if error.
	 */
	public boolean chat(String msg);

	/**
	 * Method to close the connection to the client.
	 */
	public void disconnect();
}
