/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.viaversion.viabackwards.api.ViaBackwardsPlatform
 */
package viamcp.loader;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import java.io.File;
import java.util.logging.Logger;
import viamcp.ViaMCP;

public class MCPBackwardsLoader
implements ViaBackwardsPlatform {
    private final File file;

    public MCPBackwardsLoader(File file) {
        this.file = new File(file, "ViaBackwards");
        this.init(this.file);
    }

    public Logger getLogger() {
        return ViaMCP.getInstance().getjLogger();
    }

    public void disable() {
    }

    public boolean isOutdated() {
        return false;
    }

    public File getDataFolder() {
        return new File(this.file, "config.yml");
    }
}

