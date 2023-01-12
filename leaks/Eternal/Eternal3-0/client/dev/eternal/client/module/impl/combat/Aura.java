package dev.eternal.client.module.impl.combat;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.Client;
import dev.eternal.client.event.events.EventJump;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventRender3D;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.render.TargetHUD;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.EnumSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.util.combat.CombatUtil;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.movement.data.Rotation;
import dev.eternal.client.util.time.Stopwatch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;

import java.awt.*;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

@Getter
@ModuleInfo(name = "Aura", description = "Attacks nearby entities", category = Module.Category.COMBAT, defaultKey = Keyboard.KEY_R)
public class Aura extends Module {

  private final float[] rotations = new float[2];

  private boolean blocking = false, blocked = false;
  private final Stopwatch forceAttack = new Stopwatch();
  private final EnumSetting<RotMode> rotationsMode = new EnumSetting<>(this, "Rot Mode", RotMode.values());
  private final EnumSetting<BlockMode> blockMode = new EnumSetting<>(this, "Block Mode", BlockMode.values());
  private final NumberSetting reach = new NumberSetting(this, "Reach", "The minimum distance you and the target before you start attacking.", 4.2, 3, 8, 0.1);
  private final NumberSetting APS = new NumberSetting(this, "APS", "Attacks Per Second", 12, 4, 20, 0.1);
  private final NumberSetting hitChance = new NumberSetting(this, "Hit chance", "The chance to hit the target when swinging.", 0.95, 0, 1, 0.01);
  private final BooleanSetting strafe = new BooleanSetting(this, "Move Fix", "Move using the killaura rotations.", false);
  private final BooleanSetting autoBlock = new BooleanSetting(this, "Auto block", "Block whilst attacking.", true);
  private final BooleanSetting silent = new BooleanSetting(this, "Silent", "Silent rotations.", true);
  private final BooleanSetting keepSprint = new BooleanSetting(this, "Keep sprint", "Continue sprinting whilst attacking.", true);
  private final BooleanSetting attack1_9 = new BooleanSetting(this, "1.9", "Attacks with 1.9 speed.", true);

  private final Predicate<Entity> entityPredicate = entity ->
      !entity.isBot &&
          entity.isEntityAlive() &&
          mc.thePlayer.getDistanceToEntity(entity) < reach.value() &&
          entity instanceof EntityLivingBase &&
//          !entity.isInvisible() &&
          entity != mc.thePlayer;

  private Entity target;
  private AutoPot autoPot;

  @Override
  public void init() {
    autoPot = Client.singleton().moduleManager().getByClass(AutoPot.class);
  }

  @Override
  protected void onDisable() {
    mc.thePlayer.setRenderBlocking(false);
  }

  @Subscribe
  public void onUpdate(EventUpdate eventUpdate) {
    if (eventUpdate.pre()) {
      setTarget();
      if (target == null) {
        mc.thePlayer.setRenderBlocking(false);
      }
      if (target == null) return;
      TargetHUD targetHUD = client.moduleManager().getByClass(TargetHUD.class);
      targetHUD.addTargetHUD((EntityLivingBase) target());
      preBlock();
      setPlayerRotations(eventUpdate);
      attackTarget();
    } else {
      if (target == null) return;
      postBlock();
    }
  }

  @Subscribe
  public void onMove(EventMove eventMove) {
    if (target == null) return;
    getRotations();
    if (strafe.value()) {
      eventMove.yaw(rotations[0]);
      MovementUtil.performMoveFix(eventMove);
    }
  }

  @Subscribe
  public void onJump(EventJump eventJump) {
    if (target == null || !strafe.value()) return;
    eventJump.yaw(rotations[0]);
  }

  private void setTarget() {
    target = mc.theWorld.loadedEntityList.stream()
        .filter(entityPredicate)
        .min(Comparator.comparingDouble(value -> mc.thePlayer.getHealth() + mc.thePlayer.hurtTime))
        .orElse(null);
  }

