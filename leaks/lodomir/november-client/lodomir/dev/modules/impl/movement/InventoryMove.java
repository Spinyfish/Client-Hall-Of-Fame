/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.input.Keyboard
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InventoryMove
extends Module {
    private final KeyBinding[] moveKeys;

    public InventoryMove() {
        super("InvMove", 0, Category.MOVEMENT);
        this.moveKeys = new KeyBinding[]{InventoryMove.mc.gameSettings.keyBindRight, InventoryMove.mc.gameSettings.keyBindLeft, InventoryMove.mc.gameSettings.keyBindBack, InventoryMove.mc.gameSettings.keyBindForward, InventoryMove.mc.gameSettings.keyBindJump, InventoryMove.mc.gameSettings.keyBindSprint};
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        if (!(InventoryMove.mc.currentScreen instanceof GuiChat)) {
            int length = this.moveKeys.length;
            for (int i = 0; i < length; ++i) {
                this.moveKeys[i].pressed = Keyboard.isKeyDown((int)this.moveKeys[i].getKeyCode());
            }
        }
    }
}

