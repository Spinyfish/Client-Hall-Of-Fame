package me.jinthium.straight.impl.modules.player;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.game.SpoofItemEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.event.world.BlockPlaceableEvent;
import me.jinthium.straight.impl.event.world.SafeWalkEvent;
import me.jinthium.straight.impl.modules.movement.Speed;
import me.jinthium.straight.impl.modules.visual.Hud;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.MultiBoolSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.animation.impl.SmoothStepAnimation;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.BlockUtils;
import me.jinthium.straight.impl.utils.player.InventoryUtils;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import me.jinthium.straight.impl.utils.vector.Vector3d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockSnow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import org.lwjglx.input.Keyboard;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.opengl.GL11.*;

public class Scaffold extends Module {
    private static final EnumFacing[] FACINGS = new EnumFacing[]{
            EnumFacing.EAST,
            EnumFacing.WEST,
            EnumFacing.SOUTH,
            EnumFacing.NORTH};

    private final ConcurrentLinkedQueue<Packet<?>> yaBoiC08Queue = new ConcurrentLinkedQueue<>();
    private final TimerUtil placeTimer = new TimerUtil(), timer = new TimerUtil();
    public int slowTicks;

    private final Animation animation = new SmoothStepAnimation(250, 1);

    // Tower
    private final BooleanSetting towerProperty = new BooleanSetting("Tower", true);
    // Placement
    private final BooleanSetting spoofHeldItemProperty = new BooleanSetting("Spoof Held Item", true);
    private final ModeSetting eventPlaceInEnumSetting = new ModeSetting("Place In", "Pre", "Update", "Pre", "Post");
    private final ModeSetting hitVecModeEnumSetting = new ModeSetting("HitVec", "Custom", "Custom", "Block Pos",
            "Randomized", "Raytrace", "Old Random");
    private final ModeSetting rotationModeEnumSetting = new ModeSetting("Rotations", "Normal", "Off", "Normal", "Always Backwards", "Move Dir",
            "Forwards", "Custom", "Invalid Pitch");
    private final ModeSetting invalidPitchYawMode = new ModeSetting("Yaw Mode", "Move Dir", "Off", "Normal", "Move Dir", "Custom");
    private final MultiBoolSetting renders = new MultiBoolSetting("Renders",
            new BooleanSetting("Block Counter", true),
            new BooleanSetting("Direction Arrow", true));
    private final NumberSetting invalidPitch = new NumberSetting("IPitch Rot", 92, -360, 360, 0.5f);
    private final NumberSetting yawRot = new NumberSetting("Custom Yaw", 180, -360, 360, 0.1);
    private final NumberSetting pitchRot = new NumberSetting("Custom Pitch", 45, -90, 90, 0.1);
    private final NumberSetting blocksToSneak = new NumberSetting("Blocks To Sneak", 0, 0, 50, 1);
    private final NumberSetting distFromEdge = new NumberSetting("Dist from edge", 0.25, 0.01, 0.99, 0.00005);
    private final NumberSetting placeDelayProperty = new NumberSetting("Place Delay", 0, 0, 5000, 1);
    private final BooleanSetting daVulcanBypassO = new BooleanSetting("SneakPacket Bypass", false);
    private final NumberSetting expandProperty = new NumberSetting("Expand", 0, 0, 10.0, 0.01);
    private final ModeSetting swingProperty = new ModeSetting("Swing", "Client", "Client", "Silent", "No Swing");
    // Movement
    public final BooleanSetting autoJumpProperty = new BooleanSetting("Auto Jump", false);
    private final BooleanSetting safeWalkProperty = new BooleanSetting("Safe Walk", false);
    private final BooleanSetting keepPosProperty = new BooleanSetting("Keep Y", true);
    public final BooleanSetting noSprintProperty = new BooleanSetting("No Sprint", true);

    // For drawing only...
    private final List<Vec3> breadcrumbs = new ArrayList<>();
    private double fadeInOutProgress;
    private int totalBlockCount;
    // Counters
    private int ticksSincePlace;
    // Block data
    private BlockData data;
    private BlockData lastPlacement;
    public float[] angles;
    // Tower
    public boolean towering, towerBoost;
    private int placedBlocks;
    private int sneakedBlocks;
    // Other...
    private Speed speed;
    private int bestBlockStack, stage;
    private double startPosY, moveSpeed;

