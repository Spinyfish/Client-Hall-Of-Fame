package de.lirium.util.render.shader;


import de.lirium.util.interfaces.IMinecraft;
import god.buddy.aot.BCompiler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class ShaderUtils implements IMinecraft {

    public static String readShader(String fileName) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (fileName.startsWith("/"))
            fileName = fileName.substring(1);
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(mc.getResourceManager().getResource(new ResourceLocation(String.format("lirium/shaders/%s", fileName))).getInputStream());
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}