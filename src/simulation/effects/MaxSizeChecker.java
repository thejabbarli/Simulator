package simulation.effects;

import simulation.core.Ball;

public class MaxSizeChecker {
    private float wallRadius;
    private float wallThickness;
    private boolean enforceWallBoundaryLimit = true;

    public MaxSizeChecker(float wallRadius, float wallThickness) {
        this.wallRadius = wallRadius;
        this.wallThickness = wallThickness;
    }

    public boolean canGrow(Ball ball, float effectiveRadius, float growAmount) {
        if (!enforceWallBoundaryLimit) return true;

        float futureEffectiveRadius = effectiveRadius + growAmount;
        float wallInner = wallRadius - wallThickness / 2f;
        boolean allowed = futureEffectiveRadius < wallInner;

        System.out.printf("🧪 GROWTH CHECK → current=%.2f, future=%.2f, wallInner=%.2f → %s\n",
                effectiveRadius, futureEffectiveRadius, wallInner,
                allowed ? "✅ ALLOWED" : "❌ BLOCKED");

        return allowed;
    }

    // Getters and setters
    public void setWallRadius(float wallRadius) {
        this.wallRadius = wallRadius;
    }

    public void setWallThickness(float wallThickness) {
        this.wallThickness = wallThickness;
    }

    public void setEnforceWallBoundaryLimit(boolean enforce) {
        this.enforceWallBoundaryLimit = enforce;
    }

    public float getWallRadius() {
        return wallRadius;
    }

    public float getWallThickness() {
        return wallThickness;
    }
}