package me.levansj01.verus.check.checks.payload;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.data.client.ClientType;

@CheckInfo(type=CheckType.PAYLOAD, subType="B", friendlyName="Hacked Client", version=CheckVersion.RELEASE, logData=true, maxViolations=1)
public class CustomPayloadB
        extends Check {
    private static final Map<String, String> INVALID_BRANDS = new ImmutableMap.Builder().put((Object)"Vanilla", (Object)"Jigsaw").put((Object)"\u0007Vanilla", (Object)"Jigsaw").put((Object)"Synergy", (Object)"Synergy").put((Object)"\u0007Synergy", (Object)"Synergy").put((Object)"Created By ", (Object)"Vape").put((Object)"\u0007Created By ", (Object)"Vape").build();
    private static final Map<String, ClientType> CLIENT_TYPES = new ImmutableMap.Builder().put((Object)"vanilla", (Object)ClientType.VANILLA).put((Object)"fml,forge", (Object)ClientType.FORGE).put((Object)"LiteLoader", (Object)ClientType.LITE_LOADER).put((Object)"fabric", (Object)ClientType.FABRIC).put((Object)"\u0007vanilla", (Object)ClientType.VANILLA).put((Object)"\u0007fml,forge", (Object)ClientType.FORGE).put((Object)"\u0007LiteLoader", (Object)ClientType.LITE_LOADER).put((Object)"\u0006fabric", (Object)ClientType.FABRIC).build();

    public void handle(String string) {
        String string2 = (String)INVALID_BRANDS.get((Object)string);
        if (string2 != null) {
            this.handleViolation(String.format((String)"T: %s", (Object[])new Object[]{string2}));
            this.handleBan(true);
            return;
        }
        ClientType clientType = (ClientType)CLIENT_TYPES.get((Object)string);
        if (clientType != null) {
            this.playerData.getClientData().setType(clientType);
        } else {
            this.debug(string);
        }
        this.playerData.setBrand(string);
    }
}
