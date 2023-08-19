package cc.swift.module.impl.movement;

import cc.swift.events.EventState;
import cc.swift.events.UpdateEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.util.PacketUtil;
import cc.swift.util.player.MovementUtil;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.item.ItemFireball;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public class LongJumpModule extends Module {

    public LongJumpModule() {
        super("LongJump", Category.MOVEMENT);
        registerValues(mode, mult1, mult2, glide1, glide2);
    }

    public final ModeValue<LJMode> mode = new ModeValue<>("Mode", LJMode.values());

    // I can't think of better names rn
    public final DoubleValue mult1 = new DoubleValue("Multiply 1", 3.85, 1.025, 4, 0.05).setDependency(() -> (this.mode.getValue() == LJMode.WATCHDOG_FIREBALL));
    public final DoubleValue mult2 = new DoubleValue("Multiply 2", 1.1, 1.025, 4, 0.05).setDependency(() -> (this.mode.getValue() == LJMode.WATCHDOG_FIREBALL));
    public final DoubleValue glide1 = new DoubleValue("Glide 1", 0.041, 0.001, 0.1, 0.001).setDependency(() -> (this.mode.getValue() == LJMode.WATCHDOG_FIREBALL));
    public final DoubleValue glide2 = new DoubleValue("Glide 2", 0.0274, 0.001, 0.1, 0.001).setDependency(() -> (this.mode.getValue() == LJMode.WATCHDOG_FIREBALL));

    int ticksWithFireball = 0;
    int fireballSlot;
    int lastSlot;

    int stage;

    @Override
    public void onEnable() {
        if (mode.getValue() == LJMode.WATCHDOG_FIREBALL) {
            findFireball();
        }
        ticksWithFireball = 0;
        stage = 0;
    }

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateEventListener = event -> {
        if (event.getState() == EventState.PRE) {
            switch (mode.getValue()) {
                case VANILLA:
                    if (stage != 0 && MovementUtil.isOnGround()) {
                        this.toggle();
                        break;
                    }

                    if (MovementUtil.isMoving()) {
                        if (MovementUtil.isOnGround()) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionY = 0.8;
                            MovementUtil.setSpeed(4);
                            stage++;
                        }
                        MovementUtil.setSpeed(MovementUtil.getSpeed());
                    }

                    break;

                case WATCHDOG_FIREBALL:

                    if (ticksWithFireball == 0) {
                        fireballSlot = findFireball();
                        if (fireballSlot == -1) {
                            ChatUtil.printChatMessage("No Fireball found");
                            this.toggle();
                            break;
                        }
                        lastSlot = mc.thePlayer.inventory.currentItem;
                        if (fireballSlot != lastSlot) {
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(fireballSlot));
                        }
                        event.setPitch(90f);
                    } else if (ticksWithFireball == 1) {
                        event.setPitch(90f);
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(fireballSlot)));
                        if (fireballSlot != lastSlot) {
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(lastSlot));
                        }
                        stage = 1;
                    }

                    if (mc.thePlayer.hurtTime == 9 && stage == 1) {
                        mc.thePlayer.motionX *= mult1.getValue();
                        mc.thePlayer.motionZ *= mult1.getValue();
                        stage++;
                    } else if (mc.thePlayer.hurtTime == 5 && stage == 2) {
                        mc.thePlayer.motionX *= mult2.getValue();
                        mc.thePlayer.motionZ *= mult2.getValue();
                        stage++;
                    }
                    if (mc.thePlayer.hurtResistantTime != 0) {
                        mc.thePlayer.motionY += 0.01;
                    }
                    if (mc.thePlayer.fallDistance >= 0.1 && mc.thePlayer.fallDistance < 0.6) {
                        ChatUtil.printChatMessage(mc.thePlayer.fallDistance);
                        mc.thePlayer.motionY += glide1.getValue();
                    } else if (mc.thePlayer.fallDistance > 0.6) {
                        if (stage == 3) {
                            //MovementUtil.setSpeed(MovementUtil.getSpeed() * 1.275);
                            stage++;
                        }
                        mc.thePlayer.motionY += glide2.getValue();
                    }

                    if (stage >= 2 && mc.thePlayer.onGround) {
                        this.toggle();
                    }

                    ticksWithFireball++;
                    break;
            }
        }
    };

    public int findFireball() {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemFireball) {
                return i;
            }
        }
        return -1;
    }

    public void throwFireball(int slot) {
    }

    enum LJMode {
        VANILLA, WATCHDOG_FIREBALL
    }
}
