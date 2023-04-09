package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

public class NoFall extends Module {

    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, NoFall.Mode.values());

    public NoFall(ModuleData moduleData) {
        super(moduleData);
        register(enumValue);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (event.isPre() && mc.thePlayer.fallDistance > 2F) {
            switch (enumValue.getValue()) {
                case EDIT: {
                    event.setOnGround(true);
                }
                break;
                case PACKET: {
                    PacketUtil.sendPacket(new C03PacketPlayer(true));
                    break;
                }
                case VERUS: {
                    double y = Math.round(mc.thePlayer.posY / 0.015625) * 0.015625;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ);
                    event.setOnGround(true);
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.fallDistance = 0;
                    break;
                }
                case HYPIXEL: {
                    event.setOnGround(mc.thePlayer.ticksExisted % 2 == 0);
                    break;
                }
            }
        }
    }

    public String getSuffix() {
        return " \2477" + enumValue.getValue().getDisplayName();
    }

    public enum Mode implements INameable {
        EDIT("Edit"), PACKET("Packet"), VERUS("Verus"), HYPIXEL("Hypixel");
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