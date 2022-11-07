package be.rm.secu.tp1.cli;

import be.rm.secu.tp1.chain.B64DecoderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.RSADecoderMiddleware;
import be.rm.secu.tp1.chain.StdoutMiddleware;
import be.rm.secu.tp1.net.Server;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.net.ServerSocketFactory;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@Command(
    name = "rsa-server",
    description = "Créé un serveur pour écouter un message encrypté par RSA via un certificat dans un keystore java"
)
public class RSACommandServer extends ServerCommand implements Callable<Integer> {
    @Option(
        names = { "--keystore" },
        description = "Specifie le keystore dans lequel la clef privé du certificat se situe"
    ) private File _keyStoreFile;

    @Option(
        names = { "-P", "--password" },
        description = "Le mot de passe du keystore"
    )
    private String _password;

    @Option(
        names = { "-a", "--alias" },
        description = "Alias du certificat dans le keystore"
    ) private String _alias;

    @Override
    public Integer call() throws Exception {
        var server = Server.builder()
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withPort(_port)
            .withExecutorService(Executors.newFixedThreadPool(2))
            .withInputMiddleware(Middleware.link(
                new B64DecoderMiddleware(),
                new RSADecoderMiddleware(_keyStoreFile, _alias, _password),
                new StdoutMiddleware()
            ))
            .build();

        return server.call();
    }
}
