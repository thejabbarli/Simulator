package simulation.core;

import processing.core.PApplet;
import processing.core.PVector;

public class Ball {
    private PVector position;
    private PVector velocity;
    private float radius;
    private float mass;
    private int color;
    private float strokeThickness = 5;
    private float maxSpeed = Float.MAX_VALUE; // Default = no limit


    public Ball(PVector position, float radius, float mass, int color) {
        this.position = position.copy();
        this.velocity = new PVector(0, 0);
        this.radius = radius;
        this.mass = mass;
        this.color = color;
    }

    public void applyForce(PVector force) {
        PVector acceleration = force.copy().div(mass);
        velocity.add(acceleration);
    }

    public void update() {
        position.add(velocity);
    }

    public void display(PApplet app) {
        float hue = (app.frameCount * 2) % 360;

        app.colorMode(PApplet.HSB, 360, 100, 100, 100);
        app.stroke(hue, 100, 100);
        app.strokeWeight(strokeThickness);
        app.noFill();

        app.ellipse(position.x, position.y, radius * 2, radius * 2);

        app.colorMode(PApplet.RGB, 255);
    }

    public void checkCollision(Collidable collidable) {
        if (collidable.checkCollision(this)) {
            collidable.resolveCollision(this);
        }
    }

    public void grow(float amount) {
        radius += amount;
    }

    // Optional: if you want the stroke thickness to grow too
    public void increaseStroke(float amount) {
        strokeThickness += amount;
    }

    // Getters and Setters

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getEffectiveRadius() {
        return radius + strokeThickness / 2.0f;
    }

    public PVector getPosition() {
        return position;
    }

    public void setPosition(PVector position) {
        this.position = position;
    }

    public PVector getVelocity() {
        return velocity;
    }

    public void setVelocity(PVector velocity) {
        if (velocity.mag() > maxSpeed) {
            velocity = velocity.copy().normalize().mult(maxSpeed);
        }
        this.velocity = velocity;
    }


    public int getCurrentVisualStrokeColor(PApplet app) {
        float hue = (app.frameCount * 2) % 360;
        app.colorMode(PApplet.HSB, 360, 100, 100, 100);
        int c = app.color(hue, 100, 100);
        app.colorMode(PApplet.RGB, 255);
        return c;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getStrokeThickness() {
        return strokeThickness;
    }

    public void setStrokeThickness(float strokeThickness) {
        this.strokeThickness = strokeThickness;
    }
}
