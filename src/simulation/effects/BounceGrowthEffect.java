package simulation.effects;

import simulation.core.Ball;
import simulation.core.Collidable;
import processing.core.PVector;

public class BounceGrowthEffect {
    private PVector lastVelocity = new PVector();

    private float growthAmount;

    public BounceGrowthEffect(float growthAmount) {
        this.growthAmount = growthAmount;
    }

    public void apply(Ball ball, Collidable collidable) {
        PVector currentVel = ball.getVelocity();

        // Detect inversion in velocity direction (bounce)
        boolean bounced = currentVel.dot(lastVelocity) < 0;

        if (bounced) {
            ball.setRadius(ball.getRadius() + growthAmount);
        }

        lastVelocity.set(currentVel);
    }

    public void setGrowthAmount(float amount) {
        this.growthAmount = amount;
    }

    public float getGrowthAmount() {
        return growthAmount;
    }
}
