package club.pulsive.impl.util.render.animations;

import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.apache.ApacheMath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class Animation {

    private final TimerUtil timerUtil = new TimerUtil();
    public int duration;
    public double endPoint;
    public Direction direction;

    public Animation(int ms, double endPoint) {
        this.duration = ms;
        this.endPoint = endPoint;
        this.direction = Direction.FORWARDS;
    }

    public boolean finished(Direction direction) {
        return isDone() && this.direction.equals(direction);
    }

    public double getLinearOutput() {
        return 1 - ((timerUtil.elapsed() / (double) duration) * endPoint);
    }

    public double getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(double endPoint) {
        this.endPoint = endPoint;
    }

    public void reset() {
        timerUtil.reset();
    }

    public boolean isDone() {
        return timerUtil.hasElapsed(duration);
    }

    public void changeDirection() {
        setDirection(direction.setOppositeDirection());
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            timerUtil.setCurrentMS(System.currentTimeMillis() - (duration - ApacheMath.min(duration, timerUtil.elapsed())));
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    protected boolean correctOutput() {
        return false;
    }

    public double getOutput() {
        if (direction == Direction.FORWARDS) {
            if (isDone())
                return endPoint;
            return (getEquation(timerUtil.elapsed()) * endPoint);
        } else {
            if (isDone()) return 0;
            if (correctOutput()) {
                double revTime = ApacheMath.min(duration, ApacheMath.max(0, duration - timerUtil.elapsed()));
                return getEquation(revTime) * endPoint;
            } else return (1 - getEquation(timerUtil.elapsed())) * endPoint;
        }
    }


    //This is where the animation equation should go, for example, a logistic function. Output should range from 0 - 1.
    //This will take the timer's time as an input, x.
    protected abstract double getEquation(double x);

}
