package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64DecoderMiddleware;
import be.rm.secu.tp1.chain.HMACDecoder;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.StdoutMiddleware;
import be.rm.secu.tp1.net.DHServer;
import picocli.CommandLine;

import javax.net.ServerSocketFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@CommandLine.Command(
    name = "hmac-server",
    description = "Lance un serveur qui va écouter un message authentifié par HMAC-MD5"
)
public class HMACCommandServer extends ServerCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        var server = new DHServer.Builder()
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withInputMiddleware(Middleware.link(
                new B64DecoderMiddleware(),
                new HMACDecoder(),
                new StdoutMiddleware()
            ))
            .withPort(_port)
            .withExecutorService(Executors.newFixedThreadPool(2))
            .build();

        return server.call();
    }
}
