package com.alan.clients.script.api;

import net.minecraft.client.Minecraft;

/**
 * @author Strikeless
 * @since 02.07.2022
 */
public abstract class API {

    // Not using InstanceAccess since we don't want the scripts to get access to the objects in it.
    protected static final Minecraft MC = Minecraft.getMinecraft();
}
