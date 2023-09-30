package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.Session;

import java.io.File;
import java.net.Proxy;

public class GameConfiguration {
    public final GameConfiguration.UserInformation userInfo;
    public final GameConfiguration.DisplayInformation displayInfo;
    public final GameConfiguration.FolderInformation folderInfo;
    public final GameConfiguration.GameInformation gameInfo;
    public final GameConfiguration.ServerInformation serverInfo;

    public GameConfiguration(final GameConfiguration.UserInformation userInfoIn, final GameConfiguration.DisplayInformation displayInfoIn, final GameConfiguration.FolderInformation folderInfoIn, final GameConfiguration.GameInformation gameInfoIn, final GameConfiguration.ServerInformation serverInfoIn) {
        this.userInfo = userInfoIn;
        this.displayInfo = displayInfoIn;
        this.folderInfo = folderInfoIn;
        this.gameInfo = gameInfoIn;
        this.serverInfo = serverInfoIn;
    }

    public static class DisplayInformation {
        public final int width;
        public final int height;
        public final boolean fullscreen;
        public final boolean checkGlErrors;

        public DisplayInformation(final int widthIn, final int heightIn, final boolean fullscreenIn, final boolean checkGlErrorsIn) {
            this.width = widthIn;
            this.height = heightIn;
            this.fullscreen = fullscreenIn;
            this.checkGlErrors = checkGlErrorsIn;
        }
    }

    public static class FolderInformation {
        public final File mcDataDir;
        public final File resourcePacksDir;
        public final File assetsDir;
        public final String assetIndex;

        public FolderInformation(final File mcDataDirIn, final File resourcePacksDirIn, final File assetsDirIn, final String assetIndexIn) {
            this.mcDataDir = mcDataDirIn;
            this.resourcePacksDir = resourcePacksDirIn;
            this.assetsDir = assetsDirIn;
            this.assetIndex = assetIndexIn;
        }
    }

    public static class GameInformation {
        public final boolean isDemo;
        public final String version;

        public GameInformation(final boolean isDemoIn, final String versionIn) {
            this.isDemo = isDemoIn;
            this.version = versionIn;
        }
    }

    public static class ServerInformation {
        public final String serverName;
        public final int serverPort;

        public ServerInformation(final String serverNameIn, final int serverPortIn) {
            this.serverName = serverNameIn;
            this.serverPort = serverPortIn;
        }
    }

    public static class UserInformation {
        public final Session session;
        public final PropertyMap userProperties;
        public final PropertyMap field_181172_c;
        public final Proxy proxy;

        public UserInformation(final Session p_i46375_1_, final PropertyMap p_i46375_2_, final PropertyMap p_i46375_3_, final Proxy p_i46375_4_) {
            this.session = p_i46375_1_;
            this.userProperties = p_i46375_2_;
            this.field_181172_c = p_i46375_3_;
            this.proxy = p_i46375_4_;
        }
    }
}
