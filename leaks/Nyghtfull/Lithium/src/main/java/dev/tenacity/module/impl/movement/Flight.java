package dev.tenacity.module.impl.movement;


import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.event.impl.player.BoundingBoxEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.player.movement.MoveEvent;
import dev.tenacity.event.impl.player.UpdateEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.player.*;
import dev.tenacity.utils.server.PacketUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.*;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public final class Flight extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog","Hypixckel","Grim","Kokscraft Vanilla","Intave","Invaded","Damage","Vulcan Motion","VerusDMG","VulcanFast","Vulcan Timer", "Zonecraft", "Watchdog", "Vanilla", "AirWalk", "Viper", "Verus", "Minemen", "Old NCP", "Slime", "Custom", "Packet", "Libercraft", "Vulcan");
    private final NumberSetting teleportDelay = new NumberSetting("Teleport Delay", 5, 20, 1, 1);
    private final NumberSetting teleportLength = new NumberSetting("Teleport Length", 5, 20, 1, 1);
    private final NumberSetting timerAmount = new NumberSetting("Timer Amount", 1, 3, 0.1, 0.1);
    public static final NumberSetting horizontalSpeed = new NumberSetting("Horizontal Speed", 2, 5, 0, 0.1);
    private final NumberSetting verticalSpeed = new NumberSetting("Vertical Speed", 1, 5, 0, 0.1);
    private final BooleanSetting viewBobbing = new BooleanSetting("View Bobbing", true);
    private final BooleanSetting antiKick = new BooleanSetting("Anti-kick", false);
    private int stage;
    private int ticks;
    private boolean doFly;
    private double x, y, z;
    private double gayY;
    private double lastX, lastY, lastZ;
    public boolean setback;
    public boolean HadDamage;
    private int runningTicks = 0;

    private final TimerUtil pearlTimer = new TimerUtil();

    private final CopyOnWriteArrayList<Packet> packets = new CopyOnWriteArrayList<>();
    private boolean hasClipped;
    private int slot = 0;
    private double speedStage;
    private float clip;
    private double moveSpeed;
    private boolean started;
    private int stage2;
    private final TimerUtil timer = new TimerUtil();
    public static final Set<BlockPosition> hiddenBlocks = new HashSet<>();
    private boolean hasS08;
    private boolean hasDamaged;
    private boolean up;
    private int airTicks;
    int Flags;
    private boolean flag;

    private boolean adjustSpeed, canSpeed, hasBeenDamaged;
    public double moveSpeed2, lastDist;
    public int stage3;
    private boolean shift;

    private double startx,starty,startz;

    // Custom fly settings
    private final BooleanSetting damage = new BooleanSetting("Damage", false);
    private final ModeSetting damageMode = new ModeSetting("Damage Mode", "Vanilla", "Vanilla", "Suffocate", "NCP");

    private final NumberSetting motionY = new NumberSetting("Motion Y", 0, 0.3, -0.3, 0.01);

    private final BooleanSetting speed = new BooleanSetting("Speed", false);
    private final NumberSetting speedAmount = new NumberSetting("Speed Amount", 0.2, 9, 0.05, 0.01);

    public Flight() {
        super("Flight", Category.MOVEMENT, "Makes you hover in the air");
        horizontalSpeed.addParent(mode, m -> m.is("Vanilla"));
        verticalSpeed.addParent(mode, m -> m.is("Vanilla"));
        antiKick.addParent(mode, m -> m.is("Vanilla"));
        damage.addParent(mode, m -> m.is("Custom"));
        damageMode.addParent(damage, ParentAttribute.BOOLEAN_CONDITION);
        motionY.addParent(mode, m -> m.is("Custom"));
        speed.addParent(mode, m -> m.is("Custom"));
        speedAmount.addParent(speed, ParentAttribute.BOOLEAN_CONDITION);
        teleportDelay.addParent(mode, m -> m.is("Packet"));
        teleportLength.addParent(mode, m -> m.is("Packet"));
        this.addSettings(mode, teleportDelay, teleportLength, motionY, damage, damageMode, speed, speedAmount, timerAmount, horizontalSpeed, verticalSpeed, viewBobbing, antiKick);
    }

    @Override
    public void onMoveEvent(MoveEvent e) {
        switch (mode.getMode()) {
            case "Vanilla":
                e.setSpeed(MovementUtils.isMoving() ? horizontalSpeed.getValue().floatValue() : 0);
                break;
            case"Kokscraft Vanilla":
                if(!shift) {
                    e.setSpeed(MovementUtils.isMoving() ? 2 : 0);
                }
                if(shift) {
                    e.setSpeed(0.1);
                }
                break;

            case "Damage":
                //if (timer.hasTimeElapsed(3000) ) {
                 //   HadDamage = false;
               // }



                if (HadDamage) {

                    mc.timer.timerSpeed = 0.5f;
                } else{
                    mc.timer.timerSpeed = 1.0f;
                }

                if (MovementUtils.isMoving()) {
                   if (HadDamage) {
                        e.setSpeed(2);
                    } else {
                        e.setSpeed(MovementUtils.getBaseMoveSpeed()* 1.01f);
                    }
                }
                break;

            case "VulcanFast":
                mc.timer.timerSpeed = 0.58f;
                if (!setback) {
                    MovementUtils.setSpeed(e, 0.0);
                } else {
                   MovementUtils.setSpeed(e, 9);
                }

                if(mc.thePlayer.ticksExisted % 6 == 0) {
                    MovementUtils.strafe(0.0f);
                    return;
                }

                break;
            case "Watchdog":
                e.setSpeed(0);
                break;
            case "Slime":
                if(stage < 8) {
                    e.setSpeed(0);
                }
                break;
            case "Packet":
                e.setSpeed(0);
                break;
        }
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        this.setSuffix(mode.getMode());
        if (viewBobbing.isEnabled()) {
            mc.thePlayer.cameraYaw = mc.thePlayer.cameraPitch = 0.08F;
        }
        if (!mode.getMode().equals("Libercraft") && !mode.getMode().equals("Damage")) {
            mc.timer.timerSpeed = timerAmount.getValue().floatValue();
        }
        if (mc.thePlayer.isUsingItem()) {
            shift = true;
        }
        if (mc.thePlayer.hurtTime>1) {
            HadDamage = true;
        }

        switch (mode.getMode()) {
            case "Watchdog":
                if (e.isPre()) {
                    mc.thePlayer.motionY = 0;
                    stage++;
                    if(stage == 1) {
                        final double x = e.getX() + -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * 7.99;
                        final double y = e.getY() - 1.75;
                        final double z = e.getZ() + Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * 7.99;
                        if(mc.theWorld.getBlockState(new BlockPosition(x, y, z)).getBlock() == Blocks.air) {
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                            mc.thePlayer.setPosition(x, y, z);
                        }
                    }
                }
                break;

            case"Kokscraft Vanilla":
                if(!shift) {
                    mc.thePlayer.motionY = 0;
                }
                    if (mc.gameSettings.keyBindJump.isKeyDown()) {
                        mc.thePlayer.motionY = 1;
                    }
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                        mc.thePlayer.motionY = -1;
                    }
                    if(shift) {
                        if(mc.thePlayer.ticksExisted % 50 == 0){
                      //  mc.thePlayer.setPosition(e.getX(),e.getY() + 1,e.getZ());
                           mc.thePlayer.jump();
                        }
                        if(mc.thePlayer.ticksExisted % 2==0) {
                            mc.gameSettings.keyBindForward.pressed = true;
                        } else{
                            mc.gameSettings.keyBindForward.pressed = false;
                        }
                        if(mc.thePlayer.ticksExisted % 3==0) {
                            mc.gameSettings.keyBindBack.pressed = true;
                        } else{
                            mc.gameSettings.keyBindBack.pressed = false;
                        }
                        if(mc.thePlayer.ticksExisted % 4==0) {
                            mc.gameSettings.keyBindRight.pressed = true;
                        } else{
                            mc.gameSettings.keyBindRight.pressed = false;
                        }
                        if(mc.thePlayer.ticksExisted % 5==0) {
                            mc.gameSettings.keyBindLeft.pressed = true;
                        } else{
                            mc.gameSettings.keyBindLeft.pressed = false;
                        }
                        if(Flags >50) {
                            ChatUtil.print("U can now disable fly");
                        }

                    } else{
                        Flags = 0;
                    }




                break;

            case"Damage":
                mc.thePlayer.motionY = 0.0;
                break;

            case"Grim":
                SecureRandom random = new SecureRandom();
                float yaw = (float) Math.toRadians(mc.thePlayer.rotationYaw);
                float pitch = (float) Math.toRadians(mc.thePlayer.rotationPitch);
                e.setYaw((float) (yaw + MathHelper.getRandomDoubleInRange(random,20,20)));
                e.setPitch(pitch);

                e.setX(Math.sin(yaw) * 10000);
               // e.setY((mc.thePlayer.posY + 150000000) * 0.98 * Math.tan(-80));
              //  e.setZ(Math.sin(yaw) * 10000);


                break;

            case"Hypixckel":
                if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
                    gayY = mc.thePlayer.posY;
                }
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }

                break;

            case"VulcanFast":
                mc.thePlayer.motionY = 0.0;


                break;

            case"Vulcan Motion":
                MovementUtils.strafe(1.2f + (float) (Math.random() / 10D));
                mc.thePlayer.motionY = (mc.gameSettings.keyBindJump.isKeyDown() ? 0.42F : mc.gameSettings.keyBindSneak.isKeyDown() ? -0.42F : 0);

                if (!MovementUtils.isMoving()) {
                  //  MovementUtils.strafe(0.0f);
                   //mc.thePlayer.posX = 0;
                  //  mc.thePlayer.posZ = 0;
                }

                if (pearlTimer.hasTimeElapsed((long) (150 + Math.random() * 50)) && MovementUtils.isMoving()) {
                    pearlTimer.reset();
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                }

                break;





            case "Vulcan Timer":



                if (Flags>3) {
                    mc.timer.timerSpeed = 10.0f;
                } else{

                        //mc.thePlayer.setPosition(startx, starty + 1, startz);

                }


                if (shift) {
                    mc.timer.timerSpeed = 1.0f;
                }
                break;

            case "Invaded":


                if (Flags>3 && MovementUtils.isMoving()) {
                   MovementUtils.setSpeed(2);
                }
                if(Flags<3) {
                    if(mc.thePlayer.ticksExisted % 10 ==0) {
                        ChatUtil.print(EnumChatFormatting.ITALIC +"*intensively waiting for s08*");
                    }
                    MovementUtils.strafe(0);
                   // mc.thePlayer.posX = 0;
                  //  mc.thePlayer.posZ = 0;
                }



                break;

            case "Vulcan":
                if(e.isPre()) {
                    mc.thePlayer.motionY = 0;
                    e.setOnGround(true);
                    mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), new BlockPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ), EnumFacing.UP, new Vec3(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ));
                }
                break;
            case "Libercraft":
                if(e.isPre()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));

                    if(stage < 3) {
                        e.setOnGround(false);
                        if(mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            stage++;
                        }
                    } else {
                        if(mc.thePlayer.hurtTime > 0 && !hasDamaged) {
                            hasDamaged = true;
                        }

                        if(hasDamaged) {
                           mc.timer.timerSpeed = 0.8f;
                            e.setOnGround(true);
                            mc.timer.timerSpeed = 0.4f;
                            mc.thePlayer.motionY = 0.05;
                            MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * 10);
                        }
                    }
                }
                break;
            case "Zonecraft":
                if(e.isPre()) {
                    stage++;
                    switch(stage) {
                        case 1:
                            e.setOnGround(true);
                            MovementUtils.setSpeed(0.55);
                            break;
                    }

                    mc.thePlayer.motionY = 0;
                    e.setY(mc.thePlayer.posY + 0.1);
                }
                break;
            case "Verus":
                if (e.isPre()) {

                    if(!mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.42f;
                            up = true;
                        } else if (up) {
                            if (!mc.thePlayer.isCollidedHorizontally) {
                                mc.thePlayer.motionY = -0.0784000015258789;
                            }
                            up = false;
                        }
                    } else if(mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.thePlayer.motionY = 0.42f;
                    }

                    MovementUtils.setSpeed(mc.gameSettings.keyBindJump.isKeyDown() ? 0 : 0.33);
                }
                break;
            case "Vanilla":
                if(mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = verticalSpeed.getValue();
                }
                if(mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = -verticalSpeed.getValue();
                }



                    if(antiKick.isEnabled()) {
                        if(!mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.thePlayer.motionY = 0.0;

                          //  if (!MovementUtils.isMoving()) {
                                if (mc.thePlayer.ticksExisted % 2 == 0) {
                                    mc.thePlayer.motionY = 0.01;
                                    //0.1
                                } else {
                                    mc.thePlayer.motionY = -0.01;
                                }
                          //  }
                        }
                    } else{

                        if(!mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.thePlayer.motionY = 0.0f;
                        }
                    }
                break;
            case "AirWalk":
                break;
            case "Viper":
                mc.thePlayer.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1 : mc.gameSettings.keyBindSneak.isKeyDown() ? -1 : 0;
                e.setOnGround(true);
                MovementUtils.setSpeed(0.3);
                break;
            case "Old NCP":
                if (hasDamaged) {
                    e.setOnGround(true);
                    double baseSpeed = MovementUtils.getBaseMoveSpeed();
                    if (!MovementUtils.isMoving() || mc.thePlayer.isCollidedHorizontally) moveSpeed = baseSpeed;
                    if (moveSpeed > baseSpeed)
                        moveSpeed -= moveSpeed / 159.0;

                    moveSpeed = Math.max(baseSpeed, moveSpeed);

                    if (e.isPre()) {
                        mc.timer.timerSpeed = 1;
                        if (MovementUtils.isMoving())
                            MovementUtils.setSpeed(moveSpeed);
                        mc.thePlayer.motionY = 0;
                        double y = 1.0E-10;
                        e.setY(e.getY() - y);
                    }
                } else if (mc.thePlayer.onGround) {
                    DamageUtils.damage(DamageUtils.DamageType.WATCHDOGUP);
                    mc.thePlayer.jump();
                    hasDamaged = true;
                }
                break;
            case "Slime":
                if(e.isPre()) {
                    stage++;
                    switch (stage) {
                        case 1:
                            if (mc.thePlayer.onGround) {
                                mc.thePlayer.jump();
                            }
                            break;
                        case 7:
                            BlockPosition pos = new BlockPosition(e.getX(), e.getY() - 2, e.getZ());
                            e.setPitch(mc.thePlayer.rotationPitchHead = 90);
                            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, EnumFacing.UP, new Vec3(pos))) {
                                mc.thePlayer.swingItem();
                            }
                            break;
                    }
                    if(stage > 8) {
                        e.setOnGround(true);
                        MovementUtils.setSpeed(0.3);
                        mc.thePlayer.motionY = 0;
                    }
                }
                break;
            case "Custom":
                if(e.isPre()) {
                    stage++;
                    switch(stage) {
                        case 1:
                            if(damage.isEnabled())
                                DamageUtils.damage(DamageUtils.DamageType.valueOf(damageMode.getMode().toUpperCase()));
                            break;
                    }
                    mc.thePlayer.motionY = motionY.getValue();
                    if(speed.isEnabled()) MovementUtils.setSpeed(speedAmount.getValue());
                }
                break;
            case "Packet":
                if(e.isPre()) {
                    mc.thePlayer.motionY = 0;
                    if(MovementUtils.isMoving() && mc.thePlayer.ticksExisted % teleportDelay.getValue().intValue() == 0) {
                        final double x = e.getX() + -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * teleportLength.getValue().intValue();
                        final double z = e.getZ() + Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * teleportLength.getValue().intValue();
                        PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, mc.thePlayer.posY, z, false));
                        mc.thePlayer.setPosition(x, mc.thePlayer.posY, z);
                    }
                }
                break;
        }
    }

    @Override
    public void onUpdateEvent(UpdateEvent event) {
    }


    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        if(mode.is("Kokscraft Vanilla")) {
            if(!shift) {
                if (event.getPacket() instanceof C03PacketPlayer) {
                    event.cancel();
                }
            }
        }

        if(mode.is("Damage")) {
            if(HadDamage) {
                if (event.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                    //event.cancel();
                }
            }
        }

        if(mode.is("Vulcan Motion")) {
            if (event.getPacket() instanceof C03PacketPlayer) {
                event.cancel();
            }
        }
        if(mode.is("Libercraft")) {


            if(event.getPacket() instanceof C0FPacketConfirmTransaction || event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C00PacketKeepAlive) {
               event.cancel();
            }

        }
        if(mc.isSingleplayer() || mc.thePlayer == null) return;
        if(mode.is("Slime") && stage > 7 && PacketUtils.isPacketValid(event.getPacket())) {
            event.cancel();
            packets.add(event.getPacket());
        }
        if(mode.is("Watchdog") && event.getPacket() instanceof C03PacketPlayer) {
            event.cancel();
        }


    }

    @Override
    public void onBoundingBoxEvent(BoundingBoxEvent event) {
        if(mode.is("AirWalk") || mode.is("Verus")) {
            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            event.setBoundingBox(axisAlignedBB);
        }

        if(mode.is("Vulcan Timer")) {

            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            event.setBoundingBox(axisAlignedBB);
        }

        if(mode.is("Kokscraft Vanilla")) {
            if(shift) {
                final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
                event.setBoundingBox(axisAlignedBB);
            }
        }

        if(mode.is("Hypixckel")) {
            if (event.getBlock() instanceof BlockAir && !mc.gameSettings.keyBindSneak.isKeyDown() && (mc.thePlayer.posY < gayY+ 1 || mc.gameSettings.keyBindJump.isKeyDown())) {
                final double x = event.getBlockPos().getX(), G = event.getBlockPos().getY(), z = event.getBlockPos().getZ();

                if (gayY < mc.thePlayer.posY) {
                    event.setBoundingBox(AxisAlignedBB.fromBounds(
                            -15,
                            -1,
                            -15,
                            15,
                            1,
                            15
                    ).offset(x, y, z));
                }
            }
        }

        if(mode.is("Invaded")) {

            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            event.setBoundingBox(axisAlignedBB);
        }

        if(mode.is("Vulcan Motion")) {

            final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            event.setBoundingBox(axisAlignedBB);
        }

        if(mode.is("Damage")) {

          //  final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
          //  event.setBoundingBox(axisAlignedBB);
        }

        if(mode.is("Libercraft") && hasDamaged) {

           // final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
            //event.setBoundingBox(axisAlignedBB);
        }


    }



    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent e) {
        if(mode.is("Vulcan Timer")) {

            if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                Flags++;
                
                e.cancel();
            }
        }

        if(mode.is("Kokscraft Vanilla")) {
            if(shift) {
                if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                    e.cancel();
                    Flags++;
                }
            }
        }

        if(mode.is("Invaded")) {
            if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                Flags++;

                e.cancel();
            }
        }

        

        if(mode.is("Vulcan Motion") && e.getPacket() instanceof S08PacketPlayerPosLook) {
            if (!flag) {
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();

                double deltaX = packet.getX() - mc.thePlayer.posX;
                double deltaY = packet.getY() - mc.thePlayer.posY;
                double deltaZ = packet.getZ() - mc.thePlayer.posZ;

                if (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) < 10) {
                    e.cancel();
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
                }
            }
        }


        if(mode.is("VulcanFast")) {
            if (e.getPacket() instanceof S08PacketPlayerPosLook && !setback) {
                final S08PacketPlayerPosLook S08 = (S08PacketPlayerPosLook) e.getPacket();

                setback= true;

                mc.thePlayer.posX = S08.getX();
                mc.thePlayer.posY = S08.getY();
                mc.thePlayer.posZ = S08.getZ();
                return;
            }

        }

        if (e.getPacket() instanceof S08PacketPlayerPosLook && !hasS08) {
            S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook) e.getPacket();
            hasS08 = true;
        }
    }

    @Override
    public void onEnable() {

        if (mode.is("VerusDMG")) {
           mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));

        }
        if(mode.is("Kokscraft Vanilla")) {
          ChatUtil.print("right click to land");
        }
        gayY = Math.floor(mc.thePlayer.posY);

        startx = mc.thePlayer.posX;
        startz = mc.thePlayer.posZ;
        starty = mc.thePlayer.posY;


        if (mode.is("Verus")) {
          //  DamageUtils.damage(DamageUtils.DamageType.VERUS);

        }

        if (mode.is("Damage")) {
              DamageUtils.damage(DamageUtils.DamageType.VERUS);

        }
        if (mode.is("Invaded")) {
            mc.thePlayer.jump();

        }
        
        if (mode.is("Vulcan Motion")) {
            flag = false;
            pearlTimer.reset();

            if (!mc.thePlayer.onGround) {
               if(this.isEnabled()) {
                   ChatUtil.print("U Need to be onground");
               }
                return;
            }

            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2 + Math.random() / 2, mc.thePlayer.posZ, false));
        }
        if (mode.is("VulcanFast")) {
            runningTicks = 0;
            setback = false;

            if (mc.thePlayer == null) {

                return;
            }

            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 2, mc.thePlayer.posZ, false));






        }
        Flags = 0;
        if (mode.is("Invaded")) {
            mc.timer.timerSpeed = 1.0f;
        }


        if (mode.is("Vulcan Timer")) {
            mc.timer.timerSpeed = 1.0f;


        }

        hasDamaged = false;
        doFly = false;
        ticks = 0;
        stage = 0;
        stage3 = 0;
        clip = 0;
        hasClipped = false;
        packets.clear();
        timer.reset();
        moveSpeed = 0;
        stage2 = 0;
        hasS08 = false;
        if (mc.thePlayer != null) {
            lastX = mc.thePlayer.posX;
            lastY = mc.thePlayer.posY;
            lastZ = mc.thePlayer.posZ;
            y = 0;
            slot = mc.thePlayer.inventory.currentItem;
            if (mode.is("Old NCP")) {
                moveSpeed = 1.6;
            } else if (mode.is("Slime")) {
                int slimeBlockSlot = InventoryUtils.getBlockSlot(Blocks.slime_block);
                if (slimeBlockSlot != -1) {
                    mc.thePlayer.inventory.currentItem = slimeBlockSlot;
                } else {
                    NotificationManager.post(NotificationType.DISABLE, "Flight", "No slime block found in hotbar!");
                    toggleSilent();
                    return;
                }
            }
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mode.is("Libercraft")) {
         MovementUtils.setSpeed(0);

        }
        hasDamaged = false;
        if(mode.is("Kokscraft Vanilla")) {
            if(shift) {
                mc.gameSettings.keyBindForward.pressed = false;
                mc.gameSettings.keyBindBack.pressed = false;
                mc.gameSettings.keyBindRight.pressed = false;
                mc.gameSettings.keyBindLeft.pressed = false;
            }
        }
        if (mode.is("VerusDMG")) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            MovementUtils.setSpeed(0);

        }

        Flags = 0;

        shift = false;
        if (mode.is("Vulcan Timer")) {
            mc.timer.timerSpeed = 1.0f;

        }

        if (mode.is("Invaded")) {
            mc.timer.timerSpeed = 1.0f;

        }
        if (mode.is("Vanilla") || mode.is("Minemen") || mode.is("Old NCP") || mode.is("Watchdog")|| mode.is("Kokscraft Vanilla")) {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
        } else if (mode.is("Slime")) {
            mc.thePlayer.inventory.currentItem = slot;
        }

        mc.timer.timerSpeed = 1;
        packets.forEach(PacketUtils::sendPacketNoEvent);
        packets.clear();
        super.onDisable();
    }

}
