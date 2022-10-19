package be.rm.secu.tp1;

import be.rm.secu.tp1.chain.CRLFAppenderMiddleware;
import be.rm.secu.tp1.chain.EchoMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.net.Server;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.util.concurrent.Executors;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        var executorService = Executors.newFixedThreadPool(4);

        Server server = Server.builder()
            .withPort(18697)
            .withServerSocketFactory(ServerSocketFactory.getDefault())
            .withExecutorService(executorService)
            .withPrinter(System.out)
            .withInputMiddleware(Middleware.link(new EchoMiddleware()))
            .withOutputMiddleware(Middleware.link(new CRLFAppenderMiddleware()))
            .build();

        server.start();
    }
}
