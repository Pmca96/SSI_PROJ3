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
                if (crypto.get(sender).verifySignture(receivedMessage)) {
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
        return new String(destinationArray);
    }
}
