package client.communication;

import structures.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerSynchronousCommunication {
    private int port;
    private String ip;
    private Socket clientSocket;

    private DataOutputStream outToServer;
    private DataInputStream inFromServer;



    public ServerSynchronousCommunication(String ip, int port) {
        this.port = port;
        this.ip = ip;
        try {
            this.clientSocket = new Socket(ip, port);
            System.out.println("[CLIENT] Connected to server@" + ip + ":" + port);
            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            this.inFromServer = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public Message send(Message message) {
        try {
            byte[] messageBytes = message.serialize();
            outToServer.writeInt(messageBytes.length);
            outToServer.write(messageBytes);
            outToServer.flush();
            int size = inFromServer.readInt();
            byte[] messageFromServer = new byte[size];
            inFromServer.read(messageFromServer);
            Message fromServer = new Message().deserialize(messageFromServer);
            return fromServer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Message();
    }
}
