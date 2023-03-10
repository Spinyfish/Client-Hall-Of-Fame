/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lodomir.dev.event.impl.EventMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.BooleanSetting;
import lodomir.dev.settings.ModeSetting;
import lodomir.dev.settings.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.PlayerUtils;
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

public class Test
extends Module {
    public static NumberSetting range = new NumberSetting("Range", 1.0, 500.0, 3.0, 1.0);
    public static ModeSetting blockingMode = new ModeSetting("Block Mode", "Vanilla", "None", "Vanilla", "Interact", "Legit", "Fake", "NCP");
    public NumberSetting cps = new NumberSetting("Cps", 1.0, 20.0, 10.0, 1.0);
    public static Entity target;
    public static float yaw;
    public static float pitch;
    private boolean blocking;
    public TimeUtils timeUtils = new TimeUtils();
    public Random random = new Random();
    public BooleanSetting invisibles = new BooleanSetting("Invisibles", false);
    public BooleanSetting players = new BooleanSetting("Players", true);
    public BooleanSetting nonPlayers = new BooleanSetting("Non Players", false);
    public BooleanSetting dead = new BooleanSetting("Dead", false);
    public BooleanSetting animals = new BooleanSetting("Animals", false);
    public BooleanSetting monsters = new BooleanSetting("Monsters", false);
    public BooleanSetting villager = new BooleanSetting("Villagers", false);
    public BooleanSetting teams = new BooleanSetting("Teams", false);

    public Test() {
        super("InfAura", 0, Category.OTHER);
        this.addSetting(range);
        this.addSetting(this.cps);
        this.addSetting(blockingMode);
        this.addSetting(this.invisibles);
        this.addSetting(this.players);
        this.addSetting(this.nonPlayers);
        this.addSetting(this.teams);
        this.addSetting(this.dead);
        this.addSetting(this.animals);
        this.addSetting(this.monsters);
        this.addSetting(this.villager);
    }

    @Subscribe
    public void onUpdate(EventMotion event) {
        for (Entity e : Test.mc.theWorld.loadedEntityList) {
            if (!this.allowedToAttack(e)) continue;
            target = e;
            this.updateTarget();
        }
        if (!this.allowedToAttack(target)) {
            target = null;
            this.unblock();
        }
        if (target != null) {
            float[] rotation = this.executeRotation(target);
            event.setYaw(rotation[0]);
            event.setPitch(rotation[1]);
            Test.mc.thePlayer.rotationYawHead = rotation[0];
            Test.mc.thePlayer.renderYawOffset = rotation[0];
            Test.mc.thePlayer.rotationPitchHead = rotation[1];
            if (this.timeUtils.hasReached(1000 / this.cps.getValueInt())) {
                this.Attack(target);
                this.timeUtils.reset();
            }
        }
    }

    @Override
    public void onEnable() {
        this.blocking = Test.mc.gameSettings.keyBindUseItem.isKeyDown();
        super.onEnable();
    }

    public float[] executeRotation(Entity e) {
        Vec3 targetPos = new Vec3(e.posX, e.posY, e.posZ);
        Vec3 playerPos = new Vec3(Test.mc.thePlayer.posX, Test.mc.thePlayer.posY + (double)Test.mc.thePlayer.getEyeHeight() - (double)1.6f, Test.mc.thePlayer.posZ);
        double d0 = targetPos.xCoord - playerPos.xCoord;
        double d1 = targetPos.yCoord - playerPos.yCoord;
        double d2 = targetPos.zCoord - playerPos.zCoord;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float)(MathHelper.func_181159_b(d2, d0) * 180.0 / Math.PI) - 90.0f;
        float f1 = (float)(-(MathHelper.func_181159_b(d1, d3) * 180.0 / Math.PI));
        return new float[]{f, f1};
    }

    @Override
    public void onDisable() {
        if (PlayerUtils.isHoldingSword()) {
            this.unblock();
        }
        super.onDisable();
    }

    private void Attack(Entity e) {
        Test.mc.thePlayer.swingItem();
        switch (blockingMode.getMode()) {
            case "None": {
                break;
            }
            case "Legit": {
                if (PlayerUtils.isHoldingSword()) {
                    Test.mc.thePlayer.getItemInUse();
                    break;
                }
            }
            case "Vanilla": {
                if (PlayerUtils.isHoldingSword()) {
                    this.block();
                } else {
                    this.unblock();
                    break;
                }
            }
            case "Interact": {
                if (!PlayerUtils.isHoldingSword()) break;
                Test.mc.playerController.interactWithEntitySendPacket(Test.mc.thePlayer, target);
                this.sendPacket(new C08PacketPlayerBlockPlacement(Test.mc.thePlayer.getHeldItem()));
                break;
            }
            case "Fake": {
                if (!PlayerUtils.isHoldingSword()) break;
                this.unblock();
                break;
            }
            case "NCP": {
                if (!PlayerUtils.isHoldingSword()) break;
                this.sendPacket(new C08PacketPlayerBlockPlacement(Test.mc.thePlayer.getHeldItem()));
            }
        }
        this.sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
    }

    public List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> entities = Test.mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityLivingBase).map(entity -> (EntityLivingBase)entity).filter(entity -> {
            if (entity instanceof EntityPlayer && !this.players.isEnabled()) {
                return false;
            }
            if (!(entity instanceof EntityPlayer) && !this.nonPlayers.isEnabled()) {
                return false;
            }
            if (entity.isInvisible() && !this.invisibles.isEnabled()) {
                return false;
            }
            if (PlayerUtils.isOnSameTeam(entity) && this.teams.isEnabled()) {
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
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityPlayer = (EntityPlayer)entity;
            }
            return Test.mc.thePlayer != entity;
        }).filter(entity -> {
            double girth = 0.5657;
            return (double)Test.mc.thePlayer.getDistanceToEntity((Entity)entity) - 0.5657 < range.getValue();
        }).sorted(Comparator.comparingDouble(entity -> -1.0)).sorted(Comparator.comparing(entity -> entity instanceof EntityPlayer)).collect(Collectors.toList());
        return entities;
    }

    private void updateTarget() {
        List<EntityLivingBase> entities = this.getTargets();
        target = entities.size() > 0 ? (Entity)entities.get(0) : null;
    }

    private boolean allowedToAttack(Entity entity) {
        this.getTargets();
        return entity instanceof EntityLivingBase && entity != Test.mc.thePlayer && Test.mc.thePlayer.getDistanceToEntity(entity) <= (float)range.getValueInt();
    }

    private void block() {
        this.sendUseItem(Test.mc.thePlayer, Test.mc.theWorld, Test.mc.thePlayer.getCurrentEquippedItem());
        Test.mc.gameSettings.keyBindUseItem.pressed = true;
        Test.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, Test.mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
        this.blocking = true;
    }

    private void unblock() {
        if (this.blocking) {
            Test.mc.gameSettings.keyBindUseItem.pressed = false;
            this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.blocking = false;
        }
    }

    public void sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
        if (Test.mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR) {
            Test.mc.playerController.syncCurrentPlayItem();
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
}

