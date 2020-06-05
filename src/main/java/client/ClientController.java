package client;

import client.communication.*;
import crypto.CryptoModuleStudents;
import structures.Contact;
import structures.Message;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ClientController {

    private ServerSynchronousCommunication serverSyncComm;
    private ServerAssynchronousCommunication serverAssyncComm;

    private CommunicationDatagram clientCommunicationDatagram;
    private ReceiverQueue receiverAssync;
    private SenderQueue senderAssync;
    private ChatMessageHandler chatMessageHandler;
    private ServerMessageHandler serverMessageHandler;

    private ConcurrentHashMap<String, Contact> userContacts;
    private BlockingQueue<byte[]> serverReceiveQueue;
    private BlockingQueue<byte[]> receiveQueue;
    private BlockingQueue<Message> senderQueue;
    private BlockingQueue<String> chatMessages;

    private ClientProperties props;
    public static String username;
    private HashMap<String, CryptoModuleStudents> crypto;

    public ClientController(String path) {
        this.props = ClientProperties.loadProperties(path);
        this.userContacts = new ConcurrentHashMap<>();
        this.serverReceiveQueue = new ArrayBlockingQueue<>(100);
        this.receiveQueue = new ArrayBlockingQueue<>(100);
        this.senderQueue = new ArrayBlockingQueue<>(100);
        this.chatMessages = new ArrayBlockingQueue<>(100);
        this.serverSyncComm = new ServerSynchronousCommunication(props.SERVER_IP, props.SERVER_PORT); //Server
        this.crypto = new HashMap<String, CryptoModuleStudents>();
    }


    public boolean login(String username, String password) {
        this.username = username;
        Message loginMessage = new Message();
        loginMessage.setType(Message.LOGIN);

        String payload = username + ":" + password;
        loginMessage.setPayload(payload.getBytes());
        Message response = serverSyncComm.send(loginMessage);

        this.clientCommunicationDatagram = new CommunicationDatagram(props.CLIENT_PORT_SND, props.CLIENT_PORT_RCV);
        this.serverAssyncComm = new ServerAssynchronousCommunication(props.SERVER_CLIENT_PORT, serverReceiveQueue); //Server

        this.receiverAssync = new ReceiverQueue(clientCommunicationDatagram, receiveQueue, crypto); //Server and clients
        this.senderAssync = new SenderQueue(clientCommunicationDatagram, senderQueue, userContacts, crypto);

        this.chatMessageHandler = new ChatMessageHandler(receiveQueue, chatMessages);
        this.serverMessageHandler = new ServerMessageHandler(serverReceiveQueue, userContacts);

        new Thread(chatMessageHandler).start();
        new Thread(serverMessageHandler).start();
        new Thread(serverAssyncComm).start();
        new Thread(receiverAssync).start();
        new Thread(senderAssync).start();

        return response.getType() == Message.LOGIN_OK;
    }

    public boolean signin(String username, String password) {
        Message signinMessage = new Message();

        signinMessage.setType(Message.SIGNIN);
        String payload = username + ":" + password + ":" + props.CLIENT_PORT_RCV + ":" + props.SERVER_CLIENT_PORT;
        signinMessage.setPayload(payload.getBytes());
        Message response = serverSyncComm.send(signinMessage);
        return response.getType() == Message.SIGNIN_OK;
    }

    public boolean logout() {
        Message logoutMessage = new Message();
        logoutMessage.setType(Message.LOGOUT);
        String payload = username;
        logoutMessage.setPayload(payload.getBytes());
        Message response = serverSyncComm.send(logoutMessage);
        return response.getType() == Message.LOGOUT_OK;
    }

    public ConcurrentHashMap<String, Contact> updateContactList() {
        return userContacts;
    }

    public String receiveMessage() {
        try {
            return chatMessages.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean sendMessage(String message, String dstUsername, String srcUsername) {
        Message chatMessage = new Message();
        chatMessage.setType(Message.CHAT);
        String payload = dstUsername + ":" + srcUsername + ":" + message;
        chatMessage.setPayload(payload.getBytes());
        return senderQueue.add(chatMessage);
    }


}
