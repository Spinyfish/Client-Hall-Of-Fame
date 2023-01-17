package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.LimitedRender2DEvent;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.Mode;
import com.alan.clients.module.impl.movement.inventorymove.BufferAbuseInventoryMove;
import com.alan.clients.module.impl.movement.inventorymove.CancelInventoryMove;
import com.alan.clients.module.impl.movement.inventorymove.WatchdogInventoryMove;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

/**
 * @author Alan
 * @since 20/10/2021
 */

@Rise
@ModuleInfo(name = "Inventory Move", description = "module.movement.inventorymove.description", category = Category.MOVEMENT)
public class InventoryMove extends Module {

    private final ModeValue bypassMode = new ModeValue("Bypass Mode", this)
            .add(new SubMode("None"))
            .add(new BufferAbuseInventoryMove("Buffer Abuse", this))
            .add(new CancelInventoryMove("Cancel", this))
            .add(new WatchdogInventoryMove("Watchdog", this))
            .setDefault("None");

    private final KeyBinding[] AFFECTED_BINDINGS = new KeyBinding[]{
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindJump
    };

    @EventLink()
    public final Listener<LimitedRender2DEvent> onLimitedRender2D = event -> {

        if (mc.currentScreen instanceof GuiChat || mc.currentScreen == this.getStandardClickGUI()) {
            return;
        }

        for (final KeyBinding bind : AFFECTED_BINDINGS) {
            bind.setPressed(GameSettings.isKeyDown(bind));
        }
    };
}