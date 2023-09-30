package wtf.automn.module.impl.visual;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebView;
import wtf.automn.gui.frame.WebPane;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.module.settings.Setting;
import wtf.automn.module.settings.SettingBoolean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ModuleInfo(name = "videoplayer", displayName = "Video Player", category = Category.VISUAL, keybind = -1)
public class ModuleVideoPlayer extends Module {

    public final SettingBoolean youTube = new SettingBoolean("youtube", false, "YouTube",this,  "Uses Youtube");
    public final SettingBoolean aniCloud = new SettingBoolean("anicloud", true, "AniCloud.io", this, "Uses AniCloud");


    private WebPane webPane;

    @Override
    protected void onEnable() {
        if(youTube.getBoolean()) {
            this.webPane = new WebPane("YouTube", 500, 500, "https://youtube.com/c/LukasHackts");
//            Platform.runLater(() -> {
//                ((WebView)((JFXPanel)this.webPane.getContentPane()).getScene().getRoot()).getEngine().load("");
//            });
        }
        else if(aniCloud.getBoolean()) {
            WebPane webPane = new WebPane("AniCloud.io", 500, 500, "https://anicloud.io");
        }

        super.onEnable();
    }

    @Override
    protected void onDisable() {
        webPane.setVisible(false);
        webPane = null;
        super.onDisable();
    }
}
