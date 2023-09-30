package axolotl.cheats.modules.impl.player;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.ModeSetting;
import axolotl.util.BlockUtils;
import axolotl.util.PlayerUtil;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class Jesus extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Normal", "Normal", "NCP", "Spartan", "Vulcan");

	public Jesus() {
		super("Jesus", Category.PLAYER, true);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}

	private int jesusTicks;

	public void onEvent(Event e) {
		if(e instanceof EventUpdate && e.eventType == EventType.PRE) {
			switch(mode.getMode()) {
				case "Vulcan":
					if(
							BlockUtils.inst.isLiquidBlock(mc.theWorld.getBlockState(
									new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5001, mc.thePlayer.posZ)).getBlock())
					) {
						mc.gameSettings.keyBindJump.pressed = true;
						mc.thePlayer.setSprinting(false);
						mc.thePlayer.motionX *= 0.5D;
						mc.thePlayer.motionZ *= 0.5D;
						mc.timer.timerSpeed = 1.3f;
					} else {
						mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
						mc.timer.timerSpeed = mc.timer.timerSpeed == 1.3f ? 1f : mc.timer.timerSpeed;
					}
					break;
				case "Spartan":
					if(
							BlockUtils.inst.isLiquidBlock(mc.theWorld.getBlockState(
									new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5001, mc.thePlayer.posZ)).getBlock())
							|| BlockUtils.inst.isLiquidBlock(mc.theWorld.getBlockState(
									new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5001, mc.thePlayer.posZ)).getBlock())
					) {
						if(++jesusTicks > 40) {
							mc.thePlayer.motionX *= (0.6 + (Math.random() / 7)) - ((jesusTicks % 5) / 8);
							mc.thePlayer.motionZ *= (0.6 + (Math.random() / 7)) - ((jesusTicks % 5) / 8);
							mc.timer.timerSpeed = 1 + (jesusTicks % 2 == 0 ? 0.3f : 0);
						} else if(jesusTicks > 10) {
							mc.thePlayer.motionX *= (0.2 + (Math.random() / 8)) - ((jesusTicks % 5) / 8);
							mc.thePlayer.motionZ *= (0.2 + (Math.random() / 8)) - ((jesusTicks % 5) / 8);
							mc.timer.timerSpeed = 1 + (jesusTicks % 2 == 0 ? 0.1f : 0);
						}
					} else {
						jesusTicks = 0;
						mc.timer.timerSpeed = 1f;
					}
					break;
				case "NCP":
					mc.timer.timerSpeed = 1f;
					if(BlockUtils.inst.isLiquidBlock(PlayerUtil.getBlockAt())) {
						mc.thePlayer.motionY += 0.24F;
						mc.thePlayer.motionY = Math.min(mc.thePlayer.motionY, 0.24F);
						mc.gameSettings.keyBindJump.pressed = false;
						mc.timer.timerSpeed = 1.2f - (mc.thePlayer.ticksExisted % 10 / 100);
					}
					break;
				default:
					break;

			}
		}
	}
	
}
