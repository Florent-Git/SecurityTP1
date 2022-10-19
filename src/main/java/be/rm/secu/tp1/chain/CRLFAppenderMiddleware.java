package be.rm.secu.tp1.chain;

import java.util.Arrays;

public class CRLFAppenderMiddleware extends Middleware<byte[]> {
    @Override
    public byte[] operate(byte[] object) {
        var newByteArray = Arrays.copyOf(object, object.length + 2);
        newByteArray[object.length] = '\r';
        newByteArray[object.length + 1] = '\n';
        return next(newByteArray);
    }
}
