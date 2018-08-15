

public interface SCPServerInterface {
	public void configure(String host, int port, String message);
	public void start();
	public void reject();
	public void accept();
	public void acknowledge();
	public void chat();
	public void disconnect();
}
