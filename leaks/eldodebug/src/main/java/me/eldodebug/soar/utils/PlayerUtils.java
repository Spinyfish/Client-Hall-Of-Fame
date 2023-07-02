package me.eldodebug.soar.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

public class PlayerUtils {

	private static Minecraft mc = Minecraft.getMinecraft();
	
    public static final Map<Integer, Float> MODIFIER_BY_TICK = new HashMap<>();
    
	public static float getSpeed() {
		double distTraveledLastTickX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
		double distTraveledLastTickZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
		double currentSpeed = MathHelper.sqrt_double(distTraveledLastTickX * distTraveledLastTickX
				+ distTraveledLastTickZ * distTraveledLastTickZ);
		
		return (float) (currentSpeed / 0.05);
	}
	
    public static int getPotionsFromInventory() {
        int count = 0;

        for (int i = 1; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();

                if (item instanceof ItemPotion) {
                    ItemPotion potion = (ItemPotion) item;

                    if (potion.getEffects(is) != null) {
                        Iterator<PotionEffect> iterator = potion.getEffects(is).iterator();

                        while (iterator.hasNext()) {
                            Object o = iterator.next();
                            PotionEffect effect = (PotionEffect) o;

                            if (effect.getPotionID() == Potion.heal.id) {
                                ++count;
                            }
                        }
                    }
                }
            }
        }

        return count;
    }
    
    public static boolean isSpectator(){
        NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(mc.thePlayer.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.SPECTATOR;
    }
    
    public static boolean isCreative(){
        NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(mc.thePlayer.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.CREATIVE;
    }
    
    public static boolean isSurvival(){
        NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(mc.thePlayer.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.SURVIVAL;
    }
    
    static {
        MODIFIER_BY_TICK.put(0, 0.0F);
        MODIFIER_BY_TICK.put(1, 0.00037497282f);
        MODIFIER_BY_TICK.put(2, 0.0015000105f);
        MODIFIER_BY_TICK.put(3, 0.0033749938f);
        MODIFIER_BY_TICK.put(4, 0.0059999824f);
        MODIFIER_BY_TICK.put(5, 0.009374976f);
        MODIFIER_BY_TICK.put(6, 0.013499975f);
        MODIFIER_BY_TICK.put(7, 0.01837498f);
        MODIFIER_BY_TICK.put(8, 0.023999989f);
        MODIFIER_BY_TICK.put(9, 0.030375004f);
        MODIFIER_BY_TICK.put(10, 0.037500024f);
        MODIFIER_BY_TICK.put(11, 0.04537499f);
        MODIFIER_BY_TICK.put(12, 0.05400002f);
        MODIFIER_BY_TICK.put(13, 0.063374996f);
        MODIFIER_BY_TICK.put(14, 0.07349998f);
        MODIFIER_BY_TICK.put(15, 0.084375024f);
        MODIFIER_BY_TICK.put(16, 0.096000016f);
        MODIFIER_BY_TICK.put(17, 0.10837501f);
        MODIFIER_BY_TICK.put(18, 0.121500015f);
        MODIFIER_BY_TICK.put(19, 0.13537502f);
        MODIFIER_BY_TICK.put(20, 0.14999998f);
    }
}
