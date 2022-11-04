package be.rm.secu.tp1.net;

import be.rm.secu.tp1.chain.Middleware;
import be.rm.secu.tp1.util.Payload;

import javax.crypto.KeyAgreement;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.ExecutorService;

public class DHServer extends Server {
    private KeyPair dhKeyPair;
    private KeyFactory keyFactory;

    protected DHServer(
        int port,
        ServerSocketFactory socketFactory,
        ExecutorService executorService,
        Middleware<Payload<byte[]>> inputMiddleware,
        Middleware<Payload<byte[]>> outputMiddleware
    ) throws IOException, NoSuchAlgorithmException {
        super(port, socketFactory, executorService, inputMiddleware, outputMiddleware);

        var keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(2048);
        dhKeyPair = keyPairGenerator.generateKeyPair();

        keyFactory = KeyFactory.getInstance("DH");
    }

    @Override
    protected Integer listen() throws IOException {
        while (!_serverService.isCancelled() && !_serverService.isDone()) {
            var serverConnexionSocket = _serverSocket.accept();

            var out = new BufferedWriter(new OutputStreamWriter(serverConnexionSocket.getOutputStream()));
            var in = new BufferedReader(new InputStreamReader(serverConnexionSocket.getInputStream()));

            try {
                var b64PubKeyEnc = in.readLine();
                var pubKeyEnc = Base64.getDecoder().decode(b64PubKeyEnc);
                var x509KeySpec = new X509EncodedKeySpec(pubKeyEnc);
                var clientPubKey = keyFactory.generatePublic(x509KeySpec);

                out.write(Base64.getEncoder().encodeToString(dhKeyPair.getPublic().getEncoded()));
                out.newLine();
                out.flush();

                var keyAgreement = KeyAgreement.getInstance("DH");
                keyAgreement.init(dhKeyPair.getPrivate());
                keyAgreement.doPhase(clientPubKey, true);

                var serverConnexion = new DHServerConnexion(
                    serverConnexionSocket,
                    _outputMiddleware,
                    _inputMiddleware,
                    keyAgreement.generateSecret()
                );

                var serverConnexionService = _executorService.submit(serverConnexion);
                _serverConnexions.put(serverConnexion, serverConnexionService);
            } catch (InvalidKeySpecException | InvalidKeyException | NoSuchAlgorithmException e) {
                out.write("Error: Invalid key");
                out.newLine();
                out.flush();
                serverConnexionSocket.close();
            }
        }

        return 0;
    }

    public static class Builder extends Server.Builder {
        @Override
        public Server build() throws Exception {
            return new DHServer(
                port, serverSocketFactory, executorService, inputMiddleware, outputMiddleware
            );
        }
    }
}
