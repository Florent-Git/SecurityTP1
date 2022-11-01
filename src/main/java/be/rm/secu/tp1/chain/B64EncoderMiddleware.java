package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import java.util.Base64;

public class B64EncoderMiddleware extends Middleware<Payload<byte[]>> {
    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        var encoder = Base64.getEncoder();
        return next(Payload.of(encoder.encode(payload.object()), payload.source()));
    }
}
