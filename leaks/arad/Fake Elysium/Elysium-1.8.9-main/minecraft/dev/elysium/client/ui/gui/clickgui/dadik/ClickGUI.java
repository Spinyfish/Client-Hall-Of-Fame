package dev.elysium.client.ui.gui.clickgui.dadik;

import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.*;
import dev.elysium.client.Elysium;
import dev.elysium.client.mods.impl.settings.ClickGui;
import dev.elysium.client.ui.font.TTFFontRenderer;
import dev.elysium.client.ui.gui.components.Button;
import dev.elysium.client.utils.render.BlurUtil;
import dev.elysium.client.utils.render.ColorAnimation;
import dev.elysium.client.utils.render.RenderUtils;
import dev.elysium.client.utils.render.Stencil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClickGUI extends GuiScreen {
    TTFFontRenderer fr;

    long openTime = 0;
    float scrollOffset = 0;
    float scrollOffsetSetting = 0;
    float scrollDistance = 0;
    float scrollDistanceSetting = 0;
    Category selectedCategory = Category.SETTINGS;
    Mod selectedMod;

    boolean searching = false;
    boolean binding = false;
    ClickGui cguiMod;

    public String search = "";
    TTFFontRenderer fr2 = Elysium.getInstance().getFontManager().getFont("POPPINS-MEDIUM 18");
    TTFFontRenderer frBig = Elysium.getInstance().fm.getFont("POPPINS-MEDIUM 30");

    float categorySlider = 0;

    List<Button> buttons = new ArrayList<>();
    Button hovering;

    BlurUtil blurUtil = new BlurUtil();

    public void initGui() {
        selectedCategory = Category.values()[Elysium.getInstance().prefs.getInt("cgui_category", 0)];
        scrollDistance = Elysium.getInstance().prefs.getFloat("cgui_scroll", 0);
        openTime = System.currentTimeMillis();
        cguiMod = ((ClickGui)Elysium.getInstance().getModManager().getModByName("ClickGui"));
        categorySlider = ((float)selectedCategory.ordinal()) / (float)Category.values().length;
        fr = elysium.fm.getFont("poppins-semibold 20");
        for(Mod m : Elysium.getInstance().getModManager().getMods()) {
            m.tTimer = m.toggled ? 1000 : 0;
            for(Setting s : m.settings.stream().filter(s -> s instanceof BooleanSetting).collect(Collectors.toList())) {
                s.tTimer = ((BooleanSetting)s).isEnabled() ? 1000 : 0;
            }
        }
        initButtons();
    }

    double transformUp = 4;

    public void onGuiClosed()
    {
        mc.entityRenderer.useShader = false;
    }

    private void initButtons() {
        buttons.clear();
        int thick = 3;
        int i = 0;
        for(Mod m : Elysium.getInstance().getModManager().getModsByCategory(selectedCategory)) {
            buttons.add(new Button(m.name.concat("toggle_module"), width / 2 + 155 - 1.5 - 50, height / 2 - 91 + (i * 44) - transformUp - 1.5, width / 2 + 172 + 1.5 - 50, height / 2 - 91 + (i * 44) + 16 - transformUp + 1.5));
            buttons.add(new Button(m.name.concat("hover_module"), width / 2 - 145 - thick, height / 2 - 100 + (i * 44) - thick - transformUp, width / 2 + 145 + thick, height / 2 - 110 + (i * 44) + 43 + thick - transformUp));
            buttons.add(new Button(m.name.concat("options_module"), width / 2 + 177 - 50, height / 2 - 92 + (i * 44) - transformUp - 3, width / 2 + 189 - 50, height / 2 - 92 + (i * 44) - transformUp + 19));
            buttons.add(new Button(m.name.concat("keybind_module"), width / 2 + 155 - 2 + 1 - 25 - 50, height / 2 - 91 + (i * 44) - 2 - transformUp + 1 - 0.5d, width / 2 + 172 + 2 - 1 - 25 - 50, height / 2 - 91 + (i * 44) + 16 + 2 - transformUp - 1 - 0.5d));
            i++;
        }
        for(int j = 0; j <= 4; j++) {
            double x = width / 2 - 190 - 2 ;
            double y = height / 2 - 80 + (j * 34) + 1.1 + 0.959d;

            buttons.add(new Button("category" + j, x - 3, y - 3, x + 23, y + 23));
        }
        int setI = 0;
        int realDex = 0;
        int booleanOffset = 0;
        if(selectedMod != null) {
            for(Setting s : selectedMod.settings) {
                if(s instanceof NumberSetting) {
                    booleanOffset = 0;
                    buttons.add(new Button(selectedMod.name + s.name, width / 2 - 134, height / 2 - 70 + (setI * 24) + 4.5 - 10, width / 2 + 134, height / 2 - 60 + (setI * 24) - 4 + 10));
                    setI++;
                } else if(s instanceof ModeSetting) {
                    double thickness = 1;

                    double biggest = 0;
                    for(String mode : ((ModeSetting)s).modes) {
                        biggest = Math.max(fr.getStringWidth(mode), biggest);
                    }

                    buttons.add(new Button(selectedMod.name + s.name + "4", width / 2 - 140 - thickness + booleanOffset, height / 2 - 90 + (setI * 24) - thickness, width / 2 - 145 + 15 + biggest+ thickness + booleanOffset, height / 2 - 90 + (setI * 24) + 15 + thickness));

                    booleanOffset += biggest + fr.getStringWidth(s.name) + 18;
                    if (selectedMod.settings.size() > realDex + 1) {
                        if (selectedMod.settings.get(realDex + 1) instanceof BooleanSetting || selectedMod.settings.get(realDex + 1) instanceof ModeSetting) {
                            Setting next = selectedMod.settings.get(realDex + 1);
                            if (next instanceof BooleanSetting) {
                                if (fr2.getStringWidth((selectedMod.settings.get(realDex + 1)).name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            } else {
                                double biggest_temp = 0;
                                for (String mode: ((ModeSetting) next).modes) {
                                    biggest_temp = Math.max(fr.getStringWidth(mode), biggest_temp);
                                }
                                if (fr.getStringWidth(biggest_temp + next.name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            }
                        } else booleanOffset = 0; //yes shit code but Uhhhhh ignore
                    } else booleanOffset = 0;
                }  else if(s instanceof BooleanSetting) {
                    double thickness = 1.5;
                    buttons.add(new Button(selectedMod.name + s.name, width / 2 - 135 - thickness + booleanOffset, height / 2 - 90 + (setI * 24) - thickness, width / 2 - 135 + 15 + thickness + booleanOffset, height / 2 - 90 + (setI * 24) + 15 + thickness));

                    booleanOffset += 28 + fr2.getStringWidth(s.name);
                    if (selectedMod.settings.size() > realDex + 1) {
                        if (selectedMod.settings.get(realDex + 1) instanceof BooleanSetting || selectedMod.settings.get(realDex + 1) instanceof ModeSetting) {
                            Setting next = selectedMod.settings.get(realDex + 1);
                            if (next instanceof BooleanSetting) {
                                if (fr2.getStringWidth((selectedMod.settings.get(realDex + 1)).name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            } else {
                                double biggest_temp = 0;
                                for (String mode: ((ModeSetting) next).modes) {
                                    biggest_temp = Math.max(fr.getStringWidth(mode), biggest_temp);
                                }
                                if (fr.getStringWidth(biggest_temp + next.name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            }
                        } else booleanOffset = 0; //yes shit code but Uhhhhh ignore
                    } else booleanOffset = 0;
                } else if(s instanceof ColorSetting) {
                    booleanOffset = 0;
                    ColorSetting cs = (ColorSetting)s;
                    for(int ci = 0; ci <= 2; ci++) {
                        buttons.add(new Button(selectedMod.name + s.name + "3", width / 2 + 75, height / 2 - 94 + (setI * 24), width / 2 + 140 , height / 2 - 92 + (setI * 24) + 17));
                        buttons.add(new Button(selectedMod.name + s.name + (ci + ""), width / 2 - 120, height / 2 - 73 + (setI * 24) + 10.5 + (ci * 21.5), width / 2 + 115, height / 2 - 60 + (setI * 24) + 5 + (ci * 21.5)));
                    }

                    setI += 3;
                }
                realDex++;
                setI++;
            }
        }
    }

    void drawCatIcon(int i) {
        int txt = 88;
        double x = width / 2 - 233 + 1;
        double y = height / 2 - 120 + (i * 34);
        String[] icons = new String[] {".",",","'","\"","!"};

        Elysium.getInstance().getFontManager().getFont("Elysium 34").drawString(icons[i],(float) x + txt / 2, (float) y + txt / 2, 0xff444863);
    }

    long lastDelta = System.currentTimeMillis();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0,0,width,height,0xC0181818);
        mc.fontRendererObj.drawStringWithShadow(selectedCategory + "", 5, 5, -1);
        mc.fontRendererObj.drawStringWithShadow("mouseOver: " + hovering, 5, 15, -1);
        blurUtil.blur(3);
        GlStateManager.translate(23,0,0);
        float delta = (System.currentTimeMillis() - lastDelta);
        if(!searching && (Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() > 5 || selectedMod != null)) {
            if (selectedMod == null) {
                scrollDistance += -(Mouse.getDWheel() * delta) / 50;
            } else {
                scrollDistanceSetting += -(Mouse.getDWheel() * delta) / 50;
            }
        } else {
            scrollDistance = scrollDistanceSetting = 0;
        }

        scrollDistance = Math.min(Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() * 44 - 220, scrollDistance);
        scrollDistance = Math.max(0, scrollDistance);

        {
            if (Math.abs(scrollDistance - scrollOffset) > 0) {
                float change = (scrollDistance - scrollOffset < 0) ? -delta / 5 : delta / 5;
                change *= Math.abs(scrollDistance - scrollOffset) / 25;
                if (Math.max(0, scrollOffset + change) != 0 || Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() * 44 - 220 != Math.min(Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).size() * 44 - 220, scrollOffset + change))
                    if (Math.abs(scrollDistance - scrollOffset) < Math.abs(change))
                        scrollOffset = scrollDistance;
                    else
                        scrollOffset += change;
            }
        }

        if(selectedMod == null || searching) {

        } else {
            int setI = 0;
            int realDex = 1;
            int booleanOffset = 0;
            for(Setting s : selectedMod.settings.stream().filter(s -> !(s instanceof KeybindSetting)).collect(Collectors.toList())) {
                if (s instanceof NumberSetting) {
                    NumberSetting ns = (NumberSetting) s;
                    setI += 1;
                } else if (s instanceof ModeSetting) {
                    ModeSetting ms = (ModeSetting) s;
                    double biggest = 0;
                    for (String mode: ms.modes) {
                        biggest = Math.max(fr.getStringWidth(mode), biggest);
                    }

                    booleanOffset += biggest + fr.getStringWidth(s.name) + 18;
                    if (selectedMod.settings.size() > realDex + 1) {
                        if (selectedMod.settings.get(realDex + 1) instanceof BooleanSetting || selectedMod.settings.get(realDex + 1) instanceof ModeSetting) {
                            Setting next = selectedMod.settings.get(realDex + 1);
                            if (next instanceof BooleanSetting) {
                                if (fr2.getStringWidth((selectedMod.settings.get(realDex + 1)).name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            } else {
                                double biggest_temp = 0;
                                for (String mode: ((ModeSetting) next).modes) {
                                    biggest_temp = Math.max(fr.getStringWidth(mode), biggest_temp);
                                }
                                if (fr.getStringWidth(biggest_temp + next.name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            }
                        } else booleanOffset = 0; //yes shit code but Uhhhhh ignore
                    } else booleanOffset = 0;
                } else if (s instanceof BooleanSetting) {
                    booleanOffset += 28 + fr2.getStringWidth(s.name);
                    if (selectedMod.settings.size() > realDex + 1) {
                        if (selectedMod.settings.get(realDex + 1) instanceof BooleanSetting || selectedMod.settings.get(realDex + 1) instanceof ModeSetting) {
                            Setting next = selectedMod.settings.get(realDex + 1);
                            if (next instanceof BooleanSetting) {
                                if (fr2.getStringWidth((selectedMod.settings.get(realDex + 1)).name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            } else {
                                double biggest_temp = 0;
                                for (String mode: ((ModeSetting) next).modes) {
                                    biggest_temp = Math.max(fr.getStringWidth(mode), biggest_temp);
                                }
                                if (fr.getStringWidth(biggest_temp + next.name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            }
                        } else booleanOffset = 0; //yes shit code but Uhhhhh ignore
                    } else booleanOffset = 0;
                } else if (s instanceof ColorSetting) {
                    setI += 3;
                }
                realDex++;
                setI++;
            }

            scrollDistanceSetting = Math.max(0, scrollDistanceSetting);

            if(setI <= 7) scrollDistanceSetting = 0;
            else {
                if((setI - 7) * 24 < scrollDistanceSetting) scrollDistanceSetting = (setI - 7) * 24;
            }

            if (Math.abs(scrollDistanceSetting - scrollOffsetSetting) > 0) {
                float change = (scrollDistanceSetting - scrollOffsetSetting < 0) ? -delta / 5 : delta / 5;
                change *= Math.abs(scrollDistanceSetting - scrollOffsetSetting)/25;
                if(Math.abs(scrollDistanceSetting - scrollOffsetSetting) < Math.abs(change))
                    scrollOffsetSetting = scrollDistanceSetting;
                else
                    scrollOffsetSetting += change;
            }
        }
        float diff = categorySlider - ((float)selectedCategory.ordinal()) / (float)Category.values().length;
        categorySlider += diff / (-100f) * delta;
        if(categorySlider < 0) categorySlider = 0; if(categorySlider > 1) categorySlider = 1;

        for(Mod m : Elysium.getInstance().getModManager().getModsByCategory(selectedCategory)) {
            if (m.tTimer > 0 && m.tTimer < 1000) {
                m.tTimer += 10 * delta;
            }
            if (m.tTimer > 1000) m.tTimer = 1000;
            if (m.tTimer < 0 && m.tTimer > -1000) {
                m.tTimer -= 10 * delta;
            }
            if (m.tTimer < -1000) m.tTimer = -1000;
            for (Setting s : m.settings.stream().filter(s -> s instanceof BooleanSetting || s instanceof ColorSetting).collect(Collectors.toList())) {
                if (s.tTimer > 0 && s.tTimer < 1000) {
                    s.tTimer += (s instanceof BooleanSetting ? 10 : 1) * delta;
                }
                if (s.tTimer > 1000) s.tTimer = 1000;
                if (s.tTimer < 0 && s.tTimer > -1000) {
                }
                if (s.tTimer < -1000) s.tTimer = -1000;
            }
        }
        Mod ignoreMod = null;
        for(Button b : buttons.stream().filter(b -> b.idname.endsWith("hover_module") && !searching && selectedMod == null && b.isMouseOverMe(mouseX - 23, (int) (mouseY + scrollOffset))).collect(Collectors.toList())) {
            Mod mod = Elysium.getInstance().getModManager().getModByName(b.idname.split("hover_")[0]);
            ignoreMod = mod;
            mod.hoverTimer += 20 * delta;
            if(mod.hoverTimer > 1000) mod.hoverTimer = 1000;
            mod.descTimer += 40 * delta;
            if(mod.descTimer > 1000) mod.hoverTimer = 1000;
        }
        for(Mod m : Elysium.getInstance().getModManager().getMods()) {
            if(m == ignoreMod || searching) continue;
            m.hoverTimer -= 10 * delta;
            if(m.hoverTimer < 0) m.hoverTimer = 0;
            m.descTimer = 0;
        }

        lastDelta = System.currentTimeMillis();

        RenderUtils.drawARoundedRect(width / 2 - 209, height / 2f - 124, width / 2 + 166, height / 2f + 124, 5, 0xff13141C);

        RenderUtils.drawARoundedRectWithBorder(width / 2 - 200, height / 2 - 85.5 - 8, width / 2 - 157, height / 2 + 85.5 + 8, 5, 0xff181924, 0.9f, 0xff262939);

        int sliderColor = ColorAnimation.getColor(0xff6089F8, 0xff8764F8, categorySlider);
        RenderUtils.drawCircle(width / 2 - 200, height / 2 - 85 + (categorySlider * 170) + 12 - 1, 2, sliderColor);
        RenderUtils.drawCircle(width / 2 - 200, height / 2 - 85 + 10 + (categorySlider * 170) + 11 - 1, 2, sliderColor);
        Gui.drawRect(width / 2 - 200, height / 2 - 85 + (categorySlider * 170) + 12 - 1, width / 2 - 200 + 2, height / 2 - 85 + 10 + (categorySlider * 170) + 11 - 1, sliderColor);

        Gui.drawRect(width / 2 - 200, height / 2 - 80, width / 2 - 201, height / 2 + 80, 0xff262939);
        Gui.drawRect(width / 2 - 201, height / 2 - 80, width / 2 - 205, height / 2 + 80, 0xff13141C);

        for(int i = 0; i <= 4; i++) {
            drawCatIcon(i);
        }

        Stencil.INSTANCE.start();
        Stencil.INSTANCE.setBuffer(true);

        int i2 = selectedCategory.ordinal();
        drawCatIcon(i2);

        Stencil.INSTANCE.cropInside();
        //TODO: making it blend would probably look nice
        RenderUtils.drawGradientRect(width / 2 - 200, height / 2 - 80 + (categorySlider * 170), width / 2 - 150, height / 2 - 50 + (categorySlider * 170), 0xff6089F8, 0xff8764F7);
        Stencil.INSTANCE.stopLayer();

        GlStateManager.pushMatrix();
        //Gui.drawRect(width / 2 - 150, height / 2 - 105.5, width / 2 + 215, height / 2 + 106.5, -1);
        Stencil.INSTANCE.start();
        Stencil.INSTANCE.setBuffer(true);
        Gui.drawRect(width / 2 - 150, height / 2 - 105.5, width / 2 + 215, height / 2 + 106.5, -1);
        Stencil.INSTANCE.cropInside();

        Mod drawDesc = null;

        int i = 0;
        for(Mod m : Elysium.getInstance().getModManager().getModsByCategory(selectedCategory)) {
            float thick = 0.9f;
            //module outline
            int outlineColor1 = ColorAnimation.getColor(0xff7F6DF8, 0xff424663, m.hoverTimer / 1001f);
            int outlineColor2 = ColorAnimation.getColor(0xff6088F8, 0xff424663, m.hoverTimer / 1001f);
            RenderUtils.drawGradientRoundedRect(width / 2 - 145 - thick, height / 2 - scrollOffset - 100 + (i * 44) - thick - transformUp, width / 2 + 145 + thick, height / 2 - scrollOffset - 110 + (i * 44) + 43 + thick - transformUp, 5, outlineColor1, outlineColor2);
            RenderUtils.drawARoundedRect(width / 2 - 145, height / 2 - scrollOffset - 100 + (i * 44) - transformUp, width / 2 + 145, height / 2 - scrollOffset - 110 + (i * 44) + 43 - transformUp, 5, 0xff181924);

            fr.drawString(m.name, width / 2 - 130, height / 2 - scrollOffset - 90 + (i * 44) - (float)transformUp, 0xff9599C3);

            GlStateManager.pushMatrix();
            GlStateManager.translate(-50, 0, 0);

            int color1 = -1; //0xff6962D7 : 0xff31334a <- un-toggled
            int color2 = -1; //0xff5F85F4 : 0xff31334a <- un-toggled

            int t = m.tTimer;
            if(t < 0) t = 1000 + m.tTimer;
            color1 = ColorAnimation.getColor(0xff6962D7, 0xff31334a, t / 1001f);
            color2 = ColorAnimation.getColor(0xff5F85F4, 0xff31334a, t / 1001f);

            //color1 = ColorAnimation.getColor(0xff6962D7, 0xff31334a, t / 1001f);
            //color2 = ColorAnimation.getColor(0xff6962D7, 0xff31334a, t / 1001f);

            //checkbox outline
            RenderUtils.drawGradientRoundedRect(width / 2 + 155 - thick + 1, height / 2 - scrollOffset - 91 + (i * 44) - thick - transformUp + 1 - 0.5d, width / 2 + 172 + thick - 1, height / 2 - scrollOffset - 91 + (i * 44) + 16 + thick - transformUp - 1 - 0.5d, 4, color1, color2);
            RenderUtils.drawARoundedRect(width / 2 + 155 + 1, height / 2 - scrollOffset - 91 + (i * 44) - transformUp + 1 - 0.5d, width / 2 + 172 - 1, height / 2 - scrollOffset - 91 + (i * 44) + 16 - transformUp - 1 - 0.5d, 4, 0xff181924);

            String key_sf = Keyboard.getKeyName(m.keyCode.code); //String format
            float offset = fr.getStringWidth(key_sf);

            //keybind outline
            int keybindColor1 = m.keyCode.code == 0 ? 0xff31334a : 0xff6962D7;
            int keybindColor2 = m.keyCode.code == 0 ? 0xff31334a : 0xff5F85F4;
            if(m.keyCode.code == 0) {
                RenderUtils.drawGradientRoundedRect(width / 2 + 155 - thick + 1 - 25, height / 2 - scrollOffset - 91 + (i * 44) - thick - transformUp + 1 - 0.5d, width / 2 + 172 + thick - 1 - 25, height / 2 - scrollOffset - 91 + (i * 44) + 16 + thick - transformUp - 1 - 0.5d, 4, keybindColor1, keybindColor2);
                RenderUtils.drawARoundedRect(width / 2 + 155 + 1 - 25, height / 2 - scrollOffset - 91 + (i * 44) - transformUp + 1 - 0.5d, width / 2 + 172 - 1 - 25, height / 2 - scrollOffset - 91 + (i * 44) + 16 - transformUp - 1 - 0.5d, 4, 0xff181924);
            } else {
                RenderUtils.drawGradientRoundedRect(width / 2 + 155 - thick + 1 - 16.5 - offset, height / 2 - scrollOffset - 91 + (i * 44) - thick - transformUp + 1 - 0.5d, width / 2 + 172 + thick - 1 - 25, height / 2 - scrollOffset - 91 + (i * 44) + 16 + thick - transformUp - 1 - 0.5d, 4, keybindColor1, keybindColor2);
                RenderUtils.drawARoundedRect(width / 2 + 155 + 1 - 16.5 - offset, height / 2 - scrollOffset - 91 + (i * 44) - transformUp + 1 - 0.5d, width / 2 + 172 - 1 - 25, height / 2 - scrollOffset - 91 + (i * 44) + 16 - transformUp - 1 - 0.5d, 4, 0xff181924);
            }

            if(!m.binding) {
                if(m.keyCode.code == 0) {
                    fr.drawString("-", width / 2 + 155 - 25 + 5, height / 2 - scrollOffset - 91 + (i * 44) - (float)transformUp + 1.5f, 0xff34374F);
                } else {
                    fr.drawString(key_sf, width / 2 + 155 - 15 + 3f - offset, height / 2 - scrollOffset - 91 + (i * 44) - (float)transformUp + 1.5f, 0xff6F7ABB);
                }
            }


            int checkmarkcolor = ColorAnimation.getColor(0xff6962D7, 0x00000000, t / 1001f);
            //checkmark
            for (int check = 0; check <= 1; check++) {
                RenderUtils.drawCircleSmall(width / 2 + 150.5 + 10 + check, height / 2 - scrollOffset - 93 + 10 + check + (i * 44) - transformUp - 0.5, 0, 360, 1f, checkmarkcolor);
            }
            for (int check = 0; check <= 4; check++) {
                RenderUtils.drawCircleSmall(width / 2 + 150.5 + 12 + check, height / 2 - scrollOffset - 93 + 12 - check + (i * 44) - transformUp - 0.5, 0, 360, 1f, checkmarkcolor);
            }

            for(int dot = 0; dot < 3; dot++) {
                RenderUtils.drawCircle(width / 2 + 183.5, height / 2 - scrollOffset - 92 + (i * 44) + (dot * 5) + 3.5 - transformUp, 0, 360, 1.4f, 0xff545997);
            }

            GlStateManager.popMatrix();

            if(m.descTimer > 998) {
                drawDesc = m;
            }

            i++;
        }

        Stencil.INSTANCE.stopLayer();

        if(!searching && drawDesc != null && mouseY > height / 2 - 105.5 && mouseY < height / 2 + 105.5) {
            float expand = Elysium.getInstance().getFontManager().getFont("POPPINS 16").getStringWidth(drawDesc.description);
            RenderUtils.drawARoundedRectWithBorder(mouseX + 9 - 23, mouseY + 10, mouseX + expand + 17 - 23, mouseY + 30, 5, 0xff161822, 0.9f, 0xff222534);
            Elysium.getInstance().getFontManager().getFont("POPPINS 16").drawString(drawDesc.description, mouseX + 14.5f - 23, mouseY + 15, 0xff8289BB);
        }

        GlStateManager.popMatrix();
        GlStateManager.translate(-23,0,0);
        if(searching) {
            blurUtil.blur(4);

            RenderUtils.drawARoundedRect(width/2 - 151, height / 2 - 57 - 50, width / 2 + 151, height/2 - 23 - 50, 4, 0xFF424663);
            RenderUtils.drawARoundedRect(width/2 - 150, height / 2 - 56 - 50, width / 2 + 150, height/2 - 24 - 50, 4, 0xFF181924);
            fr.drawString(search == null || search.isEmpty() ? "Search.." + (System.currentTimeMillis() % 1400 > 700 ? "." : "") :
                    search,width/2 - 120,height/2 - 96,0xFF292a39);
            RenderUtils.glColor(0xFF292a39);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            mc.getTextureManager().bindTexture(new ResourceLocation("Elysium/clickgui/search.png"));
            Gui.drawModalRectWithCustomSizedTexture(width/2 - 142, height/2 - 98,0,0,16,16,16,16);
            GlStateManager.disableBlend();

            transformUp = 5;
            i = 1;
            for(Mod m : Elysium.getInstance().getModManager().getMods()) {
                if(i > 4 || (search != null && !search.isEmpty()) && !m.name.toLowerCase().contains(search.toLowerCase()))
                    continue;
                float thick = 0.9f;
                //module outline

                float x1 = width / 2 - 145 - thick;
                float y1 = (float) (height / 2 - scrollOffset - 100 + (i * 44) - thick - transformUp);
                float x2 = width / 2 + 145 + thick;
                float y2 = (float) (height / 2 - scrollOffset - 110 + (i * 44) + 43 + thick - transformUp);

                if(mouseX > x1 && mouseX < x2) {
                    if (mouseY > y1 && mouseY < y2) {
                        m.hoverTimer += 20 * delta;
                        if (m.hoverTimer > 1000) m.hoverTimer = 1000;
                    } else {
                        m.hoverTimer -= 10 * delta;
                        if(m.hoverTimer < 0) m.hoverTimer = 0;
                    }
                } else {
                    m.hoverTimer -= 10 * delta;
                    if(m.hoverTimer < 0) m.hoverTimer = 0;
                }

                int outlineColor1 = ColorAnimation.getColor(0xff7F6DF8, 0xFF2f3145, m.hoverTimer / 1001f);
                int outlineColor2 = ColorAnimation.getColor(0xff6088F8, 0xFF2f3145, m.hoverTimer / 1001f);
                RenderUtils.drawGradientRoundedRect(width / 2 - 145 - thick, height / 2 - scrollOffset - 100 + (i * 44) - thick - transformUp, width / 2 + 145 + thick, height / 2 - scrollOffset - 110 + (i * 44) + 43 + thick - transformUp, 4, outlineColor1, outlineColor2);
                RenderUtils.drawARoundedRect(width / 2 - 145, height / 2 - scrollOffset - 100 + (i * 44) - transformUp, width / 2 + 145, height / 2 - scrollOffset - 110 + (i * 44) + 43 - transformUp, 4, 0xff181924);

                fr.drawString(m.name, width / 2 - 130, height / 2 - scrollOffset - 90 + (i * 44) - (float)transformUp, 0xff9599C3);

                GlStateManager.pushMatrix();
                GlStateManager.translate(-50, 0, 0);

                int t = m.tTimer;
                if(t < 0) t = 1000 + m.tTimer;

                int color1 = ColorAnimation.getColor(0xff6962D7, 0xff31334a, t / 1001f);
                int color2 = ColorAnimation.getColor(0xff5F85F4, 0xff31334a, t / 1001f);

                //color1 = ColorAnimation.getColor(0xff6962D7, 0xff31334a, t / 1001f);
                //color2 = ColorAnimation.getColor(0xff6962D7, 0xff31334a, t / 1001f);

                String key_sf = Keyboard.getKeyName(m.keyCode.code); //String format
                float offset = fr.getStringWidth(key_sf);

                for(int dot = 0; dot < 3; dot++) {
                    RenderUtils.drawCircle(width / 2 + 183.5, height / 2 - scrollOffset - 92 + (i * 44) + (dot * 5) + 3.5 - transformUp, 0, 360, 1.4f, 0xff545997);
                }

                GlStateManager.popMatrix();
                i++;
            }
            transformUp = 4;
        }

        if(selectedMod != null) {
            //RenderUtils.drawARoundedRect(width / 2 - 215, height / 2f - 124, width / 2 + 215, height / 2f + 124, 5, 0x60000000); //make bg darker
            Gui.drawRect(0,0,width,height,0x60101010);
            RenderUtils.drawARoundedRect(width / 2 - 155, height / 2 - 115, width / 2 + 155, height / 2 + 115, 5, 0xff181A27); //main box

            frBig.drawString(selectedMod.name, width / 2 - 140, height / 2 - 105, 0xffAEB6E3);

            Stencil.INSTANCE.start();
            Stencil.INSTANCE.setBuffer(true);
            Gui.drawRect(width / 2 - 155, height / 2 - 72, width / 2 + 155, height / 2 + 100, -1);
            Stencil.INSTANCE.cropInside();

            GlStateManager.translate(0,-scrollOffsetSetting,1);

            int setI = 1;
            int realDex = 1;
            int booleanOffset = 0;
            for(Setting s : selectedMod.settings.stream().filter(s -> !(s instanceof KeybindSetting)).collect(Collectors.toList())) {
                if(s instanceof NumberSetting) {
                    double thickness = 0.9;
                    NumberSetting ns = (NumberSetting)s;
                    RenderUtils.drawARoundedRect(width / 2 - 140 - thickness, height / 2 - 90 + (setI * 24) - thickness, width / 2 + 140 + thickness, height / 2 - 90 + (setI * 24) + 38 + thickness, 5, 0xff202438);
                    RenderUtils.drawARoundedRect(width / 2 - 140, height / 2 - 90 + (setI * 24), width / 2 + 140, height / 2 - 90 + (setI * 24) + 38, 5, 0xff181C29);

                    fr2.drawString(s.name +
                            ":", width / 2 - 133, height / 2 - 85 + (setI * 24), 0xff6973B0);
                    fr2.drawString(ns.getValue() + "", width / 2 + 135 - fr2.getStringWidth(ns.getValue() + ""), height / 2 - 85 + (setI * 24), 0xff6973B0);

                    double value = ((ns.getValue() - ns.getMin()) / (ns.getMax() - ns.getMin()));
                    RenderUtils.drawARoundedRect(width / 2 - 134, height / 2 - 70 + (setI * 24) + 4.5, width / 2 + 134, height / 2 - 60 + (setI * 24) - 4, 0.5, 0xff2F3754);
                    RenderUtils.drawARoundedRect(width / 2 - 134, height / 2 - 70 + (setI * 24) + 4.5, width / 2 - 134 + (value * 272), height / 2 - 60 + (setI * 24) - 4, 0.5, 0xff5774D9);

                    Gui.drawRect(width / 2 - 134.5, height / 2 - 70 + (setI * 24) + 5, width / 2 - 133, height / 2 - 70 + (setI * 24) + 5.5, 0xff5774D9);
                    Gui.drawRect(width / 2 - 134.5 + 268, height / 2 - 70 + (setI * 24) + 5, width / 2 - 133 + 267.5, height / 2 - 70 + (setI * 24) + 5.5, 0xff2F3754);

                    RenderUtils.drawACircle(width / 2 - 134 + (value * 268), height / 2 - 70 + (setI * 24) + 5, 0, 360, 3.5, 0xffA2ADF9);
                    RenderUtils.drawACircle(width / 2 - 134 + (value * 268), height / 2 - 70 + (setI * 24) + 5, 0, 360, 4, 0x55A2ADF9);

                    setI++;
                } else if(s instanceof ModeSetting) {
                    double thickness = 1;
                    ModeSetting ms = (ModeSetting)s;

                    int color1 = 0xff7870F7;
                    int color2 = 0xff495BD2;

                    double biggest = 0;
                    for(String mode : ms.modes) {
                        biggest = Math.max(fr.getStringWidth(mode), biggest);
                    }

                    double middle = ((width / 2 - 140 - thickness + booleanOffset) + (width / 2 - 145 + biggest + 15 + thickness + booleanOffset))/2;

                    RenderUtils.drawGradientRoundedRect(width / 2 - 140 - thickness + booleanOffset, height / 2 - 90 + (setI * 24) - thickness, width / 2 - 145 + biggest + 15 + thickness + booleanOffset, height / 2 - 90 + (setI * 24) + 15 + thickness, 2, color1, color2);
                    RenderUtils.drawARoundedRect(width / 2 - 140 + booleanOffset, height / 2 - 90 + (setI * 24), width / 2 - 145 + biggest + 15 + booleanOffset, height / 2 - 90 + (setI * 24) + 15, 1, 0xFF1f2234);

                    fr.drawString(ms.getMode(), (float) middle - fr.getStringWidth(ms.getMode())/2,height / 2 - 88.5F + (setI * 24),0xFF5f689f);

                    double expandsize = 0.4;
                    for(double xo = -expandsize; xo <= expandsize; xo += expandsize) {
                        for(double yo = -expandsize; yo <= expandsize; yo += expandsize) {
                            fr.drawString(s.name, (float) (width / 2 - 145 + biggest + 17 + thickness + booleanOffset + xo), (float) (height / 2 - 88.5F + yo + (setI * 24)),0xFF0c0c11);
                        }
                    }

                    fr.drawString(s.name, (float) (width / 2 - 145 + biggest + 17 + thickness + booleanOffset),height / 2 - 88.5F + (setI * 24),0xFFaeb6e3);

                    booleanOffset += biggest + fr.getStringWidth(s.name) + 18;
                    if(selectedMod.settings.size() > realDex + 1) {
                        if(selectedMod.settings.get(realDex + 1) instanceof BooleanSetting || selectedMod.settings.get(realDex + 1) instanceof ModeSetting) {
                            Setting next = selectedMod.settings.get(realDex + 1);
                            if(next instanceof BooleanSetting) {
                                if (fr2.getStringWidth((selectedMod.settings.get(realDex + 1)).name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            } else {
                                double biggest_temp = 0;
                                for(String mode : ((ModeSetting)next).modes) {
                                    biggest_temp = Math.max(fr.getStringWidth(mode), biggest_temp);
                                }
                                if (fr.getStringWidth(biggest_temp + next.name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            }
                        } else booleanOffset = 0; //yes shit code but Uhhhhh ignore
                    } else booleanOffset = 0;
                } else if(s instanceof BooleanSetting) {
                    double thickness = 1;
                    RenderUtils.drawARoundedRect(width / 2 - 135 - thickness + booleanOffset, height / 2 - 90 + (setI * 24) - thickness, width / 2 - 135 + 15 + thickness + booleanOffset, height / 2 - 90 + (setI * 24) + 15 + thickness, 2, 0xff383E66);
                    RenderUtils.drawARoundedRect(width / 2 - 135 + booleanOffset, height / 2 - 90 + (setI * 24), width / 2 - 135 + 15 + booleanOffset, height / 2 - 90 + (setI * 24) + 15, 1, 0xff181A27);

                    int t = s.tTimer;
                    if(t < 0) t = 1000 + s.tTimer;
                    int color1 = ColorAnimation.getColor(0x00000000, 0xff7870F7, t / 1001f);
                    int color2 = ColorAnimation.getColor(0x00000000, 0xff495BD2, t / 1001f);

                    RenderUtils.drawGradientRoundedRect(width / 2 - 135 - thickness + booleanOffset, height / 2 - 90 + (setI * 24) - thickness, width / 2 - 135 + 15 + thickness + booleanOffset, height / 2 - 90 + (setI * 24) + 15 + thickness, 3, color1, color2);

                    //checkmark
                    for (int check = 0; check <= 1; check++) {
                        RenderUtils.drawCircleSmall(width / 2 - 139 + 8 + booleanOffset + check, height / 2 - 93 + 15 + (check * 1) + (setI * 24) - transformUp, 0, 360, 1f, 0xff181a27);
                    }
                    for (int check = 0; check <= 5; check++) {
                        RenderUtils.drawCircleSmall(width / 2 - 139 + 10 + booleanOffset + check, height / 2 - 93 + 17 - check + (setI * 24) - transformUp, 0, 360, 1f, 0xff181a27);
                    }

                    fr2.drawString(s.name, width / 2 - 115 + booleanOffset, height / 2 - 90 + (setI * 24) + 2, 0xff5F689F);

                    booleanOffset += 28 + fr2.getStringWidth(s.name);
                    if(selectedMod.settings.size() > realDex + 1) {
                        if(selectedMod.settings.get(realDex + 1) instanceof BooleanSetting || selectedMod.settings.get(realDex + 1) instanceof ModeSetting) {
                            Setting next = selectedMod.settings.get(realDex + 1);
                            if(next instanceof BooleanSetting) {
                                if (fr2.getStringWidth((selectedMod.settings.get(realDex + 1)).name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            } else {
                                double biggest_temp = 0;
                                for(String mode : ((ModeSetting)next).modes) {
                                    biggest_temp = Math.max(fr.getStringWidth(mode), biggest_temp);
                                }
                                if (fr.getStringWidth(biggest_temp + next.name) + 28 + booleanOffset < 300) {
                                    setI--;
                                } else booleanOffset = 0;
                            }
                        } else booleanOffset = 0; //yes shit code but Uhhhhh ignore
                    } else booleanOffset = 0;
                } else if(s instanceof ColorSetting) {
                    ColorSetting cs = (ColorSetting)s;

                    double thickness = 0.9;

                    fr2.drawString(s.name + ":", width / 2 - 135, height / 2 - 90 + (setI * 24) + 1, 0xff6973B0);

                    RenderUtils.drawARoundedRect(width / 2 + 75 - thickness, height / 2 - 94 + (setI * 24) - thickness, width / 2 + 140 + thickness, height / 2 - 92 + (setI * 24) + 17 + thickness, 4, 0xff202538);
                    RenderUtils.drawARoundedRect(width / 2 + 75, height / 2 - 94 + (setI * 24), width / 2 + 140 , height / 2 - 92 + (setI * 24) + 17, 4, 0xff181c29);

                    if(cs.color.a != 255) cs.color.a = 255;
                    GlStateManager.resetColor();
                    RenderUtils.drawARoundedRect(width / 2 + 88.5 - 10, height / 2 - 94 + (setI * 24) + 3.5, width / 2 + 88.5 + 13 - 10, height / 2 - 94 + (setI * 24) + 15.5, 2, cs.color.getColor());

                    String ss = "#" + cs.color.getHexString().toUpperCase();
                    if(s.tTimer > 0 && s.tTimer < 950)
                        ss = "Copied!";

                    fr2.drawString(ss, width / 2 + 95, height / 2 - 94 + (setI * 24) + 4f, 0xff898DBD);

                    RenderUtils.drawARoundedRect(width / 2 - 140 - thickness, height / 2 - 94 + (setI * 24) - thickness + 25, width / 2 + 140 + thickness, height / 2 - 94 + (setI * 24) + 90 + thickness, 5, 0xff202538);
                    RenderUtils.drawARoundedRect(width / 2 - 140, height / 2 - 94 + (setI * 24) + 25, width / 2 + 140, height / 2 - 94 + (setI * 24) + 90, 5, 0xff181c29);

                    fr2.drawString("R", width / 2 - 133, height / 2 - 94 + (setI * 24) + 30, 0xff6973B0);
                    fr2.drawString("G", width / 2 - 133, height / 2 - 94 + (setI * 24) + 51.5f, 0xff6973B0);
                    fr2.drawString("B", width / 2 - 133, height / 2 - 94 + (setI * 24) + 73, 0xff6973B0);

                    int[] colors = {0xff7E5555, 0xff567E55, 0xff3A4682};

                    for(int ci = 0; ci <= 2; ci++) {
                        double value = cs.color.getRGBvals()[ci] / 255d;
                        RenderUtils.drawARoundedRect(width / 2 - 120, height / 2 - 70 + (setI * 24) + 10.5 + (ci * 21.5), width / 2 + 115, height / 2 - 60 + (setI * 24) + 2 + (ci * 21.5), 0.5, 0xff2F3754);
                        RenderUtils.drawARoundedRect(width / 2 - 120, height / 2 - 70 + (setI * 24) + 10.5 + (ci * 21.5), width / 2 - 120 + (value * 235), height / 2 - 60 + (setI * 24) + 2 + (ci * 21.5), 0.5, colors[ci]);

                        Gui.drawRect(width / 2 - 120.5, height / 2 - 70 + (setI * 24) + 11 + (ci * 21.5), width / 2 - 119, height / 2 - 70 + (setI * 24) + 11.5 + (ci * 21.5), colors[ci]);
                        Gui.drawRect(width / 2 + 114, height / 2 - 70 + (setI * 24) + 11 + (ci * 21.5), width / 2 + 115.5, height / 2 - 70 + (setI * 24) + 11.5 + (ci * 21.5), 0xff2F3754);

                        RenderUtils.drawACircle(width / 2 - 120 + (value * 235), height / 2 - 70 + (setI * 24) + 11 + (ci * 21.5), 0, 360, 3.5, 0xffA2ADF9);
                        RenderUtils.drawACircle(width / 2 - 120 + (value * 235), height / 2 - 70 + (setI * 24) + 11 + (ci * 21.5), 0, 360, 4, 0xaaA2ADF9);
                    }

                    fr2.drawString(cs.color.getRGBvals()[0] + "", width / 2 + 120, height / 2 - 70 + (setI * 24) + 6f + (0 * 21.5f), 0xff898DBD); //q&a time!
                    fr2.drawString(cs.color.getRGBvals()[1] + "", width / 2 + 120, height / 2 - 70 + (setI * 24) + 6f + (1 * 21.5f), 0xff898DBD); //Q: WHY??? THERE IS A LOOP ABOVE
                    fr2.drawString(cs.color.getRGBvals()[2] + "", width / 2 + 120, height / 2 - 70 + (setI * 24) + 6f + (2 * 21.5f), 0xff898DBD); //A: bad font renderer and this is easier than fixing it lol
                    //A2: nvm ik how to fix but im gonna leave it herelmaiadjiusahdniusadgsa6ydfvsadhjas

                    setI += 3;
                }
                realDex++;
                setI++;
            }
            GlStateManager.translate(0,scrollOffsetSetting,1);
            Stencil.INSTANCE.stopLayer();
        }
        for(Button b : buttons) {
            //b.draw(20, 0x80000000);
        }
    }

    public void keyTyped(char typedChar, int keyCode) {

        if(selectedMod == null && isCtrlKeyDown() && keyCode == Keyboard.KEY_F) {
            searching = true;
            search = "";
        } else if(searching) {
            if(!search.isEmpty() && keyCode == Keyboard.KEY_BACK)
                search = isCtrlKeyDown() ? "" : search.substring(0,search.length()-1);
            else if("ABCDEFGHIKLMNOPQRSTVXYZ1234567890".toLowerCase().contains((typedChar+"").toLowerCase()))
                search += typedChar;
        }
        if(binding && Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).stream().filter(mod -> mod.binding).collect(Collectors.toList()).size() > 0) {
            Mod m = Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).stream().filter(mod -> mod.binding).collect(Collectors.toList()).get(0);
            if(keyCode == Keyboard.KEY_ESCAPE) {
                m.keyCode.code = 0;
            } else
                m.keyCode.code = keyCode;
            binding = false;
            m.binding = false;
        } else {
            if(System.currentTimeMillis() - openTime < 200l) return;
            if(keyCode == Keyboard.KEY_ESCAPE || keyCode == Elysium.getInstance().getModManager().getModByName("ClickGui").getKey()) {
                if(selectedMod != null) {
                    selectedMod = null; initButtons(); return;
                }
                searching = false;
                Elysium.getInstance().prefs.putInt("cgui_category", selectedCategory.ordinal());
                Elysium.getInstance().prefs.putFloat("cgui_scroll", scrollDistance);
                mc.thePlayer.closeScreen();
                openTime = Long.MAX_VALUE;
            }
        }
    }
    String[] badChars = {"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "!", ".", ",", "-", "=", "]", "[", "/", "?", "\\"};

    public void mouseClicked(int msx, int mouseY, int button) {
        int mouseX = msx;
        if(searching) {
            int i = 1;
            for(Mod m : Elysium.getInstance().getModManager().getMods()) {
                if (i > 4 || (search != null && !search.isEmpty()) && !m.name.toLowerCase().contains(search.toLowerCase()))
                    continue;
                float thick = 0.9f;
                float x1 = width / 2 - 145 - thick;
                float y1 = (float) (height / 2 - scrollOffset - 100 + (i * 44) - thick - 5);
                float x2 = width / 2 + 145 + thick;
                float y2 = (float) (height / 2 - scrollOffset - 110 + (i * 44) + 43 + thick - 5);

                if(mouseX > x1 && mouseX < x2)
                    if(mouseY > y1 && mouseY < y2) {
                        searching = false;
                        selectedCategory = m.category;
                        initButtons();
                        m.hoverTimer = 10000;
                        int index = 0;
                        for(Mod mod : Elysium.getInstance().getModManager().getModsByCategory(m.category)) {
                            index++;
                            if(mod == m)
                                break;
                        }
                        scrollDistance = index*22;
                    }
                i++;
            }
            return;
        } else
            mouseX += 23;
        if(selectedMod != null) {
            if(mouseX < width / 2 - 160 || mouseX > width / 2 + 160 || mouseY < height / 2 - 120 || mouseY > height / 2 + 120) {
                selectedMod = null;
                initButtons();
                return;
            }
        }
        for(Button b : buttons.stream().filter(b -> b.isMouseOverMe(msx - 23, (int) (mouseY))).collect(Collectors.toList())) {
            if(selectedMod == null) {
                for (int i = 0; i <= 4; i++) {
                    if (b.idname.equalsIgnoreCase("category" + i)) {
                        selectedCategory = Category.values()[i];
                        initButtons();
                    }
                }
            }
        }

        float offset = selectedMod == null ? scrollOffset : scrollOffsetSetting;

        for(Button b : buttons.stream().filter(b -> b.isMouseOverMe(msx + (selectedMod == null ? -23 : 0), (int) (mouseY + offset))).collect(Collectors.toList())) {
            if(selectedMod == null && !searching) {
                for (Mod m : Elysium.getInstance().getModManager().getModsByCategory(selectedCategory)) {
                    if (b.idname.equalsIgnoreCase(m.name + "toggle_module")) {
                        m.toggle();
                        m.tTimer = m.toggled ? 1 : -1;
                        m.wasToggledInSwipe = true;
                    } else if (b.idname.equalsIgnoreCase(m.name + "options_module")) {
                        selectedMod = m;
                        initButtons();
                    } else if (b.idname.equalsIgnoreCase(m.name + "keybind_module")) {
                        m.binding = true;
                        binding = true;
                    }
                }
            } else if(!searching) {
                for(Mod m : Elysium.getInstance().getModManager().getModsByCategory(selectedCategory)) {
                    for(Setting s : m.settings) {
                        if(b.idname.equalsIgnoreCase(m.name + s.name)) {
                            if(s instanceof BooleanSetting) {
                                if(!((BooleanSetting)s).wasToggledInSwap) {
                                    ((BooleanSetting)s).toggle();
                                    ((BooleanSetting)s).wasToggledInSwap = true;
                                    s.tTimer = ((BooleanSetting) s).isEnabled() ? 1 : -1;
                                }
                            }
                        } if(b.idname.equalsIgnoreCase(m.name + s.name + "4")) {
                            if(s instanceof ModeSetting) {
                                if(button == 1)
                                    ((ModeSetting)s).cycle();
                                else if(button == 0)
                                    ((ModeSetting)s).previous();
                            }
                        } if(b.idname.equalsIgnoreCase(m.name + s.name + "3")) {
                            ColorSetting cs = (ColorSetting)s;
                            if(button == 1) {
                                try {
                                    String clipboard = GuiScreen.getClipboardString();
                                    if(clipboard.startsWith("#")) {
                                        clipboard = clipboard.substring(1, clipboard.length() - 1);
                                    } if(clipboard.startsWith("0x")) {
                                        clipboard = clipboard.substring(4, clipboard.length() - 1);
                                    }
                                    if(clipboard.length() != 6) return;
                                    for(String ss : badChars) {
                                        if(clipboard.contains(ss))
                                            return;
                                    }

                                    int[] colorVals = {0, 0, 0};

                                    for(int j = 0; j <= 2; j++) {
                                        colorVals[j] = Integer.parseInt(clipboard.substring(j * 2, j * 2 + 2), 16);
                                    }

                                    cs.color.setRGBvals(colorVals);
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace(); //TODO: remove so yes yes xd
                                }
                            } else {
                                GuiScreen.setClipboardString(cs.color.getHexString().toUpperCase());
                                s.tTimer = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        Elysium.getInstance().getModManager().getModsByCategory(selectedCategory).forEach(mod -> mod.wasToggledInSwipe = false);
        if(selectedMod != null) {
            for(Setting s : selectedMod.settings.stream().filter(setting -> setting instanceof BooleanSetting).collect(Collectors.toList())) {
                ((BooleanSetting)s).wasToggledInSwap = false;
            }
        }
    }

    public void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        float offset = selectedMod == null ? scrollOffset : scrollOffsetSetting;
        for(Button b : buttons.stream().filter(b -> b.isMouseOverMe(mouseX, (int) (mouseY+offset))).collect(Collectors.toList())) {
            if(selectedMod == null) {
                for (Mod m : Elysium.getInstance().getModManager().getModsByCategory(selectedCategory)) {
                    if (b.idname.equalsIgnoreCase(m.name + "toggle_module") && !m.wasToggledInSwipe) {
                        m.toggle();
                        m.tTimer = m.toggled ? 1 : -1;
                        m.wasToggledInSwipe = true;
                    }
                }
            } else {
                for(Setting s : selectedMod.settings) {
                    if(b.idname.equalsIgnoreCase(selectedMod.name + s.name)) {
                        if(s instanceof NumberSetting) {
                            NumberSetting ns = (NumberSetting)s;
                            double value = (mouseX - b.x) / 270d;
                            if(value > 0.98d) {
                                ns.setValue((float)ns.getMax());
                            } else if(value < 0.02d){
                                ns.setValue((float)ns.getMin());
                            } else {
                                ns.setValue((float) (ns.getMin() + (value * (ns.getMax() - ns.getMin()))));
                            }

                        } else if(s instanceof BooleanSetting) {
                            if(!((BooleanSetting)s).wasToggledInSwap) {
                                ((BooleanSetting) s).toggle();
                                s.tTimer = ((BooleanSetting) s).isEnabled() ? 1 : -1;
                                ((BooleanSetting)s).wasToggledInSwap = true;
                            }
                        }
                    }
                    for(int i = 0; i <= 3; i++) {
                        if(b.idname.equalsIgnoreCase(selectedMod.name + s.name + (i + ""))) {
                            ColorSetting cs = (ColorSetting)s;
                            double value = (mouseX - b.x) / 235d;
                            if(value > 0.99d) {
                                cs.color.setVar(i, 255);
                            } else if(value < 0.01d){
                                cs.color.setVar(i, 0);
                            } else {
                                cs.color.setVar(i, (int) (value * 255));
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
