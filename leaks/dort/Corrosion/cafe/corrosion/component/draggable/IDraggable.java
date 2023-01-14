package cafe.corrosion.component.draggable;

import cafe.corrosion.menu.drag.data.HudComponentProxy;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public interface IDraggable {
    void render(HudComponentProxy var1, ScaledResolution var2, int var3, int var4, int var5, int var6);

    default void renderBackground(ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY, int color) {
        Gui.drawRect((double)(posX - 1), (double)(posY - 1), (double)(posX + expandX + 1), (double)(posY + expandY + 1), color);
    }
}
