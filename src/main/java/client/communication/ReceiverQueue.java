package client.communication;

import client.ClientController;
import crypto.CryptoModuleStudents;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ReceiverQueue implements Runnable {

    private BlockingQueue<byte[]> receiveQueue;
    private CommunicationDatagram communicationDatagram;
    private HashMap<String, CryptoModuleStudents> crypto;

    public ReceiverQueue(CommunicationDatagram communicationDatagram, BlockingQueue<byte[]> receiveQueue,
            HashMap<String, CryptoModuleStudents> crypto) {
        this.communicationDatagram = communicationDatagram;
        this.receiveQueue = receiveQueue;
        this.crypto = crypto;
    }

    @Override
    public void run() {
        while (true) {
            byte[] receivedMessage = communicationDatagram.receiveMessage();
            String sender = getDestination(receivedMessage);
            if (!crypto.containsKey(sender)) {
                crypto.put(sender, new CryptoModuleStudents(ClientController.username, sender));
            }
            try {
                System.out.println(receivedMessage.length);
                if (crypto.get(sender).verifySignture(getSignatura(receivedMessage), sender)) {
                    System.out.println(sender);
                    byte[] decipheredPayload = crypto.get(sender).decrypt(receivedMessage, ClientController.username);
                    receiveQueue.add(decipheredPayload);
                }
            } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    private String getDestination(byte[] ciphered) {
        ByteBuffer buffer = ByteBuffer.wrap(ciphered);
        int size = buffer.getInt();
        byte[] destinationArray = new byte[size];
        buffer.get(destinationArray, 0, size);
        System.out.println(new String(destinationArray) + "-"+size);
        return new String(destinationArray);
    }

    private byte[] getSignatura(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        int lenSrc = buffer.getInt();
        byte[] src = new byte[lenSrc];
        buffer.get(src, 0, lenSrc);
            //SRC = NOME
        int cipheredLen = buffer.getInt();
        byte[] ciphered = new byte[cipheredLen];
        buffer.get(ciphered, 0, cipheredLen);
        // CIPHERED = SIGNATURE
        System.out.println("assinatura: " + new String(ciphered));
        return ciphered;
    }
}
