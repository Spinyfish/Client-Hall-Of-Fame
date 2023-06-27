package net.minecraft.network.play.server;

import net.minecraft.network.play.*;
import net.minecraft.util.*;
import java.io.*;
import net.minecraft.network.*;

public class S45PacketTitle implements Packet<INetHandlerPlayClient>
{
    private Type type;
    private IChatComponent message;
    private int fadeInTime;
    private int displayTime;
    private int fadeOutTime;
    
    public S45PacketTitle() {
    }
    
    public S45PacketTitle(final Type type, final IChatComponent message) {
        this(type, message, -1, -1, -1);
    }
    
    public S45PacketTitle(final int fadeInTime, final int displayTime, final int fadeOutTime) {
        this(Type.TIMES, null, fadeInTime, displayTime, fadeOutTime);
    }
    
    public S45PacketTitle(final Type type, final IChatComponent message, final int fadeInTime, final int displayTime, final int fadeOutTime) {
        this.type = type;
        this.message = message;
        this.fadeInTime = fadeInTime;
        this.displayTime = displayTime;
        this.fadeOutTime = fadeOutTime;
    }
    
    @Override
    public void readPacketData(final PacketBuffer buf) throws IOException {
        this.type = buf.readEnumValue(Type.class);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE) {
            this.message = buf.readChatComponent();
        }
        if (this.type == Type.TIMES) {
            this.fadeInTime = buf.readInt();
            this.displayTime = buf.readInt();
            this.fadeOutTime = buf.readInt();
        }
    }
    
    @Override
    public void writePacketData(final PacketBuffer buf) throws IOException {
        buf.writeEnumValue(this.type);
        if (this.type == Type.TITLE || this.type == Type.SUBTITLE) {
            buf.writeChatComponent(this.message);
        }
        if (this.type == Type.TIMES) {
            buf.writeInt(this.fadeInTime);
            buf.writeInt(this.displayTime);
            buf.writeInt(this.fadeOutTime);
        }
    }
    
    @Override
    public void processPacket(final INetHandlerPlayClient handler) {
        handler.handleTitle(this);
    }
    
    public Type getType() {
        return this.type;
    }
    
    public IChatComponent getMessage() {
        return this.message;
    }
    
    public int getFadeInTime() {
        return this.fadeInTime;
    }
    
    public int getDisplayTime() {
        return this.displayTime;
    }
    
    public int getFadeOutTime() {
        return this.fadeOutTime;
    }
    
    public enum Type
    {
        TITLE, 
        SUBTITLE, 
        TIMES, 
        CLEAR, 
        RESET;
        
        public static Type byName(final String name) {
            for (final Type s45packettitle$type : values()) {
                if (s45packettitle$type.name().equalsIgnoreCase(name)) {
                    return s45packettitle$type;
                }
            }
            return Type.TITLE;
        }
        
        public static String[] getNames() {
            final String[] astring = new String[values().length];
            int i = 0;
            for (final Type s45packettitle$type : values()) {
                astring[i++] = s45packettitle$type.name().toLowerCase();
            }
            return astring;
        }
    }
}
