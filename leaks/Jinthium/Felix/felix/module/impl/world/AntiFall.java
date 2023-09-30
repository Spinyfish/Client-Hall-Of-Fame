package felix.module.impl.world;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.module.impl.movement.Flight;
import felix.value.impl.NumberValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class AntiFall extends Module {

    public NumberValue<Float> distance = new NumberValue<>("Distance", 6F, 1F, 10F, 1F);

    public AntiFall() {
        super("AntiFall", 0, ModuleCategory.WORLD);
        setHidden(true);
        addValues(distance);
    }

    @Handler
    public void onEvent(final EventMotionUpdate eventMotion) {
        if (eventMotion.isPre()) {
            if (mc.thePlayer.fallDistance > distance.getValue() && !Client.INSTANCE.getModuleManager().getModule(Flight.class).isEnabled()) {
                if (!isBlockUnder()) {
                    mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 12, mc.thePlayer.posZ, false));
                    mc.thePlayer.fallDistance = 0;
                }
            }
        }
    }

    private boolean isBlockUnder() {
        if (mc.thePlayer.posY < 0)
            return false;
        for (int offset = 0; offset < (int) mc.thePlayer.posY + 2; offset += 2) {
            AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
