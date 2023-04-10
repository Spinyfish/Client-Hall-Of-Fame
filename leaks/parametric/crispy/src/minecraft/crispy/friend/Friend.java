package crispy.friend;

import lombok.Getter;

@Getter
public class Friend {
    private final String name;

    public Friend(String name) {
        this.name = name;

    }
}