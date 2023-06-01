package frapppyz.cutefurry.pics.event.impl;

import frapppyz.cutefurry.pics.event.Event;
import net.minecraft.network.Packet;

public class ReceivePacket extends Event {
    public boolean isCancelled;
    private Packet p;

    public ReceivePacket(Packet p){
        this.p = p;
    }

    public Packet getPacket(){return p;}
    public void setPacket(Packet p){this.p = p;}
}
