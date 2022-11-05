package be.rm.secu.tp1.cli;

import picocli.CommandLine;

public class ServerCommand {
    @CommandLine.Option(
        names = {"-p", "--port"},
        description = "Définis le port d'écoute du serveur",
        scope = CommandLine.ScopeType.INHERIT
    ) protected int _port;
}
