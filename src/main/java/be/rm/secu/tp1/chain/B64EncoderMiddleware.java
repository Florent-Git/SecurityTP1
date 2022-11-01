package be.rm.secu.tp1.chain;

import java.util.Base64;

public class B64EncoderMiddleware extends Middleware<byte[]> {
    @Override
    public byte[] operate(byte[] object) {
        var encoder = Base64.getEncoder();
        return next(encoder.encode(object));
    }
}
