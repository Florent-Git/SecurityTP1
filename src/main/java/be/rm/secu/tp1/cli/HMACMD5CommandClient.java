package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.*;
import be.rm.secu.tp1.net.DHClient;
import picocli.CommandLine;

import javax.net.SocketFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "hmacmd5-client",
    description = "Lancement d'un client qui envoie un message authentifié par HMAC-MD5 (et D-H) à un serveur"
)

public class HMACMD5CommandClient implements Callable<Integer> {
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
        InputStream stdin;
        if (_message != null) stdin = new ByteArrayInputStream(_message.getBytes(StandardCharsets.UTF_8));
        else stdin = System.in;

        var client = new DHClient.Builder()
            .withHost(_host)
            .withPort(_port)
            .withStdout(System.out)
            .withSocketFactory(SocketFactory.getDefault())
            .withOutputMiddlewares(Middleware.link(
                new HMACMD5EncoderMiddleware(),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .build();

        //Envoi du message
        client.sendMessage(_message);

        return 0;
    }
}
