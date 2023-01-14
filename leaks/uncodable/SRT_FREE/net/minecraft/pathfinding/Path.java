package net.minecraft.pathfinding;

public class Path {
   private PathPoint[] pathPoints = new PathPoint[1024];
   private int count;

   public void addPoint(PathPoint point) {
      if (point.index < 0) {
         if (this.count == this.pathPoints.length) {
            PathPoint[] apathpoint = new PathPoint[this.count << 1];
            System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
            this.pathPoints = apathpoint;
         }

         this.pathPoints[this.count] = point;
         point.index = this.count;
         this.sortBack(this.count++);
      }
   }

   public void clearPath() {
      this.count = 0;
   }

   public PathPoint dequeue() {
      PathPoint pathpoint = this.pathPoints[0];
      this.pathPoints[0] = this.pathPoints[--this.count];
      this.pathPoints[this.count] = null;
      if (this.count > 0) {
         this.sortForward(0);
      }

      pathpoint.index = -1;
      return pathpoint;
   }

   public void changeDistance(PathPoint p_75850_1_, float p_75850_2_) {
      float f = p_75850_1_.distanceToTarget;
      p_75850_1_.distanceToTarget = p_75850_2_;
      if (p_75850_2_ < f) {
         this.sortBack(p_75850_1_.index);
      } else {
         this.sortForward(p_75850_1_.index);
      }
   }

   private void sortBack(int p_75847_1_) {
      PathPoint pathpoint = this.pathPoints[p_75847_1_];
      if (pathpoint != null) {
         int i;
         for(float f = pathpoint.distanceToTarget; p_75847_1_ > 0; p_75847_1_ = i) {
            i = p_75847_1_ - 1 >> 1;
            PathPoint pathpoint1 = this.pathPoints[i];
            if (pathpoint1 == null) {
               return;
            }

            if (f >= pathpoint1.distanceToTarget) {
               break;
            }

            this.pathPoints[p_75847_1_] = pathpoint1;
            pathpoint1.index = p_75847_1_;
         }

         this.pathPoints[p_75847_1_] = pathpoint;
         pathpoint.index = p_75847_1_;
      }
   }

   private void sortForward(int p_75846_1_) {
      PathPoint pathpoint = this.pathPoints[p_75846_1_];
      if (pathpoint != null) {
         float f = pathpoint.distanceToTarget;

         while(true) {
            int i = 1 + (p_75846_1_ << 1);
            int j = i + 1;
            if (i >= this.count) {
               break;
            }

            PathPoint pathpoint1 = this.pathPoints[i];
            float f1 = pathpoint1.distanceToTarget;
            PathPoint pathpoint2;
            float f2;
            if (j >= this.count) {
               pathpoint2 = null;
               f2 = Float.POSITIVE_INFINITY;
            } else {
               pathpoint2 = this.pathPoints[j];
               f2 = pathpoint2.distanceToTarget;
            }

            if (f1 < f2) {
               if (f1 >= f) {
                  break;
               }

               this.pathPoints[p_75846_1_] = pathpoint1;
               pathpoint1.index = p_75846_1_;
               p_75846_1_ = i;
            } else {
               if (f2 >= f) {
                  break;
               }

               this.pathPoints[p_75846_1_] = pathpoint2;

               assert pathpoint2 != null;

               pathpoint2.index = p_75846_1_;
               p_75846_1_ = j;
            }
         }

         this.pathPoints[p_75846_1_] = pathpoint;
         pathpoint.index = p_75846_1_;
      }
   }

   public boolean isPathEmpty() {
      return this.count == 0;
   }
}
