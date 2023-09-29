package juul.inject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Resources;

import juul.inject.LUT.Version;

public class VersionMapping {

	public Version version;
	public List<ClassWrapper> classes = new ArrayList<ClassWrapper>();
	
	public VersionMapping(Version version) {
		this.version = version;
		
		String base = "juul/mappings/" + version.name + "/",
			   joined = base + "joined.srg", 
			   fields = base + "fields.csv", 
			   methods = base + "methods.csv";
		
		Map<String, String> mcpToSourceMethods = new HashMap<String, String>();
		Map<String, String> mcpToSourceFields = new HashMap<String, String>();
		
		Map<String, String> notchToMcpMethods = new HashMap<String, String>();
		Map<String, String> notchToMcpFields = new HashMap<String, String>();
		Map<String, String> notchToSourceClasses = new HashMap<String, String>();
		
		try {
			List<String> fieldLines = Resources.readLines(Resources.getResource(fields), StandardCharsets.UTF_8);
			List<String> methodLines = Resources.readLines(Resources.getResource(methods), StandardCharsets.UTF_8);
			List<String> joinedLines = Resources.readLines(Resources.getResource(joined), StandardCharsets.UTF_8);
			
			for(String s : fieldLines) {
				if(s.startsWith("field_")) {
					mcpToSourceFields.put(s.split(",")[0], s.split(",")[1]);
				}
			}
			
			for(String s : methodLines) {
				if(s.startsWith("func_")) {
					mcpToSourceMethods.put(s.split(",")[0], s.split(",")[1]);
				}
			}
	
			for(String s : joinedLines) {
				if(s.startsWith("CL: ")) {
					String clas = s.substring(4);
					classes.add(new ClassWrapper(clas.split(" ")[0], clas.split(" ")[1]));
				}
			}
			
			for(String s : joinedLines) {
				//Method
				if(s.startsWith("MD: ")) {
					String method = s.substring(4);
					String obfVersion = method.split(" ")[0];
					if(obfVersion.contains("net/minecraft"))
						continue;
					
					String obfClass = obfVersion.split("\\/")[0];
					String obfMethod = obfVersion.split("\\/")[1];
					
					String unobfVersion = method.split(" ")[2];
					
					String unobfClass = unobfVersion.split("\\/")[unobfVersion.split("\\/").length-2];
					String unobfMethod = unobfVersion.split("\\/")[unobfVersion.split("\\/").length-1];
					
					ClassWrapper wrapper = getClassWrapperByObf(obfClass);
					/*if(mcpToSourceMethods.get(unobfMethod) == null) {
						System.out.println(obfClass+"."+obfMethod + " -> " + unobfClass+"."+unobfMethod);
					}*/
					wrapper.unobfToObfMethods.put(mcpToSourceMethods.get(unobfMethod) == null ? unobfMethod : mcpToSourceMethods.get(unobfMethod), obfMethod);
					
					//mcpToSourceMethods.put(method.split(" ")[0], s.split(",")[1]);
				}
				//Field
				if(s.startsWith("FD: ")) {
					String field = s.substring(4);
					String obfVersion = field.split(" ")[0];
					if(obfVersion.contains("net/minecraft"))
						continue;
					
					String obfClass = obfVersion.split("\\/")[0];
					String obfField = obfVersion.split("\\/")[1];
					
					String unobfVersion = field.split(" ")[1];
					
					String unobfClass = unobfVersion.split("\\/")[unobfVersion.split("\\/").length-2];
					String unobfField = unobfVersion.split("\\/")[unobfVersion.split("\\/").length-1];
					
					ClassWrapper wrapper = getClassWrapperByObf(obfClass);
					wrapper.unobfToObfFields.put(mcpToSourceFields.get(unobfField) == null ? unobfField : mcpToSourceFields.get(unobfField), obfField);
					
				}
			}
		}catch(Exception e) {
			System.err.println("Failed to load " + version.name + " mappings");
			e.printStackTrace();
		}
	}

	public ClassWrapper getClassWrapperByObf(String name) {
		for(ClassWrapper wrapper : classes) {
			if(wrapper.getObfuscatedName().equals(name)) {
				return wrapper;
			}
		}
		
		return null;
	}
	
	public ClassWrapper getClassWrapperByUnobf(String name) {
		for(ClassWrapper wrapper : classes) {
			if(wrapper.getName().equals(name)) {
				return wrapper;
			}
		}
		
		return null;
	}
	
}
