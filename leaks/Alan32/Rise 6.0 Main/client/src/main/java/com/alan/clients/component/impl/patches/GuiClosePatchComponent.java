package com.alan.clients.component.impl.patches;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class GuiClosePatchComponent extends Component {

    private boolean inGUI;

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (mc.currentScreen == null && inGUI) {
            for (final KeyBinding bind : mc.gameSettings.keyBindings) {
                bind.setPressed(GameSettings.isKeyDown(bind));
            }
        }

        inGUI = mc.currentScreen != null;
    };
}
