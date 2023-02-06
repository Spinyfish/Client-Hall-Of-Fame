package dev.elysium.client.mods.impl.movement;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventPacket;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoSlowDown extends Mod {
    public ModeSetting mode = new ModeSetting("Mode",this,"Vanilla","NCP","Hypixel","AAC","Fake","Spam");
    public ModeSetting vanillamode = new ModeSetting("Timing",this,"PRE","POST");
    public ModeSetting ncpmode = new ModeSetting("NCP Mode",this,"Old","New");
    public NoSlowDown() {
        super("NoSlowDown","Modifies your received slowdown", Category.MOVEMENT);
        vanillamode.setPredicate(mod -> mode.is("Vanilla"));
    }

    @EventTarget
    public void onEventPacket(EventPacket e) {
        Packet p = e.getPacket();

        if(p instanceof S30PacketWindowItems && mode.is("NCP") && ncpmode.is("New") && (mc.thePlayer.isBlocking())) {
            e.setCancelled(true);
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255,
                    mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
        }
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        switch (mode.getMode()) {
            case "Vanilla":
                if(vanillamode.is("POST") && e.isPost() && mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                }
                break;
            case "NCP":
                if(mc.thePlayer.isBlocking())
                    if (e.isPost())
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    else if(ncpmode.is("Old"))
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                break;
            case "Hypixel":
                if(mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking())
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255,
                            mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
                break;
            case "AAC":
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange());
                break;
            case "Fake":
                break;
            case "Spam":
                if(mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                }
                break;
        }
    }
}
