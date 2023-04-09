package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.movement.MotionUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

public class NoVoid extends Module {

    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, NoVoid.Mode.values());
    private final NumberValue range = new NumberValue("Distance", this, 3, 1, 10, SliderUnit.BLOCKS, true);
    private final BooleanValue booleanValue = new BooleanValue("Fast Fall", this, true);
    private final NumberValue fallSpeed = new NumberValue("Fall Speed", this, 1, 0.1, 3);

    public NoVoid(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, range, fallSpeed, booleanValue);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        switch (enumValue.getValue()) {
            case SETBACK:
                if (event.isPre() && !MotionUtils.isBlockUnder() && mc.thePlayer.fallDistance > range.getValue() && mc.thePlayer.ticksExisted % 10 == 0) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition());
                }
                break;
//            case HIGHJUMP:
//                if (booleanValue.getValue() && !MotionUtils.isBlockUnder() && mc.thePlayer.fallDistance > 5 && mc.thePlayer.fallDistance < 6) {
//                    mc.thePlayer.motionY -= fallSpeed.getValue();
//                }
//
//                if (!MotionUtils.isBlockUnder() && mc.thePlayer.fallDistance > 30) {
//                    mc.thePlayer.fallDistance = 0;
//                    mc.thePlayer.motionY = 4;
//                }
//                break;
            case HIGHJUMP2:
                if (booleanValue.getValue() && !MotionUtils.isBlockUnder() && mc.thePlayer.fallDistance > 5 && mc.thePlayer.fallDistance < 6) {
                    mc.thePlayer.motionY -= fallSpeed.getValue();
                }

                if (!MotionUtils.isBlockUnder() && mc.thePlayer.fallDistance > 10) {
                    mc.thePlayer.fallDistance = 0;
                    mc.thePlayer.motionY = 2.15;
                }
                break;
        }
    }

    public enum Mode implements INameable {
        SETBACK("Flag"), /*HIGHJUMP("High Jump"),*/ HIGHJUMP2("Jump");
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
