package com.alan.clients.module.impl.render;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.newevent.impl.render.RenderHungerEvent;
import com.alan.clients.value.impl.NumberValue;
import com.alan.clients.module.impl.render.appleskin.AppleSkinHelper;
import com.alan.clients.module.impl.render.appleskin.FoodValues;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import org.lwjgl.opengl.GL11;

import java.util.Random;
import java.util.Vector;

/**
 * @author Auth, Squeek
 * @since 02/07/2022
 */

@ModuleInfo(name = "Apple Skin", description = "module.render.appleskin.description", category = Category.RENDER)
public class AppleSkin extends Module {

    private final NumberValue maxFlashAlpha = new NumberValue("Max Flash Alpha", this, 0.65, 0, 1, 0.05);

    public final Vector<IntPoint> foodBarOffsets = new Vector<>();

    private final Random random = new Random();

    private float unclampedFlashAlpha;
    private float flashAlpha;
    private byte alphaDir = 1;

    @EventLink()
    public final Listener<RenderHungerEvent> onHunger = event -> {

        final ScaledResolution scaledResolution = event.getScaledResolution();
        final FoodStats stats = mc.thePlayer.getFoodStats();

        final int right = scaledResolution.getScaledWidth() / 2 + 91;
        final int top = scaledResolution.getScaledHeight() - 39;

        this.generateHungerBarOffsets(right, 0, mc.ingameGUI.updateCounter);

        this.drawSaturationOverlay(0, stats.getSaturationLevel(), 0, stats.getFoodLevel(), right, top, 1.0F);

        final ItemStack heldItem = mc.thePlayer.getHeldItem();

        final boolean holdingFood = heldItem != null && heldItem.getItem() instanceof ItemFood;

        if (!holdingFood) {
            this.resetFlash();
            return;
        }

        final FoodValues foodValues = AppleSkinHelper.getFoodValues(heldItem);

        final int foodHunger = foodValues.hunger;
        final float foodSaturationIncrement = foodValues.getSaturationIncrement();

        final int newFoodValue = stats.getFoodLevel() + foodHunger;
        final float newSaturationValue = stats.getSaturationLevel() + foodSaturationIncrement;
        final float saturationGained = newSaturationValue > newFoodValue ? newFoodValue - stats.getSaturationLevel() : foodSaturationIncrement;

        this.drawHungerOverlay(foodHunger, stats.getFoodLevel(), right, top, flashAlpha, AppleSkinHelper.isRottenFood(heldItem));

        this.drawSaturationOverlay(saturationGained, stats.getSaturationLevel(), foodHunger, stats.getFoodLevel(), right, top, flashAlpha);
    };

    @EventLink()
    public final Listener<TickEvent> onTick = event -> {

        unclampedFlashAlpha += alphaDir * 0.125F;

        if (unclampedFlashAlpha >= 1.5F) {
            alphaDir = -1;
        } else if (unclampedFlashAlpha <= -0.5F) {
            alphaDir = 1;
        }

        flashAlpha = Math.max(0.0F, Math.min(1.0F, unclampedFlashAlpha)) * Math.min(1.0F, maxFlashAlpha.getValue().floatValue());
    };

    private void generateHungerBarOffsets(final int right, final int top, final int ticks) {
        random.setSeed(ticks * 312871L);

        final int preferFoodBars = 10;

        final FoodStats stats = mc.thePlayer.getFoodStats();

        final float saturationLevel = stats.getSaturationLevel();
        final int foodLevel = stats.getFoodLevel();

        final boolean shouldAnimatedFood = saturationLevel <= 0.0F && mc.ingameGUI.updateCounter % (foodLevel * 3 + 1) == 0;

        if (foodBarOffsets.size() != preferFoodBars) {
            foodBarOffsets.setSize(preferFoodBars);
        }

        for (int i = 0; i < preferFoodBars; ++i) {
            final int x = right - i * 8 - 9;
            int y = top;

            if (shouldAnimatedFood) {
                y += random.nextInt(3) - 1;
            }

            IntPoint point = foodBarOffsets.get(i);

            if (point == null) {
                point = new IntPoint();
                foodBarOffsets.set(i, point);
            }

            point.x = x - right;
            point.y = y;
        }
    }

