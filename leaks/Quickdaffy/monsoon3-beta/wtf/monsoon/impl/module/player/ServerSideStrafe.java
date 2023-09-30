/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;
import wtf.monsoon.impl.module.combat.TargetStrafe;
import wtf.monsoon.impl.module.player.Scaffold;

public class ServerSideStrafe
extends Module {
    private float targetYaw;
    private float lastYaw;
    private final Setting<Boolean> strafeMotion = new Setting<Boolean>("Strafe Motion", false).describedBy("Whether to set your motion to strafing.");
    private final Setting<Boolean> whileAura = new Setting<Boolean>("While Aura Enabled", false).describedBy("Whether to set your rotation while Aura is attacking.");
    private final Setting<Boolean> whileScaffold = new Setting<Boolean>("While Scaffold Enabled", false).describedBy("Whether to set your rotation while Scaffold is enabled.");
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (!(!this.player.isMoving() || Wrapper.getModule(Scaffold.class).isEnabled() && this.whileScaffold.getValue().booleanValue() || Wrapper.getModule(Aura.class).isEnabled() && Wrapper.getModule(Aura.class).getTarget() != null && this.whileAura.getValue().booleanValue())) {
            this.targetYaw = e.getYaw();
            if (this.mc.gameSettings.keyBindBack.pressed) {
                this.targetYaw += 180.0f;
                if (this.mc.gameSettings.keyBindLeft.pressed) {
                    this.targetYaw += 45.0f;
                }
                if (this.mc.gameSettings.keyBindRight.pressed) {
                    this.targetYaw -= 45.0f;
                }
            } else if (this.mc.gameSettings.keyBindForward.pressed) {
                if (this.mc.gameSettings.keyBindLeft.pressed) {
                    this.targetYaw -= 45.0f;
                }
                if (this.mc.gameSettings.keyBindRight.pressed) {
                    this.targetYaw += 45.0f;
                }
            } else {
                if (this.mc.gameSettings.keyBindLeft.pressed) {
                    this.targetYaw -= 90.0f;
                }
                if (this.mc.gameSettings.keyBindRight.pressed) {
                    this.targetYaw += 90.0f;
                }
            }
            if (Wrapper.getModule(TargetStrafe.class).isEnabled() && this.mc.gameSettings.keyBindJump.isKeyDown() && Wrapper.getModule(Aura.class).getTarget() != null && (double)Wrapper.getModule(Aura.class).getTarget().getDistanceToEntity(this.mc.thePlayer) < Wrapper.getModule(Aura.class).getRange().getValue() && TargetStrafe.canMove) {
                this.targetYaw = MathHelper.wrapAngleTo180_float(Wrapper.getModule(TargetStrafe.class).currentYaw);
            }
            this.mc.thePlayer.rotationYawHead = this.mc.thePlayer.renderYawOffset = (this.targetYaw = this.interpolateRotation(this.lastYaw, this.targetYaw, 45.0f));
            e.setYaw(this.mc.thePlayer.renderYawOffset);
            this.lastYaw = this.targetYaw;
            if (this.strafeMotion.getValue().booleanValue()) {
                double baseSpeed = 0.221;
                if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    baseSpeed *= 1.0 + 0.2 * (double)(this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
                }
                this.player.setSpeed(baseSpeed);
            }
        }
    };

    public ServerSideStrafe() {
        super("Server Side Strafe", "Sets your rotation so you strafe server side.", Category.PLAYER);
    }

    private float interpolateRotation(float prev, float now, float maxTurn) {
        float var4 = MathHelper.wrapAngleTo180_float(now - prev);
        if (var4 > maxTurn) {
            var4 = maxTurn;
        }
        if (var4 < -maxTurn) {
            var4 = -maxTurn;
        }
        return prev + var4;
    }
}

