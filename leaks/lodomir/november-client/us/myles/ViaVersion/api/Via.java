/*
 * Decompiled with CFR 0.152.
 */
package us.myles.ViaVersion.api;

import io.netty.buffer.ByteBuf;
import java.util.SortedSet;
import java.util.UUID;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;

@Deprecated
public class Via<T>
implements ViaAPI<T> {
    private static final ViaAPI INSTANCE = new Via();

    private Via() {
    }

    @Deprecated
    public static ViaAPI getAPI() {
        return INSTANCE;
    }

    @Override
    public int getPlayerVersion(T player) {
        return com.viaversion.viaversion.api.Via.getAPI().getPlayerVersion(player);
    }

    @Override
    public int getPlayerVersion(UUID uuid) {
        return com.viaversion.viaversion.api.Via.getAPI().getPlayerVersion(uuid);
    }

    @Override
    public boolean isInjected(UUID playerUUID) {
        return com.viaversion.viaversion.api.Via.getAPI().isInjected(playerUUID);
    }

    @Override
    public String getVersion() {
        return com.viaversion.viaversion.api.Via.getAPI().getVersion();
    }

    @Override
    public void sendRawPacket(T player, ByteBuf packet) {
        com.viaversion.viaversion.api.Via.getAPI().sendRawPacket(player, packet);
    }

    @Override
    public void sendRawPacket(UUID uuid, ByteBuf packet) {
        com.viaversion.viaversion.api.Via.getAPI().sendRawPacket(uuid, packet);
    }

    @Override
    public BossBar createBossBar(String title, BossColor color, BossStyle style) {
        return new BossBar(com.viaversion.viaversion.api.Via.getAPI().legacyAPI().createLegacyBossBar(title, com.viaversion.viaversion.api.legacy.bossbar.BossColor.values()[color.ordinal()], com.viaversion.viaversion.api.legacy.bossbar.BossStyle.values()[style.ordinal()]));
    }

    @Override
    public BossBar createBossBar(String title, float health, BossColor color, BossStyle style) {
        return new BossBar(com.viaversion.viaversion.api.Via.getAPI().legacyAPI().createLegacyBossBar(title, health, com.viaversion.viaversion.api.legacy.bossbar.BossColor.values()[color.ordinal()], com.viaversion.viaversion.api.legacy.bossbar.BossStyle.values()[style.ordinal()]));
    }

    @Override
    public SortedSet<Integer> getSupportedVersions() {
        return com.viaversion.viaversion.api.Via.getAPI().getSupportedVersions();
    }

    @Override
    public SortedSet<Integer> getFullSupportedVersions() {
        return com.viaversion.viaversion.api.Via.getAPI().getFullSupportedVersions();
    }
}

