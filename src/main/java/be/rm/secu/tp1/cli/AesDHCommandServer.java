package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.AesDecoderMiddleware;
import be.rm.secu.tp1.chain.B64DecoderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.StdoutMiddleware;
import be.rm.secu.tp1.net.DHServer;
import picocli.CommandLine;

import javax.net.ServerSocketFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@CommandLine.Command(
    name = "aes-server",
    description = "Lance un server qui écoute les messages encryptés par AES et la clef est générée par DH"
)
public class AesDHCommandServer extends ServerCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        var server = new DHServer.Builder()
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withExecutorService(Executors.newFixedThreadPool(2))
            .withInputMiddleware(Middleware.link(
                new B64DecoderMiddleware(),
                new AesDecoderMiddleware(),
                new StdoutMiddleware()
            ))
            .withPort(_port)
            .build();

        return server.call();
    }
}
