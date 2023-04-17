package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;

public abstract class VPacketPlayInEntityAction extends VPacket
{
    private static final int count;
    protected PlayerAction action;
    protected int value;
    
    public int getValue() {
        return this.value;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayInEntityAction.count;
    }
    
    static {
        count = VPacket.count();
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public PlayerAction getAction() {
        return this.action;
    }
    
    public enum PlayerAction
    {
        STOP_SPRINTING("STOP_SPRINTING", 4, Type.SPRINT, false), 
        START_SNEAKING("START_SNEAKING", 0, Type.SNEAK, true), 
        START_SPRINTING("START_SPRINTING", 3, Type.SPRINT, true);
        
        private final Type type;
        
        RIDING_JUMP("RIDING_JUMP", 5, true), 
        STOP_SLEEPING("STOP_SLEEPING", 2, false);
        
        private final boolean value;
        
        STOP_SNEAKING("STOP_SNEAKING", 1, Type.SNEAK, false), 
        OPEN_INVENTORY("OPEN_INVENTORY", 6, true);
        
        private static final PlayerAction[] $VALUES;
        
        START_FALL_FLYING("START_FALL_FLYING", 7, true);
        
        private PlayerAction(final String s, final int n, final Type type, final boolean value) {
            this.type = type;
            this.value = value;
        }
        
        private static PlayerAction[] $values() {
            return new PlayerAction[] { PlayerAction.START_SNEAKING, PlayerAction.STOP_SNEAKING, PlayerAction.STOP_SLEEPING, PlayerAction.START_SPRINTING, PlayerAction.STOP_SPRINTING, PlayerAction.RIDING_JUMP, PlayerAction.OPEN_INVENTORY, PlayerAction.START_FALL_FLYING };
        }
        
        public boolean isValue() {
            return this.value;
        }
        
        static {
            $VALUES = $values();
        }
        
        public Type getType() {
            return this.type;
        }
        
        public boolean isSprint() {
            return this.type == Type.SPRINT;
        }
        
        private PlayerAction(final String s, final int n, final boolean b) {
            this(s, n, Type.OTHER, b);
        }
        
        public boolean isSneak() {
            return this.type == Type.SNEAK;
        }
        
        public enum Type
        {
            SNEAK("SNEAK", 0), 
            SPRINT("SPRINT", 1), 
            OTHER("OTHER", 2);
            
            private static final Type[] $VALUES;
            
            static {
                $VALUES = $values();
            }
            
            private static Type[] $values() {
                return new Type[] { Type.SNEAK, Type.SPRINT, Type.OTHER };
            }
            
            private Type(final String s, final int n) {
            }
        }
    }
}
