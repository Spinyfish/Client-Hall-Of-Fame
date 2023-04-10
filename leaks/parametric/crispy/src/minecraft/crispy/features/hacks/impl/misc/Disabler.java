package crispy.features.hacks.impl.misc;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.player.PlayerUtil;
import crispy.util.time.TimeHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Timer;
import net.superblaubeere27.valuesystem.ModeValue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@HackInfo(name = "Disabler", category = Category.MISC)
public class Disabler extends Hack {
    private final ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
    public ModeValue modeValue = new ModeValue("Mode", "Larkus", "Larkus", "Verus Combat", "Verus", "COC", "NullPing", "Redesky", "Matrix Config", "Pinecone", "SpookyAC", "KeepAlive", "WarAC", "GhostBlock");
    private final TimeHelper tpTimer = new TimeHelper();
    private final TimeHelper transactionTimer = new TimeHelper();
    private double flyHeight;
    private final ArrayList<C03PacketPlayer> flyTrap = new ArrayList<>();
    private final ArrayList<C0FPacketConfirmTransaction> transactions = new ArrayList<>();
    private boolean destroyVerus;
    private boolean destroyPinecone;
    private int count;

    @Override
    public void onEnable() {
        tpTimer.reset();
        flyTrap.clear();
        destroyVerus = false;
        destroyPinecone = true;
        transactionTimer.reset();
        if (modeValue.getMode().equalsIgnoreCase("Verus Combat")) {
            NotificationPublisher.queue("[Verus Combat]", "Disabling verus combat please wait", NotificationType.INFO);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        tpTimer.reset();
        flyTrap.clear();
        Timer.timerSpeed = 1;
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + modeValue.getMode());

            if (modeValue.getMode().equalsIgnoreCase("Verus")) {
                if (mc.thePlayer.ticksExisted % 10 == 0) {

                    flyTrap.forEach(packet -> {

                        mc.thePlayer.sendQueue.addToSendNoEvent(packet);
                    });
                    flyTrap.clear();
                }
            }

            if (modeValue.getMode().equalsIgnoreCase("Verus Combat")) {
                if (transactionTimer.hasReached(8000)) {
                    if (Minecraft.theWorld != null) {
                        for (C0FPacketConfirmTransaction c0f : transactions) {
                            mc.thePlayer.sendQueue.addToSendNoEvent(c0f);

                        }
                        transactions.clear();
                        if (!destroyVerus) {
                            destroyVerus = true;
                            NotificationPublisher.queue("[Verus Combat]", "Verus combat is now disabled", NotificationType.SUCCESS);
                        }
                    }
                }
            }
            if (modeValue.getMode().equalsIgnoreCase("C0C")) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, mc.thePlayer.movementInput.jump, mc.thePlayer.movementInput.sneak));
            }
        }
        if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
            switch (modeValue.getMode()) {
                case "Larkus": {
                    if(Minecraft.theWorld != null) {
                        if (packet instanceof C03PacketPlayer) {
                            e.setCancelled(true);
                            updateFlyHeight();
                            goToGround();

                        }
                        if (packet instanceof S08PacketPlayerPosLook) {
                            e.setCancelled(true);
                            S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) ((EventPacket) e).getPacket();

                            mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(s08PacketPlayerPosLook.getX(), s08PacketPlayerPosLook.getY(), s08PacketPlayerPosLook.getZ(), s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), mc.thePlayer.onGround));
                        }
                        if(packet instanceof S02PacketChat) {
                            S02PacketChat s02PacketChat = (S02PacketChat) packet;
                            if(!s02PacketChat.getChatComponent().getUnformattedText().contains(mc.thePlayer.getCommandSenderName()))
                            {
                                e.setCancelled(true);
                            }
                        }
                    }
                    break;
                }
                case "Verus Combat": {

                    if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                        e.setCancelled(true);
                    }
                    if (packet instanceof C0BPacketEntityAction) {
                        e.setCancelled(true);
                    }
                    break;
                }
                case "WarAC": {
                    if (packet instanceof C17PacketCustomPayload) {
                        C17PacketCustomPayload pay = (C17PacketCustomPayload) packet;
                        if (pay.getChannelName().equalsIgnoreCase("MC|Brand")) {
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            ByteBuf message = Unpooled.buffer();
                            Crispy.addChatMessage("WARAC DISABLED");
                            message.writeBytes("warac:exempt".getBytes());
                            mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("warac:exempt", new PacketBuffer(message)));
                        }
                    }

                    break;

                }
                case "GhostBlock": {
                    if (packet instanceof C08PacketPlayerBlockPlacement) {
                        e.setCancelled(true);
                    }
                    break;
                }
                case "Verus": {
                    if (packet instanceof C03PacketPlayer) {
                        if (PlayerUtil.isMoving2()) {
                            if (mc.thePlayer.ticksExisted % 60 != 0) {
                                e.setCancelled(true);

                                flyTrap.add((C03PacketPlayer) packet);
                            }
                        }
                    }
                    if (packet instanceof S08PacketPlayerPosLook) {
                        Crispy.addChatMessage("Sex on tick " + mc.thePlayer.ticksExisted);
                    }
                    if (packet instanceof C0BPacketEntityAction) {
                        e.setCancelled(true);
                    }
                    if (packet instanceof C00PacketKeepAlive) {
                        C00PacketKeepAlive c00PacketKeepAlive = (C00PacketKeepAlive) packet;
                        c00PacketKeepAlive.setKey(-1);

                    }
                    if (Minecraft.theWorld != null) {

                        if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {
                            e.setCancelled(true);
                            new Thread(() -> {
                                try {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException interruptedException) {
                                        interruptedException.printStackTrace();
                                    }
                                    mc.thePlayer.sendQueue.addToSendNoEvent(packet);
                                } catch (Exception e2) {
                                }


                            }).start();
                        }
                    } else {
                        setState(false);
                    }
                    break;
                }
                case "NullPing": {
                    if (Minecraft.theWorld != null) {
                        if (packet instanceof C0FPacketConfirmTransaction) {
                            e.setCancelled(true);
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C0FPacketConfirmTransaction());
                            mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, true, true));
                        }
                        if (packet instanceof C0BPacketEntityAction) {
                            e.setCancelled(true);
                        }
                        if (packet instanceof C03PacketPlayer) {
                            if (mc.thePlayer.ticksExisted % 10 == 0) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, true, true));
                                e.setCancelled(true);
                                mc.thePlayer.sendQueue.addToSendNoEvent(new C00PacketKeepAlive());
                            }
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C0CPacketInput());

                        }
                    }
                    break;
                }
                case "Redesky": {

                    if (packet instanceof C03PacketPlayer) {
                        if (mc.thePlayer.ticksExisted % 15 == 0) {
                            mc.thePlayer.sendQueue.addToSendNoEvent(new C0CPacketInput());
                        } else {
                            if (mc.thePlayer.ticksExisted % 2 == 0) {

                                mc.thePlayer.sendQueue.addToSendNoEvent(new C10PacketCreativeInventoryAction());
                                mc.thePlayer.sendQueue.addToSendNoEvent(new C00PacketKeepAlive());

                            }
                            if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                                if (mc.thePlayer.ticksExisted % 50 == 0) {
                                    mc.thePlayer.sendQueue.addToSendNoEvent(new C13PacketPlayerAbilities());
                                    e.setCancelled(true);


                                }
                            }

                            if (PlayerUtil.isMoving2()) {
                                if (mc.thePlayer.ticksExisted % 2 == 0) {
                                    if (packet instanceof S08PacketPlayerPosLook) {
                                        S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook) packet;
                                        double x = s08PacketPlayerPosLook.getX();
                                        double y = s08PacketPlayerPosLook.getY();
                                        double z = s08PacketPlayerPosLook.getZ();
                                        double distancex = Math.abs(Math.abs(mc.thePlayer.posX) - Math.abs(x));
                                        double distancey = Math.abs(Math.abs(mc.thePlayer.posY) - Math.abs(y));
                                        double distancez = Math.abs(Math.abs(mc.thePlayer.posZ) - Math.abs(z));

                                        double distance = Math.sqrt(distancex * distancex + distancey * distancey + distancez * distancez);
                                        if (distance > 15) {
                                            mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), true));

                                            e.setCancelled(false);
                                        } else {
                                            e.setCancelled(true);
                                            mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(x, y, z, s08PacketPlayerPosLook.getYaw(), s08PacketPlayerPosLook.getPitch(), true));
                                        }
                                    }

                                    if (packet instanceof C0FPacketConfirmTransaction || packet instanceof C00PacketKeepAlive) {

                                        e.setCancelled(true);
                                    }
                                }
                            } else {
                                mc.thePlayer.sendQueue.addToSendNoEvent(new C13PacketPlayerAbilities());

                                e.setCancelled(false);

                            }
                        }
                    }
                    break;
                }
                case "Matrix Config": {
                    if (packet instanceof C03PacketPlayer) {
                        if (mc.thePlayer.ticksExisted % 12 == 0) {
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(b);
                            PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
                            buf.writeBytes(b.toByteArray());
                            mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("matrix:geyser", buf));

                            e.setCancelled(false);
                        } else {
                            if (mc.thePlayer.ticksExisted % 5 == 0) {
                                e.setCancelled(true);

                                mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, true, true));
                            }
                        }
                    }
                    if (packet instanceof C17PacketCustomPayload) {
                        C17PacketCustomPayload pay = (C17PacketCustomPayload) packet;
                        if (pay.getChannelName().equalsIgnoreCase("MC|Brand")) {
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            ByteBuf message = Unpooled.buffer();
                            message.writeBytes("matrix:geyser".getBytes());
                            mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(message)));
                        }
                    }

                    break;
                }
                case "Pinecone": {
                    if (packet instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                        e.setCancelled(true);
                        mc.thePlayer.sendQueue.addToSendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
                    }
                    if (packet instanceof C08PacketPlayerBlockPlacement) {
                        e.setCancelled(true);

                    }
                    break;
                }
                case "SpookyAC": {
                    if (packet instanceof C00PacketKeepAlive) {
                        C00PacketKeepAlive c00PacketKeepAlive = (C00PacketKeepAlive) packet;
                        c00PacketKeepAlive.setKey(999999);
                    }
                    if (packet instanceof C03PacketPlayer) {
                        count++;
                        if (count >= 4) {
                            count = 0;
                            e.setCancelled(true);
                        }
                    }
                    if (packet instanceof S00PacketKeepAlive) {
                        e.setCancelled(true);
                    }
                    break;
                }
                case "KeepAlive": {
                    if (packet instanceof S00PacketKeepAlive) {
                        e.setCancelled(true);
                        S00PacketKeepAlive s00PacketKeepAlive = (S00PacketKeepAlive) packet;

                        thread.schedule(() ->
                                mc.thePlayer.sendQueue.addToSendQueue(new C00PacketKeepAlive(s00PacketKeepAlive.func_149134_c())), 5000, TimeUnit.MILLISECONDS
                        );
                    }
                    if (packet instanceof C0FPacketConfirmTransaction) {
                        e.setCancelled(true);
                    }
                    break;
                }

            }


        }
    }

    public void updateFlyHeight() {
        double h = 1.0D;
        AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(0.0625D, 0.0625D, 0.0625D);

        for (this.flyHeight = 0.0D; this.flyHeight < mc.thePlayer.posY; this.flyHeight += h) {
            AxisAlignedBB nextBox = box.offset(0.0D, -this.flyHeight, 0.0D);
            if (Minecraft.theWorld.checkBlockCollision(nextBox)) {
                if (h < 0.0625D) {
                    break;
                }

                this.flyHeight -= h;
                h /= 2.0D;
            }
        }

    }

    public void goToGround() {
        if (this.flyHeight <= 300.0D) {
            double minY = mc.thePlayer.posY - this.flyHeight;
            if (minY > 0.0D) {
                double y = mc.thePlayer.posY;

                C03PacketPlayer.C04PacketPlayerPosition packet;
                while (y > minY) {
                    y -= 100.0D;
                    if (y < minY) {
                        y = minY;
                    }

                    packet = new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true);
                    mc.thePlayer.sendQueue.addToSendNoEvent(packet);
                }

                y = minY;
                //No going upward

            }
        }
    }

}
