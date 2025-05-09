package simulation.core;

import processing.core.PVector;
import java.util.List;

public class PhysicsEngine {
    private final PVector gravity;

    public PhysicsEngine(float gravityStrength) {
        this.gravity = new PVector(0, gravityStrength);
    }

    public void applyForces(Ball ball) {
        ball.applyForce(gravity);
    }

    public void update(Ball ball, List<Collidable> collidables) {
        applyForces(ball);
        ball.update();
        for (Collidable c : collidables) {
            ball.checkCollision(c);
        }
    }
}
