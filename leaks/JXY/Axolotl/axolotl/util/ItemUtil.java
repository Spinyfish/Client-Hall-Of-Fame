package axolotl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;

public class ItemUtil {

	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static int getDamageReductionAmount(ItemStack var0) {
		
		try {
			ItemArmor var1 = (ItemArmor)var0.getItem();
			int var2 = 0;
			
	        var2 = var1.getArmorMaterial().getDamageReductionAmount(var1.armorType);
	        var2 += checkProtection(var0);
	        
	        return var2;
		} catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
		
	}
	
    public static int checkProtection(ItemStack var0) {
    	return EnchantmentHelper.getEnchantmentLevel(0, var0);
    }

	public static ItemStack getItemInArmorSlot(int i) {
		return getItemInSlot(39 - i);
	}
	
	public static ItemStack getItemInSlot(int i) {
		return mc.thePlayer.inventory.getStackInSlot(i);
	}

	public static int getFreeSlot() {
	      for(int var1 = 35; var1 > 0; --var1) {
	         ItemStack var2 = getItemInSlot(var1);
	         if (var2 == null) {
	            return var1;
	         }
	      }

	      return -1;
	   }

	public static ItemSword getBestSword() {
		ItemSword bestSword = null;
		float bestSwordStrength = 0;
		for(int i=0;i<36;i++) {
			ItemStack itemStack = getItemInSlot(i);
			if(itemStack == null)continue;
			Item item = itemStack.getItem();
			if(item instanceof ItemSword) {
				if(bestSword == null) {
					bestSword = (ItemSword)item;
					bestSwordStrength = getMineSpeed(itemStack);
				} else {
					float oldStrength = bestSwordStrength;
					bestSwordStrength = Math.max(getMineSpeed(itemStack), bestSwordStrength);
					bestSword = oldStrength >= bestSwordStrength ? bestSword : (ItemSword) item;
				}
			}
		}
		return bestSword;
	}
	
	public static ItemTool[] getBestTools() {
		ItemAxe bestAxe = null;
		float bestAxeStrength = 0;
		ItemPickaxe bestPickaxe = null;
		float bestPickaxeStrength = 0;
		ItemSpade bestShovel = null;
		float bestShovelStrength = 0;
		for(int i=0;i<36;i++) {
			ItemStack itemStack = getItemInSlot(i);
			if(itemStack == null)continue;
			Item item = itemStack.getItem();
			if(item instanceof ItemTool) {
				if(item instanceof ItemAxe) {
					if(bestAxe == null) {
						bestAxe = (ItemAxe)item;
						bestAxeStrength = getMineSpeed(itemStack);
					} else {
						float oldStrength = bestAxeStrength;
						bestAxeStrength = Math.max(getMineSpeed(itemStack), bestAxeStrength);
						bestAxe = oldStrength >= bestAxeStrength ? bestAxe : (ItemAxe) item;
					}
				} else if(item instanceof ItemPickaxe) {
					if(bestPickaxe == null) {
						bestPickaxe = (ItemPickaxe)item;
						bestPickaxeStrength = getMineSpeed(itemStack);
					} else {
						float oldStrength = bestPickaxeStrength;
						bestPickaxeStrength = Math.max(getMineSpeed(itemStack), bestPickaxeStrength);
						bestPickaxe = oldStrength >= bestPickaxeStrength ? bestPickaxe : (ItemPickaxe) item;
					}
				} else if(item instanceof ItemSpade) {
					if(bestShovel == null) {
						bestShovel = (ItemSpade)item;
						bestShovelStrength = getMineSpeed(itemStack);
					} else {
						float oldStrength = bestShovelStrength;
						bestShovelStrength = Math.max(getMineSpeed(itemStack), bestShovelStrength);
						bestShovel = oldStrength >= bestShovelStrength ? bestShovel : (ItemSpade) item;
					}
				}

			}
		}
		return new ItemTool[] {bestAxe, bestPickaxe, bestShovel};
	}
	
	public static float getMineSpeed(ItemStack item) {
		
		if(item == null)return 0;
		
		try {
			
			if(item.getItem() instanceof ItemTool) {
				
				return getDigspeedOfItem(item);
				
			}
			
		} catch(Exception e) {
			return 0;
		}
		
		return 0;
		
	}
	
	public static float getDigspeedOfItem(ItemStack itemStack) {
		
		Item item = itemStack.getItem();
		
		int strength = getStrengthOfMaterial(((ItemTool)item).toolMaterial);
		
		if(item.getRarity(itemStack) == EnumRarity.COMMON) return strength; // unenchanted -> return the item
		
		float effLevel = itemStack.getEnchantmentTagList().getFloat(Enchantment.efficiency.effectId);
		
		return effLevel + strength;
		
	}
	
	public static int getStrengthOfMaterial(Item.ToolMaterial tM) {
		
		switch(tM) {
		
			case DIAMOND:
				return 4 ;
		
			case IRON:
				return 3;
		
			case GOLD:
				return 2;
		
			case STONE:
				return 2;
				
			case WOOD:
				return 1;
				
			default:
				return 0;
				
		}
		
	}

	public static ItemStack betterArmor(ItemStack armor1, ItemStack armor2) {
        int firstProtection = ItemUtil.getDamageReductionAmount(armor1);
        int secondProtection = ItemUtil.getDamageReductionAmount(armor2);
        
        return firstProtection >= secondProtection ? armor1 : armor2;
	}
	
}
