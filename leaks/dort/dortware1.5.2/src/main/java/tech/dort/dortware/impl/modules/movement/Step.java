package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.EntityStepEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.exploit.Phase;
import tech.dort.dortware.impl.utils.TimeUtils;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

public class Step extends Module {

    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, Step.Mode.values());
    private final NumberValue height = new NumberValue("Height", this, 1, 1, 10, SliderUnit.BLOCKS);
    private final NumberValue timer = new NumberValue("Timer", this, 0.5, 0.1, 2, false);
    private final NumberValue delay = new NumberValue("Timer Duration", this, 150, 25, 500, SliderUnit.MS, true);

    public Step(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, height, timer, delay);
    }

    @Subscribe
    public void onEntityStep(EntityStepEvent event) {
        if (event.getEntity() instanceof EntityPlayerSP) {
            Mode mode = enumValue.getValue();
            if (mode == Mode.NCP) {
                if (mc.thePlayer.onGround && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava() && !mc.thePlayer.isOnLadder() && !Client.INSTANCE.getModuleManager().get(Phase.class).isToggled() && !Client.INSTANCE.getModuleManager().get(LongJump.class).isToggled()) {
                    if (event.isPre()) {
                        event.setHeight(height.getCastedValue().floatValue());
                    } else {
                        float[] stepValues = new float[]{};
                        final float realHeight = event.getHeight();

                        if (realHeight > .9F) {
                            stepValues = new float[]{0.42F, 0.7532F};
                        } else if (realHeight > 0.5f) {
                            stepValues = new float[]{0.42F, 0.7532F};
                        }
                        for (float value : stepValues) {
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + value, mc.thePlayer.posZ, false));
                        }

                        if (realHeight > 0.5f) {
                            TimeUtils.sleepTimer(mc.timer, timer.getValue().floatValue(), delay.getValue().longValue());
                        }
                    }
                }
            } else if (!event.isPre()) {
                final float realHeight = event.getHeight();
                if (realHeight > 0.5f) {
                    TimeUtils.sleepTimer(mc.timer, timer.getValue().floatValue(), delay.getValue().longValue());
                }
            }

        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (enumValue.getValue().equals(Mode.VANILLA)) {
            if (event.isPre() && !Client.INSTANCE.getModuleManager().get(Phase.class).isToggled())
                mc.thePlayer.stepHeight = height.getCastedValue().floatValue();
            if (Client.INSTANCE.getModuleManager().get(Phase.class).isToggled()) mc.thePlayer.stepHeight = 0.6F;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.stepHeight = 0.6F;
    }

    @Override
    public String getSuffix() {
        return " \2477" + enumValue.getValue().getDisplayName();
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