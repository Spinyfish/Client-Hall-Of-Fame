package org.crystalpvp.anticheat.api.util;

import com.google.common.collect.ImmutableSet;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassUtil {

	private ClassUtil() {
		throw new RuntimeException("Cannot instantiate a utility class.");
	}

	/**
	 * Gets all the classes in a the provided package.
	 *
	 * @param plugin      The plugin who owns the package
	 * @param packageName The package to scan classes in.
	 *
	 * @return The classes in the package packageName.
	 */
	public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
		Collection<Class<?>> classes = new ArrayList<>();

		CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
		URL resource = codeSource.getLocation();
		String relPath = packageName.replace('.', '/');
		String resPath = resource.getPath().replace("%20", " ");
		String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
		JarFile jarFile;

		try {
			jarFile = new JarFile(jarPath);
		} catch (IOException e) {
			throw (new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e));
		}

		Enumeration<JarEntry> entries = jarFile.entries();

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			String className = null;

			if (entryName.endsWith(".class") && entryName.startsWith(relPath) &&
			    entryName.length() > (relPath.length() + "/".length())) {
				className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
			}

			if (className != null) {
				Class<?> clazz = null;

				try {
					clazz = Class.forName(className);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				if (clazz != null) {
					classes.add(clazz);
				}
			}
		}

		try {
			jarFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return (ImmutableSet.copyOf(classes));
	}

}