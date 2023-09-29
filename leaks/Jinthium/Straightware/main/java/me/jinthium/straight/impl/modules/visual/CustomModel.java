package me.jinthium.straight.impl.modules.visual;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.settings.ColorSetting;
import me.jinthium.straight.impl.settings.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.RandomUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CustomModel extends Module {

    public static final ResourceLocation amongusModel = new ResourceLocation("straight/models/amogus.png");
    public static final ResourceLocation rabbitModel = new ResourceLocation("straight/models/rabbit.png");

    public static boolean enabled = false;

    public static final ModeSetting model = new ModeSetting("Model", "Among Us", "Among Us", "Rabbit");
    private static final ModeSetting mogusColorMode = new ModeSetting("Among Us Mode", "Random", "Random", "Sync", "Custom");
    private static final ColorSetting amongusColor = new ColorSetting("Among Us Color", Color.RED);

    public CustomModel() {
        super("Custom Model", Category.VISUALS);
        mogusColorMode.addParent(model, modeSetting -> modeSetting.is("Among Us"));
        amongusColor.addParent(mogusColorMode, modeSetting -> modeSetting.is("Custom"));
        addSettings(model, mogusColorMode, amongusColor);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        enabled = false;
    }

    private static final Map<Object, Color> entityColorMap = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        entityColorMap.clear();
        enabled = true;
    }

    @Callback
    final EventCallback<WorldEvent> worldEventEventCallback = event -> {
        entityColorMap.clear();
    };

    public static Color getColor(Entity entity) {
        Color color = Color.WHITE;
        switch (mogusColorMode.getMode()) {
            case "Sync":
                Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
                color = hud.getHudColor((float) System.currentTimeMillis() / 600);
                break;
            case "Random":
                if (entityColorMap.containsKey(entity)) {
                    color = entityColorMap.get(entity);
                } else {
                    color = new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255));
                    entityColorMap.put(entity, color);
                }
                break;
            case "Custom":
                color = amongusColor.getColor();
                break;
        }

        return color;
    }

    public static double getYOffset() {
        if (model.is("Among Us")) {
            return 0.25;
        }
        return 0;
    }


}
