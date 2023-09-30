package me.jinthium.scripting.api.bindings;

import me.jinthium.scripting.api.objects.ScriptPlayerCapabilites;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.Arrays;
import java.util.UUID;

// please dont touch this class, everything is organized a very specific way for documentation purposes
public class PlayerBinding implements MinecraftInstance {

    //
    //   FUNCTIONS
    //

    public void respawn() {
        mc.thePlayer.respawnPlayer();
    }

    public void swingItem() {
        mc.thePlayer.swingItem();
    }

    public void setPitch(double pitch) {
        mc.thePlayer.rotationPitch = (float) pitch;
    }

    public void setYaw(double yaw) {
        mc.thePlayer.rotationYaw = (float) yaw;
    }

    public void setMotionX(double x) {
        mc.thePlayer.motionZ = x;
    }

    public void setMotionY(double y) {
        mc.thePlayer.motionY = y;
    }

    public void setMotionZ(double z) {
        mc.thePlayer.motionZ = z;
    }

    public void setPosition(double x, double y, double z) {
        mc.thePlayer.setPosition(x, y, z);
    }

    public void jump() {
        mc.thePlayer.jump();
    }

    public void setSpeed(double speed) {
        MovementUtil.setSpeed(speed);
    }

    public void setSneaking(boolean state) {
        mc.thePlayer.setSneaking(state);
    }

    public void setSprinting(boolean state) {
        mc.thePlayer.setSprinting(state);
    }

    public void setFallDistance(double fallDistance) {
        mc.thePlayer.setFallDistance((float) fallDistance);
    }

    public void leftClick() {
        mc.clickMouse();
    }

    public void rightClick() {
        mc.rightClickMouse();
    }

    public void setHeldItemSlot(int slot) {
        mc.thePlayer.inventory.currentItem = slot;
    }

    public void sendMessage(String msg) {
        mc.thePlayer.sendChatMessage(msg);
    }


    //
    //   BOOLEANS
    //
    public boolean collidedHorizontally() {
        return mc.thePlayer.isCollidedHorizontally;
    }

    public boolean collidedVertically() {
        return mc.thePlayer.isCollidedVertically;
    }

    public boolean collided() {
        return mc.thePlayer.isCollided;
    }

    public boolean moving() {
        return mc.thePlayer.isMoving();
    }

    public boolean sneaking() {
        return mc.thePlayer.isSneaking();
    }

    public boolean sprinting() {
        return mc.thePlayer.isSprinting();
    }

    public boolean eating() {
        return mc.thePlayer.isEating();
    }

    public boolean onGround() {
        return mc.thePlayer.onGround;
    }

    public boolean airBorne() {
        return mc.thePlayer.isAirBorne;
    }

    public boolean onLadder() {
        return mc.thePlayer.isOnLadder();
    }

    public boolean inWater() {
        return mc.thePlayer.isInWater();
    }

    public boolean inLava() {
        return mc.thePlayer.isInLava();
    }

    public boolean inWeb() {
        return mc.thePlayer.isInWeb;
    }

    public boolean inPortal() {
        return mc.thePlayer.inPortal;
    }

    public boolean usingItem() {
        return mc.thePlayer.isUsingItem();
    }

    public boolean burning() {
        return mc.thePlayer.isBurning();
    }

    public boolean dead() {
        return mc.thePlayer.isDead;
    }

    public boolean isPotionActive(int potionId) {
        return mc.thePlayer.isPotionActive(potionId);
    }

    public String name() {
        return mc.thePlayer.getName();
    }

    public int hurtTime() {
        return mc.thePlayer.hurtTime;
    }

    public int heldItemSlot() {
        return mc.thePlayer.inventory.currentItem;
    }

    public double ticksExisted() {
        return mc.thePlayer.ticksExisted;
    }

    public double fallDistance() {
        return mc.thePlayer.fallDistance;
    }

    public double health() {
        return mc.thePlayer.getHealth();
    }

    public double maxHealth() {
        return mc.thePlayer.getMaxHealth();
    }

    public double armorValue() {
        return mc.thePlayer.getTotalArmorValue();
    }

    public double hunger() {
        return mc.thePlayer.getFoodStats().getFoodLevel();
    }

    public double absorption() {
        return mc.thePlayer.getAbsorptionAmount();
    }

    public double pitch() {
        return mc.thePlayer.rotationPitch;
    }

    public double yaw() {
        return mc.thePlayer.rotationYaw;
    }

    public double x() {
        return mc.thePlayer.posX;
    }

    public double y() {
        return mc.thePlayer.posY;
    }

    public double z() {
        return mc.thePlayer.posZ;
    }

    public double prevX() {
        return mc.thePlayer.prevPosX;
    }

