package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SignatureRSASH1DecoderMiddleware extends Middleware<Payload<byte[]>> {
    PublicKey clientPublicKey;
    public SignatureRSASH1DecoderMiddleware(File publicKey){
        try {
            byte[] keyBytes = Files.readAllBytes(publicKey.toPath());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            clientPublicKey = kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Payload<byte[]> operate(Payload<byte[]> object) {
        //On a un message avec taille de signature (4 bytes) + message + signature
        var longueurPayload = object.getObject().length;
        int tailleSign = 0;
        for (int i = 0; i < 4; i++) {
            tailleSign = (tailleSign << 8) + (object.getObject()[i] & 0xFF);
        }
        var signaturePayload = Arrays.copyOfRange(object.getObject(), longueurPayload - tailleSign, longueurPayload);
        var message = Arrays.copyOfRange(object.getObject(), 4, longueurPayload - tailleSign);

        boolean verif;
        try {
            Signature signatureLocale = Signature.getInstance("SHA1withRSA");
            signatureLocale.initVerify(clientPublicKey);
            signatureLocale.update(message);
            verif = signatureLocale.verify(signaturePayload);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException(e);
        }

        if (verif){
            System.out.println("Signature reçue: OK");
        }else{
            System.out.println("Signature reçue: NOK");
        }

        return next(object.copy(message));
    }
}
