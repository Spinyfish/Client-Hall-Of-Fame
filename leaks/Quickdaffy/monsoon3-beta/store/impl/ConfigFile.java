/*
 * Decompiled with CFR 0.152.
 */
package store.impl;

import java.awt.Image;
import store.File;

public class ConfigFile
extends File {
    public ConfigFile(String name, String description, Image image) {
        super(name, description, image);
    }

    public ConfigFile(String name, String description) {
        super(name, description);
    }
}

