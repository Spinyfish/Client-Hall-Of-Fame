package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.impl.ui.shader.impl.GlowShader;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;

@ModuleInfo(name = "ChestESP", category = Category.VISUAL)
public class ChestESPModule extends Module {
    private final BooleanSetting shaderSeparateTextures = new BooleanSetting("Shader separate textures",false);
    private final NumberSetting shaderRadius = new NumberSetting("Shader radius", 10, 1, 3.5, 0.5);
    private final NumberSetting shaderExposure = new NumberSetting("Shader exposure", 5, 1, 2.5, 0.5);

    private final GlowShader glowShader = new GlowShader();

    public ChestESPModule() {
        this.registerSettings(this.shaderSeparateTextures, this.shaderRadius, this.shaderExposure);
    }

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_3D) {
            boolean rendering = false;

            for (int i = 0; i < mc.theWorld.loadedTileEntityList.size(); ++i) {
                TileEntity tileEntity = mc.theWorld.loadedTileEntityList.get(i);

                if (tileEntity instanceof TileEntityChest) {
                    rendering = true;
                    break;
                }
            }

            if (rendering) {
                this.glowShader.applyGlow(() -> {
                    for (int i = 0; i < mc.theWorld.loadedTileEntityList.size(); ++i) {
                        TileEntity tileEntity = mc.theWorld.loadedTileEntityList.get(i);

                        if (tileEntity instanceof TileEntityChest) {
                            TileEntityRendererDispatcher.instance.renderTileEntity(
                                    tileEntity,
                                    event.getPartialTicks(),
                                    -1
                            );
                        }
                    }
                });
            } else {
                this.glowShader.clearFrameBuffer();
            }
        }

        if (event.getType() == RenderEvent.Type.RENDER_2D) {
            this.glowShader.updateBuffer(
                    this.shaderRadius.getValue().floatValue(),
                    this.shaderExposure.getValue().floatValue(),
                    this.shaderSeparateTextures.getValue(),
                    UISettings.CURRENT_COLOR
            );
        }
    };
}
