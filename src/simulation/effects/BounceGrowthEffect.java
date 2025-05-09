package simulation.effects;

import simulation.core.Ball;
import simulation.core.Collidable;

public class BounceGrowthEffect {
    private float lastYVelocity = 0;

    public void apply(Ball ball, Collidable collidable) {
        float currentY = ball.getVelocity().y;
        boolean isBouncing = (lastYVelocity > 0) && (currentY < 0);

        if (isBouncing) {
            ball.setRadius(ball.getRadius() + 3.5f);
        }

        lastYVelocity = currentY;
    }
}
