package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import com.google.inject.Singleton;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
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
    private final ExecutorService _executorService;
    private final ServerSocket _socket;
    private Future<Integer> _serverService;
    private final Map<ServerConnexion, Future<Integer>> _serverConnexions = new HashMap<>();
    private final Middleware<ServerConnexionPayload> _inputMiddleware;
    private final Middleware<byte[]> _outputMiddleware;
    private final PrintStream _printer;

    private Server(
        int port,
        ServerSocketFactory socketFactory,
        ExecutorService executorService,
        OutputStream printer,
        Middleware<ServerConnexionPayload> inputMiddleware,
        Middleware<byte[]> outputMiddleware
    ) throws IOException {
        _socket = socketFactory.createServerSocket(port);
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
    private Integer listen() throws IOException {
        while (!_serverService.isCancelled() && !_serverService.isDone()) {
            var serverConnexionSocket = _socket.accept();
            System.out.println("New connexion from " + serverConnexionSocket.getInetAddress());
            var serverConnexion = new ServerConnexion(serverConnexionSocket, _outputMiddleware);
            var serverConnexionService = _executorService.submit(serverConnexion);
            serverConnexion.subscribe(this::onConnexionMessage);
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

    private void onConnexionMessage(ServerConnexionPayload payload) {
        var processedMessage = _inputMiddleware.operate(payload);
        var stringMessage = new String(processedMessage.bytes(), StandardCharsets.UTF_8);
        _printer.println(stringMessage);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int port;
        private ServerSocketFactory serverSocketFactory;
        private OutputStream printer;
        private ExecutorService executorService;
        private Middleware<byte[]> outputMiddleware;
        private Middleware<ServerConnexionPayload> inputMiddleware;

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

        public Builder withOutputMiddleware(Middleware<byte[]> middleware) {
            this.outputMiddleware = middleware;
            return this;
        }

        public Builder withInputMiddleware(Middleware<ServerConnexionPayload> middleware) {
            this.inputMiddleware = middleware;
            return this;
        }

        public Server build() throws IOException {
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
