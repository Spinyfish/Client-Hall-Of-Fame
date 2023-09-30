package dev.echo.module.impl.player;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.network.PacketSendEvent;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.player.SlowDownEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.combat.KillAura;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.utils.player.ChatUtil;
import dev.echo.utils.player.MovementUtils;
import dev.echo.utils.server.PacketUtils;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.LinkedList;

public class NoSlow extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Vanilla", "BlocksMC", "Watchdog", "Switch","Vulcan");
    private static final LinkedList<Packet<?>> packets = new LinkedList<>();

    private int ticks = 0;
    ;

    public NoSlow() {
        super("NoSlow", Category.PLAYER, "prevent item slowdown");
        this.addSettings(mode);
    }

    @Override
    public void onEnable() {
        packets.clear();
        super.onEnable();
    }

    @Link
    public final Listener<SlowDownEvent> slowDownEventListener = event -> {
        event.setCancelled(true);
    };

    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Watchdog":
                if (e.isPre()) {
                    if (MovementUtils.isMoving()) {
                        if (mc.thePlayer.isBlocking() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                            PacketUtils.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        }
                    }
                }
                break;
            case "BlocksMC":
                if (MovementUtils.isMoving()) {
                    if (e.isPre()) {
                        if (mc.thePlayer.isEating() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                            PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getCurrentEquippedItem()));
                        }
                    }
                }
                break;
            case "Switch":
                if (MovementUtils.isMoving()) {
                    if (e.isPre()) {
                        if (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) {
                            PacketUtils.sendPacket(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                            PacketUtils.sendPacket(new C09PacketHeldItemChange());
                        }
                    }
                }
                break;
        }
    };
    @Link
    public Listener<PacketSendEvent> onPacketSend = e -> {
        if (mc.thePlayer == null && mc.theWorld == null) {
            return;
        }
        this.setSuffix(mode.getMode());
        switch (mode.getMode()) {
            case "Watchdog":
                if (e.isPre()) {
                    // TODO: FIX THIS

                    if (mc.thePlayer != null && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood) {
                        if (mc.thePlayer.isUsingItem()) {
                            if (ticks < 20) {
                                if (e.getPacket() instanceof C03PacketPlayer) {
                                    //packets.add(e.getPacket());
                                    ticks++;
                                    ChatUtil.print("cancel" + ticks);
                                    e.setCancelled(true);
                                }
                            }
                        }
                        if (ticks == 20 && !mc.thePlayer.isUsingItem()) {
                            ticks = 0;
                        }

                        // if(ticks >= 13) {
                        //   while (!packets.isEmpty()) {
                        //       ChatUtil.print("reset");
                        //        PacketUtils.sendPacket(packets.poll());
                        //     }
                        // }
                        //   packets.clear();

                    }
                }
                break;
            case "Vulcan": {
                PacketUtils.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                if (mc.thePlayer.isBlocking() && ((e.getPacket() instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging) e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) || e.getPacket() instanceof C08PacketPlayerBlockPlacement)) {
                    e.setCancelled(true);
                }
            }
        }
    };
}
