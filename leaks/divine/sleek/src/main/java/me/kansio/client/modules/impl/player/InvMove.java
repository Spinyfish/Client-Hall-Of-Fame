package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;

@ModuleData(
        name = "Inventory Move",
        category = ModuleCategory.PLAYER,
        description = "Move whilst having an inventory open"
)
public class InvMove extends Module {

    public ModeValue mode = new ModeValue("Mode", this, "Vanilla");
    public BooleanValue noclose = new BooleanValue("No Close", this, true);

    private final KeyBinding[] keyBindings = new KeyBinding[]{
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSprint
    };

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (noclose.getValue()) {
            if (event.getPacket() instanceof S2EPacketCloseWindow && (mc.currentScreen instanceof GuiInventory)) {
                event.setCancelled(true);
            }
            if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat) return;
        }

        for (KeyBinding keyBinding : keyBindings) {
            keyBinding.pressed = Keyboard.isKeyDown(keyBinding.getKeyCode());
        }
    }

}
