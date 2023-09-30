package dev.tenacity.intent.cloud.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoteType {

    UP("upvote"),
    DOWN("downvote"),
    UNVOTE("unvote");

    private final String actionName;

}
