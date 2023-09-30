package club.pulsive.impl.util.client.changelog;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public class Changelog {
    private ChangelogType changelogType;
    private String change;


    public String getChangelogTypeString(){
        String prefix = null;
        switch(changelogType){
            case ReDone:
                prefix = "*";
                break;
            case Removed:
                prefix = "-";
                break;
            case Addition:
                prefix = "+";
                break;
        }
        return prefix + change;
    }

    public Color getChangelogTypeColor(){
        Color color = Color.RED;
        switch(changelogType){
            case ReDone:
                color = Color.LIGHT_GRAY;
                break;
            case Removed:
                color = Color.RED;
                break;
            case Addition:
                color = Color.GREEN;
                break;
        }
        return color;
    }
}
