package me.kansio.client.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.modules.impl.movement.Flight;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.utils.network.PacketUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleData(
        name = "Criticals",
        category = ModuleCategory.COMBAT,
        description = "Automatically deals criticals"
)
public class Criticals extends Module {

    private ModeValue mode = new ModeValue("Mode", this, "Packet", "Verus", "MiniJump", "Jump");
    private final BooleanValue c06 = new BooleanValue("C06", this, true);

    public final double[] packetValues = new double[]{0.0625D, 0.0D, 0.05D, 0.0D};

    @Subscribe
    public void onPacket(PacketEvent event) {
        Flight flight = (Flight) Client.getInstance().getModuleManager().getModuleByName("Flight");

        if (flight.isToggled())
            return;

        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            final C02PacketUseEntity packetUseEntity = event.getPacket();
            final Entity entity = packetUseEntity.getEntityFromWorld(mc.theWorld);
            if (mc.thePlayer.onGround && entity.hurtResistantTime != -1) {
                doCritical();
                entity.hurtResistantTime = -1;
            }

        }
    }

    public void doCritical() {
        switch (mode.getValueAsString()) {
            case "Packet": {
                for (double d : packetValues) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
                }
                break;
            }
            case "Verus": {
                if (!mc.thePlayer.onGround) {
                    return;
                }

                sendPacket(0, 0.11, 0, false);
                sendPacket(0, 0.1100013579, 0, false);
                sendPacket(0, 0.0000013579, 0, false);
                break;
            }
            case "MiniJump": {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = 0.22f;
                }
                break;
            }
            case "Jump": {
                if (!mc.thePlayer.onGround) {
                    return;
                }

                mc.thePlayer.jump();
                break;
            }
        }
    }

    void sendPacket(double xOffset, double yOffset, double zOffset, boolean ground) {
        double x = mc.thePlayer.posX + xOffset;
        double y = mc.thePlayer.posY + yOffset;
        double z = mc.thePlayer.posZ + zOffset;
        if (c06.getValue()) {
            mc.getNetHandler().addToSendQueue(new
                    C03PacketPlayer.C06PacketPlayerPosLook(
                            x,
                            y,
                            z,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch,
                            ground
                    )
            );
        } else {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, ground));
        }
    }

    @Override
    public String getSuffix() {
        return " " + mode.getValueAsString();
    }
}
