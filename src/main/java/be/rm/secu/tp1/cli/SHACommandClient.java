package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64EncoderMiddleware;
import be.rm.secu.tp1.chain.CRLFAppenderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.ShaHashMiddleware;
import be.rm.secu.tp1.net.Client;
import picocli.CommandLine;

import javax.net.SocketFactory;
import java.io.IOException;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "sha-client",
    description = "Envoie un message et le hash de ce dernier"
)
public class SHACommandClient extends ClientCommand implements Callable<Integer> {
    @Override
    public Integer call() throws IOException {
        Client client = Client.builder()
            .withHost(_host)
            .withPort(_port)
            .withSocketFactory(SocketFactory.getDefault())
            .withOutputMiddlewares(Middleware.link(
                new ShaHashMiddleware(),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .withStdout(System.out)
            .build();

        client.sendMessage(_message);

        return 0;
    }
}
