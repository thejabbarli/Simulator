package simulation.effects;

import simulation.core.Ball;
import simulation.core.Collidable;

public class BounceGrowthEffect {
    private float growthAmount;
    private MaxSizeStopEffect sizeLimiter;

    public BounceGrowthEffect(float growthAmount, MaxSizeStopEffect limiter) {
        this.growthAmount = growthAmount;
        this.sizeLimiter = limiter;
    }

    public void apply(Ball ball, Collidable collidable) {
        if (ball.hasJustBounced()) {
            if (MaxSizeStopEffect.canGrow(ball, growthAmount, sizeLimiter.getWallRadius(), sizeLimiter.getWallThickness())) {
                ball.setRadius(ball.getRadius() + growthAmount);
                System.out.println("✅ Bounce! New radius: " + ball.getRadius());
            } else {
                System.out.println("🛑 Max size reached. Growth blocked.");
            }
        }
    }

    public void setGrowthAmount(float amount) {
        this.growthAmount = amount;
    }

    public float getGrowthAmount() {
        return growthAmount;
    }
}
