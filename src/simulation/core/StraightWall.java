package simulation.core;

import processing.core.PApplet;
import processing.core.PVector;

public class StraightWall extends Wall {
    private final float y;
    private final float elasticity;

    public StraightWall(float y, float thickness, float elasticity) {
        super(thickness);
        this.y = y;
        this.elasticity = elasticity;
    }

    @Override
    public boolean checkCollision(Ball ball) {
        float ballBottom = ball.getPosition().y + ball.getEffectiveRadius();
        return ballBottom >= y - thickness / 2 && ball.getVelocity().y > 0;
    }

    @Override
    public void resolveCollision(Ball ball) {
        PVector pos = ball.getPosition();
        PVector vel = ball.getVelocity();

        pos.y = y - thickness / 2 - ball.getEffectiveRadius();
        vel.y *= -elasticity;

        ball.setVelocity(vel);
        ball.setPosition(pos);
    }

    @Override
    public void display(PApplet app) {
        app.stroke(255);
        app.strokeWeight(thickness);
        app.line(0, y, app.width, y);
    }
}
