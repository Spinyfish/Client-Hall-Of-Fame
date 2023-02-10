/**
 * @project Myth
 * @author Skush/Duzey
 * @at 05.08.2022
 */
package dev.myth.api.interfaces;

import dev.myth.api.logger.Logger;
import dev.myth.api.manager.Manager;
import dev.myth.main.ClientMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public interface IMethods {
    Minecraft MC = Minecraft.getMinecraft();

    /** Minecraft */
    default WorldClient getWorld() {
        return MC.theWorld;
    }
    default EntityPlayerSP getPlayer() {
        return MC.thePlayer;
    }
    default GameSettings getGameSettings() {
        return MC.gameSettings;
    }
    default double getX() {
        return getPlayer().posX;
    }
    default double getY() {
        return getPlayer().posY;
    }
    default double getZ() {
        return getPlayer().posZ;
    }
    default float getPitch() {
        return getPlayer().rotationPitch;
    }
    default float getYaw() {
        return getPlayer().rotationYaw;
    }

    /** Client */
    default void sendPacketUnlogged(final Packet<? extends INetHandler> packet) {
        MC.getNetHandler().getNetworkManager().sendPacket(packet, null);
    }
    
    default void sendPacket(final Packet<? extends INetHandler> packet) {
        MC.thePlayer.sendQueue.addToSendQueue(packet);
    }

    default void setTimer(final float timer) {
        MC.timer.timerSpeed = timer;
    }

    default void doLog(final Object obj) {
        Logger.doLog(obj);
    }

    default void doLog(final Logger.Type type, final Object obj) {
        Logger.doLog(type, obj);
    }

    default <T extends Manager> T getManager(final Class<T> clazz) {
        return (T) ClientMain.INSTANCE.manager.getManager(clazz);
    }
}
