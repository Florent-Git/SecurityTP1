package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class RSAEncoderMiddleware extends Middleware<Payload<byte[]>> {
    private PrivateKey privatekey;

    public RSAEncoderMiddleware(File keystoreFile, String password, String alias){
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            char[] pwd = password.toCharArray();
            keyStore.load(new FileInputStream(keystoreFile), pwd);
            KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection(pwd);
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, keyPassword);

            privatekey = privateKeyEntry.getPrivateKey();

        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Payload<byte[]> operate(Payload<byte[]> object) {
        byte[] payload;
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, privatekey);
            payload = encryptCipher.doFinal();
        } catch (NoSuchPaddingException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
        return next(Payload.of(payload));
    }
}
