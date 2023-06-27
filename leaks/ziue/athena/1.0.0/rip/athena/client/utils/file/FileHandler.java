package rip.athena.client.utils.file;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;
import java.util.*;

public class FileHandler
{
    private final String separator;
    private File file;
    private boolean fresh;
    
    public FileHandler(final File file) {
        this.separator = System.getProperty("line.separator");
        this.fresh = false;
        this.file = file;
    }
    
    public void init() throws IOException {
        if (this.file.getParent() != null) {
            final File folder = new File(this.file.getParent());
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        if (!this.file.exists()) {
            this.file.createNewFile();
            this.fresh = true;
        }
    }
    
    public void writeToFile(final String content, final boolean append) throws IOException {
        try (final Writer writer = new BufferedWriter(new FileWriter(this.file, append))) {
            writer.write(content);
        }
    }
    
    public void writeToFile(final byte[] content) throws IOException {
        try (final OutputStream writer = new FileOutputStream(this.file)) {
            writer.write(content);
        }
    }
    
    public byte[] getContentInBytes() throws IOException {
        final byte[] bytes = new byte[(int)this.file.length()];
        try (final FileInputStream reader = new FileInputStream(this.file)) {
            reader.read(bytes);
        }
        return bytes;
    }
    
    public static String readInputStream(final InputStream inputStream) {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
    
    public String getContent(final boolean ignoreComments) throws IOException {
        final StringBuilder builder = new StringBuilder();
        try (final Stream<String> stream = Files.lines(this.file.toPath())) {
            final StringBuilder sb;
            stream.forEach(line -> {
                if (line.startsWith("//") && ignoreComments) {
                    return;
                }
                else {
                    sb.append((line.isEmpty() ? "" : this.separator) + line);
                    return;
                }
            });
        }
        return builder.toString();
    }
    
    public String[] getContentInLines(final boolean ignoreComments) throws IOException {
        final List<String> lines = new ArrayList<String>();
        try (final Stream<String> stream = Files.lines(this.file.toPath())) {
            final List<String> list;
            stream.forEach(line -> {
                if (line.startsWith("//") && ignoreComments) {
                    return;
                }
                else {
                    list.add(line);
                    return;
                }
            });
        }
        return lines.toArray(new String[lines.size()]);
    }
    
    public String[] getLinesByPrefix(final String prefix, final boolean ignoreComments) throws IOException {
        final List<String> lines = new ArrayList<String>(Arrays.asList(this.getContentInLines(ignoreComments)));
        for (int i = 0; i < lines.size(); ++i) {
            final String line = lines.get(i);
            if (!line.startsWith(prefix)) {
                lines.remove(i);
            }
        }
        return lines.toArray(new String[lines.size()]);
    }
    
    public String[] getLinesByNeedle(final String needle, final boolean ignoreComments) throws IOException {
        final List<String> lines = new ArrayList<String>(Arrays.asList(this.getContentInLines(ignoreComments)));
        for (int i = 0; i < lines.size(); ++i) {
            final String line = lines.get(i);
            if (!line.contains(needle)) {
                lines.remove(i);
            }
        }
        return lines.toArray(new String[lines.size()]);
    }
    
    public void removeLinesByPrefix(final String prefix, final boolean ignoreComments) throws IOException {
        final List<String> lines = new ArrayList<String>(Arrays.asList(this.getContentInLines(ignoreComments)));
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < lines.size(); ++i) {
            final String line = lines.get(i);
            if (!line.startsWith(prefix)) {
                output.append(String.valueOf((output.length() == 0) ? "" : this.separator) + line);
            }
        }
        this.writeToFile(output.toString(), false);
    }
    
    public void removeLinesByNeedle(final String needle, final boolean ignoreComments) throws IOException {
        final List<String> lines = new ArrayList<String>(Arrays.asList(this.getContentInLines(ignoreComments)));
        final StringBuilder output = new StringBuilder();
        for (int i = 0; i < lines.size(); ++i) {
            final String line = lines.get(i);
            if (!line.contains(needle)) {
                output.append(String.valueOf((output.length() == 0) ? "" : this.separator) + line);
            }
        }
        this.writeToFile(output.toString(), false);
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public boolean isFresh() {
        return this.fresh;
    }
    
    public void setFresh(final boolean fresh) {
        this.fresh = fresh;
    }
}
