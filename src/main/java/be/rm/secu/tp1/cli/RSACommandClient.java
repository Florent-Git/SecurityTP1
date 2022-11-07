package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64EncoderMiddleware;
import be.rm.secu.tp1.chain.CRLFAppenderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.RSAEncoderMiddleware;
import be.rm.secu.tp1.net.Client;
import picocli.CommandLine;

import javax.net.SocketFactory;
import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(
    name = "rsa-client",
    description = "Envoie un message crypté avec RSA"
)
public class RSACommandClient extends ClientCommand implements Callable<Integer> {
    @CommandLine.Option(
        names = { "-k", "--keystore" },
        description = "Le truststore contenat le certificat du serveur"
    ) private File keystoreFile;

    @CommandLine.Option(
        names = { "-a", "--alias" },
        description = "L'alias du certificat dans le truststore"
    ) private String alias = "cert";

    @CommandLine.Option(
        names = { "-P", "--password" },
        description = "Le mot de passe du truststore"
    ) private String password = "hepl2022";

    @Override
    public Integer call() throws Exception {
        var client = Client.builder()
            .withSocketFactory(SocketFactory.getDefault())
            .withPort(_port)
            .withHost(_host)
            .withOutputMiddlewares(Middleware.link(
                new RSAEncoderMiddleware(keystoreFile, password, alias),
                new B64EncoderMiddleware(),
                new CRLFAppenderMiddleware()
            ))
            .withStdout(System.out)
            .build();

        client.sendMessage(_message);

        return 0;
    }
}
