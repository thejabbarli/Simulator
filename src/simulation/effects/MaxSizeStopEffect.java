package simulation.effects;

import processing.core.PVector;
import simulation.core.Ball;

public class MaxSizeStopEffect {
    private float wallRadius;
    private float wallThickness;
    private boolean shouldStop;
    private boolean shouldShrink;
    private float shrinkRate;
    private boolean enforceWallBoundaryLimit;
    private float growthAmount;


    public MaxSizeStopEffect(

            float wallRadius,
            float wallThickness,
            float growthAmount, // <--- Add this
            boolean shouldStop,
            boolean shouldShrink,
            float shrinkRate,
            boolean enforceWallBoundaryLimit


    ) {
        this.wallRadius = wallRadius;
        this.wallThickness = wallThickness;
        this.growthAmount = growthAmount; // <--- Add this
        this.shouldStop = shouldStop;
        this.shouldShrink = shouldShrink;
        this.shrinkRate = shrinkRate;
        this.enforceWallBoundaryLimit = enforceWallBoundaryLimit;
    }


    public void apply(Ball ball, PVector wallCenter) {
        if (!ball.hasJustBounced()) return;

        float current = ball.getEffectiveRadius();
        float future = current + growthAmount;

        float wallInner = wallRadius - wallThickness / 2f;  // correct: center of wall stroke
        System.out.printf("🧪 GROWTH CHECK → current=%.2f, future=%.2f, wallInner=%.2f → ",
                current, future, wallInner);

        if (future >= wallInner) {
            System.out.println("❌ BLOCKED");

            ball.setPosition(wallCenter.copy());

            float adjustedRadius = wallInner - ball.getStrokeThickness() / 2f;
            ball.setRadius(adjustedRadius);

            ball.lockMotion();

            System.out.printf("🔒 Ball locked in center. Final radius = %.2f, Effective = %.2f, Target = %.2f\n",
                    ball.getRadius(), ball.getEffectiveRadius(), wallInner);
        } else {
            System.out.println("✅ ALLOWED");
        }
    }


    public static boolean canGrow(Ball ball, float growAmount, float wallRadius, float wallThickness) {
        float futureOuter = ball.getRadius() + growAmount + ball.getStrokeThickness() / 2f;
        float wallInner = wallRadius - wallThickness / 2f;
        boolean allowed = futureOuter < wallInner;
        System.out.printf("\uD83E\uDDEA GROWTH CHECK \u2192 current=%.2f, future=%.2f, wallInner=%.2f \u2192 %s\n",
                ball.getRadius(), futureOuter, wallInner, allowed ? "\u2705 ALLOWED" : "\u274C BLOCKED");
        return allowed;
    }

    public void setWallRadius(float wallRadius) {
        this.wallRadius = wallRadius;
    }

    public void setWallThickness(float wallThickness) {
        this.wallThickness = wallThickness;
    }

    public void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }

    public void setShouldShrink(boolean shouldShrink) {
        this.shouldShrink = shouldShrink;
    }

    public void setShrinkRate(float shrinkRate) {
        this.shrinkRate = shrinkRate;
    }

    public void setEnforceWallBoundaryLimit(boolean enabled) {
        this.enforceWallBoundaryLimit = enabled;
    }

    public boolean isEnforceWallBoundaryLimit() {
        return enforceWallBoundaryLimit;
    }

    public float getWallRadius() {
        return wallRadius;
    }

    public float getWallThickness() {
        return wallThickness;
    }
}
