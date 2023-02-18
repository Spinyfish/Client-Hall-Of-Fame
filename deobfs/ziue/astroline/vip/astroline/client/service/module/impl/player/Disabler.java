/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.inventory.GuiInventory
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.C00PacketKeepAlive
 *  net.minecraft.network.play.client.C03PacketPlayer$C04PacketPlayerPosition
 *  net.minecraft.network.play.client.C03PacketPlayer$C06PacketPlayerPosLook
 *  net.minecraft.network.play.client.C0DPacketCloseWindow
 *  net.minecraft.network.play.client.C0EPacketClickWindow
 *  net.minecraft.network.play.client.C0FPacketConfirmTransaction
 *  net.minecraft.network.play.client.C16PacketClientStatus
 *  net.minecraft.network.play.client.C16PacketClientStatus$EnumState
 *  net.minecraft.network.play.server.S08PacketPlayerPosLook
 *  net.minecraft.util.MathHelper
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.event.impl.move.EventPreUpdate
 *  vip.astroline.client.service.event.impl.other.EventTick
 *  vip.astroline.client.service.event.impl.packet.EventReceivePacket
 *  vip.astroline.client.service.event.impl.packet.EventSendPacket
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.storage.utils.angle.Angle
 *  vip.astroline.client.storage.utils.angle.AngleUtility
 *  vip.astroline.client.storage.utils.other.TimeHelper
 */
package vip.astroline.client.service.module.impl.player;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.MathHelper;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.event.impl.move.EventPreUpdate;
import vip.astroline.client.service.event.impl.other.EventTick;
import vip.astroline.client.service.event.impl.packet.EventReceivePacket;
import vip.astroline.client.service.event.impl.packet.EventSendPacket;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.storage.utils.angle.Angle;
import vip.astroline.client.storage.utils.angle.AngleUtility;
import vip.astroline.client.storage.utils.other.TimeHelper;

