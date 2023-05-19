package club.marsh.bloom.impl.mods.movement;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.components.BlinkingComponent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.other.Timer;
import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;


public class Blink extends Module {
	Timer timer = new Timer();
	BooleanValue serverSide = new BooleanValue("Server Sided", true);
	BooleanValue pulse = new BooleanValue("Pulse", false);
	NumberValue<Double> pulseTime = new NumberValue<>("Pulse Time", 500D, 1D, 10000D, 0, pulse::getObject);

	public Blink() {
		super("Blink",Keyboard.KEY_NONE,Category.MOVEMENT);
	}

	@Override
	public void onDisable() {
		BlinkingComponent.INSTANCE.setOn(false);
		BlinkingComponent.INSTANCE.setServerside(false);
	}

	@Override
	public void onEnable() {
		timer.reset();
		BlinkingComponent.INSTANCE.setOn(true);
		BlinkingComponent.INSTANCE.setServerside(serverSide.isOn());
		if (mc.thePlayer == null || mc.theWorld == null)
			this.setToggled(false);
	}

	@Subscribe
	public void onUpdate(UpdateEvent event) {
		try {
			if (System.currentTimeMillis() - timer.lastMS > pulseTime.getObject() && pulse.isOn()) {
				BlinkingComponent.INSTANCE.releasePackets();
				timer.reset();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	@Subscribe
	public void onPacket(PacketEvent e) {
		if (mc.thePlayer == null || mc.thePlayer.ticksExisted <= 1 || mc.thePlayer.isDead) {
			this.setToggled(false);
		}
	}
}
