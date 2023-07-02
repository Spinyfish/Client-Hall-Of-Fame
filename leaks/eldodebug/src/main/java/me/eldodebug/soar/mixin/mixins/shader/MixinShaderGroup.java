package me.eldodebug.soar.mixin.mixins.shader;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import me.eldodebug.soar.utils.interfaces.IMixinShaderGroup;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;

@Mixin(ShaderGroup.class)
public abstract class MixinShaderGroup implements IMixinShaderGroup{
	
	@Override
	@Accessor
	public abstract List<Shader> getListShaders();
}
