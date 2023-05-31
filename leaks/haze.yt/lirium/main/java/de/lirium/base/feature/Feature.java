package de.lirium.base.feature;

import de.lirium.Client;
import de.lirium.impl.events.PlayerMoveEvent;
import de.lirium.impl.module.impl.movement.TargetStrafeFeature;
import de.lirium.util.entity.FakeEntityUtil;
import de.lirium.util.interfaces.IMinecraft;
import de.lirium.util.rotation.RotationUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Timer;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public interface Feature extends IMinecraft {
    String getName();

    default void sendPacket(Packet<?> packet) {
        if (mc.getConnection() != null)
            mc.getConnection().sendPacket(packet);
    }

    default EntityOtherPlayerMP copy(EntityPlayer player) {
        final EntityOtherPlayerMP entity = new EntityOtherPlayerMP(getWorld(), player.getGameProfile());
        entity.copyLocationAndAnglesFrom(player);
        entity.setDataManager(player.getDataManager());
        entity.inventory = new InventoryPlayer(entity);
        entity.copyDataFromOld(getPlayer());
        entity.inventory.currentItem = player.inventory.currentItem;
        entity.rotationYaw = player.rotationYaw;
        entity.prevRotationYaw = player.prevRotationYaw;
        if(player != getPlayer()) {
            entity.rotationYawHead = player.rotationYawHead;
            entity.prevRotationYawHead = player.prevRotationYawHead;
        } else {
            entity.rotationYawHead = RotationUtil.yaw;
            entity.prevRotationYawHead = RotationUtil.prevYaw;
            entity.rotationPitch = RotationUtil.pitch;
            entity.prevRotationPitch = RotationUtil.prevPitch;
        }
        entity.chasingPosX = player.chasingPosX;
        entity.chasingPosY = player.chasingPosY;
        entity.chasingPosZ = player.chasingPosZ;
        entity.prevChasingPosX = player.prevChasingPosX;
        entity.prevChasingPosY = player.prevChasingPosY;
        entity.prevChasingPosZ = player.prevChasingPosZ;
        entity.prevPosX = player.prevPosX;
        entity.prevPosY = player.prevPosY;
        entity.prevPosZ = player.prevPosZ;
        entity.renderYawOffset = player.renderYawOffset;
        entity.prevRenderYawOffset = player.prevRenderYawOffset;
        entity.setSneaking(player.isSneaking());
        entity.hurtTime = player.hurtTime;
        entity.distanceWalkedModified = player.distanceWalkedModified;
        entity.prevDistanceWalkedModified = player.prevDistanceWalkedModified;
        entity.limbSwing = player.limbSwing;
        entity.limbSwingAmount = player.limbSwingAmount;
        entity.prevLimbSwingAmount = player.prevLimbSwingAmount;
        entity.swingProgress = player.swingProgress;
        entity.swingProgressInt = player.swingProgressInt;
        entity.prevSwingProgress = player.prevSwingProgress;
        entity.isSwingInProgress = player.isSwingInProgress;
        return entity;
    }

    default void spawn(EntityOtherPlayerMP entity) {
        FakeEntityUtil.clientSideEntities.add(entity);
    }

    default void sendPositionOffset(final double y, final boolean ground) {
        sendPositionOffset(0, y, 0, ground);
    }

    default void sendPositionOffset(final double x, final double y, final double z, final boolean ground) {
        sendPosition(getPlayer().posX + x, getPlayer().posY + y, getPlayer().posZ + z, ground);
    }

    default void sendPosition(final double x, final double y, final double z, final boolean ground) {
        sendPacketUnlogged(new CPacketPlayer.Position(x, y, z, ground));
    }

    default void sendPacketUnlogged(Packet<?> packet) {
        if (mc.getConnection() != null)
            mc.getConnection().getNetworkManager().sendPacketUnlogged(packet);
    }

    default double getRange(Entity entity) {
        return getPlayer().getPositionEyes(1.0f).distanceTo(RotationUtil.getBestVector(getPlayer().getPositionEyes(1F),
                entity.getEntityBoundingBox()));
    }

    default float getDirection() {
        final TargetStrafeFeature targetStrafe = Client.INSTANCE.getModuleManager().get(TargetStrafeFeature.class);
        if (targetStrafe != null && targetStrafe.isEnabled() && targetStrafe.isStrafing())
            return (float) Math.toRadians(targetStrafe.getStrafeYaw());
        float yaw = getYaw();
        float roundedStrafing = Math.max(-1, Math.min(1, Math.round(getPlayer().movementInput.moveStrafe * 100))),
                roundedForward = Math.max(-1, Math.min(1, Math.round(getPlayer().movementInput.moveForward * 100)));
        if (roundedStrafing != 0)
            yaw -= 90 * roundedStrafing * (roundedForward != 0 ? roundedForward * 0.5 : 1);
        if (isMovingBackward()) yaw += 180;
        return (float) Math.toRadians(yaw);
    }

    default float getMovementDirection(final float rotationYaw) {
        double direction = 0;
        if (this.getPlayer().moveForward < 0)
            direction += 180;
        if (this.getPlayer().moveStrafing < 0)
            direction += this.getPlayer().moveForward == 0F ? 90 : 45 * (this.getPlayer().moveForward < 0 ? -1 : 1);
        if (this.getPlayer().moveStrafing > 0)
            direction -= this.getPlayer().moveForward == 0F ? 90 : 45 * (this.getPlayer().moveForward < 0 ? -1 : 1);
        return (float) (rotationYaw + direction);
    }

    default void setSpeed(double speed) {
        getPlayer().motionX = -Math.sin(getDirection()) * speed;
        getPlayer().motionZ = Math.cos(getDirection()) * speed;
    }

    default void setSpeed(PlayerMoveEvent event, double speed) {
        event.setX(-Math.sin(getDirection()) * speed);
        event.setZ(Math.cos(getDirection()) * speed);
    }

    default boolean isMovingForward() {
        return getPlayer().movementInput.moveForward > 0;
    }

    default boolean isMovingBackward() {
        return getPlayer().movementInput.moveForward < 0;
    }

    default boolean isMovingLeft() {
        return getPlayer().movementInput.moveStrafe > 0;
    }

    default boolean isMovingRight() {
        return getPlayer().movementInput.moveStrafe < 0;
    }

    default boolean isMoving() {
        return isMovingForward() || isMovingBackward() || isMovingLeft() || isMovingRight();
    }

    default EntityPlayerSP getPlayer() {
        return mc.player;
    }

    default Timer getTimer() {
        return mc.timer;
    }

    default WorldClient getWorld() {
        return mc.world;
    }

    default GameSettings getGameSettings() {
        return mc.gameSettings;
    }


    default double getX() {
        return getPlayer().posX;
    }

    default double getY() {
        return getPlayer().posY;
    }

    default double getZ() {
        return getPlayer().posZ;
    }

    default float getYaw() {
        return getPlayer().rotationYaw;
    }

    default float getPitch() {
        return getPlayer().rotationPitch;
    }

    default boolean isOnGround() {
        return getPlayer().onGround;
    }

    default double getSpeed() {
        return Math.sqrt(getPlayer().motionX * getPlayer().motionX + getPlayer().motionZ * getPlayer().motionZ);
    }

    default void setTimer(final float timer) {
        getTimer().timerSpeed = timer;
    }

    default void sendMessage(Object object) {
        getPlayer().addChatMessage(new TextComponentString(Client.PREFIX + object));
    }

    default void setPosition(double x, double y, double z) {
        getPlayer().setPosition(x, y, z);
    }

    default void addPosition(double x, double y, double z) {
        setPosition(getPlayer().posX + x, getPlayer().posY + y, getPlayer().posZ + z);
    }

    default boolean isInteger(String input) {
        try {
            Integer.valueOf(input);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    default boolean isKeyDown(int key) {
        if (key < 0) {
            return Mouse.isButtonDown(key + 100);
        } else {
            return Keyboard.isKeyDown(key);
        }
    }

    default String getName(EntityPlayer player) {
        return player.getGameProfile().getName();
    }

    default boolean isValidEntityName(Entity entity) {
        if (!(entity instanceof EntityPlayer))
            return true;
        final String name = getName((EntityPlayer) entity);
        return name.length() <= 16 && name.length() >= 3 && name.matches("[a-zA-Z0-9_]*");
    }

    default boolean hasPing(Entity entity, int ping) {
        if (mc.isSingleplayer())
            return true;
        for (NetworkPlayerInfo playerInfo : getPlayer().connection.getPlayerInfoMap()) {
            if (playerInfo.getGameProfile().getId().equals(entity.getUniqueID())) {
                if (playerInfo.getResponseTime() == ping)
                    return true;
            }
        }
        return false;
    }

    default float getBaseSpeed() {
        final float baseSpeed = 0.2873f;
        final PotionEffect effect;
        if ((effect = getPlayer().getActivePotionEffect(MobEffects.SPEED)) != null)
            return baseSpeed * (1.0f + 0.2f * effect.getAmplifier());
        return baseSpeed;
    }

    default double getWatchdogUnpatchValues() {
        double value = 1.0 / System.currentTimeMillis();
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(4, 20); i++)
            value *= 1.0 / System.currentTimeMillis();
        return value;
    }

    default Color getTeamColor(EntityLivingBase livingBase) {
        final ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) livingBase.getTeam();
        int i = 16777215;

        if (scoreplayerteam != null) {
            final String s = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix());
            if (s.length() >= 2) {
                if (mc.getRenderManager().getFontRenderer().getColorCode(s.charAt(1)) != 0)
                    i = mc.getRenderManager().getFontRenderer().getColorCode(s.charAt(1));
            }
        }
        final float f1 = (float) (i >> 16 & 255) / 255.0F;
        final float f2 = (float) (i >> 8 & 255) / 255.0F;
        final float f = (float) (i & 255) / 255.0F;

        return new Color(f1, f2, f);
    }

    default boolean isTeam(EntityLivingBase first, EntityLivingBase second) {
        if(first.getTeam() instanceof ScorePlayerTeam && second.getTeam() instanceof ScorePlayerTeam) {
            return Objects.requireNonNull(first.getTeam()).isSameTeam(second.getTeam()) || getTeamColor(first).equals(getTeamColor(second));
        }
        return false;
    }

}