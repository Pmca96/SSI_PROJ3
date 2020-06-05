package client;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ClientProperties {

    public static int SERVER_PORT,SERVER_CLIENT_PORT;
    public static int CLIENT_PORT_RCV;
    public static int CLIENT_PORT_SND;
    public static String SERVER_IP;
    private static ClientProperties properties = null;

    ClientProperties(String propertiesPath) {
        InputStream input = null;
        try {
            input = new FileInputStream(propertiesPath);
            Properties prop = new Properties();
            prop.load(input);

            SERVER_IP = prop.getProperty("client.comm.server_ip");
            SERVER_PORT = Integer.parseInt(prop.getProperty("client.comm.server_port"));
            SERVER_CLIENT_PORT = Integer.parseInt(prop.getProperty("client.comm.client_server_port_rcv"));
            CLIENT_PORT_RCV = Integer.parseInt(prop.getProperty("client.comm.client_client_port_rcv"));
            CLIENT_PORT_SND = Integer.parseInt(prop.getProperty("client.comm.client_client_port_snd"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ClientProperties loadProperties(String propertiesPath) {
        if (properties == null) {
            properties = new ClientProperties(propertiesPath);
        }
        return properties;
    }
}
