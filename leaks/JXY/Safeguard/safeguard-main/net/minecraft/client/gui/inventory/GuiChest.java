package net.minecraft.client.gui.inventory;

import java.io.IOException;

import intentions.modules.render.TabGUI;
import intentions.modules.world.ChestStealer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.util.ResourceLocation;

public class GuiChest extends GuiContainer
{
    private static final ResourceLocation field_147017_u = new ResourceLocation("textures/gui/container/generic_54.png");
    private IInventory upperChestInventory;
    private IInventory lowerChestInventory;

    /**
     * window height is calculated with these values; the more rows, the heigher
     */
    private int inventoryRows;
    private static final String __OBFID = "CL_00000749";

    public GuiChest(IInventory p_i46315_1_, IInventory p_i46315_2_)
    {
        super(new ContainerChest(p_i46315_1_, p_i46315_2_, Minecraft.getMinecraft().thePlayer));
        this.upperChestInventory = p_i46315_1_;
        this.lowerChestInventory = p_i46315_2_;
        this.allowUserInput = false;
        short var3 = 222;
        int var4 = var3 - 108;
        this.inventoryRows = p_i46315_2_.getSizeInventory() / 9;
        this.ySize = var4 + this.inventoryRows * 18;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items). Args : mouseX, mouseY
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }
    
    /**
     * stealtime :D
     */
    
    protected long getSleepTime() {
    	return (long) Math.floor(((Math.floor(Math.random() * 8))-4 + ChestStealer.stealSpeed.getValue()));
    }

    /**
     * Args : renderPartialTicks, mouseX, mouseY
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_147017_u);
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(var4, var5 + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
    
    /**
     * SafeGuard - Chest Stealer, chest stealer buttons
     */
    public void initGui() {
    	super.initGui();
    		int posY = (height - ySize) / 2+2;
    		if(TabGUI.openTabGUI) {
    			buttonList.add(new GuiButton(1, width/2-40, posY, 40, 12, "Steal"));
    			buttonList.add(new GuiButton(2, width/2+10, posY, 40, 12, "Store"));
    		}
    		
    		if(TabGUI.openTabGUI && ChestStealer.autoSteal.isEnabled()) {
            	new Thread(new Runnable() {
        			@Override
        			public void run() {
        				try {
        					for(int i = 0; i < GuiChest.this.inventoryRows * 9; i++) {
        						Slot slot = (Slot) GuiChest.this.inventorySlots.inventorySlots.get(i);
        						if(slot.getStack() != null && !slot.getStack().getItem().toString().equals("net.minecraft.item.ItemSkull@6e807e2")) {
        							Thread.sleep(getSleepTime());
        							GuiChest.this.handleMouseClick(slot, slot.slotNumber, 0, 1);
        							GuiChest.this.handleMouseClick(slot, slot.slotNumber, 0, 6);
        						}
        					}
        					if(ChestStealer.autoClose.isEnabled()) {
        						Thread.sleep(250L);
        						if(mc.currentScreen == null)return;
        						mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
        						mc.displayGuiScreen(null);
        					}
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
        			}
        		}).start();
            }
    }
    
    @Override
    public void actionPerformed(GuiButton button) throws IOException {
    	
    	if(!TabGUI.openTabGUI) return;
    	
    	super.actionPerformed(button);
    	
    	if(button.id == 1) {
    		new Thread(new Runnable() {
    			@Override
    			public void run() {
    				try {
    					for(int i = 0; i < GuiChest.this.inventoryRows * 9; i++) {
    						Slot slot = (Slot) GuiChest.this.inventorySlots.inventorySlots.get(i);
    						if(slot.getStack() != null) {
    							Thread.sleep(getSleepTime());
    							GuiChest.this.handleMouseClick(slot, slot.slotNumber, 0, 1);
    							GuiChest.this.handleMouseClick(slot, slot.slotNumber, 0, 6);
    						}
    					}
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		})
    		.start();
    	} else if (button.id == 2) {
    		new Thread(new Runnable() {
    			@Override
    			public void run() {
    				try {
    					for(int i = GuiChest.this.inventoryRows * 9; i <GuiChest.this.inventoryRows * 9+44; i++) {
    						Slot slot = (Slot) GuiChest.this.inventorySlots.inventorySlots.get(i);
    						if(slot.getStack() != null) {
    							Thread.sleep(getSleepTime());
    							GuiChest.this.handleMouseClick(slot, slot.slotNumber, 0, 1);
    							GuiChest.this.handleMouseClick(slot, slot.slotNumber, 0, 6);
    						}
    					}
    					if(ChestStealer.autoClose.isEnabled()) {
    						mc.thePlayer.closeScreen();
    					}
    				}catch(Exception e) {
    					e.printStackTrace();
    				}
    			}
    		})
    		.start();
    	}
    }
}
