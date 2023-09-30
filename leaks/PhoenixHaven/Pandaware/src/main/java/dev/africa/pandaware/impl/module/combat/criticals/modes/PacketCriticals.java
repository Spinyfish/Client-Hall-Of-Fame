package dev.africa.pandaware.impl.module.combat.criticals.modes;

import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.combat.criticals.CriticalsModule;
import dev.africa.pandaware.impl.module.combat.criticals.ICriticalsMode;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketCriticals extends ModuleMode<CriticalsModule> implements ICriticalsMode {
    public PacketCriticals(String name, CriticalsModule parent) {
        super(name, parent);
    }

    @Override
    public void handle(MotionEvent event, int ticksExisted) {
        if (PlayerUtils.isMathGround() && ticksExisted % 2 == 0) {
            double value = RandomUtils.nextDouble(0.0615, 0.0645);

            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer
                    .C04PacketPlayerPosition(mc.thePlayer.posX,
                    mc.thePlayer.posY + value, mc.thePlayer.posZ, false));

            mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C03PacketPlayer
                    .C04PacketPlayerPosition(mc.thePlayer.posX,
                    mc.thePlayer.posY, mc.thePlayer.posZ, false));
        }
    }
}
