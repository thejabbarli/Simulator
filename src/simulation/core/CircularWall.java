package simulation.core;

import processing.core.PApplet;
import processing.core.PVector;

public class CircularWall extends Wall {
    private final PVector center;
    private final float radius;
    private final float elasticity;

    public CircularWall(PVector center, float radius, float thickness, float elasticity) {
        super(thickness);
        this.center = center.copy();
        this.radius = radius;
        this.elasticity = elasticity;
    }

    @Override
    public boolean checkCollision(Ball ball) {
        float ballEffectiveRadius = ball.getEffectiveRadius();
        float distance = PVector.dist(ball.getPosition(), center);

        float innerLimit = radius - thickness / 2 - ballEffectiveRadius;
        float outerLimit = radius + thickness / 2 + ballEffectiveRadius;

        // ✅ Bounce when inside shell region
        return distance >= innerLimit && distance <= outerLimit;
    }

    @Override
    public void resolveCollision(Ball ball) {
        PVector toBall = PVector.sub(ball.getPosition(), center);
        float distance = toBall.mag();

        if (distance < 0.001f) {
            toBall = new PVector(0, -1); // fallback to avoid NaN
        } else {
            toBall.normalize();
        }

        float ballEffectiveRadius = ball.getEffectiveRadius();
        float outerSurface = radius + thickness / 2f + ballEffectiveRadius;
        float innerSurface = radius - thickness / 2f - ballEffectiveRadius;

        float distanceToOuter = Math.abs(distance - (radius + thickness / 2f));
        float distanceToInner = Math.abs(distance - (radius - thickness / 2f));

        // Determine which shell surface is closer and use it as target
        float targetSurface = (distanceToOuter < distanceToInner) ? outerSurface : innerSurface;

        // New position on correct shell
        PVector targetPosition = PVector.add(center, toBall.copy().mult(targetSurface));
        ball.setPosition(targetPosition);

        // Reflect velocity outward/inward from center
        PVector velocity = ball.getVelocity();
        float dot = velocity.dot(toBall);
        PVector reflection = PVector.sub(velocity, toBall.mult(2 * dot)).mult(elasticity);
        ball.setVelocity(reflection);

        ball.markBounce();
    }

    @Override
    public void display(PApplet app) {
        app.stroke(255);
        app.strokeWeight(thickness);
        app.noFill();
        app.ellipse(center.x, center.y, radius * 2, radius * 2);
    }

    public float getRadius() {
        return radius;
    }

    public PVector getCenter() {
        return center.copy();
    }
}