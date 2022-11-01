package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class ThreeDesDecoderMiddleware extends Middleware<Payload<byte[]>> {
    private final byte[] keyBytes;

    public ThreeDesDecoderMiddleware(String key) {
        keyBytes = key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        var secretKeySpec = new SecretKeySpec(keyBytes, "TripleDES");
        Cipher decryptCipher;
        byte[] secretMessage;

        try {
            decryptCipher = Cipher.getInstance("TripleDES/ECB/PKCS5Padding");
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            secretMessage = decryptCipher.doFinal(payload.object());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        return next(Payload.of(secretMessage, payload.source()));
    }
}
