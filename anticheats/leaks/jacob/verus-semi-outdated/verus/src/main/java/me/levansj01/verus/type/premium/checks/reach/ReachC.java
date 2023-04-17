package me.levansj01.verus.type.premium.checks.reach;

import java.util.List;
import me.levansj01.verus.check.ReachCheck;
import me.levansj01.verus.check.annotation.CheckInfo;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.data.reach.DistanceData;
import me.levansj01.verus.data.reach.ReachBase;
import me.levansj01.verus.util.java.MathUtil;

@CheckInfo(
        type = CheckType.REACH,
        subType = "C",
        friendlyName = "Reach",
        version = CheckVersion.RELEASE,
        minViolations = -1.5D,
        maxViolations = 15
)
public class ReachC extends ReachCheck {
    public void handle(List<ReachBase> reachBaseList, long l) {
        ReachBase reachBase = (ReachBase)MathUtil.max(reachBaseList, ReachBase::getUncertaintyReachValue);
        if (reachBase != null) {
            DistanceData distanceData = reachBase.getDistanceData();
            double horizontal = distanceData.getHorizontal();
            double extra = distanceData.getExtra();
            if (reachBase.getUncertaintyReachValue() > 3.0D && horizontal < 6.0D && extra < 5.0D) {
                this.handleViolation(String.format("R: %.2f H: %.2f E: %.2f", distanceData.getReach(), horizontal, extra), Math.max(Math.ceil(horizontal - 3.0D), 1.0D));
            } else {
                this.decreaseVL(0.035D);
            }
        }
    }
}
