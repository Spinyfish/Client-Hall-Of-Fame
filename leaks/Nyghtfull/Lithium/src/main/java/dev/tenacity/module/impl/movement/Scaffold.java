package dev.tenacity.module.impl.movement;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.player.input.LegitClickEvent;
import dev.tenacity.event.impl.player.input.MoveInputEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.SafeWalkEvent;
import dev.tenacity.event.impl.player.movement.correction.JumpEvent;
import dev.tenacity.event.impl.player.movement.correction.StrafeEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.FlagfolUtil.Utils.RayCastUtil;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.player.MovementUtils;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.player.ScaffoldUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.server.PacketUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.Random;

public class Scaffold extends Module {

    private final ModeSetting countMode = new ModeSetting("Block Counter", "Tenacity", "None", "Tenacity", "Basic", "Polar");
    private final BooleanSetting rotations = new BooleanSetting("Rotations", true);

    private final BooleanSetting raycast = new BooleanSetting("raycast", true);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", "Watchdog", "Watchdog","None", "NCP","Dev", "Back", "Enum", "Down");
    private final ModeSetting placeType = new ModeSetting("Place Type", "Post", "Pre", "Post", "Legit", "Dynamic");
    public static ModeSetting keepYMode = new ModeSetting("Keep Y Mode", "Always", "Always", "Speed toggled");
    public static ModeSetting sprintMode = new ModeSetting("Sprint Mode", "Vanilla", "Vanilla","Hypixel", "Watchdog", "Cancel");
    public static ModeSetting towerMode = new ModeSetting("Tower Mode", "Vanilla", "Vanilla","Intave","Watchdog", "BlocksMC");
    public static ModeSetting swingMode = new ModeSetting("Swing Mode", "Client", "Client", "Silent");
    public static NumberSetting mindelay = new NumberSetting("Min Delay", 0, 2, 0, 0.05);

    public static NumberSetting maxdelay = new NumberSetting("Max Delay", 0, 2, 0, 0.05);
    public static final BooleanSetting auto3rdPerson = new BooleanSetting("Auto 3rd Person", false);
    public static final BooleanSetting movementCorrection = new BooleanSetting("Movement Correction", true);
    public static final BooleanSetting speedSlowdown = new BooleanSetting("Speed Slowdown", true);
    public static final NumberSetting speedSlowdownAmount = new NumberSetting("Slowdown Amount", 0.1, 0.2, 0.01, 0.01);
    public static final BooleanSetting itemSpoof = new BooleanSetting("Item Spoof", false);
    public static final BooleanSetting downwards = new BooleanSetting("Downwards", false);
    public static final BooleanSetting safewalk = new BooleanSetting("Safewalk", false);
    public static final BooleanSetting sprint = new BooleanSetting("Sprint", false);
    private final BooleanSetting sneak = new BooleanSetting("Sneak", false);
    public static final BooleanSetting tower = new BooleanSetting("Tower", false);
    private final BooleanSetting swing = new BooleanSetting("Swing", true);
    private final BooleanSetting autoJump = new BooleanSetting("Auto Jump", false);
    private final BooleanSetting hideJump = new BooleanSetting("Hide Jump", false);
    private final BooleanSetting baseSpeed = new BooleanSetting("Base Speed", false);
    public static BooleanSetting keepY = new BooleanSetting("Keep Y", false);
    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;
    private float y;
    private final TimerUtil delayTimer = new TimerUtil();
    private final TimerUtil timerUtil = new TimerUtil();
    public static double keepYCoord;
    private boolean pre;
    private int slot;
    private int prevSlot;

    public int onGroundTicks, offGroundTicks;

    private final Animation anim = new DecelerateAnimation(250, 1);

