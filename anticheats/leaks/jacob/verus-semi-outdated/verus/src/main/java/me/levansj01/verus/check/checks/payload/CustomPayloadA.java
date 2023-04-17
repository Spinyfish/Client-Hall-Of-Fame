package me.levansj01.verus.check.checks.payload;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.api.PacketHandler;
import me.levansj01.verus.compat.packets.VPacketPlayInCustomPayload;

@CheckInfo(type=CheckType.PAYLOAD, subType="A", friendlyName="Hacked Client", version=CheckVersion.RELEASE, logData=true, maxViolations=1)
public class CustomPayloadA
        extends Check
        implements PacketHandler {
    private static final Map<String, String> INVALID_CHANNELS = new ImmutableMap.Builder().put((Object)"LOLIMAHCKER", (Object)"Cracked Vape").put((Object)"CPS_BAN_THIS_NIGGER", (Object)"Cracked Vape").put((Object)"EROUAXWASHERE", (Object)"Cracked Vape").put((Object)"EARWAXWASHERE", (Object)"Cracked Vape").put((Object)"#unbanearwax", (Object)"Cracked Vape").put((Object)"1946203560", (Object)"Vape v3").put((Object)"cock", (Object)"Reach Mod").put((Object)"lmaohax", (Object)"Reach Mod").put((Object)"reach", (Object)"Reach Mod").put((Object)"gg", (Object)"Reach Mod").put((Object)"customGuiOpenBspkrs", (Object)"Bspkrs Client").put((Object)"0SO1Lk2KASxzsd", (Object)"Bspkrs Client").put((Object)"MCnetHandler", (Object)"Misplace").put((Object)"n", (Object)"Misplace").put((Object)"CRYSTAL|KZ1LM9TO", (Object)"CrystalWare").put((Object)"CRYSTAL|6LAKS0TRIES", (Object)"CrystalWare").put((Object)"BLC|M", (Object)"Remix").put((Object)"XDSMKDKFDKSDAKDFkEJF", (Object)"Cracked Moon").build();

    public void handle(VPacketPlayInCustomPayload vPacketPlayInCustomPayload) {
        String string = vPacketPlayInCustomPayload.getChannel();
        if (string.startsWith("CRYSTAL|")) {
            this.handleViolation("CrystalWare");
            this.handleBan(true);
            return;
        }
        String string2 = (String)INVALID_CHANNELS.get((Object)string);
        if (string2 != null) {
            this.handleViolation(string2);
            this.handleBan(true);
        }
    }
}
