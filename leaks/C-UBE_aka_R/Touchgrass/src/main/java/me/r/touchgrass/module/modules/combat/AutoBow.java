package me.r.touchgrass.module.modules.combat;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.events.EventUpdate;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * Created by r on 26/12/2021
 */
@Info(name = "AutoBow", category = Category.Combat, description = "Automatically shoots an arrow, if your bow is fully charged")
public class AutoBow extends Module {

    @EventTarget
    public void onUpdate(EventUpdate e) {
        if(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == Items.bow && mc.thePlayer.isUsingItem() && mc.thePlayer.getItemInUseDuration() > 20) {
            mc.thePlayer.stopUsingItem();
            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }
}
