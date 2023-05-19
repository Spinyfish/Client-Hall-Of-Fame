package club.marsh.bloom.impl.mods.player;

import java.util.Arrays;
import java.util.List;

import com.viaversion.viaversion.protocols.protocol1_9to1_8.ArmorType;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.Render2DEvent;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;


public class InventoryManager extends Module {
	long lastTime = System.currentTimeMillis();
	public List<String> junkItems = Arrays.asList("stick", "egg", "string", "cake", "mushroom", "flint", "dyePowder", "feather", "chest", "snowball","fish", "enchant", "exp", "shears", "anvil", "torch", "seeds", "leather", "reeds", "skull", "record", "piston", "snow");
	public static NumberValue<Double> time = new NumberValue<Double>("Time",50D,50D,1500D,0);
	BooleanValue spoof = new BooleanValue("Spoof",true,() -> true);
	BooleanValue sort = new BooleanValue("Sort",true,() -> true);
	BooleanValue clean = new BooleanValue("Clean",true,() -> true);


	public InventoryManager() {
		super("InventoryManager",Keyboard.KEY_NONE,Category.PLAYER);
	}

	@Override
	public void onEnable() {
		lastTime = System.currentTimeMillis();
	}

	@Subscribe
	public void onRender2D(Render2DEvent e) {
		try {
			if (mc.thePlayer.openContainer instanceof ContainerChest || !(mc.currentScreen instanceof GuiInventory || spoof.isOn())) {
				lastTime = System.currentTimeMillis();
				return;
			}
			for (int i = 9; i < 45; ++i) {
				ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				sort(stack,i);
				dropJunk(stack,i);
			}
		} catch (Exception ex) { ex.printStackTrace(); }
	}


	public void sort(ItemStack stack, int i) {
		if (!sort.isOn())
			return;
		if ((stack == null || stack.getItem() instanceof ItemSword && !goodSword(stack)) && delayDone()) {
			switch (i) {
				case 36:
					int bestSwordSlot = sortForSword();
					if (bestSwordSlot != -1)
						mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, bestSwordSlot, 0, 2, mc.thePlayer);
					break;
				case 37:
					int bestPickaxeSlot = sortForPickaxe();
					if (bestPickaxeSlot != -1)
						mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, bestPickaxeSlot, 1, 2, mc.thePlayer);
					break;
				case 38:
					int bestGappleSlot = sortForGoldenApple();
					if (bestGappleSlot != -1)
						mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, bestGappleSlot, 2, 2, mc.thePlayer);
					break;
				case 39:
					int goldenAppleSlot = -1;
					for (int slot = 9; slot < 45; ++slot) {
						ItemStack stacker = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
						if (stacker == null) continue;
						if (stacker.getItem() instanceof ItemBlock) {
							goldenAppleSlot = slot;
						}
					}
					if (goldenAppleSlot != -1)
						mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, goldenAppleSlot, i - 36, 2, mc.thePlayer);
					break;
			}
		}
	}


	public void dropJunk(ItemStack stack, int i) {
		if (!clean.isOn())
			return;
		if (stack == null)
			return;
		if (stack.getUnlocalizedName().contains("legging"))
			return;
		for (String junkName : junkItems) {
			if ((stack.getUnlocalizedName().contains(junkName) || !goodSword(stack)) && delayDone()) {
				drop(i);
				lastTime = System.currentTimeMillis();
			}
		}
	}

	public static float swordDamage(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F + (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 1F;
    }

	public boolean goodSword(ItemStack compareStack) {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack stack = slot.getStack();

            if (stack != null && compareStack != stack && stack.getItem() instanceof ItemSword && compareStack.getItem() instanceof ItemSword) {
                ItemSword item = (ItemSword) stack.getItem();
                ItemSword compare = (ItemSword) compareStack.getItem();
                if (compare.attackDamage + swordDamage(compareStack) <= item.attackDamage + swordDamage(stack))
                     return false;
            }
        }

        return true;
    }

	public boolean goodPickaxe(ItemStack compareStack) {
		for (int i = 9; i < 45; i++) {
			Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
			ItemStack stack = slot.getStack();

			if (stack != null && compareStack != stack && stack.getItem() instanceof ItemPickaxe && compareStack.getItem() instanceof ItemPickaxe) {
				ItemPickaxe item = (ItemPickaxe) stack.getItem();
				ItemPickaxe compare = (ItemPickaxe) compareStack.getItem();
				if (compare.getStrVsBlock(compareStack, Blocks.diamond_ore) <= item.getStrVsBlock(stack, Blocks.diamond_ore))
					return false;
			}
		}

		return true;
	}

	public int sortForSword() { //This code will sort for the sword slot. (grabs sword slot uwu)
		int bestSwordSlot = -1;
		for (int slot = 9; slot < 45; ++slot) {
			ItemStack stacked = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
			if (stacked == null)
				continue;
			if (!goodSword(stacked))
				bestSwordSlot = slot;
		}
		if (bestSwordSlot == -1)
			for (int slot = 9; slot < 45; ++slot) {
				ItemStack stacked = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
				if (stacked == null)
					continue;
				if (stacked.getItem() instanceof ItemSword)
					bestSwordSlot = slot;
			}
		return bestSwordSlot;
	}

	public int sortForGoldenApple() {
		int goldenAppleSlot = -1;
		for (int slot = 9; slot < 45; ++slot) {
			ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
			if (stack == null) continue;
			if (stack.getItem() instanceof ItemAppleGold) {
				goldenAppleSlot = slot;
			}
		}
		return goldenAppleSlot;
	}

	public int sortForPickaxe() {
		int bestSwordSlot = -1;
		for (int slot = 9; slot < 45; ++slot) {
			ItemStack stacked = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
			if (stacked == null)
				continue;
			if (!goodPickaxe(stacked))
				bestSwordSlot = slot;
		}
		if (bestSwordSlot == -1)
			for (int slot = 9; slot < 45; ++slot) {
				ItemStack stacked = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
				if (stacked == null)
					continue;
				if (stacked.getItem() instanceof ItemPickaxe)
					bestSwordSlot = slot;
			}
		return bestSwordSlot;
	}

	public boolean isHotbar(int slot) {
		return slot >= 36;
	}

	public boolean delayDone() {
		return Math.abs(lastTime - System.currentTimeMillis()) > (time.value.doubleValue() + Math.random()*50);
	}

	public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }
}
