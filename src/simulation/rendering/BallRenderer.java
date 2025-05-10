package simulation.rendering;

import processing.core.PApplet;
import simulation.core.Ball;

public class BallRenderer {
    private float strokeThickness = 5;
    private int color;

    // In the BallRenderer class, update the constructor if necessary
    public BallRenderer(int color) {
        this.color = color;
        this.strokeThickness = 5; // Set a default thickness value that's clearly visible
    }

    public void display(Ball ball, PApplet app) {
        float hue = (app.frameCount * 2) % 360;

        app.colorMode(PApplet.HSB, 360, 100, 100, 100);
        app.stroke(hue, 100, 100);
        app.strokeWeight(strokeThickness);
        app.noFill();

        app.ellipse(ball.getPosition().x, ball.getPosition().y,
                ball.getRadius() * 2, ball.getRadius() * 2);

        app.colorMode(PApplet.RGB, 255);
    }

    public int getCurrentVisualStrokeColor(PApplet app) {
        float hue = (app.frameCount * 2) % 360;
        app.colorMode(PApplet.HSB, 360, 100, 100, 100);
        int c = app.color(hue, 100, 100);
        app.colorMode(PApplet.RGB, 255);
        return c;
    }

    public float getStrokeThickness() { return strokeThickness; }
    public void setStrokeThickness(float thickness) { this.strokeThickness = thickness; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }

    public float getEffectiveRadius(Ball ball) {
        return ball.getRadius() + strokeThickness / 2.0f;
    }
}