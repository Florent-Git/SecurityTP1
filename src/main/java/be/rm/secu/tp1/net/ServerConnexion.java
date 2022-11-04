package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.util.Payload;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class ServerConnexion implements Callable<Integer>, Closeable {
    private final Socket _connexionSocket;
    private final Middleware<Payload<byte[]>> _outputMiddleware;
    private final Middleware<Payload<byte[]>> _inputMiddleware;
    private boolean _shouldClose = false;

    public ServerConnexion(
        Socket connexionSocket,
        Middleware<Payload<byte[]>> outputMiddleware,
        Middleware<Payload<byte[]>> inputMiddleware
    ) {
        _outputMiddleware = outputMiddleware;
        _inputMiddleware = inputMiddleware;
        _connexionSocket = connexionSocket;
    }

    @Override
    public Integer call() throws IOException {
        var input = new BufferedReader(new InputStreamReader(_connexionSocket.getInputStream()));

        while (!_shouldClose) {
            var string = input.readLine();
            var bytes = string.getBytes(StandardCharsets.UTF_8);
            _inputMiddleware.operate(Payload.of(bytes));
        }

        return 0;
    }

    @Override
    public void close() throws IOException {
        _connexionSocket.close();
        _shouldClose = true;
    }

    public void send(byte[] message) throws IOException {
        var output = _connexionSocket.getOutputStream();
        var processedMessage = _outputMiddleware.operate(Payload.of(message));

        output.write(processedMessage.getObject());
    }
}
