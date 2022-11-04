package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.util.Payload;

import javax.crypto.KeyAgreement;
import javax.net.SocketFactory;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DHClient extends Client {
    private byte[] secretKey;
    protected DHClient(String host, int port, SocketFactory socketFactory, OutputStream stdout, Middleware<Payload<byte[]>> inputMiddlewares, Middleware<Payload<byte[]>> outputMiddlewares) throws IOException {
        super(host, port, socketFactory, stdout, inputMiddlewares, outputMiddlewares);

        var input = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        var out = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));

        try {
            //Génération de la paire de clés client DH
            KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
            clientKpairGen.initialize(2048);
            KeyPair clientKpair = clientKpairGen.generateKeyPair();

            //Initialisation du KeyAgreement du client
            KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
            clientKeyAgree.init(clientKpair.getPrivate());

            //Encryption et envoi de la clé publique du client au serveur
            byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();
            out.write(Base64.getEncoder().encodeToString(clientPubKeyEnc));
            out.newLine();
            out.flush();

            //Traitement de la réponse du serveur
            KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
            var b64In = input.readLine();
            byte[] in = Base64.getDecoder().decode(b64In);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(in);
            PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);
            clientKeyAgree.doPhase(serverPubKey, true);

            //Récupération de la clé
            secretKey = clientKeyAgree.generateSecret();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder extends Client.Builder{
        @Override
        public Client build() throws IOException {
            return new DHClient(host, port, socketFactory, stdout, inputMiddlewares, outputMiddlewares);
        }
    }

    @Override
    protected Payload<byte[]> createPayload(byte[] bytes) {
        return super.createPayload(bytes).withOption("key", secretKey);
    }
}
