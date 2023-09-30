package io.github.raze.modules.collection.client;

import io.github.raze.Raze;
import io.github.raze.modules.collection.combat.Aura;
import io.github.raze.modules.collection.combat.Velocity;
import io.github.raze.modules.collection.movement.*;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.modules.system.ingame.ModuleIngame;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.utilities.collection.fonts.CFontUtil;
import io.github.raze.utilities.system.fonts.CFontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ActiveModules extends ModuleIngame {

    public ArraySetting size, type;
    public ArraySetting horizontal, vertical, mode;

    public ActiveModules() {
        super("ActiveModules", "Displays active modules as a list.", ModuleCategory.CLIENT);

        this.x = 350;
        this.y = 350;

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                mode = new ArraySetting(this, "Font Mode", "Jello", "Jello", "Minecraft"),

                size = new ArraySetting(this, "Font Size", "20", "12", "16", "20", "24", "28", "32", "36", "40"),
                type = new ArraySetting(this, "Font Type", "Light", "Light", "Regular", "Medium"),

                horizontal = new ArraySetting(this, "Horizontal", "Normal", "Normal", "Reverse"),
                vertical = new ArraySetting(this, "Horizontal", "Normal", "Normal", "Reverse")

        );

        setEnabled(true);
    }

    @Override
    public void renderIngame() {

        ScaledResolution scaledResolution = new ScaledResolution(mc);


        CFontRenderer font = CFontUtil.getFontByName("Jello" + " " + type.get() + " " + size.get()).getRenderer();

        List<BaseModule> active = Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getEnabledModules()

                .stream()

                .sorted(Comparator.comparingDouble(module -> {

                    String addThis = module.getName();

                    // I am trying to remove as much warnings in this project as possible.
                    // But at this point, I don't think it's even worth it, considering the code quality.

                    if(addThis == "Speed") {
                        addThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Speed.class).mode.get();
                    }

                    if(addThis == "Flight") {
                        addThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Flight.class).mode.get();
                    }

                    if(addThis == "Velocity") {
                        addThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Velocity.class).mode.get();
                    }

                    if(addThis == "Glide") {
                        addThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Glide.class).mode.get();
                    }

                    if(addThis == "NoSlow") {
                        addThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(NoSlow.class).mode.get();
                    }

                    if(addThis == "Scaffold") {
                        addThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Scaffold.class).rotationMode.get();
                    }

                    if(addThis == "Aura") {
                        addThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Aura.class).rotation.get();
                    }

                    return font.getStringWidth(addThis);

                }))

                .collect(Collectors.toList());

        Collections.reverse(active);

        active.forEach(module -> {

            String drawThis = module.getName();

            if(drawThis == "Speed") {
                drawThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Speed.class).mode.get();
            }

            if(drawThis == "Flight") {
                drawThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Flight.class).mode.get();
            }

            if(drawThis == "Velocity") {
                drawThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Velocity.class).mode.get();
            }

            if(drawThis == "Glide") {
                drawThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Glide.class).mode.get();
            }

            if(drawThis == "NoSlow") {
                drawThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(NoSlow.class).mode.get();
            }

            if(drawThis == "Scaffold") {
                drawThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Scaffold.class).rotationMode.get();
            }

            if(drawThis == "Aura") {
                drawThis = module.getName() + " - " + Raze.INSTANCE.MANAGER_REGISTRY.MODULE_REGISTRY.getModule(Aura.class).rotation.get();
            }

            int index = active.indexOf(module);

            switch (mode.get()) {
                case "Jello": {
                    font.drawString(
                            drawThis,
                            scaledResolution.getScaledWidth() - font.getStringWidth(drawThis) - 3,
                            2 + index * (font.getHeight() + 2),
                            Color.WHITE
                    );
                    break;
                }
                case "Minecraft": {
                    mc.fontRenderer.drawString(
                            drawThis,
                            scaledResolution.getScaledWidth() - mc.fontRenderer.getStringWidth(module.getName()) - 2,
                            2 + index * (mc.fontRenderer.FONT_HEIGHT + 2),
                            Color.WHITE.getRGB()
                    );
                    break;
                }
            }

        });


    }

    @Override
    public void renderDummy() {
        renderIngame();
    }
}
