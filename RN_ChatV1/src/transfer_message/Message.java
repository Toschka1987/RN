package transfer_message;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

/**
 * Messages for communication between Client (Client-Thread) & ClientServerController-Thread
 */
public class Message {
    // Der Typ der Message
    @NotNull
    private MessageType type;

    // Payload der Message
    @NotNull
    private String payload;

    /**
     * Konstrurktor
     */
    @SuppressWarnings("unused")
    public Message() {
        this.type = MessageType.MESSAGE;
        this.payload = "";
    }

    /**
     * Constructor for convenience
     *
     * @param type Not null
     */
    public Message(@NotNull final MessageType type) {
        this.type = type;
        this.payload = "";
    }

    /**
     * Constructor for convenience.
     *
     * @param payload Not null
     */
    public Message(@NotNull final String payload) {
        this.type = MessageType.MESSAGE;
        this.payload = payload;
    }

    /**
     * Constructor with transfer_message Type
     *
     * @param type    Not null.
     * @param payload Not null.
     */
    public Message(@NotNull final MessageType type, @NotNull final String payload) {
        Preconditions.checkNotNull(type, "type must not be null.");
        Preconditions.checkNotNull(payload, "payload must not be null.");

        this.type = type;
        this.payload = payload;
    }

    /**
     * Gets the type
     *
     * @return type.
     */
    @NotNull
    public MessageType getType() {
        return type;
    }

    /**
     * Gets the payload
     *
     * @return payload.
     */
    @NotNull
    public String getPayload() {
        return payload;
    }

    /**
     * Die verschiedenen Arten der Message.
     */
    public enum MessageType {
        WHO_IS_IN, MESSAGE, LOGOUT, HELP
    }
}