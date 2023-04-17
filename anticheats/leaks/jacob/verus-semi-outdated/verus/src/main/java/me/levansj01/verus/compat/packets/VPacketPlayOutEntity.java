package me.levansj01.verus.compat.packets;

import me.levansj01.verus.compat.*;
import me.levansj01.verus.compat.api.*;
import me.levansj01.verus.data.transaction.tracker.*;

public abstract class VPacketPlayOutEntity extends VPacket
{
    protected byte pitch;
    protected boolean pos;
    protected int z;
    protected int x;
    private static final int count;
    protected int y;
    protected boolean ground;
    protected byte yaw;
    protected boolean look;
    protected int id;
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: invokevirtual   me/levansj01/verus/compat/NMSManager.getServerVersion:()Lme/levansj01/verus/compat/ServerVersion;
        //     6: getstatic       me/levansj01/verus/compat/ServerVersion.v1_8_R3:Lme/levansj01/verus/compat/ServerVersion;
        //     9: invokevirtual   me/levansj01/verus/compat/ServerVersion.after:(Lme/levansj01/verus/compat/ServerVersion;)Z
        //    12: ifeq            18
        //    15: ldc2_w          4096.0
        //    18: invokestatic    me/levansj01/verus/compat/packets/VPacketPlayOutEntity.count:()I
        //    21: putstatic       me/levansj01/verus/compat/packets/VPacketPlayOutEntity.count:I
        //    24: return         
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Inconsistent stack size at #0018 (coming from #0015).
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2183)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:211)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at us.deathmarine.luyten.FileSaver.doSaveJarDecompiled(FileSaver.java:192)
        //     at us.deathmarine.luyten.FileSaver.access$300(FileSaver.java:45)
        //     at us.deathmarine.luyten.FileSaver$4.run(FileSaver.java:112)
        //     at java.lang.Thread.run(Unknown Source)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public boolean moves() {
        return this.pos && (this.x != 0 || this.y != 0 || this.z != 0);
    }
    
    public boolean isGround() {
        return this.ground;
    }
    
    @Override
    public int ordinal() {
        return VPacketPlayOutEntity.count;
    }
    
    public byte getPitch() {
        return this.pitch;
    }
    
    public boolean isLook() {
        return this.look;
    }
    
    public IServerLocation move() {
        return (IServerLocation)new ServerLocation(this.x, this.y, this.z);
    }
    
    @Override
    public String toString() {
        return "VPacketPlayOutEntity(id=" + this.getId() + ", x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", pitch=" + this.getPitch() + ", yaw=" + this.getYaw() + ", ground=" + this.isGround() + ", look=" + this.isLook() + ", pos=" + this.isPos() + ")";
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public byte getYaw() {
        return this.yaw;
    }
    
    public int getX() {
        return this.x;
    }
    
    public boolean isPos() {
        return this.pos;
    }
    
    @Override
    public void handle(final PacketHandler packetHandler) {
        packetHandler.handle(this);
    }
    
    public int getY() {
        return this.y;
    }
    
    public PacketLocation move(final PacketLocation packetLocation) {
        return packetLocation.add(this.x / 32.0, this.y / 32.0, this.z / 32.0);
    }
}
