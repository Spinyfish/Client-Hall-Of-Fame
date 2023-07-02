package me.eldodebug.soar.utils.server;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.eldodebug.soar.utils.RandomUtils;
import net.hypixel.api.HypixelAPI;
import net.minecraft.util.EnumChatFormatting;

public class HypixelUtils {

	private static final Map<UUID, String> levelCache = new HashMap<>();

	private static HypixelAPI api;
	
	private static String apiKey;
	
	public static String getHypixelLevel(boolean isMainPlayer, String name, UUID id) {
		
		if((name.contains(EnumChatFormatting.OBFUSCATED.toString()) && !isMainPlayer)) {
			return null;
		}

		if(levelCache.containsKey(id)) {
			String result = levelCache.get(id);
			if(result.isEmpty()) {
				return null;
			}
			return result;
		}else if(api != null) {
			levelCache.put(id, "");
			api.getPlayerByUuid(id).whenCompleteAsync((response, error) -> {
				if(!response.isSuccess() || error != null) {
					return;
				}
				
				if(response.getPlayer().exists()) {
					levelCache.put(id, Integer.toString((int) response.getPlayer().getNetworkLevel()));
				}
				else {
					levelCache.put(id, Integer.toString(RandomUtils.randomInt(180, 280)));
				}
			});
		}
		return null;
	}
	
	public static String getApiKey() {
		return HypixelUtils.apiKey;
	}
	
	public static void setApiKey(String apiKey) {
		HypixelUtils.apiKey = apiKey;
		if(apiKey != null) {
			api = new HypixelAPI(new ApacheHttpClient(UUID.fromString(apiKey)));
		}
	}
}
