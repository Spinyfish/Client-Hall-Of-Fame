package felix.module.impl.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.value.impl.BooleanValue;

import java.util.Objects;

public class InvMove extends Module {

    public InvMove() {
        super("InvMove", 0, ModuleCategory.PLAYER);
        setHidden(true);
    }
}