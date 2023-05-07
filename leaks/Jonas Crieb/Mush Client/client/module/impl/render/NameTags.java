package client.module.impl.render;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.render.Render3DEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.util.player.MathUtils;
import client.util.player.PlayerUtil;
import client.util.player.RenderUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "NameTags", description = "NameTags", category = Category.RENDER)
public class NameTags extends Module {
    @EventLink()
    public final Listener<Render3DEvent> onRender3D = event -> {

    };
   

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
