package me.wavelength.baseclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.status.server.S00PacketServerInfo;

import java.util.ArrayList;
import java.util.Objects;

public class HypixelStatus {
    private boolean inGame = false;
    private int gameKills = 0;
    private int sessionKills = 0;
    private int skywarsLevel = 0;
    private int skywarsSoloKills = 0;
    private long timeJoined = -1;
    private int skywarsSoloWins = 0;
    private int skywarsCoins = 0;
    private int skywarsDoubleKills = 0;
    private int skywarsDoubleWins = 0;
    private String guild = "null";
    private String rank = "Default";
    private int lastKills = 0;

    public void scoreBoardEvent(String line, ArrayList<String> lines) {
        Boolean totallyGame = false;
        for (String text : lines) {
            String unformattedString = text;

            String formattedString = unformattedString.replaceAll("[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\s]", "").replaceAll("§f", "")
                    .replaceAll("§b", "").replaceAll("§6", "").replaceAll("§a", "").replaceAll("§a", "")
                    .replaceAll("§7", "").replaceAll("§e", "").replaceAll("§2", "").replaceAll(",", "");

            String[] split = formattedString.split(": ");

            if (split.length > 1) {
                String value = split[1];
                if (Objects.equals(line, "§e§lSKYWARS")) {
                    switch (split[0]) {
                        case "Your Level":
                            inGame = false;
                            skywarsLevel = Integer.parseInt(value.replace("⋆", ""));
                            break;
                        case "Solo Kills":
                            skywarsSoloKills = Integer.parseInt(value);
                            break;
                        case "Solo Wins":
                            skywarsSoloWins = Integer.parseInt(value);
                            break;
                        case "Double Wins":
                            skywarsDoubleWins = Integer.parseInt(value);
                            break;
                        case "Double Kills":
                            skywarsDoubleKills = Integer.parseInt(value);
                            break;
                        case "Coins":
                            skywarsCoins = Integer.parseInt(value);
                            break;
                        case "Kills":
                            totallyGame = true;
                            if(lastKills != Integer.parseInt(value))
                                //killEvent(Integer.parseInt(value) - lastKills);
                            lastKills = Integer.parseInt(value);
                            break;
                        case "Map":
                            totallyGame = true;
                            break;
                    }
                }
            }
        }
        this.inGame = totallyGame;
        if(!inGame){
            //DiscordRPCUtil.killCount = 0;
            //this.gameKills = 0;
        }
    }

    public void killEvent(int killChange) {
        this.gameKills += killChange;
        this.sessionKills += killChange;
        System.out.println(killChange);
        //Diablo.getInstance().addNotification(new Notification("You got a Kill","Detected Kill",1500, NotificationType.INFORMATION));
        this.sessionKills++;
    }

    public void reset(){
        this.gameKills = 0;
        this.sessionKills = 0;
        this.skywarsDoubleKills = 0;
        this.skywarsSoloKills = 0;
        this.skywarsSoloWins = 0;
        this.skywarsDoubleWins = 0;
        this.skywarsCoins = 0;
        this.timeJoined = System.currentTimeMillis();
        System.out.println("Resetting Hypixel Stats");
    }

    public void onPacket(Packet p){
        if(p instanceof S00PacketServerInfo){
            reset();
        }
    }

    public int getSessionKills() {
        return sessionKills;
    }

    public int getGameKills() {
        return gameKills;
    }

    public int getSkywarsLevel() {
        return skywarsLevel;
    }

    public int getSkywarsSoloKills() {
        return skywarsSoloKills;
    }

    public int getSkywarsSoloWins() {
        return skywarsSoloWins;
    }

    public int getSkywarsCoins() {
        return skywarsCoins;
    }

    public int getSkywarsDoubleKills() {
        return skywarsDoubleKills;
    }

    public int getSkywarsDoubleWins() {
        return skywarsDoubleWins;
    }

    public String getSessionLengthString() {
        long totalSeconds = (System.currentTimeMillis() - this.timeJoined) / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return (hours > 0 ? (hours + "h, ") : "") + minutes + "m, "+ seconds + "s";
    }

    public long getSessionLength(){
        return System.currentTimeMillis() - this.timeJoined;
    }
}