    public Scaffold() {
        super("Scaffold", Category.PLAYER);
        this.distFromEdge.addParent(blocksToSneak, r -> blocksToSneak.getValue() > 0);
        yawRot.addParent(rotationModeEnumSetting, r -> rotationModeEnumSetting.is("Custom") || (rotationModeEnumSetting.is("Invalid Pitch") && invalidPitchYawMode.is("Custom")));
        pitchRot.addParent(rotationModeEnumSetting, r -> rotationModeEnumSetting.is("Custom"));
        invalidPitchYawMode.addParent(rotationModeEnumSetting, r -> rotationModeEnumSetting.is("Invalid Pitch"));
        invalidPitch.addParent(rotationModeEnumSetting, r -> rotationModeEnumSetting.is("Invalid Pitch"));

        this.addSettings(towerProperty, spoofHeldItemProperty, eventPlaceInEnumSetting,
                hitVecModeEnumSetting, rotationModeEnumSetting, renders, invalidPitchYawMode, invalidPitch, yawRot, pitchRot, placeDelayProperty, daVulcanBypassO,
                expandProperty, swingProperty, autoJumpProperty, safeWalkProperty, keepPosProperty, noSprintProperty);
    }


    @Callback
    final EventCallback<SpoofItemEvent> spoofItemEventEventCallback = event -> {
        if (spoofHeldItemProperty.isEnabled() && bestBlockStack != -1 && bestBlockStack >= 36)
            event.setCurrentItem(bestBlockStack - InventoryUtils.ONLY_HOT_BAR_BEGIN);
    };

    @Callback
    final EventCallback<PlayerMoveEvent> playerMoveEventEventCallback = event -> {
        if (!noSprintProperty.isEnabled()) {
            if (mc.thePlayer.onGround)
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
                    MovementUtil.setSpeed(event, 5.61 / 20);
                else
                    MovementUtil.setSpeed(event, mc.thePlayer.ticksExisted % 3 == 0 ? 5.95 / 20 : 4.1 / 20);
        }
    };

    @Callback
    final EventCallback<BlockPlaceableEvent> blockPlaceableEventEventCallback = event -> {
        ticksSincePlace = 0;
    };

    @Callback
    final EventCallback<SafeWalkEvent> safeWalkEventEventCallback = event -> {
        if (safeWalkProperty.isEnabled())
            event.setSafe(true);
    };

    @Callback
    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
        if (!towering || !mc.thePlayer.onGround || !mc.thePlayer.isMoving())
            return;

