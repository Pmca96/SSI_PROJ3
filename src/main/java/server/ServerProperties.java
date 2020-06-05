package server;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ServerProperties {

    public static int SERVER_PORT;
    public static String DB_NAME, DB_USER, DB_PASS;

    private static ServerProperties properties = null;

    ServerProperties(String propertiesPath) {
        InputStream input = null;
        try {
            input = new FileInputStream(propertiesPath);
            Properties prop = new Properties();
            prop.load(input);
            SERVER_PORT = Integer.parseInt(prop.getProperty("server.comm.server_port"));
            DB_NAME = prop.getProperty("server.db.db_name");
            DB_USER = prop.getProperty("server.db.db_user");
            DB_PASS = prop.getProperty("server.db.db_pass");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ServerProperties loadProperties(String propertiesPath) {
        if (properties == null) {
            properties = new ServerProperties(propertiesPath);
        }
        return properties;
    }
}
