/**
 * @project Myth
 * @author CodeMan
 * @at 04.11.22, 19:09
 */
package dev.myth.features.movement;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.events.MoveEvent;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import net.minecraft.potion.Potion;

@Feature.Info(
        name = "Step",
        description = "Allows you to step up blocks",
        category = Feature.Category.MOVEMENT
)
public class StepFeature extends Feature {

    private int state;
    private float startYaw;

    private SpeedFeature speedFeature;

    @Handler
    public final Listener<MoveEvent> moveEventListener = event -> {

        if(speedFeature == null) {
            speedFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(SpeedFeature.class);
        }

        if(state > 3) {
            state = 0;
            return;
        }

        if(getPlayer().isPotionActive(Potion.jump)) return;
        if(speedFeature.isEnabled()) return;

        if (getPlayer().isCollidedHorizontally && MovementUtil.isOnGround() && MovementUtil.isMoving()) {
            startYaw = MovementUtil.getDirection();
            double yaw = Math.toRadians(startYaw);
            double x2 = -Math.sin(yaw) * 0.4;
            double z2 = Math.cos(yaw) * 0.4;
            if(!getWorld().getCollisionBoxes(getPlayer().getEntityBoundingBox().offset(x2, 1, z2)).isEmpty() || getWorld().getCollisionBoxes(getPlayer().getEntityBoundingBox().offset(x2, 0.5, z2)).isEmpty()) {
                return;
            }
            MovementUtil.fakeJump();
            event.setY(getPlayer().motionY = 0.42F);
        } else {
            if(state == 0) return;

            if(state == 1) {
                event.setY(getPlayer().motionY = 0.33319999363422365);
            } else if(state == 2) {
                event.setY(getPlayer().motionY = 0.24813599859094576);
            } else if(state == 3) {
                event.setY(getPlayer().motionY = 0);
            }
        }
        event.setX(0);
        event.setZ(0);
        if(state == 3) {
            double yaw = Math.toRadians(startYaw);
            event.setX(MC.thePlayer.motionX = -Math.sin(yaw) * 0.2);
            event.setZ(MC.thePlayer.motionZ = Math.cos(yaw) * 0.2);
        }
        state++;
    };

}
