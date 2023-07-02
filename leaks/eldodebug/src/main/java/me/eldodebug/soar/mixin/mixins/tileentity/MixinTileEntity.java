package me.eldodebug.soar.mixin.mixins.tileentity;

import org.spongepowered.asm.mixin.Mixin;

import me.eldodebug.soar.utils.interfaces.ICullable;
import net.minecraft.tileentity.TileEntity;

@Mixin(TileEntity.class)
public class MixinTileEntity implements ICullable{

	private boolean culled;
	
	public boolean isCulled() {
		return culled;
	}

	public void setCulled(boolean culled) {
		this.culled = culled;
	}

}
