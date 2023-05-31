package com.github.drapostolos.typeparser;

import static com.github.drapostolos.typeparser.Util.defaultSplitStrategy;
import static com.github.drapostolos.typeparser.Util.makeNullArgumentErrorMsg;

import java.util.List;

/**
 * Helper class providing helper methods to implementations of {@link SplitStrategy} when splitting
 * a string into sub-strings.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
public final class SplitStrategyHelper extends Helper {

    private static final SplitStrategyHelper IGNORED = null;

    SplitStrategyHelper(TargetType targetType) {
        super(targetType);
    }

    /**
     * Gives access to the default split strategy, which splits
     * the {@code input} String by using a comma (,).
     * <p>
     * 
     * @param input String to split.
     * @return List of substrings of the input string
     * @throws NullPointerException if {@code input} argument is null.
     */
    public List<String> splitWithDefaultSplitStrategy(String input) {
        if (input == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("input"));
        }
        return defaultSplitStrategy().split(input, IGNORED);
    }

}
