package me.eldodebug.soar.management.mods.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRenderSelectedItem;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class ItemInfoMod extends Mod{

	public ItemInfoMod() {
		super("Item Info", "Display your held item info", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		this.addBooleanSetting("Static Color", this, false);
	}
	
	@EventTarget
	public void onRenderTooltip(EventRenderSelectedItem event) {
		
        ItemStack heldItemStack = mc.thePlayer.inventory.getCurrentItem();
        
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        
        int addY = 70;
        
        if (heldItemStack != null) {
        	
        	String s1 = "";
        	
            if (heldItemStack.getItem() instanceof ItemPotion) {
            	s1 = this.getPotionEffectString(heldItemStack);
            } else {
            	s1 = this.getEnchantmentString(heldItemStack);
            }
            
            if(PlayerUtils.isCreative()) {
            	addY = 55;
            }

        	fr.drawStringWithShadow(s1, (sr.getScaledWidth() / 2) - (mc.fontRendererObj.getStringWidth(s1) / 2), sr.getScaledHeight() - addY - 1, event.getColor());
        }
	}
	
    @SuppressWarnings("rawtypes")
	private String getPotionEffectString(ItemStack heldItemStack) {
        ItemPotion potion = (ItemPotion) heldItemStack.getItem();
        List effects = potion.getEffects(heldItemStack);

        if (effects == null) {
            return "";
        } else {
            StringBuilder potionBuilder = new StringBuilder();
            Iterator iterator = effects.iterator();

            while (iterator.hasNext()) {
                PotionEffect entry = (PotionEffect) iterator.next();
                int duration = entry.getDuration() / 20;

                if(Soar.instance.settingsManager.getSettingByName(this, "Static Color").getValBoolean()) {
                	if(entry.getPotionID() == Potion.moveSpeed.getId()) {
                		potionBuilder.append(EnumChatFormatting.AQUA);
                	}
                	if(entry.getPotionID() == Potion.regeneration.getId()) {
                		potionBuilder.append(EnumChatFormatting.LIGHT_PURPLE);
                	}
                	if(entry.getPotionID() == Potion.poison.getId()) {
                		potionBuilder.append(EnumChatFormatting.DARK_GREEN);
                	}
                	if(entry.getPotionID() == Potion.jump.getId()) {
                		potionBuilder.append(EnumChatFormatting.GREEN);
                	}
                	if(entry.getPotionID() == Potion.fireResistance.getId()) {
                		potionBuilder.append(EnumChatFormatting.GOLD);
                	}
                	if(entry.getPotionID() == Potion.heal.getId()) {
                		potionBuilder.append(EnumChatFormatting.RED);
                	}
                	if(entry.getPotionID() == Potion.moveSlowdown.getId()) {
                		potionBuilder.append(EnumChatFormatting.GRAY);
                	}
                	if(entry.getPotionID() == Potion.nightVision.getId()) {
                		potionBuilder.append(EnumChatFormatting.DARK_BLUE);
                	}
                	if(entry.getPotionID() == Potion.damageBoost.getId()) {
                		potionBuilder.append(EnumChatFormatting.DARK_PURPLE);
                	}
                }
                
                potionBuilder.append(StatCollector.translateToLocal(entry.getEffectName()));
        		potionBuilder.append(EnumChatFormatting.WHITE);
                potionBuilder.append(" ");
                potionBuilder.append(entry.getAmplifier() + 1);
                potionBuilder.append(" ");
                potionBuilder.append("(");
                potionBuilder.append(duration / 60 + String.format(":%02d", new Object[] { Integer.valueOf(duration % 60)}));
                potionBuilder.append(") ");
            }

            return potionBuilder.toString().trim();
        }
    }

    @SuppressWarnings("rawtypes")
	private String getEnchantmentString(ItemStack heldItemStack) {
        StringBuilder enchantBuilder = new StringBuilder();
        Map en = EnchantmentHelper.getEnchantments(heldItemStack);
        Iterator iterator = en.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            enchantBuilder.append((String) this.ENCHANTMENT_SHORT_NAME.get(entry.getKey()));
            enchantBuilder.append(" ");
            enchantBuilder.append(entry.getValue());
            enchantBuilder.append(" ");
        }

        return enchantBuilder.toString().trim();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	private final Map ENCHANTMENT_SHORT_NAME = new HashMap() {
        {
            this.put(Integer.valueOf(0), "P");
            this.put(Integer.valueOf(1), "FP");
            this.put(Integer.valueOf(2), "FF");
            this.put(Integer.valueOf(3), "BP");
            this.put(Integer.valueOf(4), "PP");
            this.put(Integer.valueOf(5), "R");
            this.put(Integer.valueOf(6), "AA");
            this.put(Integer.valueOf(7), "T");
            this.put(Integer.valueOf(8), "DS");
            this.put(Integer.valueOf(9), "FW");
            this.put(Integer.valueOf(16), "SH");
            this.put(Integer.valueOf(17), "SM");
            this.put(Integer.valueOf(18), "BoA");
            this.put(Integer.valueOf(19), "KB");
            this.put(Integer.valueOf(20), "FA");
            this.put(Integer.valueOf(21), "L");
            this.put(Integer.valueOf(32), "EFF");
            this.put(Integer.valueOf(33), "ST");
            this.put(Integer.valueOf(34), "UNB");
            this.put(Integer.valueOf(35), "F");
            this.put(Integer.valueOf(48), "POW");
            this.put(Integer.valueOf(49), "PUN");
            this.put(Integer.valueOf(50), "FLA");
            this.put(Integer.valueOf(51), "INF");
            this.put(Integer.valueOf(61), "LoS");
            this.put(Integer.valueOf(62), "LU");
            this.put(Integer.valueOf(70), "MEN");
        }
    };
}
