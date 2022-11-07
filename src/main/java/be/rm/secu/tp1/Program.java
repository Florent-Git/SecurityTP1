package be.rm.secu.tp1;

import be.rm.secu.tp1.cli.*;
import picocli.CommandLine;

@CommandLine.Command(
    name = "tp1",
    description = "Projet de TP1 en Principe de Sécurité des Réseaux pour le Master en Architecture des Systèmes Informatiques à la HEPL",
    subcommands = {
        ThreeDESCommandClient.class,
        ThreeDESCommandServer.class,
        AesDHCommandServer.class,
        AESCommandClient.class,
        SHACommandClient.class,
        SHACommandServer.class,
        HMACCommandServer.class,
        HMACMD5CommandClient.class,
        RSASHA1CommandClient.class,
        RSASHA1CommandServer.class,
        RSACommandClient.class,
        RSACommandServer.class
    }
)
public class Program {
    public static void main(String[] args) {
        System.exit(
            new CommandLine(new Program())
                .execute(args)
        );
    }
}
