package me.wavelength.baseclient.module.modules.render;

import me.wavelength.baseclient.event.EventHandler;
import me.wavelength.baseclient.event.events.EventPreUpdate;
import me.wavelength.baseclient.event.events.EventTick;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class ESP extends Module {

    public ESP() {
        super("ESP", "Player out line & more!", Keyboard.KEY_NONE, Category.RENDER, AntiCheat.GLOW);
    }



}