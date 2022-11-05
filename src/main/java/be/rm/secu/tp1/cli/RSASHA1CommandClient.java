package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64EncoderMiddleware;
import be.rm.secu.tp1.chain.CRLFAppenderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.SignatureRSASHA1EncoderMiddleware;
import be.rm.secu.tp1.net.Client;
import picocli.CommandLine;

import javax.net.SocketFactory;
import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "rsa-sha1-client",
    description = "Envoie un message signé avec RSA/SHA1"
)
public class RSASHA1CommandClient extends ClientCommand implements Callable<Integer> {
    @CommandLine.Option(
        names = { "-k", "--privatekey" },
        description = "La clef privé RSA dont la clef publique a été précédemment communiquée avec le serveur"
    ) private File privateKeyFile;

    @Override
    public Integer call() throws Exception {
        var client = Client.builder()
            .withSocketFactory(SocketFactory.getDefault())
            .withPort(_port)
            .withHost(_host)
            .withOutputMiddlewares(Middleware.link(
                new SignatureRSASHA1EncoderMiddleware(privateKeyFile),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .withStdout(System.out)
            .build();

        client.sendMessage(_message);

        return 0;
    }
}
