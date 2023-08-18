package me.jinthium.straight.impl.modules.movement;

import best.azura.irc.utils.Wrapper;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.components.ItemDamageComponent;
import me.jinthium.straight.impl.components.PacketlessDamageComponent;
import me.jinthium.straight.impl.components.PingSpoofComponent;
import me.jinthium.straight.impl.event.game.SpoofItemEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.movement.longjump.VulcanLongjump;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.InventoryUtils;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import org.checkerframework.checker.units.qual.C;

public class Longjump extends Module {

//    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog", "Vulcan");
//    private final ModeSetting watchdogMode = new ModeSetting("WD Mode", "Normal", "Fireball", "Normal");
//    private int slot, tick;
//    private boolean damaged, inAir;
//    private double moveSpeed = 0, motionY;
//    private float yawAtDamage;

    public Longjump(){
        super("Longjump", Category.MOVEMENT);
        this.registerModes(
                new VulcanLongjump()
        );
//        this.addSettings(mode, watchdogMode);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> this.setSuffix(this.getCurrentMode().getInformationSuffix());

//    @Override
//    public void onEnable() {
//        if(watchdogMode.is("Normal")){
////            PacketlessDamageComponent.setActive(1.0f);
////            ItemDamageComponent.damage(false);
//            damaged = false;
//        }
//        moveSpeed = 0;
//        motionY = 0;
//        inAir = false;
//        tick = 0;
//        super.onEnable();
//    }
//
//    @Callback
//    final EventCallback<PacketEvent> packetEventEventCallback = event -> {
//        if(event.getPacketState() == PacketEvent.PacketState.RECEIVING){
//            if (event.getPacket() instanceof S12PacketEntityVelocity s12 && watchdogMode.is("Normal") && mode.is("Watchdog")) {
//                if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
//                    motionY = s12.motionY / 8000.0D;
//                    Wrapper.sendMessage(String.valueOf(motionY));
//                    if (motionY > 0.1 - Math.random() / 10000f) {
//                        damaged = true;
//                        event.setCancelled(true);
//                    }
//                }
//            }
//        }
//    };
//
//    @Callback
//    final EventCallback<SpoofItemEvent> spoofItemEventCallback = event -> {
//        if(watchdogMode.is("Fireball") && slot != -1)
//            event.setCurrentItem(slot);
//    };
//
//    @Callback
//    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
//        if(event.isPre() && watchdogMode.is("Fireball")){
//            float[] distRots = new float[] {MovementUtil.getMovementDirection1() - 180, 90};
//            RotationUtils.setRotations(event, distRots, 90);
//        }
//
//        if(event.isPre() && watchdogMode.is("Normal")){
//            if(mc.thePlayer.onGround && tick < 4){
//                mc.thePlayer.jump();
//                tick++;
//                event.setOnGround(false);
//            }
//
//            if(tick == 4) {
//                mc.thePlayer.motionY = 0.42F;
////                        MovementUtil.strafe(MovementUtil.getAllowedHorizontalDistance() * 1.6);
//                event.setOnGround(false);
//                tick++;
//            }
//
//            if(tick == 5 && mc.thePlayer.motionY > 0.2 && mc.thePlayer.motionY < .25){
//                Wrapper.sendMessage("help");
//                event.setOnGround(true);
//                tick++;
//            }
//        }
//
//        if(event.isUpdate()){
////            if (inAir && mc.thePlayer.onGround) {
////                this.toggle();
////            }
////
////            inAir = !mc.thePlayer.onGround && damaged;
//            switch (watchdogMode.getMode()){
//                case "Fireball" -> {
//                    slot = -1;
////                    mc.thePlayer.motionY += 0.028;
//                    Wrapper.sendMessage(String.valueOf(mc.thePlayer.hurtTime));
//                    if (mc.thePlayer.hurtTime == 10) {
//                        yawAtDamage = mc.thePlayer.rotationYaw;
//                        mc.thePlayer.motionY = 1.5;
//                        moveSpeed = 1.4;
//                    }
//
//                    if (mc.thePlayer.hurtTime == 9) {
//                        moveSpeed = 2 - Math.random() / 100f;
//                    }
//
//                    if (mc.thePlayer.ticksSinceVelocity <= 11) {
//                        mc.thePlayer.motionX *= 1.3f;
//                        mc.thePlayer.motionZ *= 1.3f;
//                        moveSpeed -= (moveSpeed / 159) + Math.random() / 100f;
//                    }
//
//                    int item = InventoryUtils.findItem(Items.fire_charge);
//
//                    if (mc.thePlayer.onGroundTicks > 0) {
//                        MovementUtil.stop();
//                    }
//
//                    if(item == -1)
//                        return;
//
//                    slot = item;
//
//                    tick++;
//
//                    if (tick == 2) {
//                        PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventoryContainer.getSlot(item).getStack()));
//                    }
//                }
//                case "Normal" -> {
//                   // PingSpoofComponent.setSpoofing(2000, true, false, false, false, false);
//
//                    //Wrapper.sendMessage(String.valueOf(event.isOnGround()));
//
//
//                    if(!damaged)
//                        return;
//
//                    if (mc.thePlayer.ticksSinceVelocity == 5) {
//                        MovementUtil.strafe(MovementUtil.getAllowedHorizontalDistance() * (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.75 : 0.7) - Math.random() / 10000f);
////                        mc.thePlayer.jump();
//                    }
//
//                    if (mc.thePlayer.ticksSinceVelocity == 9) {
//                        MovementUtil.strafe((mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.8 : 0.7) - Math.random() / 10000f);
//                        mc.thePlayer.motionY = motionY;
//                    }
//
//                    if (mc.thePlayer.ticksSinceVelocity <= 50 && mc.thePlayer.ticksSinceVelocity > 9) {
//                        mc.thePlayer.motionY += 0.028;
//
//                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
//                            mc.thePlayer.motionX *= 1.038;
//                            mc.thePlayer.motionZ *= 1.038;
//                        } else {
//                            if (mc.thePlayer.ticksSinceVelocity == 12 || mc.thePlayer.ticksSinceVelocity == 13) {
//                                mc.thePlayer.motionX *= 1.1;
//                                mc.thePlayer.motionZ *= 1.1;
//                            }
//
//                            mc.thePlayer.motionX *= 1.019;
//                            mc.thePlayer.motionZ *= 1.019;
//                        }
//                    }
//
////                    if(tick == 6 && damaged){
////                        moveSpeed++;
////
////                        if(moveSpeed == 5) {
////                            MovementUtil.moveFlying(0.085);
////                            mc.thePlayer.motionY = motionY;
////                        }
////
////                        if(moveSpeed == 10){
////                            MovementUtil.moveFlying(-0.01);
////                        }
////
//////                        if(moveSpeed > 10)
//////                            mc.thePlayer.motionY += 0.025;
////                        //MovementUtil.strafe(MovementUtil.getSpeed() * 0.99);
////                    }
//
////                   if(tick == 6 && !damaged){
////                       moveSpeed++;
////                   }
//
////                    if (mc.thePlayer.ticksSinceVelocity <= 50 && mc.thePlayer.ticksSinceVelocity > 9) {
////
////                        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
////                            mc.thePlayer.motionX *= 1.038;
////                            mc.thePlayer.motionZ *= 1.038;
////                        } else {
////                            if (mc.thePlayer.ticksSinceVelocity == 12 || mc.thePlayer.ticksSinceVelocity == 13) {
////                                mc.thePlayer.motionX *= 1.1;
////                                mc.thePlayer.motionZ *= 1.1;
////                            }
////
////                            mc.thePlayer.motionX *= 1.019;
////                            mc.thePlayer.motionZ *= 1.019;
////                        }
////                    }
//                }
//            }
//        }
//    };
//
//    @Callback
//    final EventCallback<PlayerMoveUpdateEvent> playerMoveUpdateEventEventCallback = event -> {
//        if (watchdogMode.is("Normal")) {
//            if (tick < 4) {
//                event.setCancelled(true);
//                MovementUtil.stop();
//            } else if (mc.thePlayer.ticksSinceVelocity <= 19) {
//                MovementUtil.strafe();
//            }
//        }
//    };
}
