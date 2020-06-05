package client.communication;

import client.ClientController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class ServerAssynchronousCommunication implements Runnable {

    private DatagramSocket receiverUDPSocket;
    private BlockingQueue<byte[]> receiveQueue;

    private byte[] buf;

    public ServerAssynchronousCommunication(int port, BlockingQueue<byte[]> receiveQueue) {
        buf = new byte[1024];
        this.receiveQueue = receiveQueue;
        try {
            this.receiverUDPSocket = new DatagramSocket(port);
            System.out.println("[CLIENT] username=" + ClientController.username + " server listening on port=" + port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {

            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                receiverUDPSocket.receive(packet);
                receiveQueue.add(packet.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
