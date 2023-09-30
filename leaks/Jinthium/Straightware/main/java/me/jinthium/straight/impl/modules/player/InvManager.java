package me.jinthium.straight.impl.modules.player;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.player.BlockUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.RandomUtils;

public class InvManager extends Module {

    private static final int WEAPON_SLOT = 36;
    private static final int PICKAXE_SLOT = 37;
    private static final int AXE_SLOT = 38;
    private static final int SHOVEL_SLOT = 39;
    private final TimerUtil timer = new TimerUtil();

    private final ModeSetting cleanMode = new ModeSetting("Mode", "Normal", "Open", "Silent", "Normal");
    private final NumberSetting delay = new NumberSetting("Delay", 75, 0, 1000, 1);
    private final NumberSetting randomMax = new NumberSetting("Random Max", 50, 0, 1000, 1);
    private final NumberSetting randomMin = new NumberSetting("Random Min", 0, 0, 1000, 1);
    private final NumberSetting blockCap = new NumberSetting("Block Cap", 512, 64, 1024, 1);
    private final BooleanSetting stopWhenCleaning = new BooleanSetting("Stop when cleaning", false);
    private final BooleanSetting random = new BooleanSetting("Randomization", false);
    private final BooleanSetting archery = new BooleanSetting("Clean Bows-Arrows", true);
    private final BooleanSetting food = new BooleanSetting("Clean food", true);
    private final BooleanSetting sword = new BooleanSetting("Prefer swords", true);
    private final BooleanSetting keepEmpty = new BooleanSetting("Keep Empty", false);


    public long lastClean;

    public InvManager() {
        super("InvManager", 0, Category.PLAYER);
        this.addSettings(cleanMode, delay, randomMin, randomMax, blockCap, stopWhenCleaning, random, archery, food, sword, keepEmpty);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        this.setSuffix(delay.getValue().toString());
        AutoArmor autoArmor = Client.INSTANCE.getModuleManager().getModule(AutoArmor.class);
        if (autoArmor.isEnabled() && (System.currentTimeMillis() - autoArmor.lastCycle) < 150) return;
        final long time = delay.getValue().intValue() + (random.isEnabled() ? RandomUtils.nextInt(randomMin.getValue().intValue(), randomMax.getValue().intValue()) : 0);

        if ((this.cleanMode.is("Open") && !(mc.currentScreen instanceof GuiInventory) &&
                !this.stopWhenCleaning.isEnabled()) || this.stopWhenCleaning.isEnabled() &&
                (mc.thePlayer.isMoving() || mc.thePlayer.moveForward > 0 || mc.thePlayer.moveStrafing > 0 ||
                        mc.gameSettings.keyBindJump.pressed))
            return;

        if (this.cleanMode.is("Fake") && (mc.thePlayer.isMoving() || mc.gameSettings.keyBindJump.pressed))
            return;

        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (timer.hasTimeElapsed(time)) {
                if (!mc.thePlayer.inventoryContainer.getSlot(WEAPON_SLOT).getHasStack()) {
                    getBestWeapon(WEAPON_SLOT);
                } else {
                    if (!isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(WEAPON_SLOT).getStack())) {
                        getBestWeapon(WEAPON_SLOT);
                    }
                }
            }

            if (timer.hasTimeElapsed(time))
                getBestPickaxe();

            if (timer.hasTimeElapsed(time))
                getBestShovel();

            if (timer.hasTimeElapsed(time))
                getBestAxe();

            if (timer.hasTimeElapsed(time)) {
                for (int i = 9; i < 45; i++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if (shouldDrop(is, i)) {
                            drop(i);
                            timer.reset();
                            lastClean = System.currentTimeMillis();
                            if (time > 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    };

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
        lastClean = System.currentTimeMillis();
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
        lastClean = System.currentTimeMillis();
    }

    public boolean isBestWeapon(ItemStack stack) {
        float damage = getDamage(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getDamage(is) > damage && (is.getItem() instanceof ItemSword || !sword.isEnabled()))
                    return false;
            }
        }
        return stack.getItem() instanceof ItemSword || !sword.isEnabled();
    }

