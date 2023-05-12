package markgg.util;

import net.minecraft.client.Minecraft;
import com.google.common.collect.Lists;
import java.util.List;


public class RoleUtil {

	public static Minecraft mc = Minecraft.getMinecraft();
	public String color;
	public List<String> Dev = Lists.newArrayList("Kellohylly", "Labrencis");

	public RoleUtil() {

	}

	public boolean isDev(String name) {
		return Dev.contains(name);
	}
	
	public String role(String role) {
		if(isDev(mc.thePlayer.getName())) {
			role = "Dev";
		}else {
			role = "User";
		}
		return role;
	}
	
}
