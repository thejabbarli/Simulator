package simulation.effects;

import processing.core.PVector;
import simulation.core.Ball;

public class BounceSpeedBoostEffect implements BallEffect {
    private float lastYVelocity = 0;
    private float boostFactor;
    private boolean enabled = true;

    public BounceSpeedBoostEffect(float boostFactor) {
        this.boostFactor = boostFactor;
    }

    @Override
    public void apply(Ball ball) {
        float currentY = ball.getVelocity().y;
        boolean isBouncing = (lastYVelocity > 0) && (currentY < 0);

        if (isBouncing) {
            PVector velocity = ball.getVelocity().copy();
            if (velocity.mag() > 0) {
                velocity.normalize().mult(ball.getVelocity().mag() * boostFactor);
                ball.setVelocity(velocity);
            }
        }

        lastYVelocity = currentY;
    }

    public void setBoostFactor(float boostFactor) {
        this.boostFactor = boostFactor;
    }

    public float getBoostFactor() {
        return boostFactor;
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