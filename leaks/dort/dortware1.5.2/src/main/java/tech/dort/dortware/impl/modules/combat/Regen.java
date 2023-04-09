package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

public class Regen extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Mode", this, Mode.values());
    private final NumberValue packets = new NumberValue("Packets", this, 20, 5, 100, true);
    private final NumberValue ticks = new NumberValue("Ticks", this, 1, 1, 20, true);

    public Regen(ModuleData moduleData) {
        super(moduleData);
        register(mode, packets, ticks);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        switch (mode.getValue()) {
            case VANILLA:
                if (event.isPre() && mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth() - 1 && mc.thePlayer.ticksExisted % ticks.getValue() == 0) {
                    for (int i = 0; i < packets.getValue().intValue(); ++i) {
                        PacketUtil.sendPacket(new C03PacketPlayer(false));
                    }
                }
                break;
            case VERUS:
                if (mc.thePlayer.getHealth() < mc.thePlayer.getMaxHealth() - 1 && mc.thePlayer.ticksExisted % ticks.getValue() == 0 && mc.thePlayer.onGround) {
                    for (double d = -1; d < 0; d += 0.5) {
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, true));
                    }

                    for (int i = 0; i < 32; ++i) {
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                    }
                }
                break;
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (mode.getValue().equals(Mode.VERUS) && event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packetPlayerPosLook = event.getPacket();
            event.forceCancel(Math.abs(packetPlayerPosLook.getX() - mc.thePlayer.posX) < 9 && Math.abs(packetPlayerPosLook.getY() - mc.thePlayer.posY) < 9 && Math.abs(packetPlayerPosLook.getZ() - mc.thePlayer.posZ) < 9);
        }
    }

    enum Mode implements INameable {
        VANILLA("Vanilla"), VERUS("Verus");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
