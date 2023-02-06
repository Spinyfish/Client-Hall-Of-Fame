package net.minecraft.client.gui.inventory;

import dev.elysium.client.Elysium;
import dev.elysium.client.mods.impl.player.ChestStealer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiChest extends GuiContainer
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;
    private int inventoryRows;

    public GuiChest(IInventory upperInv, IInventory lowerInv)
    {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().thePlayer));
        this.upperChestInventory = upperInv;
        this.lowerChestInventory = lowerInv;
        this.allowUserInput = false;
        int i = 222;
        int j = i - 108;
        this.inventoryRows = lowerInv.getSizeInventory() / 9;
        this.ySize = j + this.inventoryRows * 18;
    }

    public boolean isDefault(){
        return this.lowerChestInventory.getDisplayName().getUnformattedText().toString().equals(I18n.format("container.chest")) ||
        this.lowerChestInventory.getDisplayName().getUnformattedText().toString().equals(I18n.format("container.chestDouble"));
    }

    public void initGui() {
        super.initGui();


        ChestStealer cs = (ChestStealer) Elysium.getInstance().getModManager().getModByName("ChestStealer");

        if(cs.toggled && (isDefault() || !cs.namecheck.isEnabled())){
            new Thread(new Runnable(){
                public void run(){
                    try{
                        for(int i = 0; i < inventoryRows * 9; i++){
                            Slot slot = (Slot) inventorySlots.inventorySlots.get(i);
                            if(slot.getStack() != null){
                                Thread.sleep((long)cs.delay.getValue());
                                handleMouseClick(slot, slot.slotNumber, 0, 1);
                                handleMouseClick(slot, slot.slotNumber, 0, 6);
                            }
                        }

                        Thread.sleep((long)cs.closedelay.getValue());

                        if(cs.autoclose.isEnabled())
                            mc.thePlayer.closeScreen();

                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
