package me.kansio.client.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.UpdateEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.value.value.NumberValue;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.network.PacketUtil;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleData(
        name = "Fast Bow",
        category = ModuleCategory.COMBAT,
        description = "Shoots your bow faster"
)
public class FastBow extends Module {

    private boolean wasShooting = false;
    private int lastSlot;

    private int serverSideSlot;

    private NumberValue<Integer> packets = new NumberValue<>("Packets", this, 20, 0, 1000, 1);
    private BooleanValue value = new BooleanValue("Hold Bow", this, true);

    private ModeValue mode = new ModeValue("Mode", this, "Ghostly", "Vanilla");

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {

                if (value.getValue()) {
                    if (mc.thePlayer.onGround && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow && mc.gameSettings.keyBindUseItem.pressed) {
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                        switch (mode.getValue()) {
                            case "Vanilla": {
                                //send the funny packets
                                for (int i = 0; i < packets.getValue(); i++) {
                                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer());
                                }
                                break;
                            }
                            case "Ghostly": {
                                if (mc.thePlayer.ticksExisted % 6 == 0) {
                                    double d = mc.thePlayer.posX;
                                    double d2 = mc.thePlayer.posY + 1.0E-9;
                                    double d3 = mc.thePlayer.posZ;
                                    for (int i = 0; i < packets.getValue(); i++) {
                                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(d, d2, d3, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                                    }
                                }
                                break;

                            }

                        }
                        PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                } else {
                    //check if they have a bow
                    if (!hasBow()) {
                        return;
                    }


                    int slotWithBow = getBowSlot();

                    //this shouldn't happen
                    if (slotWithBow == -1) {
                        return;
                    }

                    serverSideSlot = slotWithBow;


                    serverSideSlot = slotWithBow;

                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slotWithBow));

                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(slotWithBow)));

                    switch (mode.getValue()) {
                        case "Vanilla": {
                            //send the funny packets
                            for (int i = 0; i < packets.getValue().intValue(); i++) {
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer());
                            }
                            break;
                        }
                        case "Ghostly": {
                            if (mc.thePlayer.ticksExisted % 5 == 0) {
                                double d = mc.thePlayer.posX;
                                double d2 = mc.thePlayer.posY + 1.0E-9;
                                double d3 = mc.thePlayer.posZ;
                                for (int i = 0; i < packets.getValue(); i++) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(d, d2, d3, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                                }
                            }
                            break;
                        }
                    }

                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    wasShooting = true;
                }
            } else if (wasShooting) { //revert to the last itemslot
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(lastSlot));
                serverSideSlot = lastSlot;
                wasShooting = false;
                ChatUtil.log("revert");
            }
        }
    }

    public boolean hasBow() {
        for (int i = 0; i < 8; i++) {
            if (mc.thePlayer.inventory == null) continue;
            if (mc.thePlayer.inventory.getStackInSlot(i) == null) continue;
            if (mc.thePlayer.inventory.getStackInSlot(i).getItem() == null) continue;

            if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBow) {
                return true;
            }
        }

        return false;
    }

    //returns -1 if it can't find a bow
    public int getBowSlot() {
        for (int i = 0; i < 8; i++) {
            if (mc.thePlayer.inventory.getStackInSlot(i) != null) {
                if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBow) {
                    return i;
                }
            }
        }

        return -1;
    }
}