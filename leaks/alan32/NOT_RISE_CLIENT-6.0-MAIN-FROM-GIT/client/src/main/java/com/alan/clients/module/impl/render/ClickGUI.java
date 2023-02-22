package com.alan.clients.module.impl.render;


import com.alan.clients.Client;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.KeyboardInputEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.render.RenderUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * Displays a GUI which can display and do various things
 *
 * @author Alan
 * @since 04/11/2021
 */
@ModuleInfo(name = "module.render.clickgui.name", description = "module.render.clickgui.description", category = Category.RENDER, keyBind = Keyboard.KEY_RSHIFT)
public final class ClickGUI extends Module {
    @Override
    public void onEnable() {
        Client.INSTANCE.getEventBus().register(Client.INSTANCE.getStandardClickGUI());
        mc.displayGuiScreen(Client.INSTANCE.getStandardClickGUI());
//        Client.INSTANCE.getStandardClickGUI().overlayPresent = null;
    }

    @Override
    public void onDisable() {
        Keyboard.enableRepeatEvents(false);
        Client.INSTANCE.getEventBus().unregister(Client.INSTANCE.getStandardClickGUI());
        Client.INSTANCE.getExecutor().execute(() -> Client.INSTANCE.getConfigFile().write());
    }

    @EventLink(value = Priorities.HIGH)
    public final Listener<Render2DEvent> onRender2D = event -> {
        double width = event.getScaledResolution().getScaledWidth();
        double height = event.getScaledResolution().getScaledHeight();

        UI_RENDER_RUNNABLES.add(() -> Client.INSTANCE.getStandardClickGUI().render());
        UI_BLOOM_RUNNABLES.add(() -> Client.INSTANCE.getStandardClickGUI().bloom());
        NORMAL_BLUR_RUNNABLES.add(() -> RenderUtil.rectangle(0, 0, width, height, Color.BLACK));
    };

    @EventLink()
    public final Listener<KeyboardInputEvent> onKey = event -> {

        if (event.getKeyCode() == this.getKeyCode()) {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    };
}
