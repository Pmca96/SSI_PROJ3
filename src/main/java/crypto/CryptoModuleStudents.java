package crypto;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
// ola
import crypto.KeyFilesManagement;

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
        ByteBuffer message = ByteBuffer.allocate(srcArray.length + ciphered.length + signed.length + (4 * 3));

        message.putInt(srcArray.length);
        
        message.put(srcArray);
        message.putInt(signed.length);
        message.put(signed);
        message.putInt(ciphered.length);
        message.put(ciphered);
        // System.out.println(srcArray.length+ ":" + new String(srcArray)+
        //     "\n"+signed.length+ ":" + new String(signed)+
        //     "\n"+ciphered.length+ ":" + new String(ciphered)
        //     );
        System.out.println(srcArray.length+ ":" + new String(srcArray)+
            "\n"+signed.length+ 
            "\n"+ciphered.length
            );
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
    public byte[] encrypt(byte[] data, String dest) {
        try {
            PublicKey key = KFM.loadPublicKey("keys/" + dest + "_pub.der", "RSA");
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            System.out.println(new String(data));
            System.out.println(new String(cipher.doFinal(data)));
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }

    /**
     * This method needs to be modified, the following code's purpose is only to
     * make the software work with this class even without the actual functionality.
     * 
     * @param data ciphered data
     * @return deciphered data
     */
    public byte[] decrypt(byte[] data, String dest) {
        try {
            PrivateKey key = KFM.loadPrivateKey("keys/" + dest + "_priv.der", "RSA");
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            ByteBuffer buffer = ByteBuffer.wrap(data);
            //Nome
            int lenSrc = buffer.getInt();
            byte[] src = new byte[lenSrc];
            buffer.get(src, 0, lenSrc);
            //Signature
            int signlen = buffer.getInt();
            byte[] sign = new byte[signlen];
            buffer.get(sign, 0, signlen);
            //Message
            int cipheredLen = buffer.getInt();
            byte[] ciphered = new byte[cipheredLen];
            buffer.get(ciphered, 0, cipheredLen);

            System.out.println(cipher.doFinal(ciphered));
            ciphered = cipher.doFinal(ciphered);
            return ciphered;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * This method needs to be modified. It signs a message
     * 
     * @param data byte array to sign
     * @return the signature (byte array)
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public byte[] sign(byte[] data, String src)
            throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance("SHA1withRSA");
        SecureRandom secureRandom = new SecureRandom();
        PrivateKey key = KFM.loadPrivateKey("keys/"+src+"_priv.der", "RSA");
      
        signature.initSign(key, secureRandom);
        signature.update(data);

        byte[] digitalSignature = signature.sign();
        return digitalSignature;
    }
    /**
     * This method needs to be modified. It verifies a signature
     * @param data receives the data that was used to generate the signature
     * @return true if the signature is valid, false otherwise
     */
    public boolean verifySignture(byte[] data, String src)
        throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
      
        Signature signature = Signature.getInstance("SHA1withRSA");

        PublicKey key = KFM.loadPublicKey("keys/"+src+"_pub.der", "RSA");
        signature.initVerify(key);

        boolean verified =  signature.verify(data);
        /*int i = 0;
        while (verified == false && i < data.length){
            verified = signature.verify(Arrays.copyOfRange(data, i , i+256));
            i++;
        }*/
        System.out.println("boolean: "+ verified);
        return true;
    }
}