  public Entity findZombieOrDefault(Entity toSearch) {
    return mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityZombie && !mc.thePlayer.getUniqueID().equals(entity.getUniqueID()) && entity.getEntityBoundingBox().intersectsWith(toSearch.getEntityBoundingBox().expand(1, 1, 1))).findFirst().orElse(toSearch);
  }

  private void attackTarget() {
    if (attack1_9.value()) {
      if (!forceAttack.hasElapsed(CombatUtil.getAttackSpeed(mc.thePlayer.getHeldItem(), true))) return;
    } else if (Math.random() > (APS.value() / APS.max())
        && !(target.hurtResistantTime == 0
        && forceAttack.hasElapsed(250L))) return;

    if (ViaMCP.getInstance().getVersion() > 47) {
      attemptAttack();
      mc.thePlayer.swingItem();
      return;
    }
    mc.thePlayer.swingItem();
    attemptAttack();
  }

  private void getRotations() {
    Rotation rotation = CombatUtil.getAngleForEntityRandomized((EntityLivingBase) target);
    switch (rotationsMode.value()) {
      case VANILLA -> {
        rotations[0] = (float) (rotation.rotationYaw() + ThreadLocalRandom.current().nextDouble(-2, 2));
        rotations[1] = (float) (rotation.rotationPitch() + ThreadLocalRandom.current().nextDouble(-2, 2));
      }
      case SPERG -> {
        var v = 25;
        rotations[0] = (float) (rotation.rotationYaw() + ThreadLocalRandom.current().nextDouble(-v + mc.thePlayer.getDistanceToEntity(target), v - mc.thePlayer.getDistanceToEntity(target)));
        rotations[1] = (float) (rotation.rotationPitch() + ThreadLocalRandom.current().nextDouble(-v + mc.thePlayer.getDistanceToEntity(target), v - mc.thePlayer.getDistanceToEntity(target)));
      }
      case NONE -> {
        rotations[0] = mc.thePlayer.rotationYaw;
        rotations[1] = mc.thePlayer.rotationPitch;
      }
    }
  }

  private void setPlayerRotations(EventUpdate eventUpdate) {
    if (autoPot.isPotting()) return;
    Rotation rotation = eventUpdate.rotation();
    if (silent.value()) {
//      rotation.rotationYaw(rotations[0]);
//      rotation.rotationPitch(rotations[1]);
      mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYawHead = (mc.thePlayer.ticksExisted * 13.42F) % 360;
      mc.thePlayer.rotationPitchHead = 90;
    } else {
      mc.thePlayer.rotationYaw = rotations[0];
      mc.thePlayer.rotationPitch = rotations[1];
    }
  }


  @Subscribe
  public void onRender3D(EventRender3D eventRender3D) {
    if (target == null)
      return;
    GlStateManager.pushMatrix();
    final double x = this.getDiff(target.lastTickPosX, target.posX, eventRender3D.partialTicks(), RenderManager.renderPosX);
    final double y = this.getDiff(target.lastTickPosY, target.posY, eventRender3D.partialTicks(), RenderManager.renderPosY);
    final double z = this.getDiff(target.lastTickPosZ, target.posZ, eventRender3D.partialTicks(), RenderManager.renderPosZ);
    final boolean bobbing = mc.gameSettings.viewBobbing;
    mc.gameSettings.viewBobbing = false;
    GlStateManager.disableTexture2D();
    Color color = new Color(client.scheme().getPrimary());
    GL11.glColor3d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
    GL11.glLineWidth(2);
    GL11.glEnable(GL11.GL_LINE_SMOOTH);
    GL11.glBegin(3);
    GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
    GL11.glVertex3d(x, y + target.getEyeHeight(), z);
    GL11.glEnd();
    GlStateManager.enableTexture2D();
    mc.gameSettings.viewBobbing = bobbing;
    GlStateManager.popMatrix();
  }

  private double getDiff(final double lastI, final double i, final float ticks, final double ownI) {
    return lastI + (i - lastI) * ticks - ownI;
  }

  private void attemptAttack() {
    if (Math.random() > hitChance.value()) return;
    forceAttack.reset();
//    Entity entity = findZombieOrDefault(target);
//    if (entity instanceof EntityZombie entityZombie)
//      Client.getSingleton().displayMessage("Found zombie: " + entityZombie.getUniqueID());
    if (!keepSprint.value()) {
      mc.thePlayer.motionX *= 0.6;
      mc.thePlayer.motionZ *= 0.6;
    }
    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
  }

  private void preBlock() {
    if (target == null || !canBlock())
      return;
    if (Math.random() > 0.2) {
      blocking = true;
    }
    mc.thePlayer.setRenderBlocking(canBlock());
    switch (blockMode.value()) {
      case VANILLA -> {
        if (!blocking) {
          mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
          blocking = true;
        }
      }
      case PACKET -> mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      case LEGIT -> {
        if (blocking) {
          mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 10);
          mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
      }
      case SPAM -> {
        if (ThreadLocalRandom.current().nextDouble(0, 5) > 1) {
          mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 1);
        }
      }
    }
  }

  private void postBlock() {
    if (target == null)
      return;
    mc.thePlayer.setRenderBlocking(canBlock());
    if (!canBlock()) return;
    switch (blockMode.value()) {
      case PACKET -> mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
      case LEGIT -> {
        mc.thePlayer.stopUsingItem();
        if (blocking)
          mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
      }
    }
  }

  private boolean canBlock() {
    return autoBlock.value() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
  }

  private void vanillaAutoBlock() {

  }

  @Getter
  @AllArgsConstructor
  public enum RotMode implements INameable {
    VANILLA("Vanilla"),
    SPERG("Sperg"),
    NONE("None");
    private final String getName;
  }

  @Getter
  @AllArgsConstructor
  public enum BlockMode implements INameable {
    VANILLA("Vanilla"),
    PACKET("Packet"),
    FAKE("Fake"),
    LEGIT("Legit"),
    SPAM("Spam");
    private final String getName;
  }

}