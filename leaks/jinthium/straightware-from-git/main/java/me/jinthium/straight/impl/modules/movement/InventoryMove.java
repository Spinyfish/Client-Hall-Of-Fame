package me.jinthium.straight.impl.modules.movement;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.clickgui.dropdown.components.ConfigPanel;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.components.BadPacketsComponent;
import me.jinthium.straight.impl.components.BlinkComponent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.movement.invmove.VanillaInvMove;
import me.jinthium.straight.impl.modules.movement.invmove.WatchdogInvMove;
import me.jinthium.straight.impl.utils.ChatUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.BlockPos;
import org.lwjglx.input.Keyboard;

import java.util.concurrent.ConcurrentLinkedQueue;

public class InventoryMove extends Module{
    public InventoryMove(){
        super("InventoryMove", 0, Category.MOVEMENT);

        this.registerModes(
                new VanillaInvMove(),
                new WatchdogInvMove()
        );
    }

    private final KeyBinding[] keyBindings = new KeyBinding[]{
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindJump
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        if(event.isPre()){

            if(mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof ConfigPanel)
                return;

            for(KeyBinding keyBinding : keyBindings){
                keyBinding.pressed = Keyboard.isKeyDown(keyBinding.getKeyCode());
            }
        }
    };
}
