package simulation.core;

import processing.core.PApplet;
import processing.core.PVector;

public class Ball {
    private PVector position;
    private PVector velocity;
    private float radius;
    private float strokeThickness;
    private float mass;
    private int color;
    private float maxSpeed = Float.MAX_VALUE;
    private PVector previousVelocity = new PVector();

    // State tracking managed by BallStateManager
    private final BallStateManager stateManager = new BallStateManager();

    public Ball(PVector position, float radius, float mass) {
        this.position = position.copy();
        this.velocity = new PVector(0, 0);
        this.radius = radius;
        this.mass = mass;
    }

    public void applyForce(PVector force) {
        if (stateManager.isLocked()) return;

        PVector acceleration = force.copy().div(mass);
        velocity.add(acceleration);
    }

    public void update() {
        if (stateManager.isLocked()) return;
        position.add(velocity);
    }

    public void checkCollision(Collidable collidable) {
        if (stateManager.isLocked()) return;
        if (collidable.checkCollision(this)) {
            collidable.resolveCollision(this);
            stateManager.markBounce();
        }
    }

    // Core physics getters/setters
    public PVector getPosition() { return position; }
    public void setPosition(PVector position) { this.position = position; }
    public PVector getVelocity() { return velocity; }

    public void setVelocity(PVector velocity) {
        if (stateManager.isLocked()) return;

        if (velocity.mag() > maxSpeed) {
            velocity = velocity.copy().normalize().mult(maxSpeed);
        }
        this.velocity = velocity;
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }
    public float getMass() { return mass; }
    public void setMass(float mass) { this.mass = mass; }
    public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }
    public float getStrokeThickness() { return strokeThickness; }
    public void setStrokeThickness(float thickness) { this.strokeThickness = thickness; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public float getEffectiveRadius() {
        return radius + strokeThickness / 2.0f;
    }

    public int getCurrentVisualStrokeColor(PApplet app) {
        float hue = (app.frameCount * 2) % 360;
        app.colorMode(PApplet.HSB, 360, 100, 100, 100);
        int c = app.color(hue, 100, 100);
        app.colorMode(PApplet.RGB, 255);
        return c;
    }

    // State delegation methods
    public boolean isLocked() { return stateManager.isLocked(); }
    public void lockMotion() {
        this.velocity.set(0, 0);
        stateManager.setLocked(true);
    }
    public boolean hasJustBounced() { return stateManager.hasJustBounced(); }
    public void resetBounceFlag() { stateManager.resetBounceFlag(); }
    public void markBounce() { stateManager.markBounce(); }

    // Physics utility methods
    public void preserveVelocity() { previousVelocity.set(velocity); }
    public PVector getPreviousVelocity() { return previousVelocity; }
}
