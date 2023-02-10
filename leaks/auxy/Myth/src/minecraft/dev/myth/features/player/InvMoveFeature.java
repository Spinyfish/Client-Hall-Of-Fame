/**
 * @project mythrecode
 * @prod HardcodeProductions
 * @author Auxy
 * @at 13.08.22, 08:10
 */
package dev.myth.features.player;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.events.PacketEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.BooleanSetting;
import dev.myth.ui.clickgui.blubgui.BlubUI;
import dev.myth.ui.clickgui.dropdowngui.ClickGui;
import dev.myth.ui.clickgui.skeetgui.SkeetGui;
import dev.myth.ui.clickgui.skeetgui.TextBoxComponent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;

@Feature.Info(name = "InvMove", category = Feature.Category.PLAYER)
public class InvMoveFeature extends Feature {

    public final BooleanSetting onlyClickGui = new BooleanSetting("Only in ClickGui", false);
    public final BooleanSetting preventClose = new BooleanSetting("Prevent Close", true);

    private boolean isClickGui;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event-> {
        if(event.getState() != EventState.PRE) return;

        if (MC.currentScreen instanceof GuiChat || MC.currentScreen == null)
            return;

        isClickGui = MC.currentScreen instanceof ClickGui || MC.currentScreen instanceof SkeetGui || MC.currentScreen instanceof BlubUI ;

        if(onlyClickGui.getValue() && !isClickGui) return;

        if(SkeetGui.INSTANCE != null) {
            if (isClickGui && SkeetGui.INSTANCE.getFocusedComponent() != null && SkeetGui.INSTANCE.getFocusedComponent() instanceof TextBoxComponent)
                return;
        }

        final KeyBinding[] key = {
                getGameSettings().keyBindForward, getGameSettings().keyBindBack, getGameSettings().keyBindJump,
                getGameSettings().keyBindLeft, getGameSettings().keyBindRight
        };

        KeyBinding[] binds;
        for (int rofl = (binds = key).length, i = 0; i < rofl; ++i) {
            KeyBinding bind = binds[i];
            KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
        }
    };

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(event.getPacket() instanceof S2EPacketCloseWindow) {
            S2EPacketCloseWindow packet = event.getPacket();

            if(!preventClose.getValue()) return;

            if(MC.currentScreen == null) return;

            event.setCancelled(true);

            doLog("Prevented closing of Gui");

            if(MC.currentScreen instanceof GuiInventory) sendPacketUnlogged(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        }
    };
}
