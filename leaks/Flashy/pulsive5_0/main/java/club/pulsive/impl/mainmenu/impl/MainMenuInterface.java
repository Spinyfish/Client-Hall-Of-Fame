package club.pulsive.impl.mainmenu.impl;

import club.pulsive.impl.util.render.RenderUtil;
import net.minecraft.client.renderer.entity.Render;

import java.util.ArrayList;

public interface MainMenuInterface {

    ArrayList<MainMenuButton> buttons = new ArrayList<>();

    default void onMouseClick(int mouseX, int mouseY) {
        for(MainMenuButton btn : buttons) {
            if(RenderUtil.isHovered((float) btn.getX(), (float) btn.getY(), (float) btn.getWidth(), (float) btn.getHeight(), mouseX,mouseY)) {
                onButtonClicked(btn);
                return;
            }
        }
    }

    default void addButton(MainMenuButton btn) {
        buttons.add(btn);
    }

    void onButtonClicked(MainMenuButton btn);
}
