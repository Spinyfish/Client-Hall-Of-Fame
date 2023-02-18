/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.Data
 *  vip.astroline.client.service.event.types.ArrayHelper
 */
package vip.astroline.client.service.event;

import vip.astroline.client.service.event.Data;
import vip.astroline.client.service.event.types.ArrayHelper;

static final class EventManager.1
extends ArrayHelper<Data> {
    final /* synthetic */ Data val$methodData;

    EventManager.1(Data data) {
        this.val$methodData = data;
        this.add(this.val$methodData);
    }
}
