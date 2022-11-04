package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64EncoderMiddleware;
import be.rm.secu.tp1.chain.CRLFAppenderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.ThreeDesEncoderMiddleware;
import be.rm.secu.tp1.net.Client;
import picocli.CommandLine;

import javax.net.SocketFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

// Message de Base -> Encrypter en 3DES         -> Encoder en B64           -> Ajouter \n à la fin
//                    ThreeDesEncoderMiddleware    B64EncoderMiddleware     -> CRLFAppenderMiddleware

@CommandLine.Command(
    name = "3des-client",
    description = "Lancement d'un client qui envoie un message encrypté par 3DES à un serveur"
)
public class ThreeDESCommandClient implements Callable<Integer> {
    @CommandLine.Option(
        names = { "-k", "--key" },
        description = "Clef d'encryption 3DES (112 ou 168 bits) (défaut: ILOVESECURITY)"
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
                new ThreeDesEncoderMiddleware(_key),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .build();

        client.call();

        var scanner = new Scanner(stdin);

        client.sendMessage(scanner.nextLine());

        return client.call();
    }
}
