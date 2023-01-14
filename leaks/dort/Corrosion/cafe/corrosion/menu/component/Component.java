package cafe.corrosion.menu.component;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class Component {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected static final FontRenderer mcRenderer;
    protected boolean expanded;
    protected int posX;
    protected int posY;
    protected int maxX;
    protected int maxY;

    public abstract void initializeComponent();

    public abstract void drawComponent(int var1, int var2);

    public abstract void handleClick(int var1, int var2, int var3);

    public abstract void handleKeyPress(char var1, int var2);

    public abstract void mouseReleased(int var1, int var2, int var3);

    public boolean isHovered(int mouseX, int mouseY) {
        return GuiUtils.isHoveringPos(mouseX, mouseY, this.posX, this.posY, this.maxX, this.maxY);
    }

    public TTFFontRenderer getFont(FontType fontType, int size) {
        return Corrosion.INSTANCE.getFontManager().getFontRenderer(fontType, (float)size);
    }

    static {
        mcRenderer = mc.fontRendererObj;
    }
}
