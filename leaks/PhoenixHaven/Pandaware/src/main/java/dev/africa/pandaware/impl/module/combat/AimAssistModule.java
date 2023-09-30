package dev.africa.pandaware.impl.module.combat;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.math.vector.Vec2f;
import dev.africa.pandaware.utils.player.RotationUtils;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.util.Comparator;

@Getter
@ModuleInfo(name = "Aim Assist", description = "Aims at children", category = Category.COMBAT)
public class AimAssistModule extends Module {

    private final NumberSetting distance = new NumberSetting("Distance", 10D, 1D, 4D, 0.5D);
    private final NumberSetting smoothing = new NumberSetting("Smoothing", 2F, 0.25F, 1.5F, 0.025F);
    private final EnumSetting<RotationUtils.RotationAt> lookAt
            = new EnumSetting<>("Look At", RotationUtils.RotationAt.HEAD);

    private final BooleanSetting randomizeAimPoint = new BooleanSetting("Randomize Aim Point", false);

    public static int mouseX, mouseY;

    public AimAssistModule() {
        this.registerSettings(
                this.distance,
                this.smoothing,
                this.lookAt,
                this.randomizeAimPoint
        );
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mouseX = mouseY = 0;
        super.onDisable();
    }

    @EventHandler
    EventCallback<MotionEvent> onRender = event -> {
        if (event.getEventState() != Event.EventState.PRE) return;

        double distance = this.distance.getValue().doubleValue();

        EntityPlayer target = mc.theWorld.playerEntities.stream().filter(entityPlayer -> entityPlayer != mc.thePlayer
                        && entityPlayer.isEntityAlive()
                        && mc.thePlayer.canEntityBeSeen(entityPlayer)
                        && entityPlayer.getDistanceSqToEntity(mc.thePlayer) < (distance * distance))
                .min(Comparator.comparingDouble(player -> player.getDistanceSqToEntity(mc.thePlayer))).orElse(null);

        if (target == null) {

            mouseX = mouseY = 0;

            return;
        }

        Vec2f targetRotations = RotationUtils.getRotations(target);
        Vec2f currentRotations = new Vec2f(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

        RotationUtils.RotationAt rotationAt = (this.randomizeAimPoint.getValue() ?
                RotationUtils.RotationAt.values()
                        [RandomUtils.nextInt(0, RotationUtils.RotationAt.values().length - 1)] :
                this.lookAt.getValue());

        float offsetX = MathHelper.wrapAngleTo180_float(targetRotations.getX() - currentRotations.getX());
        float offsetY = MathHelper.clamp_float(
                currentRotations.getY() - RotationUtils.getMiddlePointRotations(target, rotationAt).getY(),
                -90F,
                90F);

        float fpt = Minecraft.getDebugFPS() / 20F;

        float smoothing = this.smoothing.getValue().floatValue();

        float smoothX = (offsetX / smoothing) / fpt;
        float smoothY = (offsetY / smoothing) / fpt;

        float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F;

        mouseX = ApacheMath.round(smoothX / f1);
        mouseY = ApacheMath.round(smoothY / f1);
    };
}