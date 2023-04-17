package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;
import java.util.*;

public abstract class VPacketPlayOutUpdateAttributes extends VPacket
{
    protected final List snapshots;
    private static final int count;
    protected int entityId;
    
    static {
        count = VPacket.count();
    }
    
    public int getEntityId() {
        return this.entityId;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutUpdateAttributes.count;
    }
    
    public List getSnapshots() {
        return this.snapshots;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public VPacketPlayOutUpdateAttributes() {
        this.snapshots = new ArrayList();
    }
    
    public static class Snapshot
    {
        private final String name;
        private final List modifiers;
        private final double baseValue;
        
        public String getName() {
            return this.name;
        }
        
        public Snapshot(final String name, final double baseValue) {
            this.modifiers = new ArrayList();
            this.name = name;
            this.baseValue = baseValue;
        }
        
        public List getModifiers() {
            return this.modifiers;
        }
        
        public double getBaseValue() {
            return this.baseValue;
        }
        
        public static class Modifier
        {
            private final int operation;
            private final double amount;
            private final String name;
            
            public String getName() {
                return this.name;
            }
            
            public Modifier(final String name, final double amount, final int operation) {
                this.name = name;
                this.amount = amount;
                this.operation = operation;
            }
            
            public double getAmount() {
                return this.amount;
            }
            
            public int getOperation() {
                return this.operation;
            }
        }
    }
}
