package net.optifine.player;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.src.Config;

public class PlayerConfiguration {
    private PlayerItemModel[] playerItemModels = new PlayerItemModel[0];
    private boolean initialized = false;

    public void renderPlayerItems(final ModelBiped modelBiped, final AbstractClientPlayer player, final float scale, final float partialTicks) {
        if (this.initialized) {
            for (int i = 0; i < this.playerItemModels.length; ++i) {
                final PlayerItemModel playeritemmodel = this.playerItemModels[i];
                playeritemmodel.render(modelBiped, player, scale, partialTicks);
            }
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(final boolean initialized) {
        this.initialized = initialized;
    }

    public PlayerItemModel[] getPlayerItemModels() {
        return this.playerItemModels;
    }

    public void addPlayerItemModel(final PlayerItemModel playerItemModel) {
        this.playerItemModels = (PlayerItemModel[]) Config.addObjectToArray(this.playerItemModels, playerItemModel);
    }
}
