package client;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import transfer_message.Message;
import utility.Connection;
import utility.ServerResponse;

import java.io.IOException;

import static utility.ServerResponse.*;

/**
 * Diese Klasse ist dient als Client für den User.
 * Diese Klasse kann sich mit dem Server verbinden und Nachricht verschicken.
 */
class ClientEntity {
    private static Logger LOG = Logger.getLogger(ClientEntity.class);

    // Die Referenzierung zur Connection
    Connection connection;

    // Name des Benutzers
    private String username;

    /**
     * Der Konstruktor mit einem Paramter für den Benutzernamen.
     *
     * @param name not null
     */
    ClientEntity(@NotNull final String name) {
        Preconditions.checkNotNull(name, ServerResponse.GIVE_USERNAME);
        this.username = name;
    }

    /**
     * Diese Methode verbindet sich zum Server, mit der ang. Hostadresse und Port.
     *
     * @param address IP or URL must not be null!
     * @param port    number of the server - must not be null!
     * @throws ServerNotFoundException if connection refused
     */
    void connect(@NotNull final String address, @NotNull final Integer port) throws ServerNotFoundException {
        Preconditions.checkNotNull(address, INVALID_ADDRESS);
        Preconditions.checkNotNull(port, INVALID_PORT);

        try {
            connection = Connection.to(address, port);
            LOG.info(HANDSHAKE_OK + "" + connection.getHostname() + ":" + connection.getPort());
        } catch (final IOException e) {
            LOG.info(INVALID_CONNCETION);
            throw new ServerNotFoundException(e);
        }

        // Empfängt Nachrichten vom Server
        new ServerListener(this).start();

        // Sendet den Benutzernamen zum Server
        try {
            connection.send(username);
            LOG.debug(VALID_USERNAME + "Valid Username");
            LOG.debug(LOGGED_IN + "Logged in");
        } catch (IOException eIO) {
            connection.kill();
        }
    }

    /**
     * Diese Methode schickt die Nachricht des Clients
     *
     * @param message the transfer object - not null
     * @throws IOException if can not write
     */
    void sendMessage(@NotNull final Message message) throws IOException {
        Preconditions.checkNotNull(message, "message must not be null.");
        connection.send(message);
    }

    /**
     * Zeigt die Nachricht, die verschickt wurde, den Client an.
     *
     * @param message die Nachricht in String, die angezeigt wird
     */
    void show(@NotNull final String message) {
        Preconditions.checkNotNull(message, "message must not be null.");
        System.out.println(message);
    }

    /**
     * Zeigt die Nachricht, die verschickt wurde, den Client an.
     *
     * @param message die Nachricht in String, die angezeigt wird
     */
    void show(@NotNull final Message message) {
        Preconditions.checkNotNull(message, "message must not be null.");
        System.out.println(message.getPayload());
    }
}