/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.internal.PlatformDependent;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public final class Version {
    private static final String PROP_VERSION = ".version";
    private static final String PROP_BUILD_DATE = ".buildDate";
    private static final String PROP_COMMIT_DATE = ".commitDate";
    private static final String PROP_SHORT_COMMIT_HASH = ".shortCommitHash";
    private static final String PROP_LONG_COMMIT_HASH = ".longCommitHash";
    private static final String PROP_REPO_STATUS = ".repoStatus";
    private final String artifactId;
    private final String artifactVersion;
    private final long buildTimeMillis;
    private final long commitTimeMillis;
    private final String shortCommitHash;
    private final String longCommitHash;
    private final String repositoryStatus;

    public static Map<String, Version> identify() {
        return Version.identify(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Map<String, Version> identify(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = PlatformDependent.getContextClassLoader();
        }
        Properties props = new Properties();
        try {
            Enumeration<URL> resources = classLoader.getResources("META-INF/io.netty.versions.properties");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                InputStream in = url.openStream();
                try {
                    props.load(in);
                }
                finally {
                    try {
                        in.close();
                    }
                    catch (Exception ignore) {}
                }
            }
        }
        catch (Exception ignore) {
            // empty catch block
        }
        HashSet<String> artifactIds = new HashSet<String>();
        for (Object o : props.keySet()) {
            String artifactId;
            String k = (String)o;
            int dotIndex = k.indexOf(46);
            if (dotIndex <= 0 || !props.containsKey((artifactId = k.substring(0, dotIndex)) + PROP_VERSION) || !props.containsKey(artifactId + PROP_BUILD_DATE) || !props.containsKey(artifactId + PROP_COMMIT_DATE) || !props.containsKey(artifactId + PROP_SHORT_COMMIT_HASH) || !props.containsKey(artifactId + PROP_LONG_COMMIT_HASH) || !props.containsKey(artifactId + PROP_REPO_STATUS)) continue;
            artifactIds.add(artifactId);
        }
        TreeMap<String, Version> versions = new TreeMap<String, Version>();
        for (String artifactId : artifactIds) {
            versions.put(artifactId, new Version(artifactId, props.getProperty(artifactId + PROP_VERSION), Version.parseIso8601(props.getProperty(artifactId + PROP_BUILD_DATE)), Version.parseIso8601(props.getProperty(artifactId + PROP_COMMIT_DATE)), props.getProperty(artifactId + PROP_SHORT_COMMIT_HASH), props.getProperty(artifactId + PROP_LONG_COMMIT_HASH), props.getProperty(artifactId + PROP_REPO_STATUS)));
        }
        return versions;
    }

    private static long parseIso8601(String value) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(value).getTime();
        }
        catch (ParseException ignored) {
            return 0L;
        }
    }

    public static void main(String[] args) {
        for (Version v : Version.identify().values()) {
            System.err.println(v);
        }
    }

    private Version(String artifactId, String artifactVersion, long buildTimeMillis, long commitTimeMillis, String shortCommitHash, String longCommitHash, String repositoryStatus) {
        this.artifactId = artifactId;
        this.artifactVersion = artifactVersion;
        this.buildTimeMillis = buildTimeMillis;
        this.commitTimeMillis = commitTimeMillis;
        this.shortCommitHash = shortCommitHash;
        this.longCommitHash = longCommitHash;
        this.repositoryStatus = repositoryStatus;
    }

    public String artifactId() {
        return this.artifactId;
    }

    public String artifactVersion() {
        return this.artifactVersion;
    }

    public long buildTimeMillis() {
        return this.buildTimeMillis;
    }

    public long commitTimeMillis() {
        return this.commitTimeMillis;
    }

    public String shortCommitHash() {
        return this.shortCommitHash;
    }

    public String longCommitHash() {
        return this.longCommitHash;
    }

    public String repositoryStatus() {
        return this.repositoryStatus;
    }

    public String toString() {
        return this.artifactId + '-' + this.artifactVersion + '.' + this.shortCommitHash + ("clean".equals(this.repositoryStatus) ? "" : " (repository: " + this.repositoryStatus + ')');
    }
}

