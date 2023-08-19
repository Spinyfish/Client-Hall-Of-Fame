package cc.swift.util.shader;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class ShaderProgram {

    @Getter
    private final int programID;

    public ShaderProgram(String fragment) {
        this(fragment, "vertex.glsl");
    }

    public ShaderProgram(String fragment, String vertex) {
        int programID = GL20.glCreateProgram();

        String vertexShader = readFile("vertex/" + vertex);
        int vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        GL20.glShaderSource(vertexShaderID, vertexShader);
        GL20.glCompileShader(vertexShaderID);
        if (GL20.glGetShaderi(vertexShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println("Vertex Shader failed to compile!");
            System.out.println(GL20.glGetShaderInfoLog(vertexShaderID, Integer.MAX_VALUE));
        }

        String fragmentShader = readFile("fragment/" + fragment);
        int fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragmentShaderID, fragmentShader);
        GL20.glCompileShader(fragmentShaderID);
        if (GL20.glGetShaderi(fragmentShaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println("Fragment Shader failed to compile!");
            System.out.println(GL20.glGetShaderInfoLog(fragmentShaderID, Integer.MAX_VALUE));
        }

        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        GL20.glLinkProgram(programID);

        this.programID = programID;
    }

    public void start() {
        GL20.glUseProgram(programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void draw() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        double width = sr.getScaledWidth_double();
        double height = sr.getScaledHeight_double();
        draw(0, 0, width, height);
    }

    public void draw(double x, double y, double width, double height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x, y, 0).tex(0, 1).endVertex();
        worldRenderer.pos(x, y + height, 0).tex(0, 0).endVertex();
        worldRenderer.pos(x + width, y + height, 0).tex(1, 0).endVertex();
        worldRenderer.pos(x + width, y, 0).tex(1, 1).endVertex();
        tessellator.draw();
    }

    private String readFile(String path) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("assets/minecraft/swift/shader/" + path)));
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}