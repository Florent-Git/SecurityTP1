package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.net.ServerConnexionPayload;

import java.nio.charset.StandardCharsets;

public class StdoutServerPayloadMiddleware extends Middleware<ServerConnexionPayload> {
    @Override
    public ServerConnexionPayload operate(ServerConnexionPayload object) {
        System.out.println(new String(object.bytes(), StandardCharsets.UTF_8));
        return new ServerConnexionPayload(new byte[0], object.source());
    }
}
