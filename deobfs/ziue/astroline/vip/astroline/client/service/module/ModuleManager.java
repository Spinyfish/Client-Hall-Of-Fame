/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.font.FontUtils
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.combat.AntiBot
 *  vip.astroline.client.service.module.impl.combat.KillAura
 *  vip.astroline.client.service.module.impl.combat.Velocity
 *  vip.astroline.client.service.module.impl.movement.Fly
 *  vip.astroline.client.service.module.impl.movement.Phase
 *  vip.astroline.client.service.module.impl.movement.Scaffold
 *  vip.astroline.client.service.module.impl.movement.Speed
 *  vip.astroline.client.service.module.impl.movement.Sprint
 *  vip.astroline.client.service.module.impl.movement.Step
 *  vip.astroline.client.service.module.impl.player.AutoHypixel
 *  vip.astroline.client.service.module.impl.player.AutoTool
 *  vip.astroline.client.service.module.impl.player.ChestStealer
 *  vip.astroline.client.service.module.impl.player.Disabler
 *  vip.astroline.client.service.module.impl.player.InvManager
 *  vip.astroline.client.service.module.impl.player.MouseDelayFix
 *  vip.astroline.client.service.module.impl.player.Nuker
 *  vip.astroline.client.service.module.impl.player.ResetVL
 *  vip.astroline.client.service.module.impl.player.StaffAnalyser
 *  vip.astroline.client.service.module.impl.render.Animation
 *  vip.astroline.client.service.module.impl.render.ChestESP
 *  vip.astroline.client.service.module.impl.render.ChunkAnimations
 *  vip.astroline.client.service.module.impl.render.ClickGui
 *  vip.astroline.client.service.module.impl.render.ESP
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.impl.render.Indicators
 *  vip.astroline.client.service.module.impl.render.JumpCircles
 *  vip.astroline.client.service.module.impl.render.LSD
 *  vip.astroline.client.service.module.impl.render.Radar
 *  vip.astroline.client.service.module.impl.render.SessionInfo
 *  vip.astroline.client.service.module.impl.render.Shaders
 */
package vip.astroline.client.service.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import vip.astroline.client.service.font.FontUtils;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.combat.AntiBot;
import vip.astroline.client.service.module.impl.combat.KillAura;
import vip.astroline.client.service.module.impl.combat.Velocity;
import vip.astroline.client.service.module.impl.movement.Fly;
import vip.astroline.client.service.module.impl.movement.Phase;
import vip.astroline.client.service.module.impl.movement.Scaffold;
import vip.astroline.client.service.module.impl.movement.Speed;
import vip.astroline.client.service.module.impl.movement.Sprint;
import vip.astroline.client.service.module.impl.movement.Step;
import vip.astroline.client.service.module.impl.player.AutoHypixel;
import vip.astroline.client.service.module.impl.player.AutoTool;
import vip.astroline.client.service.module.impl.player.ChestStealer;
import vip.astroline.client.service.module.impl.player.Disabler;
import vip.astroline.client.service.module.impl.player.InvManager;
import vip.astroline.client.service.module.impl.player.MouseDelayFix;
import vip.astroline.client.service.module.impl.player.Nuker;
import vip.astroline.client.service.module.impl.player.ResetVL;
import vip.astroline.client.service.module.impl.player.StaffAnalyser;
import vip.astroline.client.service.module.impl.render.Animation;
import vip.astroline.client.service.module.impl.render.ChestESP;
import vip.astroline.client.service.module.impl.render.ChunkAnimations;
import vip.astroline.client.service.module.impl.render.ClickGui;
import vip.astroline.client.service.module.impl.render.ESP;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.impl.render.Indicators;
import vip.astroline.client.service.module.impl.render.JumpCircles;
import vip.astroline.client.service.module.impl.render.LSD;
import vip.astroline.client.service.module.impl.render.Radar;
import vip.astroline.client.service.module.impl.render.SessionInfo;
import vip.astroline.client.service.module.impl.render.Shaders;

public class ModuleManager {
    private ArrayList<Module> modules = new ArrayList();

    public ModuleManager() {
        this.modules.add((Module)new AntiBot());
        this.modules.add((Module)new KillAura());
        this.modules.add((Module)new Velocity());
        this.modules.add((Module)new Fly());
        this.modules.add((Module)new Phase());
        this.modules.add((Module)new Scaffold());
        this.modules.add((Module)new Sprint());
        this.modules.add((Module)new Speed());
        this.modules.add((Module)new Step());
        this.modules.add((Module)new AutoHypixel());
        this.modules.add((Module)new AutoTool());
        this.modules.add((Module)new ChestStealer());
        this.modules.add((Module)new Disabler());
        this.modules.add((Module)new InvManager());
        this.modules.add((Module)new MouseDelayFix());
        this.modules.add((Module)new Nuker());
        this.modules.add((Module)new ResetVL());
        this.modules.add((Module)new StaffAnalyser());
        this.modules.add((Module)new Animation());
        this.modules.add((Module)new ChestESP());
        this.modules.add((Module)new ChunkAnimations());
        this.modules.add((Module)new ClickGui());
        this.modules.add((Module)new ESP());
        this.modules.add((Module)new Hud());
        this.modules.add((Module)new Indicators());
        this.modules.add((Module)new JumpCircles());
        this.modules.add((Module)new LSD());
        this.modules.add((Module)new Radar());
        this.modules.add((Module)new SessionInfo());
        this.modules.add((Module)new Shaders());
    }

    public ArrayList<Module> getModules() {
        return this.modules;
    }

    public List<Module> getModulesRender(Object font) {
        List<Module> modules = null;
        modules = this.getModules().stream().filter(module -> !module.isHidden() && module.isToggled() && (Hud.renderRenderCategory.getValue() == false || module.getCategory() != Category.Render)).collect(Collectors.toList());
        if (font instanceof FontUtils) {
            FontUtils nFont = (FontUtils)font;
            modules.sort((m1, m2) -> {
                String modText1 = m1.getDisplayName();
                String modText2 = m2.getDisplayName();
                float width1 = nFont.getStringWidth(modText1);
                float width2 = nFont.getStringWidth(modText2);
                return Float.compare(width1, width2);
            });
        }
        Collections.reverse(modules);
        return modules;
    }

    public Module getModule(String name) {
        return this.modules.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
