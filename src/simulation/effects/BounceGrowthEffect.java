package simulation.effects;

import simulation.core.Ball;

public class BounceGrowthEffect implements BallEffect {
    private float growthAmount;
    private boolean enabled = true;
    private final MaxSizeChecker sizeChecker;

    public BounceGrowthEffect(float growthAmount, MaxSizeChecker sizeChecker) {
        this.growthAmount = growthAmount;
        this.sizeChecker = sizeChecker;
    }

    @Override
    public void apply(Ball ball) {
        if (!ball.hasJustBounced()) return;

        float effectiveRadius = ball.getEffectiveRadius();
        if (sizeChecker.canGrow(ball, effectiveRadius, growthAmount)) {
            ball.setRadius(ball.getRadius() + growthAmount);
            System.out.println("✅ Bounce! New radius: " + ball.getRadius());
        } else {
            System.out.println("🛑 Max size reached. Growth blocked.");
        }
    }

    public void setGrowthAmount(float amount) {
        this.growthAmount = amount;
    }

    public float getGrowthAmount() {
        return growthAmount;
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