    private void drawSaturationOverlay(final float saturationGained, final float saturationLevel, final int hungerRestored, final int foodLevel, final int right, final int top, final float alpha) {
        if (saturationLevel + saturationGained < 0) {
            return;
        }

        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        final float modifiedSaturation = Math.max(0, Math.min(20, saturationLevel + saturationGained));

        final int modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored));

        int startSaturationBar = 0;
        final int endSaturationBar = (int) Math.ceil(modifiedSaturation / 2.0F);

        if (saturationGained != 0) {
            startSaturationBar = (int) Math.max(saturationLevel / 2.0F, 0);
        }

        final int iconStartOffset = 16;
        final int iconSize = 9;

        for (int i = startSaturationBar; i < endSaturationBar; ++i) {
            final IntPoint offset = foodBarOffsets.get(i);

            if (offset == null) {
                continue;
            }

            final int x = right + offset.x;
            final int y = top + offset.y;

            final int v = 3 * iconSize;
            int u = iconStartOffset + 4 * iconSize;
            int ub = iconStartOffset + iconSize;

            for (final PotionEffect e : mc.thePlayer.getActivePotionEffects()) {
                if (e.getPotionID() == Potion.hunger.getId()) {
                    u += 4 * iconSize;
                    break;
                }
            }

            int ubX = x;
            int ubIconSize = iconSize;

            if (i * 2 + 1 == (int) modifiedSaturation) {
                final int halfIconSize = iconSize / 2;

                ubX += halfIconSize;
                ub += halfIconSize;
                ubIconSize -= halfIconSize;
            }

            if (i * 2 + 1 == modifiedFood) {
                u += iconSize;
            }

            GlStateManager.color(0.75F, 0.65F, 0.0F, alpha);
            mc.ingameGUI.drawTexturedModalRect(ubX, y, ub, v, ubIconSize, iconSize);

            if (modifiedSaturation > modifiedFood) {
                continue;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
            mc.ingameGUI.drawTexturedModalRect(x, y, u, v, iconSize, iconSize);
        }

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawHungerOverlay(final int hungerRestored, final int foodLevel, final int right, final int top, final float alpha, final boolean useRottenTextures) {
        if (hungerRestored <= 0) {
            return;
        }

        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        final int modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored));

        final int startFoodBars = Math.max(0, foodLevel / 2);
        final int endFoodBars = (int) Math.ceil(modifiedFood / 2.0F);

        final int iconStartOffset = 16;
        final int iconSize = 9;

        for (int i = startFoodBars; i < endFoodBars; ++i) {
            final IntPoint offset = foodBarOffsets.get(i);

            if (offset == null) {
                continue;
            }

            final int x = right + offset.x;
            final int y = top + offset.y;

            final int v = 3 * iconSize;
            int u = iconStartOffset + 4 * iconSize;
            int ub = iconStartOffset + iconSize;

            if (useRottenTextures) {
                u += 4 * iconSize;
                ub += 12 * iconSize;
            }

            if (i * 2 + 1 == modifiedFood) {
                u += iconSize;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha * 0.25F);
            mc.ingameGUI.drawTexturedModalRect(x, y, ub, v, iconSize, iconSize);
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

            mc.ingameGUI.drawTexturedModalRect(x, y, u, v, iconSize, iconSize);
        }

        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void resetFlash() {
        unclampedFlashAlpha = 0.0F;
        flashAlpha = 0.0F;
        alphaDir = 1;
    }


    /**
     * @author Auth
     * @since 02/07/2022
     */
    public static class IntPoint {
        public int x;
        public int y;
    }
}