package dev.tenacity.intent.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.tenacity.Tenacity;
import dev.tenacity.intent.cloud.data.CloudConfig;
import dev.tenacity.intent.cloud.data.CloudScript;
import dev.tenacity.intent.cloud.data.Votes;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.ui.sidegui.utils.CloudDataUtils;
import dev.tenacity.utils.client.ReleaseType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class CloudDataManager {

    // Configs and Scripts
    private final List<CloudConfig> cloudConfigs = new ArrayList<>();
    private final List<CloudScript> cloudScripts = new ArrayList<>();
    private boolean refreshing = false;

    // Votes
    private final Map<String, Votes> voteMap = new HashMap<>();
    private final Map<String, Boolean> userVoteMap = new HashMap<>();
    private boolean refreshedVotes = false;

    public void refreshData() {
        refreshing = false;
    }

    public void refreshVotes() {
        refreshedVotes = true;
    }

    public void applyVotes() {
        if (refreshedVotes) {
            refreshedVotes = false;
        }
    }

}
