package simulation.effects;

import processing.core.PVector;
import simulation.core.Ball;

public class MaxSizeStopEffect implements BallEffect {
    private final PVector wallCenter;
    private final MaxSizeChecker sizeChecker;
    private float growthAmount;
    private boolean shouldStop;
    private boolean shouldShrink;
    private float shrinkRate;
    private boolean enabled = true;

    public MaxSizeStopEffect(
            PVector wallCenter,
            MaxSizeChecker sizeChecker,
            float growthAmount,
            boolean shouldStop,
            boolean shouldShrink,
            float shrinkRate
    ) {
        this.wallCenter = wallCenter;
        this.sizeChecker = sizeChecker;
        this.growthAmount = growthAmount;
        this.shouldStop = shouldStop;
        this.shouldShrink = shouldShrink;
        this.shrinkRate = shrinkRate;
    }

    @Override
    public void apply(Ball ball) {
        if (!ball.hasJustBounced()) return;

        float effectiveRadius = ball.getEffectiveRadius();
        float wallInner = sizeChecker.getWallRadius() - sizeChecker.getWallThickness() / 2f;

        // Check if we'll exceed the wall boundary after growth
        if (effectiveRadius + growthAmount >= wallInner) {
            System.out.println("❌ Max size reached - ball at limits");

            if (shouldStop) {
                // Center and lock the ball at max allowed size
                ball.setPosition(wallCenter.copy());
                float adjustedRadius = wallInner - ball.getStrokeThickness() / 2f;
                ball.setRadius(adjustedRadius);
                ball.lockMotion();

                System.out.printf("🔒 Ball locked in center. Final radius = %.2f, Effective = %.2f, Target = %.2f\n",
                        ball.getRadius(), ball.getEffectiveRadius(), wallInner);
            } else if (shouldShrink) {
                // Shrink the ball instead of locking
                float newRadius = ball.getRadius() - shrinkRate;
                // Don't let it shrink to zero or negative
                if (newRadius > 1.0f) {
                    ball.setRadius(newRadius);
                    System.out.printf("📉 Ball shrinking. New radius = %.2f\n", newRadius);
                }
            }
        }
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

    public void setEnforceWallBoundaryLimit(boolean enforce) {
        sizeChecker.setEnforceWallBoundaryLimit(enforce);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}