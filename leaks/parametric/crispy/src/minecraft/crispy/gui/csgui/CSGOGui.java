package crispy.gui.csgui;

import arithmo.gui.altmanager.Colors;
import crispy.Crispy;
import crispy.gui.csgui.point.Point;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.impl.render.ClickGui;
import crispy.fonts.decentfont.FontUtil;
import crispy.fonts.decentfont.MinecraftFontRenderer;
import crispy.util.animation.Translate;
import crispy.util.animation.animations.impl.SmoothStepAnimation;
import crispy.util.render.gui.RenderUtil;
import crispy.util.rotation.LookUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;
import net.superblaubeere27.valuesystem.Value;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.glEnable;


public class CSGOGui extends GuiScreen {


    private final ArrayList<ModeValue> extendedModes = new ArrayList<>();
    private final ArrayList<Hack> extendedHacks = new ArrayList<>();
    public int startX, startY;
    public boolean dragging;
    @Getter
    @Setter
    public SmoothStepAnimation openingAnimation;
    MinecraftFontRenderer fr;
    MinecraftFontRenderer small;
    private int dragPosX;
    private int dragPosy;
    private Hack settingHack;
    private int scroll;
    private Point point = new Point(0, 0);
    private Translate sliderTranslate = new Translate(0, 0);
    private NumberValue sliding;
    private double sliderWidth;
    private Category category;
    private int lastscroll;
    private final Translate opacity = new Translate(0, 0);
    private Translate translate = new Translate(0, 0);


    public CSGOGui() {
        startX = 50;
        startY = 50;
        dragPosX = 50;
        dragPosy = 50;
    }

