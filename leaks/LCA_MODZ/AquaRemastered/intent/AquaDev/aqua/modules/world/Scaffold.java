package intent.AquaDev.aqua.modules.world;

import de.Hero.settings.Setting;
import events.Event;
import events.EventType;
import events.listeners.EventPostRender2D;
import events.listeners.EventPreMotion;
import events.listeners.EventRender2D;
import events.listeners.EventRenderNameTags;
import events.listeners.EventSycItem;
import events.listeners.EventTick;
import events.listeners.EventUpdate;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.gui.novolineOld.themesScreen.ThemeScreen;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.combat.Killaura;
import intent.AquaDev.aqua.modules.movement.Fly;
import intent.AquaDev.aqua.modules.visual.Blur;
import intent.AquaDev.aqua.modules.visual.ShaderMultiplier;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.utils.RandomUtil;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.RotationUtil;
import intent.AquaDev.aqua.utils.TimeUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Scaffold extends Module {
   private BlockPos espPos = null;
   private final Scaffold.BlackList blackList = new Scaffold.BlackList();
   public float[] rots = new float[2];
   public float[] lastRots = new float[2];
   public int slot;
   public MovingObjectPosition objectPosition = null;
   private final ArrayList<Vec3> lastPositions = new ArrayList<>();
   private double[] xyz = new double[3];
   public static Scaffold.BlockData data;
   TimeUtil timeUtil = new TimeUtil();
   private double posY;
   public boolean down;
   private int offGround = 0;

   public Scaffold() {
      super("Scaffold", Module.Type.World, "Scaffold", 0, Category.World);
      Aqua.setmgr.register(new Setting("Sprint", this, false));
      Aqua.setmgr.register(new Setting("EnableCalculate", this, false));
      Aqua.setmgr.register(new Setting("EnableSneak", this, false));
      Aqua.setmgr.register(new Setting("BlockCount", this, true));
      Aqua.setmgr.register(new Setting("BlockESP", this, true));
      Aqua.setmgr.register(new Setting("Intave", this, false));
      Aqua.setmgr.register(new Setting("Swing", this, false));
      Aqua.setmgr.register(new Setting("Down", this, false));
      Aqua.setmgr.register(new Setting("SameY", this, false));
      Aqua.setmgr.register(new Setting("Expand", this, false));
      Aqua.setmgr.register(new Setting("AutoDisable", this, false));
      Aqua.setmgr.register(new Setting("BMCBoost", this, false));
      Aqua.setmgr.register(new Setting("LegitPlace", this, false));
      Aqua.setmgr.register(new Setting("ReverseYaw", this, false));
      Aqua.setmgr.register(new Setting("SneakModify", this, 1.0, 0.3, 1.0, false));
      Aqua.setmgr.register(new Setting("Expandlength", this, 8.0, 0.0, 25.0, false));
      Aqua.setmgr.register(new Setting("YawPosition", this, 90.0, 0.0, 380.0, false));
      Aqua.setmgr.register(new Setting("RotationMode", this, "Static", new String[]{"Static", "Calculated"}));
      Aqua.setmgr.register(new Setting("Shader", this, "Glow", new String[]{"Glow", "Shadow", "Jello"}));
      Aqua.setmgr
         .register(new Setting("Tower", this, "None", new String[]{"None", "Watchdog", "VerusFast", "IntaveFast", "Cubecraft", "UpdatedNCP", "WatchdogNew"}));
   }

   @Override
   public void onEnable() {
      if (Aqua.setmgr.getSetting("ScaffoldEnableCalculate").isState()) {
         mc.thePlayer.rotationPitchHead = 82.0F;
         mc.thePlayer.rotationYawHead = 180.0F;
      }

      this.offGround = 0;
      if (Aqua.setmgr.getSetting("ScaffoldEnableSneak").isState()) {
         mc.gameSettings.keyBindSneak.pressed = true;
      }

      this.posY = mc.thePlayer.posY;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      mc.timer.timerSpeed = 1.0F;
      mc.gameSettings.keyBindSneak.pressed = false;
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventRenderNameTags && Aqua.setmgr.getSetting("ScaffoldBlockESP").isState()) {
         BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
         this.espPos = mc.theWorld.getBlockState(pos).getBlock().isFullBlock() ? pos : this.espPos;
         if (this.espPos != null) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GlStateManager.disableCull();
            GL11.glDepthMask(false);
            Color color = new Color(Aqua.setmgr.getSetting("HUDColor").getColor());
            Blur.drawBlurred(
               () -> RenderUtil.drawBlockESP(
                     this.espPos, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 1.0F, 0.0F, 1.0F
                  ),
               false
            );
            RenderUtil.drawBlockESP(
               this.espPos, (float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, 0.39215687F, 0.0F, 1.0F
            );
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthMask(true);
            GlStateManager.enableCull();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2848);
         }
      }

      if (event instanceof EventTick) {
         if (mc.thePlayer.onGround) {
            this.offGround = 0;
         } else {
            ++this.offGround;
         }
      }

      if (event instanceof EventRender2D && Aqua.setmgr.getSetting("ScaffoldBlockCount").isState()) {
         ScaledResolution sr = new ScaledResolution(mc);
         if (!GuiNewChat.animatedChatOpen) {
            if (Aqua.setmgr.getSetting("ScaffoldShader").getCurrentMode().equalsIgnoreCase("Jello") && !ThemeScreen.themeHero) {
               ShaderMultiplier.drawGlowESP(
                  () -> RenderUtil.drawRoundedRect2Alpha(
                        (double)((float)sr.getScaledWidth() / 2.0F - 18.0F),
                        (double)(sr.getScaledHeight() - 85),
                        (double)Aqua.INSTANCE.novolineSmall.getStringWidth(getBlockCount() + "     Blocks"),
                        15.0,
                        3.0,
                        new Color(5, 5, 5, 255)
                     ),
                  false
               );
               ShaderMultiplier.drawGlowESP(
                  () -> RenderUtil.drawTriangleFilled2(
                        (float)sr.getScaledWidth() / 2.0F - 4.0F, (float)(sr.getScaledHeight() - 70), 5.0F, 5.0F, new Color(5, 5, 5, 255).getRGB()
                     ),
                  false
               );
               RenderUtil.drawRoundedRect2Alpha(
                  (double)((float)sr.getScaledWidth() / 2.0F - 18.0F),
                  (double)(sr.getScaledHeight() - 85),
                  (double)Aqua.INSTANCE.novolineSmall.getStringWidth(getBlockCount() + "     Blocks"),
                  15.0,
                  3.0,
                  new Color(40, 40, 40, 255)
               );
               RenderUtil.drawTriangleFilled2(
                  (float)sr.getScaledWidth() / 2.0F - 4.0F, (float)(sr.getScaledHeight() - 70), 5.0F, 5.0F, new Color(40, 40, 40, 255).getRGB()
               );
               Aqua.INSTANCE
                  .novolineSmall
                  .drawString(getBlockCount() + "", (float)sr.getScaledWidth() / 2.0F - 16.0F, (float)sr.getScaledHeight() - 81.0F, -1);
               Aqua.INSTANCE
                  .novolineSmall
                  .drawString(
                     "Blocks",
                     (float)Aqua.INSTANCE.novolineSmall.getStringWidth(getBlockCount() + "") + (float)sr.getScaledWidth() / 2.0F - 12.0F,
                     (float)(sr.getScaledHeight() - 81),
                     Color.gray.getRGB()
                  );
            }
         } else if (Aqua.setmgr.getSetting("ScaffoldShader").getCurrentMode().equalsIgnoreCase("Jello") && !ThemeScreen.themeHero) {
            Shadow.drawGlow(
               () -> RenderUtil.drawRoundedRect2Alpha(
                     (double)((float)sr.getScaledWidth() / 2.0F - 18.0F),
                     (double)(sr.getScaledHeight() - 80),
                     (double)Aqua.INSTANCE.novolineSmall.getStringWidth(getBlockCount() + "     Blocks"),
                     15.0,
                     3.0,
                     new Color(40, 40, 40, 255)
                  ),
               false
            );
            Shadow.drawGlow(
               () -> RenderUtil.drawTriangleFilled2(
                     (float)sr.getScaledWidth() / 2.0F - 4.0F, (float)(sr.getScaledHeight() - 65), 5.0F, 5.0F, new Color(40, 40, 40, 255).getRGB()
                  ),
               false
            );
            RenderUtil.drawRoundedRect2Alpha(
               (double)((float)sr.getScaledWidth() / 2.0F - 18.0F),
               (double)(sr.getScaledHeight() - 80),
               (double)Aqua.INSTANCE.novolineSmall.getStringWidth(getBlockCount() + "     Blocks"),
               15.0,
               3.0,
               new Color(40, 40, 40, 255)
            );
            RenderUtil.drawTriangleFilled2(
               (float)sr.getScaledWidth() / 2.0F - 4.0F, (float)(sr.getScaledHeight() - 65), 5.0F, 5.0F, new Color(40, 40, 40, 255).getRGB()
            );
            Aqua.INSTANCE.novolineSmall.drawString(getBlockCount() + "", (float)sr.getScaledWidth() / 2.0F - 16.0F, (float)sr.getScaledHeight() - 76.5F, -1);
            Aqua.INSTANCE
               .novolineSmall
               .drawString(
                  "Blocks",
                  (float)Aqua.INSTANCE.novolineSmall.getStringWidth(getBlockCount() + "") + (float)sr.getScaledWidth() / 2.0F - 12.0F,
                  (float)sr.getScaledHeight() - 76.5F,
                  Color.gray.getRGB()
               );
         }
      }

      if (event instanceof EventRender2D && Aqua.setmgr.getSetting("ScaffoldBlockCount").isState()) {
         ScaledResolution sr = new ScaledResolution(mc);
         if (ThemeScreen.themeLoaded && ThemeScreen.themeHero) {
            RenderUtil.drawRoundedRect2Alpha(
               (double)((int)((float)sr.getScaledWidth() / 2.0F + 10.0F)),
               (double)((int)((float)sr.getScaledHeight() / 2.0F - 5.0F)),
               (double)(Aqua.INSTANCE.roboto2.getStringWidth(" " + getBlockCount()) + 1),
               13.0,
               0.0,
               new Color(0, 0, 0, 255)
            );
            RenderUtil.drawRoundedRect2Alpha(
               (double)((int)((float)sr.getScaledWidth() / 2.0F + 10.0F)),
               (double)((int)((float)sr.getScaledHeight() / 2.0F - 6.0F)),
               (double)(Aqua.INSTANCE.roboto2.getStringWidth(" " + getBlockCount()) + 1),
               1.0,
               0.0,
               new Color(6, 226, 70, 160)
            );
            Aqua.INSTANCE
               .roboto2
               .drawString(
                  getBlockCount() + "",
                  (float)((int)((float)sr.getScaledWidth() / 2.0F + 11.0F)),
                  (float)((int)((float)sr.getScaledHeight() / 2.0F - 6.0F)),
                  -1
               );
         } else if (!Aqua.setmgr.getSetting("ScaffoldShader").getCurrentMode().equalsIgnoreCase("Jello")
            && Aqua.setmgr.getSetting("ScaffoldShader").getCurrentMode().equalsIgnoreCase("Shadow")) {
            if (!Aqua.setmgr.getSetting("ScaffoldShader").getCurrentMode().equalsIgnoreCase("Jello")) {
               Shadow.drawGlow(
                  () -> RenderUtil.drawRoundedRect2Alpha(
                        (double)((int)((float)sr.getScaledWidth() / 2.0F - 25.0F)),
                        (double)((int)((float)sr.getScaledHeight() / 2.0F + 20.0F)),
                        (double)(mc.fontRendererObj.getStringWidth("Blocks: " + getBlockCount()) + 5),
                        13.0,
                        5.0,
                        Color.BLACK
                     ),
                  false
               );
            } else {
               ShaderMultiplier.drawGlowESP(
                  () -> RenderUtil.drawRoundedRect2Alpha(
                        (double)((int)((float)sr.getScaledWidth() / 2.0F - 25.0F)),
                        (double)((int)((float)sr.getScaledHeight() / 2.0F + 20.0F)),
                        (double)(mc.fontRendererObj.getStringWidth("Blocks: " + getBlockCount()) + 5),
                        13.0,
                        5.0,
                        new Color(Aqua.setmgr.getSetting("HUDColor").getColor())
                     ),
                  false
               );
            }

            Blur.drawBlurred(
               () -> RenderUtil.drawRoundedRect(
                     (double)((int)((float)sr.getScaledWidth() / 2.0F - 25.0F)),
                     (double)((int)((float)sr.getScaledHeight() / 2.0F + 20.0F)),
                     (double)(mc.fontRendererObj.getStringWidth("Blocks: " + getBlockCount()) + 5),
                     13.0,
                     5.0,
                     new Color(0, 0, 0, 120).getRGB()
                  ),
               false
            );
         }
      }

      if (event instanceof EventPostRender2D) {
         ScaledResolution sr = new ScaledResolution(mc);
         if (Aqua.setmgr.getSetting("ScaffoldBlockCount").isState()
            && !Aqua.setmgr.getSetting("ScaffoldShader").getCurrentMode().equalsIgnoreCase("Jello")
            && (!ThemeScreen.themeLoaded || !ThemeScreen.themeHero)
            && (
               Aqua.setmgr.getSetting("ScaffoldShader").getCurrentMode().equalsIgnoreCase("Shadow")
                  || Aqua.setmgr.getSetting("ScaffoldShader").getCurrentMode().equalsIgnoreCase("Glow")
            )) {
            RenderUtil.drawRoundedRect2Alpha(
               (double)((int)((float)sr.getScaledWidth() / 2.0F - 25.0F)),
               (double)((int)((float)sr.getScaledHeight() / 2.0F + 20.0F)),
               (double)(mc.fontRendererObj.getStringWidth("Blocks: " + getBlockCount()) + 5),
               13.0,
               5.0,
               new Color(0, 0, 0, 70)
            );
            Aqua.INSTANCE
               .comfortaa4
               .drawString(
                  "Blocks : " + getBlockCount(),
                  (float)((int)((float)sr.getScaledWidth() / 2.0F - 20.0F)),
                  (float)((int)((float)sr.getScaledHeight() / 2.0F + 23.0F)),
                  -1
               );
         }
      }

      if (event instanceof EventSycItem && this.getBlockSlot() != -1) {
         ((EventSycItem)event).slot = this.slot = this.getBlockSlot();
      }

      if (event instanceof EventUpdate && event.type == EventType.PRE) {
         this.objectPosition = null;
         data = this.find(new Vec3(0.0, 0.0, 0.0));
      }

      if (event instanceof EventUpdate) {
         data = this.find(new Vec3(0.0, 0.0, 0.0));
         if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null) {
            if (Aqua.setmgr.getSetting("ScaffoldBMCBoost").isState()) {
               mc.timer.timerSpeed = 1.105F;
            }
         } else {
            mc.timer.timerSpeed = 1.0F;
         }

         if (Aqua.setmgr.getSetting("ScaffoldAutoDisable").isState() && Killaura.target != null) {
            Aqua.moduleManager.getModuleByName("Scaffold").setState(false);
         }
      }

      if (event instanceof EventUpdate) {
         if (Aqua.setmgr.getSetting("ScaffoldTower").getCurrentMode().equalsIgnoreCase("Cubecraft") && mc.gameSettings.keyBindJump.pressed) {
            if (this.timeUtil.hasTimePassed(50L)) {
               mc.thePlayer.motionY = 0.42;
               this.timeUtil.reset();
            }

            if (isOnGround(0.1)) {
               mc.thePlayer.motionY = 0.42;
            }
         }

         if (Aqua.setmgr.getSetting("ScaffoldTower").getCurrentMode().equalsIgnoreCase("Watchdog") && mc.gameSettings.keyBindJump.pressed) {
            if (mc.thePlayer.onGround) {
               mc.thePlayer.motionY = 0.409;
            }

            if (this.offGround == 3) {
               float random1 = RandomUtil.instance.nextFloat(0.9908900590734863, 0.9909900590734863);
               mc.thePlayer.motionY = (mc.thePlayer.motionY - 0.1) * (double)random1;
            }
         }

         if (Aqua.setmgr.getSetting("ScaffoldTower").getCurrentMode().equalsIgnoreCase("UpdatedNCP") && mc.gameSettings.keyBindJump.pressed) {
            if (!mc.thePlayer.isMoving()) {
               if (this.timeUtil.hasReached(2000L)) {
                  this.timeUtil.reset();
               } else if (mc.thePlayer.ticksExisted % 3 == 1 && this.offGround > 1 && !mc.thePlayer.onGround) {
                  mc.thePlayer.motionY = 0.4196;
               }

               this.timeUtil.reset();
            } else {
               if (mc.thePlayer.onGround) {
                  mc.thePlayer.motionY = 0.409;
               }

               if (this.offGround == 3) {
                  float random1 = RandomUtil.instance.nextFloat(0.9908900590734863, 0.9909900590734863);
                  mc.thePlayer.motionY = (mc.thePlayer.motionY - 0.1) * (double)random1;
               }
            }
         }

         if (Aqua.setmgr.getSetting("ScaffoldTower").getCurrentMode().equalsIgnoreCase("WatchdogNew")
            && mc.gameSettings.keyBindJump.pressed
            && mc.thePlayer.isMoving()) {
            if (this.timeUtil.hasReached(2000L)) {
               this.timeUtil.reset();
            } else if (mc.thePlayer.ticksExisted % 3 == 1 && this.offGround > 1 && !mc.thePlayer.onGround) {
               mc.thePlayer.motionY = 0.4196;
            }

            this.timeUtil.reset();
         }

         if (Aqua.setmgr.getSetting("ScaffoldTower").getCurrentMode().equalsIgnoreCase("IntaveFast")
            && mc.gameSettings.keyBindJump.pressed
            && !mc.gameSettings.keyBindForward.pressed) {
            if (mc.thePlayer.onGround) {
               mc.thePlayer.motionY = 0.405;
            }

            if (this.offGround == 5) {
               float random1 = RandomUtil.instance.nextFloat(0.9908900590734863, 0.9909900590734863);
               mc.thePlayer.motionY = (mc.thePlayer.motionY - 0.08) * (double)random1;
            }
         }

         if (Aqua.setmgr.getSetting("ScaffoldTower").getCurrentMode().equalsIgnoreCase("VerusFast")) {
            if (mc.gameSettings.keyBindJump.pressed) {
               Fly.sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0.0F, 0.5F, 0.0F));
               Fly.sendPacketUnlogged(
                  new C08PacketPlayerBlockPlacement(
                     new BlockPos(Fly.getX(), Fly.getY() - 1.5, Fly.getZ()),
                     1,
                     new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))),
                     0.0F,
                     0.94F,
                     0.0F
                  )
               );
               if (this.timeUtil.hasReached(5000L)) {
                  this.timeUtil.reset();
               } else {
                  mc.thePlayer.motionY = 0.70096;
               }
            } else {
               mc.timer.timerSpeed = 1.0F;
            }

            this.timeUtil.reset();
         }

         if (!Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Intave")) {
            if (Aqua.setmgr.getSetting("ScaffoldSprint").isState()) {
               mc.gameSettings.keyBindSprint.pressed = true;
            } else {
               mc.thePlayer.setSprinting(false);
            }
         } else if (Aqua.setmgr.getSetting("ScaffoldSprint").isState()
            && Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Intave")
            && !mc.gameSettings.keyBindJump.pressed) {
            mc.thePlayer.setSprinting(true);
         } else {
            mc.thePlayer.setSprinting(false);
         }

         if (!Aqua.setmgr.getSetting("ScaffoldLegitPlace").isState()) {
            if (Aqua.setmgr.getSetting("ScaffoldSameY").isState() && mc.thePlayer.onGround) {
            }

            if (Aqua.setmgr.getSetting("ScaffoldDown").isState()) {
               if (mc.gameSettings.keyBindSneak.pressed) {
                  this.down = true;
                  this.posY = mc.thePlayer.posY - 1.0;
               } else {
                  this.down = false;
                  this.posY = mc.thePlayer.posY;
               }
            }

            data = this.find(new Vec3(0.0, 0.0, 0.0));
            if (data != null && this.getBlockSlot() != -1) {
               BlockPos blockpos = mc.objectMouseOver.getBlockPos();
               mc.playerController.updateController();
               Vec3 hitVec = new Vec3(Scaffold.BlockData.getPos())
                  .addVector(0.5, 0.5, 0.5)
                  .add(new Vec3(Scaffold.BlockData.getFacing().getDirectionVec()).multi(0.5));
               if (this.slot != -1
                  && mc.playerController
                     .onPlayerRightClick(
                        mc.thePlayer,
                        mc.theWorld,
                        mc.thePlayer.inventory.getStackInSlot(this.slot),
                        Scaffold.BlockData.getPos(),
                        Scaffold.BlockData.getFacing(),
                        hitVec
                     )) {
                  if (Aqua.setmgr.getSetting("ScaffoldSwing").isState()) {
                     mc.thePlayer.swingItem();
                  } else {
                     mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                  }
               }
            }

            BlockPos var11 = mc.objectMouseOver.getBlockPos();
         } else {
            data = this.find(new Vec3(0.0, 0.0, 0.0));
            BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
               mc.gameSettings.keyBindSneak.pressed = true;
            } else if (!Aqua.setmgr.getSetting("DisablerModes").getCurrentMode().equalsIgnoreCase("Intave")) {
               mc.gameSettings.keyBindSneak.pressed = false;
            }

            if (mc.gameSettings.keyBindJump.pressed) {
               mc.thePlayer.setSprinting(false);
            }

            if (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed) != null || !Aqua.setmgr.getSetting("ScaffoldSprint").isState()) {
               mc.thePlayer.setSprinting(false);
               mc.gameSettings.keyBindSprint.pressed = false;
            }

            if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
               this.rightClickMouse(mc.thePlayer.inventory.getStackInSlot(this.slot), this.slot);
            }

            if (mc.gameSettings.keyBindJump.pressed) {
               mc.gameSettings.keyBindSneak.pressed = true;
            } else {
               mc.gameSettings.keyBindSneak.pressed = mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air;
            }
         }
      }

      if (event instanceof EventPreMotion) {
         if (data == null) {
            return;
         }

         float[] rotation2 = rotationrecode2(data);
         if (Aqua.setmgr.getSetting("ScaffoldRotationMode").getCurrentMode().equalsIgnoreCase("Static")) {
            RotationUtil.yaw = mc.thePlayer.rotationYawHead;
            RotationUtil.pitch = mc.thePlayer.rotationPitchHead;
            ((EventPreMotion)event).setYaw(mc.thePlayer.rotationYaw + 180.0F);
            ((EventPreMotion)event).setPitch(82.0F);
         }

         if (Aqua.setmgr.getSetting("ScaffoldRotationMode").getCurrentMode().equalsIgnoreCase("Calculated")) {
            RotationUtil.yaw = mc.thePlayer.rotationYawHead;
            RotationUtil.pitch = mc.thePlayer.rotationPitchHead;
            ((EventPreMotion)event).setYaw(rotation2[0]);
            ((EventPreMotion)event).setPitch(rotation2[1]);
         }
      }
   }

   private Vec3 getPositionByFace(BlockPos position, EnumFacing facing) {
      Vec3 offset = new Vec3(
         (double)facing.getDirectionVec().getX() / 2.0, (double)facing.getDirectionVec().getY() / 2.0, (double)facing.getDirectionVec().getZ() / 2.0
      );
      Vec3 point = new Vec3((double)position.getX() + 0.5, (double)position.getY() + 0.5, (double)position.getZ() + 0.5);
      return point.add(offset);
   }

   private boolean rayTrace(Vec3 origin, Vec3 position) {
      Vec3 difference = position.subtract(origin);
      int steps = 10;
      double x = difference.xCoord / (double)steps;
      double y = difference.yCoord / (double)steps;
      double z = difference.zCoord / (double)steps;
      Vec3 point = origin;

      for(int i = 0; i < steps; ++i) {
         BlockPos blockPosition = new BlockPos(point = point.addVector(x, y, z));
         IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(blockPosition);
         if (!(blockState.getBlock() instanceof BlockLiquid) && !(blockState.getBlock() instanceof BlockAir)) {
            AxisAlignedBB boundingBox = blockState.getBlock().getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, blockPosition, blockState);
            if (boundingBox == null) {
               boundingBox = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }

            if (boundingBox.offset((double)blockPosition.getX(), (double)blockPosition.getY(), (double)blockPosition.getZ()).isVecInside(point)) {
               return true;
            }
         }
      }

      return false;
   }

   private Scaffold.BlockData find(Vec3 offset3) {
      double xDiff = mc.thePlayer.posX - mc.thePlayer.prevPosX;
      double zDiff = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
      float expand = (float)Aqua.setmgr.getSetting("ScaffoldExpandlength").getCurrentNumber();
      double x = Aqua.setmgr.getSetting("ScaffoldExpand").isState() ? mc.thePlayer.posX + xDiff * (double)expand : mc.thePlayer.posX;
      double y = !Aqua.setmgr.getSetting("ScaffoldSameY").isState() && !Aqua.setmgr.getSetting("ScaffoldDown").isState()
         ? mc.thePlayer.posY
         : (mc.gameSettings.keyBindForward.pressed ? this.posY : mc.thePlayer.posY);
      double z = Aqua.setmgr.getSetting("ScaffoldExpand").isState() ? mc.thePlayer.posZ + zDiff * (double)expand : mc.thePlayer.posZ;
      EnumFacing[] invert = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
      BlockPos position = new BlockPos(new Vec3(x, y, z).add(offset3)).offset(EnumFacing.DOWN);

      for(EnumFacing facing : EnumFacing.values()) {
         BlockPos offset = position.offset(facing);
         if (!(mc.theWorld.getBlockState(offset).getBlock() instanceof BlockAir)
            && !this.rayTrace(mc.thePlayer.getLook(0.0F), this.getPositionByFace(offset, invert[facing.ordinal()]))) {
            return new Scaffold.BlockData(invert[facing.ordinal()], offset);
         }
      }

      BlockPos[] offsets = new BlockPos[]{
         new BlockPos(-1, 0, 0),
         new BlockPos(1, 0, 0),
         new BlockPos(0, 0, -1),
         new BlockPos(0, 0, 1),
         new BlockPos(0, 0, 2),
         new BlockPos(0, 0, -2),
         new BlockPos(2, 0, 0),
         new BlockPos(-2, 0, 0)
      };

      for(BlockPos offset : offsets) {
         BlockPos offsetPos = position.add(offset.getX(), 0, offset.getZ());
         if (mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir) {
            for(EnumFacing facing : EnumFacing.values()) {
               BlockPos offset2 = offsetPos.offset(facing);
               if (!(mc.theWorld.getBlockState(offset2).getBlock() instanceof BlockAir)
                  && !this.rayTrace(mc.thePlayer.getLook(0.01F), this.getPositionByFace(offset, invert[facing.ordinal()]))) {
                  return new Scaffold.BlockData(invert[facing.ordinal()], offset2);
               }
            }
         }
      }

      return null;
   }

   public int getBlockSlot() {
      for(int i = 0; i < 9; ++i) {
         ItemStack s = mc.thePlayer.inventory.getStackInSlot(i);
         if (s != null && s.getItem() instanceof ItemBlock) {
            Scaffold.BlackList var10000 = this.blackList;
            s.getItem();
            if (var10000.isNotBlackListed(Item.itemRegistry.getNameForObject(s.getItem()).toString())) {
               return i;
            }
         }
      }

      return -1;
   }

   public static float[] mouseSens(float yaw, float pitch) {
      float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
      float f3 = f2 * f2 * f2 * 1.2F;
      yaw -= yaw % f3;
      pitch -= pitch % (f3 * f2);
      return new float[]{yaw, pitch};
   }

   public static float updateRotation(float current, float needed, float speed) {
      float f = MathHelper.wrapAngleTo180_float(needed - current);
      if (f > speed) {
         f = speed;
      }

      if (f < -speed) {
         f = -speed;
      }

      return current + f;
   }

   public static float[] rotationrecode2(Scaffold.BlockData blockData) {
      double x = (double)Scaffold.BlockData.getPos().getX() + 0.5 - mc.thePlayer.posX + (double)Scaffold.BlockData.getFacing().getFrontOffsetX() / 2.0;
      double z = (double)Scaffold.BlockData.getPos().getZ() + 0.5 - mc.thePlayer.posZ + (double)Scaffold.BlockData.getFacing().getFrontOffsetZ() / 2.0;
      double y = (double)Scaffold.BlockData.getPos().getY() + 0.6;
      double ymax = mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight() - y;
      double allmax = (double)MathHelper.sqrt_double(x * x + z * z);
      float yaw = Aqua.setmgr.getSetting("ScaffoldReverseYaw").isState()
         ? (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 270.0F
         : (float)(Math.atan2(z, x) * 180.0 / Math.PI) - (float)Aqua.setmgr.getSetting("ScaffoldYawPosition").getCurrentNumber();
      float pitch = (float)(Math.atan2(ymax, allmax) * 180.0 / Math.PI);
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
      float f3 = f2 * f2 * f2 * 1.2F;
      yaw -= yaw % f3;
      pitch -= pitch % (f3 * f2);
      return new float[]{yaw, MathHelper.clamp_float(pitch, 78.0F, 80.0F)};
   }

   public void rightClickMouse(ItemStack itemstack, int slot) {
      if (!mc.playerController.getIsHittingBlock()) {
         mc.rightClickDelayTimer = 4;

         try {
            switch(mc.objectMouseOver.typeOfHit) {
               case ENTITY:
                  if (!mc.playerController.isPlayerRightClickingOnEntity(mc.thePlayer, mc.objectMouseOver.entityHit, mc.objectMouseOver)
                     && mc.playerController.interactWithEntitySendPacket(mc.thePlayer, mc.objectMouseOver.entityHit)) {
                  }
                  break;
               case BLOCK:
                  BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                  if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                     if (itemstack == null) {
                        boolean var10000 = false;
                     }

                     if (mc.playerController
                        .onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, blockpos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec)) {
                        if (!Aqua.setmgr.getSetting("ScaffoldSwing").isState()) {
                           mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        } else {
                           mc.thePlayer.swingItem();
                        }
                     }

                     if (itemstack == null) {
                        return;
                     }

                     if (itemstack.stackSize == 0) {
                        mc.thePlayer.inventory.mainInventory[slot] = null;
                     }
                  }
            }
         } catch (NullPointerException var5) {
         }
      }
   }

   public static int getBlockCount() {
      int itemCount = 0;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
         if (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemBlock) {
            itemCount += stack.stackSize;
         }
      }

      return itemCount;
   }

   public static boolean isOnGround(double height) {
      return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
   }

   class BlackList {
      List<String> stringids = new ArrayList<>();

      public BlackList() {
         this.addID("minecraft:wooden_slab");
         this.addID("minecraft:stone_slab");
         this.addID("minecraft:banner");
         this.addID("minecraft:beacon");
         this.addID("minecraft:trapped_chest");
         this.addID("minecraft:chest");
         this.addID("minecraft:anvil");
         this.addID("minecraft:enchanting_table");
         this.addID("minecraft:crafting_table");
         this.addID("minecraft:furnace");
         this.addID("minecraft:banner");
         this.addID("minecraft:wall_banner");
         this.addID("minecraft:standing_banner");
         this.addID("minecraft:web");
         this.addID("minecraft:sapling");
      }

      public List<String> getStringids() {
         return this.stringids;
      }

      public boolean isNotBlackListed(String blockID) {
         return !this.stringids.contains(blockID);
      }

      public void addID(String id) {
         this.stringids.add(id);
      }
   }

   public static class BlockData {
      private static EnumFacing facing;
      private static BlockPos pos;

      public BlockData(EnumFacing facing, BlockPos pos) {
         Scaffold.BlockData.facing = facing;
         Scaffold.BlockData.pos = pos;
      }

      public static EnumFacing getFacing() {
         return facing;
      }

      public static BlockPos getPos() {
         return pos;
      }
   }
}