        mc.thePlayer.motionY = MovementUtil.getJumpHeight(0.42F);
//        mc.thePlayer.motionX *= .65;
//        mc.thePlayer.motionZ *= .65;
    };

    @Callback
    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
        if (event.getPacketState() == PacketEvent.PacketState.SENDING && towerProperty.isEnabled()) {
            if (mc.thePlayer.motionY > -0.0784000015258789 && !mc.thePlayer.isPotionActive(Potion.jump) && event.getPacket() instanceof C08PacketPlayerBlockPlacement wrapper && mc.thePlayer.isMoving()) {
                if (wrapper.getPosition().equals(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.4, mc.thePlayer.posZ))) {
                    mc.thePlayer.motionY = -0.0784000015258789;
                }
            }
        }
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {

        switch (event.getEventState()) {
            case PRE -> {
                if (mc.thePlayer.isMoving()) {
                    if (noSprintProperty.isEnabled()) {
                        if (mc.thePlayer.onGround) {
                            MovementUtil.strafe(MovementUtil.getSpeed() * (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.97 : 0.98));
                        }
//                    mc.thePlayer.motionX *= 0.973 - Math.random() / 500;
//                    mc.thePlayer.motionZ *= 0.973 - Math.random() / 500;
                    } else {

//                    mc.thePlayer.motionX *= 0.91;
//                    mc.thePlayer.motionZ *= 0.91;
                    }
                }

                if (autoJumpProperty.isEnabled()) {
                    if (mc.thePlayer.onGround && mc.thePlayer.isMoving()) {
                        double speed = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
                        boolean boost = mc.thePlayer.isPotionActive(1);
                        switch (stage) {
                            case 1 -> {
                                moveSpeed = 0.42f;
                                speed = boost ? speed + 0.2 : 0.48;
                                event.setOnGround(true);
                            }
                            case 2 -> {
                                speed = boost ? speed * 0.71 : 0.19;
                                moveSpeed -= 0.0784f;
                                event.setOnGround(false);
                            }
                            default -> {
                                stage = 0;
                                speed /= boost ? 0.64 : 0.66;
                                event.setOnGround(true);
                            }
                        }
                        MovementUtil.strafe(speed);
                        stage++;
                        event.setPosY(event.getPosY() + moveSpeed);
                    } else {
                        stage = 0;
                    }
                }

                // Increment tick counters
                ticksSincePlace++;

                // Invalidate old data
                data = null;

                if (noSprintProperty.isEnabled() || autoJumpProperty.isEnabled()) {
                    mc.thePlayer.setSprinting(false);
                }

                // Update towering state
                towering = towerProperty.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown();

                // Look for best block stack in hot bar
                bestBlockStack = getBestBlockStack();

                calculateTotalBlockCount();

//            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && eventPlaceInEnumSetting.isEnabled() == EventPlaceIn.Pre && mc.thePlayer.onGround) {
//                PacketUtil.sendPacketNoEvent(new C02PacketUseEntity(-1, C02PacketUseEntity.Action.ATTACK));
//                mc.thePlayer.motionY = 0.48F;
//                mc.gameSettings.keyBindJump.pressed = false;
//            }

//            if (towering) {
//                if(MovementUtil.isMoving())
//                    mc.timer.timerSpeed = 1.0f;
//                else
//                    mc.timer.timerSpeed = 1.7f;
//
//                mc.thePlayer.motionY = 0.41998F - MovementUtil.getRandomHypixelValuesFloat() * 3;
//
//                if (MovementUtil.isMoving()) {
//                    mc.thePlayer.motionX *= 0.7;
//                    mc.thePlayer.motionZ *= 0.7;
//                }
//
////                event.setPosX(event.getPosX() + (mc.thePlayer.ticksExisted % 2 != 0 ? -MovementUtil.getRandomHypixelValues() * 2 : MovementUtil.getRandomHypixelValues() * 2));
////                event.setPosZ(event.getPosZ() + (mc.thePlayer.ticksExisted % 2 != 0 ? -MovementUtil.getRandomHypixelValues() * 2 : MovementUtil.getRandomHypixelValues() * 2));
//            }

//            if(mc.thePlayer.onGround)
//                slowTicks = 0;
//            else
//                slowTicks++;

                // If best block stack is in hot bar
                if (bestBlockStack >= InventoryUtils.ONLY_HOT_BAR_BEGIN) {
                    final BlockPos blockUnder = getBlockUnder();
                    data = getBlockData(blockUnder);

                    if (data == null) data = getBlockData(blockUnder.offset(EnumFacing.DOWN));

                    // Set rotations to persistent rotations
                    if (!rotationModeEnumSetting.is("Off")) {
                        if (angles == null || lastPlacement == null)
                            angles = new float[]{MovementUtil.getMovementDirection1() - 180, 84.f};

                        calcRots(event, data);
                    }

                    if (!isOnEdge() && safeWalkProperty.isEnabled() && expandProperty.getValue().floatValue() <= 0) {
                        placeTimer.reset();
                    }

                    double[] dist = MovementUtil.getXZ(distFromEdge.getValue().floatValue());
                    boolean onEdge = BlockUtils.getBlock(mc.thePlayer.posX + dist[0], mc.thePlayer.posY - 0.5, mc.thePlayer.posZ + dist[1]) instanceof BlockAir;

                    if (onEdge && blocksToSneak.getValue() > 0 && sneakedBlocks < blocksToSneak.getValue()) {
                        mc.gameSettings.keyBindSneak.pressed = true;
                    } else if (!onEdge) {
                        mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode());
                    }

                    if (eventPlaceInEnumSetting.is("Pre"))
                        doPlace(event);
                }
            }
            case POST -> {
                if (eventPlaceInEnumSetting.is("Post"))
                    doPlace(event);
            }
            case UPDATE -> {
                if (eventPlaceInEnumSetting.is("Update"))
                    doPlace(event);
            }
        }
    };

    public boolean isToweringOnHyp() {
        return towering;
    }

    private boolean isOnEdge() {
        final WorldClient world = mc.theWorld;
        final EntityPlayerSP player = mc.thePlayer;
        double[] gars = new double[]{0, 0.1, -0.1};
        for (double x : gars) {
            for (double z : gars) {
                final BlockPos belowBlockPos = new BlockPos(player.posX + x, getBlockUnder().getY(), player.posZ + z);
                if (!(world.getBlockState(belowBlockPos).getBlock() instanceof BlockAir))
                    return false;
            }
        }
        return true;
    }

    public void calcRots(PlayerUpdateEvent event, BlockData blockData) {
        if (blockData == null || blockData.pos == null || blockData.face == null || event == null) {
            angles = new float[]{MovementUtil.getMovementDirection1() - 180, 84.f};
            return;
        }

        switch (rotationModeEnumSetting.getMode()) {
            case "Normal" -> {
                if (!RotationUtils.overBlock(RotationUtils.getCurrentRotationsv(), blockData.face, blockData.pos, false)) {
                    getRotations(blockData, 0);
                }
                angles[1] = 84.f;
            }
            case "Invalid Pitch" -> {
                switch (invalidPitchYawMode.getMode()) {
                    case "Normal" -> {
                        if (!RotationUtils.overBlock(RotationUtils.getCurrentRotationsv(), blockData.face, blockData.pos, false)) {
                            getRotations(blockData, 0);
                        }
                    }
                    case "Move Dir" -> angles[0] = MovementUtil.getMovementDirection1() - 180;
                    case "Custom" -> angles[0] = yawRot.getValue().floatValue();
                    case "Off" -> angles[0] = mc.thePlayer.rotationYaw;
                }
                angles[1] = invalidPitch.getValue().floatValue();
            }
            case "Always Backwards" -> angles = new float[]{MovementUtil.getMovementDirection1() - 180, 0};
            case "Move Dir" -> angles = new float[]{MovementUtil.getMovementDirection1() - 180.f, 84.f};
            case "Custom" -> angles = new float[]{yawRot.getValue().floatValue(), pitchRot.getValue().floatValue()};
            case "Forwards" -> angles = new float[]{0, 84.f};
        }

        if (rotationModeEnumSetting.is("Invalid Pitch"))
            RotationUtils.setRotationsNoLimitsf(event, angles, 100);
        else
            RotationUtils.setRotations(event, angles, 100);
    }

    public void getRotations(BlockData blockData, final float yawOffset) {
        boolean found = false;

        if (blockData == null || blockData.pos == null || blockData.face == null) {
            angles = new float[]{MovementUtil.getMovementDirection1() - 180.f, 84.f};
            return;
        }

        for (float possibleYaw = mc.thePlayer.rotationYaw - 180 + yawOffset; possibleYaw <= mc.thePlayer.rotationYaw + 360 - 180 && !found; possibleYaw += 45) {
            for (float possiblePitch = 90; possiblePitch > 30 && !found; possiblePitch -= possiblePitch > (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 60 : 80) ? 1 : 10) {
                if (RotationUtils.overBlock(new Vector2f(possibleYaw, possiblePitch), blockData.face, blockData.pos, true)) {
                    angles = new float[]{possibleYaw, possiblePitch};
                    found = true;
                }
            }
        }

        if (!found) {
            final Vector2f rotations = RotationUtils.calculate(
                    new Vector3d(blockData.pos.getX(), blockData.pos.getY(), blockData.pos.getZ()), blockData.face);

            angles = new float[]{rotations.x, rotations.y};
        }
    }

    public double[] getExpandCords(double y) {
        BlockPos underPos = new BlockPos(mc.thePlayer.posX, y, mc.thePlayer.posZ);
        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        MovementInput movementInput = mc.thePlayer.movementInput;
        float forward = movementInput.moveForward, strafe = movementInput.moveStrafe, yaw = mc.thePlayer.rotationYaw;
        double xCalc = -999, zCalc = -999, dist = 0, expandDist = expandProperty.getValue();

        while (!isAirBlock(underBlock)) {
            xCalc = mc.thePlayer.posX;
            zCalc = mc.thePlayer.posZ;
            dist++;
            if (dist > expandDist) dist = expandDist;
            xCalc += (forward * 0.45 * MathHelper.cos((float) Math.toRadians(yaw + 90.0f)) + strafe * 0.45 * MathHelper.sin((float) Math.toRadians(yaw + 90.0f))) * dist;
            zCalc += (forward * 0.45 * MathHelper.sin((float) Math.toRadians(yaw + 90.0f)) - strafe * 0.45 * MathHelper.cos((float) Math.toRadians(yaw + 90.0f))) * dist;
            if (dist == expandDist) break;
            underPos = new BlockPos(xCalc, y, zCalc);
            underBlock = mc.theWorld.getBlockState(underPos).getBlock();
        }

        return new double[]{xCalc, zCalc};
    }

    public boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            return !(block instanceof BlockSnow) || !(block.getBlockBoundsMaxY() > 0.125);
        }

        return false;
    }


    private void doPlace(final PlayerUpdateEvent event) {
        if (bestBlockStack < 36 || data == null || !placeTimer.hasTimeElapsed(placeDelayProperty.getValue().longValue()) && placeDelayProperty.getValue() > 0)
            return;

        final Vec3 hitVec = getHitVec(event, data);

        final ItemStack heldItem;

        if (spoofHeldItemProperty.isEnabled()) {
            heldItem = mc.thePlayer.inventoryContainer.getSlot(bestBlockStack).getStack();
        } else {
            // Switch item client side
            mc.thePlayer.inventory.currentItem = bestBlockStack - InventoryUtils.ONLY_HOT_BAR_BEGIN;
            heldItem = mc.thePlayer.getCurrentEquippedItem();
        }

        if (heldItem == null || hitVec == null) return;


        // Attempt place using ray trace hit vec
//        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, angles[0], angles[1],  / 64)));
        // PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(null));
//        if(MovementUtil.isOnGround(1/ 64)) {
//            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, angles[0], angles[1],  true));
//        }
        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, heldItem, data.pos, data.face, hitVec)) {
//            if(event.isPre())
//                Logger.print("pre");
//            else
//                Logger.print("post");
            lastPlacement = data;
            if (blocksToSneak.getValue() > 0)
                sneakedBlocks++;

            placeTimer.reset();
            placedBlocks++;
            slowTicks = 3;

            switch (swingProperty.getMode()) {
                case "Client" -> mc.thePlayer.swingItem();
                case "Silent" -> PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
            }

            if (daVulcanBypassO.isEnabled()) {
                if (placedBlocks % 6 == 0) {
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    if (ticksSincePlace > 0) {
                        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                    }
                }
//                if(towering)
//                    mc.thePlayer.motionY = -1;
            }
        }
    }


    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        final ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        final float mx = sr.getScaledWidth() / 2.0f;
        final float my = sr.getScaledHeight() / 2.0f;
        for (BooleanSetting setting : renders.getBoolSettings()) {
            if (setting.isEnabled()) {
                switch (setting.getName()) {
                    case "Block Counter" -> {

                        Gui.drawRect(0, 0, 0, 0, 0);
                        final EntityPlayerSP player = mc.thePlayer;

                        // Block counter
                        if (bestBlockStack != -1) {
                            //GL11.glPushMatrix();
                            // Get the "best" block itemStack from the inventory (computed every tick)
                            final ItemStack stack = player.inventoryContainer.getSlot(bestBlockStack).getStack();
                            // Check the stack in slot has not changed since last update
                            if (stack != null) {
                                if (fadeInOutProgress < 1.0)
                                    fadeInOutProgress += 1.0 / Minecraft.getDebugFPS() * 2;

                                final String blockCount = String.format(totalBlockCount == 1 ? "1 block" : "%s blocks", totalBlockCount);
                                final FontRenderer fontRenderer = mc.fontRendererObj;

                                final double width = 40 + (int) (double) fontRenderer.getStringWidth(blockCount) / 2.7;
                                final double height = 40 + (int) (double) fontRenderer.getStringWidth(blockCount) / 2.7;

                                final double left = mx - width / 2.0;
                                final double top = my + 20 + 10; // middle + arrow spacing + size

                                // Background
                                //RoundedUtil.drawRound((float) left, (float) top, (float) width, (float) height, 8, true, new Color(30, 30, 30, 120));


                                final int itemStackSize = 16;

                                final int textWidth = itemStackSize + 2 + (int) (double) fontRenderer.getStringWidth(blockCount);

                                final int iconRenderPosX = (int) (left + width / 2 - textWidth / 2);

                                final int iconRenderPosY = (int) (top + (height - itemStackSize) / 2);

                                // Setup for item render with proper lighting

                                GlStateManager.enableRescaleNormal();
                                RenderHelper.enableGUIStandardItemLighting();
                                RoundedUtil.drawRound((float) iconRenderPosX, (float) top + (float) (height / 3),
                                        (float) width, (float) height / 1.7f, 8, new Color(0, 0, 0, 100));
                                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (int) (iconRenderPosX + width / 2 - 8), iconRenderPosY + 3);
                                normalFont19.drawCenteredString(blockCount, (float) (iconRenderPosX + width / 2) - 1,
                                        (float) (top + height - normalFont19.getHeight() - 5),
                                        -1);
                                RenderHelper.disableStandardItemLighting();
                                GlStateManager.disableRescaleNormal();
                            }
                        }
                    }
                    case "Direction Arrow" -> {
                        final float partialTicks = event.getPartialTicks();

                        final float client = RenderUtil.interpolate(mc.thePlayer.prevRotationYaw, mc.thePlayer.rotationYaw, partialTicks);
                        final float server = RenderUtil.interpolate(mc.thePlayer.getPreviousRotation().x, mc.thePlayer.currentEvent.getYaw(), partialTicks);

                        final float rotation = server - client;

                        glDisable(GL_TEXTURE_2D);
                        final boolean restore = RenderUtil.glEnableBlend();

                        glEnable(GL_POLYGON_SMOOTH);
                        glEnable(GL_LINE_SMOOTH);

                        glLineWidth(1);

                        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
                        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

                        glPushMatrix();
                        glTranslatef(mx, my, 0);

                        glRotatef(rotation, 0, 0, 1);
                        glTranslatef(0, -20, 0);

                        // Draw Outline

                        RenderUtil.color(0xFFFFFFFF);

                        glBegin(GL_LINE_LOOP);
                        addTriangleVertices();
                        glEnd();

                        // Draw Arrow

                        glEnable(GL_POLYGON_SMOOTH);
                        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

                        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
                        RenderUtil.color(ColorUtil.applyOpacity(hud.getHudColor((float) System.currentTimeMillis() / -600), 0.7f).getRGB());

                        glBegin(GL_TRIANGLES);
                        addTriangleVertices();
                        glEnd();

                        glPopMatrix();

                        glDisable(GL_POLYGON_SMOOTH);
                        glDisable(GL_LINE_SMOOTH);

                        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
                        glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);

                        RenderUtil.glRestoreBlend(restore);
                        glEnable(GL_TEXTURE_2D);
                    }
                }
            }
        }
    };

    private static void addTriangleVertices() {
        glVertex2d(0, -(double) 10 / 2);
        glVertex2d(-(double) 10 / 2, (double) 10 / 2);
        glVertex2d((double) 10 / 2, (double) 10 / 2);
    }

    @Override
    public void onEnable() {
        lastPlacement = null;
        animation.reset();
        yaBoiC08Queue.clear();
        towering = false;
        slowTicks = 3;
        placeTimer.reset();
        sneakedBlocks = 0;
        stage = 0;
        placedBlocks = 0;
//        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
//        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.te]));
        if (speed == null) {
            speed = Client.INSTANCE.getModuleManager().getModule(Speed.class);
        }
        if (autoJumpProperty.isEnabled()) {
            if (speed.isEnabled()) {
                speed.toggle();
            }
            // Autojump
        }
        if (mc.thePlayer != null) startPosY = mc.thePlayer.posY;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        angles = null;
        breadcrumbs.clear();
//        EntityPlayer.enableCameraYOffset = false;
        PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        towering = false;
//        EntityPlayer.cameraYPosition = mc.thePlayer.posY;
        super.onDisable();
    }

    private BlockData getBlockData(final BlockPos pos) {
        final EnumFacing[] facings = FACINGS;

        // 1 of the 4 directions around player
        for (EnumFacing facing : facings) {
            final BlockPos blockPos = pos.add(facing.getOpposite().getDirectionVec());
            if (InventoryUtils.validateBlock(mc.theWorld.getBlockState(blockPos).getBlock(), InventoryUtils.BlockAction.PLACE_ON)) {
                final BlockData data = new BlockData(blockPos, facing);
                if (validateBlockRange(data))
                    return data;
            }
        }

        // 2 Blocks Under e.g. When jumping
        final BlockPos posBelow = pos.add(0, -1, 0);
        if (InventoryUtils.validateBlock(mc.theWorld.getBlockState(posBelow).getBlock(), InventoryUtils.BlockAction.PLACE_ON)) {
            final BlockData data = new BlockData(posBelow, EnumFacing.UP);
            if (validateBlockRange(data))
                return data;
        }

        // 2 Block extension & diagonal
        for (EnumFacing facing : facings) {
            final BlockPos blockPos = pos.add(facing.getOpposite().getDirectionVec());
            for (EnumFacing facing1 : facings) {
                final BlockPos blockPos1 = blockPos.add(facing1.getOpposite().getDirectionVec());
                if (InventoryUtils.validateBlock(mc.theWorld.getBlockState(blockPos1).getBlock(), InventoryUtils.BlockAction.PLACE_ON)) {
                    final BlockData data = new BlockData(blockPos1, facing1);
                    if (validateBlockRange(data))
                        return data;
                }
            }
        }

        return null;

    }

    private boolean validateBlockRange(final BlockData data) {
        final Vec3 pos = getHitVec(mc.thePlayer.currentEvent, data);

        if (pos == null)
            return false;

        final EntityPlayerSP player = mc.thePlayer;

        final double x = (pos.xCoord - player.posX);
        final double y = (pos.yCoord - (player.posY + mc.thePlayer.getEyeHeight()));
        final double z = (pos.zCoord - player.posZ);

        final float reach = mc.playerController.getBlockReachDistance() + expandProperty.getValue().floatValue();

        return Math.sqrt(x * x + y * y + z * z) <= reach;
    }

    private boolean validateReplaceable(final BlockData data) {
        final BlockPos pos = data.pos.offset(data.face);
        return mc.theWorld.getBlockState(pos)
                .getBlock()
                .isReplaceable(mc.theWorld, pos);
    }

    private BlockPos getBlockUnder() {
        if (expandProperty.getValue().floatValue() > 0.f) {
            if (keepPosProperty.isEnabled() && !Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                double[] expand = getExpandCords(Math.min(startPosY, mc.thePlayer.posY) - 1);
                boolean air = isAirBlock(BlockUtils.getBlock(mc.thePlayer.posX, Math.min(startPosY, mc.thePlayer.posY) - 1, mc.thePlayer.posZ));
                return new BlockPos(air ? mc.thePlayer.posX : expand[0], Math.min(startPosY, mc.thePlayer.posY) - 1, air ? mc.thePlayer.posZ : expand[1]);
            } else {
                startPosY = mc.thePlayer.posY;
                double[] expand2 = getExpandCords(startPosY - 1);
                boolean air1 = isAirBlock(BlockUtils.getBlock(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ));
//            if(air1)
//                Wrapper.sendMessage("air");
                return new BlockPos(air1 ? mc.thePlayer.posX : expand2[0], mc.thePlayer.posY - 1, air1 ? mc.thePlayer.posZ : expand2[1]);
            }
        } else {
            if (keepPosProperty.isEnabled() && !Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                return new BlockPos(mc.thePlayer.posX, Math.min(startPosY, mc.thePlayer.posY) - 1, mc.thePlayer.posZ);
            } else {
                startPosY = mc.thePlayer.posY;
                return new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);
            }
        }
    }
