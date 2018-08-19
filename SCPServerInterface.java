import java.net.*;
import java.io.*;


public interface SCPServerInterface {
	public void configure(String host, int port, String message);
	public void start();

	/**
	 * returns a string array where
	 * 0 := request created value
	 * 1 := username
	 */
	public String[] connect(BufferedReader in) throws IOException;
	public void acknowledge(Socket client, Client data);
	public void chat(Socket client, Client data);
	public void disconnect(Socket client, Client data);
}