    public void getBestWeapon(int slot) {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (isBestWeapon(is) && getDamage(is) > 0 && (is.getItem() instanceof ItemSword || !sword.isEnabled())) {
                    swap(i, slot - 36);
                    timer.reset();
                    break;
                }
            }
        }
    }

    private float getDamage(ItemStack stack) {
        float damage = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool tool) {
            damage += tool.getDamageVsEntity();
        }
        if (item instanceof ItemSword swordItem) {
            damage += swordItem.getAttackDamage();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f +
                EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
        return damage;
    }

    public boolean shouldDrop(ItemStack stack, int slot) {
        String itemName = stack.getItem().getUnlocalizedName().toLowerCase();
        if (stack.getDisplayName().toLowerCase().contains("(right click)")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("profil")) {
            return false;
        }
        if (stack.getDisplayName().toLowerCase().contains("§k||")) {
            return false;
        }
        if ((slot == WEAPON_SLOT && isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(WEAPON_SLOT).getStack())) ||
                (slot == PICKAXE_SLOT && isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(PICKAXE_SLOT).getStack()) && PICKAXE_SLOT >= 0) ||
                (slot == AXE_SLOT && isBestAxe(mc.thePlayer.inventoryContainer.getSlot(AXE_SLOT).getStack()) && AXE_SLOT >= 0) ||
                (slot == SHOVEL_SLOT && isBestShovel(mc.thePlayer.inventoryContainer.getSlot(SHOVEL_SLOT).getStack()) && SHOVEL_SLOT >= 0)) {
            return false;
        }
        if (stack.getItem() instanceof ItemBlock &&
                (getBlockCount() > blockCap.getValue().intValue() ||
                        BlockUtils.INVALID_BLOCKS.contains(((ItemBlock) stack.getItem()).getBlock()))) {
            return true;
        }

        if (stack.getItem() instanceof ItemPotion) {
            return isBadPotion(stack);
        }

        if (stack.getItem() instanceof ItemArmor)
            return false;

        if (stack.getItem() instanceof ItemFood && food.isEnabled() && !(stack.getItem() instanceof ItemAppleGold || stack.getItem() instanceof ItemFood)) {
            return true;
        }

        if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword) {
            return true;
        }

        if ((stack.getItem() instanceof ItemBow || itemName.contains("arrow")) && archery.isEnabled()) {
            return true;
        }

        if (stack.getItem() instanceof ItemDoublePlant) {
            return true;
        }

        if ((itemName.contains("bowl") ||
                (itemName.contains("bucket") && !itemName.contains("water") &&
                        !itemName.contains("lava") && !itemName.contains("milk")) ||
                (stack.getItem() instanceof ItemGlassBottle && !keepEmpty.isEnabled())) && !keepEmpty.isEnabled()) {
            return true;
        }
        return (itemName.contains("tnt")) ||
                (itemName.contains("stick")) ||
                (itemName.contains("egg")) ||
                (itemName.contains("string")) ||
                (itemName.contains("cake")) ||
                (itemName.contains("mushroom") && !itemName.contains("stew")) ||
                (itemName.contains("flint")) ||
                (itemName.contains("dyepowder")) ||
                (itemName.contains("feather")) ||
                (itemName.contains("chest") && !stack.getDisplayName().toLowerCase().contains("collect")) ||
                (itemName.contains("snow")) ||
                (itemName.contains("enchant")) ||
                (itemName.contains("exp")) ||
                (itemName.contains("shears")) ||
                (itemName.contains("anvil")) ||
                (itemName.contains("torch")) ||
                (itemName.contains("seeds")) ||
                (itemName.contains("beacon")) ||
                (itemName.contains("flower")) ||
                (itemName.contains("leather")) ||
                (itemName.contains("reeds")) ||
                (itemName.contains("skull")) ||
                (itemName.contains("record")) ||
                (itemName.contains("snowball")) ||
                (itemName.contains("slab")) ||
                (itemName.contains("stair")) ||
                (itemName.contains("piston"));
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && !BlockUtils.INVALID_BLOCKS.contains(((ItemBlock) item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
        }
        return blockCount;
    }

    private void getBestPickaxe() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (isBestPickaxe(is) && PICKAXE_SLOT != i) {
                    if (!isBestWeapon(is)) {
                        if (!mc.thePlayer.inventoryContainer.getSlot(PICKAXE_SLOT).getHasStack()) {
                            swap(i, PICKAXE_SLOT - 36);
                            timer.reset();
                            if (delay.getValue().longValue() > 0)
                                return;
                        } else if (!isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(PICKAXE_SLOT).getStack())) {
                            swap(i, PICKAXE_SLOT - 36);
                            timer.reset();
                            if (delay.getValue().longValue() > 0)
                                return;
                        }
                    }
                }
            }
        }
    }

    private void getBestShovel() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (isBestShovel(is) && SHOVEL_SLOT != i) {
                    if (!isBestWeapon(is)) {
                        if (!mc.thePlayer.inventoryContainer.getSlot(SHOVEL_SLOT).getHasStack()) {
                            swap(i, SHOVEL_SLOT - 36);
                            timer.reset();
                            if (delay.getValue().longValue() > 0)
                                return;
                        } else if (!isBestShovel(mc.thePlayer.inventoryContainer.getSlot(SHOVEL_SLOT).getStack())) {
                            swap(i, SHOVEL_SLOT - 36);
                            timer.reset();
                            if (delay.getValue().longValue() > 0)
                                return;
                        }
                    }
                }
            }
        }
    }

    private void getBestAxe() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (isBestAxe(is) && AXE_SLOT != i) {
                    if (!isBestWeapon(is)) {
                        if (!mc.thePlayer.inventoryContainer.getSlot(AXE_SLOT).getHasStack()) {
                            swap(i, AXE_SLOT - 36);
                            timer.reset();
                            if (delay.getValue().longValue() > 0)
                                return;
                        } else if (!isBestAxe(mc.thePlayer.inventoryContainer.getSlot(AXE_SLOT).getStack())) {
                            swap(i, AXE_SLOT - 36);
                            timer.reset();
                            if (delay.getValue().longValue() > 0)
                                return;
                        }
                    }
                }
            }
        }
    }

    private boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBestShovel(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemSpade))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemSpade) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isBestAxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemAxe))
            return false;
        float value = getToolEffect(stack);
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    private float getToolEffect(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemTool tool))
            return 0;
        String name = item.getUnlocalizedName();
        float value;
        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else if (item instanceof ItemAxe) {
            value = tool.getStrVsBlock(stack, Blocks.log);
            if (name.toLowerCase().contains("gold")) {
                value -= 5;
            }
        } else
            return 1f;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D;
        value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100d;
        return value;
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion potion) {
            if (potion.getEffects(stack) == null && !potion.hasEffect(stack)) {
                return true;
            }
            for (final PotionEffect o : potion.getEffects(stack)) {
                if (o.getPotionID() == Potion.poison.getId() || o.getPotionID() == Potion.harm.getId() || o.getPotionID() == Potion.moveSlowdown.getId() || o.getPotionID() == Potion.weakness.getId()) {
                    return true;
                }
            }
        }
        return false;
    }
}