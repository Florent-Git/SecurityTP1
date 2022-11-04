package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.util.Payload;

import java.net.Socket;

public class DHServerConnexion extends ServerConnexion {
    private byte[] secret;

    public DHServerConnexion(
        Socket connexionSocket,
        Middleware<Payload<byte[]>> outputMiddleware,
        Middleware<Payload<byte[]>> inputMiddleware,
        byte[] secret
    ) {
        super(connexionSocket, outputMiddleware, inputMiddleware);
        this.secret = secret;
    }

    @Override
    protected Payload<byte[]> createPayload(byte[] object) {
        return super.createPayload(object)
            .withOption("key", secret);
    }
}