public class Disabler
extends Module {
    public static AngleUtility angleUtility = new AngleUtility(110.0f, 120.0f, 30.0f, 40.0f);
    public static Angle lastAngle;
    public static float yawDiff;
    private short id;
    private int transactions;
    private List<Packet> listTransactions = new CopyOnWriteArrayList<Packet>();
    Queue<C0FPacketConfirmTransaction> confirmTransactionQueue = new ConcurrentLinkedQueue<C0FPacketConfirmTransaction>();
    Queue<C00PacketKeepAlive> keepAliveQueue = new ConcurrentLinkedQueue<C00PacketKeepAlive>();
    public static TimeHelper timer;
    int lastUid;
    int cancelledPackets;
    public static boolean hasDisabled;
    public CopyOnWriteArrayList<C0EPacketClickWindow> clickWindowPackets = new CopyOnWriteArrayList();
    public TimeHelper timedOutTimer = new TimeHelper();
    public boolean isCraftingItem = false;

    public Disabler() {
        super("Disabler", Category.Player, 0, false);
    }

    @EventTarget
    public void onSendPacket(EventSendPacket event) {
    }

    @EventTarget
    public void onUpdate(EventPreUpdate event) {
        if (Astroline.INSTANCE.moduleManager.getModule("Speed").isToggled()) {
            float targetYaw = event.getYaw();
            if (Disabler.mc.gameSettings.keyBindBack.pressed) {
                targetYaw += 180.0f;
                if (Disabler.mc.gameSettings.keyBindLeft.pressed) {
                    targetYaw += 45.0f;
                }
                if (Disabler.mc.gameSettings.keyBindRight.pressed) {
                    targetYaw -= 45.0f;
                }
            } else if (Disabler.mc.gameSettings.keyBindForward.pressed) {
                if (Disabler.mc.gameSettings.keyBindLeft.pressed) {
                    targetYaw -= 45.0f;
                }
                if (Disabler.mc.gameSettings.keyBindRight.pressed) {
                    targetYaw += 45.0f;
                }
            } else {
                if (Disabler.mc.gameSettings.keyBindLeft.pressed) {
                    targetYaw -= 90.0f;
                }
                if (Disabler.mc.gameSettings.keyBindRight.pressed) {
                    targetYaw += 90.0f;
                }
            }
            Angle angle = angleUtility.smoothAngle(new Angle(Float.valueOf(targetYaw), Float.valueOf(event.getYaw())), lastAngle, 120.0f, 360.0f);
            yawDiff = MathHelper.wrapAngleTo180_float((float)(targetYaw - angle.getYaw().floatValue()));
            event.setYaw(angle.getYaw().floatValue());
        }
        lastAngle = new Angle(Float.valueOf(event.getYaw()), Float.valueOf(event.getPitch()));
    }

    @EventTarget
    public void onTick(EventTick e) {
    }

    public void sendPacketWithoutEvent(Packet packet) {
        if (Disabler.mc.thePlayer == null) return;
        mc.getNetHandler().getNetworkManager().sendPacketWithoutEvent(packet);
    }

    public void doInvMove(EventSendPacket e) {
        C0EPacketClickWindow clickWindow;
        C16PacketClientStatus clientStatus;
        if (e.getPacket() instanceof C16PacketClientStatus && (clientStatus = (C16PacketClientStatus)e.getPacket()).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof C0DPacketCloseWindow) {
            C0DPacketCloseWindow closeWindow = (C0DPacketCloseWindow)e.getPacket();
            if (closeWindow.windowId == 0) {
                if (this.isCraftingItem) {
                    this.isCraftingItem = false;
                }
                e.setCancelled(true);
            }
        }
        if (e.getPacket() instanceof C0EPacketClickWindow && (clickWindow = (C0EPacketClickWindow)e.getPacket()).getWindowId() == 0) {
            if (!this.isCraftingItem && clickWindow.getSlotId() >= 1 && clickWindow.getSlotId() <= 4) {
                this.isCraftingItem = true;
            }
            if (this.isCraftingItem && clickWindow.getSlotId() == 0 && clickWindow.getClickedItem() != null) {
                this.isCraftingItem = false;
            }
            this.timedOutTimer.reset();
            e.setCancelled(true);
            this.clickWindowPackets.add(clickWindow);
        }
        boolean isDraggingItem = false;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiInventory && Minecraft.getMinecraft().thePlayer.inventory.getItemStack() != null) {
            isDraggingItem = true;
        }
        if (Disabler.mc.thePlayer.ticksExisted % 5 != 0) return;
        if (this.clickWindowPackets.isEmpty()) return;
        if (isDraggingItem) return;
        if (this.isCraftingItem) return;
        Astroline.INSTANCE.tellPlayer("Release Click Packets");
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        Iterator<C0EPacketClickWindow> iterator = this.clickWindowPackets.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)new C0DPacketCloseWindow(0));
                this.clickWindowPackets.clear();
                this.timedOutTimer.reset();
                return;
            }
            C0EPacketClickWindow clickWindowPacket = iterator.next();
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)clickWindowPacket);
        }
    }

    @EventTarget
    public void onPacket(EventReceivePacket e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)e.getPacket();
            Astroline.INSTANCE.tellPlayer("S08: " + packet.getX() + " " + packet.getY() + " " + packet.getZ());
        }
        if (!(e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook)) return;
        C03PacketPlayer.C06PacketPlayerPosLook c06 = (C03PacketPlayer.C06PacketPlayerPosLook)e.getPacket();
        e.setPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(c06.x, c06.y, c06.z, c06.isOnGround()));
        Astroline.INSTANCE.tellPlayer("C03: x: " + c06.x + " y: " + c06.y + " z: " + c06.z);
    }

    public void processConfirmTransactionPacket(EventSendPacket e) {
        C0FPacketConfirmTransaction packet = (C0FPacketConfirmTransaction)e.getPacket();
        int windowId = packet.getWindowId();
        short uid = packet.getUid();
        if (windowId != 0 || uid >= 0) {
            Astroline.INSTANCE.tellPlayer("Inventory synchronized!");
        } else {
            if (uid == --this.lastUid) {
                if (!hasDisabled) {
                    Astroline.INSTANCE.tellPlayer("Watchdog disabled.");
                    hasDisabled = true;
                }
                this.confirmTransactionQueue.offer(packet);
                e.setCancelled(true);
            }
            this.lastUid = uid;
        }
    }

    public void processKeepAlivePacket(EventSendPacket e) {
        C00PacketKeepAlive packet = (C00PacketKeepAlive)e.getPacket();
        if (!hasDisabled) return;
        this.keepAliveQueue.offer(packet);
        e.setCancelled(true);
    }

    public void processPlayerPosLooksPacket(EventSendPacket e) {
        if (hasDisabled) return;
        e.setCancelled(true);
    }

    static {
        timer = new TimeHelper();
    }
}
