package client.module.impl.player;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;

import client.event.impl.motion.UpdateEvent;
import client.event.impl.packet.PacketSendEvent;
import client.module.Category;
import client.module.Module;

import client.module.ModuleInfo;
import client.util.player.MoveUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.*;


@ModuleInfo(name = "Scaffold", description = "", category = Category.PLAYER)
public class Scaffold extends Module {
    private int ticks;
    private int placedBlocks;
    int i;
    float f1;
    float f2;
    float f3;
    @Override
    protected void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0F;
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        mc.timer.timerSpeed = 1.0F;
    }

    @EventLink
    public final Listener<UpdateEvent> onPreUpdate = event -> {
        MoveUtil.strafe(0.18);
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.setSprinting(false);
        double d1 = mc.thePlayer.posX;
        double d2 = mc.thePlayer.posY;
        double d3 = mc.thePlayer.posZ;
        if (d1 < 0) {
            d1 = d1 - 1;
        }
        if (d3 < 0) {
            d3 = d3 - 1;
        }
        i = mc.thePlayer.getHorizontalFacing().getIndex();
        f1 = 0.0F;
        f2 = 0.0F;
        f3 = 0.0F;
        if (i == 2) {
            f1 = (float) (0.49F + Math.random() / 50);
            f2 = (float) (0.49F + Math.random() / 50);
            f3 = 0.0F;
        }
        if (i == 3) {
            f1 = (float) (0.49F + Math.random() / 50);
            f2 = (float) (0.49F + Math.random() / 50);
            f3 = 1.0F;
        }
        if (i == 4) {
            f1 = 0.0F;
            f2 = (float) (0.49F + Math.random() / 50);
            f3 = (float) (0.49F + Math.random() / 50);
        }
        if (i == 5) {
            f1 = 1.0F;
            f2 = (float) (0.49F + Math.random() / 50);
            f3 = (float) (0.49F + Math.random() / 50);
        }
        int i1 = (int) d1;
        int i2 = (int) d2;
        int i3 = (int) d3;
        if (mc.theWorld.getBlockState(new BlockPos(i1, i2 - 1, i3)).getBlock() == Blocks.air) {
            mc.theWorld.setBlockState(new BlockPos(i1, i2 - 1, i3), Blocks.stone.getDefaultState());
            if (i == 2) {
                i3 = i3 + 1;
            }
            if (i == 3) {
                i3 = i3 - 1;
            }
            if (i == 4) {
                i1 = i1 + 1;
            }
            if (i == 5) {
                i1 = i1 - 1;
            }
            if (placedBlocks % 2 == 0) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(i1, i2 - 1, i3), i, mc.thePlayer.getHeldItem(), f1, f2, f3));
            placedBlocks++;
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
    };

    @EventLink
    public final Listener<MotionEvent> gay = event -> {
        float yaw = 360.0F * (int) (event.rotationYaw / 360.0F);
        if (i == 2 || i == 3) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                if (i == 2) {
                    event.rotationYaw = yaw - f1 * 3.0F;
                }
                if (i == 3) {
                    event.rotationYaw = yaw - 180.0F - f1 * 3.0F;
                }
            } else {
                if (i == 2) {
                    event.rotationYaw = yaw + f1 * 3.0F;
                }
                if (i == 3) {
                    event.rotationYaw = yaw - 180.0F + f1 * 3.0F;
                }
            }
        }
        if (i == 4 || i == 5) {
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                if (i == 4) {
                    event.rotationYaw = yaw - 90.0F - f3 * 3.0F;
                }
                if (i == 5) {
                    event.rotationYaw = yaw + 90.0F - f3 * 3.0F;
                }
            } else {
                if (i == 4) {
                    event.rotationYaw = yaw - 90.0F + f3 * 3.0F;
                }
                if (i == 5) {
                    event.rotationYaw = yaw + 90.0F + f3 * 3.0F;
                }
            }
        }
        event.rotationPitch = 86.0F;
    };

    @EventLink
    public final Listener<PacketSendEvent> onPacketSend = event -> {
        Packet packet = event.getPacket();
        if (packet instanceof C03PacketPlayer) {
            C03PacketPlayer r = (C03PacketPlayer) packet;
            float yaw = 360.0F * (int) (r.getYaw() / 360.0F);
            if (i == 2 || i == 3) {
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    if (i == 2) {
                        r.yaw = yaw - f1 * 3.0F;
                    }
                    if (i == 3) {
                        r.yaw = yaw - 180.0F - f1 * 3.0F;
                    }
                } else {
                    if (i == 2) {
                        r.yaw = yaw + f1 * 3.0F;
                    }
                    if (i == 3) {
                        r.yaw = yaw - 180.0F + f1 * 3.0F;
                    }
                }
            }
            if (i == 4 || i == 5) {
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    if (i == 4) {
                        r.yaw = yaw - 90.0F - f3 * 3.0F;
                    }
                    if (i == 5) {
                        r.yaw = yaw + 90.0F - f3 * 3.0F;
                    }
                } else {
                    if (i == 4) {
                        r.yaw = yaw - 90.0F + f3 * 3.0F;
                    }
                    if (i == 5) {
                        r.yaw = yaw + 90.0F + f3 * 3.0F;
                    }
                }
            }
            r.pitch = 86.0F;
        }
        /*if (packet instanceof C0CPacketInput) {
            C0CPacketInput c0C = (C0CPacketInput) packet;
            if (c0C.getForwardSpeed() > 0.0F) {
                c0C.setForwardSpeed(-c0C.getForwardSpeed());
            }
        }
        if (packet instanceof C03PacketPlayer) {
            C03PacketPlayer c03 = (C03PacketPlayer) packet;
            c03.setYaw(c03.getYaw() - 180.0F);
            c03.setPitch(86.0F);
        }*/
    };
}