package simulation.config;

import processing.data.JSONObject;

public class SettingsManager {

    // Ball settings
    private float ballRadius = 30f;
    private float ballStroke = 0.1f;
    private float ballMass = 1.0f;
    private int ballColor = 0xFFFF00FF; // Magenta (HSB ignored)
    private float ballMaxSpeed = 300f;

    // Physics
    private float gravity = 0.7f;

    // Bounce Growth
    private float growthAmount = 1.1f;

    // Bounce Speed Boost
    private float speedBoostFactor = 1.012f;

    // Max Size Stop
    private float maxSizeRadius = 250f;
    private boolean shouldStop = true;
    private boolean shouldShrink = true;
    private float shrinkRate = 0.5f;

    // Ball Trace
    private float traceFrequency = 2500f;
    private int traceLifetimeFrames = 2;
    private boolean permanentTraces = false;

    // In your SettingsManager class
    private float trailThicknessMultiplier = 1f; // Default to 1x thickness

    public float getTrailThicknessMultiplier() {
        return trailThicknessMultiplier;
    }

    public void setTrailThicknessMultiplier(float multiplier) {
        this.trailThicknessMultiplier = multiplier;
    }

    // In SettingsManager class
    private float trailThickness = 10.0f; // Default thickness

    public float getTrailThickness() {
        return trailThickness;
    }

    public void setTrailThickness(float thickness) {
        this.trailThickness = thickness;
    }

    // Sound settings
    private boolean soundEnabled = true;
    private int noteDuration = 150; // milliseconds
    private float noteVolume = 0.5f; // 0.0 - 1.0
    private int bounceInstrument = 0; // Default to sine wave
    private boolean pitchModeRadius = true; // If true, pitch is determined by radius
    private boolean pitchModeVelocity = false; // If true, pitch is determined by velocity
    private int basePitch = 48; // A4 (middle A) by default
    private int pitchRange = 36; // 3 octaves range

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public int getNoteDuration() {
        return noteDuration;
    }

    public void setNoteDuration(int duration) {
        this.noteDuration = duration;
    }

    public float getNoteVolume() {
        return noteVolume;
    }