    private static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void initGui() {
        opacity.setX(0);

        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public void handle(int mouseX, int mouseY, int button, Handle type) {

        float left = dragPosX,
                top = dragPosy,
                width = 90,
                right = left + width;


        small = FontUtil.cleanSmall;
        fr = FontUtil.clean;

        Color base = new Color(39, 42, 49, 255);
        Color sideBar = new Color(38, 39, 44, 255);
        Color slider = new Color(68, 96, 140, 255);
        Color open = new Color(47, 50, 56, 255);
        Color closed = new Color(44, 47, 53, 255);
        Color selectedSideBar = new Color(68, 137, 246, 255);
        if (ClickGui.theme.getMode().equalsIgnoreCase("Purple")) {
            base = new Color(43, 39, 49, 255);
            sideBar = new Color(40, 38, 44, 255);
            slider = new Color(97, 68, 140, 255);
            selectedSideBar = new Color(140, 68, 240, 255);
            open = new Color(51, 47, 56, 255);
            closed = new Color(47, 45, 53, 255);

        } else if (ClickGui.theme.getMode().equalsIgnoreCase("Red")) {
            sideBar = new Color(44, 38, 38, 255);
            base = new Color(49, 39, 39, 255);
            closed = new Color(50, 45, 47, 255);
            open = new Color(56, 47, 47, 255);
            slider = new Color(140, 68, 68, 255);
            selectedSideBar = new Color(240, 68, 68, 255);
        }
        float translationFactor = (float) (14.4 / (float) Minecraft.getDebugFPS());
        Minecraft.getMinecraft().gameSettings.enableVsync = true;
        if (type == Handle.DRAW) {
            if (ClickGui.dark.getObject()) {
                opacity.interpolate(200, 100, translationFactor);
                Gui.drawRect(0, 0, ScaledResolution.getScaledWidth(), ScaledResolution.getScaledHeight(), Colors.getColor(0, 0, 0, (int) opacity.getX()));
            }
        }
        RenderUtil.drawRoundedRect(left, top, right + 240, top + 270, 10, base.getRGB());
        RenderUtil.drawLeftRounded(left, top, right, top + 270, 10, sideBar.getRGB());

        /*
         * Drawing cats
         */

        if (category != null) {
            translate.interpolate(point.getX(), point.getY(), translationFactor);
            RenderUtil.drawRoundedRect(left, top + translate.getY() - 4, left + translate.getX() + 56, top + translate.getY() + 17, 0, selectedSideBar.getRGB());
        }

        int number = 20;
        int add = 20;
        Crispy.INSTANCE.getFontManager().getFont("ROBO 30").drawCenteredString("Rice", left + 38, top, -1);
        for (Category category : Category.values()) {
            glEnable(3042);
            switch (category.getName().toLowerCase()) {
                case "combat": {
                    mc.getTextureManager().bindTexture(new ResourceLocation("Client/gui/combat.png"));
                    Gui.drawModalRectWithCustomSizedTexture(left + 5, top + number, 10, 10, 10, 10, 10, 10);
                    Crispy.INSTANCE.getFontManager().getFont("Applefnt 10").drawString("Combat", left + 15, top + number, -1);

                    break;
                }
                case "movement": {
                    mc.getTextureManager().bindTexture(new ResourceLocation("Client/gui/movement.png"));
                    Gui.drawModalRectWithCustomSizedTexture(left + 5, top + number, 10, 10, 10, 10, 10, 10);
                    Crispy.INSTANCE.getFontManager().getFont("Applefnt 10").drawString("Movement", left + 15, top + number, -1);
                    break;
                }
                case "player": {
                    mc.getTextureManager().bindTexture(new ResourceLocation("Client/gui/player.png"));
                    Gui.drawModalRectWithCustomSizedTexture(left + 5, top + number, 10, 10, 10, 10, 10, 10);
                    Crispy.INSTANCE.getFontManager().getFont("Applefnt 10").drawString("Player", left + 15, top + number + 1, -1);
                    break;
                }
                case "render": {
                    mc.getTextureManager().bindTexture(new ResourceLocation("Client/gui/render.png"));

                    Gui.drawModalRectWithCustomSizedTexture(left + 5, top + number, 14, 14, 12, 14, 12, 14);
                    Crispy.INSTANCE.getFontManager().getFont("Applefnt 10").drawString("Render", left + 15, top + number + 1, -1);
                    break;
                }
                case "misc": {
                    mc.getTextureManager().bindTexture(new ResourceLocation("Client/gui/misc.png"));
                    Gui.drawModalRectWithCustomSizedTexture(left + 4, top + number, 12, 12, 12, 12, 12, 12);
                    Crispy.INSTANCE.getFontManager().getFont("Applefnt 10").drawString("Misc", left + 15, top + number + 1, -1);
                    break;
                }
                case "configs": {
                    mc.getTextureManager().bindTexture(new ResourceLocation("Client/gui/profiles.png"));
                    Gui.drawModalRectWithCustomSizedTexture(left + 4, top + number, 12, 12, 12, 12, 12, 12);
                    Crispy.INSTANCE.getFontManager().getFont("Applefnt 10").drawString("Config", left + 15, top + number + 1, -1);
                    break;
                }
            }
            number += add;
        }


        if (category != null) {

            float count = (float) category.getCurrentScroll().getY();
            int nextHeight = (int) (category.getCurrentScroll().getY() + 20);
            double maxScroll = (category.getMaxScroll() - 270);
            if (maxScroll < 0) {
                maxScroll = 0;
            }

            if (-category.getCurrentScroll().getY() > maxScroll || -category.getCurrentScroll().getY() < -1) {
                if (-category.getCurrentScroll().getY() < -1) {
                    scroll -= 5;
                } else {
                    scroll = (int) -(maxScroll) + 1;
                }
            }
            small.drawString(category.getName(), right + 10, top + 10, -1);
            category.getCurrentScroll().interpolate(0, -1 + scroll, translationFactor);
            glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.prepareScissorBox(left, top + 20, right + 300, top + 270);
            for (Hack hack : Crispy.INSTANCE.getHackManager().getModules(category)) {
                count += 30;

                int addS = extendedHacks.contains(hack) ? ((int) Crispy.INSTANCE.getValueManager().getAllValuesFrom(hack.getName()).stream().filter(Value::isVisible).count() * 12) + 2 : 0;
                hack.getGui().interpolate(addS, 0, translationFactor);
                nextHeight += hack.getGui().getX();
                boolean isHovered = isHovered(left + 105, top + (count - 8), right + 220, top + 25 + (count - 8), mouseX, mouseY);

                GlStateManager.color(1, 1, 1);
                boolean extended = extendedHacks.contains(hack);
                RenderUtil.drawRoundedRect(left + 95, top + (count - 8), right + 230, top + 25 + (count - 8) + hack.getGui().getX(), 5, extended ? open.getRGB() : closed.getRGB());
                Crispy.INSTANCE.getFontManager().getFont("clean 20").drawString(hack.getName(), left + 100, top + count, hack.isEnabled() ? selectedSideBar.darker().getRGB() : -1);
                if (extendedHacks.contains(hack)) {
                    for (Value value : Crispy.INSTANCE.getValueManager().getAllValuesFrom(hack.getName())) {

                        if (value.isVisible()) {
                            count += 12;

                            if (value instanceof BooleanValue) {
                                BooleanValue set = (BooleanValue) value;
                                boolean hovered = isHovered(left + 100, top + count + 10, right + 300, top + count + 20, mouseX, mouseY);

                                small.drawStringWithShadow(value.getName(), left + 100, (float) LookUtils.round(top + count + 10, 4), -1);

                                if (set.getObject()) {
                                    Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Client/gui/SliderDot.png"));
                                    Gui.drawModalRectWithCustomSizedTexture(small.getStringWidth(value.getName()) + left + 103.5, top + count + 10.5, 10, 5, 5, 5, 5, 5);

                                }
                                RenderUtil.drawCircle(small.getStringWidth(value.getName()) + left + 106, LookUtils.round(top + count + 13, 8), 3, -1);
                                //Gui.drawRect(left + 300, top + index + 30, right + 400, top + index + 50, color.darker().getRGB());
                                ///Gui.drawRect(left + 400, top + index, right + 300, top + 1, new Color(255, 255, 255).getRGB());
                                if (hovered && type == Handle.CLICK && button == 0) {
                                    set.setObject(!set.getObject());
                                }

                            }
                            if (value instanceof ModeValue) {

                                ModeValue set = (ModeValue) value;
                                boolean hovered = isHovered(left + 100, top + count + 10, right + 300, top + count + 20, mouseX, mouseY);
                                double lol = top + count + 10;
                                small.drawStringWithShadow(set.getName() + " " + set.getMode(), left + 100, (float) LookUtils.round(lol, 4), -1);
                                if (hovered && button == 0 && type == Handle.CLICK) {
                                    set.cycle();
                                }
                                if (hovered && button == 1 && type == Handle.CLICK) {
                                    set.cycleReverse();
                                }
                            }
                            if (value instanceof NumberValue) {
                                NumberValue set = (NumberValue) value;
                                Number min = set.getMin();
                                Number max = set.getMax();
                                Number val = ((Number) set.getObject());
                                double renderWidth = (88) * (val.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());

                                final double offset = small.getStringWidth(set.getName()) + 105;
                                if(Math.abs(renderWidth - set.getTranslate().getX()) >= 1) {
                                    set.getTranslate().interpolate(renderWidth, 0, translationFactor);
                                }


                                boolean hovered = isHovered((float) (left + offset), top + count + 11, right + 300, top + count + 16, mouseX, mouseY);
                                RenderUtil.drawRoundedRect(left + offset, top + count + 12, left + offset + 88, top + count + 15, 0, slider.getRGB());
                                //Gui.drawRect(left + 308, top + index + 20, right + 220 + 88, top + index + 25, hovered ?  new Color(30, 30, 45).getRGB(): new Color(30, 30, 45).brighter().getRGB());

                                GL11.glColor4f(66, 123, 184, 1);
                                final double theLeft = (left + set.getTranslate().getX());


                                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Client/gui/SliderDot.png"));
                                Gui.drawModalRectWithCustomSizedTexture(roundToPlace(theLeft + offset, 4), top + count + 11, 5, 5, 5, 5, 5, 5);

                                small.drawStringWithShadow(set.getName(), left + 100, (float) LookUtils.round(top + count + 10, 4), -1);
                                small.drawStringWithShadow(set.getObject() + "", left + offset + 95, (float) LookUtils.round(top + count + 10, 4), -1);


                                if (hovered && type == Handle.CLICK && button == 0) {
                                    sliding = set;
                                    this.sliderWidth = offset;
                                }
                                if (type == Handle.RELEASE && button == 0) {
                                    sliding = null;
                                }


                            }

                        }
                    }
                }


                if (isHovered && button == 0 && type == Handle.CLICK) {
                    hack.setState(!hack.isEnabled());
                }
                if (isHovered && button == 1 && type == Handle.CLICK) {
                    if (!extendedHacks.contains(hack)) extendedHacks.add(hack);
                    else extendedHacks.remove(hack);

                    Crispy.INSTANCE.getValueManager().getAllValuesFrom(hack.getName()).stream().filter(value -> value instanceof NumberValue).forEach(value -> ((NumberValue<?>) value).setTranslate(new Translate(0, 0)));
                    break;
                }

            }
            if (category != null) {
                category.setMaxScroll(nextHeight - category.getCurrentScroll().getY() + (Crispy.INSTANCE.getHackManager().getModules(category).size() + 1) * 30);
            }


            GL11.glDisable(GL11.GL_SCISSOR_TEST);

        }

        double diff = Math.min(88, Math.max(0, mouseX - (left + sliderWidth)));
        if (sliding != null) {

            Number min = sliding.getMin();
            Number max = sliding.getMax();
            Number val = ((Number) sliding.getObject());
            if (diff == 0) {
                sliding.setObject(sliding.getMin());
            } else {
                double renderWidth = (66) * (val.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
                if (sliding.getObject() instanceof Integer) {

                    int newValue = (int) roundToPlace(((diff / 88) * (max.intValue() - min.intValue()) + min.intValue()), 2);
                    sliderTranslate = new Translate((float) (left + sliderWidth + renderWidth), 0);
                    sliding.setObject(newValue);
                }
                if (sliding.getObject() instanceof Float) {
                    sliderTranslate = new Translate((float) (left + sliderWidth + renderWidth), 0);
                    double newValue = roundToPlace(((diff / 88) * (max.floatValue() - min.floatValue()) + min.floatValue()), 2);
                    sliding.setObject((float) newValue);
                }
                if (sliding.getObject() instanceof Long) {
                    double newValue = roundToPlace(((diff / 88) * (max.longValue() - min.longValue()) + min.longValue()), 2);
                    sliding.setObject((long) newValue);
                    sliderTranslate = new Translate((float) (left + sliderWidth + renderWidth), 0);
                }
                if (sliding.getObject() instanceof Double) {
                    double newValue = roundToPlace(((diff / 88) * (max.doubleValue() - min.doubleValue()) + min.doubleValue()), 2);
                    sliding.setObject(newValue);
                    sliderTranslate = new Translate((float) (left + sliderWidth + renderWidth), 0);
                }


            }
        }

        if (dragging) {
            dragPosX = mouseX - startX;
            dragPosy = mouseY - startY;
        }
        if (type == Handle.RELEASE && button == 0) {
            dragging = false;
        }
        if (type == Handle.CLICK && button == 0) {

            boolean hoveringCategory = isHovered(left, top, right + 400, top + 10, mouseX, mouseY);
            if (hoveringCategory) {
                dragging = true;
                startX = mouseX - dragPosX;
                startY = mouseY - dragPosy;
                return;

            }
            if (isHovered(left, top + 20, right, top + 40, mouseX, mouseY)) {
                translate = new Translate(point.getX(), point.getY());
                category = Category.COMBAT;

                point = new Point(30, 20);

            }
            if (isHovered(left, top + 40, right, top + 60, mouseX, mouseY)) {
                translate = new Translate(point.getX(), point.getY());
                category = Category.MOVEMENT;

                point = new Point(30, 40);
            }
            if (isHovered(left, top + 60, right, top + 80, mouseX, mouseY)) {
                translate = new Translate(point.getX(), point.getY());
                category = Category.PLAYER;

                point = new Point(30, 60);
            }
            if (isHovered(left, top + 80, right, top + 100, mouseX, mouseY)) {
                translate = new Translate(point.getX(), point.getY());
                category = Category.RENDER;

                point = new Point(30, 80);
            }
            if (isHovered(left, top + 100, right, top + 120, mouseX, mouseY)) {
                translate = new Translate(point.getX(), point.getY());
                category = Category.MISC;

                point = new Point(30, 100);
            }
            if (isHovered(left, top + 120, right, top + 140, mouseX, mouseY)) {
                translate = new Translate(point.getX(), point.getY());

                category = Category.CONFIG;

                point = new Point(30, 120);
            }

        }
        if (type == Handle.CLICK && button == 0) {
            dragging = false;
            // binding = null;
        }

        Minecraft.getMinecraft().gameSettings.enableVsync = false;
    }

    @Override
    public void handleMouseInput() throws IOException {
        int var1 = Mouse.getEventDWheel();

        if (var1 != 0) {
            if (var1 > 1) {
                var1 = 20;
            }

            if (var1 < -1) {
                var1 = -20;
            }

            scroll += var1;

        }
        super.handleMouseInput();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        handle(mouseX, mouseY, -1, Handle.DRAW);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        handle(mouseX, mouseY, mouseButton, Handle.CLICK);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        handle(mouseX, mouseY, state, Handle.RELEASE);

    }

    public boolean isHovered(float left, float top, float right, float bottom, int mouseX, int mouseY) {
        return mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    enum Handle {
        DRAW,
        CLICK,
        RELEASE
    }

}