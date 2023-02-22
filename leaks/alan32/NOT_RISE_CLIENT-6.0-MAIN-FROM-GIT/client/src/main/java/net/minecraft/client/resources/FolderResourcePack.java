package net.minecraft.client.resources;

import com.google.common.collect.Sets;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

import java.io.*;
import java.util.Set;

public class FolderResourcePack extends AbstractResourcePack {
    public FolderResourcePack(final File resourcePackFileIn) {
        super(resourcePackFileIn);
    }

    protected InputStream getInputStreamByName(final String name) throws IOException {
        return new BufferedInputStream(new FileInputStream(new File(this.resourcePackFile, name)));
    }

    protected boolean hasResourceName(final String name) {
        return (new File(this.resourcePackFile, name)).isFile();
    }

    public Set<String> getResourceDomains() {
        final Set<String> set = Sets.newHashSet();
        final File file1 = new File(this.resourcePackFile, "assets/");

        if (file1.isDirectory()) {
            for (final File file2 : file1.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY)) {
                final String s = getRelativeName(file1, file2);

                if (!s.equals(s.toLowerCase())) {
                    this.logNameNotLowercase(s);
                } else {
                    set.add(s.substring(0, s.length() - 1));
                }
            }
        }

        return set;
    }
}
