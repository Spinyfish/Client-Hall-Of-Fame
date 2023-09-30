/*
 * Decompiled with CFR 0.152.
 */
package store;

import java.awt.Image;
import java.io.Serializable;

public class File
implements Serializable {
    public String name;
    public String description;
    public int vote = 0;
    public Image image;

    public File(String name, String description, Image image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public File(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getVote() {
        return this.vote;
    }

    public Image getImage() {
        return this.image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}

