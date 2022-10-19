package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ServerConnexion implements Callable<Integer>, Closeable {
    private final Socket _connexionSocket;
    private final PublishSubject<ServerConnexionPayload> publishSubject;
    private final List<Disposable> _disposables = new ArrayList<>();
    private final Middleware<byte[]> _outputMiddleware;
    private boolean _shouldClose = false;

    public ServerConnexion(
        Socket connexionSocket,
        Middleware<byte[]> outputMiddleware
    ) {
        _outputMiddleware = outputMiddleware;
        _connexionSocket = connexionSocket;
        publishSubject = PublishSubject.create();
    }

    @Override
    public Integer call() throws IOException {
        var input = new BufferedReader(new InputStreamReader(_connexionSocket.getInputStream()));

        while (!_shouldClose) {
            var string = input.readLine();
            var bytes = string.getBytes(StandardCharsets.UTF_8);
            System.out.println("New message from " + _connexionSocket.getInetAddress());
            publish(new ServerConnexionPayload(bytes, this));
        }

        return 0;
    }

    @Override
    public void close() throws IOException {
        System.out.println("Client " + _connexionSocket.getInetAddress() + " disconnected");
        _connexionSocket.close();
        _shouldClose = true;
        publishSubject.onComplete();
        _disposables.forEach(d -> {
            if (!d.isDisposed()) d.dispose();
        });
    }

    public void subscribe(Consumer<? super ServerConnexionPayload> onNext) {
        _disposables.add(publishSubject.subscribe(onNext));
    }

    public void send(byte[] message) throws IOException {
        var output = _connexionSocket.getOutputStream();
        var processedMessage = _outputMiddleware.operate(message);

        output.write(processedMessage);
    }

    private void publish(ServerConnexionPayload value) {
        publishSubject.onNext(value);
    }
}
