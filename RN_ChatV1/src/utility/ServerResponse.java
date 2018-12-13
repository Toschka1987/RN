package utility;

public class ServerResponse {
    // COMMANDS FROM SERVER TO CLIENT
    private static final String SERVER_COMMAND_PREFIX = ">>";
    public static final String GIVE_USERNAME = SERVER_COMMAND_PREFIX + "300";

    // SERVER COMMANDS THAT FORCE ALTERATION OF CLIENT
    public static final String FORCE_DISCONNECT = SERVER_COMMAND_PREFIX + "601_";

    // POSITIVE SERVER RESPONSES
    public static final String HANDSHAKE_OK = SERVER_COMMAND_PREFIX + "100_";
    public static final String LOGGED_IN = SERVER_COMMAND_PREFIX + "101_";
    public static final String VALID_USERNAME = SERVER_COMMAND_PREFIX + "102_";
    public static final String IN_ROOM = SERVER_COMMAND_PREFIX + "103_";
    public static final String MESSAGE_RECEIVED = SERVER_COMMAND_PREFIX + "104_";
    public static final String CLIENT_CREATED = SERVER_COMMAND_PREFIX + "105_";

    // NEGATIVE SERVER RESPONSES
    public static final String INVALID_USERNAME = SERVER_COMMAND_PREFIX + "000_";
    public static final String INVALID_CLIENT = SERVER_COMMAND_PREFIX + "001_";
    public static final String INVALID_ADDRESS = SERVER_COMMAND_PREFIX + "002_";
    public static final String INVALID_PORT = SERVER_COMMAND_PREFIX + "003_";
    public static final String INVALID_SERVER = SERVER_COMMAND_PREFIX + "004_";
    public static final String INVALID_ROOM = SERVER_COMMAND_PREFIX + "005_";
    public static final String INVALID_CONNCETION = SERVER_COMMAND_PREFIX + "006_";
    public static final String INVALID_MESSAGE = SERVER_COMMAND_PREFIX + "007_";

    private ServerResponse() {
    }
}