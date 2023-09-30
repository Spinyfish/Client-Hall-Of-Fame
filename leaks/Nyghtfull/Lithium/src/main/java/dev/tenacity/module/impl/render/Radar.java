package dev.tenacity.module.impl.render;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.event.impl.render.ShaderEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.ColorSetting;
import dev.tenacity.module.settings.impl.MultipleBoolSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.objects.Dragging;
import dev.tenacity.utils.objects.GradientColorWheel;
import dev.tenacity.utils.render.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Radar extends Module {

    public final NumberSetting size = new NumberSetting("Size", 90, 125, 75, 1);
    private final ColorSetting playerColor = new ColorSetting("Player Color", Color.RED);
    private final ColorSetting mobColor = new ColorSetting("Mob Color", Color.ORANGE);
    private final ColorSetting animalColor = new ColorSetting("Animal Color", Color.BLUE);
    private final ColorSetting itemColor = new ColorSetting("Item Color", Color.YELLOW);

    public final MultipleBoolSetting targets = new MultipleBoolSetting("Entities",
            new BooleanSetting("Players", true),
            new BooleanSetting("Mobs", true),
            new BooleanSetting("Animals", true),
            new BooleanSetting("Items", true));


    public final Dragging drag = Tenacity.INSTANCE.createDrag(this, "radar", 5, 40);
    private final List<Entity> entities = new ArrayList<>();
    private final GradientColorWheel colorWheel = new GradientColorWheel();

    public Radar() {
        super("Radar", Category.RENDER, "Shows entites on a gui");
        playerColor.addParent(targets, targetsSetting -> targetsSetting.getSetting("Players").isEnabled());
        mobColor.addParent(targets, targetsSetting -> targetsSetting.getSetting("Mobs").isEnabled());
        animalColor.addParent(targets, targetsSetting -> targetsSetting.getSetting("Animals").isEnabled());

        itemColor.addParent(targets, targetsSetting -> targetsSetting.getSetting("Items").isEnabled());

        addSettings(targets, colorWheel.createModeSetting("Color Mode"), colorWheel.getColorSetting(),
                size, playerColor, mobColor, animalColor, itemColor);
    }

    @Override
    public void onShaderEvent(ShaderEvent e) {
        float x = drag.getX(), y = drag.getY(), size = this.size.getValue().floatValue(), middleX = x + size / 2f, middleY = y + size / 2f;
        if (e.getBloomOptions().getSetting("Radar").isEnabled()) {
            RoundedUtil.drawGradientRound(x, y, size, size, 6, colorWheel.getColor1(), colorWheel.getColor4(), colorWheel.getColor2(), colorWheel.getColor3());
        } else {
            RoundedUtil.drawRound(x, y, size, size, 6, Color.BLACK);

        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent e) {
        getEntities();
        float x = drag.getX(), y = drag.getY(), size = this.size.getValue().floatValue(), middleX = x + size / 2f, middleY = y + size / 2f;

        drag.setWidth(size);
        drag.setHeight(size);

        Color lineColor = new Color(255, 255, 255, 180);

        colorWheel.setColors();
        float alpha = .85f;
        RoundedUtil.drawGradientRound(x, y, size, size, 6,
                ColorUtil.applyOpacity(colorWheel.getColor1(), alpha),
                ColorUtil.applyOpacity(colorWheel.getColor4(), alpha),
                ColorUtil.applyOpacity(colorWheel.getColor2(), alpha),
                ColorUtil.applyOpacity(colorWheel.getColor3(), alpha));


        Gui.drawRect2(x - 1, y + (size / 2f - .5), size + 2, 1, lineColor.getRGB());
        Gui.drawRect2(x + (size / 2f - .5), y - 1, 1, size + 2, lineColor.getRGB());


        StencilUtil.initStencilToWrite();
        RenderUtil.renderRoundedRect(x, y, size, size, 6, -1);
        StencilUtil.readStencilBuffer(1);
        GLUtil.startRotate(middleX, middleY, mc.thePlayer.rotationYaw);

        for (Entity entity : entities) {
            double xDiff = MathUtils.interpolate(entity.prevPosX, entity.posX, mc.timer.renderPartialTicks) - MathUtils.interpolate(mc.thePlayer.prevPosX, mc.thePlayer.posX, mc.timer.renderPartialTicks);
            double zDiff = MathUtils.interpolate(entity.prevPosZ, entity.posZ, mc.timer.renderPartialTicks) - MathUtils.interpolate(mc.thePlayer.prevPosZ, mc.thePlayer.posZ, mc.timer.renderPartialTicks);
            if ((xDiff + zDiff) < (size / 2f)) {
                float translatedX = (float) (middleX - xDiff);
                float translatedY = (float) (middleY - zDiff);
                RoundedUtil.drawRound(translatedX, translatedY, 3, 3, 1f, getColor(entity));
            }
        }

        GLUtil.endRotate();
        StencilUtil.uninitStencilBuffer();
    }


    public Color getColor(Entity entity) {
        Color color = Color.WHITE;

        if (entity instanceof EntityPlayer) {
            color = playerColor.getColor();
        }
        if (entity instanceof EntityMob || entity instanceof EntityWaterMob) {
            color = mobColor.getColor();
        }

        if (entity instanceof EntityAnimal) {
            color = animalColor.getColor();
        }

        if (entity instanceof EntityItem) {
            color = itemColor.getColor();
        }

        return color;

    }


    public void getEntities() {
        entities.clear();
        for (Entity entity : mc.theWorld.loadedEntityList) {


            if (entity instanceof EntityPlayer && targets.getSetting("Players").isEnabled()) {
                if (entity != mc.thePlayer && !entity.isInvisible()) {
                    entities.add(entity);
                }
            }

            if ((entity instanceof EntityMob || entity instanceof EntityWaterMob) && targets.getSetting("Mobs").isEnabled()) {
                entities.add(entity);
            }

            if (entity instanceof EntityAnimal && targets.getSetting("Animals").isEnabled()) {
                entities.add(entity);
            }

            if (entity instanceof EntityItem && targets.getSetting("Items").isEnabled()) {
                entities.add(entity);
            }
        }
    }


}
