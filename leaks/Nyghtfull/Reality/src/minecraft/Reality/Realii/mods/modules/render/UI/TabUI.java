
package Reality.Realii.mods.modules.render.UI;

import Reality.Realii.Client;
import Reality.Realii.event.EventBus;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.misc.EventKey;
import Reality.Realii.event.events.rendering.EventRender2D;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.event.value.Value;
import Reality.Realii.managers.Manager;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.ClientSettings;
import Reality.Realii.mods.modules.render.HUD;
import Reality.Realii.guis.font.CFontRenderer;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.utils.cheats.player.Helper;
import Reality.Realii.utils.math.MathUtil;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import Reality.Realii.utils.render.RenderUtil;

public class TabUI
        implements Manager {
    private Section section = Section.TYPES;
    private ModuleType selectedType = ModuleType.values()[0];
    private Module selectedModule = null;
    private Value selectedValue = null;
    private int currentType = 0;
    private int currentModule = 0;
    private int currentValue = 0;
    public static int height = 16;
    private int maxType;
    private int maxModule;
    private int maxValue;
    private static int[] Sections;
    private float modulesXanim = 0;

    @Override
    public void init() {
        ModuleType[] arrmoduleType = ModuleType.values();
        int n = arrmoduleType.length;
        int n2 = 0;
        while (n2 < n) {
            ModuleType mt = arrmoduleType[n2];
            if (this.maxType <= Helper.mc.fontRendererObj.getStringWidth(mt.name().toUpperCase()) + 4) {
                this.maxType = Helper.mc.fontRendererObj.getStringWidth(mt.name().toUpperCase()) + 4;
            }
            ++n2;
        }
        for (Module m : ModuleManager.getModules()) {
            if (this.maxModule > Helper.mc.fontRendererObj.getStringWidth(m.getName().toUpperCase()) + 4) continue;
            this.maxModule = Helper.mc.fontRendererObj.getStringWidth(m.getName().toUpperCase()) + 4;
        }
        for (Module m : ModuleManager.getModules()) {
            if (m.getValues().isEmpty()) continue;
            for (Value val : m.getValues()) {
                if (this.maxValue > Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + 4)
                    continue;
                this.maxValue = Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + 4;
            }
        }
        this.maxModule += 12;
        this.maxValue += 24;
        boolean highestWidth = false;
        this.maxType = this.maxType < this.maxModule ? this.maxModule : this.maxType;
        this.maxModule += this.maxType;
        this.maxValue += this.maxModule;
        EventBus.getInstance().register(this);
    }

    private void resetValuesLength() {
        this.maxValue = 0;
        for (Value val : this.selectedModule.getValues()) {
            int off;
            int n = off = val instanceof Option ? 6 : Helper.mc.fontRendererObj.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
            if (this.maxValue > Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off)
                continue;
            this.maxValue = Helper.mc.fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off;
        }
        this.maxValue += this.maxModule;
    }

    float modY = 0;
    float cateY = 0;


    @EventHandler
    private void renderTabGUI(EventRender2D e) {
        if (!HUD.tabGui.getValue()) {
            return;
        }
        CFontRenderer font = FontLoaders.arial16;
        FontRenderer fontv2 = Minecraft.getMinecraft().fontRendererObj;
        CFontRenderer fontv3 = FontLoaders.roboto16;
        CFontRenderer fontv4 = FontLoaders.arial16B;
      

        
        if (Helper.mc.gameSettings.showDebugInfo || !Client.instance.getModuleManager().getModuleByClass(HUD.class).isEnabled())
            return;
        int categoryY = this.height;
        float moduleY = categoryY;
        int valueY = categoryY;
        RenderUtil.drawRect(0,0,0,0,0);
        RenderUtil.drawRect(2.0f, categoryY, 55, categoryY + 12 * ModuleType.values().length, new Color(33, 33, 33, 180).getRGB());
        ModuleType[] moduleArray = ModuleType.values();
        int mA = moduleArray.length;
        int mA2 = 0;
        while (mA2 < mA) {
            ModuleType mt = moduleArray[mA2];
            if (this.selectedType == mt) {
                if (cateY != categoryY) {
                    cateY += (categoryY - cateY) / 10;
                }
                Gui.drawRect(2, (double) cateY, 55, (double) (cateY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2, new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue()).getRGB());
                moduleY = categoryY;
            }
            if (HUD.tabuifont.getValue().equals("Arial")) {
            font.drawString(mt.name(), 6, categoryY + 4, -1);
            categoryY += 12;
            ++mA2;
            }
            
            
            if (HUD.tabuifont.getValue().equals("Vannila")) {
                fontv2.drawString(mt.name(), 6, categoryY + 4, -1);
                categoryY += 12;
                ++mA2;
            }
            
            if (HUD.tabuifont.getValue().equals("Sigma")) {
                fontv3.drawString(mt.name(), 6, categoryY + 4, -1);
                categoryY += 12;
                ++mA2;
            }
            
            if (HUD.tabuifont.getValue().equals("Thick")) {
                fontv4.drawString(mt.name(), 6, categoryY + 4, -1);
                categoryY += 12;
                ++mA2;
            }
            
            
            
        }
        if (this.section == Section.MODULES || modulesXanim > 1) {
            if (modulesXanim < 60) {
                modulesXanim += (60 - modulesXanim) / 20.0;
            }
            Gui.drawRect(60, moduleY, 60 + modulesXanim, moduleY + 12 * Client.instance.getModuleManager().getModulesInType(this.selectedType).size(), new Color(33, 33, 33, 180).getRGB());
            for (Module m : Client.instance.getModuleManager().getModulesInType(this.selectedType)) {
                if (this.selectedModule == m) {
                    if (modY != moduleY) {
                        modY += (moduleY - modY) / 10;
                    }
                    Gui.drawRect(60, (double) modY, 60 + modulesXanim, (double) (modY + Helper.mc.fontRendererObj.FONT_HEIGHT) + 2, new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue()).getRGB());
                }
                
                    if (HUD.tabuifont.getValue().equals("Arial")) {
                    	if (this.selectedModule == m) {
                    
                    font.drawString(m.getName(), 64, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                } else {
                    font.drawString(m.getName(), 64, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                }
                    }
                    
                    
                    if (HUD.tabuifont.getValue().equals("Vannila")) {
                    	if (this.selectedModule == m) {
                    	
                    	
                    	
                        
                    fontv2.drawString(m.getName(), 64, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                } else {
                    fontv2.drawString(m.getName(), 64, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                }
                    }
                    
                    if (HUD.tabuifont.getValue().equals("Sigma")) {
                    	if (this.selectedModule == m) {
                    	
                        
                    fontv3.drawString(m.getName(), 64, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                } else {
                    fontv3.drawString(m.getName(), 64, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                }
                    }
                    
                    if (HUD.tabuifont.getValue().equals("Thick")) {
                    	if (this.selectedModule == m) {
                    
                    fontv4.drawString(m.getName(), 64, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                } else {
                    fontv4.drawString(m.getName(), 64, moduleY + 3, m.isEnabled() ? -1 : 11184810);
                }
                    }
                    
                    	
                    	
                
                moduleY += 12;
            }
        }
        if (modulesXanim > 0 && this.section != Section.MODULES) {
            modulesXanim -= 5;
        }
        return;
    }

    @EventHandler
    private void onKey(EventKey e) {
        if (!Helper.mc.gameSettings.showDebugInfo) {
            block0:
            switch (e.getKey()) {
                case 208: {
                    switch (TabUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            ++this.currentType;
                            if (this.currentType > ModuleType.values().length - 1) {
                                this.currentType = 0;
                            }
                            this.selectedType = ModuleType.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            ++this.currentModule;
                            if (this.currentModule > Client.instance.getModuleManager().getModulesInType(this.selectedType).size() - 1) {
                                this.currentModule = 0;
                            }
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            ++this.currentValue;
                            if (this.currentValue > this.selectedModule.getValues().size() - 1) {
                                this.currentValue = 0;
                            }
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                        }
                    }
                    break;
                }
                case 200: {
                    switch (TabUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            --this.currentType;
                            if (this.currentType < 0) {
                                this.currentType = ModuleType.values().length - 1;
                            }
                            this.selectedType = ModuleType.values()[this.currentType];
                            break block0;
                        }
                        case 2: {
                            --this.currentModule;
                            if (this.currentModule < 0) {
                                this.currentModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).size() - 1;
                            }
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            break block0;
                        }
                        case 3: {
                            --this.currentValue;
                            if (this.currentValue < 0) {
                                this.currentValue = this.selectedModule.getValues().size() - 1;
                            }
                            this.selectedValue = this.selectedModule.getValues().get(this.currentValue);
                        }
                    }
                    break;
                }
                case 205: {
                    switch (TabUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            this.currentModule = 0;
                            this.selectedModule = Client.instance.getModuleManager().getModulesInType(this.selectedType).get(this.currentModule);
                            this.section = Section.MODULES;
                            modulesXanim = 0;
                            break block0;
                        }
                    }
                    break;
                }
                case 28: {
                    switch (TabUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.selectedModule.setEnabled(!this.selectedModule.isEnabled());
                            break block0;
                        }
                        case 3: {
                            this.section = Section.MODULES;
                        }
                    }
                    break;
                }
                case 203: {
                    switch (TabUI.$SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section()[this.section.ordinal()]) {
                        case 1: {
                            break block0;
                        }
                        case 2: {
                            this.section = Section.TYPES;
                            this.currentModule = 0;
                            break block0;
                        }
                        case 3: {
                            if (this.selectedValue instanceof Option) {
                                this.selectedValue.setValue((Boolean) this.selectedValue.getValue() == false);
                            } else if (this.selectedValue instanceof Numbers) {
                                Numbers value = (Numbers) this.selectedValue;
                                double inc = (Double) value.getValue();
                                inc -= ((Double) value.getIncrement()).doubleValue();
                                if ((inc = MathUtil.toDecimalLength(inc, 1)) < (Double) value.getMinimum()) {
                                    inc = (Double) ((Numbers) this.selectedValue).getMaximum();
                                }
                                this.selectedValue.setValue(inc);
                            } else if (this.selectedValue instanceof Mode) {
                                Mode theme = (Mode) this.selectedValue;
                                Enum current = (Enum) theme.getValue();
                                int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
                                this.selectedValue.setValue(theme.getModes()[next]);
                            }
                            this.maxValue = 0;
                            for (Value val : this.selectedModule.getValues()) {
                                int off;
                                int n = off = val instanceof Option ? 6 : Minecraft.getMinecraft().fontRendererObj.getStringWidth(String.format(" \u00a77%s", val.getValue().toString())) + 6;
                                if (this.maxValue > Minecraft.getMinecraft().fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off)
                                    continue;
                                this.maxValue = Minecraft.getMinecraft().fontRendererObj.getStringWidth(val.getDisplayName().toUpperCase()) + off;
                            }
                            this.maxValue += this.maxModule;
                        }
                    }
                }
            }
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$enjoytheban$module$modules$render$UI$TabUI$Section() {
        int[] arrn;
        int[] arrn2 = Sections;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Section.values().length];
        try {
            arrn[Section.MODULES.ordinal()] = 2;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
        try {
            arrn[Section.TYPES.ordinal()] = 1;
        } catch (NoSuchFieldError noSuchFieldError) {
        }
        Sections = arrn;
        return Sections;
    }

    public static enum Section {
        TYPES,
        MODULES,
    }

}

