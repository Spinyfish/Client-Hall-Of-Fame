package me.jinthium.straight.impl.components;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import static net.minecraft.network.play.client.C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT;

public class BadPacketsComponent extends Component {
    private static boolean slot, attack, swing, block, inventory;

    public static boolean bad() {
        return bad(true, true, true, true, true);
    }

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if(event.getPacketState() == PacketEvent.PacketState.SENDING){
            final Packet<?> packet = event.getPacket();

            if (packet instanceof C09PacketHeldItemChange) {
                slot = true;
            } else if (packet instanceof C0APacketAnimation) {
                swing = true;
            } else if (packet instanceof C02PacketUseEntity) {
                attack = true;
            } else if (packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging) {
                block = true;
            } else if (packet instanceof C0EPacketClickWindow ||
                    (packet instanceof C16PacketClientStatus && ((C16PacketClientStatus) packet).getStatus() == OPEN_INVENTORY_ACHIEVEMENT) ||
                    packet instanceof C0DPacketCloseWindow) {
                inventory = true;
            } else if (packet instanceof C03PacketPlayer) {
                reset();
            }
        }
    };

    public static boolean bad(final boolean slot, final boolean attack, final boolean swing, final boolean block, final boolean inventory) {
        return (BadPacketsComponent.slot && slot) ||
                (BadPacketsComponent.attack && attack) ||
                (BadPacketsComponent.swing && swing) ||
                (BadPacketsComponent.block && block) ||
                (BadPacketsComponent.inventory && inventory);
    }

    public static void reset() {
        slot = false;
        swing = false;
        attack = false;
        block = false;
        inventory = false;
    }
}
