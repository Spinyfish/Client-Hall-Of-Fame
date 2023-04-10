package crispy.util.render.shaders;

import lombok.Getter;

@Getter
public enum ShadersUtil {
    INSTANCE;

    private final String sunShine = "#ifdef GL_ES\n" +
            "precision mediump float;\n" +
            "#endif\n" +
            "\n" +
            "#extension GL_OES_standard_derivatives : enable\n" +
            "\n" +
            "#define NUM_OCTAVES 16\n" +
            "\n" +
            "uniform float time;\n" +
            "uniform vec2 resolution;\n" +
            "\n" +
            "mat3 rotX(float a) {\n" +
            "\tfloat c = cos(a);\n" +
            "\tfloat s = sin(a);\n" +
            "\treturn mat3(\n" +
            "\t\t1, 0, 0,\n" +
            "\t\t0, c, -s,\n" +
            "\t\t0, s, c\n" +
            "\t);\n" +
            "}\n" +
            "mat3 rotY(float a) {\n" +
            "\tfloat c = cos(a);\n" +
            "\tfloat s = sin(a);\n" +
            "\treturn mat3(\n" +
            "\t\tc, 0, -s,\n" +
            "\t\t0, 1, 0,\n" +
            "\t\ts, 0, c\n" +
            "\t);\n" +
            "}\n" +
            "\n" +
            "float random(vec2 pos) {\n" +
            "\treturn fract(sin(dot(pos.xy, vec2(1399.9898, 78.233))) * 43758.5453123);\n" +
            "}\n" +
            "\n" +
            "float noise(vec2 pos) {\n" +
            "\tvec2 i = floor(pos);\n" +
            "\tvec2 f = fract(pos);\n" +
            "\tfloat a = random(i + vec2(0.0, 0.0));\n" +
            "\tfloat b = random(i + vec2(1.0, 0.0));\n" +
            "\tfloat c = random(i + vec2(0.0, 1.0));\n" +
            "\tfloat d = random(i + vec2(1.0, 1.0));\n" +
            "\tvec2 u = f * f * (3.0 - 2.0 * f);\n" +
            "\treturn mix(a, b, u.x) + (c - a) * u.y * (1.0 - u.x) + (d - b) * u.x * u.y;\n" +
            "}\n" +
            "\n" +
            "float fbm(vec2 pos) {\n" +
            "\tfloat v = 0.0;\n" +
            "\tfloat a = 0.5;\n" +
            "\tvec2 shift = vec2(100.0);\n" +
            "\tmat2 rot = mat2(cos(0.5), sin(0.5), -sin(0.5), cos(0.5));\n" +
            "\tfor (int i=0; i<NUM_OCTAVES; i++) {\n" +
            "\t\tv += a * noise(pos);\n" +
            "\t\tpos = rot * pos * 2.0 + shift;\n" +
            "\t\ta *= 0.5;\n" +
            "\t}\n" +
            "\treturn v;\n" +
            "}\n" +
            "\n" +
            "void main(void) {\n" +
            "\tvec2 p = (gl_FragCoord.xy * 2.0 - resolution.xy) / min(resolution.x, resolution.y);\n" +
            "\n" +
            "\tfloat t = 0.0, d;\n" +
            "\n" +
            "\tfloat time2 = 3.0 * time / 2.0;\n" +
            "\n" +
            "\tvec2 q = vec2(0.0);\n" +
            "\tq.x = fbm(p + 0.00 * time2);\n" +
            "\tq.y = fbm(p + vec2(1.0));\n" +
            "\tvec2 r = vec2(0.0);\n" +
            "\tr.x = fbm(p + 1.0 * q + vec2(1.7, 9.2) + 0.15 * time2);\n" +
            "\tr.y = fbm(p + 1.0 * q + vec2(8.3, 2.8) + 0.126 * time2);\n" +
            "\tfloat f = fbm(p + r);\n" +
            "\tvec3 color = mix(\n" +
            "\t\tvec3(0.101961, 0.866667, 0.319608),\n" +
            "\t\tvec3(.666667, 0.598039, 0.366667),\n" +
            "\t\tclamp((f * f) * 4.0, 0.0, 1.0)\n" +
            "\t);\n" +
            "\n" +
            "\tcolor = mix(\n" +
            "\t\tcolor,\n" +
            "\t\tvec3(0.34509803921, 0.06666666666, 0.83137254902),\n" +
            "\t\tclamp(length(q), 0.0, 1.0)\n" +
            "\t);\n" +
            "\n" +
            "\n" +
            "\tcolor = mix(\n" +
            "\t\tcolor,\n" +
            "\t\tvec3(0.1, -0.5, 0.1),\n" +
            "\t\tclamp(length(r.x), 0.0, 1.0)\n" +
            "\t);\n" +
            "\n" +
            "\tcolor = (f *f * f + 0.6 * f * f + 0.5 * f) * color;\n" +
            "\n" +
            "\tgl_FragColor = vec4(color, 1.0);\n" +
            "}";
    private final String passthroughvsh = "#version 120\r\n"
            + "\r\n"
            + "void main(){\r\n"
            + "    gl_Position = gl_Vertex;\r\n"
            + "}";

}