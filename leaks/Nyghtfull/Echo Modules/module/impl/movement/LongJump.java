package dev.echo.module.impl.movement;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.player.MoveEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.player.ChatUtil;
import dev.echo.utils.player.MovementUtils;
import dev.echo.utils.server.PacketUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public final class LongJump extends Module {

    private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Watchdog","Hypixel", "NCP", "AGC");
    private final ModeSetting watchdogMode = new ModeSetting("Watchdog Mode", "Damage", "Damage", "Damageless");
    private final NumberSetting damageSpeed = new NumberSetting("Damage Speed", 1, 20, 1, 0.5);
    private final BooleanSetting spoofY = new BooleanSetting("Spoof Y", false);
    private int movementTicks = 0;
    private double speed;

    private int tick2 = 0;

    private boolean kid;
    private float pitch;
    private int prevSlot, ticks = 0;
    private boolean damagedBow;
    private final TimerUtil jumpTimer = new TimerUtil();
    private final TimerUtil timer = new TimerUtil();
    private boolean damaged;
    private double x;
    private double y;
    private double z;
    private final List<Packet> packets = new ArrayList<>();
    private int stage;

    @Link
    public Listener<MotionEvent> motionEventListener = event -> {
        setSuffix(mode.getMode());
        if(spoofY.isEnabled()) mc.thePlayer.posY = y;
        switch (mode.getMode()) {
            case "Vanilla":
                if (MovementUtils.isMoving() && mc.thePlayer.onGround) {
                    MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * 2);
                    mc.thePlayer.jump();
                }
                break;
            case "Watchdog":
                if(event.isPre()) {
                    switch(watchdogMode.getMode()) {
                        case "Damage":
                            if(mc.thePlayer.onGround) {
                                stage++;
                                if(stage <= 3)
                                    mc.thePlayer.jump();
                                if(stage > 5 && damaged)
                                    toggle();
                            }
                            if(stage <= 3) {
                                event.setOnGround(false);
                                mc.thePlayer.posY = y;
                                mc.timer.timerSpeed = damageSpeed.getValue().floatValue();
                                speed = 1.2;
                            }
                            if(mc.thePlayer.hurtTime > 0) {
                                damaged = true;
                                ticks++;
                                if(ticks < 2)
                                    mc.thePlayer.motionY = 0.41999998688698;
                                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * speed);
                                speed -= 0.01;
                                mc.timer.timerSpeed = 1;
                            }
                            if(damaged) {
                                mc.thePlayer.motionY += 0.0049;
                            }
                            break;
                        case "Damageless":
                            stage++;

                            if(stage == 1 && mc.thePlayer.onGround) {
                                mc.thePlayer.motionY = 0.42;
                                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * 1.2);
                                speed = 1.45f;
                            }

                            if(stage > 1) {
                                MovementUtils.setSpeed(MovementUtils.getBaseMoveSpeed() * speed);
                                speed -= 0.015;
                            }

                            if(mc.thePlayer.onGround && stage > 1)
                                toggle();
                            break;
                    }
                }
                break;
            case "NCP":
                if (MovementUtils.isMoving()) {
                    if (MovementUtils.isOnGround(0.00023)) {
                        mc.thePlayer.motionY = 0.41;
                    }

                    switch (movementTicks) {
                        case 1:
                            speed = MovementUtils.getBaseMoveSpeed();
                            break;
                        case 2:
                            speed = MovementUtils.getBaseMoveSpeed() + (0.132535 * Math.random());
                            break;
                        case 3:
                            speed = MovementUtils.getBaseMoveSpeed() / 2;
                            break;
                    }
                    MovementUtils.setSpeed(Math.max(speed, MovementUtils.getBaseMoveSpeed()));
                    movementTicks++;
                }
                break;
            case "AGC":
                int bow = getFireballSlot();
                if(!kid) {
                    event.setPitch(pitch);
                }
              //  mc.thePlayer.rotationPitch = pitch;
                if (damagedBow) {
                    if (mc.thePlayer.onGround && jumpTimer.hasTimeElapsed(1000)) {
                        toggle();
                    }

                }
                if(damagedBow) {
                    tick2++;
                }
                if(!kid) {
                    if (timer.hasTimeElapsed(10)) {
                        if (prevSlot != bow) {
                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(bow));
                        }
                        //TODO:FIX THIS
                      //  if (mc.thePlayer.rotationPitch == 90) {
                            ChatUtil.print("woaksd");
                            PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(bow)));
                      //  }
                        kid = true;
                    }
                }

                if (!damagedBow) {
                    switch (ticks) {
                        case 0:
                           // if (prevSlot != bow) {
                          //      PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(bow));
                         //   }
                           // //TODO:FIX THIS
                          //  if (mc.thePlayer.rotationPitch == 90) {
                           //     ChatUtil.print("woaksd");
                            //    PacketUtils.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(bow)));
                           // }
                            break;
                        case 1:
                            PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(event.getX(), event.getY(), event.getZ(), event.getYaw(), pitch, event.isOnGround()));
                            PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                            if (prevSlot != bow) {
                                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(prevSlot));
                            }
                            break;
                    }
                    if (mc.thePlayer.hurtTime != 0) {
                        damagedBow = true;
                    }
                }
        }
        if(!mode.is("Watchdog"))
            ticks++;
    };

    @Link
    public Listener<MoveEvent> moveEventListener = event -> {
        if (mode.is("AGC")) {
            if(mc.thePlayer.hurtTime >= 9) {
                ChatUtil.print("CUm");
                MovementUtils.setSpeed(5);
            }
            mc.gameSettings.keyBindSprint.pressed = true;
          //  event.setX(0);
          //  event.setZ(0);
        }
        if(!damaged && mode.is("Watchdog") && watchdogMode.is("Damage")) {
            event.setSpeed(0);
        }
    };

    public int getFireballSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack is = mc.thePlayer.inventory.getStackInSlot(i);
            if (is != null && is.getItem() == Items.fire_charge) {
                return i;
            }
        }
        return -1;
    }

    public int getItemCount(Item item) {
        int count = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() == item) {
                count += stack.stackSize;
            }
        }
        return count;
    }

    @Override
    public void onEnable() {
        tick2 = 0;
        kid = false;
        timer.reset();
        if ((mode.is("AGC"))) {
            prevSlot = mc.thePlayer.inventory.currentItem;
            pitch = 90;
            if (getFireballSlot() == -1) {
                this.toggleSilent();
                return;
            }
        }
        ticks = 0;
        damagedBow = false;
        damaged = false;
        jumpTimer.reset();
        x = mc.thePlayer.posX;
        y = mc.thePlayer.posY;
        z = mc.thePlayer.posZ;
        packets.clear();
        stage = 0;
        speed = 1.4f;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        packets.forEach(PacketUtils::sendPacketNoEvent);
        packets.clear();
        super.onDisable();
    }

    public LongJump() {
        super("Long Jump", Category.MOVEMENT, "jump further");
        watchdogMode.addParent(mode, m -> m.is("Watchdog"));
        damageSpeed.addParent(mode, m -> m.is("Watchdog") && watchdogMode.is("Damage"));
        this.addSettings(mode, watchdogMode, damageSpeed, spoofY);
    }

}