//    private void moveBlocksIntoHotBar() {
//        // If no blocks in hot bar
//        if (ticksSinceWindowClick > 3) {
//            // Look for best block stack in inventory
//            final int bestStackInInv = getBestBlockStack(InventoryUtils.EXCLUDE_ARMOR_BEGIN, InventoryUtils.ONLY_HOT_BAR_BEGIN);
//            // If you have no blocks return
//            if (bestStackInInv == -1) return;
//
//            boolean foundEmptySlot = false;
//
//            for (int i = InventoryUtils.END - 1; i >= InventoryUtils.ONLY_HOT_BAR_BEGIN; i--) {
//                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
//
//                if (stack == null) {
//                    if (lastRequest == null || lastRequest.isCompleted()) {
//                        final int slotID = i;
//                        InventoryUtils.queueClickRequest(lastRequest = new WindowClickRequest() {
//                            @Override
//                            public void performRequest() {
//                                // Move blocks from inventory into free slot
//                                InventoryUtils.windowClick(mc, bestStackInInv,
//                                        slotID - InventoryUtils.ONLY_HOT_BAR_BEGIN,
//                                        InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
//                            }
//                        };
//                    }
//
//                    foundEmptySlot = true;
//                }
//            }
//
//            if (!foundEmptySlot) {
//                if (lastRequest == null || lastRequest.isCompleted()) {
//                    InventoryUtils.queueClickRequest(lastRequest = new WindowClickRequest() {
//                        @Override
//                        public void performRequest() {
//                            final int overrideSlot = 9;
//                            // Swap with item in last slot of hot bar
//                            InventoryUtils.windowClick(mc, bestStackInInv, overrideSlot,
//                                    InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
//                        }
//                    };
//                }
//            }
//        }
//    }

    private void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }

    private int getBestBlockStack() {
        int bestSlot = -1, bestSlotStackSize = 0;

        for (int i = InventoryUtils.ONLY_HOT_BAR_BEGIN; i < InventoryUtils.END; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null &&
                    stack.getItem() instanceof ItemBlock &&
                    InventoryUtils.isStackValidToPlace(stack)) {

                bestSlot = i;
                bestSlotStackSize = stack.stackSize;
            }
        }

        return bestSlot;
    }

    private void calculateTotalBlockCount() {
        totalBlockCount = 0;

        for (int i = InventoryUtils.EXCLUDE_ARMOR_BEGIN; i < InventoryUtils.END; i++) {
            final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null &&
                    stack.stackSize >= 1 &&
                    stack.getItem() instanceof ItemBlock &&
                    InventoryUtils.isStackValidToPlace(stack)) {

                totalBlockCount += stack.stackSize;
            }
        }
    }

    private static class BlockData {

        private final BlockPos pos;
        private final EnumFacing face;
        private final Vec3 hitVec1;

        public BlockData(BlockPos pos, EnumFacing face) {
            this.pos = pos;
            this.face = face;
            this.hitVec1 = calculateBlockData();
        }

        private Vec3 calculateBlockData() {
            Vec3 hitVec = new Vec3(pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random());

            final MovingObjectPosition movingObjectPosition = RotationUtils.rayCast(RotationUtils.getCurrentRotationsv(), mc.playerController.getBlockReachDistance());

            switch (face) {
                case DOWN -> hitVec.yCoord = pos.getY();
                case UP -> hitVec.yCoord = pos.getY() + 1;
                case NORTH -> hitVec.zCoord = pos.getZ();
                case EAST -> hitVec.xCoord = pos.getX() + 1;
                case SOUTH -> hitVec.zCoord = pos.getZ() + 1;
                case WEST -> hitVec.xCoord = pos.getX();
            }

            if (movingObjectPosition != null && movingObjectPosition.getBlockPos() != null && movingObjectPosition.getBlockPos().equals(pos) &&
                    movingObjectPosition.sideHit == face) {

                if (movingObjectPosition.hitVec != null)
                    hitVec = movingObjectPosition.hitVec;
            }

            return hitVec;
        }
    }

    public Vec3 getHitVec(PlayerUpdateEvent event, BlockData blockData) {
        switch (hitVecModeEnumSetting.getMode()) {
            case "Custom" -> {
                return blockData.hitVec1;
            }
            case "Block Pos" -> {
                return new Vec3(blockData.pos);
            }
            case "Randomized" -> {
                return new Vec3(blockData.pos.getX() + Math.random(),
                        blockData.pos.getY() + Math.random(), blockData.pos.getZ() + Math.random());
            }
            case "Raytrace" -> {
                // Perform ray trace with current angle stepped rotations
                final MovingObjectPosition rayTraceResult = RotationUtils.rayTraceBlocks(mc,
                        event.isPre() ? event.getPrevYaw() : event.getYaw(),
                        event.isPre() ? event.getPrevPitch() : event.getPitch());
                // If nothing is hit return
                if (rayTraceResult == null) return null;
                // If did not hit block return
                if (rayTraceResult.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return null;
                // If side hit does not match block data return
                if (rayTraceResult.sideHit != blockData.face) return null;
                // If block pos does not match block data return
                final BlockPos dstPos = blockData.pos;
                final BlockPos rayDstPos = rayTraceResult.getBlockPos();
                if (rayDstPos.getX() != dstPos.getX() ||
                        rayDstPos.getY() != dstPos.getY() ||
                        rayDstPos.getZ() != dstPos.getZ()) return null;

                return rayTraceResult.hitVec;
            }
            case "Old Random" -> {
                return getVec3(blockData.pos, blockData.face);
            }
        }
        return null;
    }

    private Vec3 getVec3(final BlockPos pos, final EnumFacing face) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;
        x += (double) face.getFrontOffsetX() / 2;
        z += (double) face.getFrontOffsetZ() / 2;
        y += (double) face.getFrontOffsetY() / 2;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtils.randomDouble(-0.3, 0.3);
            z += MathUtils.randomDouble(-0.3, 0.3);
        } else {
            y += MathUtils.randomDouble(0.4, 0.5);
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtils.randomDouble(-0.3, 0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtils.randomDouble(-0.3, 0.3);
        }
        return new Vec3(x, y, z);
    }

}
