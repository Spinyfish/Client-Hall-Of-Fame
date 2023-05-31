package de.lirium.impl.module.impl.combat;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.animation.Animation;
import de.lirium.base.animation.Easings;
import de.lirium.base.drag.DragHandler;
import de.lirium.base.drag.Draggable;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.*;
import de.lirium.impl.module.ModuleManager;
import de.lirium.impl.module.impl.misc.FriendFeature;
import de.lirium.util.random.RandomUtil;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.rotation.RotationUtil;
import de.lirium.impl.module.ModuleFeature;
import god.buddy.aot.BCompiler;
import lombok.SneakyThrows;
import me.felix.friends.FriendData;
import me.felix.util.dropshadow.JHLabsShaderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ModuleFeature.Info(name = "Aura", description = "Attack entities automatically", category = ModuleFeature.Category.COMBAT)
public class AuraFeature extends ModuleFeature {

    /* Attack Settings */
    @Value(name = "Range")
    private final SliderSetting<Double> range = new SliderSetting<>(3D, 1D, 6D);

    @Value(name = "Perfect Hit")
    private final CheckBox perfectHit = new CheckBox(true);

    @Value(name = "Cps")
    private final SliderSetting<Double> cps = new SliderSetting<>(20D, 1D, 20D, new Dependency<>(perfectHit, false));

    @Value(name = "Randomize CPS")
    private final CheckBox randomizeCps = new CheckBox(true, new Dependency<>(perfectHit, false));

    @Value(name = "Smart CPS")
    private final CheckBox smartCPS = new CheckBox(false, new Dependency<>(perfectHit, false));

    @Value(name = "Raycast")
    private final CheckBox raycast = new CheckBox(false);

    /* Target Settings */
    @Value(name = "Max Targets")
    private final SliderSetting<Integer> maxTargets = new SliderSetting<>(1, 1, 10);

    /* Rotation Settings */
    @Value(name = "Rotations")
    private final ComboBox<String> rotations = new ComboBox<>("Normal", new String[]{"None"});

    @Value(name = "Mouse Sensitivity")
    private final CheckBox mouseSensitivity = new CheckBox(true);

    @Value(name = "Heuristics")
    private final CheckBox heuristics = new CheckBox(false);

    @Value(name = "Prediction")
    private final CheckBox prediction = new CheckBox(true);

    /* Movement Modification */
    @Value(name = "Hit Slow")
    private final CheckBox hitSlow = new CheckBox(true);

    /* Misc */
    @Value(name = "Time Shift")
    private final CheckBox timeShift = new CheckBox(false);

    @Value(name = "Cap Ticks")
    private final CheckBox capTicks = new CheckBox(false, new Dependency<>(timeShift, true));

    @Value(name = "Only when needed")
    private final CheckBox onlyWhenNeeded = new CheckBox(false, new Dependency<>(timeShift, true));

    @Value(name = "Max Time Delay")
    private final SliderSetting<Double> maxTimeDelay = new SliderSetting<>(0.2, 0.01, 3.0, new Dependency<>(timeShift, true));

    /* Rendering */
    @Value(name = "TargetHUD", visual = true)
    private final CheckBox targetHUD = new CheckBox(true);

    @Value(name = "Particles", visual = true)
    private final CheckBox particles = new CheckBox(true);

    /* Fields */
    private final CopyOnWriteArrayList<EntityLivingBase> targetsCopyOnWrite = new CopyOnWriteArrayList<>();

    private final TimeHelper timeHelper = new TimeHelper();
    private final Animation animation = new Animation();

    private double balance, lastTime;
    private float animationX = 0;


    public FontRenderer fontRenderer = null, fontRenderer2 = null;

    public Draggable drag = DragHandler.setupDrag(this, "TargetHUD", 100, 100, false);

    private final Color targetHUDColor = new Color(147, 60, 176), targetHUDBackground = new Color(40, 40, 40), targetHUDBackground2 = new Color(30, 30, 30);

    @EventHandler
    public final Listener<HitSlowEvent> hitSlowEvent = e -> {
        if (!hitSlow.getValue()) {
            e.unSprint = false;
            e.slowdown = 1;
            getPlayer().setSprinting(true);
        }
    };

