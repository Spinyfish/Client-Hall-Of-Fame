package me.wavelength.baseclient.module.modules.combat;

import me.wavelength.baseclient.event.events.Render3DEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.client.settings.KeyBinding;

import me.wavelength.baseclient.utils.Timer;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.util.concurrent.Delayed;

public class Autoclick extends Module {
    public Autoclick() {
        super("Autoclick", "Auto click cps.", 0, Category.COMBAT);
        moduleSettings.addDefault("tick_delay", 0D);
    }

    @Override
    public void setup() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onUpdate(UpdateEvent event) {


        if (mc.gameSettings.keyBindAttack.isKeyDown()) return;
        Timer timer=new Timer();
        if(timer.delay(50*this.moduleSettings.getDouble("tick_delay"))){
        	mc.thePlayer.swingItem();
            mc.clickMouse();
            timer.reset();
        }
    }

}