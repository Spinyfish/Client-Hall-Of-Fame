package me.eldodebug.soar.management.mods.impl;

import java.util.ArrayList;
import java.util.List;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventPreMotionUpdate;
import me.eldodebug.soar.management.events.impl.EventRender3D;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.render.RenderUtils;
import net.minecraft.util.Vec3;

public class BreadcrumbsMod extends Mod{

    private List<Vec3> path = new ArrayList<>();
    
	public BreadcrumbsMod() {
		super("Breadcrumbs", "Render the trail you walked on", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		this.addBooleanSetting("Timeout", this, true);
		this.addSliderSetting("Time", this, 15, 1, 150, true);
	}

	@EventTarget
	public void onRender3D(EventRender3D event) {
		RenderUtils.renderBreadCrumbs(path);
	}
	
	@EventTarget
	public void onPreMotionUpdate(EventPreMotionUpdate event) {
        if (mc.thePlayer.lastTickPosX != mc.thePlayer.posX || mc.thePlayer.lastTickPosY != mc.thePlayer.posY || mc.thePlayer.lastTickPosZ != mc.thePlayer.posZ) {
            path.add(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
        }
        
        if (Soar.instance.settingsManager.getSettingByName(this, "Timeout").getValBoolean()) {
            while (path.size() > (int) Soar.instance.settingsManager.getSettingByName(this, "Time").getValDouble()) {
                path.remove(0);
            }
        }
	}
}
