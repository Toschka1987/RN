package server;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import utility.Connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static utility.ServerResponse.INVALID_PORT;

class ServerEntity {
    private static final Logger Log = Logger.getLogger(ServerEntity.class);

    // Jede Client-ID ist identifizierbar
    private static int uniqueID;

    // Instanz für den ServerSocket
    private ServerSocket serverSocket;

    // Es existiert nur ein Chatraum
    @NotNull
    private ChatRoom room;

    // Server läuft solange, bis keepGoing auf false steht
    private boolean keepGoing;

    // Port vom Server
    @NotNull
    private Integer port;

    /**
     * Constructor
     *
     * @param port Port des Servers in Integer
     */
    ServerEntity(@NotNull final Integer port) {
        Preconditions.checkNotNull(port, INVALID_PORT);

        this.port = port;
        room = new ChatRoom(this, "Room");

        Log.debug("Server starting..");
        Log.debug(String.format("Server managing only room (%s)", getRoom().getName()));
    }

    /**
     *
     */
    void start() {
        keepGoing = true;

        try {
            serverSocket = new ServerSocket(port);
            Log.debug(String.format("Server startet on %s", serverSocket.getInetAddress().getHostName()));

            // solange der Server läuft (immer)
            while (keepGoing) {
                Socket socket = serverSocket.accept();
                room.enterChatRoom(Connection.to(socket));
                if (!keepGoing) break;
            }
            closeAllConnections();
        } catch (final IOException e) {
            Log.error("Exception on new ServerSocket", e);
        }
        Log.debug("ServerEntity started.");
    }

    /**
     * Gibt die ClientID zurück, die ein Client identifizerbar macht.
     *
     * @return clientID als Integer
     */
    @NotNull
    Integer getClientIdFromSequence() {
        return ++uniqueID;
    }

    /**
     * Schließt die Verbindungen
     */
    private void closeAllConnections() {
        Log.debug("Close all Connections..");
        try {
            serverSocket.close();
            for (final ConnectedClient client : room.getClients()) client.disconnect();
        } catch (final Exception e) {
            Log.error("Exception on closeAllConnections", e);
        }
        Log.debug("All Connections closed!");
    }

    /**
     * Gibt den aktuellen Raum zurück
     *
     * @return den Raum Namen als ChatRoom
     */
    @Contract(pure = true)
    @NotNull
    private ChatRoom getRoom() {
        return room;
    }
}