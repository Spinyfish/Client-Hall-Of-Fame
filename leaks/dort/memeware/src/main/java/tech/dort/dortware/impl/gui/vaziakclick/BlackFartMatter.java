package tech.dort.dortware.impl.gui.vaziakclick;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.enums.ModuleCategory;
import tech.dort.dortware.impl.gui.vaziakclick.element.impl.CategoryPane;
import tech.dort.dortware.impl.gui.vaziakclick.element.impl.ModulePane;
import tech.dort.dortware.impl.gui.vaziakclick.element.impl.setting.Setting;
import tech.dort.dortware.impl.modules.render.ClickGUI;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlackFartMatter extends GuiScreen {

    private final List<CategoryPane> categoryPanes = new CopyOnWriteArrayList<>();
    private CategoryPane selected;
    private boolean otherSelected;
    private boolean otherSliderSelected = false;
    private int prevMouseX = 0, prevMouseY = 0;

    public BlackFartMatter() {
        int cx = 20;
        for (ModuleCategory category : ModuleCategory.values()) {
            if (category != ModuleCategory.HIDDEN) {
                categoryPanes.add(new CategoryPane(category, cx, 20));
                cx += 120;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            this.handleInput();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClickGUI clickGUI = Client.INSTANCE.getModuleManager().get(ClickGUI.class);

        if (clickGUI.booleanValue2.getValue()) {
            ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

            double red1 = clickGUI.red.getCastedValue();
            double green1 = clickGUI.green.getCastedValue();
            double blue1 = clickGUI.blue.getCastedValue();
            double alpha1 = clickGUI.alpha.getCastedValue();

            int startColor = new Color(0, 0, 0, 0).getRGB();
            int endColor = new Color((float) red1 / 255.0f, (float) green1 / 255.0f, (float) blue1 / 255.0f, (float) alpha1 / 255f).getRGB();
            Gui.drawGradientRectDiagonal(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), startColor, endColor);
        }

        categoryPanes.forEach(categoryPane -> categoryPane.onDraw(mouseX, mouseY, partialTicks));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (CategoryPane categoryPane : categoryPanes) {
            for (ModulePane modulePane : categoryPane.getModPanes()) {
                modulePane.keyTyped(typedChar, keyCode);
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        if (mc.entityRenderer.theShaderGroup != null) {
            mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            mc.entityRenderer.theShaderGroup = null;
        }
        selected = null;
        otherSelected = false;
        otherSliderSelected = false;
        setPrevCursor(0, 0);
        resetMouse();
        super.onGuiClosed();
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        for (CategoryPane categoryPane : categoryPanes) {
            for (ModulePane modulePane : categoryPane.getModPanes()) {
                for (Setting setting : modulePane.getSettingPane().getSettings()) {
                    boolean otherSelected = setting.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick, otherSliderSelected);
                    if (otherSelected) {
                        otherSliderSelected = true;
                        selected = null;
                        this.otherSelected = true;
                    }
                }
            }
        }

        if (isPrevCursorSet()) {
            setPrevCursor(mouseX, mouseY);
        }
        if (selected != null && (mouseX == 0 || mouseY == 0)) {
            {
                selected = null;
            }
        }
        for (int i = categoryPanes.size() - 1; i >= 0; i--) {
            CategoryPane pane = categoryPanes.get(i);
            boolean headerSelected = GuiUtils.isHeaderHovering(mouseX, mouseY, pane);
            if ((((headerSelected) || selected == pane) && !otherSelected) && clickedMouseButton == 0) {
                if (selected == null) {
                    prevMouseX = mouseX;
                    prevMouseY = mouseY;
                }
                selected = pane;
                //cringe but efficient way to move to the back of the list
                categoryPanes.remove(pane);
                categoryPanes.add(pane);
                pane.movePane(mouseX - prevMouseX, mouseY - prevMouseY);
                setPrevCursor(mouseX, mouseY);
                break;
            }
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // mouseClickMove(mouseX, mouseY, mouseButton, 0);
        for (CategoryPane pane : categoryPanes) {
            if (GuiUtils.isHovering(mouseX, mouseY, pane)) {
                pane.mouseClicked(mouseX, mouseY, mouseButton);
            }
            boolean headerSelected = GuiUtils.isHeaderHovering(mouseX, mouseY, pane);
            if (headerSelected && mouseButton == 1) {
                pane.toggleExpansion();
            }
        }
        selected = null;
        otherSelected = false;
        otherSliderSelected = false;
        resetMouse();
        setPrevCursor(0, 0);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void setPrevCursor(int x, int y) {
        prevMouseX = x;
        prevMouseY = y;
    }

    private boolean isPrevCursorSet() {
        return prevMouseX == 0 || prevMouseY == 0;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (CategoryPane pane : categoryPanes) {
            for (ModulePane modulePane : pane.getModPanes()) {
                modulePane.getSettingPane().getSettings().forEach(setting -> setting.mouseReleased(mouseX, mouseY, state));
            }
            selected = null;
            otherSelected = false;
            setPrevCursor(0, 0);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    private void resetMouse() {
        int var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int var3 = Mouse.getEventButton();
        this.mouseReleased(var1, var2, var3);
    }

    public List<CategoryPane> getCategoryPanes() {
        return categoryPanes;
    }
}