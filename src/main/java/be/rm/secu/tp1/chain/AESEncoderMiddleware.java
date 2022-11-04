package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESEncoderMiddleware extends Middleware<Payload<byte[]>>{
    private String initVector = "FlorentTimothy22";

    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        SecretKeySpec secretKeySpec = new SecretKeySpec((byte[]) payload.getOption("key"),0, 16,"AES");
        Cipher encryptCipher;
        byte[] secretMessage;

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            secretMessage = encryptCipher.doFinal(payload.getObject());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        return next(payload.copy(secretMessage));
    }
}
