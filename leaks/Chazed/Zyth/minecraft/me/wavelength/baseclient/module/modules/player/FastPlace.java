package me.wavelength.baseclient.module.modules.player;

import me.wavelength.baseclient.event.EventHandler;
import me.wavelength.baseclient.event.events.EventPreUpdate;
import me.wavelength.baseclient.event.events.EventTick;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class FastPlace extends Module {

    public FastPlace() {
        super("FastPlace", "Place block = no delay!", Keyboard.KEY_G, Category.PLAYER);
    }

    @Override
    public void setup() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
        mc.rightClickDelayTimer = 6;
        super.onDisable();
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        mc.rightClickDelayTimer = 1;
    }
}