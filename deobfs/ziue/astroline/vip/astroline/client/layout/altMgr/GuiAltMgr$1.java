/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.altMgr.kingAlts.KingAlts
 *  vip.astroline.client.layout.altMgr.kingAlts.ProfileJson
 */
package vip.astroline.client.layout.altMgr;

import vip.astroline.client.layout.altMgr.kingAlts.KingAlts;
import vip.astroline.client.layout.altMgr.kingAlts.ProfileJson;

class GuiAltMgr.1
extends Thread {
    GuiAltMgr.1(String x0) {
        super(x0);
    }

    @Override
    public void run() {
        if (KingAlts.API_KEY == null) return;
        if (KingAlts.API_KEY.length() <= 3) return;
        GuiAltMgr.this.apiString = "King Alts Profile Loading...";
        ProfileJson json = KingAlts.getProfile();
        GuiAltMgr.this.apiString = json.getMessage() != null ? "\u00a7cERROR: " + json.getMessage() : "You have generated " + json.getGeneratedToday() + " alt" + (json.getGeneratedToday() > 1 ? "s" : "") + " today.";
    }
}
