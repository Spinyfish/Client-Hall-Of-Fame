package me.jinthium.scripting.api.bindings;

import me.jinthium.scripting.api.objects.ScriptPlayerCapabilites;

public class PacketBinding {

    public final int C00Handshake = 0;
    public final int C00PacketLoginStart = 1;
    public final int C00PacketServerQuery = 2;
    public final int C01PacketPing = 3;

    public final int C0APacketAnimation = 36;
    public final int C0BPacketEntityAction = 37;
    public final int C0CPacketInput = 38;
    public final int C0DPacketCloseWindow = 39;

    public final int C0EPacketClickWindow = 4;
    public final int C0FPacketConfirmTransaction = 5;
    public final int C00PacketKeepAlive = 6;
    public final int C01PacketChatMessage = 7;
    public final int C02PacketUseEntity = 8;
    public final int C03PacketPlayer = 9;
    public final int C04PacketPlayerPosition = 10;
    public final int C05PacketPlayerLook = 11;
    public final int C06PacketPlayerPosLook = 12;
    public final int C07PacketPlayerDigging = 13;
    public final int C08PacketPlayerBlockPlacement = 14;
    public final int C09PacketHeldItemChange = 15;
    public final int C13PacketPlayerAbilities = 19;
    public final int C14PacketTabComplete = 20;
    public final int C15PacketClientSettings = 21;
    public final int C16PacketClientStatus = 22;
    public final int C17PacketCustomPayload = 23;
    public final int C18PacketSpectate = 24;
    public final int C19PacketResourcePackStatus = 25;

    public final int S00PacketKeepAlive = 26;
    public final int S2DPacketOpenWindow = 27;
    public final int S2EPacketCloseWindow = 28;
    public final int S2FPacketSetSlot = 29;
    public final int S3FPacketCustomPayload = 30;
    public final int S08PacketPlayerPosLook = 31;
    public final int S09PacketHeldItemChange = 32;
    public final int S12PacketEntityVelocity = 33;
    public final int S27PacketExplosion = 34;
    public final int S32PacketConfirmTransaction = 35;


    public ScriptPlayerCapabilites newPlayerCapabilities() {
        return new ScriptPlayerCapabilites();
    }

}
