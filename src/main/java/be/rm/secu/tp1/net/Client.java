package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.util.Payload;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import javax.net.SocketFactory;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Client implements Callable<Integer> {
    protected final Socket _socket;
    private final PrintStream _printer;
    protected Future<Integer> _clientCon;
    private final Middleware<Payload<byte[]>> _inputMiddlewares;
    private final Middleware<Payload<byte[]>> _outputMiddlewares;
    private final ExecutorService _executorService;
    private final Subject<byte[]> newMessages = PublishSubject.create();

    protected Client(
        String host,
        int port,
        SocketFactory socketFactory,
        ExecutorService executorService,
        OutputStream stdout,
        Middleware<Payload<byte[]>> inputMiddlewares,
        Middleware<Payload<byte[]>> outputMiddlewares
    ) throws IOException {
        _inputMiddlewares = inputMiddlewares;
        _outputMiddlewares = outputMiddlewares;
        this._socket = socketFactory.createSocket(host, port);

        this._executorService = executorService;

        this._printer = new PrintStream(stdout);
    }

    @Override
    public Integer call() {
        _clientCon = _executorService.submit(this::listen);
        return 0;
    }

    public void sendMessage(String message) throws IOException {
        sendMessage(message.getBytes(StandardCharsets.UTF_8));
    }

    public void sendMessage(byte[] message) throws IOException {
        var processedInput = _outputMiddlewares.operate(
            Payload.of(message, this)
        );

        _socket.getOutputStream().write(processedInput.object());
    }

    public byte[] readMessage() {
        return newMessages.blockingFirst();
    }

    protected Integer listen() throws IOException {
        var input = new BufferedReader(new InputStreamReader(_socket.getInputStream()));

        while (!_clientCon.isDone() && !_clientCon.isCancelled()) {
            var newMessage = input.readLine();
            onNewMessage(newMessage);
        }

        return 0;
    }

    protected void onNewMessage(String newMessage) {
        var encodedMessage = newMessage.getBytes(StandardCharsets.UTF_8);
        var operatedMessage = _inputMiddlewares.operate(Payload.of(encodedMessage, null));
        newMessages.onNext(operatedMessage.object());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        protected String host;
        protected int port;
        protected SocketFactory socketFactory;
        protected ExecutorService executorService;
        protected OutputStream stdout;
        protected Middleware<Payload<byte[]>> inputMiddlewares;
        protected Middleware<Payload<byte[]>> outputMiddlewares;

        public Builder withHost(String host) {
            this.host = host;
            return this;
        }

        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        public Builder withSocketFactory(SocketFactory socketFactory) {
            this.socketFactory = socketFactory;
            return this;
        }

        public Builder withExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder withStdout(OutputStream stdout) {
            this.stdout = stdout;
            return this;
        }

        public Builder withInputMiddlewares(Middleware<Payload<byte[]>> inputMiddlewares) {
            this.inputMiddlewares = inputMiddlewares;
            return this;
        }

        public Builder withOutputMiddlewares(Middleware<Payload<byte[]>> outputMiddlewares) {
            this.outputMiddlewares = outputMiddlewares;
            return this;
        }

        public Client build() throws IOException {
            if (inputMiddlewares == null) inputMiddlewares = Middleware.basic();
            if (outputMiddlewares == null) outputMiddlewares = Middleware.basic();

            return new Client(
                host, port, socketFactory, executorService, stdout, inputMiddlewares, outputMiddlewares
            );
        }
    }
}
