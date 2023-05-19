package club.marsh.bloom.impl.mods.combat;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.Render2DEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.combat.ClickTimer;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import com.google.common.eventbus.Subscribe;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;


public class AutoClicker extends Module {
	public AutoClicker() {
		super("Auto Clicker",Keyboard.KEY_NONE,Category.COMBAT);
	}
	
	public boolean clickedThisTick = false;
	public ClickTimer clickTimer = new ClickTimer(7.5);
	NumberValue<Double> cps = new NumberValue("Clicks per second.",7.5,1,75,1,() -> true);
	BooleanValue breakBlocks = new BooleanValue("Break Blocks", false);
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		clickTimer.setMinCps(cps.getObject().doubleValue());
		clickTimer.update();
		mc.gameSettings.keyBindAttack.pressed = clickedThisTick;
		clickedThisTick = false;
	}
	@Subscribe
	public void onRender2D(Render2DEvent e) {
		if (!breakBlocks.isOn() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			return;

		if (clickTimer.hasEnoughTimeElapsed(true) && !clickedThisTick && Mouse.isButtonDown(0)) {
			clickedThisTick = true;
			mc.gameSettings.keyBindAttack.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
		}

	}
}
