package me.jinthium.straight.impl.modules.visual;

import best.azura.irc.utils.Wrapper;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.movement.WorldEvent;
import me.jinthium.straight.impl.event.render.Render3DEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.particle.Particle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BreadCrumbs extends Module {

    private final BooleanSetting timeout = new BooleanSetting("Remove", false);
    private final NumberSetting maxParticles = new NumberSetting("Max Particles", 50, 10, 1000, 1);
    private final List<Vec3> path = new ArrayList<>();

    public BreadCrumbs(){
        super("Breadcrumbs", Category.VISUALS);
        maxParticles.addParent(timeout, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(timeout, maxParticles);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        path.clear();
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event -> {
        if(event.isPre()){
            if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ)
                path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));

            if(timeout.isEnabled()) {
                while (path.size() > maxParticles.getValue())
                    path.remove(0);
            }
        }
    };

    @Callback
    final EventCallback<WorldEvent> worldEventEventCallback = event -> {
        path.clear();
    };

    @Callback
    final EventCallback<Render3DEvent> render3DEventEventCallback = event -> {
        if(path.isEmpty())
            return;

        RenderUtil.renderBreadCrumbs(path);
    };
}
