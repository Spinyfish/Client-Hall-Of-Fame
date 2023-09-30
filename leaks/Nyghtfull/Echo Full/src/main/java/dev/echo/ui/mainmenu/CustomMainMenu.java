package dev.echo.ui.mainmenu;

import dev.echo.Echo;
import dev.echo.other.intent.cloud.Cloud;
import dev.echo.ui.Screen;
import dev.echo.ui.altmanager.panels.LoginPanel;
import dev.echo.ui.mainmenu.particles.ParticleEngine;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.misc.*;
import dev.echo.utils.render.GLUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.render.RoundedUtil;
import dev.echo.utils.render.StencilUtil;
import dev.echo.utils.render.blur.GaussianBlur;
import lombok.Getter;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomMainMenu extends GuiScreen {
    private ParticleEngine particleEngine;

    public static boolean animatedOpen = false;

    private final List<MenuButton> buttons = new ArrayList() {{
        add(new MenuButton("Singleplayer"));
        add(new MenuButton("Multiplayer"));
        add(new MenuButton("Alt Manager"));
        add(new MenuButton("Settings"));
        add(new MenuButton("Exit"));
    }};

    private final List<TextButton> textButtons = new ArrayList() {{
        add(new TextButton("Scripting"));
        add(new TextButton("Discord"));
    }};

    private final ResourceLocation backgroundResource = new ResourceLocation("Echo/MainMenu/funny.png");
    private final ResourceLocation blurredRect = new ResourceLocation("Echo/MainMenu/rect-test.png");
    private final ResourceLocation hoverCircle = new ResourceLocation("Echo/MainMenu/hover-circle.png");

    private static boolean firstInit = false;

    @Override
    public void initGui() {
        if (!firstInit) {
            NetworkingUtils.bypassSSL();
            firstInit = true;
        }

        if (particleEngine == null) particleEngine = new ParticleEngine();
        if (mc.gameSettings.guiScale != 2) {
            Echo.prevGuiScale = mc.gameSettings.guiScale;
            Echo.updateGuiScale = true;
            mc.gameSettings.guiScale = 2;
            mc.resize(mc.displayWidth - 1, mc.displayHeight);
            mc.resize(mc.displayWidth + 1, mc.displayHeight);
        }
        buttons.forEach(MenuButton::initGui);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        width = sr.getScaledWidth();
        height = sr.getScaledHeight();


        RenderUtil.resetColor();
        RenderUtil.drawImage(backgroundResource, 0, 0, width, height);

        particleEngine.render();

        float rectWidth = 277;
        float rectHeight = 275.5f;

        GaussianBlur.startBlur();
        RoundedUtil.drawRound(width / 2f - rectWidth / 2f, height / 2f - rectHeight / 2f,
                rectWidth, rectHeight, 10, Color.WHITE);
        GaussianBlur.endBlur(40, 2);


        float outlineImgWidth = 688 / 2f;
        float outlineImgHeight = 681 / 2f;
        GLUtil.startBlend();
        RenderUtil.color(-1);
        RenderUtil.drawImage(blurredRect, width / 2f - outlineImgWidth / 2f, height / 2f - outlineImgHeight / 2f,
                outlineImgWidth, outlineImgHeight);

        GL11.glEnable(GL11.GL_BLEND);


        StencilUtil.initStencilToWrite();

        RenderUtil.setAlphaLimit(13);
        buttons.forEach(MenuButton::drawOutline);

        RenderUtil.setAlphaLimit(0);
        StencilUtil.readStencilBuffer(1);


        float circleW = 174 / 2f;
        float circleH = 140 / 2f;
        ResourceLocation rs = new ResourceLocation("Echo/MainMenu/circle-funny.png");
        mc.getTextureManager().bindTexture(rs);
        GLUtil.startBlend();
        RenderUtil.drawImage(rs, mouseX - circleW / 2f, mouseY - circleH / 2f, circleW, circleH);

        StencilUtil.uninitStencilBuffer();


        float buttonWidth = 140;
        float buttonHeight = 25;

        int count = 0;
        for (MenuButton button : buttons) {
            button.x = width / 2f - buttonWidth / 2f;
            button.y = ((height / 2f - buttonHeight / 2f) - 25) + count;
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "Singleplayer":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "Multiplayer":
                        String username = "TenaGayUser";
                        if (username == null || username.trim().isEmpty()) {
                            mc.displayGuiScreen(new GuiSelectWorld(this));
                        } else {
                            mc.displayGuiScreen(new GuiMultiplayer(this));
                        }
                        break;
                    case "Alt Manager":
                        mc.displayGuiScreen(Echo.INSTANCE.getAltManager());
                        break;
                    case "Settings":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "Exit":
                        mc.shutdown();
                        break;
                }
            };
            button.drawScreen(mouseX, mouseY);
            count += buttonHeight + 5;
        }


        float buttonCount = 0;
        float buttonsWidth = (float) textButtons.stream().mapToDouble(TextButton::getWidth).sum();
        int buttonsSize = textButtons.size();
        buttonsWidth += echoFont16.getStringWidth(" | ") * (buttonsSize - 1);

        int buttonIncrement = 0;
        for (TextButton button : textButtons) {
            button.x = width / 2f - buttonsWidth / 2f + buttonCount;
            button.y = (height / 2f) + 120;
            switch (button.text) {
                case "Scripting":
                    button.clickAction = () -> {
                        IOUtils.openLink("https://scripting.tenacity.dev");
                    };
                    break;
                case "Discord":
                    button.clickAction = () -> {
                        IOUtils.openLink("https://tenacity.dev/discord");
                    };
                    break;
            }

            button.addToEnd = (buttonIncrement != (buttonsSize - 1));

            button.drawScreen(mouseX, mouseY);


            buttonCount += button.getWidth() + echoFont14.getStringWidth(" | ");
            buttonIncrement++;
        }

        echoBoldFont80.drawCenteredString("Echo", width / 2f, height / 2f - 110, Color.WHITE.getRGB());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        LoginPanel.cracked = Cloud.getApiKey() == null;
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        textButtons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onGuiClosed() {
        if (Echo.updateGuiScale) {
            mc.gameSettings.guiScale = Echo.prevGuiScale;
            Echo.updateGuiScale = false;
        }
    }

    private static class TextButton implements Screen {
        public float x, y;
        @Getter
        private final float width, height;
        public Runnable clickAction;
        private final String text;

        private final Animation hoverAnimation = new DecelerateAnimation(150, 1);

        public boolean addToEnd;

        public TextButton(String text) {
            this.text = text;
            width = echoFont16.getStringWidth(text);
            height = echoFont16.getHeight();
        }

        @Override
        public void initGui() {

        }

        @Override
        public void keyTyped(char typedChar, int keyCode) {

        }

        @Override
        public void drawScreen(int mouseX, int mouseY) {
            boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
            echoFont16.drawString(text, x, y - (height / 2f * hoverAnimation.getOutput().floatValue()), Color.WHITE.getRGB());
            if (addToEnd) {
                echoFont16.drawString(" | ", x + width, y, Color.WHITE.getRGB());
            }
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int button) {
            boolean hovered = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
            if (hovered && button == 0) {
                clickAction.run();
            }
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY, int state) {

        }
    }

}
