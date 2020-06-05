package client.communication;

import client.ClientController;
import crypto.CryptoModuleStudents;
import structures.Contact;
import structures.Message;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class SenderQueue implements Runnable {

    private CommunicationDatagram communicationDatagram;
    private BlockingQueue<Message> senderQueue;
    private ConcurrentHashMap<String, Contact> userContact;
    private HashMap<String, CryptoModuleStudents> crypto;

    public SenderQueue(CommunicationDatagram communicationDatagram, BlockingQueue<Message> senderQueue, ConcurrentHashMap<String, Contact> userContact, HashMap<String, CryptoModuleStudents> crypto) {
        this.userContact = userContact;
        this.communicationDatagram = communicationDatagram;
        this.senderQueue = senderQueue;
        this.crypto = crypto;
    }

    @Override
    public void run() {

        try {
            while (true) {
                Message msg = senderQueue.take();
                System.out.println(msg);
                byte[] serializedMessage = msg.serialize();
                String[] contacts = new String(msg.getPayload()).split(":");
                String dst = contacts[0];
                String src = contacts[1];
                Contact contact = userContact.get(dst);
                if(!crypto.containsKey(dst)){
                    crypto.put(dst, new CryptoModuleStudents(ClientController.username, dst));
                }
                byte[] ciphered = crypto.get(dst).encrypt(serializedMessage, dst);
                byte[] signed = crypto.get(dst).sign(ciphered);
                byte[] messageToSend = crypto.get(dst).prepareMessage(src, ciphered, signed);
                communicationDatagram.sendMessage(messageToSend, contact.getIp(), contact.getPort());

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
