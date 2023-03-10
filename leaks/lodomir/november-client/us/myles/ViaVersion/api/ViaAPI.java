/*
 * Decompiled with CFR 0.152.
 */
package us.myles.ViaVersion.api;

import io.netty.buffer.ByteBuf;
import java.util.SortedSet;
import java.util.UUID;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;

@Deprecated
public interface ViaAPI<T> {
    public int getPlayerVersion(T var1);

    public int getPlayerVersion(UUID var1);

    default public boolean isPorted(UUID playerUUID) {
        return this.isInjected(playerUUID);
    }

    public boolean isInjected(UUID var1);

    public String getVersion();

    public void sendRawPacket(T var1, ByteBuf var2);

    public void sendRawPacket(UUID var1, ByteBuf var2);

    public BossBar createBossBar(String var1, BossColor var2, BossStyle var3);

    public BossBar createBossBar(String var1, float var2, BossColor var3, BossStyle var4);

    public SortedSet<Integer> getSupportedVersions();

    public SortedSet<Integer> getFullSupportedVersions();
}

