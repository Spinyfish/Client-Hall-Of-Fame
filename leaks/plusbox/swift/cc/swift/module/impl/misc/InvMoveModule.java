package cc.swift.module.impl.misc;

import cc.swift.events.UpdateEvent;
import cc.swift.gui.clickgui.ClickGui;
import cc.swift.module.Module;
import cc.swift.value.impl.BooleanValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class InvMoveModule extends Module {

    public final BooleanValue whileSpeed = new BooleanValue("While Speed", false);

    private final KeyBinding[] keys = {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump
    };

    public InvMoveModule() {
        super("InvMove", Category.MISC);
        this.registerValues(whileSpeed);
    }

    @Handler
    public final Listener<UpdateEvent> updateEventListener = e -> {
        if (mc.currentScreen == null || mc.currentScreen instanceof ClickGui || mc.currentScreen instanceof GuiChat)
            return;

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed.id) && !whileSpeed.getValue())
            return;

        for (KeyBinding key : keys) {
            KeyBinding.setKeyBindState(key.getKeyCode(), Keyboard.isKeyDown(key.getKeyCode()));
        }
    };
}