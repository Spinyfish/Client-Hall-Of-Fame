package juul.util;

import juul.Juul;
import juul.inject.wrappers.*;

public interface Utils {
    MinecraftWrapper mc = Juul.INSTANCE.constants.getMC();
    GlStateManagerWrapper glStateManager = Juul.INSTANCE.getWrapperManager().get(GlStateManagerWrapper.class);
    EntityPlayerSPWrapper thePlayerSP = Juul.INSTANCE.constants.playerSPWrapper();
    EntityPlayerWrapper thePlayer = thePlayerSP.getEntityPlayer();
    TessellatorWrapper tessalator = Juul.INSTANCE.getWrapperManager().get(TessellatorWrapper.class);
    WorldRendererWrapper worldRenderer = Juul.INSTANCE.getWrapperManager().get(WorldRendererWrapper.class);
    ItemSoupWrapper itemSoup = Juul.INSTANCE.getWrapperManager().get(ItemSoupWrapper.class);
    ItemPotionWrapper itemPotion = Juul.INSTANCE.getWrapperManager().get(ItemPotionWrapper.class);

}
