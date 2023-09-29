package dev.eternal.client.render.engine;

import dev.eternal.client.render.engine.program.LinkedProgram;

import java.nio.FloatBuffer;

public record DrawingData(VAO vao, VBO vbo, LinkedProgram program, FloatBuffer vertices,
                          int verticesCount) {

}
