package club.pulsive.impl.util.render.animations.impl;


import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;

public class MainAnimations extends Animation {
    
    public MainAnimations(int ms, double endPoint){
        super(ms, endPoint);
    }
    
    public MainAnimations(int ms, double endPoint, Direction direction){
        super(ms, endPoint, direction);
    }

   protected double getEquation(double x){
       double x1 = x / duration;
       return 1 - ((x1 - 1) * (x1 - 1));
   }
}
