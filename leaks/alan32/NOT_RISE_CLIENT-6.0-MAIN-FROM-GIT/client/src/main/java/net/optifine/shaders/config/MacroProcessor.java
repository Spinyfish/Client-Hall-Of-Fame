package net.optifine.shaders.config;

import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MacroProcessor {
    public static InputStream process(final InputStream in, final String path) throws IOException {
        String s = Config.readInputStream(in, "ASCII");
        final String s1 = getMacroHeader(s);

        if (!s1.isEmpty()) {
            s = s1 + s;

            if (Shaders.saveFinalShaders) {
                final String s2 = path.replace(':', '/') + ".pre";
                Shaders.saveShader(s2, s);
            }

            s = process(s);
        }

        if (Shaders.saveFinalShaders) {
            final String s3 = path.replace(':', '/');
            Shaders.saveShader(s3, s);
        }

        final byte[] abyte = s.getBytes(StandardCharsets.US_ASCII);
        final ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte);
        return bytearrayinputstream;
    }

    public static String process(final String strIn) throws IOException {
        final StringReader stringreader = new StringReader(strIn);
        final BufferedReader bufferedreader = new BufferedReader(stringreader);
        final MacroState macrostate = new MacroState();
        final StringBuilder stringbuilder = new StringBuilder();

        while (true) {
            String s = bufferedreader.readLine();

            if (s == null) {
                s = stringbuilder.toString();
                return s;
            }

            if (macrostate.processLine(s) && !MacroState.isMacroLine(s)) {
                stringbuilder.append(s);
                stringbuilder.append("\n");
            }
        }
    }

    private static String getMacroHeader(final String str) throws IOException {
        final StringBuilder stringbuilder = new StringBuilder();
        final List<ShaderOption> list = null;
        List<ShaderMacro> list1 = null;
        final StringReader stringreader = new StringReader(str);
        final BufferedReader bufferedreader = new BufferedReader(stringreader);

        while (true) {
            final String s = bufferedreader.readLine();

            if (s == null) {
                return stringbuilder.toString();
            }

            if (MacroState.isMacroLine(s)) {
                if (stringbuilder.length() == 0) {
                    stringbuilder.append(ShaderMacros.getFixedMacroLines());
                }

                if (list1 == null) {
                    list1 = new ArrayList(Arrays.asList(ShaderMacros.getExtensions()));
                }

                final Iterator iterator = list1.iterator();

                while (iterator.hasNext()) {
                    final ShaderMacro shadermacro = (ShaderMacro) iterator.next();

                    if (s.contains(shadermacro.getName())) {
                        stringbuilder.append(shadermacro.getSourceLine());
                        stringbuilder.append("\n");
                        iterator.remove();
                    }
                }
            }
        }
    }

    private static List<ShaderOption> getMacroOptions() {
        final List<ShaderOption> list = new ArrayList();
        final ShaderOption[] ashaderoption = Shaders.getShaderPackOptions();

        for (int i = 0; i < ashaderoption.length; ++i) {
            final ShaderOption shaderoption = ashaderoption[i];
            final String s = shaderoption.getSourceLine();

            if (s != null && s.startsWith("#")) {
                list.add(shaderoption);
            }
        }

        return list;
    }
}
