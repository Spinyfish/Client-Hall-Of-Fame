package net.minecraft.realms;

public class RealmsServerPing
{
    public volatile String nrOfPlayers;
    public volatile long lastPingSnapshot;
    public volatile String playerList;
    
    public RealmsServerPing() {
        this.nrOfPlayers = "0";
        this.lastPingSnapshot = 0L;
        this.playerList = "";
    }
}
