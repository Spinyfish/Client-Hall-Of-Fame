package igbt.astolfy.ui.ClickGUI.Compact;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import igbt.astolfy.Astolfy;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.module.ModuleBase.Category;
import igbt.astolfy.ui.ClickGUI.Compact.impl.CButton;
import igbt.astolfy.ui.ClickGUI.Compact.impl.MButton;
import igbt.astolfy.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class CompactGUI extends GuiScreen {
	
	public double x4 = 1;
	int widthX = 350;
	int widthY = 200;
	public int x; //= sr.getScaledWidth() / 2 - (widthX / 2);
	public int y; //= sr.getScaledHeight() / 2 - (widthY /2);
	public int headerWidth = 20;
	boolean dragging = false;
	double offsetX = 0;
	double offsetY = 0;
	public Category selectedCategory = Category.COMBAT;

	ArrayList<CButton> cateButtons = new ArrayList<CButton>();
	ArrayList<MButton> mButtons = new ArrayList<MButton>();
	
	public CompactGUI() {
		mc = Minecraft.getMinecraft();
		widthX = 350;
		widthY = 200;
		x = mc.displayWidth /2 / 2 - (widthX / 2);
		y = mc.displayHeight /2 / 2 - (widthY /2);
		headerWidth = 20;
		int count = 0;
		int count2 = 0;
		for(Category c : Category.values()) {
			cateButtons.add(new CButton(count ,this, c,45,20));
			
			for(ModuleBase mod : Astolfy.moduleManager.getModulesByCategory(c)) {

				mButtons.add(new MButton(count2, this, mod,100,30));
				count2++;
			}
			count2 = 0;
			
			count++;
		}
	}
	
	public double scroll = 0;
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {            MButton lastButton = null;
    for(MButton m : mButtons) {
    	if(m.mod.getCategory().equals(selectedCategory)) {
    		lastButton = m;
    	}
    }
		if(!(lastButton.y + lastButton.height - y > 198))
			scroll -= 0;
        	
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		RenderUtils.prepareScissorBox(x, y, x + widthX, y + widthY);
		if(dragging) {
			x = (int) (mouseX - offsetX); 
			y = (int) (mouseY - offsetY);
		}
		Gui.drawRect(x, y, x + widthX, y + widthY, new Color(25,25,25).getRGB());
		Gui.drawRect(x, y + headerWidth, x + 65 + 4, y + widthY, new Color(21,21,21).getRGB());

		for(CButton c : cateButtons) {
			c.drawScreen(mouseX, mouseY, partialTicks);
		}
		for(MButton m : mButtons) {
			if(m.mod.getCategory().equals(selectedCategory)) {
				m.drawScreen(mouseX, mouseY, partialTicks,fits(m.x, m.y, m.x + m.width, m.y + m.height));
				
			}
		}
       
		//System.out.println(scroll);
		Gui.drawRect(x, y, x + widthX, y + headerWidth, new Color(30,30,30).getRGB());		
		//Gui.drawRect(x, y + widthY - 20, x + widthX, y + widthY, new Color(21,21,21).getRGB());

		mc.customFont.drawString("Astolfy", x + 5, y + (headerWidth - mc.customFont.getHeight()) /2, -1);
		mc.customFont.drawString(selectedCategory.name, x + 65 + 5, y + headerWidth + 2, -1);

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
	
	public boolean fits(double x6, double y6, double x7,double y7) {
		
		//if(x <= x6 && x + width >= x6 && x <= x7 && x + width >= x7
		//&& y <= y6 && y + height >= y6 && y <= y7 && y + height - 305 >= y7)
			return true;
		
		//return false;
	}

	protected void keyTyped(char typedChat, int keyCode) {
		if(keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(null);
		}
		if(keyCode == 201) {
		}
	}
	
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();

        if (i != 0)
        {
            if (i > 1)
            {
                i = 1;
            }

            if (i < -1)
            {
                i = -1;
            }

            if (!isShiftKeyDown())
            {
                i *= 7;
            }



            
            scroll(i);
            //System.out.println(scroll + " | " + (lastButton.y + 5 - y - ((lastButton.height + 5) * lastButton.count)));
           // System.out.println((lastButton.y + lastButton.height - y));
        }
    }
    
    public void scroll(int i) {
        MButton lastButton = null;
        for(MButton m : mButtons) {
        	if(m.mod.getCategory().equals(selectedCategory)) {
        		lastButton = m;
        	}
        }
        if(!(scroll >= 0 && i >= 0
        		&& scroll <= lastButton.y - headerWidth))
        	if(!(lastButton.y + lastButton.height - y < 198 && i < 0))
        		scroll += i;
    }

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(RenderUtils.isHovered(mouseX, mouseY, x, y, x + width, y + headerWidth)) {
			dragging = true;
			offsetX = mouseX - x;
			offsetY = mouseY - y;
		}
		for(CButton b : this.cateButtons) {
			b.mouseClicked(mouseX, mouseY, mouseButton);
		}
		for(MButton m : mButtons) {
			if(m.mod.getCategory().equals(selectedCategory))
				m.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		dragging = false;
	}
	
	@Override
	public void onGuiClosed() {
		Astolfy.i.moduleManager.getModuleByName("ClickGUI").toggle();
	}
	
	
}
