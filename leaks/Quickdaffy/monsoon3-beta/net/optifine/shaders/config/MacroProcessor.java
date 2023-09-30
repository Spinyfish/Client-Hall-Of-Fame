/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.src.Config;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.MacroState;
import net.optifine.shaders.config.ShaderMacro;
import net.optifine.shaders.config.ShaderMacros;
import net.optifine.shaders.config.ShaderOption;

public class MacroProcessor {
    public static InputStream process(InputStream in, String path) throws IOException {
        String s = Config.readInputStream(in, "ASCII");
        String s1 = MacroProcessor.getMacroHeader(s);
        if (!s1.isEmpty()) {
            s = s1 + s;
            if (Shaders.saveFinalShaders) {
                String s2 = path.replace(':', '/') + ".pre";
                Shaders.saveShader(s2, s);
            }
            s = MacroProcessor.process(s);
        }
        if (Shaders.saveFinalShaders) {
            String s3 = path.replace(':', '/');
            Shaders.saveShader(s3, s);
        }
        byte[] abyte = s.getBytes("ASCII");
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte);
        return bytearrayinputstream;
    }

    public static String process(String strIn) throws IOException {
        StringReader stringreader = new StringReader(strIn);
        BufferedReader bufferedreader = new BufferedReader(stringreader);
        MacroState macrostate = new MacroState();
        StringBuilder stringbuilder = new StringBuilder();
        while (true) {
            String s;
            if ((s = bufferedreader.readLine()) == null) {
                s = stringbuilder.toString();
                return s;
            }
            if (!macrostate.processLine(s) || MacroState.isMacroLine(s)) continue;
            stringbuilder.append(s);
            stringbuilder.append("\n");
        }
    }

    /*
     * Unable to fully structure code
     */
    private static String getMacroHeader(String str) throws IOException {
        stringbuilder = new StringBuilder();
        list = null;
        list1 = null;
        stringreader = new StringReader(str);
        bufferedreader = new BufferedReader(stringreader);
        block0: while (true) {
            if ((s = bufferedreader.readLine()) == null) {
                return stringbuilder.toString();
            }
            if (!MacroState.isMacroLine(s)) continue;
            if (stringbuilder.length() == 0) {
                stringbuilder.append(ShaderMacros.getFixedMacroLines());
            }
            if (list1 == null) {
                list1 = new ArrayList<ShaderMacro>(Arrays.asList(ShaderMacros.getExtensions()));
            }
            iterator = list1.iterator();
            while (true) {
                if (iterator.hasNext()) ** break;
                continue block0;
                shadermacro = (ShaderMacro)iterator.next();
                if (!s.contains(shadermacro.getName())) continue;
                stringbuilder.append(shadermacro.getSourceLine());
                stringbuilder.append("\n");
                iterator.remove();
            }
            break;
        }
    }

    private static List<ShaderOption> getMacroOptions() {
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        ShaderOption[] ashaderoption = Shaders.getShaderPackOptions();
        for (int i = 0; i < ashaderoption.length; ++i) {
            ShaderOption shaderoption = ashaderoption[i];
            String s = shaderoption.getSourceLine();
            if (s == null || !s.startsWith("#")) continue;
            list.add(shaderoption);
        }
        return list;
    }
}

