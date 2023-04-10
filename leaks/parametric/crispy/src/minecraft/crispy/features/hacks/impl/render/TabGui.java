package crispy.features.hacks.impl.render;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventKey;
import crispy.features.event.impl.render.EventRenderGui;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.fonts.greatfont.TTFFontRenderer;
import crispy.util.animation.Translate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.superblaubeere27.valuesystem.Value;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@HackInfo(name = "TabGui", category = Category.RENDER)
public class TabGui extends Hack {
    private int currentTab, moduleIndex, settingIndex;
    private boolean moduleExpand, settingExpand;
    private final Translate categoryT = new Translate(0, 0);
    private final Translate moduleT = new Translate(0, 0);
    private final Translate settingT = new Translate(0, 0);
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventRenderGui) {
            TTFFontRenderer fontRendererObj = Crispy.INSTANCE.getFontManager().getDefaultFont();
            float translationFactor = (float) (14.4 / (float) Minecraft.getDebugFPS());
            Gui.drawRect(5, 30.5, 70, 30 + Category.values().length * 16 + 1.5, 0x90000000);
            categoryT.interpolate(5, 31 + currentTab * 16, translationFactor);
            Gui.drawRect(categoryT.getX(), categoryT.getY(), categoryT.getX() + 65, categoryT.getY() + 17, 0xff0090ff);
            int count = 0;
            for(Category c : Category.values()) {
                fontRendererObj.drawStringWithShadow(c.getName(), 11, 35 + count * 16, -1);
                count++;
            }
            if(moduleExpand) {
                List<Hack> hacks = Crispy.INSTANCE.getHackManager().getModules(Category.values()[currentTab]);

                List<Hack> bop = new ArrayList<Hack>(hacks);
                bop.sort((o, o2) -> (int) (fontRendererObj.getWidth(o.getName()) - fontRendererObj.getWidth(o2.getName())));

                Gui.drawRect(70, 30.5, 70 + 6 + fontRendererObj.getWidth(bop.get(hacks.size() - 1).getName()), 30 + hacks.size() * 16 + 1.5, 0x90000000);
                moduleT.interpolate(70, 33 + moduleIndex * 16, translationFactor);
                Gui.drawRect(moduleT.getX(), moduleT.getY(), moduleT.getX() + 4 + fontRendererObj.getWidth(bop.get(hacks.size() - 1).getName()), moduleT.getY() + 12, 0xff0090ff);
                count = 0;
                for (Hack hack : hacks) {
                    fontRendererObj.drawStringWithShadow(hack.getName(), 73, 35 + count * 16, hack.isEnabled() ? new Color(150, 150, 150).getRGB() : -1);
                    count++;
                }
            }
            if(settingExpand) {
                List<Hack> hacks = Crispy.INSTANCE.getHackManager().getModules(Category.values()[currentTab]);
                List<Value> values = Crispy.INSTANCE.getValueManager().getAllValuesFrom(hacks.get(moduleIndex).getName());
                List<Hack> bop = new ArrayList<Hack>(hacks);
                bop.sort((o, o2) -> (int) (fontRendererObj.getWidth(o.getName()) - fontRendererObj.getWidth(o2.getName())));
                if(!values.isEmpty()) {
                    double left = 70 + 6 + fontRendererObj.getWidth(bop.get(hacks.size() - 1).getName());
                    Gui.drawRect(left, 30.5, left + 6 + 120, 30 + values.size() * 16 + 1.5, 0x90000000);
                    settingT.interpolate(left + 2, 33 + settingIndex * 16, translationFactor);
                    Gui.drawRect(settingT.getX(), settingT.getY(), settingT.getX() + 4 + 118, settingT.getY() + 12, 0xff0090ff);

                    count = 0;
                    for (Value value : values) {

                        fontRendererObj.drawStringWithShadow(value.getName() + ": " + value.getObject(), (float) (left + 2), 35 + count * 16,  -1);
                        count++;
                    }
                }
            }

        }
        if(e instanceof EventKey) {
            int code = ((EventKey) e).getCode();
            List<Hack> hacks = Crispy.INSTANCE.getHackManager().getModules(Category.values()[currentTab]);
            if(code == Keyboard.KEY_UP) {

                List<Value> values = null;
                if(settingExpand) {
                    values = Crispy.INSTANCE.getValueManager().getAllValuesFrom(hacks.get(moduleIndex).getName());
                }
                if(!moduleExpand) {
                    if (currentTab <= 0) {
                        currentTab = Category.values().length - 1;
                    } else {
                        currentTab--;
                    }
                } else if(!settingExpand){
                    if(moduleIndex <= 0) {
                        moduleIndex = hacks.size() - 1;
                    } else {
                        moduleIndex--;
                    }
                } else {
                    if(settingIndex <= 0) {
                        settingIndex = values.size() - 1;
                    } else {
                        settingIndex--;
                    }
                }

            }
            if(code == Keyboard.KEY_DOWN) {

                List<Value> values = null;
                if(settingExpand) {
                    values = Crispy.INSTANCE.getValueManager().getAllValuesFrom(hacks.get(moduleIndex).getName());
                }


                if(!moduleExpand) {
                    if (currentTab >= Category.values().length - 1) {
                        currentTab = 0;
                    } else {
                        currentTab++;
                    }
                } else if(!settingExpand){
                    if(moduleIndex >= hacks.size() - 1) {
                        moduleIndex = 0;
                    } else {
                        moduleIndex++;
                    }
                } else {
                    if(settingIndex >= values.size() -1) {
                        settingIndex = 0;
                    } else {
                        settingIndex++;
                    }
                }
            }
            if(code == Keyboard.KEY_RIGHT) {

                List<Value> values = null;
                if(moduleExpand) {
                    values = Crispy.INSTANCE.getValueManager().getAllValuesFrom(hacks.get(moduleIndex).getName());
                }
                if(!settingExpand) {
                    settingIndex = 0;
                }
                if(!moduleExpand) {
                    moduleIndex = 0;
                }
                if(moduleExpand && !values.isEmpty()) {
                    settingExpand = true;
                }
                moduleExpand = true;

            }
            if(code == Keyboard.KEY_LEFT) {
                if(settingExpand) {
                    settingExpand = false;
                } else {
                    moduleExpand = false;
                }

            }
            if(code == Keyboard.KEY_RETURN) {
                hacks.get(moduleIndex).toggle();
            }


        }
    }
}
