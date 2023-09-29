package haptic.module.impl.player;

import haptic.event.Event;
import haptic.event.impl.EventRender;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.ListeningType;
import haptic.module.Module;
import haptic.setting.impl.BooleanSetting;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.network.PacketUtils;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastEat extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Instant", "Instant", "Custom");
	private NumberSetting speed = new NumberSetting("Speed", 10, 1, 20, 1, this);
	private BooleanSetting onlyOnGround = new BooleanSetting("Only on ground", false, this);
	
	public FastEat() {
		super("FastEat", Category.PLAYER, ListeningType.ALWAYS);
		this.addSettings(mode, speed, onlyOnGround);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventRender) {
			speed.setShowed(mode.is("Custom"));
		}
		
		if(!this.isEnabled()) return;
		
		if(event instanceof EventUpdate) {
			if(mc.thePlayer.onGround || !onlyOnGround.isEnabled()) {
				if(mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion)) {
					switch(mode.getMode()) {
					case "Instant": {
						for(int i = 0; i < 32; i++) {
							PacketUtils.sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
						}
						mc.playerController.onStoppedUsingItem(mc.thePlayer);
						break;
					}
					case "Custom": {
						for(int i = 0; i < speed.getValue(); i++) {
							PacketUtils.sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
						}
						break;
					}
					}
				}
			}
		}
	}

}
