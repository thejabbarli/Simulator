package simulation.effects;

import processing.core.PApplet;
import processing.core.PVector;
import simulation.core.Ball;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BallTraceEffect implements BallEffect {

    private static class Trace {
        PVector position;
        float radius;
        float stroke;
        int color;
        int lifetime;


        Trace(Ball ball, int lifetime, int color) {
            this.position = ball.getPosition().copy();
            this.radius = ball.getRadius();
            this.stroke = ball.getStrokeThickness(); // Store the original thickness
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
    private boolean enabled = true;
    private PApplet applet;
    private float trailThicknessMultiplier = 1.0f; // Default is same as ball

    public BallTraceEffect(
            float frequencyPerSecond,
            int traceLifetimeFrames,
            boolean permanentTraces,
            float frameRate,
            PApplet applet
    ) {
        this.currentFrameRate = frameRate;
        this.captureIntervalFrames = Math.round(frameRate / frequencyPerSecond);
        this.traceLifetimeFrames = traceLifetimeFrames;
        this.permanentTraces = permanentTraces;
        this.applet = applet;
        this.trailThicknessMultiplier = 1.0f; // Default same as ball
    }

    // Overload constructor to allow specifying trail thickness multiplier
    public BallTraceEffect(
            float frequencyPerSecond,
            int traceLifetimeFrames,
            boolean permanentTraces,
            float frameRate,
            PApplet applet,
            float trailThicknessMultiplier
    ) {
        this(frequencyPerSecond, traceLifetimeFrames, permanentTraces, frameRate, applet);
        this.trailThicknessMultiplier = trailThicknessMultiplier;
    }

    @Override
    public void apply(Ball ball) {
        frameCounter++;
        if (frameCounter >= captureIntervalFrames) {
            frameCounter = 0;
            int currentColor = ball.getCurrentVisualStrokeColor(applet);
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

    // In BallTraceEffect class, modify the display method
    public void display(PApplet app) {
        for (Trace t : traces) {
            app.colorMode(PApplet.RGB, 255);
            app.stroke(t.color);
            app.strokeWeight(10); // Use a fixed, clearly visible value like 10 pixels
            app.noFill();
            app.ellipse(t.position.x, t.position.y, t.radius * 2, t.radius * 2);
        }
    }

    // Runtime-adjustable setters
    public void setFrequency(float frequencyPerSecond) {
        this.captureIntervalFrames = Math.round(currentFrameRate / frequencyPerSecond);
    }

    public void setTrailThicknessMultiplier(float multiplier) {
        this.trailThicknessMultiplier = multiplier;
    }

    public float getTrailThicknessMultiplier() {
        return trailThicknessMultiplier;
    }

    public void setTraceLifetimeFrames(int frames) {
        this.traceLifetimeFrames = frames;
    }

    public void setPermanentTraces(boolean permanent) {
        this.permanentTraces = permanent;
    }

    public float getFrequency() {
        return currentFrameRate / captureIntervalFrames;
    }

    public int getTraceLifetimeFrames() {
        return traceLifetimeFrames;
    }

    public boolean isPermanentTraces() {
        return permanentTraces;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}