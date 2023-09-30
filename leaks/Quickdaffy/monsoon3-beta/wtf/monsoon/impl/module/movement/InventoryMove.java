/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Keyboard
 */
package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import org.lwjgl.input.Keyboard;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.player.ChestStealer;

public class InventoryMove
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.NORMAL).describedBy("The mode to use.");
    private boolean shouldBlink = false;
    private LinkedBlockingQueue<Packet<?>> packets = new LinkedBlockingQueue();
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        if (Wrapper.getModule(ChestStealer.class).isEnabled() && Wrapper.getModule(ChestStealer.class).stop.getValue().booleanValue() && this.mc.currentScreen instanceof GuiChest) {
            return;
        }
        this.shouldBlink = this.mc.currentScreen != null;
        if (this.mc.currentScreen != null) {
            if (Keyboard.isKeyDown((int)205) && !(this.mc.currentScreen instanceof GuiChat)) {
                this.mc.thePlayer.rotationYaw += 8.0f;
            }
            if (Keyboard.isKeyDown((int)203) && !(this.mc.currentScreen instanceof GuiChat)) {
                this.mc.thePlayer.rotationYaw -= 8.0f;
            }
            if (Keyboard.isKeyDown((int)200) && !(this.mc.currentScreen instanceof GuiChat)) {
                this.mc.thePlayer.rotationPitch -= 8.0f;
            }
            if (Keyboard.isKeyDown((int)208) && !(this.mc.currentScreen instanceof GuiChat)) {
                this.mc.thePlayer.rotationPitch += 8.0f;
            }
            KeyBinding[] moveKeys = new KeyBinding[]{this.mc.gameSettings.keyBindRight, this.mc.gameSettings.keyBindLeft, this.mc.gameSettings.keyBindBack, this.mc.gameSettings.keyBindForward, this.mc.gameSettings.keyBindJump, this.mc.gameSettings.keyBindSprint};
            if (this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiChat) {
                for (KeyBinding bind : moveKeys) {
                    if (Keyboard.isKeyDown((int)bind.getKeyCode())) continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            } else {
                for (KeyBinding key : moveKeys) {
                    key.pressed = Keyboard.isKeyDown((int)key.getKeyCode());
                }
            }
        }
        switch (this.mode.getValue()) {
            case WATCHDOG: {
                break;
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        switch (this.mode.getValue()) {
            case WATCHDOG: {
                break;
            }
        }
    };

    public InventoryMove() {
        super("Inventory Move", "Inventory move", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.shouldBlink = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.shouldBlink = false;
    }

    static enum Mode {
        NORMAL,
        WATCHDOG;

    }
}

