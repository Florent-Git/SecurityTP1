package be.rm.secu.tp1.chain;

import be.rm.secu.tp1.util.Payload;

import java.nio.charset.StandardCharsets;

public class StdoutMiddleware extends Middleware<Payload<byte[]>> {
    @Override
    public Payload<byte[]> operate(Payload<byte[]> payload) {
        System.out.println(new String(payload.object(), StandardCharsets.UTF_8));
        return payload;
    }
}
