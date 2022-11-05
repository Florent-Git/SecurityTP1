package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.AESEncoderMiddleware;
import be.rm.secu.tp1.chain.B64EncoderMiddleware;
import be.rm.secu.tp1.chain.CRLFAppenderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.net.DHClient;
import picocli.CommandLine;

import javax.net.SocketFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "aes-client",
    description = "Lancement d'un client qui envoie un message encrypté par AES (et D-H) à un serveur"
)

public class AESCommandClient extends ClientCommand implements Callable<Integer> {
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
                new AESEncoderMiddleware(),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .build();

        //Envoi du message
        client.sendMessage(_message);

        return 0;
    }
}
