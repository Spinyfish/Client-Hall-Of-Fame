package de.verschwiegener.atero.util.account;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Account {

    private String email;
    private String password;
    private String username;
    private String UUID;
    private boolean lastLoginSuccess;
    private String[] bannedServer;
    private ResourceLocation location;
    private long timeStamp;

    public Account(String email, String password, String username, String UUID, boolean lastLoginSuccess, String[] bannedServer, String timestamp) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.UUID = UUID;
        this.lastLoginSuccess = lastLoginSuccess;
        if (bannedServer.length != 1) {
            this.bannedServer = bannedServer;
        } else {
            this.bannedServer = new String[]{};
        }
        timeStamp = 0;
        this.timeStamp = Long.valueOf(timestamp).longValue();
        //location = Util.getSkin(username);
        if (this.UUID != null && !this.UUID.isEmpty()) {
            try {
                BufferedImage image = ImageIO.read(new URL("https://crafatar.com/avatars/" + this.UUID).openStream());
                location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(username, new DynamicTexture(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
        this.username = "";
        this.UUID = "";
        this.lastLoginSuccess = false;
        timeStamp = 0;
        this.bannedServer = new String[]{};
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        //location = Util.getSkin(username);
    }

    public boolean isLastLoginSuccess() {
        return lastLoginSuccess;
    }

    public void setLastLoginSuccess(boolean lastLoginSuccess) {
        this.lastLoginSuccess = lastLoginSuccess;
        timeStamp = System.currentTimeMillis();
    }

    public String[] getBannedServer() {
        return bannedServer;
    }

    public void setBannedServer(String[] bannedServer) {
        this.bannedServer = bannedServer;
    }

    public void addBannedServer(String ServerIP) {
        bannedServer = Arrays.copyOf(bannedServer, bannedServer.length + 1);
        bannedServer[bannedServer.length - 1] = ServerIP;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String uUID) {
        UUID = uUID;
        if (this.UUID != null && !this.UUID.isEmpty()) {
            try {
                BufferedImage image = ImageIO.read(new URL("https://crafatar.com/avatars/" + this.UUID).openStream());
                location = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(username, new DynamicTexture(image));
                System.out.println("Location: " + location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

}
