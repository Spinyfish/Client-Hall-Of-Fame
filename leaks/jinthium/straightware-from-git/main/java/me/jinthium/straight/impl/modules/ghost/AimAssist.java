package me.jinthium.straight.impl.modules.ghost;

import best.azura.irc.utils.RandomUtil;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.entity.EntityValidator;
import me.jinthium.straight.impl.utils.entity.impl.AliveCheck;
import me.jinthium.straight.impl.utils.entity.impl.ConstantDistanceCheck;
import me.jinthium.straight.impl.utils.entity.impl.EntityCheck;
import me.jinthium.straight.impl.utils.entity.impl.TeamsCheck;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.RandomUtils;
import org.lwjglx.input.Mouse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AimAssist extends Module {

    private final NumberSetting strength = new NumberSetting("Strength", 30, 1, 100, 1);
    private final BooleanSetting onRotate = new BooleanSetting("Only on mouse movement", true);
    private Vector2f rotations, lastRotations;

    public final List<EntityLivingBase> targets = new ArrayList<>();
    public EntityValidator entityValidator = new EntityValidator();
    public EntityLivingBase target;

    public AimAssist(){
        super("Aim Assist", Category.GHOST);
        this.addSettings(strength, onRotate);
    }

    @Override
    public void onEnable() {
        target = null;
        targets.clear();
        EntityRenderer.mouseAddedX = EntityRenderer.mouseAddedY = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        target = null;
        targets.clear();
        EntityRenderer.mouseAddedX = EntityRenderer.mouseAddedY = 0;
        super.onDisable();
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(!event.isPre())
            return;

        this.setSuffix(strength.getValue().toString());

        lastRotations = rotations;
        rotations = null;

        if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) return;

        entityValidator = new EntityValidator();
        final AliveCheck aliveCheck = new AliveCheck();
        final EntityCheck entityCheck = new EntityCheck(true, false, false, false);
        final TeamsCheck teamsCheck = new TeamsCheck(true);
        entityValidator.add(aliveCheck);
        entityValidator.add(new ConstantDistanceCheck(5));
        entityValidator.add(entityCheck);
        entityValidator.add(teamsCheck);
        updateTargets();
        target = getTarget();


        if(target == null) return;

        rotations = RotationUtils.calculate(target);
    };

    public EntityLivingBase getTarget() {
        if (targets.isEmpty()) {
            return null;
        }

        for (EntityLivingBase bot : KillAura.bots) {
            if(targets.contains(bot))
                return null;
        }

        targets.sort(new DistanceSorter());

        return targets.get(0);
    }

    private final static class DistanceSorter implements Comparator<EntityLivingBase> {
        public int compare(EntityLivingBase o1, EntityLivingBase o2) {
            return -Double.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2));
        }
    }

    private void updateTargets() {
        targets.clear();

        final List<Entity> entities = mc.theWorld.loadedEntityList;

        for (final Entity entity : entities) {
            if (entity instanceof EntityLivingBase entityLivingBase) {
                if (entityValidator.validate(entityLivingBase) && mc.theWorld.loadedEntityList.contains(entityLivingBase)) {
                    this.targets.add(entityLivingBase);
                }
            }
        }
    }


    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        if (mc.currentScreen != null || !Mouse.isButtonDown(0) || rotations == null || lastRotations == null ||
                (this.onRotate.isEnabled() && this.mc.mouseHelper.deltaX == 0 && this.mc.mouseHelper.deltaY == 0)) {
            return;
        }

        Vector2f rotations = new Vector2f(this.lastRotations.x + (this.rotations.x - this.lastRotations.x) * mc.timer.renderPartialTicks, 0);
        final float strength = (float) RandomUtils.nextFloat(this.strength.getValue().floatValue(), this.strength.getValue().floatValue() +  RandomUtils.nextFloat(1, 10));

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 8.0F;

        int i = mc.gameSettings.invertMouse ? -1 : 1;
        float f2 = this.mc.mouseHelper.deltaX + ((rotations.x - mc.thePlayer.rotationYaw) * (strength / 100) -
                this.mc.mouseHelper.deltaX) * gcd;
        float f3 = this.mc.mouseHelper.deltaY - this.mc.mouseHelper.deltaY * gcd;

        this.mc.thePlayer.setAngles(f2, f3 * (float) i);
//        mc.thePlayer.rotationYaw = f2;
//        mc.thePlayer.rotationPitch = f3 * i;
    };
}
