package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class HMACMD5EncoderMiddleware extends Middleware<Payload<byte[]>> {
    @Override
    public Payload<byte[]> operate(Payload<byte[]> object) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec((byte[]) object.getOption("key"),0, 16,"HmacMD5");
            Mac hmac = Mac.getInstance("HMAC-MD5");
            hmac.init(secretKeySpec);
            hmac.update(object.getObject());
            byte[] hash = hmac.doFinal();

            //Envoi du message + hash
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(object.getObject());
            out.write(hash);

            return next(object.copy(out.toByteArray()));
        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

