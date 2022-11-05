package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64DecoderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.StdoutMiddleware;
import be.rm.secu.tp1.chain.ThreeDesDecoderMiddleware;
import be.rm.secu.tp1.net.Server;
import picocli.CommandLine;

import javax.net.ServerSocketFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@CommandLine.Command(
    name = "3des-server",
    description = "Lancement d'un server qui écoute pour recevoir un message encrypté par 3DES"
)
public class ThreeDESCommandServer extends ServerCommand implements Callable<Integer> {
    @CommandLine.Option(
        names = { "-k", "--key" },
        description = "Clef d'encryption 3DES (112 ou 168 bits) (défaut: ILOVESECURITY)"
    ) private String _key = "ILOVESECURITY";

    @Override
    public Integer call() throws Exception {
        var executor = Executors.newFixedThreadPool(4);

        var server = Server.builder()
            .withPort(_port)
            .withExecutorService(executor)
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withInputMiddleware(Middleware.link(
                new B64DecoderMiddleware(),
                new ThreeDesDecoderMiddleware(_key),
                new StdoutMiddleware()
            ))
            .build();

        return server.call();
    }
}
