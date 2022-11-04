package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.util.Payload;
import com.google.inject.Singleton;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 */
@Singleton
public class Server implements Callable<Integer> {
    protected final ExecutorService _executorService;
    protected final ServerSocket _serverSocket;
    protected Future<Integer> _serverService;
    protected final Map<ServerConnexion, Future<Integer>> _serverConnexions = new HashMap<>();
    protected final Middleware<Payload<byte[]>> _inputMiddleware;
    protected final Middleware<Payload<byte[]>> _outputMiddleware;
    private final PrintStream _printer;

    protected Server(
        int port,
        ServerSocketFactory socketFactory,
        ExecutorService executorService,
        OutputStream printer,
        Middleware<Payload<byte[]>> inputMiddleware,
        Middleware<Payload<byte[]>> outputMiddleware
    ) throws IOException {
        _serverSocket = socketFactory.createServerSocket(port);
        _executorService = executorService;

        _printer = new PrintStream(printer);
        _inputMiddleware = inputMiddleware;
        _outputMiddleware = outputMiddleware;
    }

    public void start() {
        _serverService = _executorService.submit(this::listen);
    }

    /**
     * Écoute la connexion des clients au serveur
     * @return 0 si aucune erreur, -1 si il y a une erreur
     */
    protected Integer listen() throws IOException {
        while (!_serverService.isCancelled() && !_serverService.isDone()) {
            var serverConnexionSocket = _serverSocket.accept();
            var serverConnexion = new ServerConnexion(serverConnexionSocket, _outputMiddleware, _inputMiddleware);
            var serverConnexionService = _executorService.submit(serverConnexion);
            _serverConnexions.put(serverConnexion, serverConnexionService);
        }

        return 0;
    }

    @Override
    public Integer call() {
        Scanner scanner = new Scanner(System.in);
        try {
            start();
            while (!_serverService.isCancelled() && !_serverService.isDone()) {
                byte entry = scanner.nextByte();
                if (entry == 0x04) _serverService.cancel(true);
            }
            return 0;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        protected int port;
        protected ServerSocketFactory serverSocketFactory;
        protected OutputStream printer;
        protected ExecutorService executorService;
        protected Middleware<Payload<byte[]>> outputMiddleware;
        protected Middleware<Payload<byte[]>> inputMiddleware;

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withServerSocketFactory(ServerSocketFactory factory) {
            this.serverSocketFactory = factory;
            return this;
        }

        public Builder withStdout(OutputStream printer) {
            this.printer = printer;
            return this;
        }

        public Builder withExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder withOutputMiddleware(Middleware<Payload<byte[]>> middleware) {
            this.outputMiddleware = middleware;
            return this;
        }

        public Builder withInputMiddleware(Middleware<Payload<byte[]>> middleware) {
            this.inputMiddleware = middleware;
            return this;
        }

        public Server build() throws Exception {
            if (inputMiddleware == null) inputMiddleware = Middleware.basic();
            if (outputMiddleware == null) outputMiddleware = Middleware.basic();

            return new Server(
                port,
                serverSocketFactory,
                executorService,
                printer,
                inputMiddleware,
                outputMiddleware
            );
        }
    }
}
