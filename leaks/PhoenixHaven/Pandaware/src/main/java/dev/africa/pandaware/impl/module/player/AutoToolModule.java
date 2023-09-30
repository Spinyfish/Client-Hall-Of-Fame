package dev.africa.pandaware.impl.module.player;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.module.combat.KillAuraModule;
import dev.africa.pandaware.impl.module.movement.ScaffoldModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "AutoTool", description = "skidded from Sigma 4.11 L")
public class AutoToolModule extends Module {
    private final BooleanSetting autoSword = new BooleanSetting("AutoSword", false);

    public AutoToolModule() {
        this.registerSettings(this.autoSword);
    }

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    private float getDamage(ItemStack stack) {
        float damage = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
            damage += tool.getDamageVsEntity();
        }
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword) item;
            damage += sword.getAttackDamage();
        }
        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f +
                EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f;
        return damage;
    }

    private boolean isBestWeapon(ItemStack stack) {
        float damage = getDamage(stack);
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (getDamage(is) > damage && (is.getItem() instanceof ItemSword))
                    return false;
            }
        }
        return stack.getItem() instanceof ItemSword;
    }

    public void updateSword() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null) {
                continue;
            }
            if (itemStack.getItem() instanceof ItemSword && isBestWeapon(itemStack)) {
                mc.thePlayer.inventory.currentItem = i;
            }
        }
    }

    public static void updateTool(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        float strength = 1.0F;
        int bestItemIndex = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null) {
                continue;
            }
            if ((itemStack.getStrVsBlock(block) > strength)) {
                strength = itemStack.getStrVsBlock(block);
                bestItemIndex = i;
            }
        }
        if (bestItemIndex != -1) {
            mc.thePlayer.inventory.currentItem = bestItemIndex;
        }
    }

    @EventHandler
    EventCallback<TickEvent> onTick = event -> {
        KillAuraModule killAuraModule = Client.getInstance().getModuleManager().getByClass(KillAuraModule.class);
        ScaffoldModule scaffoldModule = Client.getInstance().getModuleManager().getByClass(ScaffoldModule.class);
        if (killAuraModule.getTarget() != null && autoSword.getValue() && mc.theWorld != null && mc.thePlayer != null &&
            !scaffoldModule.getData().isEnabled()) {
            updateSword();
        }
        if (!mc.gameSettings.keyBindAttack.isKeyDown() || mc.objectMouseOver == null) return;
        BlockPos pos = mc.objectMouseOver.getBlockPos();
        if (pos == null) return;
        updateTool(pos);
    };
}
