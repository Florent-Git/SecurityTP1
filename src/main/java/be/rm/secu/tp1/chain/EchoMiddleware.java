package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.net.ServerConnexionPayload;

import java.io.IOException;

public class EchoMiddleware extends Middleware<ServerConnexionPayload> {
    @Override
    public ServerConnexionPayload operate(ServerConnexionPayload object) {
        try {
            object.source().send(object.bytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return next(object);
    }
}
