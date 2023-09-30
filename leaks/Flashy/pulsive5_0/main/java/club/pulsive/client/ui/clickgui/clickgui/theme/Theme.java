package club.pulsive.client.ui.clickgui.clickgui.theme;


import club.pulsive.client.ui.clickgui.clickgui.component.implementations.BooleanComponent;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.ColorPickerComponent;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.EnumComponent;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.SliderComponent;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.CategoryPanel;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.ModulePanel;
import club.pulsive.client.ui.clickgui.clickgui.panel.implementations.MultiSelectPanel;
import club.pulsive.impl.module.Module;

public interface Theme {
    void drawCategory(CategoryPanel panel, float x, float y, float width, float height);
    void drawModule(ModulePanel panel, float x, float y, float width, float height);
    void drawMulti(MultiSelectPanel panel, float x, float y, float width, float height);
    void drawBindComponent(Module module, float x, float y, float width, float height, boolean focused);
    void drawBooleanComponent(BooleanComponent component, float x, float y, float width, float height, float settingWidth, float settingHeight, int opacity);
    void drawEnumComponent(EnumComponent component, float x, float y, float width, float height);
    void drawSliderComponent(SliderComponent component, float x, float y, float width, float height, float length);
    void drawColorPickerComponent(ColorPickerComponent component, float x, float y, float width, float height);
}
