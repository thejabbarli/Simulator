package simulation.effects;

import processing.core.PVector;
import simulation.core.Ball;
import simulation.core.Collidable;

public class BounceSpeedBoostEffect {
    private float lastYVelocity = 0;
    private final float boostFactor;

    public BounceSpeedBoostEffect(float boostFactor) {
        this.boostFactor = boostFactor;
    }

    public void apply(Ball ball, Collidable collidable) {
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
}
