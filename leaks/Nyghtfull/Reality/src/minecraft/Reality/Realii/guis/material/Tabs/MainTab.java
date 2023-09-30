package Reality.Realii.guis.material.Tabs;

import Reality.Realii.Client;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.guis.material.Main;
import Reality.Realii.guis.material.Tab;

import java.awt.*;

public class MainTab extends Tab {
    public MainTab() {
        name = "Main Menu";
    }

    @Override
    public void render(float mouseX, float mouseY) {
        super.render(mouseX, mouseY);
        FontLoaders.arial24.drawString("Welcome! To Reality Client Made By Nyghtfull Skidded Cgui lmaoooo", Main.windowX + Main.animListX + 50, Main.windowY + 100, new Color(255, 255, 255).getRGB());
        float width = FontLoaders.arial24.getStringWidth("Welcome! To Reality Client Skidded Cgui lmaoooo");
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
    }
}
