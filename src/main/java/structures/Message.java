package structures;

import java.nio.ByteBuffer;

public class Message {


    public static final int LOGIN = 1;
    public static final int LOGIN_OK = 2;
    public static final int LOGIN_ERR = 3;
    public static final int LOGOUT = 4;
    public static final int LOGOUT_OK = 5;

    public static final int SIGNIN = 10;
    public static final int SIGNIN_OK = 20;
    public static final int SIGNIN_ERR = 30;

    public static final int GET_CONTACT_OK = 21;


    public static int CHAT = 30;
    private byte[] payload;
    private int type;

    public static int HEADER_SIZE = (Integer.SIZE / 8) * 2;

    public Message() {

    }

    public Message(int type, byte[] payload) {
        this.type = type;
        this.payload = payload;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {

        this.payload = payload;
    }

    public static int getLOGIN() {
        return LOGIN;
    }

    @Override
    public String toString() {
        return "Message{" +
                " type=" + type +
                " payload size=" + payload.length +
                '}';
    }

    public Message deserialize(byte[] serilizedPacket) {
        ByteBuffer buffer = ByteBuffer.wrap(serilizedPacket);
        int type = buffer.getInt();
        int payloadSize = buffer.getInt();
        byte[] payload = new byte[payloadSize];
        buffer.get(payload);
        Message p = new Message();
        p.setType(type);
        p.setPayload(payload);
        return p;
    }

    public byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE + this.payload.length);
        buffer.putInt(type);
        buffer.putInt(this.payload.length);
        buffer.put(this.payload);
        return buffer.array();
    }
}
