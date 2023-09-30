package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.player.BoundingBoxEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import net.minecraft.util.AxisAlignedBB;

public final class Jesus extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Verus");

    private boolean shouldJesus;

    public Jesus() {
        super("Jesus", Category.MOVEMENT, "Walks on water, like jesus");
        this.addSettings(mode);
    }

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {
        if(event.getBlock().getMaterial().isLiquid()) {
            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            shouldJesus = true;
            event.setBoundingBox(axisAlignedBB);
        }
    }

    @Override
    public void onMotionEvent(MotionEvent event) {
        setSuffix(mode.getMode());
        if(event.isPre()) {
            if(shouldJesus) {
                switch(mode.getMode()) {
                    case "Verus":
                        if(mc.thePlayer.ticksExisted % 5 == 0)
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ);
                        break;
                }
            }
            shouldJesus = false;
        }
    }
}
