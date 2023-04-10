package crispy.features.hacks.impl.combat;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.time.TimeHelper;
import lombok.Getter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;

@HackInfo(name = "AutoPot", category = Category.COMBAT)
public class AutoPot extends Hack {
    TimeHelper stopwatch = new TimeHelper();
    TimeHelper timer = new TimeHelper();
    BooleanValue spoof = new BooleanValue("Spoof Inventory", false);
    NumberValue<Double> requiredHealth = new NumberValue<Double>("Required HealthPots", 10D, 5D, 20D);
    NumberValue<Long> grabDelay = new NumberValue<Long>("Grab Delay", 50L, 1L, 1000L);
    NumberValue<Long> potDelay = new NumberValue<Long>("Pot Delay", 50L, 1L, 1000L);
    @Getter
    private boolean isPotting;
    private boolean inventoryOpen;
    private int lastSlot;

    @Override
    public void onEnable() {
        timer.reset();
        stopwatch.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        timer.reset();
        stopwatch.reset();
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + potDelay.getObject());
            if (getPotionCount() != 0 && !(mc.currentScreen instanceof GuiContainer)) {


                if (timer.hasReached(grabDelay.getObject())) {
                    timer.reset();

                    if (!hotbarHasPots()) {
                        isPotting = false;
                        getPotsFromInventory();

                    }

                }
                if (hotbarHasPots()) {

                    splashPot((EventUpdate) e);

                }


            } else {
                isPotting = false;
            }
        }
    }

    public void openInvPacket() {

        if (!inventoryOpen)
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));

        inventoryOpen = true;
    }

    public void closeInvPacket() {
        if (inventoryOpen)
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));

        inventoryOpen = false;
    }

    public int getPotionCount() {
        int i = 0;
        for (int i1 = 9; i1 < 45; i1++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
            if (itemStack != null)
                if (isItemHealthPotion(itemStack) || isItemSpeedPotion(itemStack))
                    i += itemStack.stackSize;
        }
        return i;
    }


    private boolean isItemHealthPotion(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) itemStack.getItem();
            if (potion.hasEffect(itemStack))
                for (Object o : potion.getEffects(itemStack)) {
                    PotionEffect effect = (PotionEffect) o;
                    if (effect.getEffectName().equals("potion.heal"))
                        return true;
                }
        }
        return false;
    }

    private boolean hotbarHasPots() {
        boolean found = false;
        for (int i1 = 36; i1 < 45; i1++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();

            if (itemStack != null) {
                boolean splash = ItemPotion.isSplash(itemStack.getItemDamage());
                if (splash) {
                    if (isItemHealthPotion(itemStack) || isItemSpeedPotion(itemStack))
                        found = true;
                }
            }
        }
        return found;
    }

    private boolean isItemSpeedPotion(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) itemStack.getItem();
            if (potion.hasEffect(itemStack))
                for (Object o : potion.getEffects(itemStack)) {
                    PotionEffect effect = (PotionEffect) o;
                    if (effect.getEffectName().equals("potion.moveSpeed") || effect.getEffectName().equals("potion.regeneration")) {

                        return true;
                    }
                }
        }
        return false;
    }

    private void getPotsFromInventory() {
        if (spoof.getObject()) {
            openInvPacket();
        }
        int item = -1;
        boolean found = false;
        boolean splash = false;
        for (int i1 = 36; i1 >= 9; i1--) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
            if (itemStack != null) {
                if (isItemHealthPotion(itemStack) || isItemSpeedPotion(itemStack)) {
                    item = i1;

                    found = true;
                    splash = ItemPotion.isSplash(itemStack.getItemDamage());
                }
            }
        }
        if (found) {
            if (splash) {
                for (int i1 = 0; i1 < 45; i1++) {
                    ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
                    if (itemStack != null)
                        if ((itemStack.getItem() == Items.glass_bottle) && (i1 >= 36) && (i1 <= 44)) {
                            minecraft.playerController.windowClick(0, i1, 0, 0, minecraft.thePlayer);
                            minecraft.playerController.windowClick(0, -999, 0, 0, minecraft.thePlayer);
                        }
                }
                minecraft.playerController.windowClick(0, item, 0, 1, minecraft.thePlayer);
            }

        }
    }

    private void splashPot(EventUpdate e) {

        int item = -1;
        boolean found = false;
        boolean splash = false;

        for (int i1 = 36; i1 < 45; i1++) {
            ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(i1).getStack();
            if (itemStack != null) {
                splash = ItemPotion.isSplash(itemStack.getItemDamage());
                if (isItemHealthPotion(itemStack) && mc.thePlayer.getHealth() <= requiredHealth.getObject() || isItemSpeedPotion(itemStack) && splash) {
                    isPotting = true;
                    e.setPitch(90);
                    item = i1;
                    found = true;

                    break;
                }
            }
        }

        lastSlot = mc.thePlayer.inventory.currentItem;
        if (found) {
            if (splash) {

                if (stopwatch.hasReached(potDelay.getObject()) && Crispy.INSTANCE.getServerRotation().getPitch() == 90) {

                    minecraft.thePlayer.inventory.currentItem = (item - 36);
                    minecraft.playerController.updateController();

                    minecraft.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1),
                            -1, minecraft.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));

                    stopwatch.reset();
                    if (spoof.getObject())
                        closeInvPacket();
                    mc.thePlayer.inventory.currentItem = lastSlot;
                    isPotting = false;
                    minecraft.playerController.updateController();

                }
            }
        }
    }


}
