package club.pulsive.api.ui;

import club.pulsive.api.config.Config;
import club.pulsive.api.font.Fonts;
import club.pulsive.client.ui.clickgui.clickgui.component.Component;
import club.pulsive.client.ui.clickgui.clickgui.panel.Panel;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import javax.vecmath.Color3f;
import java.awt.*;

@Getter
public class ConfigButton extends Component {

    
 
    private String config;
    
    public ConfigButton(float x, float y, float width, float height, String config) {
        super(x, y, width, height);
        this.config = config;
        
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Gui.drawRect(x, y, x + width, y + height, new Color(60, 60, 60).getRGB());
        Fonts.moon.drawString(config, x + 1, y, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
