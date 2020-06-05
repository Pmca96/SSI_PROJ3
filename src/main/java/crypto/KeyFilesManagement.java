package crypto;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
    import java.security.PrivateKey;
    import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyFilesManagement {


    public KeyFilesManagement() {

    }


    public PublicKey loadPublicKey(String publickey_file, String algorithm) { //RSA/DSA

        FileInputStream fis = null;
        try {
            File filePublicKey = new File(publickey_file);
            fis = new FileInputStream(publickey_file);
            DataInputStream dis = new DataInputStream(fis);
            byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
            dis.readFully(encodedPublicKey);
            dis.close();
            fis.close();

            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            X509EncodedKeySpec ks = new X509EncodedKeySpec(encodedPublicKey);

            PublicKey pubKey = (PublicKey) keyFactory.generatePublic(ks);
            return pubKey;
        } catch (Exception ex) {

            ex.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {

            }
        }
        return null;
    }

    public PrivateKey loadPrivateKey(String privatekey_file, String algorithm) {
        FileInputStream fis = null;
        try {
            File filePrivateKey = new File(privatekey_file);
            fis = new FileInputStream(privatekey_file);
            byte[] encodedPublicKey;
            try (DataInputStream dis = new DataInputStream(fis)) {
                encodedPublicKey = new byte[(int) filePrivateKey.length()];
                dis.readFully(encodedPublicKey);
            }
            fis.close();
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(encodedPublicKey);
            PrivateKey privKey = (PrivateKey) keyFactory.generatePrivate(ks);
            return privKey;
        } catch (Exception ex) {

        } finally {
            try {
                fis.close();
            } catch (IOException ex) {

            }
        }
        return null;
    }




}
