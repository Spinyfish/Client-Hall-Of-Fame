package dev.eternal.client.render.engine.dispatch;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import dev.eternal.client.render.engine.PipelineApplier;
import dev.eternal.client.render.engine.RenderContext;
import dev.eternal.client.render.engine.VAO;
import dev.eternal.client.render.engine.VBO;
import dev.eternal.client.render.engine.program.LinkedProgram;
import dev.eternal.client.render.engine.program.ProgramLinker;
import dev.eternal.client.render.engine.program.ShaderProgram;
import dev.eternal.client.render.engine.shard.RenderShard;
import dev.eternal.client.render.engine.vertex.VertexAttributeFormat;
import dev.eternal.client.render.engine.vertex.VertexBuffer;
import dev.eternal.client.util.math.MatrixUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

@Getter
public abstract class RenderDispatcher {

  protected final FloatBuffer modelViewBuffer = BufferUtils.createFloatBuffer(16);
  protected final FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
  private final List<RenderShard> renderShards = new ArrayList<>();
  protected VertexAttributeFormat format;
  protected VAO vao;
  protected VBO vbo;
  protected LinkedProgram program;
  protected VertexBuffer buffer;
  protected float width, height;
  private Matrix4f viewMatrix;
  private ProgramLinker programLinker = new ProgramLinker();
  private int lastSize;

  public abstract DispatcherMetaData metaData();

  public void init(RenderContext context) {
    var linkFunction = internalInit(context);

    this.vbo.bind(GL_ARRAY_BUFFER);
    this.program = linkFunction.apply(this.programLinker);
    this.applyVertexAttributes();

    this.viewMatrix = MatrixUtil.scale(1, 1, 1);

    this.applyUniforms(this.program().program());
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);

    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
  }

  public abstract Consumer<ShaderProgram> uniformCallback();

  public void applyUniforms(ShaderProgram program) {

    FloatBuffer modelViewMatrix = BufferUtils.createFloatBuffer(16);
    GlStateManager.getFloat(GL_MODELVIEW_MATRIX, modelViewMatrix);
    int uniformModel = glGetUniformLocation(program.program(), "modelView");
    glUniformMatrix4(uniformModel, false, modelViewMatrix);

    FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
    GlStateManager.getFloat(GL_PROJECTION_MATRIX, projectionMatrix);
    int uniformProjection = glGetUniformLocation(program.program(), "projection");
    glUniformMatrix4(uniformProjection, false, projectionMatrix);
  }

  public void applyVertexAttributes() {
    var link = this.program.link();
    var strideSum =
        Arrays.stream(link.attributes()).mapToDouble(VertexAttributeFormat::size).sum();

    VertexAttributeFormat[] attributes = link.attributes();
    var pointer = 0;
    program.program().use();
    for (int i = 0; i < attributes.length; i++) {
      final VertexAttributeFormat attribute = attributes[i];
      final String location =
          link.attributeLocations().length == attributes.length
              && link.attributeLocations()[i] != null
              ? link.attributeLocations()[i]
              : attribute.location();
      int attrib = glGetAttribLocation(program.program().program(), location);
      glEnableVertexAttribArray(attrib);
      glVertexAttribPointer(
          attrib,
          attribute.size(),
          attribute.type(),
          false,
          (int) (strideSum * Float.BYTES),
          (long) pointer * Float.BYTES);
      pointer += attribute.size();
    }
    program.program().unUse();
  }

  protected abstract Function<ProgramLinker, LinkedProgram> internalInit(RenderContext context);

  public void begin(float width, float height) {
    this.width = width;
    this.height = height;
  }

  public void initBuffers(int size) {
    if (lastSize == size) {
      return;
    }

    if (buffer == null) {
      if (vao != null) {
        vao.delete();
      }
      if (vbo != null) {
        vbo.delete();
      }
      vao = new VAO();
      vbo = new VBO();
    }
    if (buffer != null) {
      buffer.clear();
    }

    vao.bind();
    vbo.bind(GL_ARRAY_BUFFER);
    buffer = new VertexBuffer();
    long bufferSize = (long) buffer.capacity() * Float.BYTES;
    vbo.uploadData(GL_ARRAY_BUFFER, bufferSize, GL_DYNAMIC_DRAW);
    lastSize = size;
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  private void renderShardsPre() {
    for (RenderShard renderShard : this.renderShards) {
      renderShard.preRender();
      renderShard.uniform(this::applyUniforms);
    }
  }

  private void renderShardsPost() {
    for (RenderShard renderShard : this.renderShards) {
      renderShard.postRender();
    }
  }

  public void dispatchRenderer(int mode, int vertices, PipelineApplier callback) {
    if (vertices > 0) {
      GlStateManager.alphaFunc(GL_GREATER, 0.0F);
      prepareRenderer();
      callback.apply();
      render(mode, vertices);
      finishRender();
      GlStateManager.alphaFunc(GL_GREATER, 0.2F);
    }
  }

  public void prepareRenderer() {
    buffer.flip();

    if (vao != null) {
      vao.bind();
    } else {
      vbo.bind(GL_ARRAY_BUFFER);
      applyVertexAttributes();
    }
    this.program.program().use();
  }

  public void render(int mode, int vertices) {
    renderShardsPre();
    vbo.bind(GL_ARRAY_BUFFER);
    vbo.uploadData(GL_ARRAY_BUFFER, (long) buffer.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
    vbo.uploadSubData(GL_ARRAY_BUFFER, 0, buffer.buffer());
    glDrawArrays(mode, 0, vertices);
    renderShardsPost();
  }

  public RenderDispatcher applyRenderShard(RenderShard shard) {
    this.renderShards.add(shard);
    return this;
  }

  public void finishRender() {
    buffer.clear();
    this.program.program().unUse();
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
  }
}
