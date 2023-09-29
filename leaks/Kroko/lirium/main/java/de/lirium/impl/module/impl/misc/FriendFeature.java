/*
 * Copyright Felix Hans from Friend coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package de.lirium.impl.module.impl.misc;

import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.impl.module.ModuleFeature;

@ModuleFeature.Info(name = "Friend", category = ModuleFeature.Category.MISCELLANEOUS, description = "Toggle to protect your friends, disable to attack your friends")
public class FriendFeature extends ModuleFeature {
    @Value(name = "Intelligent", visual = true)
    public final CheckBox intelligent = new CheckBox(true);
}