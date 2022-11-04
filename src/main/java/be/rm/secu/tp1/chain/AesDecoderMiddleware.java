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

public class AesDecoderMiddleware extends Middleware<Payload<byte[]>> {
    private final String IV = "FlorentTimothy";

    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        byte[] decrypted;
        SecretKeySpec keySpec = new SecretKeySpec((byte[]) payload.getOption("key"), 0, 16, "AES");
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

            decrypted = cipher.doFinal(payload.getObject());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }

        return next(Payload.of(decrypted));
    }
}
