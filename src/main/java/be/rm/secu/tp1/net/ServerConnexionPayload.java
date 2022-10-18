package be.rm.secu.tp1.net;

public record ServerConnexionPayload(
    byte[] bytes,
    ServerConnexion source
) { }
