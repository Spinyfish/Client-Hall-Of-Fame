package com.alan.clients.module.impl.movement;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.combat.KillAura;
import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.JumpEvent;
import com.alan.clients.newevent.impl.motion.PreUpdateEvent;
import com.alan.clients.newevent.impl.motion.StrafeEvent;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.rotation.RotationUtil;
import com.alan.clients.util.vector.Vector3d;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.util.List;

/**
 * @author Alan
 * @since 20/10/2021
 */
@Rise
@ModuleInfo(name = "module.movement.targetstrafe.name", description = "module.movement.targetstrafe.description", category = Category.MOVEMENT)
public class TargetStrafe extends Module {

    private final NumberValue range = new NumberValue("Range", this, 1, 0.2, 6, 0.1);

    public final BooleanValue holdJump = new BooleanValue("Hold Jump", this, true);
    private float yaw;
    private EntityLivingBase target;
    private boolean left, colliding;
    private boolean active;

    @EventLink(value = Priorities.HIGH)
    public final Listener<JumpEvent> onJump = event -> {
        if (target != null && active) {
            event.setYaw(yaw);
        }
    };

    @EventLink(value = Priorities.HIGH)
    public final Listener<StrafeEvent> onStrafe = event -> {
        if (target != null && active) {
            event.setYaw(yaw);
        }
    };

    @EventLink(value = Priorities.HIGH)
    public final Listener<PreUpdateEvent> onPreUpdate = event -> {
        // Disable if scaffold is enabled
        Module scaffold = getModule(Scaffold.class);
        Module killaura = getModule(KillAura.class);

        if (scaffold == null || scaffold.isEnabled() || killaura == null || !killaura.isEnabled()) {
            active = false;
            return;
        }

        active = true;
        
        /*
         * Getting targets and selecting the nearest one
         */
        Module speed = getModule(Speed.class);
        Module test = null;
        Module flight = getModule(Flight.class);

        if (holdJump.getValue() && !mc.gameSettings.keyBindJump.isKeyDown() || !(mc.gameSettings.keyBindForward.isKeyDown() &&
                ((flight != null && flight.isEnabled()) || ((speed != null && speed.isEnabled()) || (test != null && test.isEnabled()))))) {
            target = null;
            return;
        }

        final List<EntityLivingBase> targets = Client.INSTANCE.getTargetManager().getTargets(this.range.getValue().doubleValue() + 3);

        if (targets.isEmpty()) {
            target = null;
            return;
        }

        if (mc.thePlayer.isCollidedHorizontally || !PlayerUtil.isBlockUnder(5, false)) {
            if (!colliding) {
                MoveUtil.strafe();
                left = !left;
            }
            colliding = true;
        } else {
            colliding = false;
        }

        target = targets.get(0);

        if (target == null) {
            return;
        }

        float yaw = RotationUtil.calculate(target).getX() + (90 + 45) * (left ? -1 : 1);

        final double range = this.range.getValue().doubleValue();
        final double posX = -MathHelper.sin((float) Math.toRadians(yaw)) * range + target.posX;
        final double posZ = MathHelper.cos((float) Math.toRadians(yaw)) * range + target.posZ;

        yaw = RotationUtil.calculate(new Vector3d(posX, target.posY, posZ)).getX();

        this.yaw = yaw;
        mc.thePlayer.movementYaw = this.yaw;
    };
}