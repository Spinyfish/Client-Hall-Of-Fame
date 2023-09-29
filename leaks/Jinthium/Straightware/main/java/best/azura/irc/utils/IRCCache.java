package best.azura.irc.utils;

import best.azura.irc.core.entities.User;

import java.util.HashMap;

/**
 * Data cache for the IRC.
 */
public class IRCCache {

    // Use the Username as KEY and the IRC-User Data as Value.
    private final HashMap<String, User> ircUserHashMap;

    /**
     * Constructor for the IRC-Cache.
     */
    public IRCCache() {
        ircUserHashMap = new HashMap<>();
    }

    /**
     * Retrieve the MapList of the User and his IRC-User.
     * @return MapList of the Data.
     */
    public HashMap<String, User> getIrcUserHashMap() {
        return ircUserHashMap;
    }
}
