package de.lirium.impl.module.impl.visual;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.impl.events.OrientCameraEvent;
import de.lirium.impl.events.OrientCameraRotateEvent;
import de.lirium.impl.events.RotateEvent;
import de.lirium.impl.module.ModuleFeature;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

@ModuleFeature.Info(name = "Free Look", description = "Lets you look around freely without changing your actualy view", category = ModuleFeature.Category.VISUAL)
public class FreeLookFeature extends ModuleFeature {

    @Value(name = "Force Thirdperson")
    private final CheckBox forceThirdperson = new CheckBox(false);

    private float yaw, pitch;

    private int thirdPersonMode;

    @EventHandler
    private final Listener<RotateEvent> rotateEventListener = e -> {
        if (forceThirdperson.getValue()) {
            getGameSettings().thirdPersonView = 1;
        }

        if (getGameSettings().thirdPersonView != 0) {
            e.stopRotate = true;
            float f = getGameSettings().mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            float f2 = (float) e.deltaX * f1;
            float f3 = (float) e.deltaY * f1;
            int i = 1;
            if (getGameSettings().invertMouse) {
                i = -1;
            }
            yaw += (float) ((double) f2 * 0.15D);
            pitch += (float) ((double) f3 * i * 0.15D);
            pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        } else {
            yaw = 0;
            pitch = 0;
        }
    };

    @EventHandler
    private final Listener<OrientCameraRotateEvent> orientCameraEventListener = e -> {
        if (getGameSettings().thirdPersonView != 0) {
            e.yaw += yaw;
            e.pitch += pitch;
            e.pitch = MathHelper.clamp(e.pitch, -90F, 90F);
        }
    };

    @Override
    public void onEnable() {
        thirdPersonMode = getGameSettings().thirdPersonView;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (forceThirdperson.getValue()) {
            getGameSettings().thirdPersonView = thirdPersonMode;
        }
        super.onDisable();
    }
}
