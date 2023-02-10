/**
 * @project Myth
 * @author CodeMan
 * @at 24.08.22, 18:01
 */
package dev.myth.api.utils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import dev.myth.api.interfaces.IMethods;
import lombok.experimental.UtilityClass;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class ScoreboardUtil implements IMethods {

    public Scoreboard getScoreboard() {
        return MC.theWorld.getScoreboard();
    }

    public String getScoreboardTitle() {
        if(getScoreboard() != null && getScoreboard().getObjectiveInDisplaySlot(1) != null) {
            return getScoreboard().getObjectiveInDisplaySlot(1).getDisplayName();
        }
        return "";
    }

    public Collection<Score> getScoreCollection() {
        ScoreObjective objective = getScoreboard().getObjectiveInDisplaySlot(1);
        Collection<Score> collection = getScoreboard().getSortedScores(objective);
        List<Score> list = collection.stream().filter(p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")).collect(Collectors.toList());

        if (list.size() > 15) {
            collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            collection = list;
        }

        return collection;
    }

}
