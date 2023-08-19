package dev.thread.api.util.render.shader.impl;

import dev.thread.api.util.render.RenderUtil;
import dev.thread.api.util.render.shader.Shader;

import java.awt.*;

public class RoundedShader extends Shader {
    @Override
    public final String getSource() {
        return "#version 130\n" +
                "\n" +
                "uniform float width;\n" +
                "uniform float height;\n" +
                "uniform vec4 color;\n" +
                "uniform float radius;\n" +
                "\n" +
                "float calc(vec2 p, vec2 b, float r) {\n" +
                "   return length(max(abs(p) - b, 0.0)) - r;\n" +
                "}\n" +
                "\n" +
                "out vec4 FragColor;\n" +
                "\n" +
                "void main() {\n" +
                "vec2 size = vec2(width, height);\n" +
                "vec2 pixel = gl_TexCoord[0].st * size;\n" +
                "vec2 centre = 0.5 * size;\n" +
                "float sa = smoothstep(0.0, 1, calc(centre - pixel, centre - radius - 1, radius));\n" +
                "vec4 c = mix(vec4(color.rgb, 1), vec4(color.rgb, 0), sa);\n" +
                "FragColor = vec4(c.rgb, color.a * c.a);\n" +
                "}";
    }
    
    public void render(float x, float y, float width, float height, float radius, Color color) {
        RenderUtil.begin();

        bind();
        uniform1f("width", width);
        uniform1f("height", height);
        uniform1f("radius", radius);
        uniform4f("color", color.getRed() / 255.0f, color.getGreen() / 255.0f,color.getBlue() / 255.0f,color.getAlpha() /255.0f);
        renderQuad(x, y, width,height);
        unbind();

        RenderUtil.end();
    }
}
