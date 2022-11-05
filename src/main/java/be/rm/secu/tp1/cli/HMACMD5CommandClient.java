package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64EncoderMiddleware;
import be.rm.secu.tp1.chain.CRLFAppenderMiddleware;
import be.rm.secu.tp1.chain.HMACMD5EncoderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.net.DHClient;
import picocli.CommandLine;

import javax.net.SocketFactory;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "hmacmd5-client",
    description = "Lancement d'un client qui envoie un message authentifié par HMAC-MD5 (et D-H) à un serveur"
)
public class HMACMD5CommandClient extends ClientCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
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

