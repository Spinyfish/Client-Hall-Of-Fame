package com.alan.clients.notification;

import com.alan.clients.util.interfaces.InstanceAccess;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Notification implements InstanceAccess {

    private final String title, content;
    private final long init = System.currentTimeMillis(), length;

    public abstract void render(int multiplierY);

    public abstract boolean isEnded();
}
