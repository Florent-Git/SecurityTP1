package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64DecoderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.SHAServerMiddleware;
import be.rm.secu.tp1.chain.StdoutMiddleware;
import be.rm.secu.tp1.net.Server;
import picocli.CommandLine;

import javax.net.ServerSocketFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@CommandLine.Command(
    name = "sha1-server",
    description = "Lancement d'un server qui écoute pour recevoir un message hashé avec SHA-1"
)
public class SHACommandServer extends ServerCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        var executor = Executors.newFixedThreadPool(4);

        var server = Server.builder()
            .withPort(_port)
            .withExecutorService(executor)
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withInputMiddleware(Middleware.link(
                new B64DecoderMiddleware(),
                new SHAServerMiddleware(),
                new StdoutMiddleware()
            ))
            .build();

        return server.call();
    }
}
