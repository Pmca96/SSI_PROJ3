package server;

import server.datalayer.DB;
import structures.Contact;
import structures.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DB db;
    private ConcurrentHashMap<String, Contact> contacts;
    private BlockingQueue<String> userNotifications;

    ClientHandler(Socket socket, DB db, ConcurrentHashMap<String, Contact> contacts, BlockingQueue<String> userNotifications) {
        this.socket = socket;
        this.db = db;
        this.userNotifications = userNotifications;
        this.contacts = contacts;
    }

    @Override
    public void run() {
        String user = "";
        while (true) {
            try {
                DataInputStream dIn = new DataInputStream(socket.getInputStream());
                DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

                int size = dIn.readInt();
                byte[] message = new byte[size];
                dIn.read(message);

                Message packet = new Message().deserialize(message);
                Message responsePacket = new Message();

                if (packet.getType() == Message.LOGIN) {
                    String login = new String(packet.getPayload());
                    String[] payload = login.split(":");
                    user = payload[0];
                    String pass = payload[1];
                    if (db.login(user, pass)) {
                        responsePacket.setType(Message.LOGIN_OK);
                        Contact userLog = contacts.get(user);
                        userLog.setOnline(true);
                        contacts.put(user, userLog);
                        for (String c : contacts.keySet()) {
                            if (contacts.get(c).isOnline()) {
                                userNotifications.add(c + ":" + contacts.get(c).isOnline() + ":" + contacts.get(c).getIp() + ":" + contacts.get(c).getPort());
                            }
                        }
                    } else {
                        responsePacket.setType(Message.LOGIN_ERR);
                    }
                    responsePacket.setPayload(new byte[]{});
                }
                if (packet.getType() == Message.SIGNIN) {
                    String login = new String(packet.getPayload());
                    String[] payload = login.split(":");
                    user = payload[0];
                    String pass = payload[1];
                    int port = Integer.parseInt(payload[2]);
                    int serverPort=Integer.parseInt(payload[3]);

                    if (db.createUser(user, pass)) {
                        responsePacket.setType(Message.SIGNIN_OK);
                        String ip = socket.getInetAddress().toString().split("/")[1];
                        Contact userContact = new Contact(user, ip, port);
                        userContact.setServerPort(serverPort);
                        System.out.println(userContact);
                        contacts.put(user, userContact);
                        db.createContact(user, ip, port, serverPort);
                    } else {
                        responsePacket.setType(Message.SIGNIN_ERR);
                    }
                    responsePacket.setPayload(new byte[]{});
                }
                if (packet.getType() == Message.LOGOUT) {
                    String username = new String(packet.getPayload());
                    Contact userContact = contacts.get(username);
                    userContact.setOnline(false);
                    responsePacket.setType(Message.LOGOUT_OK);
                    responsePacket.setPayload(new byte[]{});
                    System.out.println("[SEVER] Client " + username + " logout");
                }
                byte[] payload = responsePacket.serialize();
                dOut.writeInt(payload.length);
                dOut.write(payload);
                dOut.flush();
            } catch (IOException e) {
                System.out.println("[SEVER] Client " + socket.getInetAddress() + " disconnected");
                contacts.get(user).setOnline(false);
                userNotifications.add(contacts.get(user).getUsername() + ":" + contacts.get(user).isOnline());
                try {
                    socket.close();
                    break;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
