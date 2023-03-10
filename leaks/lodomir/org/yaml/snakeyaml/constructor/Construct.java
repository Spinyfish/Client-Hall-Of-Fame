/*
 * Decompiled with CFR 0.152.
 */
package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.Node;

public interface Construct {
    public Object construct(Node var1);

    public void construct2ndStep(Node var1, Object var2);
}

