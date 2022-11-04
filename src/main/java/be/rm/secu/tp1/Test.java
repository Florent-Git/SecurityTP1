package be.rm.secu.tp1;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        // --------------------------------------- ALICE ---------------------------------------------

        var aliceKeyPairGenerator = KeyPairGenerator.getInstance("DH");
        aliceKeyPairGenerator.initialize(2048);
        var aliceKeyPair = aliceKeyPairGenerator.generateKeyPair();

        var aliceKeyAgreement = KeyAgreement.getInstance("DH");
        aliceKeyAgreement.init(aliceKeyPair.getPrivate());

        byte[] alicePubKeyEnc = aliceKeyPair.getPublic().getEncoded();

        // ---------------------------------------- BOB -----------------------------------------------

        KeyFactory bobKeyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec bobX509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);
        PublicKey alicePubKey = bobKeyFactory.generatePublic(bobX509KeySpec);

        var bobKeyPairGenerator = KeyPairGenerator.getInstance("DH");
        bobKeyPairGenerator.initialize(2048);
        var bobKeyPair = bobKeyPairGenerator.generateKeyPair();

        var bobKeyAgreement = KeyAgreement.getInstance("DH");
        bobKeyAgreement.init(bobKeyPair.getPrivate());

        byte[] bobPubKeyEnc = bobKeyPair.getPublic().getEncoded();

        // --------------------------------------- ALICE ---------------------------------------------

        KeyFactory aliceKeyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec aliceX509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
        PublicKey bobPubKey = aliceKeyFactory.generatePublic(aliceX509KeySpec);

        aliceKeyAgreement.doPhase(bobPubKey, true);

        // ---------------------------------------- BOB -----------------------------------------------

        bobKeyAgreement.doPhase(alicePubKey, true);

        System.out.println(Base64.getEncoder().encodeToString(bobKeyAgreement.generateSecret()));
        System.out.println(Base64.getEncoder().encodeToString(aliceKeyAgreement.generateSecret()));
    }
}
