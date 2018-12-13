package server;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import transfer_message.Message;
import utility.Connection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static utility.ServerResponse.*;

/**
 *
 */
public class ConnectedClient extends Thread {
    private static final Logger LOG = Logger.getLogger(ConnectedClient.class);

    // Die Client-ID des Clients
    int clientId;

    // Connection to the client
    private Connection connection;

    // Benutzername des Clients + Datum der Verbindung
    private String username, dateOfConnection;

    // Der Raum, in der sich der Client befindet
    private ChatRoom chatRoom;

    // Datumformatierung für Nachrichten
    private SimpleDateFormat dateFormatter;

    /**
     * @param serverEntity der Server
     * @param chatRoom     der Chatraum
     * @param connection   die Connection des Client
     */
    ConnectedClient(@NotNull ServerEntity serverEntity, @NotNull ChatRoom chatRoom, @NotNull Connection connection) {
        Preconditions.checkNotNull(serverEntity, INVALID_SERVER);
        Preconditions.checkNotNull(chatRoom, INVALID_ROOM);
        Preconditions.checkNotNull(connection, INVALID_CONNCETION);

        this.chatRoom = chatRoom;
        this.connection = connection;
        this.clientId = serverEntity.getClientIdFromSequence();
        this.dateOfConnection = new Date().toString() + "\n";
        this.dateFormatter = new SimpleDateFormat("HH:mm:ss");
        LOG.debug(connection.getHostname());

        // Versucht den Usernamen zu empfangen, der vom Client angegeben wurde
        try {
            username = connection.receive().getPayload();
            LOG.debug(CLIENT_CREATED + "Handshake erfolgreich, Thread erstellt");
        } catch (final Exception e) {
            LOG.error("Thread could not create streams", e);
        }
    }

    /**
     * @see Thread#run()
     */
    public void run() {
        boolean keepGoing = true;
        Message message;

        while (keepGoing) {
            try {
                message = connection.receive();
                LOG.debug(MESSAGE_RECEIVED + "Message empfangen");
                LOG.debug("Ich bin ConnectedClient: " + connection.getHostname());
            } catch (final Exception e) {
                LOG.error("Thread could not read obj", e);
                break;
            }

            // Die Nachrichtenarten, die Empfangen werden
            switch (message.getType()) {
                case WHO_IS_IN:
                    deliverWhoIsIn();
                    break;
                case MESSAGE:
                    distributeMessage(message);
                    break;
                case LOGOUT:
                    keepGoing = false;
                    deliverMessage(String.format("User %s left chat!", username));
                    LOG.debug(FORCE_DISCONNECT + "User: " + username + " left Chat!");
                    break;
                case HELP:
                    deliverHelp();
                    break;
            }
        }
        leaveChatRoom();
        connection.kill();
    }

    /**
     * Gibt den Benutzernamen zurück
     *
     * @return username in String
     */
    @NotNull
    String getUsername() {
        return username;
    }

    /**
     * Beendet die Verbindung
     */
    void disconnect() {
        connection.kill();
    }

    /**
     * Write a String to the Client output stream
     *
     * @param message die Nachricht, die verschickt werden soll
     * @return true, wenn die Nachricht verbreitet wurde, false andernfalls
     */
    synchronized boolean deliverMessage(@NotNull final String message) {
        Preconditions.checkNotNull(message, "transfer_object must not be null.");

        // PRECONDITION: Client still connected.
        if (connection.isInactive()) {
            connection.kill();
            return false;
        }

        try {
            connection.send(message);
            return true;
        } catch (final IOException e) {
            // if an error occurs, do not abort just inform the user
            LOG.debug("Couldn't write transfer_message to stream", e);
            return false;
        }
    }

    /**
     * Diese Methode wird dem Client angezeigt.
     */
    private void deliverHelp() {
        deliverMessage("" +
                "1.) LOGOUT für abmelden, \n" +
                "2.) WHOISIN zeigt alle User an, die eingeloggt sind, \n");
    }

    /**
     * Diese Methode liefert die verbundenen Clients dem User, der das anfordert.
     */
     private void deliverWhoIsIn() {
        deliverMessage("List of the users connected at " + dateFormatter.format(new Date()) + "\n");
        List<ConnectedClient> clients = chatRoom.getClients(); // get unmodifiable list

        for (int i = 0; i < clients.size(); ++i) {
            ConnectedClient client = clients.get(i);
            deliverMessage(String.format("%d.) %s since %s", i + 1, client.username, client.dateOfConnection));
        }
    }

    /**
     * @param message die Nachricht, die verbreitet werden soll
     */
    private void distributeMessage(@NotNull final Message message) {
        chatRoom.distributeMessage(username + ": " + message.getPayload());
    }


    /**
     *
     */
    private void leaveChatRoom() {
        LOG.debug(username + " is leaving " + chatRoom.getName());
        chatRoom.removeClientFromRoom(this.clientId);
        chatRoom = null;
    }
}