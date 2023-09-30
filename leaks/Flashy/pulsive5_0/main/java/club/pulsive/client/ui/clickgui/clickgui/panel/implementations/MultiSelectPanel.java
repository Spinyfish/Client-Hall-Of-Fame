package club.pulsive.client.ui.clickgui.clickgui.panel.implementations;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.client.ui.clickgui.clickgui.component.SettingComponent;
import club.pulsive.client.ui.clickgui.clickgui.component.implementations.BooleanComponent;
import club.pulsive.client.ui.clickgui.clickgui.panel.Panel;
import club.pulsive.impl.module.impl.visual.HUD;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;

@Getter
@Setter
public class MultiSelectPanel extends SettingComponent<MultiSelectEnumProperty> {

    private MultiSelectEnumProperty property;
    private BooleanComponent booleanComponent;
    private boolean hovered;
    private boolean extended;
    int current;
    public int count = 0;

    public MultiSelectPanel(MultiSelectEnumProperty property, float x, float y, float width, float height, boolean visible) {
        super(property, x, y, width, height, visible);
        this.property = property;
//        Arrays.stream(property.getConstants()).forEach(constant -> {
//            Property<Boolean> setting = new Property<>(StringUtils.upperSnakeCaseToPascal(constant.toString()), property.isSelected(constant));
//            setting.addValueChange(((oldValue, value) -> {
//                int index = 0;
//                for (Enum constants : property.getConstants()) {
//                    if (constants == constant)
//                        property.setValue(index, value);
//                    index++;
//                }
//            }));
//            components.add(new BooleanComponent(setting, x, y, width, height));
//        });
    }

    public boolean isaVisible() {
        return property.available();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        hovered = RenderUtil.isHovered(x, y + Fonts.moonSmall.getHeight() * 3 + count, width, Fonts.moonSmall.getHeight(), mouseX, mouseY);
        if (visible) {
            theme.drawMulti(this, x, y, width, height);
        }
        //super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHovered(mouseX, mouseY)) {
            if(mouseButton == 1) {
                Logger.printSysLog("e");
                extended = !extended;
            }
        }
        if(extended) {
            for (Enum e : getProperty().getValues()){
                if(isHovered2(x, y + (Fonts.moonSmall.getHeight() + 4) * Arrays.asList(getProperty().getValues()).indexOf(e) + 15, x +  Fonts.moonSmall.getStringWidth(e.name()), y + (Fonts.moonSmall.getHeight() + 4) * Arrays.asList(getProperty().getValues()).indexOf(e) + 8 + 15, mouseX, mouseY)) {
                    if( getProperty().isSelected(Arrays.asList(getProperty().getValues()).indexOf(e))) {
                        getProperty().unselect(Arrays.asList(getProperty().getValues()).indexOf(e));
                    } else {
                        getProperty().select(Arrays.asList(getProperty().getValues()).indexOf(e));
                    }
                }
                GL11.glPushMatrix();
                GL11.glPopMatrix();
                GlStateManager.color(1, 1, 1, 1);
                RenderUtil.color(-1);
                //count += Fonts.moonSmall.getHeight() + 4;
            }
        }
    }
    public boolean isHovered2(double x, double y, double width, double height, int mouseX, int mouseY) {
        return mouseX > x && mouseY > y && mouseX < width && mouseY < height;
    }
    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }
}
