package crispy.gui.novoline;

import com.google.common.collect.Lists;
import crispy.Crispy;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.impl.render.ClickGui;
import crispy.fonts.greatfont.TTFFontRenderer;
import crispy.util.animation.Translate;
import crispy.features.hacks.ConfigModule;
import crispy.util.render.gui.RenderUtil;
import crispy.util.render.shaders.BlurUtil;
import net.arikia.dev.drpc.DiscordUser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
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
import java.util.List;

import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

public class NovoGui extends GuiScreen {
    TTFFontRenderer fr;
    TTFFontRenderer small;
    private int dragPosX, dragPosy, startX, startY, scroll;
    private boolean dragging;
    private Category selectedCategory = Category.COMBAT, lastCategory = Category.COMBAT;
    private NumberValue sliding;

    private double sliderWidth;
    private double slide, maxCScroll;
    private Translate translate = new Translate(0, 0), blur = new Translate(0, 0), configScroll = new Translate(0, 0), hover = new Translate(0 ,0);
    private boolean profile;

    public NovoGui() {
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
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        blur = new Translate(0, 0);
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        if (mc.entityRenderer.getShaderGroup() != null) {
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            mc.entityRenderer.theShaderGroup = null;
        }
        super.onGuiClosed();
    }

    public void handle(int mouseX, int mouseY, int button, Handle type) {
        float left = dragPosX,
                top = dragPosy,
                width = 90,
                right = left + width;
        float translationFactor = (float) (14.4 / (float) Minecraft.getDebugFPS());
        float faster = (float) (20.4 / (float) Minecraft.getDebugFPS());
        float slower = (float) (6.4 / (float) Minecraft.getDebugFPS());
        if (type == Handle.DRAW) {

            if (ClickGui.blur.getObject()) {
                if (Math.abs(90 - blur.getX()) >= 1) {
                    blur.interpolate(90, 0, slower);
                }

                BlurUtil.blur((int) blur.getX());
            }
        }
        Color panel = new Color(33, 37, 51, 255);
        Color sideBar = new Color(31, 33, 47, 255);
        if (ClickGui.novoThemes.getMode().equalsIgnoreCase("New")) {
            panel = new Color(38, 42, 46, 255);
            sideBar = new Color(36, 38, 44, 255);
        }
        RenderUtil.drawRoundedRect(left, top, right + 300, top + 235, 5, panel.getRGB());
        RenderUtil.drawLeftRounded(left, top, right - 50, top + 235, 5, sideBar.getRGB());
        small = Crispy.INSTANCE.getFontManager().getFont("ROBO 14");
        int number = 10;


        int add = 30;

        translate.interpolate(0, slide, faster);


        Gui.drawRect(left + 2, profile ? top + translate.getY() - 15 : top + translate.getY() - 4, right - 50, top + translate.getY() + 20, panel.getRGB());

        for (Category category : Category.values()) {
            switch (category.getName().toLowerCase()) {
                case "combat": {

                    Crispy.INSTANCE.getFontManager().getFont("icon 38").drawString("a", left + 9, top + number + 1, selectedCategory == category ? new Color(130, 136, 157).getRGB() : new Color(59, 63, 81).getRGB());
                    break;
                }
                case "movement": {
                    Crispy.INSTANCE.getFontManager().getFont("icon 38").drawString("b", left + 9, top + number + 1, selectedCategory == category ? new Color(130, 136, 157).getRGB() : new Color(59, 63, 81).getRGB());
                    break;
                }
                case "player": {
                    Crispy.INSTANCE.getFontManager().getFont("icon 38").drawString("c", left + 9, top + number + 1, selectedCategory == category ? new Color(130, 136, 157).getRGB() : new Color(59, 63, 81).getRGB());
                    break;
                }
                case "render": {
                    Crispy.INSTANCE.getFontManager().getFont("icon 38").drawString("g", left + 9, top + number + 1, selectedCategory == category ? new Color(130, 136, 157).getRGB() : new Color(59, 63, 81).getRGB());
                    break;
                }
                case "misc": {
                    Crispy.INSTANCE.getFontManager().getFont("icon 38").drawString("e", left + 9, top + number + 1, selectedCategory == category ? new Color(130, 136, 157).getRGB() : new Color(59, 63, 81).getRGB());
                    break;
                }
            }
            number += add;
        }
        if (type == Handle.DRAW) {
            //BARRR
            mc.getTextureManager().bindTexture(Crispy.INSTANCE.getDiscordPng());
            Gui.drawModalRectWithCustomSizedTexture(left + 9, top + 7 * 30 - 10, 20, 20, 20, 20, 20, 20);

            mc.getTextureManager().bindTexture(new ResourceLocation("Client/gui/Bar.png"));
            Gui.drawModalRectWithCustomSizedTexture(left, top, 0, 0, 391, 5, 391, 5);

            //Crispy.INSTANCE.getFontManager().getFont("config 38").drawString("s", left + 9, top + 7 * 30 - 10, profile ? new Color(130, 136, 157).getRGB() : new Color(59, 63, 81).getRGB());

        }


        Crispy.INSTANCE.getFontManager().getFont("ROBO 14").drawCenteredString("Crispy", left + 18.5f, top + 7 * 30 + 12, new Color(255, 255, 255, 122).getRGB());
        if (profile) {
            RenderUtil.drawLine(right - 40, top + 50, right + 290, top + 50, 0.5f, panel.brighter().getRGB());
            RenderUtil.color(new Color(255, 255, 255, 255));
            Crispy.INSTANCE.getFontManager().getFont("ROBO 28").drawStringWithShadow(Crispy.INSTANCE.getDiscordName(), right - 40, top + 30, new Color(255, 255, 255, 255).getRGB());
            small.drawStringWithShadow("HWID:", right + 120, top + 20, new Color(255, 255, 255, 100).getRGB());
            small.drawStringWithShadow(Crispy.INSTANCE.getHwid(), right + 290 - (small.getWidth(Crispy.INSTANCE.getHwid())), top + 20, new Color(255, 255, 255, 155).getRGB());
            small.drawStringWithShadow("DiscordID:", right + 120, top + 30, new Color(255, 255, 255, 100).getRGB());
            small.drawStringWithShadow(Crispy.INSTANCE.getDiscordID(), right + 290 - (small.getWidth(Crispy.INSTANCE.getDiscordID())), top + 30, new Color(255, 255, 255, 155).getRGB());
            small.drawStringWithShadow("Build:", right + 120, top + 40, new Color(255, 255, 255, 100).getRGB());
            small.drawStringWithShadow(Crispy.INSTANCE.getBuild() + "", right + 290 - (small.getWidth(Crispy.INSTANCE.getBuild() + "")), top + 40, new Color(255, 255, 255, 150).getRGB());

            final boolean isHoveringThing = isHovered(right - 55, top + 5, right + 10, top + 35, mouseX, mouseY);


            hover.interpolate(isHoveringThing ? 255 : 100, 0, slower);
            small.drawString("Dashboard", right - 30, top + 20, new Color(255, 255, 255, (int) hover.getX()).getRGB());
            Crispy.INSTANCE.getFontManager().getFont("config 14").drawString("e", right - 40, top + 20, new Color(255, 255, 255, (int) hover.getX()).getRGB());
            if(isHoveringThing && type == Handle.CLICK && button == 0) {

            }


            if (Math.abs(scroll - configScroll.getY()) >= 1) {
                configScroll.interpolate(0, -1 + scroll, translationFactor);
            }


            float count = (float) configScroll.getY();
            double maxHeight = (float) configScroll.getY() + 20;
            double maxScroll = maxCScroll - (top + 50);
            if (maxScroll < 0) {
                maxScroll = 0;
            }

            if (-configScroll.getY() > maxScroll || -configScroll.getY() < -1) {
                if (-configScroll.getY() < -1) {
                    scroll = 1;
                } else {
                    scroll = (int) -(maxScroll) + 1;
                }

            }

            if(Math.toIntExact(Crispy.INSTANCE.getHackManager().getHacks().stream().filter(hack -> hack instanceof ConfigModule).count()) + 1 == 0) {
                small.drawStringWithShadow("Smh no configs present :3", right + 80, top + 60, new Color(255, 255, 255).getRGB());
            }
            glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.prepareScissorBox(left, top + 50, right + 300, top + 235);
            for (Hack hack : Crispy.INSTANCE.getHackManager().getHacks()) {
                if (hack instanceof ConfigModule) {
                    
                    count += 15;
                    maxHeight += 15;
                    final boolean isHovered = isHovered(right - 40, top + count + 40, right + 290, top + count + 50, mouseX, mouseY);
                    RenderUtil.drawRoundedRect(right - 40, top + count + 40, right + 290, top + count + 50, 3, panel.brighter().getRGB());
                    hack.getNovoGui().interpolate(isHovered ? 255 : 100, 0, slower);
                    small.drawStringWithShadow(hack.getName(), right - 31, top + count + 43, new Color(255, 255, 255, (int) hack.getNovoGui().getX()).getRGB());
                    Crispy.INSTANCE.getFontManager().getFont("config 14").drawString("s", right - 39, top + count + 43, new Color(255, 255, 255, (int) hack.getNovoGui().getX()).getRGB());

                    if (isHovered && button == 0 && type == Handle.CLICK) {
                        hack.toggle();
                    }
                }
            }
            glDisable(GL11.GL_SCISSOR_TEST);

            maxCScroll = maxHeight - configScroll.getY();


        }
        if (selectedCategory != null) {

            List<Hack> hacks = Crispy.INSTANCE.getHackManager().getModules(selectedCategory);
            List<List<Hack>> subHacks = Lists.partition(hacks, (hacks.size() + 1) / 2);

            boolean go = false;
            double epikScroll;
            int ticks = 1;
            double lastScroll = 0;
            int invalidScrollTicks = 0;
            if (Math.abs(scroll - selectedCategory.getCurrentScroll().getY()) >= 1) {
                selectedCategory.getCurrentScroll().interpolate(0, -1 + scroll, translationFactor);
            }
            for (List<Hack> subs : subHacks) {
                float count = (float) selectedCategory.getCurrentScroll().getY();
                double maxHeight = (float) selectedCategory.getCurrentScroll().getY() + 20;
                double maxScroll = selectedCategory.getMaxScroll() - 235;
                if (maxScroll < 0) {
                    maxScroll = 0;
                }

                if (-selectedCategory.getCurrentScroll().getY() > maxScroll || -selectedCategory.getCurrentScroll().getY() < -1) {
                    invalidScrollTicks++;
                    if (lastScroll > maxScroll) {
                        if (invalidScrollTicks > 1) {
                            if (-selectedCategory.getCurrentScroll().getY() < -1) {
                                scroll = 1;
                            } else {
                                scroll = (int) -(lastScroll) + 1;
                            }
                        }
                    } else {
                        if (invalidScrollTicks > 1) {
                            if (-selectedCategory.getCurrentScroll().getY() < -1) {
                                scroll = 1;
                            } else {

                                scroll = (int) -(maxScroll) + 1;
                            }
                        }
                    }
                    lastScroll = maxScroll;

                }


                glEnable(GL11.GL_SCISSOR_TEST);
                RenderUtil.prepareScissorBox(left, top + 10, right + 300, top + 235);
                go = !go;
                float moduleLeft = go ? right - 40 : right + 120;
                for (Hack hack : subs) {

                    count += 20;
                    maxHeight += 20;
                    boolean hoveredModule = isHovered(moduleLeft, top + count - 10, moduleLeft + small.getWidth(hack.getName()) + 20, top + count + 10, mouseX, mouseY);
                    hack.getNovoGui().interpolate(hoveredModule ? hack.isEnabled() ? 255 : 155 : hack.isEnabled() ? 255 : 100, 0, slower);
                    int addS = (int) (Crispy.INSTANCE.getValueManager().getAllValuesFrom(hack.getName()).stream().count() * 15) + 2;
                    small.drawStringWithShadow(hack.getName(), moduleLeft, top + count, new Color(255, 255, 255, (int) hack.getNovoGui().getX()).getRGB());

                    if (hoveredModule && type == Handle.CLICK && button == 0) {
                        hack.toggle();
                    }
                    for (Value value : Crispy.INSTANCE.getValueManager().getAllValuesFrom(hack.getName())) {
                        if (value.isVisible()) {
                            maxHeight += 15;
                            if (value instanceof ModeValue) {
                                count += 15;
                                ModeValue set = (ModeValue) value;

                                final boolean isHovered = isHovered(moduleLeft, top + count, moduleLeft + 150, top + count + 10, mouseX, mouseY);
                                set.getTranslate().interpolate(isHovered ? 155 : 100, 0, slower);

                                small.drawStringWithShadow("- " + set.getName(), moduleLeft, top + count, new Color(255, 255, 255, (int) set.getTranslate().getX()).getRGB());
                                RenderUtil.drawRoundedRect(moduleLeft + 85, top + count - 3, moduleLeft + 150, top + count + 7, 2, panel.brighter().getRGB());
                                small.drawStringWithShadow(set.getMode() + "", moduleLeft + 87, top + count, new Color(255, 255, 255, 100).getRGB());
                                if (isHovered && type == Handle.CLICK && button == 0) {
                                    set.cycle();
                                } else if (isHovered && type == Handle.CLICK && button == 1) {
                                    set.cycleReverse();
                                }

                            }
                            if (value instanceof NumberValue) {
                                count += 15;
                                NumberValue set = (NumberValue) value;
                                Number min = set.getMin();
                                Number max = set.getMax();
                                Number val = ((Number) set.getObject());
                                double renderWidth = (51) * (val.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
                                final boolean isHovered = isHovered(moduleLeft, top + count, moduleLeft + 150, top + count + 10, mouseX, mouseY);
                                if (Math.abs(renderWidth - set.getTranslate().getX()) >= 1) {
                                    set.getTranslate().interpolate(renderWidth, 0, translationFactor);
                                }
                                set.getOpacityT().interpolate(isHovered ? 155 : 100, 0, slower);

                                RenderUtil.drawRoundedRect(moduleLeft + 100, top + count + 2, moduleLeft + 150, top + count + 4, 0, panel.brighter().getRGB());
                                RenderUtil.color(new Color(82, 136, 246, 255));
                                RenderUtil.drawRoundedRect(moduleLeft + 100, top + count + 2, moduleLeft + set.getTranslate().getX() + 100, top + count + 4, 0, new Color(82, 136, 246, 255).getRGB());

                                small.drawStringWithShadow("- " + set.getName() + " " + set.getObject(), moduleLeft, top + count, new Color(255, 255, 255, (int) set.getOpacityT().getX()).getRGB());
                                if (isHovered && type == Handle.CLICK && button == 0) {
                                    sliding = set;
                                    this.sliderWidth = moduleLeft + 100;
                                }
                                if (type == Handle.RELEASE && button == 0) {
                                    sliding = null;
                                }
                            }
                            if (value instanceof BooleanValue) {
                                count += 15;
                                BooleanValue booleanValue = (BooleanValue) value;
                                final boolean isHovered = isHovered(moduleLeft, top + count, moduleLeft + 150, top + count + 10, mouseX, mouseY);
                                booleanValue.getTextTranslate().interpolate(isHovered ? booleanValue.getObject() ? 255 : 155 : booleanValue.getObject() ? 255 : 100, 0, slower);
                                small.drawStringWithShadow("- " + booleanValue.getName(), moduleLeft, top + count, new Color(255, 255, 255, (int) booleanValue.getTextTranslate().getX()).getRGB());
                                booleanValue.getTranslate().interpolate(booleanValue.getObject() ? 255 : 0, 0, slower);
                                RenderUtil.drawRoundedRect(moduleLeft + 145, top + count, moduleLeft + 150, top + count + 5, 2, panel.brighter().getRGB());
                                RenderUtil.drawRoundedRect(moduleLeft + 145, top + count, moduleLeft + 150, top + count + 5, 2, new Color(82, 136, 246, (int) booleanValue.getTranslate().getX()).getRGB());

                                if (isHovered && type == Handle.CLICK && button == 0) {
                                    booleanValue.setObject(!booleanValue.getObject());
                                }
                            }

                        }
                    }

                }
                selectedCategory.setMaxScroll(maxHeight - selectedCategory.getCurrentScroll().getY());
                GL11.glDisable(GL11.GL_SCISSOR_TEST);

            }
            //System.out.println(maxHeight);


        }


        /**
         * Slider shit
         */
        double diff = Math.min(51, Math.max(0, mouseX - (sliderWidth)));
        if (sliding != null) {

            Number min = sliding.getMin();
            Number max = sliding.getMax();
            Number val = ((Number) sliding.getObject());
            if (diff == 0) {
                sliding.setObject(sliding.getMin());
            } else {
                double renderWidth = (52) * (val.doubleValue() - min.doubleValue()) / (max.doubleValue() - min.doubleValue());
                if (sliding.getObject() instanceof Integer) {

                    int newValue = (int) roundToPlace(((diff / 51) * (max.intValue() - min.intValue()) + min.intValue()), 2);
                    sliding.setObject(newValue);
                }
                if (sliding.getObject() instanceof Float) {

                    double newValue = roundToPlace(((diff / 51) * (max.floatValue() - min.floatValue()) + min.floatValue()), 2);
                    sliding.setObject((float) newValue);
                }
                if (sliding.getObject() instanceof Long) {
                    double newValue = roundToPlace(((diff / 51) * (max.longValue() - min.longValue()) + min.longValue()), 2);
                    sliding.setObject((long) newValue);

                }
                if (sliding.getObject() instanceof Double) {
                    double newValue = roundToPlace(((diff / 51) * (max.doubleValue() - min.doubleValue()) + min.doubleValue()), 2);
                    sliding.setObject(newValue);

                }
            }
        }


        /**
         * Selecting category
         */
        if (type == Handle.CLICK && button == 0) {
            final float side = right - 50;


            if (isHovered(left, top + 10, side, top + 40, mouseX, mouseY)) {
                slide = 10;
                profile = false;
                selectedCategory = Category.COMBAT;
            }
            if (isHovered(left, top + 40, side, top + 70, mouseX, mouseY)) {
                selectedCategory = Category.MOVEMENT;
                slide = 40;
                profile = false;
            }
            if (isHovered(left, top + 70, side, top + 100, mouseX, mouseY)) {
                selectedCategory = Category.PLAYER;
                slide = 70;
                profile = false;
            }
            if (isHovered(left, top + 100, side, top + 130, mouseX, mouseY)) {
                selectedCategory = Category.RENDER;
                slide = 100;
                profile = false;
            }
            if (isHovered(left, top + 130, side, top + 160, mouseX, mouseY)) {
                selectedCategory = Category.MISC;
                slide = 130;
                profile = false;
            }
            if (isHovered(left, top + 210, side, top + 240, mouseX, mouseY)) {
                selectedCategory = null;
                slide = 210;
                profile = true;
            }
            if (lastCategory != selectedCategory) {
                for (Hack hack : Crispy.INSTANCE.getHackManager().getHacks()) {
                    for (Value value : Crispy.INSTANCE.getValueManager().getAllValuesFrom(hack.getName())) {
                        if (value instanceof BooleanValue) {
                            BooleanValue booleanValue = (BooleanValue) value;
                            booleanValue.setTextTranslate(new Translate(100, 0));
                            booleanValue.setTranslate(new Translate(0, 0));
                        }
                        if (value instanceof NumberValue) {
                            NumberValue numberValue = (NumberValue) value;
                            numberValue.setTranslate(new Translate(0, 0));
                        }
                    }
                }
            }
            lastCategory = selectedCategory;

        }
        if (dragging) {
            dragPosX = mouseX - startX;
            dragPosy = mouseY - startY;
        }
        if (type == Handle.RELEASE && button == 0) {
            dragging = false;
        }
        if (type == Handle.CLICK && button == 0) {
            boolean dragPos = isHovered(left, top, right + 400, top + 10, mouseX, mouseY);
            if (dragPos) {
                dragging = true;
                startX = mouseX - dragPosX;
                startY = mouseY - dragPosy;
                return;
            }
        }
        if (type == Handle.CLICK && button == 0) {
            dragging = false;
        }


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

    public static String getAvatarLink(DiscordUser discordUser) {
        if (discordUser != null) {
            return "https://cdn.discordapp.com/avatars/" + discordUser.userId + "/" + discordUser.avatar + ".png?size=256";
        }
        return null;
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


    enum Handle {
        DRAW,
        CLICK,
        RELEASE
    }


}
