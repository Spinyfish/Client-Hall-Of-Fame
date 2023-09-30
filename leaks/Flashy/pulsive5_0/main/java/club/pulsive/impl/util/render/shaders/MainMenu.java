package club.pulsive.impl.util.render.shaders;

import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.util.render.secondary.ShaderUtil;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;

import static org.lwjgl.opengl.GL11.*;

public class MainMenu implements MinecraftUtil {

    public static ShaderUtil shaderUtil = new ShaderUtil("mainMenu.frag");
    public static ShaderUtil shaderUtil2 = new ShaderUtil("mainMenu2.frag");
    @Setter
    @Getter
    private static float time;

    public static void nigger(){
        shaderUtil.init();
        setUniforms();
        ShaderUtil.drawQuads();
        shaderUtil.unload();
        //time++;
    }
    public static void nigger2(){
        shaderUtil2.init();
        setUniforms();
        ShaderUtil.drawQuads();
        shaderUtil2.unload();
        //time++;
    }

    public static void setUniforms(){
        shaderUtil.setUniformf("time", time);
        shaderUtil.setUniformf("resolution", mc.displayWidth, mc.displayHeight);
    }

}