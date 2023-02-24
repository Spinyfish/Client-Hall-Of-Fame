package me.kansio.client.utils.moshi;

import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.utils.network.TimedPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;

import java.util.function.Consumer;

/**
 * @author moshi
 */
public interface IPacketUtils {

    default void sendPacket(Packet<?> p) {
        Minecraft.getMinecraft().getNetHandler().addToSendQueue(p);
    }

    default void sendPacketSilent(Packet<?> p) {
        Minecraft.getMinecraft().getNetHandler().addToSendQueueNoEvent(p);
    }

    default void sendPacket(Packet<?> p, int rep) {
        for (int a = 0; a < rep; a++) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(p);
        }
    }

    default void sendPacketSilent(Packet<?> p, int rep) {
        for (int a = 0; a < rep; a++) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueueNoEvent(p);
        }
    }

    // Client

    default void onCPacketAnimation(PacketEvent e, Consumer<C0APacketAnimation> consumer) {
        if (e.getPacket() instanceof C0APacketAnimation) {
            consumer.accept((C0APacketAnimation) e.getPacket());
        }
    }

    default void onCPacketInput(PacketEvent e, Consumer<C0CPacketInput> consumer) {
        if (e.getPacket() instanceof C0CPacketInput) {
            consumer.accept((C0CPacketInput) e.getPacket());
        }
    }

    default void onCClickWindow(PacketEvent e, Consumer<C0EPacketClickWindow> consumer) {
        if (e.getPacket() instanceof C0EPacketClickWindow) {
            consumer.accept((C0EPacketClickWindow) e.getPacket());
        }
    }

    default void onCPacketChat(PacketEvent e, Consumer<C01PacketChatMessage> consumer) {
        if (e.getPacket() instanceof C01PacketChatMessage) {
            consumer.accept((C01PacketChatMessage) e.getPacket());
        }
    }

    default void onCUseEntity(PacketEvent e, Consumer<C02PacketUseEntity> consumer) {
        if (e.getPacket() instanceof C02PacketUseEntity) {
            consumer.accept((C02PacketUseEntity) e.getPacket());
        }
    }

    default void onCPacketDigging(PacketEvent e, Consumer<C07PacketPlayerDigging> consumer) {
        if (e.getPacket() instanceof C07PacketPlayerDigging) {
            consumer.accept((C07PacketPlayerDigging) e.getPacket());
        }
    }

    default void onCBlockPlacement(PacketEvent e, Consumer<C08PacketPlayerBlockPlacement> consumer) {
        if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            consumer.accept((C08PacketPlayerBlockPlacement) e.getPacket());
        }
    }

    default void onCHeldItem(PacketEvent e, Consumer<C09PacketHeldItemChange> consumer) {
        if (e.getPacket() instanceof C09PacketHeldItemChange) {
            consumer.accept((C09PacketHeldItemChange) e.getPacket());
        }
    }

    default void onCPlayerCapabilties(PacketEvent e, Consumer<C13PacketPlayerAbilities> consumer) {
        if (e.getPacket() instanceof C13PacketPlayerAbilities) {
            consumer.accept((C13PacketPlayerAbilities) e.getPacket());
        }
    }

    default void onCCloseWindow(PacketEvent e, Consumer<C0DPacketCloseWindow> consumer) {
        if (e.getPacket() instanceof C0DPacketCloseWindow) {
            consumer.accept((C0DPacketCloseWindow) e.getPacket());
        }
    }

    default void onCPacketPlayer(PacketEvent e, Consumer<C03PacketPlayer> consumer) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            consumer.accept((C03PacketPlayer) e.getPacket());
        }
    }

    default void onCTransaction(PacketEvent e, Consumer<C0FPacketConfirmTransaction> consumer) {
        if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
            consumer.accept((C0FPacketConfirmTransaction) e.getPacket());
        }
    }

    default void onCKeepAlive(PacketEvent e, Consumer<C00PacketKeepAlive> consumer) {
        if (e.getPacket() instanceof C00PacketKeepAlive) {
            consumer.accept((C00PacketKeepAlive) e.getPacket());
        }
    }

    default void onCEntityAction(PacketEvent e, Consumer<C0BPacketEntityAction> consumer) {
        if (e.getPacket() instanceof C0BPacketEntityAction) {
            consumer.accept((C0BPacketEntityAction) e.getPacket());
        }
    }

    // Server

    default void onSPosLook(PacketEvent e, Consumer<S08PacketPlayerPosLook> consumer) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            consumer.accept((S08PacketPlayerPosLook) e.getPacket());
        }
    }

    default void onSVelocity(PacketEvent e, Consumer<S12PacketEntityVelocity> consumer) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            consumer.accept((S12PacketEntityVelocity) e.getPacket());
        }
    }

    default void onSRespawn(PacketEvent e, Consumer<S07PacketRespawn> consumer) {
        if (e.getPacket() instanceof S07PacketRespawn) {
            consumer.accept((S07PacketRespawn) e.getPacket());
        }
    }

    default void onSOpenWindow(PacketEvent e, Consumer<S2DPacketOpenWindow> consumer) {
        if (e.getPacket() instanceof S2DPacketOpenWindow) {
            consumer.accept((S2DPacketOpenWindow) e.getPacket());
        }
    }

    default void onSCloseWindow(PacketEvent e, Consumer<S2EPacketCloseWindow> consumer) {
        if (e.getPacket() instanceof S2EPacketCloseWindow) {
            consumer.accept((S2EPacketCloseWindow) e.getPacket());
        }
    }

    // Utils

    default TimedPacket create(Packet<?> p) {
        return new TimedPacket(p, System.currentTimeMillis());
    }
}
