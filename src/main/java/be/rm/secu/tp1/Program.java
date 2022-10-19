package be.rm.secu.tp1;

import be.rm.secu.tp1.chain.CRLFAppenderMiddleware;
import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.chain.StdoutMiddleware;
import be.rm.secu.tp1.net.Client;

import javax.net.SocketFactory;
import java.util.concurrent.Executors;

public class Program {
    public static void main(String[] args) throws Exception {
        var executorService = Executors.newFixedThreadPool(4);

        Client client = Client.builder()
            .withHost("localhost")
            .withPort(18697)
            .withSocketFactory(SocketFactory.getDefault())
            .withExecutorService(executorService)
            .withStdin(System.in)
            .withStdout(System.out)
            .withOutputMiddlewares(Middleware.link(new CRLFAppenderMiddleware()))
            .withInputMiddlewares(Middleware.link(new StdoutMiddleware()))
            .build();

        client.call();
    }
}
