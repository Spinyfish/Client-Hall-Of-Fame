package com.alan.clients.module.impl.render.fullbright;

import com.alan.clients.module.impl.render.FullBright;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.value.Mode;

/**
 * @author Strikeless
 * @since 04.11.2021
 */
public final class GammaFullBright extends Mode<FullBright> {

    private float oldGamma;

    public GammaFullBright(String name, FullBright parent) {
        super(name, parent);
    }


    @EventLink()
    public final Listener<TickEvent> onTick = event -> {
        mc.gameSettings.gammaSetting = 100.0F;
    };

    @Override
    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldGamma;
    }
}