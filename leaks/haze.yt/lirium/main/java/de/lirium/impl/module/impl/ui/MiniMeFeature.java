package de.lirium.impl.module.impl.ui;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.drag.DragHandler;
import de.lirium.base.drag.Draggable;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.impl.events.Render2DEvent;
import de.lirium.impl.events.RotationEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.rotation.RotationUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLSync;

@ModuleFeature.Info(name = "Mini Me", description = "A smaller version of you mfg", category = ModuleFeature.Category.UI)
public class MiniMeFeature extends ModuleFeature {

    @Value(name = "Size")
    private final SliderSetting<Integer> size = new SliderSetting<>(30, 1, 100);

    @Value(name = "Motion")
    private final CheckBox motion = new CheckBox(true);

    @Value(name = "Static Yaw")
    private final CheckBox staticYaw = new CheckBox(false);

    @Value(name = "Yaw")
    private final SliderSetting<Float> yaw = new SliderSetting<>(0F, -180F, 180F, new Dependency<>(staticYaw, true));

    @Value(name = "Fights")
    private final CheckBox fights = new CheckBox(false);

    private final Draggable miniMe = DragHandler.setupDrag(this, "Mini Me", 3, 200, true);

    float lastYaw, mouseX, yawOffset;
    float renderYaw;
    int ticks;

    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = e -> render();

    @Override
    public void onEnable() {
        this.mouseX = 0;
        this.renderYaw = 0;
        this.yawOffset = RotationUtil.yaw;
        super.onEnable();
    }

    private void render() {
        if(staticYaw.getValue()) {
            mouseX = yaw.getValue() * -1;
            renderYaw = yaw.getValue() * -1;
        } else {
            if (lastYaw == RotationUtil.yaw) {
                if (ticks > 150 && mc.mouseHelper.deltaX == 0 && mc.mouseHelper.deltaY == 0) {
                    yawOffset = mouseX;
                }
                if (ticks++ > 500) {
                    mouseX = 0;
                    yawOffset = 0;
                    renderYaw = 0;
                }

            } else {
                mouseX = RotationUtil.yaw;
                ticks = 0;
                renderYaw += RotationUtil.yaw - RotationUtil.prevYaw;
            }
        }

        if(!targets.isEmpty() && fights.getValue()) {
            renderYaw = 70;

            final EntityLivingBase entity = targets.get(0);
            double motion = (entity.posY - entity.prevPosY) * 10;
            motion = MathHelper.clamp(motion, -10, 10);

            if (!this.motion.getValue())
                motion = 0;

            GuiInventory.drawEntityOnScreen((int) ((int) miniMe.getXPosition() + size.getValue() / 2 - size.getValue() - entity.width * 30), (int) ((int) miniMe.getYPosition() + size.getValue() * 2 + motion), size.getValue(), 0, 0, -70, -70, entity.rotationPitch, entity);
        }

        GlStateManager.disableLighting();
        double motion = (getPlayer().posY - getPlayer().prevPosY) * 10;
        motion = MathHelper.clamp(motion, -10, 10);

        if (!this.motion.getValue())
            motion = 0;

        GuiInventory.drawEntityOnScreen((int) miniMe.getXPosition() + size.getValue() / 2, (int) ((int) miniMe.getYPosition() + size.getValue() * 2 + motion), size.getValue(), 0, 0, renderYaw, renderYaw, RotationUtil.pitch, getPlayer());
        miniMe.setObjectWidth(size.getValue());
        miniMe.setObjectHeight(size.getValue() * 2);
        lastYaw = RotationUtil.yaw;
    }
}