    public void setNoteVolume(float volume) {
        this.noteVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public int getBounceInstrument() {
        return bounceInstrument;
    }

    public void setBounceInstrument(int instrument) {
        this.bounceInstrument = instrument;
    }

    public boolean isPitchModeRadius() {
        return pitchModeRadius;
    }

    public void setPitchModeRadius(boolean enabled) {
        this.pitchModeRadius = enabled;
    }

    public boolean isPitchModeVelocity() {
        return pitchModeVelocity;
    }

    public void setPitchModeVelocity(boolean enabled) {
        this.pitchModeVelocity = enabled;
    }

    public int getBasePitch() {
        return basePitch;
    }

    public void setBasePitch(int pitch) {
        this.basePitch = Math.max(0, Math.min(87, pitch));
    }

    public int getPitchRange() {
        return pitchRange;
    }

    public void setPitchRange(int range) {
        this.pitchRange = Math.max(1, Math.min(87, range));
    }

    // Wall boundaries
    private boolean enforceWallBoundaryLimit = true;

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

    public boolean isEnforceWallBoundaryLimit() {
        return enforceWallBoundaryLimit;
    }

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

    public void setEnforceWallBoundaryLimit(boolean enforce) {
        this.enforceWallBoundaryLimit = enforce;
    }

    /**
     * Convert settings to JSON
     * @return JSON representation of settings
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        // Ball settings
        json.setFloat("ballRadius", ballRadius);
        json.setFloat("ballStroke", ballStroke);
        json.setFloat("ballMass", ballMass);
        json.setInt("ballColor", ballColor);
        json.setFloat("ballMaxSpeed", ballMaxSpeed);

        // Physics
        json.setFloat("gravity", gravity);
        json.setFloat("growthAmount", growthAmount);
        json.setFloat("speedBoostFactor", speedBoostFactor);

        // Max Size
        json.setFloat("maxSizeRadius", maxSizeRadius);
        json.setBoolean("shouldStop", shouldStop);
        json.setBoolean("shouldShrink", shouldShrink);
        json.setFloat("shrinkRate", shrinkRate);
        json.setBoolean("enforceWallBoundaryLimit", enforceWallBoundaryLimit);

        // Traces
        json.setFloat("traceFrequency", traceFrequency);
        json.setInt("traceLifetimeFrames", traceLifetimeFrames);
        json.setBoolean("permanentTraces", permanentTraces);
        json.setFloat("trailThickness", trailThickness);
        json.setFloat("trailThicknessMultiplier", trailThicknessMultiplier);

        // Sound
        json.setBoolean("soundEnabled", soundEnabled);
        json.setInt("noteDuration", noteDuration);
        json.setFloat("noteVolume", noteVolume);
        json.setInt("bounceInstrument", bounceInstrument);
        json.setBoolean("pitchModeRadius", pitchModeRadius);
        json.setBoolean("pitchModeVelocity", pitchModeVelocity);
        json.setInt("basePitch", basePitch);
        json.setInt("pitchRange", pitchRange);

        return json;
    }

    /**
     * Load settings from JSON
     * @param json JSON representation of settings
     */
    public void fromJSON(JSONObject json) {
        // Ball settings
        if (json.hasKey("ballRadius")) ballRadius = json.getFloat("ballRadius");
        if (json.hasKey("ballStroke")) ballStroke = json.getFloat("ballStroke");
        if (json.hasKey("ballMass")) ballMass = json.getFloat("ballMass");
        if (json.hasKey("ballColor")) ballColor = json.getInt("ballColor");
        if (json.hasKey("ballMaxSpeed")) ballMaxSpeed = json.getFloat("ballMaxSpeed");

        // Physics
        if (json.hasKey("gravity")) gravity = json.getFloat("gravity");
        if (json.hasKey("growthAmount")) growthAmount = json.getFloat("growthAmount");
        if (json.hasKey("speedBoostFactor")) speedBoostFactor = json.getFloat("speedBoostFactor");

        // Max Size
        if (json.hasKey("maxSizeRadius")) maxSizeRadius = json.getFloat("maxSizeRadius");
        if (json.hasKey("shouldStop")) shouldStop = json.getBoolean("shouldStop");
        if (json.hasKey("shouldShrink")) shouldShrink = json.getBoolean("shouldShrink");
        if (json.hasKey("shrinkRate")) shrinkRate = json.getFloat("shrinkRate");
        if (json.hasKey("enforceWallBoundaryLimit")) enforceWallBoundaryLimit = json.getBoolean("enforceWallBoundaryLimit");

        // Traces
        if (json.hasKey("traceFrequency")) traceFrequency = json.getFloat("traceFrequency");
        if (json.hasKey("traceLifetimeFrames")) traceLifetimeFrames = json.getInt("traceLifetimeFrames");
        if (json.hasKey("permanentTraces")) permanentTraces = json.getBoolean("permanentTraces");
        if (json.hasKey("trailThickness")) trailThickness = json.getFloat("trailThickness");
        if (json.hasKey("trailThicknessMultiplier")) trailThicknessMultiplier = json.getFloat("trailThicknessMultiplier");

        // Sound
        if (json.hasKey("soundEnabled")) soundEnabled = json.getBoolean("soundEnabled");
        if (json.hasKey("noteDuration")) noteDuration = json.getInt("noteDuration");
        if (json.hasKey("noteVolume")) noteVolume = json.getFloat("noteVolume");
        if (json.hasKey("bounceInstrument")) bounceInstrument = json.getInt("bounceInstrument");
        if (json.hasKey("pitchModeRadius")) pitchModeRadius = json.getBoolean("pitchModeRadius");
        if (json.hasKey("pitchModeVelocity")) pitchModeVelocity = json.getBoolean("pitchModeVelocity");
        if (json.hasKey("basePitch")) basePitch = json.getInt("basePitch");
        if (json.hasKey("pitchRange")) pitchRange = json.getInt("pitchRange");
    }
}