package client;

import structures.Contact;
import structures.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMessageHandler implements Runnable {


    private BlockingQueue<byte[]> messages;
    private ConcurrentHashMap<String, Contact> contacts;

    ServerMessageHandler(BlockingQueue<byte[]> messages, ConcurrentHashMap<String, Contact> contacts) {
        this.messages = messages;
        this.contacts = contacts;

    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] msgRcv = messages.take();
                Message msg = new Message().deserialize(msgRcv);

                System.out.println("[CLIENT] MESSAGE RECEIVED=" + msg);
                if (msg.getType() == Message.GET_CONTACT_OK) {
                    String payload[] = new String(msg.getPayload()).split(":");
                    String user = payload[0];
                    boolean online = Boolean.parseBoolean(payload[1]);
                    String ip = payload[2];
                    int port = Integer.parseInt(payload[3]);
                    Contact userContact = new Contact(user, ip, port, online);
                    contacts.put(user, userContact);
                    System.out.println("[CLIENT] contact to add= " + contacts.get(user));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
