package tech.dort.dortware.impl.modules.combat;

import com.google.common.eventbus.Subscribe;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

public class AutoApple extends Module {

    private final Stopwatch timer = new Stopwatch();
    private int ticks, oldSlot;
    private final NumberValue minHealth = new NumberValue("Min Health", this, 12, 5, 20, true);
    private final NumberValue delay = new NumberValue("Delay", this, 150, 50, 1000, SliderUnit.MS, true);

    public AutoApple(ModuleData moduleData) {
        super(moduleData);
        register(minHealth, delay);
    }


    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.getHealth() < minHealth.getValue() && timer.timeElapsed(delay.getValue().longValue())) {
            if (event.isPre()) {
                if (hotBarHasApples()) {
                    useApple();
                } else {
                    getAppleFromInventory();
                }
            }
            if (ticks >= 3) {
                timer.resetTime();
                ticks = 0;
            }
        }
    }

    private boolean hotBarHasApples() {
        for (int index = 36; index < 45; ++index) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemAppleGold)) {
                continue;
            }
            return true;
        }
        return false;
    }

    private void getAppleFromInventory() {
        int index;
        ItemStack itemStack;
        int item = -1;
        boolean found = false;
        for (index = 36; index >= 9; --index) {
            itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemAppleGold)) {
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

    private void useApple() {
        int index;
        ItemStack itemStack;
        int item = -1;
        boolean found = false;
        for (index = 36; index < 45; ++index) {
            itemStack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemAppleGold)) {
                continue;
            }
            item = index;
            found = true;
            break;
        }
        if (found) {
            item -= 36;
            int temp = mc.thePlayer.inventory.currentItem;
            ticks++;
            if (ticks == 1) {
                PacketUtil.sendPacket(new C09PacketHeldItemChange(item));
                PacketUtil.sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            } else if (ticks == 2) {
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(item + (item != 8 ? 1 : -1)));
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(item));
            } else {
                PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            oldSlot = temp;
        }
    }
}
