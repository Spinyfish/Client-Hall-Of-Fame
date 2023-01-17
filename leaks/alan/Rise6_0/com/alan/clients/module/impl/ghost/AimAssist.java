package com.alan.clients.module.impl.ghost;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.render.LimitedRender2DEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.BoundsNumberValue;
import com.alan.clients.util.rotation.RotationUtil;
import com.alan.clients.util.vector.Vector2f;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;

import java.util.List;

/**
 * @author Alan
 * @since 29/01/2021
 */

@Rise
@ModuleInfo(name = "Aim Assist", description = "module.ghost.aimassist.description", category = Category.GHOST)
public class AimAssist extends Module {

    private final BoundsNumberValue strength = new BoundsNumberValue("AimAssist Strength", this, 30, 30, 1, 100, 1);
    private final BooleanValue onRotate = new BooleanValue("Only on mouse movement", this, true);
    private Vector2f rotations, lastRotations;

    @Override
    protected void onDisable() {
        EntityRenderer.mouseAddedX = EntityRenderer.mouseAddedY = 0;
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        lastRotations = rotations;
        rotations = null;

        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) return;

        final List<EntityLivingBase> entities = Client.INSTANCE.getTargetManager().getTargets(5);

        if (entities.isEmpty()) {
            return;
        }

        final EntityLivingBase target = entities.get(0);

        rotations = RotationUtil.calculate(target);
    };


    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        if (rotations == null || lastRotations == null ||
                (this.onRotate.getValue() && this.mc.mouseHelper.deltaX == 0 && this.mc.mouseHelper.deltaY == 0)) {
            return;
        }

        Vector2f rotations = new Vector2f(this.lastRotations.x + (this.rotations.x - this.lastRotations.x) * mc.timer.renderPartialTicks, 0);
        final float strength = (float) MathUtil.getRandom(this.strength.getValue().floatValue(), this.strength.getSecondValue().floatValue());

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 8.0F;

        int i = mc.gameSettings.invertMouse ? -1 : 1;
        float f2 = this.mc.mouseHelper.deltaX + ((rotations.x - mc.thePlayer.rotationYaw) * (strength / 100) -
                this.mc.mouseHelper.deltaX) * gcd;
        float f3 = this.mc.mouseHelper.deltaY - this.mc.mouseHelper.deltaY * gcd;

        this.mc.thePlayer.setAngles(f2, f3 * (float) i);
    };
}