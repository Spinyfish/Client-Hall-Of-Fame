package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

/**
 * @author Auth
 */

public class NoSlow extends Module {

    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, NoSlow.Mode.values());

    public NoSlow(ModuleData moduleData) {
        super(moduleData);
        register(enumValue);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (enumValue.getValue().equals(Mode.NCP) && mc.thePlayer.isBlocking()) {
            if (event.isPre()) {
                PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
            } else {
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
            }
        }
    }

    public String getSuffix() {
        Mode mode = enumValue.getValue();
        return " \2477" + mode.getDisplayName();
    }

    public enum Mode implements INameable {
        VANILLA("Vanilla"), NCP("NCP");
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
