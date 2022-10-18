package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.net.ServerConnexionPayload;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class EchoMiddleware extends Middleware<ServerConnexionPayload> {
    @Override
    public ServerConnexionPayload operate(ServerConnexionPayload object) {
        try {
            var out = new PrintStream(object.source().getConnexionSocket().getOutputStream());
            out.println(new String(object.bytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return next(object);
    }
}
