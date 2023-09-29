package de.lirium.base.texturefix;

import god.buddy.aot.BCompiler;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.ArrayList;

public class FixList extends ArrayList<int[][]> {
    final TextureAtlasSprite sprite;
    boolean reload = true;

    public FixList(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public int size() {
        if(this.reload)
            reload();
        return super.size();
    }

    public boolean isEmpty() {
        return (size() == 0);
    }

    public int[][] get(int index) {
        if(this.reload)
            reload();
        return super.get(index);
    }

    public void clear() {
        this.reload = true;
        super.clear();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void reload() {
        this.reload = true;
        TextureFix.reloadTextureData(this.sprite);
        TextureFix.markForUnload(this.sprite);
    }
}
