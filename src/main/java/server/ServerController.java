package server;

import server.datalayer.DB;
import structures.Contact;
import structures.Message;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ServerController {

    private ServerSocket welcomeSocket;
    private DatagramSocket socket;
    private DB db;
    private ConcurrentHashMap<String, Contact> contacts;
    private BlockingQueue<String> userNotifications;
    private ServerProperties props;

    public ServerController(String path) {
        this.props = ServerProperties.loadProperties(path);
        this.db = new DB(props.DB_NAME, props.DB_USER, props.DB_PASS);
        this.userNotifications = new ArrayBlockingQueue<>(100);
        this.contacts = new ConcurrentHashMap<>();
        LinkedList<String> list = db.getContacts();
        System.out.println("[SERVER] Loading contacts .....");
        for (String elem : list) {
            String[] contact = elem.split(":");
            Contact c = new Contact(contact[0], contact[1], Integer.parseInt(contact[2]));
            c.setServerPort(Integer.parseInt(contact[3]));
            contacts.put(contact[0], c);
            System.out.println("Contact=" + c);
        }
        try {
            this.welcomeSocket = new ServerSocket(props.SERVER_PORT);
            this.socket = new DatagramSocket(4446);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        new Thread(new StatusNotifier()).start();
        while (true) {
            try {
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Client connected " + connectionSocket.getInetAddress());
                new Thread(new ClientHandler(connectionSocket, db, contacts, userNotifications)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class StatusNotifier implements Runnable {
        @Override
        public void run() {
            Message message = new Message();
            message.setType(Message.GET_CONTACT_OK);
            while (true) {
                try {
                    String notification = userNotifications.take();
                    Thread.sleep(1000);
                    for (String contact : contacts.keySet()) {
                        Contact cont = contacts.get(contact);
                        if (cont.isOnline() && !cont.getUsername().equals(notification.split(":")[0])) {
                            InetAddress inetAddress = InetAddress.getByName(cont.getIp());
                            message.setPayload(notification.getBytes());
                            byte[] serializedMessage = message.serialize();
                            System.out.println("[SERVER] Sending notification to " + cont.getUsername() + ":" + cont.getServerPort() + "  about: " + notification);
                            DatagramPacket packet = new DatagramPacket(serializedMessage, serializedMessage.length, inetAddress, cont.getServerPort());
                            socket.send(packet);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
