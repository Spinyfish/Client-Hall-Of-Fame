package cafe.corrosion.menu.config;

import cafe.corrosion.Corrosion;
import cafe.corrosion.config.base.Config;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.animation.Animation;
import cafe.corrosion.menu.animation.impl.CubicEaseAnimation;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.Blurrer;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class ConfigMenu extends GuiScreen {
    private static final TTFFontRenderer RENDERER;
    private static final TTFFontRenderer SMALL_RENDERER;
    private static final int BACKGROUND_COLOR;
    private static final int WHITE;
    private static final int CONFIG_BACKGROUND;
    private static final int RED;
    private final Animation animation = new CubicEaseAnimation(250L);

    public ConfigMenu() {
        this.animation.start(false);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float progress = 1.0F;
        float transformX = 0.0F;
        float transformY = 0.0F;
        if (this.animation.isAnimating()) {
            progress = (float)this.animation.calculate();
            GL11.glScalef(progress, progress, 1.0F);
            transformX = (float)this.mc.displayWidth * (1.0F - progress) / 2.0F;
            transformY = (float)this.mc.displayHeight * (1.0F - progress) / 2.0F;
            GL11.glTranslatef(transformX, transformY, 0.0F);
        }

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int width = 150;
        int height = 150;
        Blurrer blurrer = Corrosion.INSTANCE.getBlurrer();
        blurrer.blur((double)(centerX - width), (double)(centerY - height), (double)width, (double)height, true);
        blurrer.bloom(centerX - width, centerY - height, width, height, 15, 200);
        RenderUtil.drawRoundedRect((float)(centerX - width), (float)(centerY - height), (float)(centerX + width), (float)(centerY + height), BACKGROUND_COLOR);
        this.drawText(centerX, centerY - 130);
        Map<String, Config> configs = Corrosion.INSTANCE.getConfigManager().getConfigurations();
        int startX = centerX - width + 20;
        int startY = centerY - height + 40;
        Iterator var15 = configs.entrySet().iterator();

        while(var15.hasNext()) {
            Entry<String, Config> entry = (Entry)var15.next();
            Config config = (Config)entry.getValue();
            this.drawConfig(startX, startY, (String)entry.getKey(), config.getAuthor(), config.getClientVersion());
        }

        GL11.glScalef(1.0F / progress, 1.0F / progress, 1.0F);
        GL11.glTranslatef(-transformX, -transformY, 0.0F);
    }

    private void drawText(int centerX, int posY) {
        String text = "Manage Configs";
        float modifier = RENDERER.getWidth(text) / 2.0F;
        RENDERER.drawString(text, (float)centerX - modifier, (float)posY, WHITE);
    }

    private void drawConfig(int posX, int posY, String name, String author, String version) {
        int expandX = 85;
        int expandY = 50;
        int secondBoxY = posY + expandY - 2;
        RenderUtil.drawRoundedRect((float)posX, (float)posY, (float)(posX + expandX), (float)(posY + expandY), CONFIG_BACKGROUND);
        RenderUtil.drawRoundedRect((float)posX, (float)secondBoxY, (float)(posX + expandX), (float)(secondBoxY + 20), RED);
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 19.0F);
        String text = "Select Config";
        float width = renderer.getWidth(text);
        float height = renderer.getHeight(text);
        int centerX = (int)((float)(posX + expandX / 2) - width / 2.0F);
        int centerY = (int)((float)(secondBoxY + 10) - height / 2.0F);
        renderer.drawString(text, (float)centerX, (float)centerY, WHITE);
        TTFFontRenderer boldRenderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.UBUNTU, 20.0F);
        float nameWidth = boldRenderer.getWidth(name);
        boldRenderer.drawStringWithShadow(name, (float)posX + (float)expandX / 2.0F - nameWidth / 2.0F, (float)(posY + 3), WHITE);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    static {
        RENDERER = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.UBUNTU, 24.0F);
        SMALL_RENDERER = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 16.0F);
        BACKGROUND_COLOR = (new Color(20, 20, 20)).getRGB();
        WHITE = Color.WHITE.getRGB();
        CONFIG_BACKGROUND = (new Color(33, 33, 33)).getRGB();
        RED = (new Color(48, 0, 92)).getRGB();
    }
}
