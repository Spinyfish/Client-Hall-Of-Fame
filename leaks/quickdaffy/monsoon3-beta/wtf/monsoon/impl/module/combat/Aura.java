/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPostMotion;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventRender3D;
import wtf.monsoon.impl.event.EventStrafing;
import wtf.monsoon.impl.module.player.Scaffold;

public class Aura
extends Module {
    private final Setting<String> attackSettings = new Setting<String>("Attack Settings", "Attack Settings").describedBy("Configure your attacks.");
    private Setting<Double> aps = new Setting<Double>("APS", 10.0).minimum(1.0).maximum(20.0).incrementation(0.5).describedBy("The amount of times to attack per second").childOf(this.attackSettings);
    private Setting<Double> range = new Setting<Double>("Range", 4.0).minimum(2.0).maximum(6.0).incrementation(0.1).describedBy("The range to attack").childOf(this.attackSettings);
    private final Setting<BlockMode> blockMode = new Setting<BlockMode>("Block Mode", BlockMode.FAKE).describedBy("The autoblock mode.").childOf(this.attackSettings);
    private final Setting<AttackStage> attackStage = new Setting<AttackStage>("Attack Stage", AttackStage.PRE).describedBy("The attack stage.").childOf(this.attackSettings);
    private final Setting<String> bypassSettings = new Setting<String>("Bypass Settings", "Bypass Settings").describedBy("Settings that involve bypasses.");
    private final Setting<Boolean> hitDelay = new Setting<Boolean>("1.9+ Hit Delay", false).describedBy("Whether or not to use the 1.9+ hit delay when attacking.").childOf(this.bypassSettings);
    private final Setting<Boolean> moveFix = new Setting<Boolean>("Move Fix", false).describedBy("Fix the move speed when attacking").childOf(this.bypassSettings);
    private final Setting<Boolean> gcdFix = new Setting<Boolean>("GCD Fix", false).describedBy("Whether to enable a GCD fix.").childOf(this.bypassSettings);
    private final Setting<Boolean> noRots = new Setting<Boolean>("No Rotations", false).describedBy("Don't set rotations.").childOf(this.bypassSettings);
    private final Setting<Boolean> whileInventoryOpen = new Setting<Boolean>("While Inventory Open", true).describedBy("Don't attack when in a GUI.").childOf(this.bypassSettings);
    private final Setting<String> targets = new Setting<String>("Targets", "Targets").describedBy("Set valid targets for Aura.");
    private final Setting<Boolean> targetPlayers = new Setting<Boolean>("Players", true).describedBy("Target players.").childOf(this.targets);
    private final Setting<Boolean> targetAnimals = new Setting<Boolean>("Animals", false).describedBy("Target animals.").childOf(this.targets);
    private final Setting<Boolean> targetMonsters = new Setting<Boolean>("Monsters", false).describedBy("Target monsters.").childOf(this.targets);
    private final Setting<Boolean> targetInvisibles = new Setting<Boolean>("Invisibles", false).describedBy("Target invisibles.").childOf(this.targets);
    private final Setting<Boolean> targetThruWalls = new Setting<Boolean>("Through Walls", true).describedBy("Target entities through walls.").childOf(this.targets);
    private final Setting<String> misc = new Setting<String>("Miscellaneous", "Miscellaneous").describedBy("Miscellaneous settings for Aura.");
    private final Setting<Boolean> prediction = new Setting<Boolean>("Prediction", true).describedBy("Predict where the entity will move, and rotate accordingly.").childOf(this.misc);
    private final Setting<Boolean> smoothRotations = new Setting<Boolean>("Smooth Rotations", true).describedBy("Rotate smoothly.").childOf(this.misc);
    private final Setting<Boolean> hvhMode = new Setting<Boolean>("HVH Mode", false).describedBy("Enable HvH mode.").childOf(this.misc);
    private boolean blocking = false;
    private EntityLivingBase target;
    private final Timer attackTimer = new Timer();
    private float finalYaw;
    private float finalPitch;
    private int ticksSinceLastSwingDelayPacket = 0;
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (Minecraft.getMinecraft().thePlayer.ticksExisted % 150 == 0 || Wrapper.getMonsoon().getTargetManager().getLoadedEntitySize() != Minecraft.getMinecraft().theWorld.loadedEntityList.size()) {
            Wrapper.getMonsoon().getTargetManager().updateTargets(this.targetPlayers.getValue(), this.targetAnimals.getValue(), this.targetMonsters.getValue(), this.targetInvisibles.getValue());
            Wrapper.getMonsoon().getTargetManager().setLoadedEntitySize(Minecraft.getMinecraft().theWorld.loadedEntityList.size());
        }
        this.target = this.getSingleTarget();
        if (this.target == null) {
            this.releaseBlock();
            return;
        }
        if (!this.moveFix.getValue().booleanValue()) {
            float[] rots = this.getRotations(this.target);
            this.finalYaw = this.processRotation(rots[0]);
            this.finalPitch = this.processRotation(rots[1]);
        }
        if (this.prediction.getValue().booleanValue()) {
            this.finalYaw = (float)((double)this.finalYaw + (Math.abs(this.target.posX - this.target.lastTickPosX) - Math.abs(this.target.posZ - this.target.lastTickPosZ)) * 0.0 * 2.0);
            this.finalPitch = (float)((double)this.finalPitch + (Math.abs(this.target.posY - this.target.lastTickPosY) - Math.abs(this.target.getEntityBoundingBox().minY - this.target.lastTickPosY)) * 0.0 * 2.0);
        }
        if (this.smoothRotations.getValue().booleanValue()) {
            float sens = (float)(Math.pow(Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6f + 0.2f, 3.0) * 8.0 * (double)0.15f);
            this.finalYaw = Aura.interpolateRotation(this.mc.thePlayer.rotationYaw, this.finalYaw, 360.0f);
            this.finalPitch = Aura.interpolateRotation(this.mc.thePlayer.rotationPitch, this.finalPitch, 90.0f);
            this.finalYaw = (float)Math.round(this.finalYaw / sens) * sens;
            this.finalPitch = (float)Math.round(this.finalPitch / sens) * sens;
        }
        this.mc.thePlayer.rotationYawHead = this.mc.thePlayer.renderYawOffset = this.finalYaw;
        e.setYaw(this.mc.thePlayer.renderYawOffset);
        this.mc.thePlayer.rotationPitchHead = this.finalPitch;
        e.setPitch(this.mc.thePlayer.rotationPitchHead);
        if ((this.mc.currentScreen instanceof GuiInventory || this.mc.currentScreen instanceof GuiChest) && !this.whileInventoryOpen.getValue().booleanValue()) {
            this.releaseBlock();
            return;
        }
        if (Wrapper.getModule(Scaffold.class).isEnabled()) {
            return;
        }
        this.preAutoblock();
        if (this.attackStage.getValue().equals((Object)AttackStage.PRE) && this.hitTimerDone()) {
            this.attack(this.target);
        }
        ++this.ticksSinceLastSwingDelayPacket;
    };
    @EventLink
    public final Listener<EventPostMotion> eventPostMotionListener = e -> {
        if (this.attackStage.getValue().equals((Object)AttackStage.POST) && this.hitTimerDone()) {
            this.attack(this.target);
        }
        this.postAutoblock();
    };
    @EventLink
    public final Listener<EventStrafing> eventStrafingListener = e -> {
        if (this.moveFix.getValue().booleanValue()) {
            float[] rots = this.getRotations(this.target);
            this.finalYaw = this.processRotation(rots[0]);
            this.finalPitch = this.processRotation(rots[1]);
            e.setYaw(this.finalYaw);
            e.setPitch(this.finalPitch);
        }
    };
    @EventLink
    public final Listener<EventRender2D> eventRender2DListener = e -> {
        if (this.hvhMode.getValue().booleanValue() && this.hitTimerDone()) {
            this.attack(this.target);
        }
    };
    @EventLink
    public final Listener<EventRender3D> eventRender2D = e -> {
        if (this.target != null) {
            this.sigmaTargetThing(this.target);
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        S45PacketTitle packet;
        if (e.getPacket() instanceof S45PacketTitle && (packet = (S45PacketTitle)e.getPacket()).getType() == S45PacketTitle.Type.SUBTITLE && packet.getMessage().getFormattedText().contains("\u02d9")) {
            this.ticksSinceLastSwingDelayPacket = 0;
        }
    };

    public Aura() {
        super("Aura", "Automatically hit entities.", Category.COMBAT);
        this.setMetadata(() -> "R " + (int)this.range.getValue().doubleValue() + " APS " + (int)this.aps.getValue().doubleValue());
        this.aps = this.aps.visibleWhen(() -> this.hitDelay.getValue() == false);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.attackTimer.reset();
        this.finalYaw = this.mc.thePlayer.rotationYaw;
        this.finalPitch = this.mc.thePlayer.rotationPitch;
        this.mc.gameSettings.keyBindUseItem.pressed = false;
        this.blocking = true;
        this.ticksSinceLastSwingDelayPacket = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.releaseBlock();
    }

    private void attack(EntityLivingBase e) {
        if (e == null) {
            return;
        }
        if (ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
            this.mc.thePlayer.swingItem();
            PacketUtil.sendPacketNoEvent(new C02PacketUseEntity((Entity)e, C02PacketUseEntity.Action.ATTACK));
        } else {
            PacketUtil.sendPacketNoEvent(new C02PacketUseEntity((Entity)e, C02PacketUseEntity.Action.ATTACK));
            this.mc.thePlayer.swingItem();
        }
        this.ticksSinceLastSwingDelayPacket = 0;
        if (this.hitDelay.getValue().booleanValue() && this.hitTimerDone(false) && !this.hitDelayOver()) {
            this.mc.thePlayer.swingNoPacket();
        }
    }

    private void swingAndAttack(EntityLivingBase entityIn) {
        if (entityIn == null) {
            return;
        }
        if (ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
            this.mc.thePlayer.swingItem();
            PacketUtil.sendPacketNoEvent(new C02PacketUseEntity((Entity)entityIn, C02PacketUseEntity.Action.ATTACK));
        } else {
            PacketUtil.sendPacketNoEvent(new C02PacketUseEntity((Entity)entityIn, C02PacketUseEntity.Action.ATTACK));
            this.mc.thePlayer.swingItem();
        }
        this.ticksSinceLastSwingDelayPacket = 0;
    }

    private boolean hitTimerDone() {
        return this.hitTimerDone(this.hitDelay.getValue());
    }

    private boolean hitTimerDone(boolean hitDelay) {
        return this.attackTimer.hasTimeElapsed((long)(1000.0 / this.aps.getValue()), true) && !hitDelay || this.hitDelayOver() && hitDelay;
    }

    private boolean hitDelayOver() {
        return this.ticksSinceLastSwingDelayPacket >= 2;
    }

    private void preAutoblock() {
        if (this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && this.target != null) {
            switch (this.blockMode.getValue()) {
                case VANILLA: 
                case H_V_H: {
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
                    this.blocking = true;
                    break;
                }
                case N_C_P: 
                case WATCHDOG: {
                    if (this.blocking || !this.mc.thePlayer.onGround) break;
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, this.mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
                    this.blocking = true;
                    break;
                }
                case VERUS: {
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem(), new BlockPos(-1, -1, -1)));
                    this.blocking = true;
                    break;
                }
                case CONTROL: {
                    this.releaseBlock();
                }
            }
        }
    }

    private void postAutoblock() {
        if (this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && this.target != null) {
            switch (this.blockMode.getValue()) {
                case N_C_P: {
                    if (this.blocking) break;
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(this.mc.thePlayer.getHeldItem()));
                    this.blocking = true;
                    break;
                }
                case WATCHDOG: {
                    if (!this.blocking) break;
                    this.releaseBlock();
                    this.blocking = false;
                    break;
                }
                case CONTROL: {
                    this.mc.gameSettings.keyBindUseItem.pressed = true;
                    this.blocking = true;
                }
            }
        }
    }

    private void releaseBlock() {
        if (this.blocking) {
            switch (this.blockMode.getValue()) {
                case VANILLA: 
                case H_V_H: 
                case N_C_P: 
                case VERUS: {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    break;
                }
                case CONTROL: {
                    this.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown((int)1);
                    break;
                }
                case WATCHDOG: {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                }
            }
        }
        this.blocking = false;
    }

    public float[] getRotations(EntityLivingBase target) {
        if (target == null) {
            return new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch};
        }
        double xDist = target.posX - this.mc.thePlayer.posX;
        double zDist = target.posZ - this.mc.thePlayer.posZ;
        AxisAlignedBB entityBB = target.getEntityBoundingBox().expand(0.1f, 0.1f, 0.1f);
        double playerEyePos = this.mc.thePlayer.posY + (double)this.mc.thePlayer.getEyeHeight();
        double yDist = playerEyePos > entityBB.maxY ? entityBB.maxY - playerEyePos : (playerEyePos < entityBB.minY ? entityBB.minY - playerEyePos : 0.0);
        double fDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
        float yaw = Aura.interpolateRotation(this.finalYaw, (float)(StrictMath.atan2(zDist, xDist) * 57.29577951308232) - 90.0f, 45.0f);
        float pitch = Aura.interpolateRotation(this.finalPitch, (float)(-(StrictMath.atan2(yDist, fDist) * 57.29577951308232)), 45.0f);
        yaw = (float)((double)yaw + (Math.random() - 0.5));
        pitch = (float)((double)pitch + (Math.random() - 0.5));
        pitch = Math.min(pitch, 90.0f);
        pitch = Math.max(pitch, -90.0f);
        return new float[]{yaw, pitch};
    }

    private static float interpolateRotation(float prev, float now, float maxTurn) {
        float var4 = MathHelper.wrapAngleTo180_float(now - prev);
        if (var4 > maxTurn) {
            var4 = maxTurn;
        }
        if (var4 < -maxTurn) {
            var4 = -maxTurn;
        }
        return prev + var4;
    }

    private float processRotation(float value) {
        float toReturn = value;
        if (this.gcdFix.getValue().booleanValue()) {
            double m = 0.005 * (double)this.mc.gameSettings.mouseSensitivity;
            double gcd = m * m * m * 1.2;
            toReturn = (float)((double)toReturn - (double)toReturn % gcd);
            return toReturn;
        }
        return toReturn;
    }

    public EntityLivingBase getSingleTarget() {
        List targets = this.mc.theWorld.getLoadedEntityLivingBases().stream().filter(entity -> entity != Minecraft.getMinecraft().thePlayer).filter(entity -> entity.ticksExisted > 0).filter(entity -> (double)this.mc.thePlayer.getDistanceToEntity((Entity)entity) <= this.range.getValue()).filter(entity -> Minecraft.getMinecraft().theWorld.loadedEntityList.contains(entity)).filter(this::validTarget).sorted(Comparator.comparingDouble(entity -> Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity((Entity)entity))).collect(Collectors.toList());
        if (targets.isEmpty()) {
            return null;
        }
        return (EntityLivingBase)targets.get(0);
    }

    public List<EntityLivingBase> getMultipleTargets(int amount) {
        List<EntityLivingBase> targets = Wrapper.getMonsoon().getTargetManager().getTargets(this.range.getValue(), this.targetPlayers.getValue(), this.targetAnimals.getValue(), this.targetMonsters.getValue(), this.targetInvisibles.getValue(), this.targetThruWalls.getValue());
        ArrayList<EntityLivingBase> toReturn = new ArrayList<EntityLivingBase>();
        for (int i = 0; i < Math.max(amount, targets.size()); ++i) {
            EntityLivingBase e = targets.get(i);
            if (e == null) continue;
            toReturn.add(e);
        }
        return toReturn;
    }

    private boolean validTarget(EntityLivingBase entity) {
        if (entity.isInvisible()) {
            return this.validTargetLayer2(entity) && this.targetInvisibles.getValue() != false;
        }
        return this.validTargetLayer2(entity);
    }

    private boolean validTargetLayer2(EntityLivingBase entity) {
        if (!entity.canEntityBeSeen(Minecraft.getMinecraft().thePlayer)) {
            return this.validTargetLayer3(entity) && this.targetThruWalls.getValue() != false;
        }
        return this.validTargetLayer3(entity);
    }

    private boolean validTargetLayer3(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            return this.targetPlayers.getValue();
        }
        if (entity instanceof EntityAnimal) {
            return this.targetAnimals.getValue();
        }
        if (entity instanceof EntityMob) {
            return this.targetMonsters.getValue();
        }
        if (entity instanceof EntityVillager || entity instanceof EntityArmorStand) {
            return false;
        }
        return false;
    }

    private void sigmaTargetThing(EntityLivingBase target) {
        double vecZ;
        double vecX;
        float partialTicks = this.mc.getTimer().renderPartialTicks;
        EntityLivingBase player = target;
        if (target == null) {
            return;
        }
        Color color = ColorUtil.fadeBetween(20, 0, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
        if (this.mc.getRenderManager() == null || player == null) {
            return;
        }
        double x = player.prevPosX + (player.posX - player.prevPosX) * (double)partialTicks - this.mc.getRenderManager().renderPosX;
        double y = player.prevPosY + (player.posY - player.prevPosY) * (double)partialTicks + Math.sin((double)System.currentTimeMillis() / 200.0) + 1.0 - this.mc.getRenderManager().renderPosY;
        double z = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)partialTicks - this.mc.getRenderManager().renderPosZ;
        GL11.glPushMatrix();
        GL11.glDisable((int)3553);
        GL11.glEnable((int)2848);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glHint((int)3154, (int)4354);
        GL11.glHint((int)3155, (int)4354);
        GL11.glHint((int)3153, (int)4354);
        GL11.glDepthMask((boolean)false);
        GlStateManager.alphaFunc(516, 0.0f);
        GL11.glShadeModel((int)7425);
        GlStateManager.disableCull();
        GL11.glBegin((int)5);
        float i = 0.0f;
        while ((double)i <= 6.4795348480289485) {
            vecX = x + 0.67 * Math.cos(i);
            vecZ = z + 0.67 * Math.sin(i);
            ColorUtil.color(ColorUtil.withAlpha(color, 63));
            GL11.glVertex3d((double)vecX, (double)y, (double)vecZ);
            i = (float)((double)i + 0.19634954084936207);
        }
        i = 0.0f;
        while ((double)i <= 6.4795348480289485) {
            vecX = x + 0.67 * Math.cos(i);
            vecZ = z + 0.67 * Math.sin(i);
            ColorUtil.color(ColorUtil.withAlpha(color, 63));
            GL11.glVertex3d((double)vecX, (double)y, (double)vecZ);
            ColorUtil.color(ColorUtil.withAlpha(color, 0));
            GL11.glVertex3d((double)vecX, (double)(y - Math.cos((double)System.currentTimeMillis() / 200.0) / 2.0), (double)vecZ);
            i = (float)((double)i + 0.19634954084936207);
        }
        GL11.glEnd();
        GL11.glShadeModel((int)7424);
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableCull();
        GL11.glDisable((int)2848);
        GL11.glDisable((int)2848);
        GL11.glEnable((int)2832);
        GL11.glEnable((int)3553);
        GL11.glPopMatrix();
        ColorUtil.glColor(Color.WHITE.getRGB());
    }

    public Setting<Double> getRange() {
        return this.range;
    }

    public EntityLivingBase getTarget() {
        return this.target;
    }

    static enum AttackStage {
        PRE,
        POST;

    }

    static enum EventTime {
        EventPreMotion,
        EventPostMotion,
        EventRender2D;

    }

    static enum BlockMode {
        FAKE,
        H_V_H,
        VANILLA,
        CONTROL,
        VERUS,
        WATCHDOG,
        N_C_P;

    }
}

