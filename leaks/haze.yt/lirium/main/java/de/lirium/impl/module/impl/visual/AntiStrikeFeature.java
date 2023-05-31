package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.RenderCharEvent;
import de.lirium.impl.events.RenderItemFrameEvent;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "Anti Strike", category = ModuleFeature.Category.VISUAL, description = "Avoid strikes from autism servers")
public class AntiStrikeFeature extends ModuleFeature {

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Censored", new String[]{"Remove"});

    @Value(name = "Hide Paintings")
    private final CheckBox hidePaintings = new CheckBox(true);

    @EventHandler
    private final Listener<RenderItemFrameEvent> renderPaintingEventListener = e -> {
        if(hidePaintings.getValue())
            e.setCancelled(true);
    };

    @EventHandler
    private final Listener<RenderCharEvent> charEventListener = e -> {
        if (mc.world != null)
            if (e.character != '§' && !Character.isDigit(e.character) && Character.isAlphabetic(e.character)) {
                switch (mode.getValue()) {
                    case "Censored":
                        e.character = '*';
                        break;
                    case "Remove":
                        e.skip = true;
                        break;
                }
            }
    };
}
