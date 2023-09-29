import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ShaderBackgroundApi3 {

    public static final String VERTEX_SHADER = "#version 130\n\nvoid main() {\n    gl_TexCoord[0] = gl_MultiTexCoord0;\n    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n}";
    private Minecraft mc = Minecraft.getMinecraft();
    private int program;
    private long startTime;
    private String fragment;

    public ShaderBackgroundApi3() {
//		this("#ifdef GL_ES\r\nprecision mediump float;\r\n#endif\r\n\r\n#extension GL_OES_standard_derivatives : enable\r\n\r\nuniform float time;\r\nuniform vec2 resolution;\r\n\r\n// \r\nfloat rand(float n)\r\n{\r\n\tfloat fl = floor(n);\r\n\tfloat fc = fract(n);\r\n\treturn mix(fract(sin(fl)), fract(sin(fl + 1.0)), fc);\r\n}\r\n\r\n// \r\nvec2 rand2(in vec2 p)\r\n{\r\n\treturn fract(\r\n\t\tvec2(\r\n\t\t\tsin(p.x * 1.32 + p.y * 54.077),\r\n\t\t\tcos(p.x * 91.32 + p.y * 9.077)\r\n\t\t)\r\n\t);\r\n}\r\n\r\n// iq\r\n// https://www.iquilezles.org/www/articles/voronoilines/voronoilines.htm\r\nfloat voronoi(in vec2 v, in float e)\r\n{\r\n\tvec2 p = floor(v);\r\n\tvec2 f = fract(v);\r\n\t\r\n\tvec2 res = vec2(8.0);\r\n\t\r\n\tfor(int j = -1; j <= 1; ++j)\r\n\t\tfor(int i = -1; i <= 1; ++i)\r\n\t\t{\r\n\t\t\tvec2 b = vec2(i, j);\r\n\t\t\tvec2 r = b - f + rand2(p + b);\r\n\t\t\t\r\n\t\t\t// \r\n\t\t\tfloat d = max(abs(r.x), abs(r.y));\r\n\t\t\t\r\n\t\t\tif(d < res.x)\r\n\t\t\t{\r\n\t\t\t\tres.y = res.x;\r\n\t\t\t\tres.x = d;\r\n\t\t\t}\r\n\t\t\t\r\n\t\t\telse if(d < res.y)\r\n\t\t\t{\r\n\t\t\t\tres.y = d;\r\n\t\t\t}\r\n\t\t}\r\n\t\r\n\tvec2 c = sqrt(res);\r\n\tfloat dist = c.y - c.x;\r\n\t\r\n\t// \r\n\treturn 1.0 - smoothstep(0.0, e, dist);\r\n}\r\n\r\n// \r\nmat2 rotate(in float a)\r\n{\r\n\treturn mat2(cos(a), -sin(a), sin(a), cos(a));\r\n}\r\n\r\nvoid main(void)\r\n{\r\n\t// \r\n\tvec2 uv =  gl_FragCoord.xy / resolution * 4.0 - 23.0;\r\n\tuv.y *= resolution.y / resolution.x;\r\n\tuv *= rotate(0.3);\r\n\t\r\n\t// \r\n\tfloat value = 0.0;     \r\n\tfloat light = 0.0;\r\n\t\r\n\tfloat f = 1.0;    // UV\r\n\tfloat a = 0.7;    // value\r\n\t\r\n\t\r\n\tfor(int i = 0; i < 3; ++i)\r\n\t{\r\n\t\t// \r\n\t\tfloat v1 = voronoi(uv * f + 1.0 + time * 0.2 , 0.1);\r\n\t\tv1 = pow(v1, 2.0);\r\n\t\tvalue += a * rand(v1 * 5.5 + 0.1);\r\n\t\t\r\n\t\t// \r\n\t\tfloat v2 = voronoi(uv * f * 1.5 + 5.0 + time, 0.2) * 1.1;\r\n\t\tv2 = pow(v2, 5.0);\r\n\t\tlight += pow(v1 * (0.5 * v2), 1.5);\r\n\t\t\r\n\t\t// \r\n\t\tf *= 2.0;\r\n\t\ta *= 0.6;\r\n\t}\r\n\t\r\n\t// \r\n\tvec3 color;\r\n\tcolor += vec3(0.8, 0.0, 0.0) * value;\r\n\tcolor += vec3(0.4, 0.0, 0.0) * light;\r\n\t\r\n\t// \r\n\tgl_FragColor = vec4(color, 1.0);\r\n}");
        this("#extension GL_OES_standard_derivatives : disable\r\n"
        		+ "\r\n"
        		+ "precision highp float;\r\n"
        		+ "\r\n"
        		+ "uniform float time;\r\n"
        		+ "uniform vec2 mouse;\r\n"
        		+ "uniform vec2 resolution;\r\n"
        		+ "\r\n"
        		+ "float distLine(vec2 p,vec2 a,vec2 b){\r\n"
        		+ "	vec2 ap = p - a;\r\n"
        		+ "	vec2 ab = b - a;\r\n"
        		+ "	float t = clamp(dot(ap,ab)/dot(ab,ab),0.,1.);\r\n"
        		+ "	return length(ap-ab*t);\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "float Line(vec2 p,vec2 a,vec2 b){\r\n"
        		+ "	float d = distLine(p,a,b);\r\n"
        		+ "	d = smoothstep(.02,.01,d);\r\n"
        		+ "	float d2 = length(a-b);\r\n"
        		+ "	return d*smoothstep(1.21,.6,d2);\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "float N21(vec2 p){\r\n"
        		+ "	p = fract(p*vec2(234.334,125.64));\r\n"
        		+ "	p+=dot(p,p+25.34); \r\n"
        		+ "	return fract(p.x*p.y);\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "vec2 N22(vec2 p){\r\n"
        		+ "	float n = N21(p);\r\n"
        		+ "	return vec2(n,N21(p+n));\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "vec2 getPos(vec2 id,vec2 offs){\r\n"
        		+ "	vec2 n = N22(id+offs)*time;\r\n"
        		+ "	\r\n"
        		+ "	return offs+sin(n)*.9;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "float layer(vec2 uv){\r\n"
        		+ "	vec2 id = floor(uv);\r\n"
        		+ "	vec2 fd = fract(uv)-.5;\r\n"
        		+ "	vec2 p[9];\r\n"
        		+ "	p[0] = getPos(id,vec2(-1,-1));\r\n"
        		+ "	p[1] = getPos(id,vec2(0,-1));\r\n"
        		+ "	p[2] = getPos(id,vec2(1,-1));\r\n"
        		+ "	p[3] = getPos(id,vec2(-1,0));\r\n"
        		+ "	p[4] = getPos(id,vec2(0,0));\r\n"
        		+ "	p[5] = getPos(id,vec2(1,0));\r\n"
        		+ "	p[6] = getPos(id,vec2(-1,1));\r\n"
        		+ "	p[7] = getPos(id,vec2(0,1));\r\n"
        		+ "	p[8] = getPos(id,vec2(1,1));\r\n"
        		+ "	float m = 0.;\r\n"
        		+ "	for(int i=0;i<9;i++){\r\n"
        		+ "		m+=Line(fd,p[4],p[i]);\r\n"
        		+ "		vec2 j = (p[i] - fd)*15.;\r\n"
        		+ "		float sparkle = 1./dot(j,j);\r\n"
        		+ "		m += sparkle*(sin(time*10.+fract(p[i].x)*10.)*.5+.5);\r\n"
        		+ "	}\r\n"
        		+ "	m+=Line(fd,p[1],p[3]);\r\n"
        		+ "	m+=Line(fd,p[1],p[5]);\r\n"
        		+ "	m+=Line(fd,p[7],p[3]);\r\n"
        		+ "	m+=Line(fd,p[7],p[5]);\r\n"
        		+ "	return m;\r\n"
        		+ "}\r\n"
        		+ "\r\n"
        		+ "void main( void ) {\r\n"
        		+ "\r\n"
        		+ "	vec2 uv = ( gl_FragCoord.xy/resolution.xy ) -.5;\r\n"
        		+ "	uv.x*=resolution.x/resolution.y;\r\n"
        		+ "	vec3 col = vec3(0.);\r\n"
        		+ "	\r\n"
        		+ "	//uv*=5.;\r\n"
        		+ "	float m = 0.;\r\n"
        		+ "	float t = time*.1;\r\n"
        		+ "	float Y = uv.y;\r\n"
        		+ "	\r\n"
        		+ "	float s = sin(t);\r\n"
        		+ "	float c = cos(t);\r\n"
        		+ "	mat2 rot = mat2(c,-s,s,c);\r\n"
        		+ "	uv*=rot;\r\n"
        		+ "	for(float i=0.;i<1.;i+=1./4.){\r\n"
        		+ "		float z = fract(i+t);\r\n"
        		+ "		float size = mix(30.,.5,z);\r\n"
        		+ "		float fade = smoothstep(0.,.6,z)*smoothstep(1.,.8,z);\r\n"
        		+ "		m += layer(uv*size+i*20.)*fade;\r\n"
        		+ "	}\r\n"
        		+ "	vec3 base = sin(t*5.*vec3(.345,.456,.568))*.4+.6;\r\n"
        		+ "	col = m*base;\r\n"
        		+ "	col-=Y*base;\r\n"
        		+ "	//col = fd.x>0.48||fd.y>0.48?vec3(1,0,0):col;\r\n"
        		+ "	gl_FragColor = vec4( col, 1.0 );\r\n"
        		+ "\r\n"
        		+ "}");
    }

    public ShaderBackgroundApi3(String fragment) {
        this.program = GL20.glCreateProgram();
        this.startTime = System.currentTimeMillis();
        initShader(fragment);
        this.fragment = fragment;
    }

    public void initShader(String frag) {
        int vertex = GL20.glCreateShader(35633), fragment = GL20.glCreateShader(35632);
        GL20.glShaderSource(vertex, VERTEX_SHADER);
        GL20.glShaderSource(fragment, frag);
        GL20.glValidateProgram(this.program);
        GL20.glCompileShader(vertex);
        GL20.glCompileShader(fragment);
        GL20.glAttachShader(this.program, vertex);
        GL20.glAttachShader(this.program, fragment);
        GL20.glLinkProgram(this.program);
    }

    public void renderFirst() {
        GL11.glClear(16640);
        bindShader();
    }

    public void renderSecond(int scaledWidth, int scaledHeight) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glTexCoord2d(0.0D, 1.0D);
        GL11.glVertex2d(0.0D, 0.0D);
        GL11.glTexCoord2d(0.0D, 0.0D);
        GL11.glVertex2d(0.0D, scaledHeight);
        GL11.glTexCoord2d(1.0D, 0.0D);
        GL11.glVertex2d(scaledWidth, scaledHeight);
        GL11.glTexCoord2d(1.0D, 1.0D);
        GL11.glVertex2d(scaledWidth, 0.0D);
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
        GL20.glUniform2f(GL20.glGetUniformLocation(this.program, "resolution"), this.mc.displayWidth,
                this.mc.displayHeight);
        float time = (float) (System.currentTimeMillis() - this.startTime) / 1000.0F;
        GL20.glUniform1f(GL20.glGetUniformLocation(this.program, "time"), time);
    }

    public String getFragment() {
        return fragment;
    }

}
