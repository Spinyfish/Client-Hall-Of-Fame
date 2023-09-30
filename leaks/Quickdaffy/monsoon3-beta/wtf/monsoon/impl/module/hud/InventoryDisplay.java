/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.hud;

import java.awt.Color;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.HUDModule;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.api.util.render.RoundedUtils;

public class InventoryDisplay
extends HUDModule {
    public InventoryDisplay() {
        super("Inventory Display", "Displays items in your inventory", 4.0f, 142.0f);
    }

    @Override
    public void render() {
        RenderUtil.getDefaultHudRenderer(this);
        int slotX = 0;
        int slotY = 0;
        for (int slotIndex = 9; slotIndex < 36; ++slotIndex) {
            Slot slot = this.mc.thePlayer.inventoryContainer.inventorySlots.get(slotIndex);
            Color faded = ColorUtil.fadeBetween(10, slotIndex, ColorUtil.getClientAccentTheme()[0], ColorUtil.getClientAccentTheme()[1]);
            Color c = ColorUtil.fadeBetween(20, slotIndex * 27, new Color(faded.getRed(), faded.getGreen(), faded.getBlue(), 150), new Color(-1018475701, true));
            RoundedUtils.round(this.getX() + 5.0f + (float)(slotX * 18) - 1.0f, this.getY() + 5.0f + (float)(slotY * 18) - 1.0f, 18.0f, 18.0f, 6.0f, c);
            if (slot.getHasStack()) {
                RenderHelper.enableGUIStandardItemLighting();
                RenderUtil.renderItem(slot.getStack(), this.getX() + 5.0f + (float)(slotX * 18), this.getY() + 5.0f + (float)(slotY * 18), 12.0f);
                RenderHelper.disableStandardItemLighting();
            }
            if (++slotX != 9) continue;
            ++slotY;
            slotX = 0;
        }
    }

    @Override
    public void blur() {
        RoundedUtils.glRound(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 10.0f, Wrapper.getPallet().getBackground().getRGB());
    }

    @Override
    public float getWidth() {
        return 170.0f;
    }

    @Override
    public float getHeight() {
        return 62.0f;
    }
}

