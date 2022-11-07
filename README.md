# Travail Pratique pour le cours de Sécurité du MASI de la HEPL à Liège

Ce projet contient la solution des exercices pour les travaux pratiques du premier quadrimestre.

## Fonctionnement du programme

Le programme fonctionne sur une interface type CLI Linux. L'exécution de ce dernier suit le schéma suivant:

```shell
> java -jar tp1-all-1.0.0-SNAPSHOT.jar [command]-[type] -p [port]
```

Où `command` représente l'énoncé de chacun des exercices du projet:

| Énoncé           | Command  |
|------------------|----------|
| 3DES/EBC         | 3des     |
| AES/CBC          | aes      |
| SHA-1            | sha1     |
| HMAC-MD5         | hmacmd5  |
| RSA & SHA-1      | rsa-sha1 |
| RSA (certificat) | rsa      |

Et `type` est soit `client`, soit `server`

### Les options de commandes

Chacun des énoncés possède leurs propres options qui sont mise à défaut lors de l'exécution du programme (sauf quelques
options où ce n'est pas possible de mettre une valeur par défaut qui fonctionne sur toutes les machines.

| Option         | Command  | Type     | Description                                                               | Valeur par défaut        |
|----------------|----------|----------|---------------------------------------------------------------------------|--------------------------|
| -p, --port     | Toutes   | Les deux | Port d'écoute du serveur ou de destination du client                      | 4556                     |
| -h, --host     | Toutes   | Client   | Hôte sur lequel le serveur est lancé                                      | localhost                |
| -m, --message  | Toutes   | Client   | Message envoyé du client au serveur                                       | Hello world !            |
| --keystore     | rsa      | Les deux | Fichier de keystore/truststore pour le server/client (PKCS 12)            | N/A*1                    |
| -k, --key      | 3des     | Les deux | Clef d'encryption pour l'encryption 3DES                                  | AAAAAAAAAAAAAAAAAAAAAAAA |
| -P, --password | rsa      | Les deux | Mot de passe du fichier keystore / truststore                             | hepl2022                 |
| -a, --alias    | rsa      | Les deux | Alias pour le certificat / la clef publique dans le keystore / truststore | cert                     |
| --privatekey   | rsa-sha1 | Client   | Le chemin du fichier de clef privé pour l'encryption RSA                  | N/A*1                    |
| --publickey    | rsa-sha1 | Server   | Le chemin du fichier de clef publique pour l'encryption RSA               | N/A*1                    |

*1 Il n'est pas possible de mettre une valeur par défaut du fichier car tout dépends de l'ouverture du projet sur la machine de l'utilisateur.

### Exemple:
```shell
> java -jar tp1-all-1.0.0-SNAPSHOT.jar rsa-server -P myPassword --alias cert -p 4556 --keystore ./my-key-store.p12
```

## Build du programme

Si le jar n'est pas disponible, il faut build le jar:

```shell
> gradlew fatJar
```

Le fichier sera disponible sous `./build/libs/tp1-all-1.0.0-SNAPSHOT.jar`.

Des fichiers certificats et des paires de clefs sont disponibles sous `./src/main/resources/`.
