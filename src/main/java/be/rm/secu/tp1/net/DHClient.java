package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.util.Payload;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.checkerframework.checker.units.qual.C;

import javax.net.SocketFactory;
import java.io.*;
import java.util.concurrent.ExecutorService;

public class DHClient extends Client {
    private Subject<byte[]> secretKeySubject = PublishSubject.create();

    protected DHClient(String host, int port, SocketFactory socketFactory, ExecutorService executorService, OutputStream stdout, Middleware<Payload<byte[]>> inputMiddlewares, Middleware<Payload<byte[]>> outputMiddlewares) throws IOException {
        super(host, port, socketFactory, executorService, stdout, inputMiddlewares, outputMiddlewares);
    }

    public static class Builder extends Client.Builder{
        @Override
        public Client build() throws IOException {
            return new DHClient(host, port, socketFactory, executorService, stdout, inputMiddlewares, outputMiddlewares);
        }
    }

    @Override
    protected Integer listen() throws IOException {
        var input = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
        var out = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));

        secretKeySubject.onNext(//secretKey);
        while (!_clientCon.isDone() && !_clientCon.isCancelled()) {
            var newMessage = input.readLine();
            onNewMessage(newMessage);
        }

        return 0;
    }

    public Observable<byte[]> waitForSecretKey(){
        return secretKeySubject.;
    }
}
