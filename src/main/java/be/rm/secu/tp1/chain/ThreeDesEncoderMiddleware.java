package be.rm.secu.tp1.chain;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ThreeDesEncoderMiddleware extends Middleware<byte[]> {
    private final byte[] keyBytes;

    public ThreeDesEncoderMiddleware(String key) {
        keyBytes = key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] operate(byte[] object) {
        var secretKeySpec = new SecretKeySpec(keyBytes, "TripleDES");
        Cipher encryptCipher;
        byte[] secretMessage;

        try {
            encryptCipher = Cipher.getInstance("TripleDES/ECB/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            secretMessage = encryptCipher.doFinal(object);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return next(secretMessage);
    }
}
