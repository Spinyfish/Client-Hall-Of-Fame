package tech.dort.dortware.api.property;

/**
 * @author - Aidan#1337
 * @created 9/26/2020 at 11:22 AM
 * Do not distribute this code without credit
 * or ill get final on ur ass
 */

public enum SliderUnit {
    BLOCKS("blocks"),
    MS("ms"),
    CPS("cps"),
    PERCENT("%"),
    BPT("bpt"),
    X("X"),
    Y("Y"),
    TPS("tps");


    final String displayText;

    SliderUnit(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }
}