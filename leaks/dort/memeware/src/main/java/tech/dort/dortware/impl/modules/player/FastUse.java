package tech.dort.dortware.impl.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

public class FastUse extends Module {

    private final NumberValue packets = new NumberValue("Packets", this, 20, 5, 100, true);
    private final EnumValue<Mode> mode = new EnumValue<>("Mode", this, Mode.values());
    private boolean reset;
    private final Stopwatch stopwatch = new Stopwatch();

    public FastUse(ModuleData moduleData) {
        super(moduleData);
        register(mode, packets);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.isUsingItem() && mc.thePlayer.isCollidedVertically && mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion) {
            if (!mode.getValue().equals(Mode.NCP) && !mode.getValue().equals(Mode.REDESKY)) {
                for (int i = 0; i < packets.getCastedValue().intValue(); i++) {
                    switch (mode.getValue()) {
                        case VANILLA:
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer(false));
                            break;

                        case VERUS:
                            if (mc.thePlayer.onGround && !mc.thePlayer.isMoving()) {
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                            }
                            break;
                    }
                }
            } else {
                switch (mode.getValue()) {
                    case NCP:
                        if (!reset) {
                            stopwatch.resetTime();
                            reset = true;
                        }

                        if (stopwatch.timeElapsed(1000L)) {
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
                        }
                        break;

                    case REDESKY:
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer(false));
                        break;
                }
            }
        } else {
            reset = false;
        }
    }

    private enum Mode implements INameable {
        VANILLA("Vanilla"), VERUS("Old Verus"), REDESKY("Redesky"), NCP("NCP");

        private final String displayName;

        Mode(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }
    }

    @Override
    public void onEnable() {
        reset = false;
        super.onEnable();
    }

    @Override
    public String getSuffix() {
        return " \2477" + mode.getValue().getDisplayName();
    }
}
