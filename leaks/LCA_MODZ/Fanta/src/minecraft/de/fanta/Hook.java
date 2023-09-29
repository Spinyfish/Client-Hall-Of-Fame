package de.fanta;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.LWJGLException;

public class Hook extends Minecraft {

    public Hook(GameConfiguration gameConfig) {
        super(gameConfig);
    }

    protected void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException {
        super.drawSplashScreen(textureManagerInstance);

    }
}
