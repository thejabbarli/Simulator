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
        ball.resetBounceFlag();          // 🧼 reset at start of frame
        ball.preserveVelocity();         // ✅ track velocity
        ball.applyForce(gravity);
        ball.update();
        for (Collidable c : collidables) {
            ball.checkCollision(c);      // this may call resolveCollision()
        }
    }


}
