package dev.africa.pandaware.utils.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.impl.module.render.HUDModule;
import lombok.experimental.UtilityClass;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;

@UtilityClass
public class CapeUtils {
    public boolean shouldRenderCape(AbstractClientPlayer abstractClientPlayer) {
        HUDModule hud = Client.getInstance().getModuleManager().getByClass(HUDModule.class);
        boolean isUser = abstractClientPlayer instanceof EntityPlayerSP && hud.getData().isEnabled() &&
                hud.getShowCape().getValue();

        return abstractClientPlayer.getLocationCape() != null || isUser;
    }

    public void bindTexture(AbstractClientPlayer abstractClientPlayer, RenderPlayer renderPlayer) {
        HUDModule hud = Client.getInstance().getModuleManager().getByClass(HUDModule.class);
        boolean isUser = abstractClientPlayer instanceof EntityPlayerSP && hud.getData().isEnabled() &&
                hud.getShowCape().getValue();

        if (isUser) {
            ResourceLocation resource = hud.getCapeMode().getValue().getResource();
            if (hud.getCapeMode().getValue() == HUDModule.CapeMode.PANDAWARE) {
                resource = hud.getAnimatedCape();
            }
            if (hud.getCapeMode().getValue() == HUDModule.CapeMode.CAR) {
                resource = hud.getCar();
            }
            renderPlayer.bindTexture(resource);
        } else {
            renderPlayer.bindTexture(abstractClientPlayer.getLocationCape());
        }
    }
}
