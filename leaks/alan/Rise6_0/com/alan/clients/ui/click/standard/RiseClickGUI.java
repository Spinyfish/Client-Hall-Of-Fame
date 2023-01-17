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
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.gui.GUIUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.shader.RiseShaders;
import com.alan.clients.util.shader.base.ShaderRenderType;
import packet.impl.client.community.ClientCommunityMessageSend;
import util.time.StopWatch;
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

import java.awt.*;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;

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

    public ArrayList<ModuleComponent> moduleList = new ArrayList<>();

    public Vector2f mouse;
    public double animationTime, animationVelocity;

    public int round = 7;

    Vector2d translate;
    public ValueComponent overlayPresent;
    public Vector2f moduleDefaultScale = new Vector2f(285, 38);

    public void rebuildModuleCache() {
        moduleList.clear();

        java.util.List<Module> sortedModules = Client.INSTANCE.getModuleManager().getAll();
        sortedModules.sort((o1, o2) -> Collator.getInstance().compare(o1.getModuleInfo().name(), o2.getModuleInfo().name()));
        sortedModules.removeIf(Module::isHidden);
        sortedModules.forEach(module -> moduleList.add(new ModuleComponent(module)));
    }

    @Override
    public void initGui() {
        animationTime = 0.08;

        if (moduleList == null || moduleList.isEmpty()) rebuildModuleCache();
        ScaledResolution scaledResolution = new ScaledResolution(mc);

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

        Client.INSTANCE.getNetworkManager().getCommunication().write(new ClientCommunityMessageSend("Test Message"));
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
        /**
         * WARNING: MODIFY THIS SCALE AND I PERSONALLY COME TO YOUR HOUSE
         * AND EXECUTE YOU IMMEDIATELY (TRY IT, I FUCK YOU OVER)
         */
        // but what if i want daddy alan to fuck me

        scale = new Vector2f(400, 300);
        ScaledResolution scaledResolution = new ScaledResolution(mc);

        // Background
        RenderUtil.rectangle(0, 0, scaledResolution.getScaledWidth(),
                scaledResolution.getScaledHeight(), ColorUtil.withAlpha(backgroundColor, (int) (animationTime * 100)));

        RiseShaders.ALPHA_SHADER.setAlpha((float) animationTime);
        RiseShaders.ALPHA_SHADER.run(ShaderRenderType.OVERLAY, InstanceAccess.mc.timer.renderPartialTicks, null);
    }

    @EventLink(value = Priorities.VERY_LOW)
    public final Listener<AlphaEvent> onAlpha = event -> {
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

        for (int i = 0; i < stopwatch.getElapsedTime(); ++i) {
            if (mc.currentScreen == Client.INSTANCE.getStandardClickGUI()) {
                animationTime = MathUtil.lerp(animationTime, 1, 3.5E-2F);
            } else {
                animationTime = Math.max(0, animationTime - 1E-2F);
            }
        }

        //Makes it not render the ClickGUI if it's animation is 0
        if (animationTime < 0.08) {
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
        NORMAL_POST_BLOOM_RUNNABLES.add(updatePositionAndScale);
        NORMAL_ABOVE_BLOOM_RUNNABLES.add(updatePositionAndScale);


        /* Drop Shadow */
//        RenderUtil.dropShadow(15, position.x, position.y, scale.x, scale.y, 100, round * 2);

        /* Background */
        RenderUtil.roundedRectangle(position.x, position.y, scale.x, scale.y, round, backgroundColor);

        /* Stop objects from going outside the ClickGUI */
        Runnable startScissor = () -> {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(position.x * animationTime + translate.x, position.y * animationTime + translate.y, scale.x * animationTime, scale.y * animationTime);
        };

        startScissor.run();
        NORMAL_POST_BLOOM_RUNNABLES.add(startScissor);
        NORMAL_ABOVE_BLOOM_RUNNABLES.add(startScissor);

        /* Render screen + animations */
        final double animationOffset = -Math.min(0, timeInCategory.getElapsedTime() * 0.02 - 5);
        position.y += animationOffset;

        double startScale = 50;
        for (double gradientScale = startScale; gradientScale <= 650; gradientScale += 80) {
            RenderUtil.roundedRectangle(position.x + 300 - (gradientScale - startScale) / 2, position.y - 100 - (gradientScale - startScale) / 2, gradientScale, gradientScale,
                    gradientScale / 2f, ColorUtil.withAlpha(getTheme().getFirstColor(), 1));
        }

        Runnable translateAnimation = () -> {
            GL11.glPushMatrix();
            GL11.glTranslated(0, animationOffset, 0);
        };

        NORMAL_POST_BLOOM_RUNNABLES.add(translateAnimation);
        NORMAL_ABOVE_BLOOM_RUNNABLES.add(translateAnimation);

        /* Renders screen depending on selected category */
        selectedScreen.onRender(mouseX, mouseY, partialTicks);

        final int opacity2 = 255 - (int) Math.min(255, timeInCategory.getElapsedTime() * 2);
        RenderUtil.roundedRectangle(position.x, position.y - animationOffset, scale.x, scale.y, round, new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), opacity2));

        position.y -= animationOffset;

        /* Sidebar */
        sidebar.renderSidebar(mouseX, mouseY);

        Runnable endScissor = () -> {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.popMatrix();
        };

        endScissor.run();
        NORMAL_POST_BLOOM_RUNNABLES.add(endScissor);
        NORMAL_ABOVE_BLOOM_RUNNABLES.add(endScissor);

        Runnable pop = GL11::glPopMatrix;
        NORMAL_POST_BLOOM_RUNNABLES.add(pop);
        NORMAL_ABOVE_BLOOM_RUNNABLES.add(pop);

        stopwatch.reset();
    };

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

