package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import java.util.Base64;

public class B64DecoderMiddleware extends Middleware<Payload<byte[]>>{
    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        var decoder = Base64.getDecoder();
        return next(payload.copy(decoder.decode(payload.getObject())));
    }
}
