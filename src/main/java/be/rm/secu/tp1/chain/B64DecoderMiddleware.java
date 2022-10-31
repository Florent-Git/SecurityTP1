package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.net.ServerConnexionPayload;

import java.util.Base64;

public class B64DecoderMiddleware extends Middleware<ServerConnexionPayload>{
    @Override
    public ServerConnexionPayload operate(ServerConnexionPayload object) {
        var decoder = Base64.getDecoder();
        var newObject = new ServerConnexionPayload(decoder.decode(object.bytes()), object.source());
        return next(newObject);
    }
}
