package server;

/**
 * <b>BENUTZUNG: java -jar Server.jar [port]</b>
 * Diese Applikation dient dazu, um den Sever zu starten.
 */
public class ServerMain {
    public static void main(String[] args) throws NumberFormatException {
        int portNr;

        switch (args.length) {
            case 1:
                portNr = Integer.parseInt(args[0]);
                break;
            default:
                return;
        }
        ServerEntity serverEntity = new ServerEntity(portNr);
        serverEntity.start();
    }
}