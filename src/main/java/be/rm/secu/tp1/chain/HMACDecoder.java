package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HMACDecoder extends Middleware<Payload<byte[]>> {
    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        String finalMessage;
        byte[] messageHmac = payload.getObject();

        byte[] hmac = Arrays.copyOfRange(messageHmac, messageHmac.length - 16, messageHmac.length);
        byte[] message = Arrays.copyOfRange(messageHmac, 0, messageHmac.length - 16);

        byte[] secretKey = (byte[]) payload.getOption("key");

        var keySpec = new SecretKeySpec(secretKey, 0, 16, "HmacMD5");
        try {
            var mac = Mac.getInstance("HmacMD5");
            mac.init(keySpec);

            var localHmac = mac.doFinal(message);

            if (Arrays.equals(localHmac, hmac)) {
                finalMessage = "Le message est OK: " + new String(message, StandardCharsets.UTF_8);
            } else {
                finalMessage = "Le message est NOK: " + new String(message, StandardCharsets.UTF_8);
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return error(e);
        }

        return next(payload.copy(finalMessage.getBytes(StandardCharsets.UTF_8)));
    }
}
