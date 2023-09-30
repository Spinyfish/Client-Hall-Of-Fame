/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders;

import java.util.ArrayDeque;
import java.util.Deque;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;

public class ProgramStack {
    private Deque<Program> stack = new ArrayDeque<Program>();

    public void push(Program p) {
        this.stack.addLast(p);
    }

    public Program pop() {
        if (this.stack.isEmpty()) {
            return Shaders.ProgramNone;
        }
        Program program = this.stack.pollLast();
        return program;
    }
}

