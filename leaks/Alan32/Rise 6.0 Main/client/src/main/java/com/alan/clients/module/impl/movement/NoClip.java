package com.alan.clients.module.impl.movement;

import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.player.RotationComponent;
import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.component.impl.player.rotationcomponent.MovementFix;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.motion.PushOutOfBlockEvent;
import com.alan.clients.newevent.impl.other.BlockAABBEvent;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.player.SlotUtil;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.impl.BooleanValue;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;

@Rise
@ModuleInfo(name = "module.movement.noclip.name", description = "module.movement.noclip.description", category = Category.MOVEMENT)
public class NoClip extends Module {

    private final BooleanValue block = new BooleanValue("Block", this, false);

    @Override
    protected void onDisable() {
        mc.thePlayer.noClip = false;
    }

    @EventLink()
    public final Listener<BlockAABBEvent> onBlockAABB = event -> {

        if (PlayerUtil.insideBlock()) {
            event.setBoundingBox(null);

            // Sets The Bounding Box To The Players Y Position.
            if (!(event.getBlock() instanceof BlockAir) && !mc.gameSettings.keyBindSneak.isKeyDown()) {
                final double x = event.getBlockPos().getX(), y = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

                if (y < mc.thePlayer.posY) {
                    event.setBoundingBox(AxisAlignedBB.fromBounds(-15, -1, -15, 15, 1, 15).offset(x, y, z));
                }
            }
        }
    };

    @EventLink()
    public final Listener<PushOutOfBlockEvent> onPushOutOfBlock = event -> {
        event.setCancelled(true);
    };

    @EventLink()
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {

        mc.thePlayer.noClip = true;

        if (block.getValue()) {
            final int slot = SlotUtil.findBlock();

            if (slot == -1) {
                return;
            }

            SlotComponent.setSlot(slot, true);

            RotationComponent.setRotations(new Vector2f(mc.thePlayer.rotationYaw, 90), 2 + Math.random(), MovementFix.NORMAL);

            if (RotationComponent.rotations.y >= 89 &&
                    mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
                    mc.thePlayer.posY == mc.objectMouseOver.getBlockPos().up().getY()) {

                mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, SlotComponent.getItemStack(),
                        mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);

                mc.thePlayer.swingItem();
            }
        }
    };
}