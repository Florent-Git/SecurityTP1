package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import java.util.Arrays;

public class CRLFAppenderMiddleware extends Middleware<Payload<byte[]>> {
    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        var object = payload.object();
        var newByteArray = Arrays.copyOf(object, object.length + 2);
        newByteArray[object.length] = '\r';
        newByteArray[object.length + 1] = '\n';
        return next(Payload.of(newByteArray, payload.source()));
    }
}
