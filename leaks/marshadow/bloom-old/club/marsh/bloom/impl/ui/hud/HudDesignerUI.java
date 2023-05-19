package club.marsh.bloom.impl.ui.hud;

import club.marsh.bloom.Bloom;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HudDesignerUI extends GuiScreen {
    public boolean open, mouseHeld;
    public int heldButton = -1;
    public HudDesigner designer = Bloom.INSTANCE.hudDesigner;
    public HashMap<String, GuiButton> values = new HashMap<>();
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //Gui.drawRect(0,0, ScaledResolution.getScaledWidth(),ScaledResolution.getScaledHeight(),new Color(0,0,0,150).getRGB());
        for (Component component : designer.components) {
            if (component.isHolding(mouseX,mouseY)) {
                if (heldButton == 0 && mouseHeld) {
                    component.setX(mouseX - component.getWidth() / 2);
                    component.setY(mouseY - component.getHeight() / 2);
                }
                mc.fontRendererObj.drawString("Holding: " + component.getName(), (ScaledResolution.getScaledWidth()/2)-fontRendererObj.getStringWidth("Holding: " + component.getName())/2,ScaledResolution.getScaledHeight()-15,-1,true);
                if (!component.isEnabled()) {
                    Gui.drawHorizontalLine(component.getX() - 1, component.getX() + component.getWidth() + 1, component.getY() + component.getHeight(), new Color(50,50,50,150).getRGB());
                    Gui.drawHorizontalLine(component.getX() - 1, component.getX() + component.getWidth() + 1, component.getY() - 2, new Color(50,50,50,150).getRGB());
                    Gui.drawVerticalLine(component.getX() - 1, component.getY() - 3, component.getY() + component.getHeight(), new Color(50,50,50,150).getRGB());
                    Gui.drawVerticalLine(component.getX() + component.getWidth() + 1, component.getY() - 3, component.getY() + component.getHeight(), new Color(50,50,50,150).getRGB());
                } else {
                    Gui.drawHorizontalLine(component.getX()-1,component.getX()+component.getWidth()+1,component.getY()+component.getHeight(),Color.WHITE.getRGB());
                    Gui.drawHorizontalLine(component.getX()-1,component.getX()+component.getWidth()+1,component.getY()-2,Color.WHITE.getRGB());
                    Gui.drawVerticalLine(component.getX()-1,component.getY()-3,component.getY()+component.getHeight(),Color.WHITE.getRGB());
                    Gui.drawVerticalLine(component.getX()+component.getWidth()+1,component.getY()-3,component.getY()+component.getHeight(),Color.WHITE.getRGB());
                }
            }
            component.render();
            component.getValues().forEach((name, value) -> {
               if (values.get(name) != null) {
                   values.get(name).displayString = component.getName() + ": " + name + ": " + value;
               }
            });
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }



    @Override
    public void initGui() {
        AtomicInteger buttons = new AtomicInteger();
        for (Component component : designer.components) {
            component.getValues().forEach((name, value) -> {
                System.out.println(name + "");
                GuiButton button = new GuiButton(buttons.incrementAndGet(), 0, (ScaledResolution.getScaledHeight())-(buttons.get()*20), component.getName() + ":" + name + ":" + value);
                values.put(name,button);
                this.buttonList.add(button);
            });
        }
    }
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        for (Component component : designer.components) {
            component.getValues().forEach((name,value) -> {
                if (values.get(name) != null) {
                    if (values.get(name).id == button.id) {
                        component.getValues().replace(name,!value);
                    }
                }
            });
        }
        super.actionPerformed(button);
    }
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Component component : designer.components) {
            if (component.isHolding(mouseX, mouseY)) {
                if (mouseButton != 0) {
                    component.setEnabled(!component.isEnabled());
                }
            }
        }
        mouseHeld = true;
        heldButton = mouseButton;
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        mouseHeld = false;
        heldButton = -1;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
