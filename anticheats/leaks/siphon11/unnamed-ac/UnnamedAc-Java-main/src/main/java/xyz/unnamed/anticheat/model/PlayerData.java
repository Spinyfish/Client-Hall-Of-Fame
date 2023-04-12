package xyz.unnamed.anticheat.model;

import lombok.Getter;
import org.atteo.classindex.ClassIndex;
import org.bukkit.entity.Player;
import xyz.unnamed.anticheat.Anticheat;
import xyz.unnamed.anticheat.model.check.AbstractCheck;
import xyz.unnamed.anticheat.model.processor.processors.PositionProcessor;
import xyz.unnamed.anticheat.model.processor.processors.collision.CollisionProcessor;
import xyz.unnamed.anticheat.service.CheckService;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
public class PlayerData {

    private final Player player;

    private final long joinTime = System.currentTimeMillis();
    private final Map<Class<? extends AbstractCheck>, AbstractCheck> checks = new HashMap<>();

    private final PositionProcessor positionProcessor = new PositionProcessor();
    private final CollisionProcessor collisionProcessor = new CollisionProcessor();
    private final CheckService checkManager = new CheckService(this);

    public PlayerData(Player player) {
        this.player = player;

        for (Class<? extends AbstractCheck> c : ClassIndex.getSubclasses(AbstractCheck.class, Anticheat.class.getClassLoader())) {
            try {
                checks.put(c, c.getDeclaredConstructor(PlayerData.class).newInstance(this));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }
}
