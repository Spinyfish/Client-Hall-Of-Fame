/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lodomir.dev.November;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.AntiBot;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.MathUtils;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.PlayerUtils;
import lodomir.dev.utils.player.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class KillAura
extends Module {
    public static Entity target;
    public static float yaw;
    public static float pitch;
    public static float lastYaw;
    public static float lastPitch;
    private boolean blocking;
    private int targetIndex;
    public TimeUtils timeUtils = new TimeUtils();
    public Random random = new Random();
    public ModeSetting mode = new ModeSetting("Mode", "Single", "Single", "Switch", "Multi");
    public ModeSetting rotations = new ModeSetting("Rotations", "Default", "Default", "Custom", "Smooth", "Derp", "None");
    public NumberSetting maxrot = new NumberSetting("Max Rotation", 0.0, 180.0, 180.0, 0.1);
    public NumberSetting minrot = new NumberSetting("Min Rotation", 0.0, 180.0, 180.0, 0.1);
    public static ModeSetting blockMode;
    public ModeSetting sort = new ModeSetting("Sort", "Distance", "Distance", "Health", "Hurttime");
    public static NumberSetting range;
    public NumberSetting aps = new NumberSetting("Aps", 1.0, 20.0, 10.0, 1.0);
    public BooleanSetting ondead = new BooleanSetting("Disable on death", true);
    public BooleanSetting thruwalls = new BooleanSetting("Trought Walls", false);
    public BooleanSetting keepsprint = new BooleanSetting("Keep Sprint", true);
    public BooleanSetting invisibles = new BooleanSetting("Invisibles", false);
    public BooleanSetting players = new BooleanSetting("Players", true);
    public BooleanSetting nonPlayers = new BooleanSetting("Non Players", false);
    public BooleanSetting dead = new BooleanSetting("Dead", false);
    public BooleanSetting teams = new BooleanSetting("Teams", false);

    public KillAura() {
        super("KillAura", 19, Category.COMBAT);
        this.addSetting(this.mode);
        this.addSetting(this.aps);
        this.addSetting(range);
        this.addSetting(this.rotations);
        this.addSetting(this.minrot);
        this.addSetting(this.maxrot);
        this.addSetting(this.sort);
        this.addSetting(this.ondead);
        this.addSetting(blockMode);
        this.addSetting(this.keepsprint);
        this.addSetting(this.players);
        this.addSetting(this.nonPlayers);
        this.addSetting(this.teams);
        this.addSetting(this.invisibles);
        this.addSetting(this.dead);
        this.addSetting(this.thruwalls);
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        if (!this.rotations.isMode("Custom")) {
            this.minrot.setVisible(false);
            this.maxrot.setVisible(false);
        } else {
            this.minrot.setVisible(true);
            this.maxrot.setVisible(true);
        }
    }

    @Subscribe
    public void onUpdate(EventPreMotion event) {
        this.setSuffix(this.mode.getMode());
        for (Entity e : KillAura.mc.theWorld.loadedEntityList) {
            if (!this.allowedToAttack(e)) continue;
            target = e;
            this.updateTarget();
        }
        if (!this.allowedToAttack(target)) {
            target = null;
            this.unblock();
        }
        if (target != null) {
            lastYaw = KillAura.mc.thePlayer.rotationYaw;
            lastPitch = KillAura.mc.thePlayer.rotationPitch;
            if (!this.thruwalls.isEnabled() && !KillAura.mc.thePlayer.canEntityBeSeen(target)) {
                return;
            }
            float[] rotation = this.executeRotation(target);
            event.setYaw(rotation[0]);
            event.setPitch(rotation[1]);
            KillAura.mc.thePlayer.rotationYawHead = rotation[0];
            KillAura.mc.thePlayer.renderYawOffset = rotation[0];
            KillAura.mc.thePlayer.rotationPitchHead = rotation[1];
            if (this.timeUtils.hasReached(1000 / this.aps.getValueInt())) {
                this.Attack(target);
                this.timeUtils.reset();
            }
        }
    }

    @Override
    public void onEnable() {
        this.blocking = KillAura.mc.gameSettings.keyBindUseItem.isKeyDown();
        super.onEnable();
    }

    public float[] executeRotation(Entity e) {
        int fps = (int)((float)mc.getDebugFPS() / 20.0f);
        float[] rots = RotationUtils.getRotations((EntityLivingBase)target);
        if (this.rotations.isMode("Default")) {
            Vec3 targetPos = new Vec3(e.posX, e.posY, e.posZ);
            Vec3 playerPos = new Vec3(KillAura.mc.thePlayer.posX, KillAura.mc.thePlayer.posY + (double)KillAura.mc.thePlayer.getEyeHeight() - (double)1.6f, KillAura.mc.thePlayer.posZ);
            double d0 = targetPos.xCoord - playerPos.xCoord;
            double d1 = targetPos.yCoord - playerPos.yCoord;
            double d2 = targetPos.zCoord - playerPos.zCoord;
            double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
            float f = (float)(MathHelper.func_181159_b(d2, d0) * 180.0 / Math.PI) - 90.0f;
            float f1 = (float)(-(MathHelper.func_181159_b(d1, d3) * 180.0 / Math.PI));
            return new float[]{f, f1};
        }
        if (this.rotations.isMode("None")) {
            yaw = KillAura.mc.thePlayer.rotationYaw;
            pitch = KillAura.mc.thePlayer.rotationPitch;
            return new float[]{yaw, pitch};
        }
        if (this.rotations.isMode("Derp")) {
            float yaw = this.timeUtils.getCurrentMS();
            float pitch = (float)Math.random();
            return new float[]{yaw, pitch};
        }
        if (this.rotations.isMode("Smooth")) {
            yaw = rots[0];
            pitch = rots[1];
            yaw = (float)((double)yaw + (Math.random() * 2.0 - 1.0));
            pitch = (float)((double)pitch + (Math.random() * 2.0 - 1.0));
            pitch = Math.min(pitch, 90.0f);
            pitch = Math.max(pitch, -90.0f);
            return new float[]{yaw, pitch};
        }
        if (this.rotations.isMode("Custom") && this.maxrot.getValue() != 180.0 && this.minrot.getValue() != 180.0) {
            float distance = (float)MathUtils.getRandom(this.minrot.getValue(), this.maxrot.getValue());
            yaw = rots[0];
            pitch = rots[1];
            yaw += distance;
            pitch += distance;
            yaw = (float)Math.min((double)yaw, this.minrot.getValue());
            pitch = (float)Math.max((double)pitch, this.maxrot.getValue());
            return new float[]{yaw, pitch};
        }
        return null;
    }

    @Override
    public void onDisable() {
        if (PlayerUtils.isHoldingSword()) {
            this.unblock();
        }
        super.onDisable();
    }

    private void Attack(Entity e) {
        KillAura.mc.thePlayer.swingItem();
        switch (blockMode.getMode()) {
            case "None": {
                break;
            }
            case "Legit": {
                if (PlayerUtils.isHoldingSword()) {
                    if (KillAura.mc.thePlayer.ticksExisted % 15 == 0) {
                        this.block();
                    } else {
                        this.unblock();
                    }
                }
                this.timeUtils.reset();
                break;
            }
            case "Vanilla": {
                if (PlayerUtils.isHoldingSword()) {
                    this.block();
                } else {
                    this.unblock();
                    break;
                }
            }
            case "Hypixel": {
                if (!(target instanceof EntityPlayer)) break;
                EntityPlayer player = (EntityPlayer)target;
                if (PlayerUtils.isHoldingSword()) {
                    if (player.hurtTime >= 5 || KillAura.mc.thePlayer.ticksExisted % 3 != 1) break;
                    this.sendPacketSilent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, KillAura.mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
                    break;
                }
                this.sendPacketSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                break;
            }
            case "Interact": {
                if (!PlayerUtils.isHoldingSword()) break;
                KillAura.mc.playerController.interactWithEntitySendPacket(KillAura.mc.thePlayer, target);
                this.sendPacket(new C08PacketPlayerBlockPlacement(KillAura.mc.thePlayer.getHeldItem()));
                break;
            }
            case "Fake": {
                if (!PlayerUtils.isHoldingSword()) break;
                this.unblock();
                break;
            }
            case "NCP": {
                if (!PlayerUtils.isHoldingSword()) break;
                this.sendPacket(new C08PacketPlayerBlockPlacement(KillAura.mc.thePlayer.getHeldItem()));
            }
        }
        switch (this.mode.getMode()) {
            case "Single": {
                if (this.keepsprint.isEnabled()) {
                    this.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                } else {
                    KillAura.mc.playerController.attackEntity(KillAura.mc.thePlayer, target);
                }
                if (!(KillAura.mc.thePlayer.fallDistance > 0.0f)) break;
                KillAura.mc.thePlayer.onCriticalHit(target);
                break;
            }
            case "Switch": {
                List<EntityLivingBase> entities = this.getTargets();
                if (entities.size() >= this.targetIndex) {
                    this.targetIndex = 0;
                }
                if (entities.isEmpty()) {
                    this.targetIndex = 0;
                    return;
                }
                EntityLivingBase entity = entities.get(this.targetIndex);
                if (this.keepsprint.isEnabled()) {
                    this.sendPacket(new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
                } else {
                    KillAura.mc.playerController.attackEntity(KillAura.mc.thePlayer, entity);
                }
                if (KillAura.mc.thePlayer.fallDistance > 0.0f) {
                    KillAura.mc.thePlayer.onCriticalHit(target);
                }
                ++this.targetIndex;
                break;
            }
            case "Multi": {
                for (EntityLivingBase entity : this.getTargets()) {
                    if (this.keepsprint.isEnabled()) {
                        this.sendPacket(new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
                    } else {
                        KillAura.mc.playerController.attackEntity(KillAura.mc.thePlayer, entity);
                    }
                    if (!(KillAura.mc.thePlayer.fallDistance > 0.0f)) continue;
                    KillAura.mc.thePlayer.onCriticalHit(entity);
                }
                break;
            }
        }
    }

    public List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> entities = KillAura.mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).map(entity -> (EntityLivingBase)entity).filter(entity -> {
            if (entity instanceof EntityPlayer && !this.players.isEnabled()) {
                return false;
            }
            if (!(entity instanceof EntityPlayer) && !this.nonPlayers.isEnabled()) {
                return false;
            }
            if (entity.isInvisible() && !this.invisibles.isEnabled()) {
                return false;
            }
            if (PlayerUtils.isOnSameTeam(entity) && !this.teams.isEnabled()) {
                return false;
            }
            if (entity.isDead && !this.dead.isEnabled()) {
                return false;
            }
            if (entity.deathTime != 0 && !this.dead.isEnabled()) {
                return false;
            }
            if (entity.ticksExisted < 2) {
                return false;
            }
            if (AntiBot.bot.contains(entity) && November.INSTANCE.moduleManager.getModule("AntiBot").isEnabled()) {
                return false;
            }
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entity;
            }
            return KillAura.mc.thePlayer != entity;
        }).filter(entity -> {
            double girth = 0.5657;
            return (double)KillAura.mc.thePlayer.getDistanceToEntity((Entity)entity) - 0.5657 < range.getValue();
        }).sorted(Comparator.comparingDouble(entity -> {
            switch (this.sort.getMode()) {
                case "Distance": {
                    return KillAura.mc.thePlayer.getDistanceSqToEntity((Entity)entity);
                }
                case "Health": {
                    return entity.getHealth();
                }
                case "Hurttime": {
                    return entity.hurtTime;
                }
            }
            return -1.0;
        })).sorted(Comparator.comparing(entity -> entity instanceof EntityPlayer)).collect(Collectors.toList());
        return entities;
    }

    private void updateTarget() {
        List<EntityLivingBase> entities = this.getTargets();
        target = entities.size() > 0 ? (Entity)entities.get(0) : null;
    }

    private boolean allowedToAttack(Entity entity) {
        this.getTargets();
        return entity instanceof EntityLivingBase && entity != KillAura.mc.thePlayer && KillAura.mc.thePlayer.getDistanceToEntity(entity) <= (float)range.getValueInt();
    }

    private void block() {
        this.sendUseItem(KillAura.mc.thePlayer, KillAura.mc.theWorld, KillAura.mc.thePlayer.getCurrentEquippedItem());
        KillAura.mc.gameSettings.keyBindUseItem.pressed = true;
        KillAura.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, KillAura.mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
        this.blocking = true;
    }

    private void unblock() {
        if (this.blocking) {
            KillAura.mc.gameSettings.keyBindUseItem.pressed = false;
            this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.blocking = false;
        }
    }

    public void sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
        if (KillAura.mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR) {
            KillAura.mc.playerController.syncCurrentPlayItem();
            int i = itemStackIn.stackSize;
            ItemStack itemstack = itemStackIn.useItemRightClick(worldIn, playerIn);
            if (itemstack != itemStackIn || itemstack.stackSize != i) {
                playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = itemstack;
                if (itemstack.stackSize == 0) {
                    playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
                }
            }
        }
    }

    static {
        blockMode = new ModeSetting("Block Mode", "Vanilla", "None", "Vanilla", "Hypixel", "Interact", "Legit", "Fake", "NCP");
        range = new NumberSetting("Range", 1.0, 6.0, 3.0, 0.05);
    }
}

