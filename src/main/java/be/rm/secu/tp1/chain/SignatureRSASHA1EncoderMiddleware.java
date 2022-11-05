package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class SignatureRSASHA1EncoderMiddleware extends Middleware<Payload<byte[]>> {
    private PrivateKey privateKey;

    public SignatureRSASHA1EncoderMiddleware(File privateKeyFile) {
        try {
            var privateKeyData = Files.readAllBytes(privateKeyFile.toPath());

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyData);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            privateKey = kf.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            error(e);
        }
    }

    @Override
    public Payload<byte[]> operate(Payload<byte[]> object) {
        byte[] returnValue;

        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initSign(privateKey);
            sign.update(object.getObject());
            var signed = sign.sign();

            var out = new ByteArrayOutputStream();
            var dataStream = new DataOutputStream(out);

            dataStream.writeInt(signed.length);
            out.write(object.getObject());
            out.write(signed);

            returnValue = out.toByteArray();
        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException | SignatureException e) {
            error(e);
            return null;
        }

        return next(object.copy(returnValue));
    }
}
