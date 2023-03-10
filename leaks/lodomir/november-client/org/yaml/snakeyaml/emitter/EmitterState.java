/*
 * Decompiled with CFR 0.152.
 */
package org.yaml.snakeyaml.emitter;

import java.io.IOException;

interface EmitterState {
    public void expect() throws IOException;
}

