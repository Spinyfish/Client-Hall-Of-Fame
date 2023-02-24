package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.kansio.client.event.impl.EntityLivingRenderEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.value.value.NumberValue;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

@ModuleData(
        name = "Chams",
        category = ModuleCategory.VISUALS,
        description = "Shows players behind walls"
)
@Getter
public class Chams extends Module {

    public BooleanValue fill = new BooleanValue("Fill", this, true);

    public NumberValue<Integer> alpha = new NumberValue<Integer>("Alpha", this, 255, 0, 255, 1);
    public NumberValue<Integer> r = new NumberValue<>("Red", this, 255, 0, 255, 1);
    public NumberValue<Integer> g = new NumberValue<Integer>("Green", this, 255, 0, 255, 1);
    public NumberValue<Integer> b = new NumberValue<Integer>("Blue", this, 255, 0, 255, 1);

    @Subscribe
    public void onRender(EntityLivingRenderEvent event) {
        if (event.isPre() && event.getEntity() instanceof EntityPlayer) {
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0f, -1100000.0f);
        } else if (event.isPost() && event.getEntity() instanceof EntityPlayer) {
            GL11.glDisable(32823);
            GL11.glPolygonOffset(1.0f, 1100000.0f);
        }
    }
}
