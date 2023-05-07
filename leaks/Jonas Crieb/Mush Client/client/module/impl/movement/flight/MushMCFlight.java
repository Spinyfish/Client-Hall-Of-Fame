package client.module.impl.movement.flight;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.event.impl.motion.StrafeEvent;
import client.module.impl.movement.Flight;
import client.util.player.MoveUtil;
import client.value.Mode;
import client.value.impl.NumberValue;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class MushMCFlight extends Mode<Flight> {

    //private final NumberValue speed = new NumberValue("Speed", this, 1, 0.1, 9.5, 0.1);

    public MushMCFlight(String name, Flight parent) {
        super(name, parent);
    }

    public void verusDamage() {
        mc.getNetHandler().addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5D, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem((World)mc.theWorld, new BlockPos(-1, -1, -1))), 0.0F, 0.94F, 0.0F));
        mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.05D, mc.thePlayer.posZ, false));
        mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.getNetHandler().addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688697815D, mc.thePlayer.posZ, true));
    }
    @Override
    public void onEnable() {
      //  verusDamage();
        super.onEnable();
    }

    @EventLink()
    public final Listener<MotionEvent> onMotion = event -> {

        mc.thePlayer.motionY = 0.4641593749554431f;
        mc.thePlayer.prevChasingPosY = mc.thePlayer.lastReportedPosY;
        mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1F : mc.gameSettings.keyBindSneak.isKeyDown() ? -1F : 0.0;
        mc.thePlayer.isAirBorne = true;
        MoveUtil.strafe(2.6753);

    };
}
