package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.*;
import be.rm.secu.tp1.net.Server;
import picocli.CommandLine;

import javax.net.ServerSocketFactory;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@CommandLine.Command(
    name = "rsasha1-server",
    description = "Lancement d'un server qui écoute pour recevoir un message signé par SHA-1 et RSA"
)
public class RSASHA1CommandServer extends ServerCommand implements Callable<Integer> {
    @CommandLine.Option(
        names = { "-k", "--publickey" },
        description = "Clé publique du client"
    ) private File _publickey;

    @Override
    public Integer call() throws Exception {
        var executor = Executors.newFixedThreadPool(4);

        var server = Server.builder()
            .withPort(_port)
            .withExecutorService(executor)
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withInputMiddleware(Middleware.link(
                new B64DecoderMiddleware(),
                new SignatureRSASH1DecoderMiddleware(_publickey),
                new StdoutMiddleware()
            ))
            .build();

        return server.call();
    }
}
