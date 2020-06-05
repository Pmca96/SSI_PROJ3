package client;


import structures.Message;

import java.util.concurrent.BlockingQueue;


public class ChatMessageHandler implements Runnable {


    private BlockingQueue<byte[]> messages;
    private BlockingQueue<String> chatMessages;

    ChatMessageHandler(BlockingQueue<byte[]> messages, BlockingQueue<String> chatMessages) {
        this.messages = messages;
        this.chatMessages = chatMessages;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] msgRcv = messages.take();
                
                Message msg = new Message().deserialize(msgRcv);

                if (msg.getType() == Message.CHAT) {
                    
                    String[] payload = new String(msg.getPayload()).split(":");
                    chatMessages.put(payload[1] + ":" + payload[0] + ":" + payload[2]);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
