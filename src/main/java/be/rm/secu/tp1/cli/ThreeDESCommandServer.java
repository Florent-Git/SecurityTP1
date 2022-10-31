package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.*;
import be.rm.secu.tp1.net.Server;
import picocli.CommandLine;

import javax.net.ServerSocketFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@CommandLine.Command(
    name = "3des-server",
    description = "Lancement d'un server qui écoute pour recevoir un message encrypté par 3DES"
)
public class ThreeDESCommandServer implements Callable<Integer> {
    @CommandLine.Option(
        names = { "-k", "--key" },
        description = "Clef d'encryption 3DES (112 ou 168 bits) (défaut: ILOVESECURITY)"
    ) private String _key = "ILOVESECURITY";

    @CommandLine.Option(
        names = { "-p", "--port" },
        description = "Port d'écoute (défaut: 56978)"
    ) private int _port = 56978;

    @Override
    public Integer call() throws Exception {
        var executor = Executors.newFixedThreadPool(4);

        var server = Server.builder()
            .withPort(_port)
            .withExecutorService(executor)
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withStdout(System.out)
            .withInputMiddleware(Middleware.link(
                new B64DecoderMiddleware(),
                new ThreeDesDecoderMiddleware(_key),
                new StdoutServerPayloadMiddleware()
            ))
            .build();

        return server.call();
    }
}
