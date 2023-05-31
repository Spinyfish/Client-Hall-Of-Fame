package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketEntityAction;

@ModuleFeature.Info(name = "Inventory Move", description = "You can walk while being in inventory", category = ModuleFeature.Category.MOVEMENT)
public class InventoryMoveFeature extends ModuleFeature {

    @Value(name = "Spam open inventory")
    final CheckBox spamOpenInventory = new CheckBox(false);

    @Value(name = "Sprint")
    final CheckBox sprint = new CheckBox(false);

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        if (!mc.inGameHasFocus && !(mc.currentScreen instanceof GuiChat)) {
            if (spamOpenInventory.getValue())
                sendPacketUnlogged(new CPacketEntityAction(getPlayer(), CPacketEntityAction.Action.OPEN_INVENTORY));
            handleKey(getGameSettings().keyBindForward);
            handleKey(getGameSettings().keyBindBack);
            handleKey(getGameSettings().keyBindLeft);
            handleKey(getGameSettings().keyBindRight);
            if(sprint.getValue()) {
                handleKey(getGameSettings().keyBindSprint);
            }
            handleKey(getGameSettings().keyBindJump);
        }
    };

    private void handleKey(KeyBinding key) {
        key.pressed = isKeyDown(key.getKeyCode());
    }
}
