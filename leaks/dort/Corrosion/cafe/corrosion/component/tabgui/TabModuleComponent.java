package cafe.corrosion.component.tabgui;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.module.Module;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;

public class TabModuleComponent {
    private static final int BACKGROUND = (new Color(20, 20, 20, 200)).getRGB();
    private static final int GRAY = (new Color(105, 105, 105)).getRGB();
    private static final int WHITE;
    private final Module.Category category;
    private boolean selected;
    private boolean expanded;
    private Module[] modules;
    private int selectedIndex;

    public TabModuleComponent(Module.Category category) {
        this.category = category;
    }

    public void onPostLoad() {
        this.modules = (Module[])Corrosion.INSTANCE.getModuleManager().getIf((module) -> {
            return module.getAttributes().category() == this.category;
        }).toArray(new Module[0]);
    }

    public void renderExpansion(int posX, int posY, int selectedColor) {
        int maxY = this.modules.length * 20 + posY;
        int maxX = posX + 85;
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 19.0F);
        RenderUtil.drawRoundedRect((float)posX, (float)posY, (float)maxX, (float)maxY, BACKGROUND);

        for(int i = 0; i < this.modules.length; ++i) {
            Module module = this.modules[i];
            if (this.selectedIndex == i) {
                RenderUtil.drawRoundedRect((float)posX, (float)(posY + i * 20), (float)(posX + 85), (float)(posY + 17 + i * 20), selectedColor, selectedColor);
            }

            renderer.drawStringWithShadow(module.getAttributes().name(), (float)(posX + 5), (float)(posY + 5 + i * 20), !module.isEnabled() && this.selectedIndex != i ? GRAY : WHITE);
        }

    }

    public void onKeyPress(int pressedKey) {
        int length = this.modules.length;
        switch(pressedKey) {
        case 28:
            if (this.selectedIndex >= this.modules.length) {
                this.selectedIndex = 0;
            }

            if (this.expanded) {
                this.modules[this.selectedIndex].toggle();
            }
            break;
        case 200:
            if (--this.selectedIndex == -1) {
                this.selectedIndex = length - 1;
            }
            break;
        case 208:
            if (++this.selectedIndex == length) {
                this.selectedIndex = 0;
            }
        }

    }

    public Module.Category getCategory() {
        return this.category;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public Module[] getModules() {
        return this.modules;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    static {
        WHITE = Color.WHITE.getRGB();
    }
}
