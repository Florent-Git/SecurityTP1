package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

public class RSADecoderMiddleware extends Middleware<Payload<byte[]>> {
    private Key privateKey;

    public RSADecoderMiddleware(File keystoreFile, String alias, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keystoreFile), password.toCharArray());
            privateKey = keyStore.getKey(alias, password.toCharArray());
        } catch (Exception e) {
            error(e);
        }
    }

    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        var encryptedMessage = payload.getObject();
        byte[] decryptedMessage;

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            cipher.update(encryptedMessage);
            decryptedMessage = cipher.doFinal();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            error(e);
            return null;
        }

        return next(payload.copy(decryptedMessage));
    }
}
