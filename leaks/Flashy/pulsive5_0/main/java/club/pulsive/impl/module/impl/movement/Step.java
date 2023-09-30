package club.pulsive.impl.module.impl.movement;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.network.PacketEvent;
import club.pulsive.impl.event.player.BlockStepEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.exploit.NoFall;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.MovementUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "Step", category = Category.MOVEMENT)

public class Step extends Module {
    private boolean cancelMorePackets;
    private byte cancelledPackets;
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil lastStep = new TimerUtil();
    private boolean resetTimer;

    @Override
    public void onDisable() {
        mc.thePlayer.stepHeight = 0.5f;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        timer.reset();
        lastStep.reset();
        super.onEnable();
    }


    @EventHandler
    private final Listener<PacketEvent> packetEventListener = event -> {
        if(event.getEventState() == PacketEvent.EventState.SENDING) {
            if(event.getPacket() instanceof C03PacketPlayer) {
                if (cancelledPackets > 0) {
                    cancelMorePackets = false;
                    cancelledPackets = 0;
                    mc.timer.timerSpeed = 1.0f;
                }
                if (cancelMorePackets) {
                    cancelledPackets++;
                }
            }
        }
    };


    @EventHandler
    private final Listener<BlockStepEvent> blockStepEventListener = e -> {

        if(mc.thePlayer == null || Pulsive.INSTANCE.getModuleManager().getModule(Speed.class).isToggled()) {
            return;
        }
        if(!mc.thePlayer.onGround) {
            return;
        }
        if (resetTimer) {
            resetTimer = false;
        }

        if (!mc.thePlayer.isInWater())
            if (e.isPre()) {
                if (mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.isKeyDown() && timer.hasElapsed(350)) {
                    e.setHeight(1);
                    timer.reset();
                }
                if(Pulsive.INSTANCE.getModuleManager().getModule(Speed.class).isToggled() ||Pulsive.INSTANCE.getModuleManager().getModule(LongJump.class).isToggled()) {
                    e.setHeight(0.5f);
                }
            } else {
          
                    double diffY = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
                    double posX = mc.thePlayer.posX, posY = mc.thePlayer.posY, posZ = mc.thePlayer.posZ;
                if (diffY > 0.625 && diffY <= 1.0) {
                        mc.timer.timerSpeed = 0.3f;
                        resetTimer = true;

                        double first = 0.41999998688698, second = 0.7531999805212;

                        if (diffY != 1) {
                            first *= diffY;
                            second *= diffY;

                            if (first > 0.425) {
                                first = 0.425;
                            }

                            if (second > 0.78) {
                                second = 0.78;
                            } else if (second < 0.49) {
                                second = 0.49;
                            }
                        }
                        step(e.getHeight());
                        mc.thePlayer.stepHeight = 0.625f;
                        cancelMorePackets = true;
                }
            }

    };

    private void step(double height) {
         if(Pulsive.INSTANCE.getModuleManager().getModule(Speed.class).isToggled()) {
             height = 0;
         }
        List<Double> offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
        double posX = mc.thePlayer.posX;
        double posZ = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;
        if (height < 1.1) {
            double first = 0.42;
            double second = 0.75;
            if (height != 1) {
                first *= height;
                second *= height;
                if (first > 0.425) {
                    first = 0.425;
                }
                if (second > 0.78) {
                    second = 0.78;
                }
                if (second < 0.49) {
                    second = 0.49;
                }
            }
            if (first == 0.42)
                first = 0.41999998688698;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
            if (y + second < y + height)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
        } else if (height < 1.6) {
            for (double off : offset) {
                y += off;
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
            }
        } else if (height < 2.1) {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
            for (double off : heights) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        } else {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
            for (double off : heights) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + 1, posZ, false));
    }
        private enum MODES {
            Hypixel,
            Vanilla
        }
}