    public double prevY() {
        return mc.thePlayer.prevPosY;
    }

    public double prevZ() {
        return mc.thePlayer.prevPosZ;
    }

    public double motionX() {
        return mc.thePlayer.motionX;
    }

    public double motionY() {
        return mc.thePlayer.motionY;
    }

    public double motionZ() {
        return mc.thePlayer.motionZ;
    }

    public double speed() {
        return MovementUtil.getSpeed();
    }

    public double timerSpeed() {
        return MovementUtil.getSpeed();
    }

    public double getBaseMoveSpeed() {
        return MovementUtil.getBaseMoveSpeed();
    }
    public ItemStack getInventorySlot(int slot) {
        return mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
    }

    public String getServerIP() {
        return mc.isSingleplayer() || mc.getCurrentServerData() == null ? "singleplayer" : mc.getCurrentServerData().serverIP;
    }

    public void sendPacket(int integer, Object... args) {
        Packet<?> packet = null;
        switch (integer) {
            case 36:
                packet = new C0APacketAnimation();
                break;
            case 37:
                C0BPacketEntityAction.Action c0bAction = Arrays.stream(C0BPacketEntityAction.Action.values())
                        .filter(action -> action.getId() == (int) args[0]).findFirst().orElse(null);
                packet = new C0BPacketEntityAction(mc.thePlayer, c0bAction);
                break;
            case 38:
                packet = new C0CPacketInput((float) args[0], (float) args[1], (boolean) args[2], (boolean) args[3]);
                break;
            case 39:
                packet = new C0DPacketCloseWindow((int) args[0]);
                break;
            case 5:
                packet = new C0FPacketConfirmTransaction((int) args[0], (short) args[1], (boolean) args[2]);
                break;
            case 6:
                packet = new C00PacketKeepAlive((int) args[0]);
                break;
            case 8:
                C02PacketUseEntity.Action c02Action = Arrays.stream(C02PacketUseEntity.Action.values())
                        .filter(action -> action.getId() == (int) args[1]).findFirst().orElse(null);
                packet = new C02PacketUseEntity((Entity) args[0], c02Action);
                break;
            case 9:
                packet = new C03PacketPlayer((boolean) args[0]);
                break;
            case 10:
                packet = new C03PacketPlayer.C04PacketPlayerPosition((double) args[0], (double) args[1], (double) args[2], (boolean) args[3]);
                break;
            case 11:
                Double arg0 = (Double) args[0];
                Double arg1 = (Double) args[1];
                packet = new C03PacketPlayer.C05PacketPlayerLook(arg0.floatValue(), arg1.floatValue(), (boolean) args[2]);
                break;
            case 12:
                Double arg3 = (Double) args[3];
                Double arg4 = (Double) args[4];
                packet = new C03PacketPlayer.C06PacketPlayerPosLook((double) args[0], (double) args[1], (double) args[2], arg3.floatValue(), arg4.floatValue(), (boolean) args[5]);
                break;
            case 13:
                C07PacketPlayerDigging.Action c07Action = Arrays.stream(C07PacketPlayerDigging.Action.values())
                        .filter(action -> action.getId() == (int) args[0]).findFirst().orElse(null);
                EnumFacing facing = Arrays.stream(EnumFacing.values())
                        .filter(facing1 -> facing1.getIndex() == (int) args[4]).findFirst().orElse(null);

                packet = new C07PacketPlayerDigging(c07Action, new BlockPos((double) args[1], (double) args[2], (double) args[3]), facing);
                break;
            case 14:
                Double arg5 = (Double) args[5];
                Double arg6 = (Double) args[6];
                Double arg7 = (Double) args[7];
                packet = new C08PacketPlayerBlockPlacement(new BlockPos((double) args[0], (double) args[1], (double) args[2]), (int) args[3],
                        (ItemStack) args[4], arg5.floatValue(), arg6.floatValue(), arg7.floatValue());
                break;
            case 15:
                packet = new C09PacketHeldItemChange((int) args[0]);
                break;
            case 19:
                ScriptPlayerCapabilites abilites = (ScriptPlayerCapabilites) args[0];
                packet = new C13PacketPlayerAbilities(abilites.getActualAbilites());
                break;
            case 22:
                C16PacketClientStatus.EnumState state = Arrays.stream(C16PacketClientStatus.EnumState.values())
                        .filter(state1 -> state1.getId() == (int) args[0]).findFirst().orElse(null);
                packet = new C16PacketClientStatus(state);
                break;
            case 24:
                packet = new C18PacketSpectate((UUID) args[0]);
                break;
        }

        PacketUtil.sendPacketNoEvent(packet);
    }
}
