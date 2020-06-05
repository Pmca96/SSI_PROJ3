package server.datalayer;

import java.sql.*;
import java.util.LinkedList;

public class DB {

    private Connection con;

    public DB(String database, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database+ "?serverTimezone=UTC", username, password);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void disconnectDB() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("exception on disconnectDB(): " + e.getMessage());
        }
    }


    public boolean login(String username, String password) {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("select password from user where username like ? ");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String pass = rs.getString("password");
                if (password.equals(pass)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB] " + username + " failed password.");
        }
        return false;
    }


    public boolean createUser(String username, String password) {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("insert into user (username, password) values(?,?)");
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.execute();
            return login(username, password);
        } catch (SQLException e) {
            System.out.println("[DB] " + username + " already exists.");
        }
        return false;

    }

    public void createContact(String username, String ip, int port, int serverPort) {
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement("insert into contact (username, ip, port, server_port) values(?,?,?,?)");
            stmt.setString(1, username);
            stmt.setString(2, ip);
            stmt.setInt(3, port);
            stmt.setInt(4, serverPort);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("[DB] " + username + " contact exists.");
        }
    }

    public LinkedList<String> getContacts() {
        PreparedStatement stmt = null;
        LinkedList<String> result = new LinkedList<>();
        try {
            stmt = con.prepareStatement("select username, ip, port, server_port from contact");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String ip = rs.getString("ip");
                int port = rs.getInt("port");
                int serverPort = rs.getInt("server_port");
                result.add(username + ":" + ip + ":" + port+ ":"+serverPort);

            }
        } catch (SQLException e) {
            System.out.println("[DB] there are no users.");
        }
        return result;
    }
}
