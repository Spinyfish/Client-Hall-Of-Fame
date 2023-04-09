package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.impl.events.Render3DEvent;
import tech.dort.dortware.impl.utils.render.RenderUtils;

public class ChestESP extends Module {

    private final NumberValue red = new NumberValue("Red", this, 255, 0, 255, true);
    private final NumberValue green = new NumberValue("Green", this, 50, 0, 255, true);
    private final NumberValue blue = new NumberValue("Blue", this, 50, 0, 255, true);
    public final NumberValue width = new NumberValue("Width", this, 3, 1, 5, true);

    public ChestESP(ModuleData moduleData) {
        super(moduleData);
        register(red, green, blue, width);
    }

    @Subscribe
    public void onRender(Render3DEvent event) {
        for (Object o : mc.theWorld.loadedTileEntityList) {
            if (o instanceof TileEntityChest || o instanceof TileEntityEnderChest) {
                RenderUtils.drawChestESP(((TileEntity) o).getPos(), red.getValue().floatValue() / 255F, green.getValue().floatValue() / 255F, blue.getValue().floatValue() / 255F, width.getValue().floatValue());
            }
        }
    }
}
