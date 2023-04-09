package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.UpdateEvent;

/**
 * @author Auth
 */

public class ViewBobbing extends Module {

    private final NumberValue numberValue1 = new NumberValue("Bobbing", this, 0.1, 0.1, 0.5);
    private final BooleanValue booleanValue = new BooleanValue("Ground Check", this, true);

    public ViewBobbing(ModuleData moduleData) {
        super(moduleData);
        register(numberValue1, booleanValue);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (booleanValue.getValue()) {
            if (mc.thePlayer.isMovingOnGround())
                mc.thePlayer.cameraYaw = numberValue1.getValue().floatValue();
        } else {
            mc.thePlayer.cameraYaw = numberValue1.getValue().floatValue();
        }
    }
}