package best.azura.irc.core.entities;

import best.azura.irc.utils.Wrapper;

/**
 * This class is used to save Data about an IRC User with
 * every needed Data about him.
 */
public class User {

    // The IRC Name of the User.
    String username;

    // The In-Game Name of the User.
    String minecraftName;

    /**
     * Constructor for the IRC-User.
     * @param username the IRC-Username.
     * @param minecraftName the Minecraft-Username.
     */
    public User(String username, String minecraftName) {
        this.username = username;
        this.minecraftName = minecraftName;
    }

    /**
     * Retrieve the Minecraft In-Game Name of the IRC-User.
     * @return In-Game Name.
     */
    public String getMinecraftName() {
        return minecraftName;
    }

    /**
     * Change the Minecraft In-Game Name of the IRC-User.
     * @param minecraftName the new Minecraft Name.
     */
    public void setMinecraftName(String minecraftName) {
        this.minecraftName = minecraftName;
    }

    /**
     * Retrieve the Username of the IRC-User.
     * @return the Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get a formatted Version.
     * @return formatted.
     */
    public String getFormattedIGN() {
        return Wrapper.getPrefixCode() + "7(" +  getMinecraftName() + Wrapper.getPrefixCode() + "7)";
    }
}
