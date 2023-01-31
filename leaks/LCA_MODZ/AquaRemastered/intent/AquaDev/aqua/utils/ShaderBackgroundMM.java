package intent.AquaDev.aqua.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderBackgroundMM {
   public static final String VERTEX_SHADER = "#version 130\n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}";
   private final Minecraft mc = Minecraft.getMinecraft();
   private final int program = GL20.glCreateProgram();
   private final long startTime = System.currentTimeMillis();
   private final String fragment;

   public ShaderBackgroundMM() {
      this(
         "#extension GL_OES_standard_derivatives : enable\n\nprecision highp float;\n\nuniform float time;\nuniform vec2 mouse;\nuniform vec2 resolution;\n\nfloat hash12(vec2 p)\n{\n\tvec3 p3  = fract(vec3(p.xyx) * .1031);\n    p3 += dot(p3, p3.yzx + 33.33);\n    return fract((p3.x + p3.y) * p3.z);\n}\n\n//----------------------------------------------------------------------------------------\n//  2 out, 1 in...\nvec2 hash21(float p)\n{\n\tvec3 p3 = fract(vec3(p) * vec3(.1031, .1030, .0973));\n\tp3 += dot(p3, p3.yzx + 33.33);\n    return fract((p3.xx+p3.yz)*p3.zy);\n\n}\nfloat noise(vec2 p)\n{\n    vec2 ip = floor(p), fp = fract(p);\n    fp = smoothstep(0.,1.,fp);\n    return mix(\n               mix(hash12(ip+vec2(0,0)), hash12(ip+vec2(1,0)), fp.x),\n               mix(hash12(ip+vec2(0,1)), hash12(ip+vec2(1,1)), fp.x),\n               fp.y);\n}\n\nfloat fbm(vec2 p, int lv)\n{\n    float a = 1.0;\n    float t = 0.0;\n    for( int i=0; i<8; i++ )\n    {\n        p += vec2(13.102,1.535);\n        t += a*noise(p);\n        p *= mat2(3,4,-4,3) * 0.4;\n        a *= 0.5;\n    }\n    return 0.5*t;\n}\n\nvoid main( )\n{\n    vec2 uv = (2.*gl_FragCoord.xy-resolution.xy)/resolution.y;\n    \n    float mtHeight = fbm(uv.xx+0.6, 8);\n    float mtHeightSm = fbm(uv.xx+0.6, 3);\n    vec3 col = vec3(0);\n    vec2 sunPos = vec2(0.8,-0.);\n    vec3 skyCol = vec3(0.075,0.310,0.518);\n    float q = uv.y-sunPos.y;\n    float q2 = uv.x-sunPos.x;\n    skyCol = mix(skyCol, vec3(0.482,0.580,0.902), exp(-0.5*q*q-0.2*q2*q2));\n    vec3 cloudCol = mix(skyCol, vec3(0.5), 0.2);\n    skyCol = mix(skyCol, vec3(0.706,0.851,0.953), exp(-q*q*3.-0.5*q2*q2));\n    skyCol = mix(skyCol, vec3(0.980,0.5,0.3), exp(-q*q*10.-0.5*q2*q2));\n    skyCol = mix(skyCol, vec3(1.0,1.0,0.7), exp(-3.*length(uv-sunPos)));\n    vec3 cloudCol2 = mix(skyCol, vec3(0.5), 0.2);\n    vec3 cloudCol3 = mix(vec3(0.980,0.5,0.3),vec3(1.0,1.0,0.7), exp(-length(uv-sunPos)));\n    cloudCol3 = mix(cloudCol3, cloudCol, smoothstep(0.,-1.,uv.y+0.3*uv.x));\n    \n    float w = 1.5*length(fwidth(uv));\n    float isSky = smoothstep(0.,w,uv.y+0.3*uv.x+0.2*max(uv.x,0.)+1.-mtHeight);\n   \n\tcol = mix(col, skyCol, isSky);\n    col = mix(col, skyCol, 0.5*smoothstep(-0.5,0.1,uv.y+0.3*uv.x+0.2*max(uv.x,0.)+1.-mtHeightSm));\n    \n    vec2 fuv = fract(0.1*uv);\n    vec2 uvv = 20.*fuv*(1.-fuv)*(0.5-fuv);\n    uvv = vec2(1,-1)*uvv.yx;\n    vec2 uv2 = uv + uvv*cos(0.4*time);// twisting\n    \n    float silver = fbm(30.*uv - 0.06*time, 8) + 30.*(uv.y + 0.8);\n    silver = smoothstep(0.,1.,silver)*smoothstep(2.,1.,silver) * 1./(1.+500.*q2*q2) * isSky;\n    col += silver * vec3(0.9,0.6,0.3) * 100.;\n    \n    \n    float lowClouds = fbm(5.*uv + 0.1*time, 8);\n    float midClouds = fbm(3.*uv + vec2(0.06,-0.03)*time, 8);\n    float hiClouds = fbm(uv2 + vec2(0.1,0.01)*time, 8) - 0.5;\n    float hiClouds2 = fbm(uv + 10. + vec2(0.062,-0.03)*time, 8) - 0.5;\n    col = mix(col, cloudCol3, 0.5*smoothstep(0.,1., -uv.y+3.*hiClouds));\n    col = mix(col, cloudCol3, 0.5*smoothstep(0.,1., -uv.y+3.*hiClouds2));\n    col = mix(col, vec3(0.9,0.6,0.3) * 100., 1./(1.+2000.*(q*q+q2*q2)));\n    col += vec3(0.9,0.6,0.3) * 2./(1.+10.*sqrt(q*q+0.3*q2*q2));\n    col = mix(col, 0.8*cloudCol, 0.8*smoothstep(0.,1., -2.*(uv.y+0.5)+midClouds));\n    col = mix(col, 0.5*cloudCol, smoothstep(0.,1., -3.*(uv.y+0.8)+lowClouds));\n    \n    \n    col = mix(col, col*pow(col/(col.r+col.g+col.b), vec3(dot(uv,uv)*0.3)), 0.2); // vignette\n    col = pow(col, vec3(2.2));\n    col = (col*(2.51*col+0.03))/(col*(2.43*col+0.59)+0.14); // tonemapping\n    //col = pow(col, vec3(1./2.2));\n    \n    col += 0.01 * (hash12(gl_FragCoord.xy)-0.5) * sqrt(resolution.y/400.);\n\n    // Output to screen\n    gl_FragColor = vec4(col,1.0);\n}"
      );
   }

   public ShaderBackgroundMM(String fragment) {
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
