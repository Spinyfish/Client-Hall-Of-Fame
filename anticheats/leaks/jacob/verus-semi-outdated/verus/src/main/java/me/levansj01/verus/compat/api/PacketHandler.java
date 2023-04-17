package me.levansj01.verus.compat.api;

import me.levansj01.verus.util.java.*;
import java.util.*;
import me.levansj01.verus.compat.packets.*;

/*
*  Yeah we know its shit code, dont judge us.
*  - Jacob
*/

public interface PacketHandler
{
    default void handle(final VPacketPlayInCustomPayload vPacketPlayInCustomPayload) {
    }
    
    default void handle(final VPacketPlayOutEntityMetadata vPacketPlayOutEntityMetadata) {
    }
    
    default void handle(final VPacketPlayInTeleportAccept vPacketPlayInTeleportAccept) {
    }
    
    default void handle(final VPacketPlayOutEntityDestroy vPacketPlayOutEntityDestroy) {
    }
    
    default void handle(final VPacketPlayInCloseWindow vPacketPlayInCloseWindow) {
    }
    
    default void handle(final VPacketPlayOutExplosion vPacketPlayOutExplosion) {
    }
    
    default void handle(final VPacketPlayOutGameStateChange vPacketPlayOutGameStateChange) {
    }
    
    default void handle(final VPacketPlayInKeepAlive vPacketPlayInKeepAlive) {
    }
    
    default void handle(final VPacketPlayInUseEntity vPacketPlayInUseEntity) {
    }
    
    default void handle(final VPacketPlayInBlockDig vPacketPlayInBlockDig) {
    }
    
    default void handle(final VPacketPlayOutPosition vPacketPlayOutPosition) {
    }
    
    default void handle(final VPacketPlayOutEntity vPacketPlayOutEntity) {
    }
    
    default void handle(final VPacketPlayOutEntityEffect vPacketPlayOutEntityEffect) {
    }
    
    default void handle(final VPacketPlayOutMapChunkBulk vPacketPlayOutMapChunkBulk) {
    }
    
    default void handle(final VPacketPlayInFlying vPacketPlayInFlying) {
    }
    
    default void handle(final VPacketPlayOutSpawnEntityLiving vPacketPlayOutSpawnEntityLiving) {
    }
    
    default void handleIn(final Transactionable transactionable) {
    }
    
    default void handle(final VPacketPlayInSetCreativeSlot vPacketPlayInSetCreativeSlot) {
    }
    
    default void handle(final VPacketPlayInChat vPacketPlayInChat) {
    }
    
    default void handle(final VPacketPlayInEntityAction vPacketPlayInEntityAction) {
    }
    
    default void handle(final VPacketPlayInArmAnimation vPacketPlayInArmAnimation) {
    }
    
    default void handle(final VPacketPlayOutRespawn vPacketPlayOutRespawn) {
    }
    
    default void handle(final VPacketPlayInWindowClick vPacketPlayInWindowClick) {
    }
    
    default void handle(final VPacketPlayOutSpawnEntity vPacketPlayOutSpawnEntity) {
    }
    
    default void handle(final VPacketPlayOutEntityTeleport vPacketPlayOutEntityTeleport) {
    }
    
    default void handleOut(final Transactionable transactionable) {
    }
    
    default void handle(final VPacketPlayInHeldItemSlot vPacketPlayInHeldItemSlot) {
    }
    
    default void handle(final VPacketPlayOutMapChunk vPacketPlayOutMapChunk) {
    }
    
    default void handle(final VPacketPlayOutNamedEntitySpawn vPacketPlayOutNamedEntitySpawn) {
    }
    
    default void handle(final VPacketPlayOutAttachEntity vPacketPlayOutAttachEntity) {
    }
    
    default void handle(final VPacketPlayOutAbilities vPacketPlayOutAbilities) {
    }
    
    default void handle(final VPacketPlayOutUpdateAttributes vPacketPlayOutUpdateAttributes) {
    }
    
    default void handle(final VPacketPlayInAbilities vPacketPlayInAbilities) {
    }
    
    default void handle(final VPacketPlayOutOpenWindow vPacketPlayOutOpenWindow) {
    }
    
    default void handle(final VPacketPlayOutMultiBlockChange vPacketPlayOutMultiBlockChange) {
    }
    
    default void handle(final VPacketPlayOutRemoveEntityEffect vPacketPlayOutRemoveEntityEffect) {
    }
    
    default void handle(final VPacketPlayInSteerVehicle vPacketPlayInSteerVehicle) {
    }
    
    default void handle(final VPacketPlayOutUnloadChunk vPacketPlayOutUnloadChunk) {
    }
    
    default void handle(final VPacketPlayOutKeepAlive vPacketPlayOutKeepAlive) {
    }
    
    default void handle(final VPacketPlayOutBlockChange vPacketPlayOutBlockChange) {
    }
    
    default void handle(final VPacketPlayInUseItem vPacketPlayInUseItem) {
    }
    
    default void handle(final VPacketPlayInVehicleMove vPacketPlayInVehicleMove) {
    }
    
    default int[] parse() {
        final LinkedList<Class> list = new LinkedList<Class>();
        int length = this.getClass().getDeclaredMethods().length;
        final int[] array = new int[list.size()];
        for (final Class clazz : list) {
            final int[] array2 = array;
            final int n = 0;
            ++length;
            array2[n] = (int)SafeReflection.getLocalField(clazz, null, "count");
        }
        return array;
    }
    
    default void handle(final VPacketPlayOutSpawnPosition vPacketPlayOutSpawnPosition) {
    }
    
    default void handle(final VPacketPlayOutSetSlot vPacketPlayOutSetSlot) {
    }
    
    default void handle(final VPacketPlayOutEntityVelocity vPacketPlayOutEntityVelocity) {
    }
    
    default void handle(final VPacketPlayInClientCommand vPacketPlayInClientCommand) {
    }
    
    default void handle(final VPacketPlayInBlockPlace vPacketPlayInBlockPlace) {
    }
}
