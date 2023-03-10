/*
 * Decompiled with CFR 0.152.
 */
package us.myles.ViaVersion.api.boss;

import java.util.Set;
import java.util.UUID;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossFlag;
import us.myles.ViaVersion.api.boss.BossStyle;

@Deprecated
public class BossBar<T> {
    private final com.viaversion.viaversion.api.legacy.bossbar.BossBar bossBar;

    public BossBar(com.viaversion.viaversion.api.legacy.bossbar.BossBar bossBar) {
        this.bossBar = bossBar;
    }

    public String getTitle() {
        return this.bossBar.getTitle();
    }

    public BossBar setTitle(String title) {
        this.bossBar.setTitle(title);
        return this;
    }

    public float getHealth() {
        return this.bossBar.getHealth();
    }

    public BossBar setHealth(float health) {
        this.bossBar.setHealth(health);
        return this;
    }

    public BossColor getColor() {
        return BossColor.values()[this.bossBar.getColor().ordinal()];
    }

    public BossBar setColor(BossColor color) {
        this.bossBar.setColor(com.viaversion.viaversion.api.legacy.bossbar.BossColor.values()[color.ordinal()]);
        return this;
    }

    public BossStyle getStyle() {
        return BossStyle.values()[this.bossBar.getStyle().ordinal()];
    }

    public BossBar setStyle(BossStyle style) {
        this.bossBar.setStyle(com.viaversion.viaversion.api.legacy.bossbar.BossStyle.values()[style.ordinal()]);
        return this;
    }

    @Deprecated
    public BossBar addPlayer(T player) {
        return this;
    }

    public BossBar addPlayer(UUID player) {
        this.bossBar.addPlayer(player);
        return this;
    }

    @Deprecated
    public BossBar addPlayers(T ... players) {
        return this;
    }

    @Deprecated
    public BossBar removePlayer(T player) {
        return this;
    }

    public BossBar removePlayer(UUID uuid) {
        this.bossBar.removePlayer(uuid);
        return this;
    }

    public BossBar addFlag(BossFlag flag) {
        this.bossBar.addFlag(com.viaversion.viaversion.api.legacy.bossbar.BossFlag.values()[flag.ordinal()]);
        return this;
    }

    public BossBar removeFlag(BossFlag flag) {
        this.bossBar.removeFlag(com.viaversion.viaversion.api.legacy.bossbar.BossFlag.values()[flag.ordinal()]);
        return this;
    }

    public boolean hasFlag(BossFlag flag) {
        return this.bossBar.hasFlag(com.viaversion.viaversion.api.legacy.bossbar.BossFlag.values()[flag.ordinal()]);
    }

    public Set<UUID> getPlayers() {
        return this.bossBar.getPlayers();
    }

    public BossBar show() {
        this.bossBar.show();
        return this;
    }

    public BossBar hide() {
        this.bossBar.hide();
        return this;
    }

    public boolean isVisible() {
        return this.bossBar.isVisible();
    }

    public UUID getId() {
        return this.bossBar.getId();
    }
}

