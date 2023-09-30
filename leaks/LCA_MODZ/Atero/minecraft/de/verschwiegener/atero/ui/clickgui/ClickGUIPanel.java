package de.verschwiegener.atero.ui.clickgui;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.design.font.Font;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.ui.clickgui.component.PanelExtendet;
import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class ClickGUIPanel {

    private final String name;
    private final int panelYOffset;
    private final ArrayList<ClickGUIButton> modules = new ArrayList<>();
    private final Font font;
    private final ArrayList<PanelExtendet> ePanels = new ArrayList<>();
    private int x, y, width, yOffset, animationHeight;
    private boolean animate, candrag;
    private int state = 2;
    private double dragx, dragy;
    private PanelExtendet pExtendet;
    private int circleAnimationDiameter = 0;
    private boolean drawCircle;
    private boolean isLongPressed;
    private int circleX, circleY;

    public ClickGUIPanel(final String name, final int x, final int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        panelYOffset = 15;
        font = Management.instance.fontmgr.getFontByName("Inter");
        yOffset = 15;
        animationHeight = yOffset;
        // Sets the base Width
        width = 120;
        for (final Module m : Management.instance.modulemgr.modules) {
            if (m.getCategory().toString().equalsIgnoreCase(name)) {
                // passt die Panel Width an die Module Name Width an
                if (font.getStringWidth(m.getName()) > width) {
                    //width = fontRenderer.getStringWidth(m.getName());
                }
                modules.add(new ClickGUIButton(m.getName(), yOffset, this));
                ePanels.add(new PanelExtendet(m.getName(), yOffset, this));
                yOffset += panelYOffset;
            }
        }
    }

    private void animateExtension(final PanelExtendet pExtendet) {
        TimeUtils timer = new TimeUtils();
        Management.instance.ANIMATION_EXECUTOR.submit(() -> {
            while (pExtendet.isAnimate()) {
                // if (pExtendet.isAnimate()) {
                if (timer.hasReached(5)) {
                    timer.reset();
                    switch (pExtendet.getState()) {
                        case 1:
                            pExtendet.setAnimationX(pExtendet.getAnimationX() + 1);
                            if (pExtendet.getAnimationX() == pExtendet.getWidth() || pExtendet.getAnimationX() > pExtendet.getWidth()) {
                                pExtendet.setAnimate(false);
                                pExtendet.setAnimationX(pExtendet.getWidth());
                                pExtendet.setState(2);
                            }
                            break;
                        case 2:
                            pExtendet.setAnimationX(pExtendet.getAnimationX() - 1);
                            if (pExtendet.getAnimationX() == 17 || pExtendet.getAnimationX() < 17) {
                                pExtendet.setAnimate(false);
                                pExtendet.setAnimationX(17);
                                pExtendet.setState(1);
                            }
                            break;
                    }
                }
                // }
            }
        });
    }

    public void colapseAll() {
        for (final PanelExtendet pe : ePanels) {
            pe.setState(2);
            pe.setAnimate(true);
        }
    }

    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {

        // Drags the panel
        if (candrag) {
            x = (int) (dragx + mouseX);
            y = (int) (dragy + mouseY);
        }
        // draws panel extendet
        ePanels.forEach(panel -> panel.drawScreen(mouseX, mouseY));
        //TODO WhiteMODE
        if (Management.instance.modulemgr.getModuleByName("Design").isEnabled()) {
            RenderUtil.fillRect(x, y, width, animationHeight, new Color(0, 0, 0, 120));
        } else {
            RenderUtil.fillRect(x, y, width, animationHeight, Management.instance.colorBlack);
        }
        if (drawCircle) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            final int scaleFactor = 2;
            GL11.glScissor((x) * scaleFactor, (((y + 15) * scaleFactor) - Minecraft.getMinecraft().displayHeight) / -1, width * scaleFactor, 15 * scaleFactor);
            RenderUtil.drawCircle(x - circleX, y - circleY, circleAnimationDiameter, new Color(48, 48, 48), isLongPressed);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        RenderUtil.fillRect(x, y + getPanelYOffset() * 2, width, 1, Management.instance.colorBlue);
        //TODO WhiteMODE
        if (Management.instance.modulemgr.getModuleByName("Design").isEnabled()) {
            font.drawString(name, x + width / 2 - font.getStringWidth2(name), y, Color.white.getRGB());
        } else {
            font.drawString(name, x + width / 2 - font.getStringWidth2(name), y, Color.white.getRGB());
        }

        if (state == 1) {
            modules.forEach(ClickGUIButton::drawButton);
        }
    }

    public int getAnimationheight() {
        return animationHeight;
    }

    public void setAnimationheight(final int animationheight) {
        animationHeight = animationheight;
    }

    public boolean isExtended() {
        return (animationHeight == yOffset);
    }

    public void setExtended(boolean extend) {
        if (extend) {
            animationHeight = yOffset;
            state = 1;
            System.out.println("Extendet: " + pExtendet);
            //pExtendet.setState(2);
            //pExtendet.setAnimate(false);
        }
    }

    public ClickGUIButton getButtonByPosition(final ClickGUIPanel p, final int x, final int y) {
        return modules.stream().filter(module -> x > p.getX() && x < p.getX() + p.getWidth()
                && y > p.getY() + module.getY() && y < p.getY() + module.getY() + (getPanelYOffset() * 2)).findFirst()
                .orElse(null);
    }

    public ArrayList<ClickGUIButton> getModules() {
        return modules;
    }

    public String getName() {
        return name;
    }

    public PanelExtendet getPanelByModuleName(final String name) {
        return ePanels.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public int getPanelYOffset() {
        return panelYOffset / 2;
    }

    public int getState() {
        return state;
    }

    public void setState(final int state) {
        this.state = state;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getYoffset() {
        return yOffset;
    }

    public boolean isAnimate() {
        return animate;
    }

    public void setAnimate(final boolean animate) {
        this.animate = animate;
    }

    public void setCircleAnimationDiameter(int circleAnimationDiameter) {
        this.circleAnimationDiameter = circleAnimationDiameter;
    }

    public boolean isDrawCircle() {
        return drawCircle;
    }

    public void setDrawCircle(boolean drawCircle) {
        this.drawCircle = drawCircle;
    }

    public void setLongPressed(boolean isLongPressed) {
        this.isLongPressed = isLongPressed;
    }

    public void setCircleX(int circleX) {
        this.circleX = circleX;
    }

    public void setCircleY(int circleY) {
        this.circleY = circleY;
    }

    private boolean isClickGUIPanelHovered(final int mouseX, final int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y - 1 && mouseY < y + (getPanelYOffset() * 2);
    }

    public void keyTyped(final char typedChar, final int keyCode) {
        ePanels.forEach(panel -> panel.onKeyTyped(typedChar, keyCode));
    }

    public void onMouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        ePanels.forEach(panel -> panel.onMouseClicked(mouseX, mouseY, mouseButton));
        switch (mouseButton) {
            case 0:
                if (isClickGUIPanelHovered(mouseX, mouseY)) {
                    // Drags
                    candrag = true;
                    dragx = x - mouseX;
                    dragy = y - mouseY;
                }
                break;
            case 1:
                final ClickGUIButton buttonLeft = getButtonByPosition(this, mouseX, mouseY);
                if (buttonLeft != null) {
                    //buttonLeft.setCircleX(x - mouseX);
                    //buttonLeft.setCircleY((buttonLeft.getY() + y) - mouseY);
                    //drawPressed(true, buttonLeft);
                }
                break;
        }
    }

    private void drawPressed(final boolean longPressed, final ClickGUIButton panel) {
        boolean isAnimate = true;
        final TimeUtils animationTimer = new TimeUtils();
        final int delay = longPressed ? 19 : 5;
        if (!panel.isDrawCircle()) {
            panel.setDrawCircle(true);
            Management.instance.ANIMATION_EXECUTOR.submit(() -> {
                int circlesize = 0;
                while (panel.isDrawCircle()) {
                    if (animationTimer.isDelayComplete(delay)) {
                        animationTimer.reset();
                        circlesize++;
                        panel.setCircleAnimationDiameter(circlesize);
                        if (circlesize >= 50) {
                            panel.setDrawCircle(false);
                            panel.setCircleAnimationDiameter(0);
                        }
                    }
                }
            });
        }
    }

    public void onMouseReleased(final int mouseX, final int mouseY, final int state) {
        if (state == 0) {
            candrag = false;
            final ClickGUIButton buttonRight = getButtonByPosition(this, mouseX, mouseY);
            if (buttonRight != null) {
                if (this.state == 1) {
                    Management.instance.modulemgr.getModuleByName(buttonRight.getName()).toggle();
                }
            }
        }
        if (state == 1) {
            final ClickGUIButton buttonLeft = getButtonByPosition(this, mouseX, mouseY);
            if (buttonLeft != null) {
                if (pExtendet != null && !pExtendet.getName().equalsIgnoreCase(buttonLeft.getName())) {
                    pExtendet.setState(2);
                    pExtendet.setAnimate(true);
                    animateExtension(pExtendet);
                }
                pExtendet = getPanelByModuleName(buttonLeft.getName());
                pExtendet.switchState();
                animateExtension(pExtendet);
            }
        }
        ePanels.forEach(panel -> panel.onMouseReleased(mouseX, mouseY, state));
    }

}
