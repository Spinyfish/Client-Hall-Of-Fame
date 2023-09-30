package felix.module.impl.visuals;

import felix.api.annotations.Priority;
import org.lwjgl.opengl.GL11;

import felix.api.annotations.Handler;
import felix.events.render.EventRender3D;
import felix.module.Module;
import felix.value.impl.BooleanValue;
import felix.value.impl.NumberValue;
import net.canelex.wingsmod.RenderWings;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayer;

public class DragonWings extends Module {
	
    public BooleanValue colored = new BooleanValue("Colored", true);
    public BooleanValue chroma = new BooleanValue("Chroma", true);
    
    public NumberValue<Integer> hue = new NumberValue("Hue", 100, 1, 100, 1);
    public NumberValue<Integer> scale = new NumberValue("Scale", 100, 1, 100, 1);
	
	public DragonWings() {
		super("Dragon Wings", 0, ModuleCategory.VISUALS);
		addValues(colored, chroma, scale, hue);
        setHidden(true);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
	}
	
	@Handler(Priority.LOW)
	public void onRender3D(final EventRender3D event) {
		switch (mc.gameSettings.thirdPersonView) {
		case 1:
		case 2:
			RenderWings wing = new RenderWings(this);
			wing.onRenderPlayer(event);
			break;
		}
	}
}