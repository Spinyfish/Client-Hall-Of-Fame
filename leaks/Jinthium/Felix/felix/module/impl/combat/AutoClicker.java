package felix.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.util.other.TimeHelper;
import felix.value.impl.BooleanValue;
import felix.value.impl.NumberValue;
import net.minecraft.client.entity.EntityPlayerSP;

public class AutoClicker extends Module {
	
	private NumberValue<Integer> minCPS = new NumberValue<>("Minimum CPS", 10, 1, 20);
	
	private NumberValue<Integer> maxCPS = new NumberValue<>("Maximum CPS", 13, 1, 20);
	
	private BooleanValue click = new BooleanValue("Mouse Only", true);

    private TimeHelper timer = new TimeHelper();
	
    public AutoClicker() {
    	super("AutoClicker", 0, ModuleCategory.COMBAT);
    	addValues(maxCPS, minCPS, click);
    }
    
    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
    	final EntityPlayerSP player = mc.thePlayer;
    	if (event.isPre() && mc.currentScreen == null && player.isEntityAlive()) {
    		if (click.isEnabled() && !Mouse.isButtonDown(0)) {
    			return;
    		}
    		try {
	    		boolean illegal = minCPS.getValue() > maxCPS.getValue();
	    		boolean equalto = minCPS.getValue() == maxCPS.getValue();
	    		final int cps = equalto ? maxCPS.getValue() : !illegal ? ThreadLocalRandom.current().nextInt(minCPS.getValue(), maxCPS.getValue()) : ThreadLocalRandom.current().nextInt(maxCPS.getValue(), minCPS.getValue());
	    		final int delay = 1000 / cps;
	    		if (timer.reach(delay)) {
	    			mc.playerController.onStoppedUsingItem(player);
	    			player.swingItem();
	    			mc.clickMouse();
	    			timer.reset();
	            }
    		}
    		catch (final IllegalArgumentException e) {
    			e.printStackTrace();
    		}
        }
    }
    
    public void onEnable() {
    	super.onEnable();
    }
    
    public void onDisable() {
    	super.onDisable();
    }
}
