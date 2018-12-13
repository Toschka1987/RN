package client;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import utility.ServerResponse;

import static utility.ServerResponse.FORCE_DISCONNECT;

/**
 * Hört dem Server für ankommende Nachrichten ab und zeigt diesem dem Client an.
 */
public class ServerListener extends Thread {
    private static final Logger Log = Logger.getLogger(ServerListener.class);
    // Referenzierung zu dem Client
    @NotNull
    private ClientEntity client;

    /**
     * Konstruktor für die Klasse.
     *
     * @param client der ClientEntity
     */
    ServerListener(@NotNull final ClientEntity client) {
        Preconditions.checkNotNull(client, ServerResponse.INVALID_CLIENT);
        this.client = client;
    }

    /**
     * Diese Methode führt den Thread aus, zeigt dem Client solange es aktiv ist,
     * alle Nachrichten an, die der Client zu empfangen hat.
     *
     * @see Thread#run()
     */
    public void run() {
        while (client.connection.isActive()) {
            try {
                client.show(client.connection.receive());
                Log.debug("Client_ServerListener " + client.toString());
            } catch (final Exception e) {
                client.show(FORCE_DISCONNECT + "left chat");
                client.connection.kill();
            }
        }
    }
}