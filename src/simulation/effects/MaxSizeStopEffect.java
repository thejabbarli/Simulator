package simulation.effects;

import simulation.core.Ball;

public class MaxSizeStopEffect {
    private float wallRadius;
    private float wallThickness;
    private boolean shouldStop;
    private boolean shouldShrink;
    private float shrinkRate;
    private boolean enforceWallBoundaryLimit;

    public MaxSizeStopEffect(float wallRadius, float wallThickness, boolean shouldStop, boolean shouldShrink, float shrinkRate, boolean enforceWallBoundaryLimit) {
        this.wallRadius = wallRadius;
        this.wallThickness = wallThickness;
        this.shouldStop = shouldStop;
        this.shouldShrink = shouldShrink;
        this.shrinkRate = shrinkRate;
        this.enforceWallBoundaryLimit = enforceWallBoundaryLimit;
    }

    public void apply(Ball ball) {
        if (!enforceWallBoundaryLimit || ball.isLocked()) return;

        float ballOuterEdge = ball.getRadius() + ball.getStrokeThickness() / 2f;
        float wallInnerEdge = wallRadius - wallThickness / 2f;

        if (ballOuterEdge >= wallInnerEdge) {
            if (!shouldShrink) {
                float maxAllowedRadius = wallInnerEdge - ball.getStrokeThickness() / 2f;
                ball.setRadius(maxAllowedRadius);
            }
            if (shouldStop && !ball.isLocked()) {
                ball.lockMotion();
                System.out.printf("\uD83E\uDDCA Ball locked at radius %.2f\n", ball.getRadius());
            }
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
