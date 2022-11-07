package be.rm.secu.tp1.cli;

import picocli.CommandLine;

public class ClientCommand {
    @CommandLine.Option(
        names = { "-p", "--port" },
        description = "Définis le port destination",
        scope = CommandLine.ScopeType.INHERIT
    ) protected int _port = 4556;

    @CommandLine.Option(
        names = { "-h", "--host" },
        description = "Définis l'hôte distant",
        scope = CommandLine.ScopeType.INHERIT
    ) protected String _host = "localhost";

    @CommandLine.Option(
        names = { "-m", "--message" },
        description = "Définis le message à envoyer au serveur",
        scope = CommandLine.ScopeType.INHERIT
    ) protected String _message = "Hello world !";
}
