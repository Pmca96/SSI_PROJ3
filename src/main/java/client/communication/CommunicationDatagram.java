package client.communication;

import client.ClientController;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class CommunicationDatagram {

    private DatagramSocket senderUDPSocket, receiverUDPSocket;

    public CommunicationDatagram(int senderPort, int receiverPort) {
        try {
            this.senderUDPSocket = new DatagramSocket(senderPort);
            this.receiverUDPSocket = new DatagramSocket(receiverPort);
            System.out.println("[CLIENT] username=" + ClientController.username + " listening on port=" + receiverPort);
            System.out.println("[CLIENT] username=" + ClientController.username + " sending on port=" + senderPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }


    public void sendMessage(byte[] payload, String ip, int port) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);

            int payloadSize = payload.length;
            DatagramPacket packetSize = new DatagramPacket(ByteBuffer.allocate(4).putInt(payloadSize).array(), 4, inetAddress, port);
            senderUDPSocket.send(packetSize);

            DatagramPacket packet = new DatagramPacket(payload, payload.length, inetAddress, port);
            senderUDPSocket.send(packet);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("[CLIENT] can't send message to " + ip + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public byte[] receiveMessage() {
        try {
            byte[] bufSize = new byte[4];
            DatagramPacket packetSize = new DatagramPacket(bufSize, bufSize.length);
            receiverUDPSocket.receive(packetSize);

            ByteBuffer wrapped = ByteBuffer.wrap(packetSize.getData()); // big-endian by default
            int rcvBufSize = wrapped.getInt();

            byte[] buf = new byte[rcvBufSize];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            receiverUDPSocket.receive(packet);

            return packet.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }
}
