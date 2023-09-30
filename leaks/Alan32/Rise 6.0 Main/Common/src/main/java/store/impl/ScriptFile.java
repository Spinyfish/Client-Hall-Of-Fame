package store.impl;


import store.File;

import java.awt.*;

public class ScriptFile extends File {
    public ScriptFile(String name, String description, Image image) {
        super(name, description, image);
    }

    public ScriptFile(String name, String description) {
        super(name, description);
    }
}
