package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

public class SHAServerMiddleware extends Middleware<Payload<byte[]>> {
    @Override
    public Payload<byte[]> operate(Payload<byte[]> object) {
        //Dans object : message + MessageDigest
        //On veut retourner l'affirmation du bon message et bon digest
        var longueurPayload = object.getObject().length;
        var messageDigestPayload = Arrays.copyOfRange(object.getObject(), longueurPayload - 20, longueurPayload - 1);
        var message = Arrays.copyOfRange(object.getObject(), 0, longueurPayload - 21);
        //On a maintenant dans message, le message à hacher et dans messageDigestPayload, le hash du message envoyé
        //Il faut donc vérifier si le hash est bon
        byte[] messageDigestLocal;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1", "BC");
            md.update(message);
            messageDigestLocal = md.digest();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
        String payload;
        if (messageDigestLocal.equals(messageDigestPayload)){
            payload = "Hash reçu OK, message : " + message.toString();
        }else{
            payload = "Hash reçu NOK, message : " + message.toString();
        }
        return next(Payload.of(payload.getBytes(StandardCharsets.UTF_8)));
    }
}