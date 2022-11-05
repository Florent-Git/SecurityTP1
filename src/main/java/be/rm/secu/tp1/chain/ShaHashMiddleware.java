package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ShaHashMiddleware extends Middleware<Payload<byte[]>> {
    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        byte[] digest;
        int payloadLength = payload.getObject().length;

        try {
            var md = MessageDigest.getInstance("SHA1");
            digest = md.digest(payload.getObject());
        } catch (NoSuchAlgorithmException e) {
            error(e);
            return null;
        }

        byte[] returnValue = Arrays.copyOf(payload.getObject(), payloadLength + digest.length);

        for (int i = 0; i < digest.length; i++) {
            returnValue[i + payloadLength] = digest[i];
        }

        return next(payload.copy(returnValue));
    }
}
