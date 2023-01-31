package intent.AquaDev.aqua.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderBackground {
   public static final String VERTEX_SHADER = "#version 130\n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}";
   private final Minecraft mc = Minecraft.getMinecraft();
   private final int program = GL20.glCreateProgram();
   private final long startTime = System.currentTimeMillis();
   private final String fragment;

   public ShaderBackground() {
      this(
         "#ifdef GL_ES\nprecision mediump float;\n#endif\n\n#extension GL_OES_standard_derivatives : enable\n\nuniform float time;\nuniform vec2 mouse;\nuniform vec2 resolution;\n\nvec3 mod289(vec3 x) {\n  return x - floor(x * (1.0 / 289.0)) * 289.0;\n}\n\nvec4 mod289(vec4 x) {\n  return x - floor(x * (1.0 / 289.0)) * 289.0;\n}\n\nvec4 permute(vec4 x) {\n     return mod289(((x*34.0)+1.0)*x);\n}\n\nvec4 taylorInvSqrt(vec4 r)\n{\n  return 1.79284291400159 - 0.85373472095314 * r;\n}\n\nfloat snoise(vec3 v)\n  { \n  const vec2  C = vec2(1.0/6.0, 1.0/3.0) ;\n  const vec4  D = vec4(0.0, 0.5, 1.0, 2.0);\n\n// First corner\n  vec3 i  = floor(v + dot(v, C.yyy) );\n  vec3 x0 =   v - i + dot(i, C.xxx) ;\n\n// Other corners\n  vec3 g = step(x0.yzx, x0.xyz);\n  vec3 l = 1.0 - g;\n  vec3 i1 = min( g.xyz, l.zxy );\n  vec3 i2 = max( g.xyz, l.zxy );\n\n  //   x0 = x0 - 0.0 + 0.0 * C.xxx;\n  //   x1 = x0 - i1  + 1.0 * C.xxx;\n  //   x2 = x0 - i2  + 2.0 * C.xxx;\n  //   x3 = x0 - 1.0 + 3.0 * C.xxx;\n  vec3 x1 = x0 - i1 + C.xxx;\n  vec3 x2 = x0 - i2 + C.yyy; // 2.0*C.x = 1/3 = C.y\n  vec3 x3 = x0 - D.yyy;      // -1.0+3.0*C.x = -0.5 = -D.y\n\n// Permutations\n  i = mod289(i); \n  vec4 p = permute( permute( permute( \n             i.z + vec4(0.0, i1.z, i2.z, 1.0 ))\n           + i.y + vec4(0.0, i1.y, i2.y, 1.0 )) \n           + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));\n\n// Gradients: 7x7 points over a square, mapped onto an octahedron.\n// The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)\n  float n_ = 0.142857142857; // 1.0/7.0\n  vec3  ns = n_ * D.wyz - D.xzx;\n\n  vec4 j = p - 49.0 * floor(p * ns.z * ns.z);  //  mod(p,7*7)\n\n  vec4 x_ = floor(j * ns.z);\n  vec4 y_ = floor(j - 7.0 * x_ );    // mod(j,N)\n\n  vec4 x = x_ *ns.x + ns.yyyy;\n  vec4 y = y_ *ns.x + ns.yyyy;\n  vec4 h = 1.0 - abs(x) - abs(y);\n\n  vec4 b0 = vec4( x.xy, y.xy );\n  vec4 b1 = vec4( x.zw, y.zw );\n\n  //vec4 s0 = vec4(lessThan(b0,0.0))*2.0 - 1.0;\n  //vec4 s1 = vec4(lessThan(b1,0.0))*2.0 - 1.0;\n  vec4 s0 = floor(b0)*2.0 + 1.0;\n  vec4 s1 = floor(b1)*2.0 + 1.0;\n  vec4 sh = -step(h, vec4(0.0));\n\n  vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;\n  vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;\n\n  vec3 p0 = vec3(a0.xy,h.x);\n  vec3 p1 = vec3(a0.zw,h.y);\n  vec3 p2 = vec3(a1.xy,h.z);\n  vec3 p3 = vec3(a1.zw,h.w);\n\n//Normalise gradients\n  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));\n  p0 *= norm.x;\n  p1 *= norm.y;\n  p2 *= norm.z;\n  p3 *= norm.w;\n\n// Mix final noise value\n  vec4 m = max(0.5 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);\n  m = m * m;\n  return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1), \n                                dot(p2,x2), dot(p3,x3) ) );\n  }\n\nvec3 normal(vec3 v, float delta) {\n\tvec2 coefficient = vec2(\n\t\tsnoise(v + vec3(delta, 0.0, 0.0)) - snoise(v - vec3(delta, 0.0, 0.0)),\n\t\tsnoise(v + vec3(0.0, delta, 0.0)) - snoise(v - vec3(0.0, delta, 0.0))\n\t) / delta;\n\tcoefficient *= 0.3;\n\tvec3 req = vec3(\n\t\t-coefficient.x,\n\t\t-coefficient.y,\n\t\t1.0\n\t);\n\treq /= length(req);\n\t\n\treturn req;\n}\n\nvoid main( void ) {\n\tvec2 uv = gl_FragCoord.xy / min(resolution.x, resolution.y) * 0.6;\n\t\n\tvec3 p = normal(vec3(uv, time*0.43), 0.01);\n\tp = (p + vec3(1.0)) * 0.5;\n\n\tgl_FragColor = vec4(p, 1.0);\n\n}\n"
      );
   }

   public ShaderBackground(String fragment) {
      this.initShader(fragment);
      this.fragment = fragment;
   }

   public void initShader(String frag) {
      int vertex = GL20.glCreateShader(35633);
      int fragment = GL20.glCreateShader(35632);
      GL20.glShaderSource(
         vertex, "#version 130\n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}"
      );
      GL20.glShaderSource(fragment, frag);
      GL20.glValidateProgram(this.program);
      GL20.glCompileShader(vertex);
      GL20.glCompileShader(fragment);
      GL20.glAttachShader(this.program, vertex);
      GL20.glAttachShader(this.program, fragment);
      GL20.glLinkProgram(this.program);
   }

   public void renderFirst() {
      GlStateManager.enableAlpha();
      this.bindShader();
   }

   public void renderSecond(int scaledWidth, int scaledHeight) {
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glBegin(7);
      GL11.glTexCoord2d(0.0, 1.0);
      GL11.glVertex2d(0.0, 0.0);
      GL11.glTexCoord2d(0.0, 0.0);
      GL11.glVertex2d(0.0, (double)scaledHeight);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d((double)scaledWidth, (double)scaledHeight);
      GL11.glTexCoord2d(1.0, 1.0);
      GL11.glVertex2d((double)scaledWidth, 0.0);
      GL11.glEnd();
      GL20.glUseProgram(0);
   }

   public void renderShader() {
      ScaledResolution sr = new ScaledResolution(this.mc);
      this.renderFirst();
      this.addDefaultUniforms();
      this.renderSecond(sr.getScaledWidth(), sr.getScaledHeight());
   }

   public void renderShader(int width, int height) {
      this.renderFirst();
      this.addDefaultUniforms();
      this.renderSecond(width, height);
   }

   public void bindShader() {
      GL20.glUseProgram(this.program);
   }

   public void addDefaultUniforms() {
      GL20.glUniform2f(GL20.glGetUniformLocation(this.program, "resolution"), (float)this.mc.displayWidth, (float)this.mc.displayHeight);
      float time = (float)(System.currentTimeMillis() - this.startTime) / 1000.0F;
      GL20.glUniform1f(GL20.glGetUniformLocation(this.program, "time"), time);
   }
}
