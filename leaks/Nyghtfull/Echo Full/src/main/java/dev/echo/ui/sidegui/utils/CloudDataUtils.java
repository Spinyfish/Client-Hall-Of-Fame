package dev.echo.ui.sidegui.utils;

import dev.echo.Echo;
import dev.echo.ui.sidegui.panels.configpanel.ConfigPanel;
import dev.echo.utils.misc.MathUtils;

public class CloudDataUtils {


    public static String getLastEditedTime(String epoch) {
        long epochTime = Long.parseLong(epoch);

        //Epoch is in seconds so we convert the System time to seconds
        long timeSince = (System.currentTimeMillis() / 1000) - epochTime;

        //Now we see if the config was uploaded less than a minute ago
        if (timeSince < 60) {
            return "Just now";
        }

        //Now we convert it to minutes
        timeSince = (long) MathUtils.round(timeSince / 60f, 0);

        //If the config was uploaded less than an hour ago
        if (timeSince < 60) {
            return timeSince + ((timeSince > 1) ? " minutes " : " minute ") + "ago";
        }

        //Convert it to hours
        timeSince = (long) MathUtils.round(timeSince / 60f, 0);

        if (timeSince < 24) {
            return timeSince + ((timeSince > 1) ? " hours " : " hour ") + "ago";
        }

        timeSince = (long) MathUtils.round(timeSince / 24f, 0);

        return timeSince + ((timeSince > 1) ? " days " : " day ") + "ago";
    }


    public static void refreshCloud() {
        if (Echo.INSTANCE.getSideGui().getPanels() != null) {
            ConfigPanel configPanel = (ConfigPanel) Echo.INSTANCE.getSideGui().getPanels().get("Configs");
            configPanel.refresh();
        }
    }

}
