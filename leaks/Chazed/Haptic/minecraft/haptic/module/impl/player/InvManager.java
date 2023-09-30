package haptic.module.impl.player;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventUpdate;
import haptic.module.Category;
import haptic.module.Module;
import haptic.module.impl.combat.AutoArmor;
import haptic.setting.impl.ModeSetting;
import haptic.setting.impl.NumberSetting;
import haptic.util.misc.TimerUtil;
import haptic.util.world.BlockUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class InvManager extends Module {
	
	private TimerUtil timer = new TimerUtil();
	ArrayList<Integer> whitelistedItems = new ArrayList<>();
	
	private int weaponSlot = 36, pickaxeSlot = 37, axeSlot = 38, shovelSlot = 39;
	
	private ModeSetting mode = new ModeSetting("Mode", "Basic", "Basic", "OpenInv");
	private NumberSetting maxBlocks = new NumberSetting("Max blocks", 256, 64, 512, 32, this);
	private NumberSetting delay = new NumberSetting("Delay", 100, 0, 300, 10, this);
	
	private AutoArmor autoArmorInstance;
	
	public InvManager() {
		super("InvManager", Category.PLAYER, Keyboard.KEY_NONE);
		this.addSettings(mode, maxBlocks, delay);
	}
	
	public void onEvent(Event event) {
		if(autoArmorInstance == null) {
			autoArmorInstance = ((AutoArmor) Haptic.getModuleManager().getModuleByName("AutoArmor"));
		}
		if(event instanceof EventUpdate) {
            if(timer.hasReached((long) delay.getValue()) && autoArmorInstance.isEnabled()) {
            	 if(!autoArmorInstance.invOpen.isEnabled() || mc.currentScreen instanceof GuiInventory) {
                     if(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
                    	 getBestArmor();
                     }
                 }
            }
            if(autoArmorInstance.isEnabled()) {
            	for(int type = 1; type < 5; type++){
            		if(mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()){
            			ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
            			if(!autoArmorInstance.isBestArmor(is, type)){
            				return;
            			}
            		}else if(invContainsType(type-1)){
            			return;
            		}
                }
            }
            
            if(mode.is("OpenInv") && !(mc.currentScreen instanceof GuiInventory)) {
            	return;
            }
            
            if(mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            	if(timer.hasReached((long) delay.getValue()) && weaponSlot >= 36){         

            		if (!mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getHasStack()){
            			getBestWeapon(weaponSlot);
                	
            		}else{
            			if(!isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())){           			
                			getBestWeapon(weaponSlot);
                		}
            		}
            	}
            	if(timer.hasReached((long) delay.getValue()) && pickaxeSlot >= 36){
            		getBestPickaxe(pickaxeSlot);
            	}
            	if(timer.hasReached((long) delay.getValue()) && shovelSlot >= 36){
            		getBestShovel(shovelSlot);
            	}
            	if(timer.hasReached((long) delay.getValue()) && axeSlot >= 36){
            		getBestAxe(axeSlot);
            	}
            	if(timer.hasReached((long) delay.getValue()))
                for (int i = 9; i < 45; i++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if(shouldDrop(is, i)) {
                        	drop(i);
                        	timer.reset();
                        	if(delay.getValue() > 0)
                        	break;
                        }
                    }
                }              
            }
		}
	}
	
	public void shiftClick(int slot) {
    	mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, mc.thePlayer);
    }
    public void swap(int slot1, int hotbarSlot) {
    	mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }
    public void drop(int slot) {
    	mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }
    public boolean isBestWeapon(ItemStack stack){
    	float damage = getDamage(stack);
    	
    	for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getDamage(is) > damage && (is.getItem() instanceof ItemSword))
                	return false;
            }
        }
    	if((stack.getItem() instanceof ItemSword)) {
    		return true;
    	}else{
    		return false;
    	}
    	
    }

    public void getBestWeapon(int slot){
    	for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(isBestWeapon(is) && getDamage(is) > 0 && (is.getItem() instanceof ItemSword)) {
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
    	if(item instanceof ItemTool){
    		ItemTool tool = (ItemTool) item;
    		damage += tool.attackDamage;
    	}
    	if(item instanceof ItemSword){
    		ItemSword sword = (ItemSword) item;
    		damage += sword.attackDamage;
    	}
    	damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f + 
    			EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 2f;
    	if(stack.getItemDamage() * 3 > stack.getMaxItemUseDuration()) {
    		damage -= 3;
    	}
        return damage;
    }
    public boolean shouldDrop(ItemStack stack, int slot) {
    	if(stack.getDisplayName().toLowerCase().contains("(right click)")){
    		return false;
    	}
    	if(stack.getDisplayName().toLowerCase().contains("§k||")){
    		return false;
    	}
    	if(stack != null && stack.getItem() instanceof ItemSword) {
    		//return false;
    	}
    	
    	if(stack.getItem() instanceof ItemFishingRod) {
    		return true;
    	}
    	
    	if((slot == weaponSlot && isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) ||
    			(slot == pickaxeSlot && isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack()) && pickaxeSlot >= 0) ||
    			(slot == axeSlot && isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack()) && axeSlot >= 0) ||
    			(slot == shovelSlot && isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack()) && shovelSlot >= 0) ){
    		return false;
    	}
    	if(stack.getItem() instanceof ItemArmor) {
    		for(int type = 1; type < 5; type++){
    			if(mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()){
        			ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
        			if(autoArmorInstance.isBestArmor(is, type)){
        				continue;
        			}
        		}
    			if(autoArmorInstance.isBestArmor(stack, type)){
    				return false;
    			}
    		}
    	}
    	if (stack.getItem() instanceof ItemBlock && (getBlockCount() > maxBlocks.getValue() || BlockUtil.blockBlacklist.contains(((ItemBlock)stack.getItem()).getBlock()))) {
    		return true;
    	}
    	if(stack.getItem() instanceof ItemPotion) {
    		if(isBadPotion(stack)) {
    			return true;
    		}
    	}
    	
    	
    	if(stack.getItem() instanceof ItemFood && !(stack.getItem() instanceof ItemAppleGold)) {
    		//return true;
    	}
    	if(stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemArmor) {
    		return true;
    	}
    	if((stack.getItem() instanceof ItemBow || stack.getItem().getUnlocalizedName().contains("arrow"))) {
    		//return true;
    	}
    	
    	if(((stack.getItem().getUnlocalizedName().contains("tnt")) ||
                        (stack.getItem().getUnlocalizedName().contains("stick")) ||
                        (stack.getItem().getUnlocalizedName().contains("egg")) ||
                        (stack.getItem().getUnlocalizedName().contains("string")) ||
                        (stack.getItem().getUnlocalizedName().contains("cake")) ||
                        (stack.getItem().getUnlocalizedName().contains("mushroom")) ||
                        (stack.getItem().getUnlocalizedName().contains("flint")) ||
                        (stack.getItem().getUnlocalizedName().contains("feather")) ||
                        //(stack.getItem().getUnlocalizedName().contains("bucket")) ||
                        (stack.getItem().getUnlocalizedName().contains("shears")) ||
                        (stack.getItem().getUnlocalizedName().contains("torch")) ||
                        (stack.getItem().getUnlocalizedName().contains("seeds")) ||
                        (stack.getItem().getUnlocalizedName().contains("reeds")) ||
                        (stack.getItem().getUnlocalizedName().contains("record")) ||
                        (stack.getItem().getUnlocalizedName().contains("snowball")) ||
                        (stack.getItem() instanceof ItemGlassBottle) ||
                        (stack.getItem().getUnlocalizedName().contains("piston")))){
    		return true;
    	}            
    	
    	return false;
    }
    public ArrayList<Integer>getWhitelistedItem(){
    	return whitelistedItems;
    }
    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && !BlockUtil.blockBlacklist.contains(((ItemBlock) item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
        }
        return blockCount;
    }
    
    private void getBestPickaxe(int slot){
    	for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				
				if(isBestPickaxe(is) && pickaxeSlot != i){
					if(!isBestWeapon(is))
					if(!mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getHasStack()){	
						swap(i, pickaxeSlot - 36);
						timer.reset();
    					if(delay.getValue() > 0)
    						return;
					}else if(!isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack())){
						swap(i, pickaxeSlot - 36);
						timer.reset();
    					if(delay.getValue() > 0)
    						return;
					}
				
				}
			}
    	}
    }
    private void getBestShovel(int slot){
    	for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				
				if(isBestShovel(is) && shovelSlot != i){
					if(!isBestWeapon(is))
					if(!mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getHasStack()){
						swap(i, shovelSlot - 36);
						timer.reset();
    					if(delay.getValue() > 0)
    						return;
					}else if(!isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack())){	
						swap(i, shovelSlot - 36);
						timer.reset();
    					if(delay.getValue() > 0)
    						return;
					}
				
				}
			}
    	}
    }
    private void getBestAxe(int slot){

    	for (int i = 9; i < 45; i++) {
			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				
				if(isBestAxe(is) && axeSlot != i){
					if(!isBestWeapon(is))
					if(!mc.thePlayer.inventoryContainer.getSlot(axeSlot).getHasStack()){
						swap(i, axeSlot - 36);
						timer.reset();
    					if(delay.getValue() > 0)
    						return;
					}else if(!isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack())){
						swap(i, axeSlot - 36);
						timer.reset();
    					if(delay.getValue() > 0)
    						return;
					}
				
				}
			}
    	}
    }
    private boolean isBestPickaxe(ItemStack stack){
     	Item item = stack.getItem();
    	if(!(item instanceof ItemPickaxe))
    		return false;
    	float value = getToolEffect(stack);
    	for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getToolEffect(is) > value && is.getItem() instanceof ItemPickaxe){                	
                	return false;
                }
                	
            }
        }
    	return true;
    }
    private boolean isBestShovel(ItemStack stack){
    	Item item = stack.getItem();
    	if(!(item instanceof ItemSpade))
    		return false;
    	float value = getToolEffect(stack);
    	for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getToolEffect(is) > value && is.getItem() instanceof ItemSpade){                	
                	return false;
                }
                	
            }
        }
    	return true;
    }
    private boolean isBestAxe(ItemStack stack){
    	Item item = stack.getItem();
    	if(!(item instanceof ItemAxe))
    		return false;
    	float value = getToolEffect(stack);
    	for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if(getToolEffect(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)){                
                	return false;
                }
                	
            }
        }
    	return true;
    }
    private float getToolEffect(ItemStack stack){
    	Item item = stack.getItem();
    	if(!(item instanceof ItemTool))
    		return 0;
    	String name = item.getUnlocalizedName();
    	ItemTool tool = (ItemTool) item;
    	float value = 1;
    	if(item instanceof ItemPickaxe){
    		value = tool.getStrVsBlock(stack, Blocks.stone);
    		if(name.toLowerCase().contains("gold")){
    			value -= 5;
    		}
    	}else if(item instanceof ItemSpade){
    		value = tool.getStrVsBlock(stack, Blocks.dirt);
    		if(name.toLowerCase().contains("gold")){
    			value -= 5;
    		}
    	}else if(item instanceof ItemAxe){
    		value = tool.getStrVsBlock(stack, Blocks.log);
    		if(name.toLowerCase().contains("gold")){
    			value -= 5;
    		}
    	}else
    		return 1f;
		value += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D;
		value += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack)/100d;
    	return value;
    }
    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if(potion.getEffects(stack) == null)
            	return true;
            for (final Object o : potion.getEffects(stack)) {
                final PotionEffect effect = (PotionEffect) o;
                if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                	return true;
                }
            }
        }
        return false;
    }
    boolean invContainsType(int type){
    	
    	for(int i = 9; i < 45; i++){
    		if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
				Item item = is.getItem();
				if(item instanceof ItemArmor) {
					ItemArmor armor = (ItemArmor) item;
					if(type == armor.armorType){
						return true;
					}	
				}
    		}
    	}
    	return false;
    }
    public void getBestArmor(){
    	for(int type = 1; type < 5; type++){
    		if(mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()){
    			ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
    			if(autoArmorInstance.isBestArmor(is, type)){
    				continue;
    			}else{
    				drop(4 + type);
    			}
    		}
    		for (int i = 9; i < 45; i++) {
    			if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
    				ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
    				if(autoArmorInstance.isBestArmor(is, type) && autoArmorInstance.getProtection(is) > 0){
    					shiftClick(i);
    					timer.reset();
    					if(delay.getValue() > 0)
    						return;
    				}
    			}
            }
        }
    }

}
