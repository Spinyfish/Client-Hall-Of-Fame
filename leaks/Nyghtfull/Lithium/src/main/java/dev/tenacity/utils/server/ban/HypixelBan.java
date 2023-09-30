package dev.tenacity.utils.server.ban;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.text.SimpleDateFormat;

@Getter
@ToString
public class HypixelBan {

    private static final SimpleDateFormat durationFormat = new SimpleDateFormat("D'd' H'h' m'm' s's'");

    @Expose
    @SerializedName("reason")
    private final HypixelBan.Reason reason;

    @Expose
    @SerializedName("unbanDate")
    private final long unbanDate;

    public HypixelBan(HypixelBan.Reason reason, String duration) {
        this.reason = reason;
        if (duration == null) {
            this.unbanDate = 0;
        } else {
            long currentTime = System.currentTimeMillis();
            String[] arr = duration.split(" ");

            long actualDuration = 0;
            for (String s : arr) {
                long time = Long.parseLong(s.substring(0, s.length() - 1));
                if (s.endsWith("d")) {
                    actualDuration += time * 24 * 60 * 60 * 1000;
                }
                if (s.endsWith("h")) {
                    actualDuration += time * 60 * 60 * 1000;
                }
                if (s.endsWith("m")) {
                    actualDuration += time * 60 * 1000;
                }
                if (s.endsWith("s")) {
                    actualDuration += time * 1000;
                }
            }

            this.unbanDate = currentTime + (actualDuration);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Reason {
        @Expose
        @SerializedName("security_alert")
        SECURITY_ALERT("Security alert"),

        @Expose
        @SerializedName("security_alert_processed")
        SECURITY_ALERT_PROCCESSED("Security alert (processed)"),

        @Expose
        @SerializedName("cheating")
        CHEATING("Cheating"),

        @Expose
        @SerializedName("misc")
        MISC("Miscellaneous");

        private final String name;
    }

}
