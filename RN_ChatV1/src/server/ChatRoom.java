package server;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utility.Connection;
import utility.ServerResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static utility.ServerResponse.*;

/**
 * Diese Klasse definiert den Chatraum.
 * Es gibt immer nur ein Raum, der vom Server verwaltet wird.
 * Sobald sich ein Client einloggt, wird er automatisch dem Chatraum hinzugefügt.
 */
public class ChatRoom {
    private static final Logger LOG = Logger.getLogger(ChatRoom.class);

    // Hält alle Clients, die verbunden sind, in einer Liste
    private List<ConnectedClient> clients;

    // Der Name des Raums
    private String name;

    // Die Angabe des Datumformats
    private SimpleDateFormat dateFormatter;

    // Die Referenzierungs-Instanz für den Server
    private ServerEntity serverEntity;

    /**
     * Konstruktor für die Klasse
     *
     * @param serverEntity the serverEntity
     * @param name         the name of the room
     */
    ChatRoom(@NotNull final ServerEntity serverEntity, @NotNull final String name) {
        Preconditions.checkNotNull(serverEntity, INVALID_SERVER);
        Preconditions.checkNotNull(name, INVALID_USERNAME);

        clients = new ArrayList<>();
        this.name = name;
        this.serverEntity = serverEntity;
        dateFormatter = new SimpleDateFormat("HH:mm:ss");
    }

    /**
     * Für jedes C
     *
     * @param connectedClient as ConnectedClient
     */
    private synchronized void enterChatRoom(@NotNull final ConnectedClient connectedClient) {
        Preconditions.checkNotNull(connectedClient, INVALID_CONNCETION);

        LOG.debug(String.format("%s_Client ist dem Raum (%s) beigetreten", IN_ROOM, name));
        clients.add(connectedClient);
        connectedClient.deliverMessage(String.format(IN_ROOM + "Welcome in the Chat Room %s", name));
        distributeMessage(String.format("%s has entered the room %s", connectedClient.getUsername(), name));
    }

    /**
     * Creates a new thread for each connection.
     *
     * @param connection as Connection
     */
    synchronized void enterChatRoom(@NotNull final Connection connection) {
        Preconditions.checkNotNull(connection, INVALID_CONNCETION);

        final ConnectedClient connectedClient = new ConnectedClient(serverEntity, this, connection);
        connectedClient.start();
        enterChatRoom(connectedClient);
    }

    /**
     * Verbreitet die Nachricht, die der Client schickt, an alle.
     *
     * @param message die Nachricht als String
     */
    synchronized void distributeMessage(@NotNull final String message) {
        Preconditions.checkNotNull(message, INVALID_MESSAGE);

        // dd HH:mm:ss and \n to the transfer object
        String time = dateFormatter.format(new Date());
        String messageFormated = String.format("%s %s", time, message);
        LOG.debug(String.format("Room [%s] <<< %s", name, messageFormated));

        // Durchlaufe alle Clients - Decrement
        for (int i = clients.size(); --i >= 0; ) {
            ConnectedClient clientThread = clients.get(i);
            // try to write to the client => if it fails, removeClientFromRoom it format list
            if (!clientThread.deliverMessage(messageFormated + "\n")) clients.remove(i);
        }
    }

    /**
     * Der Client, der LOGOUT bentuzt, wird erstmal vom Raum entfernt.
     *
     * @param id die ID des Client in Integer
     */
    synchronized void removeClientFromRoom(@NotNull final Integer id) {
        Preconditions.checkNotNull(id, INVALID_CLIENT);

        // Solange durchgehen, bis die ID des Abzumeldenden Clients gefunden wird
        for (int i = 0; i < clients.size(); ++i) {
            ConnectedClient client = clients.get(i);
            if (client.clientId == id) {
                clients.remove(i);
                return;
            }
        }
    }

    /**
     * Unmodifiable list of connected Clients
     *
     * @return Unmodifiable list of connected Clients as List<ConnectedClient>
     */
    synchronized List<ConnectedClient> getClients() {
        return Collections.unmodifiableList(this.clients);
    }

    /**
     * Get the chatRoom Name
     *
     * @return name as String
     */
    @NotNull
    String getName() {
        return name;
    }
}