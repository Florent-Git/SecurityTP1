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

// Message de Base -> Encrypter en 3DES         -> Encoder en B64           -> Ajouter \n à la fin
//                    ThreeDesEncoderMiddleware    B64EncoderMiddleware     -> CRLFAppenderMiddleware

@CommandLine.Command(
    name = "3des-client",
    description = "Lancement d'un client qui envoie un message encrypté par 3DES à un serveur"
)
public class ThreeDESCommandClient extends ClientCommand implements Callable<Integer> {
    @CommandLine.Option(
        names = { "-k", "--key" },
        description = "Clef d'encryption 3DES (112 ou 168 bits) (défaut: ILOVESECURITY)"
    ) private String _key = "AAAAAAAAAAAAAAAAAAAAAAAA";

    @Override
    public Integer call() throws Exception {
        InputStream stdin;
        if (_message != null) stdin = new ByteArrayInputStream(_message.getBytes(StandardCharsets.UTF_8));
        else stdin = System.in;

        var client = Client.builder()
            .withHost(_host)
            .withPort(_port)
            .withStdout(System.out)
            .withSocketFactory(SocketFactory.getDefault())
            .withOutputMiddlewares(Middleware.link(
                new ThreeDesEncoderMiddleware(_key),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .build();

        var scanner = new Scanner(stdin);

        client.sendMessage(scanner.nextLine());

        return 0;
    }
}
