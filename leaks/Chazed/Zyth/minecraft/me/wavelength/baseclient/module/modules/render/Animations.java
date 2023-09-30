package me.wavelength.baseclient.module.modules.render;

import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class Animations extends Module {

    public Animations() {
        super("animation", "animation!", Keyboard.KEY_NONE, Category.RENDER, AntiCheat.MINECRAFT, AntiCheat.RUB, AntiCheat.ORGASM, AntiCheat.WANK);
        
    }
    
    public static double factor;
    public static int mode;
    
	@Override
	public void setup() {
		moduleSettings.addDefault("Factor", 0.3D);
		factor = 0.3;
        setToggled(true);
	}
    

    
	@Override
	public void onUpdate(UpdateEvent event) {
		factor = this.moduleSettings.getDouble("Factor");
		
		if(this.antiCheat == AntiCheat.MINECRAFT) {
			mode = 4;
		}
	
		if(this.antiCheat == AntiCheat.RUB) {
			mode = 1;
		}
		
		if(this.antiCheat == AntiCheat.ORGASM) {
			mode = 2;
		}
		
		if(this.antiCheat == AntiCheat.WANK) {
			mode = 3;
		}
	
	}
}
