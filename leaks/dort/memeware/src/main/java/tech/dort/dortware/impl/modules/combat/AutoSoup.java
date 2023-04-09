package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

public class AutoSoup extends Module {

    private final Stopwatch timer = new Stopwatch();

    private final NumberValue minHealth = new NumberValue("Min Health", this, 12, 5, 20, true);
    private final NumberValue delay = new NumberValue("Delay", this, 150, 50, 1000, SliderUnit.MS, true);

    public AutoSoup(ModuleData moduleData) {
        super(moduleData);
        register(minHealth, delay);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.getHealth() < minHealth.getValue() && timer.timeElapsed(delay.getValue().longValue())) {
            if (hotBarHasSoups()) {
                useSoup();
            } else {
                getSoupFromInventory();
            }
            timer.resetTime();
        }
    }

    private boolean hotBarHasSoups() {
        for (int index = 36; index < 45; ++index) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemSoup)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void getSoupFromInventory() {
        int index;
        ItemStack itemStack;
        int item = -1;
        boolean found = false;
        for (index = 36; index >= 9; --index) {
            itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemSoup)) {
                continue;
            }
            item = index;
            found = true;
            break;
        }
        if (found) {
            for (index = 0; index < 45; ++index) {
                itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
                if (itemStack == null) {
                    continue;
                }
                if (itemStack.getItem() != Items.bowl || index < 36) {
                    break;
                }
                mc.playerController.windowClick(0, index, 0, 0, mc.thePlayer);
                mc.playerController.windowClick(0, -1, 0, 0, mc.thePlayer);
                break;
            }
            mc.playerController.windowClick(0, item, 0, 1, mc.thePlayer);
        }
    }

    private void useSoup() {
        int index;
        ItemStack itemStack;
        int item = -1;
        boolean found = false;
        for (index = 36; index < 45; ++index) {
            itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemSoup)) {
                continue;
            }
            item = index;
            found = true;
            break;
        }
        if (found) {
            for (index = 0; index < 45; ++index) {
                itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
                if (itemStack == null || itemStack.getItem() != Items.bowl || index < 36) {
                    continue;
                }
                mc.playerController.windowClick(0, index, 0, 0, mc.thePlayer);
                mc.playerController.windowClick(0, -1, 0, 0, mc.thePlayer);
            }
            final int slot = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.inventory.currentItem = item - 36;
            mc.playerController.updateController();
            PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            mc.thePlayer.stopUsingItem();
            mc.thePlayer.inventory.currentItem = slot;
        }
    }
}
