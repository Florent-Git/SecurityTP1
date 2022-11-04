package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;

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
    private SecretKeySpec secretKeySpec;
    private String initVector = "FlorentTimothy";

    public AESEncoderMiddleware(Observable<byte[]> keyBytes) {
        keyBytes.subscribe(bytes -> {
            secretKeySpec = new SecretKeySpec(bytes,0, 16, "AES");
        });
    }

    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        Cipher encryptCipher;
        byte[] secretMessage;

        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
            secretMessage = encryptCipher.doFinal(payload.object());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

        return next(Payload.of(secretMessage, payload.source()));
    }
}
