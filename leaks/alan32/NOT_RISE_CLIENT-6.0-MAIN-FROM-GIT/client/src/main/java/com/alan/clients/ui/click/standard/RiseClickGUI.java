package com.alan.clients.ui.click.standard;

import com.alan.clients.Client;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.impl.render.ClickGUI;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.AlphaEvent;
import com.alan.clients.ui.click.standard.components.ModuleComponent;
import com.alan.clients.ui.click.standard.components.category.SidebarCategory;
import com.alan.clients.ui.click.standard.components.value.ValueComponent;
import com.alan.clients.ui.click.standard.components.value.impl.StringValueComponent;
import com.alan.clients.ui.click.standard.screen.Screen;
import com.alan.clients.ui.click.standard.screen.impl.SearchScreen;
import com.alan.clients.ui.click.standard.screen.impl.ThemeScreen;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.animation.Easing;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.shader.RiseShaders;
import com.alan.clients.util.shader.base.ShaderRenderType;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import packet.impl.client.community.ClientCommunityPopulateRequest;
import util.time.StopWatch;

import java.awt.*;
import java.io.IOException;
import java.text.Collator;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public final class RiseClickGUI extends GuiScreen implements InstanceAccess {

    public Vector2f position = new Vector2f(-1, -1);
    public Vector2f scale = new Vector2f(320 * 1.3f, 260 * 1.3f);

    /* Colors */
    public final Color backgroundColor = new Color(23, 26, 33),
            sidebarColor = new Color(18, 20, 25),
            logoColor = new Color(255, 255, 255),
            fontColor = new Color(255, 255, 255),
            fontDarkColor = new Color(255, 255, 255, 220),
            fontDarkerColor = new Color(255, 255, 255, 40),
            opaqueAccentColor = new Color(68, 134, 240, 160);

    /* Sidebar */
    public SidebarCategory sidebar = new SidebarCategory();

    /* Selected Screen */
    public Screen selectedScreen = Category.SEARCH.getClickGUIScreen();
    public Screen lastScreen = selectedScreen;

    public float draggingOffsetX, draggingOffsetY;
    public boolean dragging;
    public StopWatch timeInCategory = new StopWatch();
    public StopWatch stopwatch = new StopWatch();

    public ConcurrentLinkedQueue<ModuleComponent> moduleList = new ConcurrentLinkedQueue<>();

    public Vector2f mouse;
    public double animationTime, opacity, animationVelocity;

    public int round = 7;

    Vector2d translate;
    public ValueComponent overlayPresent;
    public Vector2f moduleDefaultScale = new Vector2f(285, 38);
    public Animation scaleAnimation = new Animation(Easing.EASE_IN_EXPO, 300);
    public Animation opacityAnimation = new Animation(Easing.EASE_IN_EXPO, 300);

    public void rebuildModuleCache() {
        moduleList.clear();

        java.util.List<Module> sortedModules = Client.INSTANCE.getModuleManager().getAll();
        sortedModules.sort((o1, o2) -> Collator.getInstance().compare(o1.getModuleInfo().name(), o2.getModuleInfo().name()));
        sortedModules.removeIf(Module::isHidden);
        sortedModules.forEach(module -> moduleList.add(new ModuleComponent(module)));
    }

    @Override
    public void initGui() {
        round = 12;
        scaleAnimation.reset();
        scaleAnimation.setValue(0);

        if (moduleList == null || moduleList.isEmpty()) {
            rebuildModuleCache();
        }

        ScaledResolution scaledResolution = mc.scaledResolution;

        lastScreen = selectedScreen;
        timeInCategory.reset();
        Keyboard.enableRepeatEvents(true);
        stopwatch.reset();
        selectedScreen.onInit();

        if (this.position.x < 0 || this.position.y < 0 ||
                this.position.x + this.scale.x > scaledResolution.getScaledWidth() ||
                this.position.y + this.scale.y > scaledResolution.getScaledHeight()) {
            this.position.x = scaledResolution.getScaledWidth() / 2f - this.scale.x / 2;
            this.position.y = scaledResolution.getScaledHeight() / 2f - this.scale.y / 2;
        }

        Client.INSTANCE.getNetworkManager().getCommunication().write(new ClientCommunityPopulateRequest());
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        dragging = false;
    }

    /**
     * If you remove this, expect to see me at your house within 24 hours. I found Alan's house
     * from a sunset picture. Don't think you're safe if you think it's a good idea to remove
     * this again.
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void render() {
        scale = new Vector2f(400, 300);

        if (animationTime > 0.99) renderGUI();

//        RenderUtil.rectangle(0, 0, 2000, 2000, Color.WHITE);

        RiseShaders.ALPHA_SHADER.setAlpha((float) opacity);
        RiseShaders.ALPHA_SHADER.run(ShaderRenderType.OVERLAY, InstanceAccess.mc.timer.renderPartialTicks, null);
    }

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<AlphaEvent> onAlpha = event -> {
        if (animationTime <= 0.99) renderGUI();
    };

    public void renderGUI() {
        if (mouse == null) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        //Information from gui draw screen to use in this event, we use this event instead of gui draw screen because it allows the clickgui to have an outro animation
        final int mouseX = (int) mouse.x;
        final int mouseY = (int) mouse.y;
        final float partialTicks = mc.getTimer().renderPartialTicks;

        /* Handles dragging */
        if (dragging) {

            // I'm a horrible programmer and can't think of a better way to fix this bug
            if (this.selectedScreen instanceof ThemeScreen) {
                ((ThemeScreen) selectedScreen).resetAnimations();
            }

            position.x = mouseX + draggingOffsetX;
            position.y = mouseY + draggingOffsetY;
        }

        opacityAnimation.setEasing(mc.currentScreen == Client.INSTANCE.getStandardClickGUI() ? Easing.EASE_OUT_EXPO : Easing.LINEAR);
        opacityAnimation.setDuration(mc.currentScreen == Client.INSTANCE.getStandardClickGUI() ? 300 : 100);
        opacityAnimation.run(mc.currentScreen == Client.INSTANCE.getStandardClickGUI() ? 1 : 0);
        opacity = opacityAnimation.getValue();

        scaleAnimation.setEasing(mc.currentScreen == Client.INSTANCE.getStandardClickGUI() ? Easing.EASE_OUT_EXPO : Easing.LINEAR);
        scaleAnimation.run(mc.currentScreen == Client.INSTANCE.getStandardClickGUI() ? 1 : 0);
        animationTime = scaleAnimation.getValue();

        if (mc.currentScreen == Client.INSTANCE.getStandardClickGUI() && animationTime == 0) animationTime = 0.01;

        //Makes it not render the ClickGUI if it's animation is 0
        if (animationTime == 0) {
            // TODO: Remove this and only do once
            Client.INSTANCE.getModuleManager().get(ClickGUI.class).toggle();
            return;
        }

        //Opening and closing animation gl
        translate = new Vector2d((position.x + scale.x / 2f) * (1 - animationTime), (position.y + scale.y / 2f) * (1 - animationTime));

        Runnable updatePositionAndScale = () -> {
            GlStateManager.pushMatrix();

//            GlStateManager.translate(50, 0, 0);
            if (animationTime != 1) {
                GlStateManager.translate(translate.x, translate.y, 0);
                GlStateManager.scale(animationTime, animationTime, 0);
            }
        };

        updatePositionAndScale.run();
        UI_BLOOM_RUNNABLES.add(updatePositionAndScale);
        UI_POST_BLOOM_RUNNABLES.add(updatePositionAndScale);


        /* Drop Shadow */
//        RenderUtil.dropShadow(14, position.x, position.y, scale.x, scale.y, 255 / 14f, round - 2);

        sidebar.preRenderClickGUI();

        /* Background */
        RenderUtil.roundedRectangle(position.x + sidebar.sidebarWidth, position.y, scale.x - sidebar.sidebarWidth, scale.y, round, backgroundColor);
        RenderUtil.rectangle(position.x + sidebar.sidebarWidth, position.y, round * 2, scale.y, backgroundColor);
//        RenderUtil.rectangle(position.x + sidebar.sidebarWidth, position.y, 0.5, scale.y, ColorUtil.withAlpha(Color.WHITE, 20));

        /* Stop objects from going outside the ClickGUI */
        Runnable startScissor = () -> {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            int padding = 1;
            RenderUtil.scissor(position.x * animationTime + translate.x + padding, position.y * animationTime + translate.y + padding, scale.x * animationTime - padding * 2, scale.y * animationTime - padding * 2);
        };

        startScissor.run();
        UI_BLOOM_RUNNABLES.add(startScissor);
        UI_POST_BLOOM_RUNNABLES.add(startScissor);

//        double startScale = 50;
//        for (double gradientScale = startScale; gradientScale <= 650; gradientScale += 20) {
//            RenderUtil.roundedRectangle(position.x - 20, position.y + 20 - (gradientScale - startScale) / 2, gradientScale, gradientScale,
//                    gradientScale / 2f, ColorUtil.withAlpha(getTheme().getFirstColor(), 1).brighter());
//        }

        /* Render screen + animations */
        final double animationOffset = -Math.min(0, timeInCategory.getElapsedTime() * 0.02 - 5);
        position.y += animationOffset;

        Runnable translateAnimation = () -> {
            GL11.glPushMatrix();
            GL11.glTranslated(0, animationOffset, 0);
        };

        UI_BLOOM_RUNNABLES.add(translateAnimation);
        UI_POST_BLOOM_RUNNABLES.add(translateAnimation);

        /* Renders screen depending on selected category */
        selectedScreen.onRender(mouseX, mouseY, partialTicks);

        final int opacity2 = 255 - (int) Math.min(255, timeInCategory.getElapsedTime() * 2);
        RenderUtil.roundedRectangle(position.x + sidebar.sidebarWidth, position.y - animationOffset, scale.x - sidebar.sidebarWidth, scale.y, round, new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), opacity2));

        position.y -= animationOffset;

        /* Sidebar */
        sidebar.renderSidebar(mouseX, mouseY);

        Runnable endScissor = () -> {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.popMatrix();
        };

        endScissor.run();
        UI_BLOOM_RUNNABLES.add(endScissor);
        UI_POST_BLOOM_RUNNABLES.add(endScissor);

        Runnable pop = GL11::glPopMatrix;
        UI_BLOOM_RUNNABLES.add(pop);
        UI_POST_BLOOM_RUNNABLES.add(pop);

        stopwatch.reset();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.mouse = new Vector2f(mouseX, mouseY);
    }

    public void bloom() {
        translate = new Vector2d((position.x + scale.x / 2f) * (1 - animationTime), (position.y + scale.y / 2f) * (1 - animationTime));

        GlStateManager.pushMatrix();

        if (animationTime != 1) {
            GlStateManager.translate(translate.x, translate.y, 0);
            GlStateManager.scale(animationTime, animationTime, 0);
        }

        /* Stop objects from going outside the ClickGUI */
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(position.x * animationTime + translate.x, position.y * animationTime + translate.y, scale.x * animationTime, (scale.y - 4) * animationTime);

        selectedScreen.onBloom();
        sidebar.bloom();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        /* Registers click if you click within the window */

        if (GUIUtil.mouseOver(position.x, position.y, scale.x, 15, mouseX, mouseY) && overlayPresent == null) {
            draggingOffsetX = position.x - mouseX;
            draggingOffsetY = position.y - mouseY;
            dragging = true;
        }

        // Only register click if within the ClickGUI
        else if (GUIUtil.mouseOver(position.getX(), position.getY(), scale.getX(), scale.getY(), mouseX, mouseY)) {
            if (overlayPresent == null) sidebar.clickSidebar(mouseX, mouseY, mouseButton);
            selectedScreen.onClick(mouseX, mouseY, mouseButton);
        }

        overlayPresent = null;
    }

    @Override
    protected void mouseReleased(final int mouseX, final int mouseY, final int state) {
        /* Registers the mouse being released */
        dragging = false;

        selectedScreen.onMouseRelease();
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        if ("abcdefghijklmnopqrstuvwxyz1234567890 ".contains(String.valueOf(typedChar).toLowerCase()) && selectedScreen.automaticSearchSwitching() && !getStandardClickGUI().activeText()) {
            this.switchScreen(Category.SEARCH);
        }

        super.keyTyped(typedChar, keyCode);
        selectedScreen.onKey(typedChar, keyCode);
    }

    public void switchScreen(final Category category) {
        if (!category.getClickGUIScreen().equals(this.selectedScreen)) {
            lastScreen = this.getStandardClickGUI().selectedScreen;
            selectedScreen = category.getClickGUIScreen();

            this.timeInCategory.reset();
            selectedScreen.onInit();

            final SearchScreen search = ((SearchScreen) Category.SEARCH.getClickGUIScreen());
            search.relevantModules = search.getRelevantModules(search.searchBar.getText());
        }
    }

    public void switchScreen(final Screen screen) {
        if (!this.selectedScreen.getClass().getSimpleName().equals(screen.getClass().getSimpleName())) {
            lastScreen = this.getStandardClickGUI().selectedScreen;
            selectedScreen = screen;

            this.timeInCategory.reset();
            selectedScreen.onInit();

            final SearchScreen search = ((SearchScreen) Category.SEARCH.getClickGUIScreen());
            search.relevantModules = search.getRelevantModules(search.searchBar.getText());
        }
    }


    public boolean activeText() {
        for (final ModuleComponent moduleComponent : Client.INSTANCE.getStandardClickGUI().getModuleList()) {
            for (final ValueComponent value : moduleComponent.getValueList()) {
                if (value instanceof StringValueComponent && ((StringValueComponent) value).textBox.selected) {
                    return true;
                }
            }
        }

        return false;
    }
}

