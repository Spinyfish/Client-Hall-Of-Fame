package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;

public class Ambience extends Module {

    private final EnumValue<Mode> mode = new EnumValue<>("Time", this, Ambience.Mode.values());

    public Ambience(ModuleData moduleData) {
        super(moduleData);
        register(mode);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        mc.theWorld.setWorldTime(mode.getValue().equals(Mode.DAY) ? 6000L : mode.getValue().equals(Mode.NIGHT) ? 18000L : mode.getValue().equals(Mode.SUNSET) ? 13000L : 0L);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }

    @Override
    public String getSuffix() {
        String mode = this.mode.getValue().getDisplayName();
        return " \2477" + mode;
    }

    public enum Mode implements INameable {
        MORNING("Morning"), DAY("Day"), SUNSET("Sunset"), NIGHT("Night");
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
