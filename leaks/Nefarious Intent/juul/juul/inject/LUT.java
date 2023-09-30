package juul.inject;

import java.util.HashMap;
import java.util.Map;

public class LUT {
	
	/*public VersionMapping V1_7_10 = new VersionMapping(Version.V1_7_10);
	public VersionMapping V1_8 = new VersionMapping(Version.V1_8);
	public VersionMapping V1_8_8 = new VersionMapping(Version.V1_8_8);
	public VersionMapping V1_8_9 = new VersionMapping(Version.V1_8_9);*/

	public Map<String, ClassWrapper> classCache = new HashMap<String, ClassWrapper>();
	public VersionMapping current;
	
	public String getMinecraftClass() {
		return getClassName("net.minecraft.client.Minecraft");
	}
	
	public String getClassName(String name) {
		if(current == null)
			return name;

		if(!classCache.containsKey(name))
			classCache.put(name, current.getClassWrapperByUnobf(name));
		
		return classCache.get(name).obfName;
	}
	
	public String getFieldName(String clazz, String name) {
		if(current == null)
			return name;

		if(!classCache.containsKey(clazz))
			classCache.put(clazz, current.getClassWrapperByUnobf(clazz));
		
		return classCache.get(clazz).getObfuscatedField(name);
	}
	
	public String getMethodName(String clazz, String name) {
		if(current == null)
			return name;

		if(!classCache.containsKey(clazz))
			classCache.put(clazz, current.getClassWrapperByUnobf(clazz));
		
		return classCache.get(clazz).getObfuscatedMethod(name);
	}
	
	public enum Version {
		
		DEV("DEV"),
		V1_7_10("1.7.10"),
		V1_8("1.8"),
		V1_8_8("1.8.8"),
		V1_8_9("1.8.9");
		
		public String name;
		
		Version(String name) {
			this.name = name;
		}
	}
	
}
