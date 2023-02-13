package juul.inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ClassWrapper {
		public String obfName, name;
		public Map<String, String> unobfToObfFields = new HashMap<String, String>(), unobfToObfMethods = new HashMap<String, String>();
		
		public String getName() {
			return name.replace("/", ".");
		}
		
		public String getObfuscatedName() {
			return obfName.replace("/", ".");
		}
		
		public String getObfuscatedMethod(String name) {
			for(Entry<String, String> entry : unobfToObfMethods.entrySet()) {
				if(entry.getKey().equals(name))
					return entry.getValue();
			}
			
			return null;
		}
		
		public String getObfuscatedField(String name) {
			for(Entry<String, String> entry : unobfToObfFields.entrySet()) {
				if(entry.getKey().equals(name))
					return entry.getValue();
			}
			
			return null;
		}
		
		public ClassWrapper(String obfName, String name) {
			this.obfName = obfName;
			this.name = name;
		}
	}