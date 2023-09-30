package club.pulsive.client.ui.clickgui.clickgui;

import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.api.ui.ConfigButton;
import club.pulsive.api.ui.ConfigPanel;
import club.pulsive.api.ui.config.ConfigElement;
import club.pulsive.client.ui.clickgui.clickgui.component.Component;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.*;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.CategoryPanel;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.ModulePanel;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.MultiSelectPanel;
import club.pulsive.client.ui.clickgui.clickgui.theme.Theme;
import club.pulsive.client.ui.clickgui.clickgui.theme.implementations.MainTheme;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.ColorProperty;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.render.FolderUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.secondary.GuiTextFieldCustom;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainCGUI extends GuiScreen {

    private final Theme currentTheme;

    private final List<Component> objects = new ArrayList<>();

    private final float componentWidth = 110;
    private final float componentHeight = 17;

    public GuiTextFieldCustom textField;
    private ConfigPanel panel;

    //private final BlurShader blurShader;

    public MainCGUI() {

        currentTheme = new MainTheme();

        //blurShader = new BlurShader(clickGUIModule.blurIntensityProperty().getValue().intValue());
        float posX = 6;
        float posY = 4;
        for (Category category : Category.values()) {
            objects.add(new CategoryPanel(category, posX, posY, componentWidth, componentHeight) {
                @Override
                public void init() {
                    List<Module> list = Pulsive.INSTANCE.getModuleManager().getModulesInCategory(category);
                    Collections.sort(list, new Comparator<Module>() {
                        @Override
                        public int compare(Module p1, Module p2) {
                            return String.CASE_INSENSITIVE_ORDER.compare(p1.getName(), p2.getName());
                        }
                    });
                    for (Module module : list) {
                        getComponents().add(new ModulePanel(module, x, y, componentWidth, componentHeight) {
                            @Override
                            public void init() {
                                components.add(new BindComponent(module, x, y, componentWidth, componentHeight));
                                for (Property property : Module.propertyRepository().propertiesBy(module.getClass())) {
                                    if (property.getValue() instanceof Boolean)
                                        components.add(new BooleanComponent(property, x, y, componentWidth, componentHeight, property.available()));
                                    if (property instanceof EnumProperty)
                                        components.add(new EnumComponent((EnumProperty) property, x, y, componentWidth, componentHeight, property.available()));
                                    if (property instanceof DoubleProperty)
                                        components.add(new SliderComponent((DoubleProperty) property, x, y, componentWidth, componentHeight, property.available()));
                                    if (property instanceof MultiSelectEnumProperty)
                                        components.add(new MultiSelectPanel((MultiSelectEnumProperty) property, x, y, componentWidth, componentHeight, property.available()));
                                    if (property instanceof ColorProperty)
                                        components.add(new ColorPickerComponent((ColorProperty) property, x, y, componentWidth, componentHeight * 5));
                                    property.addValueChange((oldValue, value) -> updateComponents());
                                }
                                updateComponents();
                            }
                        });
                    }
                }
            });
            //objects.add(panel = new ConfigPanel(40, Display.getHeight() - 40, 47, 15));
            posX += componentWidth + 3;
        }
    }

    @Override
    public void initGui() {
        // this.panel.initGui();
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        this.buttonList.add(new GuiButton(1, 20, scaledresolution.getScaledHeight() - 150, 100, 10, "Load"));
        objects.forEach(Component::reset);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        ConfigElement.onMouse();
    }

    @Override
    public void onGuiClosed() {
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.mouseReleased(0, 0, 0);
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //this.textField.drawTextBox();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        //RenderUtil.makeCropBox(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.drawScreen(mouseX, mouseY);
        });

        // RenderUtil.destroyCropBox();


        ShaderRound.drawRound(15, scaledResolution.getScaledHeight() - 30, 20, 20, 10, new Color(12,12,12,200));
        ShaderRound.drawRound(15 + 22, scaledResolution.getScaledHeight() - 30, Fonts.moon.getStringWidth(Pulsive.INSTANCE.getClientDirConfigs().toString()) + 4, 20, 5, new Color(12,12,12,200));
        Fonts.moon.drawString(Pulsive.INSTANCE.getClientDirConfigs().toAbsolutePath().toString(),15 + 24, scaledResolution.getScaledHeight() - 23,-1);

        Fonts.undefeated.drawString("v",20, scaledResolution.getScaledHeight() - 22,-1);
        Gui.drawRect(0,0,0,0,0);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.mouseClicked(mouseX, mouseY, mouseButton);
        });
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        if(isHovered2(0, scaledResolution.getScaledHeight() - 30, 30, scaledResolution.getScaledHeight(), mouseX, mouseY)) {
            mc.displayGuiScreen(new ConfigPanel(40, Display.getHeight() - 40, 47, 15));
        }
        if(isHovered2(15 + 22, scaledResolution.getScaledHeight() - 30, 15 + 22 + Fonts.moon.getStringWidth(Pulsive.INSTANCE.getClientDirConfigs().toString()) + 4, (scaledResolution.getScaledHeight() - 30) + 20, mouseX, mouseY)){
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(java.awt.Desktop.Action.BROWSE) && Pulsive.INSTANCE.getClientDirConfigs().toAbsolutePath() != null) {
                    URI uri = Pulsive.INSTANCE.getClientDirConfigs().toAbsolutePath().toUri(); // url is a string containing the URL
                    desktop.browse(uri);
                }
            }
            Logger.printSysLog("Opening folder: " + Pulsive.INSTANCE.getClientDirConfigs().toString());
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.mouseReleased(mouseX, mouseY, state);
        });
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean focused = false;
        for (Component panel : objects)
            if (panel.isVisible() && panel.focused())
                focused = true;
        if (!focused) {
            super.keyTyped(typedChar, keyCode);
        }
        objects.forEach(panel -> {
            if (panel.isVisible()) panel.keyTyped(typedChar, keyCode);
        });
    }

    public void mouseClicked(int mouseX, int mouseY) {

    }

    public boolean isHovered2(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}