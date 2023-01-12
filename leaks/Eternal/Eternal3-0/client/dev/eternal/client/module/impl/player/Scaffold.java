package dev.eternal.client.module.impl.player;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventInput;
import dev.eternal.client.event.events.EventMove;
import dev.eternal.client.event.events.EventPacket;
import dev.eternal.client.event.events.EventRender3D;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.property.impl.BooleanSetting;
import dev.eternal.client.property.impl.NumberSetting;
import dev.eternal.client.util.math.RayCastUtil;
import dev.eternal.client.util.movement.MovementUtil;
import dev.eternal.client.util.network.PacketUtil;
import dev.eternal.client.util.player.RotationUtil;
import dev.eternal.client.util.world.WorldUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
;

/*
 * @author W4z3d & Eternal
 */
@ModuleInfo(
    name = "Scaffold",
    defaultKey = Keyboard.KEY_B,
    description = "Automatically places blocks under you.",
    category = Module.Category.MOVEMENT)
public class Scaffold extends Module {
  private static final Queue<BlockPos> BLOCK_QUEUE = new LinkedList<>();
  private static final HashSet<BlockPos> VISITED_HASH = new HashSet<>();

  private final BooleanSetting moveFix =
      new BooleanSetting(this, "Move Fix", "Move using the scaffold rotations.", false);
  private final BooleanSetting sprint =
      new BooleanSetting(this, "Sprint", "Allow sprinting whilst scaffolding.", false);
  private final BooleanSetting keepRotations =
      new BooleanSetting(
          this, "Keep rotations", "Keep the rotations after block placing is done.", false);
  private final BooleanSetting tower =
      new BooleanSetting(this, "Tower", "Jump after placing a block.", false);
  private final BooleanSetting antiSchizophrenic =
      new BooleanSetting(this, "Null place", "For bypassing Anticheats.", false);
  private final BooleanSetting sneak =
      new BooleanSetting(this, "Sneak", "Sneaks when near blocks.", false);
  private final NumberSetting sneakTicks =
      new NumberSetting(this, "Sneak distance", "The distance before sneaking.", 2, 0, 5, 1);

  private boolean placedThisTick;
  private BlockPos blockToPlaceOn;
  private float rotationYaw;
  private float rotationPitch;
  private int silentSlot;
  private int silentC09Slot;

  public Scaffold() {
    sneakTicks.visible(sneak::value);
  }

