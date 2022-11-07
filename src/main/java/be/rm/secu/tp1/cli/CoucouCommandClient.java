package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.*;
import be.rm.secu.tp1.net.Client;
import be.rm.secu.tp1.net.DHClient;
import picocli.CommandLine;

import javax.net.SocketFactory;
import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "coucou-client",
    description = "Énoncé final du projet de sécurité, côté client"
)
public class CoucouCommandClient extends ClientCommand implements Callable<Integer> {
    @CommandLine.Option(
        names = { "--privatekey" },
        description = "La clef privé d'encryption"
    ) private File _privateKey;

    @Override
    public Integer call() throws Exception {
        Client client = new DHClient.Builder()
            .withHost(_host)
            .withPort(_port)
            .withSocketFactory(SocketFactory.getDefault())
            .withStdout(System.out)
            .withOutputMiddlewares(Middleware.link(
                new AESEncoderMiddleware(),
                new SignatureRSASHA1EncoderMiddleware(_privateKey),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .build();

        client.sendMessage("Coucou les amis, moi je mange la glace");

        return 0;
    }
}
