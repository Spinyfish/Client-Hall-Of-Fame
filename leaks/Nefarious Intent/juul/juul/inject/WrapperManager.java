package juul.inject;

import juul.inject.wrappers.*;

import java.util.HashMap;

public class WrapperManager {

    public HashMap<Class<? extends Wrapper>, Wrapper> wrappers;

    public WrapperManager() {
        wrappers = new HashMap<>();

        wrappers.put(GlStateManagerWrapper.class, new GlStateManagerWrapper());
        wrappers.put(MinecraftWrapper.class, new MinecraftWrapper());
        wrappers.put(GuiInventoryWrapper.class, new GuiInventoryWrapper());
        wrappers.put(ItemPotionWrapper.class, new ItemPotionWrapper());
        wrappers.put(ItemSoupWrapper.class, new ItemSoupWrapper());
        wrappers.put(TessellatorWrapper.class, new TessellatorWrapper());
        wrappers.put(WorldRendererWrapper.class, new WorldRendererWrapper(((TessellatorWrapper) get(TessellatorWrapper.class)).getWorldRenderer()));
    }

    public <T extends Wrapper> T get(Class<? extends Wrapper> wrapperClass) { return (T) wrappers.get(wrapperClass); }


}
