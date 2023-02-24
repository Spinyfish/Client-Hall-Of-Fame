package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.movement.Flight;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.block.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;

@ModuleData(
        name = "Anti Void",
        category = ModuleCategory.PLAYER,
        description = "Prevents you from falling into the void"
)
public class AntiVoid extends Module {

    //values for storing the previous pos (when they weren't in the void)
    double prevX = 0;
    double prevY = 0;
    double prevZ = 0;

    private final ModeValue modeValue = new ModeValue("Mode", this, "Basic", "Blink");
    private final NumberValue fallDist = new NumberValue<>("Fall Distance", this, 7, 0, 30, 1);

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        //save a safe position to teleport to
        if (!(BlockUtil.getBlockAt(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)) instanceof BlockAir)) {
            prevX = mc.thePlayer.posX;
            prevY = mc.thePlayer.posY;
            prevZ = mc.thePlayer.posZ;
        }

        //check if they should be teleported back to the safe position
        if (shouldTeleportBack()) {
            mc.thePlayer.setPositionAndUpdate(prevX, prevY, prevZ);

            //set the motion to 0
            mc.thePlayer.motionZ = 0;
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionY = 0;
        }
    }

    public boolean shouldTeleportBack() {
        Flight flight = (Flight) Client.getInstance().getModuleManager().getModuleByName("Flight");

        //don't antivoid while using flight
        if (flight.isToggled())
            return false;

        //don't teleport them if they're on the ground
        if (mc.thePlayer.onGround)
            return false;

        //don't teleport them if they're collided vertically
        if (mc.thePlayer.isCollidedVertically)
            return false;


        //check if fall distance is greater than fall distance required to tp back
        if (mc.thePlayer.fallDistance >= fallDist.getValue().doubleValue()) {
            //loop through all the blocks below the player
            for (double i = mc.thePlayer.posY + 1; i > 0; i--) {
                Block below = BlockUtil.getBlockAt(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - i, mc.thePlayer.posZ));

                //if it's air this returns true.
                if (!(below instanceof BlockAir))
                 return false;
            }
            //return true because there aren't any blocks under the player
            return true;
        }
        return false;
    }

}
