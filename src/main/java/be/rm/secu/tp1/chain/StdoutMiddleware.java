package be.rm.secu.tp1.chain;

import java.nio.charset.StandardCharsets;

public class StdoutMiddleware extends Middleware<byte[]> {
    @Override
    public byte[] operate(byte[] object) {
        System.out.println(new String(object, StandardCharsets.UTF_8));
        return new byte[0];
    }
}
