package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.util.Payload;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

public class Client {
    protected final Socket _socket;
    private final PrintStream _printer;
    private final Middleware<Payload<byte[]>> _inputMiddlewares;
    private final Middleware<Payload<byte[]>> _outputMiddlewares;
    private final Subject<byte[]> newMessages = PublishSubject.create();

    protected Client(
        String host,
        int port,
        SocketFactory socketFactory,
        OutputStream stdout,
        Middleware<Payload<byte[]>> inputMiddlewares,
        Middleware<Payload<byte[]>> outputMiddlewares
    ) throws IOException {
        _inputMiddlewares = inputMiddlewares;
        _outputMiddlewares = outputMiddlewares;
        this._socket = socketFactory.createSocket(host, port);

        this._printer = new PrintStream(stdout);
    }

    public void sendMessage(String message) throws IOException {
        sendMessage(message.getBytes(StandardCharsets.UTF_8));
    }

    public void sendMessage(byte[] message) throws IOException {
        var processedInput = _outputMiddlewares.operate(
            createPayload(message)
        );

        _socket.getOutputStream().write(processedInput.getObject());
    }

    protected Payload<byte[]> createPayload(byte[] bytes){
        return Payload.of(bytes);
    }

    public byte[] readMessage() {
        return newMessages.blockingFirst();
    }

    protected void onNewMessage(String newMessage) {
        var encodedMessage = newMessage.getBytes(StandardCharsets.UTF_8);
        var operatedMessage = _inputMiddlewares.operate(Payload.of(encodedMessage));
        newMessages.onNext(operatedMessage.getObject());
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
                host, port, socketFactory, stdout, inputMiddlewares, outputMiddlewares
            );
        }
    }
}