    private float[] cachedRotations = new float[] { 0.0F, 0.0F };
    private float[] lastRotations = new float[] { 0.0F, 0.0F };
    float yaw = 0;

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT, "Automatically places blocks under you");
        this.addSettings(countMode, rotations, rotationMode, placeType, keepYMode, sprintMode, towerMode, swingMode, mindelay,maxdelay,
                auto3rdPerson, movementCorrection, speedSlowdown, speedSlowdownAmount, itemSpoof, downwards, safewalk, sprint, sneak, tower,
                swing, autoJump, hideJump, baseSpeed, keepY);
        rotationMode.addParent(rotations, ParentAttribute.BOOLEAN_CONDITION);
        sprintMode.addParent(sprint, ParentAttribute.BOOLEAN_CONDITION);
        towerMode.addParent(tower, ParentAttribute.BOOLEAN_CONDITION);
        swingMode.addParent(swing, ParentAttribute.BOOLEAN_CONDITION);
        keepYMode.addParent(keepY, ParentAttribute.BOOLEAN_CONDITION);
        hideJump.addParent(autoJump, ParentAttribute.BOOLEAN_CONDITION);
        speedSlowdownAmount.addParent(speedSlowdown, ParentAttribute.BOOLEAN_CONDITION);
    }

    @Override
    public void onJumpEvent(JumpEvent event) {

        if (keepY.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) {
            event.cancel();
        }

        super.onJumpEvent(event);
    }

    @Override
    public void onMotionEvent(MotionEvent event) {

        if (!event.isPost()) {

            if (mc.thePlayer.onGround) {
                this.onGroundTicks += 1;
                this.offGroundTicks = 0;
            } else {
                this.onGroundTicks = 0;
                this.offGroundTicks += 1;
            }

            if (baseSpeed.isEnabled() && mc.thePlayer.onGround) {
                MovementUtils.setSpeed(0.09);
            }

            if (autoJump.isEnabled() && mc.thePlayer.onGround && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.thePlayer.jump();
            }

            if (sprint.isEnabled() && sprintMode.is("Watchdog") && mc.thePlayer.onGround && MovementUtils.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown() && !isDownwards() && mc.thePlayer.isSprinting()) {
                final double[] offset = MathUtils.yawPos(mc.thePlayer.getDirection(), MovementUtils.getSpeed() / 2);
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX - offset[0], mc.thePlayer.posY, mc.thePlayer.posZ - offset[1], true));
            }
            if (sprint.isEnabled() && sprintMode.is("Hypixel") && mc.thePlayer.onGround) {
                mc.gameSettings.keyBindSneak.pressed = true;
               // MovementUtils.strafe(0.27f);
                MovementUtils.strafe(MovementUtils.getBaseMoveSpeed());
            }

            if (rotations.isEnabled()) {
                lastRotations = cachedRotations;

                switch (rotationMode.getMode()) {
                    case "Watchdog":
                        cachedRotations = new float[]{MovementUtils.getMovementDirection(event.getYaw()) - 180, y};
                        break;
                    case "NCP":
                        cachedRotations = new float[] { mc.thePlayer.rotationYaw + mc.thePlayer.movementInput.moveForward < 0.0f ? 0 : 180, y};
                        break;
                    case "Back":
                        cachedRotations = new float[] { MovementUtils.getMovementDirection(event.getYaw()) - 180, 78 };
                        break;
                    case "Down":
                        cachedRotations = new float[] { mc.thePlayer.rotationYaw - 180, 90 };
                        break;
                    case "Enum":
                        if (lastBlockCache != null) {
                            cachedRotations = new float[] { RotationUtils.getEnumRotations(lastBlockCache.getFacing()),y};
                            //77
                        } else {
                            cachedRotations = new float[] { mc.thePlayer.rotationYaw - 180, y };
                        }
                        break;
                }

                float[] fixedRotations = RotationUtils.getFixedRotations(cachedRotations, lastRotations);
                if(!rotationMode.is("none")) {
                    event.setRotations(fixedRotations[0], fixedRotations[1]);

                    RotationUtils.setVisualRotations(event);
                }

                yaw = event.getYaw();
            }

            if (speedSlowdown.isEnabled() && mc.thePlayer.isPotionActive(Potion.moveSpeed) && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.onGround) {
                MovementUtils.setSpeed(speedSlowdownAmount.getValue());
            }

            if (sneak.isEnabled()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            }

            if (mc.thePlayer.onGround) {
                keepYCoord = Math.floor(mc.thePlayer.posY - 1.0);
            }

            if (tower.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) {
                double centerX = Math.floor(event.getX()) + 0.5, centerZ = Math.floor(event.getZ()) + 0.5;

                switch (towerMode.getMode()) {
                    case "Vanilla":
                        mc.thePlayer.motionY = 0.60f;
                        break;

                    case"Intave":
                        if(mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.37f;
                        }
                        break;

                }
            }

            // Setting Block Cache
            blockCache = ScaffoldUtils.getBlockInfo();
            if (blockCache != null) {
                lastBlockCache = ScaffoldUtils.getBlockInfo();
            } else {
                return;
            }

            if (mc.thePlayer.ticksExisted % 4 == 0) {
                pre = true;
            }

            // Placing Blocks (Pre)
            if (placeType.is("Pre") || (placeType.is("Dynamic") && pre)) {
                if (place()) {
                    pre = false;
                }
            }
        } else {
            // Setting Item Slot
            if (!itemSpoof.isEnabled()) {
                mc.thePlayer.inventory.currentItem = slot;
            }

            // Placing Blocks (Post)
            if (placeType.is("Post") || (placeType.is("Dynamic") && !pre)) {
                place();
            }

            if (tower.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown()) {
                double centerX = Math.floor(event.getX()) + 0.5, centerZ = Math.floor(event.getZ()) + 0.5;

                switch (towerMode.getMode()) {
                    case "BlocksMC":
                        if (mc.thePlayer.posY % 1.0 <= 0.00153598) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                            mc.thePlayer.motionY = 0.41998;
                            break;
                        }

                        if (mc.thePlayer.posY % 1 > 0.1 || this.offGroundTicks == 0) {
                            break;
                        }

                        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                        break;

                    case"Watchdog":
                        if (!mc.gameSettings.keyBindJump.isKeyDown()) return;


                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.41999998688697815F;
                            mc.thePlayer.motionX *= .65;
                            mc.thePlayer.motionZ *= .65;
                        }
                        break;
                }
            }

            pre = false;
        }
    }


    private boolean place() {

        if (keepY.isEnabled() && !mc.thePlayer.isAirBorne) {
            return false;
        }

        int slot = ScaffoldUtils.getBlockSlot();
        if (blockCache == null || lastBlockCache == null || slot == -1) return false;

        if (this.slot != slot) {
            this.slot = slot;
            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(this.slot));
        }

        boolean placed = false;
        if (delayTimer.hasTimeElapsed(MathHelper.getRandomDoubleInRange(new Random(),mindelay.getValue() * 1000,maxdelay.getValue() * 1000))) {
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(this.slot),
                    lastBlockCache.getPosition(), lastBlockCache.getFacing(),
                    ScaffoldUtils.getHypixelVec3(blockCache))) {
                placed = true;
                //ChatUtil.print(MathHelper.getRandomDoubleInRange(new Random(),mindelay.getValue() * 1000,maxdelay.getValue() * 1000));
                y = MathUtils.getRandomInRange(75.5f, 83.5f);
                if (swing.isEnabled()) {
                    if (swingMode.is("Client")) {
                        mc.thePlayer.swingItem();
                    } else {
                        PacketUtils.sendPacket(new C0APacketAnimation());
                    }
                }
                delayTimer.reset();
                blockCache = null;
            }
            mc.rightClickDelayTimer = 0;
        }
        return placed;
    }

    @Override
    public void onLegitClickEvent(LegitClickEvent event) {
        if (placeType.is("Legit")) {
            place();
        }
    }

    @Override
    public void onTickEvent(TickEvent event) {
        if (mc.thePlayer == null) return;
        if (hideJump.isEnabled() && !mc.gameSettings.keyBindJump.isKeyDown() && MovementUtils.isMoving() && !mc.thePlayer.onGround && autoJump.isEnabled()) {
            mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.1F;
        }
        if (downwards.isEnabled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            mc.thePlayer.movementInput.sneak = false;
        }
    }

    @Override
    public void onMoveInputEvent(MoveInputEvent event) {
        if (movementCorrection.isEnabled()) {
            MovementUtils.fixMovement(event, yaw);
        }
    }

    @Override
    public void onStrafeEvent(StrafeEvent event) {
        if (movementCorrection.isEnabled()) {
            event.setYaw(yaw);
        }
    }

    @Override
    public void onJumpFixEvent(JumpEvent event) {
        if (movementCorrection.isEnabled()) {
            event.setYaw(yaw);
        }
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer != null) {
            if (!itemSpoof.isEnabled()) mc.thePlayer.inventory.currentItem = prevSlot;
            if (slot != mc.thePlayer.inventory.currentItem && itemSpoof.isEnabled())
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

            if (auto3rdPerson.isEnabled()) {
                mc.gameSettings.thirdPersonView = 0;
            }
            if (mc.thePlayer.isSneaking() && sneak.isEnabled())
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(mc.gameSettings.keyBindSneak));
        }
        if(sprint.isEnabled() && sprintMode.is("Hypixel")) {
            mc.gameSettings.keyBindSneak.pressed = false;
        }
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        lastBlockCache = null;
        if (mc.thePlayer != null) {
            prevSlot = mc.thePlayer.inventory.currentItem;
            slot = mc.thePlayer.inventory.currentItem;
            if (mc.thePlayer.isSprinting() && sprint.isEnabled() && sprintMode.is("Cancel")) {
                PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            if (auto3rdPerson.isEnabled()) {
                mc.gameSettings.thirdPersonView = 1;
            }
        }
        timerUtil.reset();
        y = 80;

        lastRotations = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };

        super.onEnable();
    }

    public void renderCounterBlur() {
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        IFontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color;
        float x, y;
        String str = countStr + " block" + (count != 1 ? "s" : "");
        float output = anim.getOutput().floatValue();
        switch (countMode.getMode()) {
            case "Tenacity":
                float blockWH = heldItem != null ? 15 : -2;
                int spacing = 3;
                String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
                float textWidth = lithiumFont18.getStringWidth(text);

                float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
                x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
                float height = 20;
                RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 3);

                RoundedUtil.drawRound(x, y, totalWidth, height, 5, Color.BLACK);
                RenderUtil.scissorEnd();
                break;
            case "Basic":
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(str) / 2F + 1;
                y = sr.getScaledHeight() / 2F + 10;
                RenderUtil.scaleStart(sr.getScaledWidth() / 2.0F, y + fr.FONT_HEIGHT / 2.0F, output);
                fr.drawStringWithShadow(str, x, y, 0x000000);
                RenderUtil.scaleEnd();
                break;

        }
    }


    public void renderCounter() {
        anim.setDirection(enabled ? Direction.FORWARDS : Direction.BACKWARDS);
        if (!enabled && anim.isDone()) return;
        int slot = ScaffoldUtils.getBlockSlot();
        ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
        int count = slot == -1 ? 0 : ScaffoldUtils.getBlockCount();
        String countStr = String.valueOf(count);
        IFontRenderer fr = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);
        int color;
        float x, y;
        String str = countStr + " block" + (count != 1 ? "s" : "");
        float output = anim.getOutput().floatValue();
        switch (countMode.getMode()) {
            case "Tenacity":
                float blockWH = heldItem != null ? 15 : -2;
                int spacing = 3;
                String text = "§l" + countStr + "§r block" + (count != 1 ? "s" : "");
                float textWidth = lithiumFont18.getStringWidth(text);

                float totalWidth = ((textWidth + blockWH + spacing) + 6) * output;
                x = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                y = sr.getScaledHeight() - (sr.getScaledHeight() / 2f - 20);
                float height = 20;
                RenderUtil.scissorStart(x - 1.5, y - 1.5, totalWidth + 3, height + 3);

             //   RoundedUtil.drawRound(x, y, totalWidth, height, 5, ColorUtil.tripleColor(20, .45f));

                lithiumFont18.drawString(text, x + 3 + blockWH + spacing, y + lithiumFont18.getMiddleOfBox(height) + .5f, -1);

                if (heldItem != null) {
                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, (int) x + 3, (int) (y + 10 - (blockWH / 2)));
                    RenderHelper.disableStandardItemLighting();
                }
                RenderUtil.scissorEnd();
                break;
            case "Basic":
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(str) / 2F + 1;
                y = sr.getScaledHeight() / 2F + 10;
                RenderUtil.scaleStart(sr.getScaledWidth() / 2.0F, y + fr.FONT_HEIGHT / 2.0F, output);
                fr.drawStringWithShadow(str, x, y, -1);
                RenderUtil.scaleEnd();
                break;
            case "Polar":
                color = count < 24 ? 0xFFFF5555 : count < 128 ? 0xFFFFFF55 : 0xFF55FF55;
                x = sr.getScaledWidth() / 2F - fr.getStringWidth(countStr) / 2F + (heldItem != null ? 6 : 1);
                y = sr.getScaledHeight() / 2F + 10;


                fr.drawOutlinedString(countStr, x, y, ColorUtil.applyOpacity(color, output), true);



                break;
        }
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent e) {
        switch (towerMode.getMode()) {
            case"Watchdog":
                final Packet<?> packet = e.getPacket();

                if (mc.thePlayer.motionY > -0.0784000015258789 && !mc.thePlayer.isPotionActive(Potion.jump) && packet instanceof C08PacketPlayerBlockPlacement) {
                    final C08PacketPlayerBlockPlacement wrapper = ((C08PacketPlayerBlockPlacement) packet);

                    if (wrapper.getPosition().equals(new BlockPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.4, mc.thePlayer.posZ))) {
                        mc.thePlayer.motionY = -0.0784000015258789;
                    }
                }


                break;
        }
        if (e.getPacket() instanceof C0BPacketEntityAction && sprint.isEnabled() && sprintMode.is("Cancel")) {
            e.cancel();
        }
        if (e.getPacket() instanceof C09PacketHeldItemChange && itemSpoof.isEnabled()) {
            e.cancel();
        }
    }

    @Override
    public void onSafeWalkEvent(SafeWalkEvent event) {
        if ((safewalk.isEnabled() && !isDownwards()) || ScaffoldUtils.getBlockCount() == 0) {
           event.setSafe(true);
        }
    }

    public static boolean isDownwards() {
        return downwards.isEnabled() && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak);
    }

}
