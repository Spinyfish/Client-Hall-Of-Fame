package com.thunderware.utils;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class EntityUtils {


	public static CopyOnWriteArrayList<Entity> distanceSort(CopyOnWriteArrayList<Entity> entityList) {
		entityList.sort(Comparator.comparingDouble(e -> Minecraft.getMinecraft().thePlayer.getDistanceToEntity((e))));
		return entityList;
	}
	
}
