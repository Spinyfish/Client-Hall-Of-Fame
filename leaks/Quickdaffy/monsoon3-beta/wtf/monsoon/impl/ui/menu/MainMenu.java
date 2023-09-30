/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.ui.menu;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.BlurUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.ui.ScalableScreen;
import wtf.monsoon.impl.ui.login.LoginScreen;
import wtf.monsoon.impl.ui.menu.MenuButton;
import wtf.monsoon.impl.ui.menu.windows.AltWindow;
import wtf.monsoon.impl.ui.menu.windows.FirstRunWindow;
import wtf.monsoon.impl.ui.menu.windows.WelcomeWindow;
import wtf.monsoon.impl.ui.menu.windows.Window;
import wtf.monsoon.impl.ui.particle.ParticleSystem;
import wtf.monsoon.impl.ui.primitive.Click;

public class MainMenu
extends ScalableScreen {
    private final Animation openAnimation = new Animation(() -> Float.valueOf(800.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final Animation deleteWindowAnimation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final Animation creditsAnimation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);
    private final List<MenuButton> buttons = new ArrayList<MenuButton>();
    private final List<Window> windows = new CopyOnWriteArrayList<Window>();
    private ParticleSystem particleSystem;
    private final WindowBar windowBar = new WindowBar(this);

    public MainMenu() {
        Wrapper.getEventBus().subscribe(this);
    }

    @Override
    public void init() {
        if (!Wrapper.loggedIn) {
            this.mc.displayGuiScreen(new LoginScreen());
        }
        this.particleSystem = new ParticleSystem();
        this.buttons.clear();
        this.buttons.add(new MenuButton("singleplayer", () -> this.mc.displayGuiScreen(new GuiSelectWorld(this)), this.getScaledWidth() / 2.0f - 140.0f, this.getScaledHeight() - 120.0f, 68.0f, 68.0f));
        this.buttons.add(new MenuButton("multiplayer", () -> this.mc.displayGuiScreen(new GuiMultiplayer(this)), this.getScaledWidth() / 2.0f - 70.0f, this.getScaledHeight() - 120.0f, 68.0f, 68.0f));
        this.buttons.add(new MenuButton("options", () -> this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings)), this.getScaledWidth() / 2.0f, this.getScaledHeight() - 120.0f, 68.0f, 68.0f));
        this.buttons.add(new MenuButton("quit", () -> this.mc.shutdown(), this.getScaledWidth() / 2.0f + 70.0f, this.getScaledHeight() - 120.0f, 68.0f, 68.0f));
        this.windows.clear();
        this.windows.add(new WelcomeWindow(5.0f, 5.0f, 200.0f, 150.0f, 14.0f));
        File checkFile = new File(this.mc.mcDataDir + "\\Monsoon");
        if (!checkFile.exists()) {
            this.windows.add(new FirstRunWindow(5.0f, 200.0f, 200.0f, 150.0f, 14.0f));
        }
        this.windows.add(new AltWindow(this, this.getScaledWidth() - 205.0f, 5.0f, 200.0f, 150.0f, 14.0f));
    }

    @Override
    public void render(float mouseX, float mouseY) {
        this.windows.removeIf(Window::shouldWindowClose);
        this.openAnimation.setState(true);
        this.creditsAnimation.setState(mouseX > this.getScaledWidth() / 2.0f - 140.0f && mouseX < this.getScaledWidth() / 2.0f + 140.0f && mouseY > 40.0f && mouseY < 80.0f);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("monsoon/background.png"));
        Gui.drawModalRectWithCustomSizedTexture(0.0f, 0.0f, 0.0f, 0.0f, this.getScaledWidth(), this.getScaledHeight(), this.getScaledWidth(), this.getScaledHeight());
        BlurUtil.alternateBlur(0.0f, 0.0f, this.getScaledWidth(), this.getScaledHeight(), (int)(4.0 * this.openAnimation.getAnimationFactor()));
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
        Wrapper.getFontUtil().greycliff40.drawCenteredString("Monsoon", this.getScaledWidth() / 2.0f / 2.0f, (float)(-40.0 + 60.0 * this.openAnimation.getAnimationFactor()), Color.WHITE, true);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        Wrapper.getFontUtil().greycliff26.drawString(Wrapper.getMonsoon().getVersion(), (float)((double)(this.getScaledWidth() / 2.0f + 85.0f) + ((double)(this.getScaledWidth() / 2.0f) - (double)(this.getScaledWidth() / 2.0f) * this.openAnimation.getAnimationFactor())), 45.0f, Color.WHITE, true);
        CharSequence[] devs = new String[]{"quick", "surge", "Shoroa_", "NanoS (8 commits :OOO)", "and YesCheatPlus"};
        String concatenated = "Brought to you by: " + System.lineSeparator() + String.join((CharSequence)", ", devs);
        Wrapper.getFont().drawString(concatenated, 0.0f, this.scaledHeight - 3.0f - (float)(Wrapper.getFont().getHeight() * 2), new Color(255, 255, 255, 150), true);
        float increase = 0.0f;
        for (MenuButton button : this.buttons) {
            button.setY((float)((double)this.getScaledHeight() - (120.0 + ((double)increase - (double)increase * this.openAnimation.getAnimationFactor())) * this.openAnimation.getAnimationFactor()));
            button.render(mouseX, mouseY);
            increase += 20.0f;
        }
        this.deleteWindowAnimation.setState(false);
        for (Window window : this.windows) {
            if (window.isDragging()) {
                this.deleteWindowAnimation.setState(true);
            }
            window.render(mouseX, mouseY);
        }
        RenderUtil.drawGradientRect(0.0f, this.getScaledHeight() - 50.0f, this.getScaledWidth(), 50.0f, new Color(0, 0, 0, 0).getRGB(), new Color(10, 10, 15, (int)(255.0 * this.deleteWindowAnimation.getAnimationFactor())).getRGB());
        float multiply = 0.5f;
        for (Window window : this.windows) {
            if (!window.isDragging() || !(window.getY() > this.getScaledHeight() - 50.0f)) continue;
            multiply = 0.8f;
            break;
        }
        Wrapper.getFont().drawCenteredString("Drag here to remove window", this.getScaledWidth() / 2.0f, this.getScaledHeight() - (float)this.deleteWindowAnimation.getAnimationFactor() * 20.0f, new Color(1.0f, 1.0f, 1.0f, 0.1f + (float)this.deleteWindowAnimation.getAnimationFactor() * multiply), false);
        this.windowBar.draw((int)mouseX, (int)mouseY);
    }

    @Override
    public void click(float mouseX, float mouseY, int mouseButton) {
        this.buttons.forEach(button -> button.mouseClicked(mouseX, mouseY));
        this.windows.forEach(window -> window.mouseClicked(mouseX, mouseY, Click.getClick(mouseButton)));
        this.windowBar.mouseClicked((int)mouseX, (int)mouseY);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.windows.removeIf(window -> window.isDragging() && window.getY() > this.getScaledHeight() - 50.0f);
        this.windows.forEach(Window::mouseReleased);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.windows.forEach(window -> window.keyTyped(typedChar, keyCode));
    }

    @Override
    public void onGuiClosed() {
        this.openAnimation.resetToDefault();
    }

    public List<Window> getWindows() {
        return this.windows;
    }

    private static class WindowElement {
        private final String name;
        private final Runnable create;

        public WindowElement(String name, Runnable create) {
            this.name = name;
            this.create = create;
        }

        public void create() {
            this.create.run();
        }

        public String getName() {
            return this.name;
        }
    }

    private static class WindowBar {
        private final MainMenu menu;
        private final List<WindowElement> elements;
        private final Animation hover = new Animation(200.0f, false, Easing.LINEAR);

        public WindowBar(MainMenu menu) {
            this.menu = menu;
            this.elements = new ArrayList<WindowElement>();
            this.elements.addAll(Arrays.asList(new WindowElement("Welcome", () -> {
                if (this.menu.getWindows().stream().anyMatch(window -> window instanceof WelcomeWindow)) {
                    this.menu.getWindows().removeIf(window -> window instanceof WelcomeWindow);
                } else {
                    this.menu.getWindows().add(new WelcomeWindow(5.0f, 5.0f, 200.0f, 150.0f, 14.0f));
                }
            }), new WindowElement("Alt Manager", () -> {
                if (this.menu.getWindows().stream().anyMatch(window -> window instanceof AltWindow)) {
                    this.menu.getWindows().removeIf(window -> window instanceof AltWindow);
                } else {
                    this.menu.getWindows().add(new AltWindow(this.menu, 5.0f, 5.0f, 200.0f, 150.0f, 14.0f));
                }
            })));
        }

        public void draw(int mouseX, int mouseY) {
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            float largest = 0.0f;
            for (WindowElement element : this.elements) {
                if (!((float)Wrapper.getFont().getStringWidth(element.name) > largest)) continue;
                largest = Wrapper.getFont().getStringWidth(element.name);
            }
            this.hover.setState(mouseY <= this.elements.size() * (Wrapper.getFont().getHeight() + 2));
            RenderUtil.drawRect((float)scaledResolution.getScaledWidth() / 2.0f - (largest + 5.0f) / 2.0f, (double)(-(this.elements.size() * (Wrapper.getFont().getHeight() + 2))) + (double)(this.elements.size() * (Wrapper.getFont().getHeight() + 2)) * this.hover.getAnimationFactor(), largest + 10.0f, this.elements.size() * (Wrapper.getFont().getHeight() + 2), -1879048192);
            float y = (float)((double)(-(this.elements.size() * (Wrapper.getFont().getHeight() + 2) + 2)) + (double)(this.elements.size() * (Wrapper.getFont().getHeight() + 2)) * this.hover.getAnimationFactor() + 2.0);
            for (WindowElement element : this.elements) {
                Wrapper.getFont().drawCenteredString(element.name, (float)scaledResolution.getScaledWidth() / 2.0f + 2.0f, y, Color.WHITE, false);
                y += (float)(Wrapper.getFont().getHeight() + 2);
            }
        }

        public void mouseClicked(int mouseX, int mouseY) {
            if (this.hover.getState()) {
                ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
                float largest = 0.0f;
                for (WindowElement element : this.elements) {
                    if (!((float)Wrapper.getFont().getStringWidth(element.name) > largest)) continue;
                    largest = Wrapper.getFont().getStringWidth(element.name);
                }
                float y = (float)((double)(-(this.elements.size() * (Wrapper.getFont().getHeight() + 2) + 2)) + (double)(this.elements.size() * (Wrapper.getFont().getHeight() + 2)) * this.hover.getAnimationFactor() + 2.0);
                for (WindowElement element : this.elements) {
                    if ((float)mouseX >= (float)scaledResolution.getScaledWidth() / 2.0f + 2.0f - (float)Wrapper.getFont().getStringWidth(element.name) / 2.0f && (float)mouseX <= (float)scaledResolution.getScaledWidth() / 2.0f + 2.0f + (float)Wrapper.getFont().getStringWidth(element.name) / 2.0f && (float)mouseY >= y && (float)mouseY <= y + (float)Wrapper.getFont().getHeight()) {
                        element.create();
                    }
                    y += (float)(Wrapper.getFont().getHeight() + 2);
                }
            }
        }
    }
}

