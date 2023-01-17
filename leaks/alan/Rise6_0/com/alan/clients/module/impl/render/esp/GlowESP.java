package com.alan.clients.module.impl.render.esp;

import com.alan.clients.component.impl.render.ESPComponent;
import com.alan.clients.component.impl.render.espcomponent.api.ESPColor;
import com.alan.clients.component.impl.render.espcomponent.impl.PlayerGlow;
import com.alan.clients.module.impl.render.ESP;
import com.alan.clients.module.impl.render.FullBright;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.value.impl.ColorValue;
import com.alan.clients.value.Mode;

import java.awt.*;

public final class GlowESP extends Mode<ESP> {

    private ColorValue normalColor = new ColorValue("Normal Color", this, Color.RED);
    private ColorValue damageColor = new ColorValue("Damage Color", this, Color.RED);
    private final ColorValue auraColor = new ColorValue("Aura Color", this, Color.RED);

    public GlowESP(String name, ESP parent) {
        super(name, parent);
    }


    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        ESPComponent.add(new PlayerGlow(new ESPColor(normalColor.getValue(), damageColor.getValue(), auraColor.getValue())));
    };
}