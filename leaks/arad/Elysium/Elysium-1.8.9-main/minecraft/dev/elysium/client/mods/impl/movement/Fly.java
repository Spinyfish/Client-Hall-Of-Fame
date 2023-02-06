package dev.elysium.client.mods.impl.movement;

import dev.elysium.client.Elysium;
import dev.elysium.client.utils.player.MovementUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.events.EventUpdate;

public class Fly extends Mod {

	public double boost = 0;
	public boolean dmg;

	public ModeSetting mode = new ModeSetting("Mode",this,"Vanilla","Airwalk","OldNCP","Funcraft","Hypixel","Verus","Minemora");
	public ModeSetting verusmode = new ModeSetting("Verus",this,"Collide","Damage","Float","AirJump");
	public ModeSetting minemora = new ModeSetting("Minemora",this,"Float","TP");
	public NumberSetting speed = new NumberSetting("Speed",0,10,1,0.01,this);

	public Fly() {
		super("Fly", "Self Explanatory", Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		dmg = true;
		boost = 0;
		if((mode.is("OldNCP") || mode.is("Funcraft")) && mc.thePlayer.onGround) {
			mc.timer.timerSpeed = 0.8F;
			MovementUtil.strafe(0.45);
			mc.thePlayer.jump();
			mc.thePlayer.motionY = 0.45;
			dmg = false;
			boost = speed.getValue();
		} else if(mode.is("Hypixel")) {
			dmg = false;
			if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 0.278, 0).expand(0, 0, 0)).isEmpty()) {
				for (int i = 0; i <= 10; ++i) {
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.278, mc.thePlayer.posZ, false));
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
					if (i == 10)
						mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
				}
			} else {
				Elysium.getInstance().addChatMessage("§cNot enough space (0.278 blocks min distance between you and the block above you)");
			}
		}

		if(mode.is("Minemora") && minemora.is("TP") && !Elysium.getInstance().getModManager().getModByName("Speed").toggled) {
			mc.timer.timerSpeed = 0.425F;
			if(!Elysium.getInstance().getModManager().getModByName("Blink").toggled)Elysium.getInstance().getModManager().getModByName("Blink").toggle();
		}
		if(mode.is("Verus") && verusmode.is("Damage")) {
			dmg = false;
			mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 1, 0));
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.05, mc.thePlayer.posZ, false));
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
		}

		super.onEnable();
	}

	@Override
	public void onDisable() {
		if(mode.is("Minemora") && minemora.is("TP") && !Elysium.getInstance().getModManager().getModByName("Speed").toggled) {
			if(Elysium.getInstance().getModManager().getModByName("Blink").toggled)Elysium.getInstance().getModManager().getModByName("Blink").toggle();
			mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
		}

		if(mode.is("Vanilla")) {
			mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
		}
		if(mode.is("OldNCP"))
			mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
		mc.timer.timerSpeed = 1F;
		super.onDisable();
		if(mode.is("Vanilla"))
			mc.thePlayer.motionY = mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
	}

	@EventTarget
	public void onEventUpdate(EventUpdate e) {
		double y;
		switch(mode.getMode()) {
			case "Vanilla":
				mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
				MovementUtil.strafe(speed.getValue());
				if(mc.gameSettings.keyBindJump.pressed)
					mc.thePlayer.motionY += speed.getValue();
				if(mc.gameSettings.keyBindSneak.pressed)
					mc.thePlayer.motionY += -speed.getValue();
				break;
			case "Airwalk":
				mc.thePlayer.motionY = 0;
				mc.thePlayer.onGround = true;
				break;
			case "Minemora":
				if(minemora.is("Float") || Elysium.getInstance().getModManager().getModByName("Speed").toggled) {
					MovementUtil.strafe(0.2755F);
					mc.timer.timerSpeed = 1.08F;
					mc.thePlayer.motionY = 0;
					y = mc.thePlayer.ticksExisted % 2 != 0 ? 0.07 : -0.07;
					mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY+y,mc.thePlayer.posZ);
					if(y > 0)
						mc.thePlayer.posY -= y;
					mc.thePlayer.cameraPitch = 0;
					mc.thePlayer.onGround = false;
				} else {
					mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
					MovementUtil.strafe(speed.getValue());
					if(mc.gameSettings.keyBindJump.pressed)
						mc.thePlayer.motionY += speed.getValue()/2;
					if(mc.gameSettings.keyBindSneak.pressed)
						mc.thePlayer.motionY += -speed.getValue()/2;
				}
				break;
			case "Funcraft":
				MovementUtil.strafe(Math.max(boost,0.15));
				mc.thePlayer.onGround = true;
				boolean prevDmg = dmg;
				if(mc.thePlayer.motionY <= 0.2)
					dmg = true;

				if(boost > 0.15) {
					boost -= 0.01;
				}

				if(dmg) {
					mc.thePlayer.onGround = false;

					y = mc.thePlayer.ticksExisted % 2 != 0 ? 1E-8 : -1E-7;
					mc.thePlayer.motionY = -y*3;
					mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY+y,mc.thePlayer.posZ);
					if(!prevDmg) {
						mc.thePlayer.performHurtAnimation();
						mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("game.neutral.hurt"), 1F));
						mc.timer.timerSpeed = 0.8F;
					}
				}
				break;
			case "Verus":
				switch(verusmode.getMode()) {
					case "Collide":

						break;
					case "Float":
						mc.thePlayer.motionY = 0;
						break;
					case "Damage":
						if(dmg) {
							mc.thePlayer.onGround = true;
							mc.thePlayer.motionX = mc.thePlayer.motionY = mc.thePlayer.motionZ = 0;
							MovementUtil.strafe(speed.getValue());
							if(mc.gameSettings.keyBindJump.pressed)
								mc.thePlayer.motionY += speed.getValue();
							if(mc.gameSettings.keyBindSneak.pressed)
								mc.thePlayer.motionY += -speed.getValue();
							if(boost++ > 12)
								Elysium.getInstance().addChatMessage("Nice client");
						} else if(mc.thePlayer.hurtTime == 9) {
							dmg = true;
						}
						break;
				}
				break;
			case "OldNCP":
				MovementUtil.strafe(Math.max(boost,0.15));
				mc.thePlayer.onGround = true;
				prevDmg = dmg;
				if(mc.thePlayer.motionY <= 0.1)
					dmg = true;

				if(boost > 0.15) {
					boost -= 0.0057525;
				}

				if(dmg) {
					mc.timer.timerSpeed = 1.2F;
					mc.thePlayer.onGround = false;
					y = mc.thePlayer.ticksExisted % 2 != 0 ? 1E-9 : -1E-9;
					mc.thePlayer.motionY = y;
					if(!prevDmg) {
						mc.thePlayer.performHurtAnimation();
						boost += 0.1;
						MovementUtil.strafe(Math.max(boost,0.15));
						mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("game.neutral.hurt"), 1F));
						mc.timer.timerSpeed = 0.8F;
					}
				}
				break;
			case "Hypixel":
				if(mc.thePlayer.hurtTime != 0 && !dmg) {
					mc.thePlayer.jump();
					mc.thePlayer.motionX *= 3;
					mc.thePlayer.motionZ *= 3;
					dmg = true;
				}
				break;
		}
	}

}
