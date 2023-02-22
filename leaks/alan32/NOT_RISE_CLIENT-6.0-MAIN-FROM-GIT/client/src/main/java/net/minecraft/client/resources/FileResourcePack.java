package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileResourcePack extends AbstractResourcePack implements Closeable {
    public static final Splitter entryNameSplitter = Splitter.on('/').omitEmptyStrings().limit(3);
    private ZipFile resourcePackZipFile;

    public FileResourcePack(final File resourcePackFileIn) {
        super(resourcePackFileIn);
    }

    private ZipFile getResourcePackZipFile() throws IOException {
        if (this.resourcePackZipFile == null) {
            this.resourcePackZipFile = new ZipFile(this.resourcePackFile);
        }

        return this.resourcePackZipFile;
    }

    protected InputStream getInputStreamByName(final String name) throws IOException {
        final ZipFile zipfile = this.getResourcePackZipFile();
        final ZipEntry zipentry = zipfile.getEntry(name);

        if (zipentry == null) {
            throw new ResourcePackFileNotFoundException(this.resourcePackFile, name);
        } else {
            return zipfile.getInputStream(zipentry);
        }
    }

    public boolean hasResourceName(final String name) {
        try {
            return this.getResourcePackZipFile().getEntry(name) != null;
        } catch (final IOException var3) {
            return false;
        }
    }

    public Set<String> getResourceDomains() {
        final ZipFile zipfile;

        try {
            zipfile = this.getResourcePackZipFile();
        } catch (final IOException var8) {
            return Collections.emptySet();
        }

        final Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
        final Set<String> set = Sets.newHashSet();

        while (enumeration.hasMoreElements()) {
            final ZipEntry zipentry = enumeration.nextElement();
            final String s = zipentry.getName();

            if (s.startsWith("assets/")) {
                final List<String> list = Lists.newArrayList(entryNameSplitter.split(s));

                if (list.size() > 1) {
                    final String s1 = list.get(1);

                    if (!s1.equals(s1.toLowerCase())) {
                        this.logNameNotLowercase(s1);
                    } else {
                        set.add(s1);
                    }
                }
            }
        }

        return set;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public void close() throws IOException {
        if (this.resourcePackZipFile != null) {
            this.resourcePackZipFile.close();
            this.resourcePackZipFile = null;
        }
    }
}
