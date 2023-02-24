package me.kansio.client.modules.impl.movement.flight.verus;

import me.kansio.client.event.impl.BlockCollisionEvent;
import me.kansio.client.event.impl.MoveEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.impl.movement.flight.FlightMode;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.AxisAlignedBB;

public class VerusDamage2 extends FlightMode {

    private double startY;
    private double veroos = 2.5;

    public VerusDamage2() {
        super("Verus Longjump");
    }

    public void onEnable() {
        PlayerUtil.damageVerus();
        veroos = .22;
        startY = mc.thePlayer.posY - 1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onUpdate(UpdateEvent event) {



        if (mc.thePlayer.hurtTime > 8) {
            veroos = getFlight().getSpeed().getValue();
        }

        if (mc.thePlayer.hurtResistantTime < 3) {
            veroos = 0.22f;
        }

        /* else if (boosted && mc.thePlayer.hurtResistantTime < 2) {
            veroos = 0.22f;
        } else if (boosted) {
            veroos -= 0.01;
        }*/


    }

    @Override
    public void onMove(MoveEvent event) {
        if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
            event.setMotionY(mc.thePlayer.motionY = PlayerUtil.getMotion(0.42f));
        }
        PlayerUtil.setMotion(event, veroos);
    }

    @Override
    public void onCollide(BlockCollisionEvent event) {
        if (event.getBlock() instanceof BlockAir && event.getY() <= startY) {
            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();
            event.setAxisAlignedBB(AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1.0F, 5).offset(x, y, z));
        }
    }
}
