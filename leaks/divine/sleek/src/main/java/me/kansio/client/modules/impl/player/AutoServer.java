package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.ModeValue;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;

@ModuleData(
        name = "Auto Server",
        category = ModuleCategory.PLAYER,
        description = "Automatically does actions on certain servers"
)
public class AutoServer extends Module {

    private boolean hasClickedAutoPlay = false;
    private boolean hasSelectedKit = false;

    private boolean hasWorldChanged = false;

    private boolean hasClickedKitSelector = false;

    private ModeValue modeValue = new ModeValue("Server", this, "BlocksMC");
    private ModeValue kitValue = new ModeValue("Kit", this, modeValue, new String[]{"BlocksMC"},"Armorer", "Knight");

    @Subscribe
    public void onPacket(PacketEvent event) {
        switch (modeValue.getValueAsString()) {
            case "BlocksMC": {
                Packet packet = event.getPacket();

                if (event.getPacket() instanceof S2FPacketSetSlot) {
                    ItemStack item = ((S2FPacketSetSlot) event.getPacket()).func_149174_e();
                    int slot = ((S2FPacketSetSlot) event.getPacket()).func_149173_d();

                    //Make sure the item isn't null to prevent npe
                    if (item == null)
                        return;

                    if (item.getDisplayName() != null && item.getDisplayName().contains("Play Again")) {

                        //set the slot to the paper
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(7));

                        //right click on it
                        for (int i = 0; i < 2; i++) {
                            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(item));
                        }

                        hasWorldChanged = true;

                        //change the slot back to what it was.
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    }

                    //Auto Kit Select
                    if (!hasSelectedKit) {
                        if (item.getDisplayName() != null && item.getDisplayName().contains("Kit Selector")) {

                            //if an inventory is open, just return
                            if (mc.currentScreen != null) return;

                            //set the slot to the bow
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(0));

                            //right click on it
                            for (int i = 0; i < 2; i++) {
                                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(item));
                            }

                            //change the slot back to what it was.
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                    }
                }

                if (event.getPacket() instanceof S2DPacketOpenWindow) {
                    S2DPacketOpenWindow packetData = event.getPacket();

                    //automatic kit selector
                    if (packetData.getWindowTitle().getFormattedText().contains("Kits")) {

                        switch (kitValue.getValue()) {
                            case "Armorer": {
                                mc.playerController.windowClick(packetData.getWindowId(), 0, 1, 0, mc.thePlayer);
                                break;
                            }
                            case "Knight": {
                                mc.playerController.windowClick(packetData.getWindowId(), 18, 1, 0, mc.thePlayer);
                                break;
                            }
                        }

                        //set selected kit to true
                        hasSelectedKit = true;
                    }
                }
                break;
            }
            case "Hypixel": {
                break;
            }
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        //set autoplay click to false since the world changed.
        if (mc.thePlayer.ticksExisted < 5) {
            hasClickedAutoPlay = false;
            hasSelectedKit = false;
            hasWorldChanged = false;
            hasClickedKitSelector = false;
        }

        switch (modeValue.getValue()) {
            case "BlocksMC": {

                //auto select kit
                if (!hasSelectedKit && !hasClickedKitSelector && mc.thePlayer.ticksExisted > 5) {

                    for (int slot = 0; slot < mc.thePlayer.inventory.mainInventory.length; slot++) {
                        ItemStack currentItem = mc.thePlayer.inventory.getStackInSlot(slot);

                        if (currentItem == null) {
                            continue;
                        }

                        if (currentItem.getDisplayName().contains("Kit Selector")) {
                            //if an inventory is open, just return
                            if (mc.currentScreen != null) return;

                            //set the slot to the bow
                            //BlocksMC skywars teams has a skull in the first slot for picking your teammate,
                            //so check if it's a skull, if it is then click the second item in your hotbar
                            if (!(mc.thePlayer.inventory.getStackInSlot(0).getItem() instanceof ItemSkull)) {
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(0));
                            } else {
                                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(1));
                            }

                            //right click on it
                            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(currentItem));

                            //change the slot back to what it was.
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                    }
                }

                break;
            }
        }
    }
}
