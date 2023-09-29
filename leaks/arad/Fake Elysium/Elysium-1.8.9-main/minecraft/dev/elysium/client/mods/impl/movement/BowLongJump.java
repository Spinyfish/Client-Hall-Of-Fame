package dev.elysium.client.mods.impl.movement;

import dev.elysium.base.events.types.Event;
import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventMove;
import dev.elysium.client.events.EventPacket;
import dev.elysium.client.events.EventUpdate;
import dev.elysium.client.utils.player.MovementUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;

public class BowLongJump extends Mod {

    int ticksSinceStart;
    int flyingFor = 0;
    boolean damaged = false;

    double[] startPos = {0, 0, 0};

    public BowLongJump() {
        super("BowLongJump","Like the name implies it makes your jumps looooooooooong using a bow", Category.MOVEMENT);
    }

    public void onEnable() {
        ticksSinceStart = 0;
        damaged = false;
        boolean toggle = false;
        startPos[0] = mc.thePlayer.posX; startPos[1] = mc.thePlayer.posY; startPos[2] = mc.thePlayer.posZ;
        if(!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
            Elysium.INSTANCE.addChatMessage("The block above you is not air!");
            toggle = true;
        }
        for(int i = 0; i <= 9; i++) {
            if(mc.thePlayer.inventory.getStackInSlot(i) != null) {
                if(mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBow) {
                    mc.thePlayer.inventory.currentItem = i;
                    return;
                }
            }
        }
        Elysium.INSTANCE.addChatMessage("No bow in your inventory!");

        if(toggle) {
            toggle();
        }
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        MovementUtil.strafe();
        if(mc.thePlayer.hurtTime > 6) {
            if(!damaged) {
                flyingFor = 0;
                Elysium.getInstance().addChatMessage("Damaged");
                damaged = true;
                mc.thePlayer.jump();
            }
        }
        //System.out.println(mc.thePlayer.hurtTime);

        if(ticksSinceStart > 2) {
            if(mc.thePlayer.inventory.getCurrentItem() == null) {
                this.toggle(); return;
            } else if(!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow)) {
                this.toggle(); return;
            }
        }
        if(flyingFor > -1) flyingFor++;
        ticksSinceStart++;

        if(ticksSinceStart < 7) {
            mc.gameSettings.keyBindUseItem.pressed = true;
        } else {
            if(ticksSinceStart < 12) {
                mc.gameSettings.keyBindUseItem.pressed = false;
            } if(flyingFor >= 9 && damaged && mc.thePlayer.onGround) {
                this.toggle();
            }
        }

        if(!damaged) {
            mc.thePlayer.setPosition(startPos[0], startPos[1], startPos[2]);
        } else if(flyingFor <= 10) {
            MovementUtil.strafe(4);
            mc.thePlayer.motionY = (10-mc.thePlayer.hurtTime)/9F;
        }

        if(ticksSinceStart < 9) {
            e.pitch = -87;
            mc.thePlayer.rotationPitchHead = -87;
        }
    }
}
