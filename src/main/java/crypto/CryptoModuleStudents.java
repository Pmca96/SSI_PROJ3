package crypto;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import crypto.KeyFilesManagement;
// BATATA FRITA
public class CryptoModuleStudents {
    private KeyFilesManagement KFM = new KeyFilesManagement();

    public CryptoModuleStudents(String username, String sender) {
    }

    /**
     * Do not change this method
     * 
     * @param src      the sender of the message
     * @param ciphered byte array with the serialized data
     * @param signed   byte array with the signature of the ciphered data
     * @return byte array with all the content
     */
    public byte[] prepareMessage(String src, byte[] ciphered, byte[] signed) {
        byte[] srcArray = src.getBytes();
        ByteBuffer message = ByteBuffer.allocate(srcArray.length + ciphered.length + (4 * 2));
        message.putInt(srcArray.length);
        message.put(srcArray);
        message.putInt(ciphered.length);
        message.put(ciphered);

        return message.array();
    }

    /**
     * This method needs to be modified
     * 
     * @param data to cipher
     * @return ciphered data
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] encrypt(byte[] data, String nome1) {
        System.out.println(nome1);
        try {
            PublicKey key = KFM.loadPublicKey("keys/"+nome1+"_pub.der", "RSA");
            Cipher cipher = Cipher.getInstance("RSA");   
            cipher.init(Cipher.ENCRYPT_MODE, key);  
            
            System.out.println(new String(data));
            System.out.println(new String(cipher.doFinal(data)));
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

//teste

    /**
     *  This method needs to be modified, the following code's purpose is only to make the software work with this
     *  class even without the actual functionality.
     * @param data ciphered data
     * @return deciphered data
     */
    public byte[] decrypt(byte[] data, String nome1) {
        try {
            PrivateKey key = KFM.loadPrivateKey("keys/pedro_priv.der", "RSA");
            Cipher cipher = Cipher.getInstance("RSA");   
            cipher.init(Cipher.DECRYPT_MODE, key);  
            
            ByteBuffer buffer = ByteBuffer.wrap(data);
            int lenSrc = buffer.getInt();
            byte[] src = new byte[lenSrc];
            buffer.get(src, 0, lenSrc);
            int cipheredLen = buffer.getInt();
            byte[] ciphered = new byte[cipheredLen];
            buffer.get(ciphered, 0, cipheredLen);

            System.out.println( cipher.doFinal(ciphered));
            ciphered = cipher.doFinal(ciphered);
            return ciphered;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    /**
     * This method needs to be modified. It signs a message
     * @param data byte array to sign
     * @return the signature (byte array)
     */
    public byte[] sign(byte[] data) {
        return data;
    }
    /**
     * This method needs to be modified. It verifies a signature
     * @param data receives the data that was used to generate the signature
     * @return true if the signature is valid, false otherwise
     */
    public boolean verifySignture(byte[] data) {
        return true;
    }
}
