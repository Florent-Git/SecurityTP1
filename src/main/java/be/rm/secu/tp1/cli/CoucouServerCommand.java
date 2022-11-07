package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.*;
import be.rm.secu.tp1.net.DHServer;
import be.rm.secu.tp1.net.Server;
import picocli.CommandLine;

import javax.net.ServerSocketFactory;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@CommandLine.Command(
    name = "coucou-server",
    description = "Énoncé final du projet de sécurité, côté serveur"
)
public class CoucouServerCommand extends ClientCommand implements Callable<Integer> {

    @CommandLine.Option(
        names = { "--publickey" },
        description = "La clef publique d'encryption"
    ) private File _publicKey;

    @Override
    public Integer call() throws Exception {
        Server server = new DHServer.Builder()
            .withPort(_port)
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withExecutorService(Executors.newFixedThreadPool(2))
            .withInputMiddleware(Middleware.link(
                new B64DecoderMiddleware(),
                new SignatureRSASH1DecoderMiddleware(_publicKey),
                new AesDecoderMiddleware(),
                new StdoutMiddleware()
            ))
            .build();

        return server.call();
    }
}
