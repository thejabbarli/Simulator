package simulation.config;

public class SettingsManager {

    // Ball settings
    private float ballRadius = 30f;
    private float ballStroke = 5f;
    private float ballMass = 1.0f;
    private int ballColor = 0xFFFF00FF; // Magenta (HSB ignored)
    private float ballMaxSpeed = 40f;

    // Physics
    private float gravity = 0.7f;

    // Bounce Growth
    private float growthAmount = 1.7f;

    // Bounce Speed Boost
    private float speedBoostFactor = 1.075f;

    // Max Size Stop
    private float maxSizeRadius = 250f;
    private boolean shouldStop = true;
    private boolean shouldShrink = true;
    private float shrinkRate = 0.5f;

    // Ball Trace
    private float traceFrequency = 40f;
    private int traceLifetimeFrames = 40;
    private boolean permanentTraces = true;

    // Getters
    public float getBallRadius() { return ballRadius; }
    public float getBallStroke() { return ballStroke; }
    public float getBallMass() { return ballMass; }
    public int getBallColor() { return ballColor; }
    public float getBallMaxSpeed() { return ballMaxSpeed; }

    public float getGravity() { return gravity; }

    public float getGrowthAmount() { return growthAmount; }

    public float getSpeedBoostFactor() { return speedBoostFactor; }

    public float getMaxSizeRadius() { return maxSizeRadius; }
    public boolean getShouldStop() { return shouldStop; }
    public boolean getShouldShrink() { return shouldShrink; }
    public float getShrinkRate() { return shrinkRate; }

    public float getTraceFrequency() { return traceFrequency; }
    public int getTraceLifetimeFrames() { return traceLifetimeFrames; }
    public boolean getPermanentTraces() { return permanentTraces; }

    // Setters (for GUI use)
    public void setBallRadius(float r) { ballRadius = r; }
    public void setBallStroke(float s) { ballStroke = s; }
    public void setBallMass(float m) { ballMass = m; }
    public void setBallColor(int c) { ballColor = c; }
    public void setBallMaxSpeed(float s) { ballMaxSpeed = s; }

    public void setGravity(float g) { gravity = g; }

    public void setGrowthAmount(float g) { growthAmount = g; }

    public void setSpeedBoostFactor(float f) { speedBoostFactor = f; }

    public void setMaxSizeRadius(float r) { maxSizeRadius = r; }
    public void setShouldStop(boolean b) { shouldStop = b; }
    public void setShouldShrink(boolean b) { shouldShrink = b; }
    public void setShrinkRate(float r) { shrinkRate = r; }

    public void setTraceFrequency(float f) { traceFrequency = f; }
    public void setTraceLifetimeFrames(int f) { traceLifetimeFrames = f; }
    public void setPermanentTraces(boolean b) { permanentTraces = b; }
}
