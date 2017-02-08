package Management;

import java.io.*;
import java.security.*;
import java.util.Objects;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

public class Security extends Thread {

    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final boolean isServer;
    private DESCrypt desCrypt;

    public Security(ObjectInputStream in, ObjectOutputStream out, boolean isServer) {
        this.isServer = isServer;
        this.in = in;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            RSACrypt rsaCrypt;
            Object inObject;
            if (isServer) {
                while (true) if (!Objects.equals((inObject = in.readObject()), null)) {
                    if (inObject.getClass().getName().endsWith("RSAPublicKeyImpl")) {
                        rsaCrypt = new RSACrypt((PublicKey) inObject);
                        break;
                    }
                }
                Thread.sleep(1000);
                desCrypt = new DESCrypt();
                out.writeObject(rsaCrypt.encrypt(desCrypt.key));
            } else {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(1024, new SecureRandom());
                KeyPair keyPair = keyGen.generateKeyPair();
                rsaCrypt = new RSACrypt(keyPair.getPublic(), keyPair.getPrivate());
                out.writeObject(keyPair.getPublic());
                while (true) if (!Objects.equals((inObject = in.readObject()), null)) {
                    inObject = rsaCrypt.decrypt((byte[]) inObject);
                    if (inObject.getClass().getName().endsWith("SecretKeySpec")) {
                        desCrypt = new DESCrypt((SecretKeySpec) inObject);
                        break;
                    }
                }
            }
        } catch (InternalError | NoSuchAlgorithmException | IOException | ClassNotFoundException | InterruptedException | NoSuchPaddingException e) {
            JOptionPane.showConfirmDialog(new JDialog(), "Error\n" + e.getMessage());
        }
    }

    // Шифровка
    public byte[] write(String text) throws NullPointerException {
        return desCrypt.writeCryptMessage(text);
    }

    // Дешифровка
    public String read(Object object) {
        return desCrypt.readCryptMessage(object);
    }

    // Ассиметричная шифровка для шифровки и передачи симметричного ключа
    private class RSACrypt {
        private final PublicKey publicKey;
        private final PrivateKey privateKey;
        private RSACrypt(PublicKey publicKey, PrivateKey privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        private RSACrypt(PublicKey publicKey) {
            this.publicKey = publicKey;
            this.privateKey = null;
        }

        private byte[] encrypt(SecretKey text) {
            byte[] cipherText = null;
            try {
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                cipherText = cipher.doFinal(text.getEncoded());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cipherText;
        }

        private SecretKey decrypt(byte[] text) {
            byte[] decryptedText = null;
            try {
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                decryptedText = cipher.doFinal(text);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            assert decryptedText != null;
            return new SecretKeySpec(decryptedText, 0, decryptedText.length, "DESede");
        }

    }

    // Основной ключ для шифровки/дешифровки сообщений (симметричное шифрование)
    private class DESCrypt {

        final private SecretKey key;
        final private Cipher cipher = Cipher.getInstance("DESede");

        private DESCrypt(SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException {
            this.key = key;
        }

        private DESCrypt() throws NoSuchAlgorithmException, NoSuchPaddingException {
            key = KeyGenerator.getInstance("DESede").generateKey();
        }

        // Шифровка сообщения
        private byte[] writeCryptMessage(String text) {
            byte[] oos = null;
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                oos = cipher.doFinal(text.getBytes());
            } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            return oos;
        }

        // Чтение зашифрованного сообщения
        private String readCryptMessage(Object object) {
            String message = null;
            try {
                cipher.init(Cipher.DECRYPT_MODE, key);
                message = new String(cipher.doFinal((byte[]) object));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return message;
        }
    }
}
