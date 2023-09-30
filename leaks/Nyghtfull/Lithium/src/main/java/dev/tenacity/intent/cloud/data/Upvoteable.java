package dev.tenacity.intent.cloud.data;

import lombok.Getter;

@Getter
public class Upvoteable {

    private boolean upvoted, downvoted;

    public void upvote() {
        if (upvoted) {
            upvoted = false;
        } else {
            upvoted = true;
            downvoted = false;
        }
    }

    public void downvote() {
        if (downvoted) {
            downvoted = false;
        } else {
            downvoted = true;
            upvoted = false;
        }
    }

    public void unvote() {
        upvoted = false;
        downvoted = false;
    }

    public void forceSet(boolean upvoted) {
        if (upvoted) {
            this.upvoted = true;
            this.downvoted = false;
        } else {
            this.upvoted = false;
            this.downvoted = true;
        }
    }

}
