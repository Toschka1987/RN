package client;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import transfer_message.Message;
import utility.ServerResponse;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>BENUTZUNG: java Client [username] [port] [address]</b>
 * Diese Klasse ist die Client Applikation für den programmierten Server.
 */
public class ClientMain {
    // Die Objektreferenzierung
    private static ClientEntity client;

    // Die Portnummer
    private static int hostPort;

    // Der Hostname
    private static String hostname = "localhost";

    // Der Benutzername
    private static String userName;

    /**
     * Main Methode, while we have a connection we read the commands and then
     * to all included commands we sends a message.
     *
     * @param args [username][port][address]
     * @throws NumberFormatException   if hostPort not an int
     * @throws ServerNotFoundException if not connect to server
     * @throws IOException             not write to server
     */
    public static void main(String[] args) throws NumberFormatException, ServerNotFoundException, IOException {
        getCommands(args);
        connectToTheGivenServer(hostname, hostPort, userName);

        Scanner scan = new Scanner(System.in);
        while (client.connection.isActive()) {
            if (scan.hasNext()) {
                System.out.print("> ");
                String msg = scan.nextLine();

                if (!msg.equalsIgnoreCase("LOGOUT")) {
                    if (msg.equalsIgnoreCase("WHOISIN"))
                        client.sendMessage(new Message(Message.MessageType.WHO_IS_IN));

                    else if (msg.equalsIgnoreCase("HELP"))
                        client.sendMessage(new Message(Message.MessageType.HELP));

                    else
                        client.sendMessage(new Message(Message.MessageType.MESSAGE, msg));
                } else {
                    client.sendMessage(new Message(Message.MessageType.LOGOUT));
                    break;
                }
            }
        }
    }

    /**
     * Diese Methode holt sich die Argumente aus dem Terminal.
     * Example: java Client [username] [port] [address]
     *
     * @param args das Argument im Terminal
     */
    private static void getCommands(String[] args) {
        switch (args.length) {
            case 3:
                hostname = args[2];
            case 2:
                hostPort = Integer.parseInt(args[1]);
            case 1:
                userName = args[0];
            case 0:
                break;
        }
    }

    /**
     * Diese Methode verbindet den Client zu dem Server.
     *
     * @param hostAddress the Hostaddress
     * @param hostPort    1500, an integer
     * @param userName    userName is Anonymous
     * @throws ServerNotFoundException if not connecto to the server
     * @throws IOException             it not can write to the server
     */
    private static void connectToTheGivenServer(@NotNull String hostAddress, int hostPort, @NotNull String userName) throws ServerNotFoundException, IOException {
        Preconditions.checkState(checkName(userName), ServerResponse.INVALID_USERNAME);

        client = new ClientEntity(userName);
        client.connect(hostAddress, hostPort);
    }

    /**
     * Diese Username prüft den Benutzernamen. Dieser Darf nur a-z/A-Z und Nummern enthalten.
     * Die Mindestlänge ist 2 und Maxlänge des Benutzernamens ist 15.
     *
     * @param userName der Username in String
     * @return true, wenn bedingung gegeben, false andernfalls.
     */
    private static boolean checkName(String userName) {
        Preconditions.checkNotNull(userName, ServerResponse.GIVE_USERNAME);
        Pattern namePattern = Pattern.compile("[a-zA-Z0-9]+");
        Matcher pM = namePattern.matcher(userName);
        return userName.length() >= 2 && userName.length() <= 15 && pM.matches();
    }
}