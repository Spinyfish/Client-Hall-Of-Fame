package me.jinthium.scripting.api;


import org.openjdk.nashorn.api.scripting.ClassFilter;

public class ScriptFilter implements ClassFilter {


    @Override
    public boolean exposeToScripts(String s) {
        return s.contains("org.lwjgl.input.Keyboard") ||
                s.contains("org.lwjgl.opengl.GL11") ||
                s.contains("org.lwjgl.opengl.GL13") ||
                s.contains("org.lwjgl.opengl.GL14") ||
                s.contains("org.lwjgl.opengl.GL20") ||
                s.contains("java.util.Map") ||
                s.contains("java.util.HashMap") ||
                s.contains("java.util.List") ||
                s.contains("java.util.ArrayList") ||
                s.contains("java.lang.Math") ||
                s.contains("java.util.stream.Collectors") ||
                s.contains("java.util.UUID") ||
                s.contains("org.lwjgl.input.Mouse") ||
                s.startsWith("me.jinthium.straight.impl.utils.tuples");
    }

}
