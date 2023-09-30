package dev.elysium.client.scripting.api.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import dev.elysium.client.Elysium;
import dev.elysium.client.scripting.api.components.events.ButtonClickEvent;
import dev.elysium.client.ui.font.TTFFontRenderer;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenWrapper extends GuiScreen{

	public GuiComponent comp;
	
	public FrameGuiComponent dragging = null;
	public int dragX;
	public int dragY;
	
	public GuiScreenWrapper(GuiComponent comp) {
		this.comp = comp;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return comp.pauseGame;
	}
	@Override
	public void onGuiClosed()
    {
		if(comp.close != null)
			comp.close.call();
    }
	
	public void renderElement(APIComponent c, int mouseX, int mouseY, float particleTicks) {
		
		if(c instanceof FrameGuiComponent) {
			FrameGuiComponent f = (FrameGuiComponent)c;
			RenderUtils.drawARoundedRect(f.renderPosX,f.renderPosY, f.renderPosX + f.GetWidth(), f.renderPosY + f.GetHeight(),f.GetCornerRadius(), f.GetColor());
		}
		if(c instanceof TextLabelComponent) {
			TextLabelComponent f = (TextLabelComponent)c;
			Elysium.getInstance().getFontManager().getFont(f.GetFontName() + " " + f.GetFontSize()).drawString(f.GetText(), f.renderPosX, f.renderPosY, f.GetColor());
		}
		if(c instanceof ButtonGuiComponent) {
			ButtonGuiComponent f = (ButtonGuiComponent)c;
			RenderUtils.drawARoundedRect(f.renderPosX, f.renderPosY, f.renderPosX + f.GetWidth(), f.renderPosY + f.GetHeight(),f.GetCornerRadius(), f.GetColor());
			TTFFontRenderer font = Elysium.getInstance().getFontManager().getFont(f.GetFontName() + " " + f.GetFontSize());
			font.drawString(f.GetText(), (f.renderPosX + f.GetWidth() / 2 - font.getStringWidth(f.GetText()) / 2) + f.paddingLeft - f.paddingRight, (f.renderPosY + f.GetHeight() / 2 - font.getHeight(f.GetText()) / 2) + f.paddingTop - f.paddingBottom, f.GetTextColor());
			if(mouseX >= f.renderPosX && mouseX <= f.renderPosX + f.GetWidth() && mouseY >= f.renderPosY && mouseY <= f.renderPosY + f.GetHeight()) {
			
				if(f.starthover != null && f.starthover != LuaValue.NIL && !f.wasHovering) {
					try {
						f.starthover.call();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				f.wasHovering = true;
				if(f.hovering != null && f.hovering != LuaValue.NIL) {
					try {
						f.hovering.call();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}else {
				if(f.wasHovering) {
					f.wasHovering = false;
					if(f.endhover != null && f.endhover != LuaValue.NIL) {
						try {
							f.endhover.call();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
			}
		}
		
		
		
		for(APIComponent cc : c.childrens) {
			renderElement(cc, mouseX, mouseY, particleTicks);
		}
		
	}
	
	public List<APIComponent> getComponents(APIComponent from){
		List<APIComponent> result = new ArrayList<APIComponent>();
		
		for(APIComponent comp : from.childrens) {
			result.add(comp);
			result.addAll(getComponents(comp));
		}
		
		return result;
	}
	
	public void drag(int mouseX, int mouseY, FrameGuiComponent frame) {
		if(frame.parent != null) {
			if(frame.parent instanceof FrameGuiComponent) {
				drag(mouseX, mouseY, (FrameGuiComponent)frame.parent);
				return;
			}
		}
		frame.SetPos(mouseX - dragX, mouseY - dragY);
		
		
	}
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(dragging != null) {
			drag(mouseX, mouseY, dragging);
		}
		
		for(APIComponent c : getComponents(comp)) {
			c.renderPosX = (c.parent != null ? c.parent.GetPosX() : 0) + c.GetPosX();
			c.renderPosY = (c.parent != null ? c.parent.GetPosY() : 0) + c.GetPosY();
		}
		
		for(APIComponent c : comp.childrens) {
			renderElement(c, mouseX, mouseY, partialTicks);
		}
	}
	
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		for(APIComponent c : getComponents(comp)) {
			if(c instanceof ButtonGuiComponent) {
				ButtonGuiComponent f = (ButtonGuiComponent)c;
				if(mouseX >= f.renderPosX && mouseX <= f.renderPosX + f.GetWidth())
					if(mouseY >= f.renderPosY && mouseY <= f.renderPosY + f.GetHeight()) {
						if(f.click != null && f.click != LuaValue.NIL) {
							try {
								f.click.call(CoerceJavaToLua.coerce(new ButtonClickEvent(mouseX, mouseY, mouseButton)));
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
					}
			}
			
			if(c instanceof FrameGuiComponent) {
				FrameGuiComponent f = (FrameGuiComponent)c;
				if(mouseX >= f.renderPosX && mouseX <= f.renderPosX + f.GetWidth())
					if(mouseY >= f.renderPosY && mouseY <= f.renderPosY + f.GetHeight()) {
						if(f.draggable) {
							FrameGuiComponent parent = f;
							while(parent.parent != null && parent.parent instanceof FrameGuiComponent)
								parent = (FrameGuiComponent)parent.parent;
							dragging = parent;
							dragX = mouseX - parent.GetPosX();
							dragY = mouseY - parent.GetPosY();
						}
						
					}
			
			}
		}
	}
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        dragging = null;
    }
	
}
