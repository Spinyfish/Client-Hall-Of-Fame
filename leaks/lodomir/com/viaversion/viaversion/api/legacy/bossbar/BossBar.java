/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.api.legacy.bossbar;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.legacy.bossbar.BossColor;
import com.viaversion.viaversion.api.legacy.bossbar.BossFlag;
import com.viaversion.viaversion.api.legacy.bossbar.BossStyle;
import java.util.Set;
import java.util.UUID;

public interface BossBar {
    public String getTitle();

    public BossBar setTitle(String var1);

    public float getHealth();

    public BossBar setHealth(float var1);

    public BossColor getColor();

    public BossBar setColor(BossColor var1);

    public BossStyle getStyle();

    public BossBar setStyle(BossStyle var1);

    public BossBar addPlayer(UUID var1);

    public BossBar addConnection(UserConnection var1);

    public BossBar removePlayer(UUID var1);

    public BossBar removeConnection(UserConnection var1);

    public BossBar addFlag(BossFlag var1);

    public BossBar removeFlag(BossFlag var1);

    public boolean hasFlag(BossFlag var1);

    public Set<UUID> getPlayers();

    public Set<UserConnection> getConnections();

    public BossBar show();

    public BossBar hide();

    public boolean isVisible();

    public UUID getId();
}

