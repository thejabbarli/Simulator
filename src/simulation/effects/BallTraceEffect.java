package simulation.effects;

import processing.core.PApplet;
import processing.core.PVector;
import simulation.core.Ball;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BallTraceEffect {

    private static class Trace {
        PVector position;
        float radius;
        float stroke;
        int color;
        int lifetime;

        Trace(Ball ball, int lifetime, int color) {
            this.position = ball.getPosition().copy();
            this.radius = ball.getRadius();
            this.stroke = ball.getStrokeThickness();
            this.color = color;
            this.lifetime = lifetime;
        }
    }

    private final List<Trace> traces = new ArrayList<>();
    private int frameCounter = 0;

    private int captureIntervalFrames;
    private int traceLifetimeFrames;
    private boolean permanentTraces;
    private float currentFrameRate;

    public BallTraceEffect(float frequencyPerSecond, int traceLifetimeFrames, boolean permanentTraces, float frameRate) {
        this.currentFrameRate = frameRate;
        this.captureIntervalFrames = Math.round(frameRate / frequencyPerSecond);
        this.traceLifetimeFrames = traceLifetimeFrames;
        this.permanentTraces = permanentTraces;
    }

    public void update(Ball ball, PApplet app) {
        frameCounter++;
        if (frameCounter >= captureIntervalFrames) {
            frameCounter = 0;
            int currentColor = ball.getCurrentVisualStrokeColor(app);
            traces.add(new Trace(ball, traceLifetimeFrames, currentColor));
        }

        if (!permanentTraces) {
            Iterator<Trace> it = traces.iterator();
            while (it.hasNext()) {
                Trace t = it.next();
                t.lifetime--;
                if (t.lifetime <= 0) it.remove();
            }
        }
    }

    public void display(PApplet app) {
        for (Trace t : traces) {
            app.colorMode(PApplet.RGB, 255);
            app.stroke(t.color);
            app.strokeWeight(t.stroke);
            app.noFill();
            app.ellipse(t.position.x, t.position.y, t.radius * 2, t.radius * 2);
        }
    }

    // Runtime-adjustable setters
    public void setFrequency(float frequencyPerSecond) {
        this.captureIntervalFrames = Math.round(currentFrameRate / frequencyPerSecond);
    }

    public void setTraceLifetimeFrames(int frames) {
        this.traceLifetimeFrames = frames;
    }

    public void setPermanentTraces(boolean permanent) {
        this.permanentTraces = permanent;
    }

    // Optional: Getters for GUI use
    public float getFrequency() {
        return currentFrameRate / captureIntervalFrames;
    }

    public int getTraceLifetimeFrames() {
        return traceLifetimeFrames;
    }

    public boolean isPermanentTraces() {
        return permanentTraces;
    }
}
