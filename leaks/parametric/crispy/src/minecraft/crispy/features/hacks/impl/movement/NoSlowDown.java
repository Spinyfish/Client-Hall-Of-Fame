package crispy.features.hacks.impl.movement;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.player.SpeedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.superblaubeere27.valuesystem.ModeValue;

@HackInfo(name = "NoSlowDown", category = Category.MOVEMENT)
public class NoSlowDown extends Hack {
    public static boolean noslow = false;
    public static ModeValue mode = new ModeValue("Mode", "Vanilla", "NCP", "AAC", "Vanilla");

    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            noslow = true;
            setDisplayName(getName() + " \2477" + mode.getMode() );
            if (mc.thePlayer.isEating() && mc.gameSettings.keyBindUseItem.pressed) {
                if (mode.getObject() == 0) {
                    if (e.isPre()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    } else {
                        mc.playerController.sendUseItem(mc.thePlayer, Minecraft.theWorld, mc.thePlayer.inventory.getCurrentItem());
                    }
                }

                if (mode.getObject() == 1) {
                    mc.thePlayer.setSprinting(false);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        noslow = false;
        super.onDisable();
    }
}