  @Override
  public void onEnable() {
    if (mc.thePlayer == null) return;
    silentC09Slot = mc.thePlayer.inventory.currentItem;
    this.rotationYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw + 180.0F);
  }

  @Override
  public void onDisable() {
    PacketUtil.sendSilent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }
  }

  @Subscribe
  public void onPacket(EventPacket packet) {
    if (antiSchizophrenic.value()) {
      if (packet.getPacket()
          instanceof C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement) {
        c08PacketPlayerBlockPlacement.stack(
            mc.thePlayer.inventory.mainInventory[this.silentSlot]);

        // The "onPlayerRightClick" function uses the current held item for the packet rather than
        // what is parsed in
      }
    }
  }

  @Subscribe
  public void onInput(EventInput eventInput) {
    this.silentSlot = getSilentSlot();
    if (silentC09Slot != silentSlot && silentSlot != -1) {
      PacketUtil.sendSilent(new C09PacketHeldItemChange(silentSlot));
      silentC09Slot = silentSlot;
    }
    if (this.silentSlot >= 0) {
      this.doBlockGettingRoutine(1.0F);
      if (!this.doPlaceRoutine(1.0F)) {
        this.doBlockGettingRoutine(2.0F);
        this.doPlaceRoutine(2.0F);
      }
    }
  }

  @Subscribe
  public void onMotion(EventUpdate event) {
    if (event.pre()) {
      //            if (!this.sprint.getValue())
      //                mc.thePlayer.setSprinting(false);

      if (sneak.value()) {
        if (WorldUtil.distanceToGround(
            MovementUtil.getFuturePosition(sneakTicks.value().intValue()))
            > 1) {
          KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
          KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
      }

      if (keepRotations.value() || placedThisTick) {
        event.rotation().rotationYaw(this.rotationYaw);
        event.rotation().rotationPitch(this.rotationPitch);
        mc.thePlayer.rotationYawHead =
            mc.thePlayer.renderYawOffset = event.rotation().rotationYaw();
        mc.thePlayer.rotationPitchHead = event.rotation().rotationPitch();
      }

      if (mc.gameSettings.showDebugInfo) {
        mc.thePlayer.rotationYaw = event.rotation().rotationYaw();
        mc.thePlayer.rotationPitch = event.rotation().rotationPitch();
      }
      placedThisTick = false;
    }
  }

  private boolean doPlaceRoutine(float partialTicks) {
    final MovingObjectPosition rayCastHit =
        RayCastUtil.rayCast(
            this.rotationYaw,
            this.rotationPitch,
            mc.playerController.getBlockReachDistance(),
            partialTicks);
    if (this.silentSlot >= 0
        && rayCastHit != null
        && rayCastHit.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
        && rayCastHit.getBlockPos().offset(rayCastHit.sideHit).getY() + 1.0F
        <= mc.thePlayer.getEntityBoundingBox().minY) {
      final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(this.silentSlot);

      if ((mc.thePlayer.onGround || WorldUtil.distanceToGround() > 0.6)
          && mc.playerController.onPlayerRightClick(
          mc.thePlayer,
          mc.theWorld,
          itemStack,
          rayCastHit.getBlockPos(),
          rayCastHit.sideHit,
          rayCastHit.hitVec)) {
        mc.thePlayer.swingItem();
        placedThisTick = true;
        if (tower.value() && mc.gameSettings.keyBindJump.isKeyDown()) {
          if (WorldUtil.distanceToGround() < 0.15F) {
            mc.thePlayer.motionY = 0.42F;
          }
        }
        return true;
      }

      if (itemStack.stackSize == 0) {
        mc.thePlayer.inventory.mainInventory[this.silentSlot] = null;
      }
    }
    return true;
  }

  private void doBlockGettingRoutine(float partialTicks) {
    final Vec3 playerHeadPos = mc.thePlayer.getPositionEyes(partialTicks);
    final Vec3 playerFeetPos = mc.thePlayer.getPositionFeet(partialTicks);
    final BlockPos playerBlockPos = new BlockPos(playerFeetPos.subtract(0.0, 0.5, 0.0));
    this.blockToPlaceOn = getBlockToPlaceOn(playerBlockPos, 1000);

    if (!mc.theWorld.isAirBlock(playerBlockPos)) return;

    if (this.blockToPlaceOn != null) {
      final Vec3 blockToPlaceOnCenter = blockToPlaceOn.getCenter();

      final EnumFacing enumFacing =
          EnumFacing.getFacingFromVector(
              (float) (playerFeetPos.xCoord - blockToPlaceOnCenter.xCoord),
              (float) (playerFeetPos.yCoord - 0.5 - blockToPlaceOnCenter.yCoord),
              (float) (playerFeetPos.zCoord - blockToPlaceOnCenter.zCoord));

      final Vec3 aimPos =
          RotationUtil.getClosestPosition(
              mc.thePlayer.getPositionFeet(partialTicks),
              mc.theWorld
                  .getBlockState(blockToPlaceOn)
                  .getBlock()
                  .getBlockBounds()
                  .offset(blockToPlaceOn.getX(), blockToPlaceOn.getY(), blockToPlaceOn.getZ()));
      final float[] rotations = RotationUtil.getRotations(playerHeadPos, aimPos);

      final int sampleCount = RotationUtil.LAZY_AIM_OFFSETS.size();
      final float spacing = 1.0F;

      boolean found = false;
      for (int i = 0; i < sampleCount; i++) {
        final float[] fibonacciOffset = RotationUtil.LAZY_AIM_OFFSETS.get(i);

        final float yawOffs = fibonacciOffset[0] * spacing;
        final float pitchOffs = fibonacciOffset[1] * spacing;

        final float yaw = this.rotationYaw + yawOffs;
        final float pitch = this.rotationPitch + pitchOffs;

        if (pitch > 90.0F || pitch < -90.0F) continue;

        final MovingObjectPosition mop =
            RayCastUtil.rayCast(
                yaw, pitch, mc.playerController.getBlockReachDistance(), partialTicks);
        if (mop != null
            && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
            && mop.sideHit == enumFacing
            && mop.getBlockPos().equals(blockToPlaceOn)) {

          found = true;
          this.rotationYaw = yaw;
          this.rotationPitch = pitch;
          break;
        }
      }

      if (!found) {
        this.rotationYaw = rotations[0];
        this.rotationPitch = rotations[1];
      }
    }
  }

  @Subscribe
  public void onRelativeMove(EventMove event) {
    if (this.moveFix.value()) {
      if (keepRotations.value() || placedThisTick) {
        //                this.rotationYaw = RotationUtil.performGCDFix(this.rotationYaw);
        event.yaw(this.rotationYaw);
        MovementUtil.performMoveFix(event);
      }
    }
  }

  @Subscribe
  public void onRender3D(EventRender3D eventRender3D) {
    GlStateManager.disableTexture2D();
    GlStateManager.disableLighting();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GlStateManager.color(1f, 1f, 1f, 0.5f);
    GlStateManager.pushMatrix();
    GlStateManager.translate(
        -RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

    GL11.glEnable(GL11.GL_LINE_SMOOTH);

    GL11.glLineWidth(5.0F);
    if (this.blockToPlaceOn != null) {
      GlStateManager.color(1f, 0f, 0f, 1f);
      RenderGlobal.drawSelectionBoundingBox(
          mc.theWorld
              .getBlockState(blockToPlaceOn)
              .getBlock()
              .getSelectedBoundingBox(mc.theWorld, this.blockToPlaceOn));
    }

    GL11.glDisable(GL11.GL_LINE_SMOOTH);

    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
    GlStateManager.color(1f, 1f, 1f, 1f);
  }

  /**
   * Performs BFS to find the closest solid block to the players feet position
   *
   * @param feetPos          the players feet position
   * @param maxCheckedBlocks the maximum amount of blocks to check
   * @return the found BlockPos or null
   */
  private BlockPos getBlockToPlaceOn(BlockPos feetPos, int maxCheckedBlocks) {
    BLOCK_QUEUE.clear();
    VISITED_HASH.clear();

    if (feetPos.getY() + 1.0F > mc.thePlayer.getEntityBoundingBox().minY) {
      feetPos = feetPos.offset(EnumFacing.DOWN);
    }
    BLOCK_QUEUE.offer(feetPos);

    int c = 0;
    BlockPos blockPos;
    while (c < maxCheckedBlocks && (blockPos = BLOCK_QUEUE.poll()) != null) {
      if (!mc.theWorld.isAirBlock(blockPos)) {
        return blockPos;
      }

      if (VISITED_HASH.contains(blockPos)) continue;
      VISITED_HASH.add(blockPos);
      c++;

      for (EnumFacing value :
          new EnumFacing[]{
              EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST
          }) {
        final BlockPos newBlockPos = blockPos.offset(value);

        if (newBlockPos.getY() + 1.0F <= mc.thePlayer.getEntityBoundingBox().minY) {
          BLOCK_QUEUE.offer(newBlockPos);
        }
      }
    }

    return null;
  }

  private int getSilentSlot() {
    ArrayList<Integer> slots = getSlots();
    return !slots.isEmpty() ? slots.get(0) : -1;
  }

  private ArrayList<Integer> getSlots() {
    ArrayList<Integer> slots = new ArrayList<>();

    for (int i = 0; i < 9; i++) {
      final ItemStack stackInHotBar = mc.thePlayer.inventory.getStackInSlot(i);
      if (stackInHotBar == null) continue;
      final Item item = stackInHotBar.getItem();

      if (item instanceof ItemSeeds) slots.add(i);

      if (item instanceof ItemBlock) {
        final ItemBlock itemBlock = (ItemBlock) stackInHotBar.getItem();
        if (itemBlock.hasContainerItem()) continue;
        if (itemBlock.getBlock().hasTileEntity()) continue;
        slots.add(i);
      }
    }

    return slots;
  }
}
