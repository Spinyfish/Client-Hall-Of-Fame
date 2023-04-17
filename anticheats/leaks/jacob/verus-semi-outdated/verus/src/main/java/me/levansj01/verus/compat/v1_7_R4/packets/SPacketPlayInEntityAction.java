package me.levansj01.verus.compat.v1_7_R4.packets;

import me.levansj01.verus.compat.packets.VPacketPlayInEntityAction;
import net.minecraft.server.v1_7_R4.PacketPlayInEntityAction;

public class SPacketPlayInEntityAction
extends VPacketPlayInEntityAction {
    public void accept(PacketPlayInEntityAction packetPlayInEntityAction) {
        switch (packetPlayInEntityAction.d()) {
            case 0: {
                this.action = VPacketPlayInEntityAction.PlayerAction.OPEN_INVENTORY;
                break;
            }
            case 1: {
                this.action = VPacketPlayInEntityAction.PlayerAction.START_SNEAKING;
                break;
            }
            case 2: {
                this.action = VPacketPlayInEntityAction.PlayerAction.STOP_SNEAKING;
                break;
            }
            case 3: {
                this.action = VPacketPlayInEntityAction.PlayerAction.STOP_SLEEPING;
                break;
            }
            case 4: {
                this.action = VPacketPlayInEntityAction.PlayerAction.START_SPRINTING;
                break;
            }
            case 5: {
                this.action = VPacketPlayInEntityAction.PlayerAction.STOP_SPRINTING;
                break;
            }
            case 6: {
                this.action = VPacketPlayInEntityAction.PlayerAction.RIDING_JUMP;
            }
        }
        this.value = packetPlayInEntityAction.e();
    }
}

