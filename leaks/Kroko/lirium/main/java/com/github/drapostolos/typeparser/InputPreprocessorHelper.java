package com.github.drapostolos.typeparser;

import static com.github.drapostolos.typeparser.Util.makeNullArgumentErrorMsg;

/**
 * Helper class providing helper methods to implementations of {@link InputPreprocessor} when
 * preparing an input string for parsing.
 * 
 * @see <a href="https://github.com/drapostolos/type-parser/wiki/User-Guide"
 *      target="_blank">User-Guide</a>
 */
public final class InputPreprocessorHelper extends Helper {

    final private NullStringStrategy nullStringStrategy;
    final private TargetType targetType;

    InputPreprocessorHelper(TargetType targetType, TypeParser typeParser) {
        super(targetType);
        this.targetType = targetType;
        this.nullStringStrategy = typeParser.nullStringStrategy;
    }

    /**
     * Checks if the given <code>input</code> is considered a <code>NullString</code> or not.
     * Returns true if it is, otherwise returns false.
     * 
     * @param input string to parse.
     * @return true if <code>input</code> is a <code>NullString</code>, otherwise false.
     * @throws NullPointerException if given argument is {@code null}.
     * @see NullStringStrategy
     */
    public boolean isNullString(String input) {
        if (input == null) {
            throw new NullPointerException(makeNullArgumentErrorMsg("input"));
        }
        return nullStringStrategy.isNullString(input, new NullStringStrategyHelper(targetType));
    }
}
