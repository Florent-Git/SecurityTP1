package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.*;
import be.rm.secu.tp1.net.Client;
import be.rm.secu.tp1.net.DHClient;
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

        InputStream stdin;
        if (_message != null) stdin = new ByteArrayInputStream(_message.getBytes(StandardCharsets.UTF_8));
        else stdin = System.in;

        var client = new DHClient.Builder()
            .withHost(_host)
            .withPort(_port)
            .withStdout(System.out)
            .withExecutorService(executor)
            .withSocketFactory(SocketFactory.getDefault())
            .withOutputMiddlewares(Middleware.link(
                new AESEncoderMiddleware(),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .build();

        client.call();

        //Envoi du message
        client.sendMessage(_message);

        return client.call();
    }
}
