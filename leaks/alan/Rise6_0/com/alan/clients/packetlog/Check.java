package com.alan.clients.packetlog;

import com.alan.clients.Client;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.module.impl.render.ClickGUI;
import com.alan.clients.module.impl.render.Interface;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.value.Value;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.ModeValue;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.atteo.classindex.IndexSubclasses;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan
 * @since 10/19/2021
 */
@Getter
@Setter
@IndexSubclasses
public abstract class Check implements InstanceAccess {
    public abstract boolean run();
}