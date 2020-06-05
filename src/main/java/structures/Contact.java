package structures;

public class Contact {

    private String ip;
    private int port;
    private int serverPort;
    private String username;
    private boolean online;

    public Contact() {

    }

    public Contact(String username, String ip, int port) {
        this.ip = ip;
        this.username = username;
        this.port = port;
        this.online = false;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public Contact(String username, String ip, int port, boolean online) {
        this.ip = ip;
        this.username = username;
        this.port = port;
        this.online = online;
    }

    @Override
    public String toString() {
        return "Contact {" +
                "ip='" + ip + '\'' +
                "port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", online=" + online +
                '}';
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
