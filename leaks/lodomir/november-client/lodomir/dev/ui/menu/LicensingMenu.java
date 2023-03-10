/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.ui.menu;

import lodomir.dev.utils.math.Stopwatch;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public final class LicensingMenu
extends GuiScreen {
    private float p = -100.0f;
    private final Stopwatch stopwatch = new Stopwatch();
    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        LicensingMenu.drawRect(0.0, width, 1.0, height, -1);
        RenderUtils.drawImg((ResourceLocation)locationMojangPng, (double)((double)width / 5.0), (double)0.0, (double)((double)width - (double)width / 2.5), (double)height);
        LicensingMenu.drawRect(0.0, width, 1.0, (int)this.p, -13878632);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