    @EventHandler
    public final Listener<Render2DEvent> render2DEvent = e -> {
        if (targetHUD.getValue()) {
            animation.update();
            if (!targets.isEmpty() || mc.currentScreen instanceof GuiChat) {
                if (fontRenderer == null || fontRenderer2 == null) {
                    fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 23);
                    fontRenderer2 = Client.INSTANCE.getFontLoader().get("arial", 16);
                }
                final EntityLivingBase target = mc.currentScreen instanceof GuiChat ? getPlayer() : targets.get(0);

                final float healthPercentage = target.getHealth() / target.getMaxHealth();

                final float x = drag.getXPosition(), y = drag.getYPosition();
                final String text = Math.round(target.getHealth()) + "/" + Math.round(target.getMaxHealth());

                final float calcWidth = fontRenderer.getStringWidth(text) + 50;

                animationX = (float) RenderUtil.getAnimationState(animationX, ((calcWidth) * healthPercentage), 88);

                drag.setObjectWidth(calcWidth + 30);
                drag.setObjectHeight(37);

                animation.animate(0, 0.05, Easings.BOUNCE_IN, false);


                //sendMessage(animation.getValue());

                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glTranslated(-animation.getValue() * 20, animation.getValue() * 20, 0);
                GL11.glRotatef(animation.getValue() * 20, 1, 1, 0);
                JHLabsShaderRenderer.renderShadow((int) x, (int) y, (int) calcWidth, 40, 12, Color.BLACK);
                RenderUtil.drawRoundedRect(x, y, calcWidth + 30, 40, 4, targetHUDBackground2);
                JHLabsShaderRenderer.renderShadow((int) x + 6, (int) y + 28, (int) animationX, 5, 12, targetHUDColor);
                RenderUtil.drawRoundedRect(x + 6, y + 28, calcWidth, 5, 2, targetHUDBackground);
                RenderUtil.drawRoundedRect(x + 6, y + 28, animationX, 5, 2, targetHUDColor);

                fontRenderer2.drawString(text, x + calcWidth + 30 - fontRenderer2.getStringWidth(text) - 3, y + 25, -1);
                fontRenderer.drawString(target.getName(), x + 6, y + 6, -1);
                GL11.glPopMatrix();
            } else {
                animation.setValue(3);
            }
        }
    };

    public int calculateMiddle(String text, FontRenderer fontRenderer, double x, double width) {
        return (int) ((float) (x + width) - (fontRenderer.getStringWidth(text) / 2f) - (float) width / 2);
    }


    @EventHandler
    public final Listener<AttackEvent> attackEvent = e -> {
        if (targets.size() < maxTargets.getValue()) {
            getWorld().loadedEntityList.stream().filter(entity -> !targets.contains(entity)).forEach(entity -> {
                if (entity != null && entity instanceof EntityLivingBase) {
                    final EntityLivingBase livingBase = (EntityLivingBase) entity;
                    if (isValid(livingBase)) {
                        targets.add(livingBase);
                        if (targets.size() >= maxTargets.getValue())
                            return;
                    }
                }
            });
        }


        targetsCopyOnWrite.clear();
        targetsCopyOnWrite.addAll(targets);
        if (isReady()) {
            int index = 0;
            for (EntityLivingBase target : targetsCopyOnWrite) {
                final double calcYaw = target.rotationYaw - RotationUtil.getYaw(getPlayer(), target); //Distance zwischen Best Rot und Rotation -> Wenn er schauen kann dann aktion
                if (smartCPS.getValue() && target.hurtTime > 6 && ((target.prevPosX != target.posX) || (target.prevPosZ != target.posZ))) {
                    if (calcYaw > 90)
                        continue;
                }
                if (isValid(target)) {
                    if (index++ < maxTargets.getValue())
                        attack(target);
                } else {
                    targets.remove(target);
                }
            }
        }
    };

    @EventHandler
    public final Listener<MouseOverEvent> mouseOverEventListener = e -> {
        e.maxRange = range.getValue();
        e.blockReach = range.getValue() + 1.5;
    };

    @EventHandler
    public final Listener<GameLoopEvent> gameLoopEventListener = e -> {
        if (timeShift.getValue()) {
            e.capTicks = capTicks.getValue();
        }
    };

    @EventHandler
    public final Listener<TimeEvent> timeEventListener = this::handleTimeShift;

    @EventHandler
    public final Listener<RotationEvent> rotationEvent = e -> {
        if (!targets.isEmpty()) {
            final float[] rotations = RotationUtil.getRotation(targets.get(0), mouseSensitivity.getValue(), heuristics.getValue(), prediction.getValue());
            if (!this.rotations.getValue().equals("None")) {
                e.yaw = rotations[0];
                e.pitch = rotations[1];
            }
        }
    };

    private void attack(Entity target) {
        timeHelper.reset();
        if (!raycast.getValue() || (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null))
            if (mc.leftClickCounter <= 0) {
                Entity curTarget = target;
                if (raycast.getValue())
                    curTarget = mc.objectMouseOver.entityHit;
                if (!getPlayer().isRowingBoat()) {
                    if (ViaMCP.getInstance().getVersion() <= ProtocolCollection.R1_8.getVersion().getVersion())
                        getPlayer().swingArm(EnumHand.MAIN_HAND);
                    mc.playerController.attackEntity(getPlayer(), curTarget);
                    if (ViaMCP.getInstance().getVersion() > ProtocolCollection.R1_8.getVersion().getVersion())
                        getPlayer().swingArm(EnumHand.MAIN_HAND);

                    if (particles.getValue()) {
                        for (int i = 0; i < 5; i++) {
                            getWorld().spawnParticle(EnumParticleTypes.CRIT_MAGIC, target.posX, target.posY + target.height / 2, target.posZ, RandomUtil.getGaussian(0.2), RandomUtil.getGaussian(0.5), RandomUtil.getGaussian(0.2));
                        }
                    }
                }
            }
    }

    @SneakyThrows
    private boolean isReady() {
        if (perfectHit.getValue()) {
            float f = (float) getPlayer().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            float f2 = getPlayer().getCooledAttackStrength(0.5F);
            f = f * (0.2F + f2 * f2 * 0.8F);
            return f >= 1;
        } else {
            final double cps = this.cps.getValue() > 10 ? this.cps.getValue() + 5 : this.cps.getValue();
            long calcCPS = (long) (1000 / cps);
            if (randomizeCps.getValue())
                calcCPS += SecureRandom.getInstanceStrong().nextGaussian() * 50;
            return (timeHelper.hasReached(calcCPS, false));
        }
    }

    private boolean isValid(EntityLivingBase entity) {
        if (entity == null) return false;
        if (!ModuleManager.getTarget().canAttack(entity)) return false;
        if (entity == getPlayer()) return false;
        if (entity.isDead || entity.deathTime != 0) return false;
        if (getRange(entity) > range.getValue()) return false;
        return true;
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void handleTimeShift(TimeEvent e) {
        if (timeShift.getValue() && getPlayer() != null && mc.world != null) {
            boolean timeShift = false;
            if (!targets.isEmpty()) {
                for (EntityLivingBase target : targets) {
                    final Vec3d bestVector = RotationUtil.getBestVector(target.getPositionEyes(1F), getPlayer().getEntityBoundingBox());
                    double x = bestVector.xCoord - target.posX;
                    double y = bestVector.yCoord - (target.posY + (double) target.getEyeHeight());
                    double z = bestVector.zCoord - target.posZ;
                    double d3 = (double) MathHelper.sqrt(x * x + z * z);
                    float f = (float) (MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
                    float f1 = (float) (-(MathHelper.atan2(y, d3) * (180D / Math.PI)));
                    if (Math.abs(MathHelper.wrapDegrees(target.rotationYaw) - f) <= 90 && onlyWhenNeeded.getValue())
                        timeShift = true;
                }
            }

            if (targets.isEmpty() || getPlayer().getPositionEyes(1.0f).distanceTo(RotationUtil.getBestVector(getPlayer().getPositionEyes(1F),
                    targets.get(0).getEntityBoundingBox())) > range.getValue() || (!isReady() && perfectHit.getValue()) || (onlyWhenNeeded.getValue() && !perfectHit.getValue() && !timeShift)) {
                if (balance / 1000 < maxTimeDelay.getValue()) {
                    balance += Minecraft.getSystemTime() - lastTime;
                }
            } else if (!targets.isEmpty() && (getPlayer().getPositionEyes(1.0f).distanceTo(RotationUtil.getBestVector(getPlayer().getPositionEyes(1F),
                    targets.get(0).getEntityBoundingBox())) <= range.getValue() || getPlayer().hurtTime == 10 || (onlyWhenNeeded.getValue() && !perfectHit.getValue() && timeShift && balance >= maxTimeDelay.getValue() / 2))) {
                balance = 0;
            }
        }

        lastTime = Minecraft.getSystemTime();
        e.time = (long) (Minecraft.getSystemTime() - balance);
    }

    @Override
    public void onEnable() {
        balance = 0;
        lastTime = Minecraft.getSystemTime();
        animation.setValue(3);
        targets.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets.clear();
        super.onDisable();
    }
}
