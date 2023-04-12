package xyz.unnamed.anticheat.model.check;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.atteo.classindex.IndexSubclasses;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.utilities.chat.CC;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
@IndexSubclasses
@RequiredArgsConstructor
public abstract class AbstractCheck {

    protected final String name;
    protected final PlayerData playerData;
    protected int vl = 0;
    private final Set<UUID> alerts = new HashSet<>();

    private double buffer;

    //TODO: Make this more efficient, less dogshit, and a better alert/hoverable system, etc... but it is what it is for now.
    protected void flag(String details) {
        this.vl++;
        String name = playerData.getPlayer().getDisplayName();

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("anticheat.alerts.view"))
                .forEach(p -> p.sendMessage(
                        CC.GOLD + CC.BOLD + "AC " + CC.GRAY + "» " +
                                CC.GOLD + name + CC.YELLOW + " flagged check "
                                + CC.GOLD + this.name + CC.YELLOW + " (" + vl + ")" + " "
                                + CC.GRAY + "[" + details + "]"
                ));
    }

    public double increaseBuffer() {
        return buffer = Math.min(10000, buffer + 1);
    }

    public double decreaseBufferBy(final double amount) {
        return buffer = Math.max(0, buffer - amount);
    }

    public void resetBuffer() {
        buffer = 0;
    }

    public void multiplyBuffer(final double multiplier) {
        buffer *= multiplier;
    }
}
