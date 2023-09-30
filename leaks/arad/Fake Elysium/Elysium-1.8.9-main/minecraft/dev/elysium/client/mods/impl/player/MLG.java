package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventUpdate;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class MLG extends Mod {

    public int stage = 0;

    public MLG() {
        super("MLG","Clutches for you with water bucket", Category.PLAYER);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate e) {
        if (mc.thePlayer.getHeldItem() != null) {
            Item helditem = mc.thePlayer.getHeldItem().getItem();
            if (helditem == Items.water_bucket && mc.thePlayer.fallDistance > 2) {
                BlockPos bu = new BlockPos(mc.thePlayer.posX + mc.thePlayer.motionX*10,
                        mc.thePlayer.posY - 0.7 + mc.thePlayer.motionY,
                        mc.thePlayer.posZ + mc.thePlayer.motionZ*10);
                if (!(bu.getBlock() instanceof BlockAir) && bu.add(0, 1, 0).getBlock() instanceof BlockAir) {
                    mc.thePlayer.rotationPitch = 90;
                    stage = 1;
                }
            }
        }

        if (stage == 3) {
            mc.rightClickMouse();
            stage++;
        }

        if(stage > 3 && mc.thePlayer.isInWater()) {
            mc.rightClickMouse();
            stage = 0;
        } else if(stage > 0) {
            stage++;
        }
    }
}
