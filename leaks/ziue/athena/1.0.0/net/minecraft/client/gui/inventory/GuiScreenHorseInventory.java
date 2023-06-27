package net.minecraft.client.gui.inventory;

import net.minecraft.util.*;
import net.minecraft.entity.passive.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.*;

public class GuiScreenHorseInventory extends GuiContainer
{
    private static final ResourceLocation horseGuiTextures;
    private IInventory playerInventory;
    private IInventory horseInventory;
    private EntityHorse horseEntity;
    private float mousePosx;
    private float mousePosY;
    
    public GuiScreenHorseInventory(final IInventory playerInv, final IInventory horseInv, final EntityHorse horse) {
        super(new ContainerHorseInventory(playerInv, horseInv, horse, Minecraft.getMinecraft().thePlayer));
        this.playerInventory = playerInv;
        this.horseInventory = horseInv;
        this.horseEntity = horse;
        this.allowUserInput = false;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        this.fontRendererObj.drawString(this.horseInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(final float partialTicks, final int mouseX, final int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiScreenHorseInventory.horseGuiTextures);
        final int i = (this.width - this.xSize) / 2;
        final int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        if (this.horseEntity.isChested()) {
            this.drawTexturedModalRect(i + 79, j + 17, 0, this.ySize, 90, 54);
        }
        if (this.horseEntity.canWearArmor()) {
            this.drawTexturedModalRect(i + 7, j + 35, 0, this.ySize + 54, 18, 18);
        }
        GuiInventory.drawEntityOnScreen(i + 51, j + 60, 17, i + 51 - this.mousePosx, j + 75 - 50 - this.mousePosY, this.horseEntity);
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.mousePosx = (float)mouseX;
        this.mousePosY = (float)mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    static {
        horseGuiTextures = new ResourceLocation("textures/gui/container/horse.png");
    }
}
