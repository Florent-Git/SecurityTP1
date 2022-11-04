package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.*;
import be.rm.secu.tp1.net.Client;
import picocli.CommandLine;

import javax.crypto.KeyAgreement;
import javax.net.SocketFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@CommandLine.Command(
    name = "aes-client",
    description = "Lancement d'un client qui envoie un message encrypté par AES (et D-H) à un serveur"
)

public class AESCommandClient implements Callable<Integer> {
    @CommandLine.Option(
        names = { "-k", "--key" },
        description = "Clé d'encryption AES (128 bits) (défaut: ILOVESECURITY)"
    ) private String _key = "ILOVESECURITY";

    @CommandLine.Option(
        names = { "-p", "--port" },
        description = "Port d'écoute du serveur d'encryption (défaut: 56978)"
    ) private int _port = 56978;

    @CommandLine.Option(
        names = { "-h", "--host" },
        description = "Hôte d'écoute du message encrypté"
    ) private String _host;

    @CommandLine.Option(
        names = { "-m", "--message" },
        description = "Le message à encrypter. Ferme le client juste après"
    ) private String _message;

    @Override
    public Integer call() throws Exception {
        var executor = Executors.newFixedThreadPool(2);
        byte[] clientSharedSecret = new byte[0];

        InputStream stdin;
        if (_message != null) stdin = new ByteArrayInputStream(_message.getBytes(StandardCharsets.UTF_8));
        else stdin = System.in;

        var client = Client.builder()
            .withHost(_host)
            .withPort(_port)
            .withStdout(System.out)
            .withExecutorService(executor)
            .withSocketFactory(SocketFactory.getDefault())
            .withOutputMiddlewares(Middleware.link(
                new AESEncoderMiddleware(clientSharedSecret),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .build();

        client.call();

        //Génération de la paire de clés client DH
        KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
        clientKpairGen.initialize(2048);
        KeyPair clientKpair = clientKpairGen.generateKeyPair();

        //Initialisation du KeyAgreement du client
        KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
        clientKeyAgree.init(clientKpair.getPrivate());

        //Encryption et envoi de la clé publique du client au serveur
        byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();

        client.sendMessage(clientPubKeyEnc);

        //Traitement de la réponse du serveur
        KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(client.readMessage());
        PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);
        clientKeyAgree.doPhase(serverPubKey, true);

        //Récupération de la clé
        clientSharedSecret = clientKeyAgree.generateSecret();

        //Envoi du message
        client.sendMessage(_message);

        return client.call();
    }
}
