/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 17:51
 */
package dev.myth.features.visual;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.builder.GuiBuilder;
import dev.myth.events.UpdateEvent;
import org.lwjgl.input.Keyboard;

@Feature.Info(
        name = "Fullbright",
        description = "Set gamma to 1000",
        category = Feature.Category.VISUAL
)
public class FullBrightFeature extends Feature {

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() != EventState.PRE) return;
        MC.gameSettings.gammaSetting = 1000;
    };

    @Override
    public void onDisable() {
        super.onDisable();
        MC.displayGuiScreen(new GuiBuilder());
        MC.gameSettings.gammaSetting = 1;
    }
}
