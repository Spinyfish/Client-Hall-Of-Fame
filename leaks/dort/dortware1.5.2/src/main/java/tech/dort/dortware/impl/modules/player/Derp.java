package tech.dort.dortware.impl.modules.player;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.render.Rotate;

public class Derp extends Module {

    private float rotationYaw;

    private final NumberValue yawSpeed = new NumberValue("Yaw Speed", this, 20, 10, 50, true);

    public Derp(ModuleData moduleData) {
        super(moduleData);
        register(yawSpeed);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.objectMouseOver != null && mc.thePlayer.isSwingInProgress)
            return;

        float speed = yawSpeed.getCastedValue().floatValue();

        rotationYaw -= speed;

        if (rotationYaw < -180) {
            rotationYaw = 180;
        }

        event.setRotationYaw(rotationYaw);
        event.setRotationPitch(180.0F);

        if (Client.INSTANCE.getModuleManager().get(Rotate.class).isToggled()) {
            mc.thePlayer.renderYawOffset = rotationYaw;
            mc.thePlayer.renderYawHead = rotationYaw;
            mc.thePlayer.renderPitchHead = 180.0F;
        }
    }
}
