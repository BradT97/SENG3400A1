import java.net.*;
import java.io.*;

public interface SCPServerInterface {
	public void configure(String host, int port, String message);
	public void start();
	public void connect(BufferedReader in);
	public void reject(Socket client, Client data);
	public void accept(Socket client, Client data);
	public void acknowledge(Socket client, Client data);
	public void chat(Socket client, Client data);
	public void disconnect(Socket client, Client data);
}
