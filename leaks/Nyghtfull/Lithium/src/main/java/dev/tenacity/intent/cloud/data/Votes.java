package dev.tenacity.intent.cloud.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Votes {

    private int upvotes, downvotes;

    public int getTotalVotes() {
        return upvotes - downvotes;
    }

}
