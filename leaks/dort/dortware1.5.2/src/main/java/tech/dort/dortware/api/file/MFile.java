package tech.dort.dortware.api.file;

public abstract class MFile {

    private final String name;
    private final String path;

    public MFile(String name) {
        this(name, "");
    }

    public MFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public abstract void load(FileManager fileManager);

    public abstract void save();

    public String getPath() {
        return path;
    }

}
