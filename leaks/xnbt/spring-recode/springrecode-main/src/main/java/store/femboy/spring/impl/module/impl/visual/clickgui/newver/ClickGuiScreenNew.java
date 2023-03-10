package store.femboy.spring.impl.module.impl.visual.clickgui.newver;

import gay.sukumi.irc.ChatClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import store.femboy.spring.Spring;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.ModuleManager;
import store.femboy.spring.impl.module.impl.visual.clickgui.ClickGui;
import store.femboy.spring.impl.module.settings.Settings;
import store.femboy.spring.impl.module.settings.impl.BooleanSetting;
import store.femboy.spring.impl.module.settings.impl.ModeSetting;
import store.femboy.spring.impl.module.settings.impl.NumberSettings;
import store.femboy.spring.impl.util.FontUtil;
import store.femboy.spring.impl.util.RenderUtil;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public final class ClickGuiScreenNew extends GuiScreen {

    //private final Animation animation = new Animation();
    Module selectedModule = null;
    Category selectedCat = Category.PLAYER;

    int width = 420;
    int height = 300;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        float x = (float) (this.width - width) / 2;
        float y = (float) (this.height - height) / 2;

        RenderUtil.drawRoundedRect(x+20, y+20, x+width+20, y+height+20,5, new Color(40,45,45).getRGB());
        RenderUtil.drawRoundedRect(x+60, y+20, x+100, y+height+20,5,  new Color(30,30,30).getRGB());
        RenderUtil.drawRoundedRect(x+20, y+20, x+50, y+height+20,5,  new Color(20,20,20).getRGB());
        RenderUtil.drawRoundedRect(x+20, y+height + 20, x+width+20, 28,5,  new Color(20,20,20).getRGB());
        FontUtil.nunitobold22.drawString("Welcome to Spring, " + ChatClient.INSTANCE.getProfile().getUsername() + ".", x+23, y+this.height+28, -1);

        switch (ClickGui.femboyMode.getMode()){
            case "None":
                break;
            case "Astolfo 1":
                RenderUtil.drawImage(new ResourceLocation("spring/femboys/Astolfo1.png"), this.width / 2 + 550, this.height /2 + 130, (int) 190.25f, (int) 233.25f);
                break;
            case "Astolfo 2":
                RenderUtil.drawImage(new ResourceLocation("spring/femboys/Astolfo2.png"), this.width / 2 + 500, this.height /2 + 100, (int) 250f, (int) 350f);
                break;
            case "Astolfo 3":
                RenderUtil.drawImage(new ResourceLocation("spring/femboys/Astolfo3.png"), this.width / 2 + 550, this.height /2 + 130, (int) 190.25f, (int) 233.25f);
                break;
            case "Astolfo 4":
                RenderUtil.drawImage(new ResourceLocation("spring/femboys/Astolfo4.png"), this.width / 2 + 575, this.height /2 + 220, (int) 137.75f, (int) 134.5f);
                break;
        }

        int ccount=0;
        int count=0;

        for(Category cat : Category.values()) {
            ccount++;
            RenderUtil.drawRoundedRect(x + 34,y + 5 - 6 + (ccount * 30),20, 20, 5, new Color(50,50,50).getRGB());
            FontUtil.icons.drawString(cat.name, x + 37, y + 5 + (ccount * 30), new Color(200,200,200).getRGB());
        }
        for(Module m : ModuleManager.getModulesByCategory(selectedCat)) {
            count++;
            if(m.toggled){
                FontUtil.normal.drawString(m.name, x + 75, y + 10 + (count * 16), new Color(200,200,200).getRGB());
            }else{
                FontUtil.normal.drawString(m.name, x + 75, y + 10 + (count * 16), new Color(255,255,255).getRGB());
            }

            int index = 0;
            if (!m.settings.isEmpty()) {
                if(selectedModule != null) {
                    for (Settings setting : selectedModule.settings) {
                        if (setting instanceof BooleanSetting) {
                            BooleanSetting bool = (BooleanSetting) setting;
                            if(bool.parent != null) {
                                if(bool.mode != null) {
                                    if(Objects.equals(bool.parent.getMode(), bool.mode)) {
                                        FontUtil.normal.drawString(setting.name,  x + 165, y + 26 + index * 16, -1);
                                        RenderUtil.drawRoundedRect(x + width + 20 - 18, y + 26 + index * 16, 10, 10, 5, !bool.enabled ? new Color(250,0,0).getRGB() : new Color(0,250,0).getRGB());
                                    } else {
                                        index--;
                                    }
                                }
                            } else {
                                FontUtil.normal.drawString(setting.name,  x + 165, y + 26 + index * 16, -1);
                                RenderUtil.drawRoundedRect(x + width + 20 - 18, y + 26 + index * 16, 10, 10, 5, !bool.enabled ? new Color(250,0,0).getRGB() : new Color(0,250,0).getRGB());
                            }
                        }
                        if (setting instanceof NumberSettings) {
                            NumberSettings number = (NumberSettings) setting;
                            if(number.parent != null) {
                                if(number.mode != null) {
                                    if(Objects.equals(number.parent.getMode(), number.mode)) {

                                        FontUtil.normal.drawString(setting.name + ": " + number.getValue(), x + 165, y + 26 + index * 16, -1);
                                        FontUtil.normal.drawString("+", (int) (x + width+ 20 - 5), (int) (y + 26 + index * 16), -1);
                                        FontUtil.normal.drawString("/", (int) (x + width+ 20 - 15), (int) (y + 26 + index * 16), -1);
                                        FontUtil.normal.drawString("-", (int) (x + width+ 20 - 25), (int) (y + 26 + index * 16), -1);
                                    } else {
                                        index--;
                                    }
                                }
                            } else {
                                FontUtil.normal.drawString(setting.name + ": " + number.getValue(), x + 165, y + 26 + index * 16, -1);
                                FontUtil.normal.drawString("+", (int) (x + width+ 20 - 5), (int) (y + 26 + index * 16), -1);
                                FontUtil.normal.drawString("/", (int) (x + width+ 20 - 15), (int) (y + 26 + index * 16), -1);
                                FontUtil.normal.drawString("-", (int) (x + width+ 20 - 25), (int) (y + 26 + index * 16), -1);
                            }
                        }
                        if (setting instanceof ModeSetting) {
                            ModeSetting mode = (ModeSetting) setting;
                            FontUtil.normal.drawString(setting.name + ": " + mode.getMode(), x + 165, y + 27 + index * 16, -1);
                        }
                        index++;
                    }
                }
            }


        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        float x = (float) (this.width - width) / 2;
        float y = (float) (this.height - height) / 2;
        int count = 0;
        int ccount = 0;
        for(Category cat : Category.values()) {
            ccount++;
            if(hoverCoords(x + 37, y + 5 + (ccount * 30),20, 20, mouseX, mouseY)) {
                selectedCat = cat;
            }

        }
        for(Module m : ModuleManager.getModulesByCategory(selectedCat)) {
            count++;
            if(hoverCoords(x + 75, y + 10 + (count * 16), (float) FontUtil.normal.getStringWidth(m.name), FontUtil.normal.getHeight(), mouseX, mouseY)) {
                if(mouseButton == 0) {
                    m.toggle();
                } if(mouseButton == 1) {
                    selectedModule = m;

                }
            }
        }

        int index = 0;
        if(selectedModule != null) {
            for (Settings setting : selectedModule.settings) {
                if (setting instanceof BooleanSetting) {
                    BooleanSetting bool = (BooleanSetting) setting;
                    if(bool.parent != null) {
                        if(bool.mode != null) {
                            if(Objects.equals(bool.parent.getMode(), bool.mode)) {
                                if(hoverCoords(x + width + 20 - 18, y + 26 + index * 16, 10, 10, mouseX, mouseY)) {
                                    ((BooleanSetting) setting).toggle();
                                }
                            } else {
                                index--;
                            }
                        }
                    } else {
                        if(hoverCoords(x + width + 20 - 18, y + 26 + index * 16, 10, 10, mouseX, mouseY)) {
                            ((BooleanSetting) setting).toggle();
                        }
                    }
                }

                if (setting instanceof NumberSettings) {
                    NumberSettings number = (NumberSettings) setting;
                    if(number.parent != null) {
                        if(number.mode != null) {
                            if(Objects.equals(number.parent.getMode(), number.mode)) {

                                if(hoverCoords(x + width + 20 - 5, y + 26 + index * 16, 10, FontUtil.normal.getHeight(), mouseX, mouseY)) {
                                    ((NumberSettings)setting).step(true);
                                } else if(hoverCoords(x + width + 20 - 25, y + 28 + index * 16, 10, 10, mouseX, mouseY)) {
                                    ((NumberSettings)setting).step(false);
                                }
                            }
                            else {
                                index--;
                            }
                        }
                    } else {
                        if(hoverCoords(x + width - 5 + 20, y + 26 + index * 16, 10, FontUtil.normal.getHeight(), mouseX, mouseY)) {
                            ((NumberSettings)setting).step(true);
                        } else if(hoverCoords(x + width - 25 + 20, y + 28 + index * 16, 10, 10, mouseX, mouseY)) {
                            ((NumberSettings)setting).step(false);
                        }
                    }
                }
                if (setting instanceof ModeSetting) {
                    ModeSetting mode = (ModeSetting) setting;
                    if(hoverCoords(x + 165 + 20, y + 27 + index * 16, (float) (10 + FontUtil.normal.getStringWidth(setting.name + ": " + mode.getMode())), 10, mouseX, mouseY)) {
                        ((ModeSetting)setting).cycle();
                    }
                }
                index++;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    public boolean hoverCoords(float x, float y, float width, float height, int mouseX, int mouseY){
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height-0.5f;
    }

    @Override
    public void onGuiClosed()
    {
        Spring.INSTANCE.getManager().getModule("ClickGUI").toggled = false;
    }
}
