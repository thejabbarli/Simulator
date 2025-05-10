package simulation.effects;

import simulation.core.Ball;

public class MaxSizeStopEffect {
    private float maxRadius;
    private boolean shouldStop;
    private boolean shouldShrink;
    private float shrinkRate;

    public MaxSizeStopEffect(float maxRadius, boolean shouldStop, boolean shouldShrink, float shrinkRate) {
        this.maxRadius = maxRadius;
        this.shouldStop = shouldStop;
        this.shouldShrink = shouldShrink;
        this.shrinkRate = shrinkRate;
    }

    public void apply(Ball ball, float wallRadius, float wallThickness) {
        float ballOuter = ball.getRadius() + ball.getStrokeThickness() / 2f;
        float wallInner = wallRadius - wallThickness / 2f;

        if (ballOuter >= wallInner) {
            if (shouldStop) {
                ball.setVelocity(ball.getVelocity().mult(0));
            }
            if (shouldShrink) {
                ball.setRadius(ball.getRadius() - shrinkRate);
            }
        }
    }

    // Runtime GUI adjustment setters
    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
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

    // Optional getters for GUI panels
    public float getMaxRadius() { return maxRadius; }
    public boolean isShouldStop() { return shouldStop; }
    public boolean isShouldShrink() { return shouldShrink; }
    public float getShrinkRate() { return shrinkRate; }
}
