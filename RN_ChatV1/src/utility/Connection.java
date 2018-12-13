package utility;

import client.ServerListener;
import org.apache.log4j.Logger;
import transfer_message.Message;
import transfer_message.JSON;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Baut die Verbindung auf - TCP basiert.
 */
public class Connection {
    private static final Logger Log = Logger.getLogger(ServerListener.class);

    /**
     * TCP-Standard Socket
     */
    private Socket socket;

    /**
     * Outputstream to Server
     */
    private ObjectOutputStream outputStream;

    /**
     * Inputstream to the server
     */
    private ObjectInputStream inputStream;

    /**
     * Hostname
     */
    private String hostname;

    /**
     * The Port from the server address
     */
    private Integer port;

    /**
     * Constructor
     *
     * @param socket socket
     * @throws IOException not read
     */
    private Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream()); // Must be declared before Input
        this.inputStream = new ObjectInputStream(socket.getInputStream());

        this.hostname = socket.getInetAddress().toString();
        this.port = socket.getPort();
    }

    /**
     * Method creates a new instance
     */
    @NotNull
    public synchronized static Connection to(@NotNull final Socket socket) throws IOException {
        Preconditions.checkNotNull(socket, "socket must not be null.");
        return new Connection(socket);
    }

    /**
     * Method creates a new instance
     */
    @NotNull
    public synchronized static Connection to(@NotNull final String serverAddress, @NotNull final Integer port) throws IOException {
        Preconditions.checkNotNull(serverAddress, "hostname must not be null.");
        Preconditions.checkNotNull(port, "port must not be null.");

        return to(new Socket(serverAddress, port));
    }

    /**
     * Receives transfer_message from socket
     */
    @NotNull
    public Message receive() throws IOException, ClassNotFoundException {
        return JSON.valueOf((String) inputStream.readObject()).to(Message.class);
    }

    /**
     * Sends a transfer_message
     */
    public void send(@NotNull final Message message) throws IOException {
        outputStream.writeObject(JSON.format(message));
    }

    /**
     * Wraps string to default transfer_message and sends it
     */
    public void send(@NotNull final String message) throws IOException {
        send(new Message(message));
    }

    /**
     * Beendet die Verbindung
     */
    public void kill() {
        try {
            if (outputStream != null) outputStream.close();
            if (inputStream != null) inputStream.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the hostname
     *
     * @return hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Get the port
     *
     * @return port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Proofs, if the connection active
     *
     * @return True, if still connected!
     */
    public boolean isActive() {
        return !socket.isClosed();
    }

    /**
     * Proofs, if the connection is inactive
     *
     * @return true, if the conncetion not active
     */
    public boolean isInactive() {
        return !isActive();
    }
}