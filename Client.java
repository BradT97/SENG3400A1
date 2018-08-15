import sun.security.x509.IPAddressName;

public class Client {
    private String ipAddress, username;
    private int port;
    
    public Client() {
        ipv4 = "";
        username = "";
        port = 0;
    }

    public Client(String ipAddress, int port){
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress = ipAddress;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPort(int port){
        this.port = port;
    }

    public String getIpAddress(){
        return this.ipAddress;
    }

    public String getUsername(){
        return this.username;
    }

    public int getPort(){
        return this.port;
    }

    public String toString(){
        return this.ipAddress + ":" + this.port + "\nUSER: " + this.username;
    }
}