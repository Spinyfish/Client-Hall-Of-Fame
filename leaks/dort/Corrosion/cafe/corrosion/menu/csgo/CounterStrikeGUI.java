package cafe.corrosion.menu.csgo;

import cafe.corrosion.menu.csgo.impl.CSCategoryComponentPane;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.impl.visual.GUI;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.render.GuiUtils;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class CounterStrikeGUI extends GuiScreen {
    private static final ResourceLocation CLOWN_LOGO = new ResourceLocation("corrosion/logos/clown-1.png");
    private static final int BACKGROUND = (new Color(15, 15, 15)).getRGB();
    private static final int BAR_COLOR = (new Color(33, 33, 33, 175)).getRGB();
    private static final int WIDTH = 245;
    private static final int HEIGHT = 145;
    private static final int BAR_OFFSET = 70;
    private static final int SECOND_BAR_OFFSET = 73;
    private static final int VERTICAL_BAR_OFFSET = 5;
    private final List<CSCategoryComponentPane> categoryComponentPanes = new ArrayList();
    private final EnumProperty<GUI.AnimationStyle> animationStyle;
    private final BooleanProperty useAnimations;

    public CounterStrikeGUI(EnumProperty<GUI.AnimationStyle> animationStyle, BooleanProperty useAnimations) {
        this.animationStyle = animationStyle;
        this.useAnimations = useAnimations;

        for(int i = 0; i < Module.Category.values().length; ++i) {
            this.categoryComponentPanes.add(new CSCategoryComponentPane(Module.Category.values()[i], i));
        }

    }

    public void initGui() {
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int posX = centerX - 245;
        int posY = centerY - 145;
        int maxX = centerX + 245;
        int maxY = centerY + 145;
        RenderUtil.drawRoundedRect((float)posX, (float)posY, (float)maxX, (float)maxY, BACKGROUND);
        RenderUtil.drawRoundedRect((float)(posX + 70), (float)(posY + 5), (float)(posX + 73), (float)(maxY - 5), BAR_COLOR);
        GuiUtils.drawImage(CLOWN_LOGO, (float)(centerX - 245 + 11), (float)(centerY - 145 + 6), 48.0F, 48.0F, -1);
        int verticalOffset = maxY + 50;

        for(Iterator var11 = this.categoryComponentPanes.iterator(); var11.hasNext(); verticalOffset += 20) {
            CSCategoryComponentPane pane = (CSCategoryComponentPane)var11.next();
            pane.setPosX(posX);
            pane.setPosX(posY + verticalOffset);
            pane.draw(mouseX, mouseY);
        }

    }
}
