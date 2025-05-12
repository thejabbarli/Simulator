package simulation.core;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import simulation.audio.NotePlayer;
import simulation.audio.ProcessingNotePlayer;
import simulation.config.SettingsManager;
import simulation.effects.*;
import simulation.gui.GuiManager;
import simulation.rendering.BallRenderer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main simulation application
 */
public class SimulationApp extends PApplet {

    // Configuration
    private SettingsManager settings;

    // Core components
    private Ball ball;
    private BallRenderer ballRenderer;
    private List<Wall> walls;
    private List<Collidable> collidables;
    private PhysicsEngine physicsEngine;
    private EffectSystem effectSystem;
    private MaxSizeChecker maxSizeChecker;
    private NotePlayer notePlayer;

    // GUI
    private GuiManager guiManager;

    // Render states
    private boolean highQualityRendering = false;
    private PGraphics renderBuffer;
    private int renderScale = 1;
    private boolean useAntialiasing = true;

    // Recording state
    private boolean recording = false;
    private int recordingCounter = 0;
    private String exportFolderPath;
    private float recordingStartTime = 0;

    // Visual settings
    private int backgroundBrightness = 0;
    private boolean rainbowMode = true;
    private float colorSpeed = 1.0f;
    private boolean showVelocityVector = false;

    // Window settings
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 800;
    private final int TARGET_FRAMERATE = 60;
    private boolean needResize = false;
    private int newWidth = WINDOW_WIDTH;
    private int newHeight = WINDOW_HEIGHT;

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        PApplet.main("simulation.core.SimulationApp");
    }

    @Override
    public void settings() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT, P2D);
        smooth(4);
    }

    @Override
    public void setup() {
        background(backgroundBrightness);
        frameRate(TARGET_FRAMERATE);
        initializeSettings();
        initializeSimulationComponents();
        initializeEffectSystem();
        initializeGUI();
        createRenderBuffer();
    }

    /**
     * Initialize settings manager
     */
    private void initializeSettings() {
        settings = new SettingsManager();
    }

    /**
     * Initialize GUI system
     */
    private void initializeGUI() {
        guiManager = new GuiManager(this, settings, this);
    }

    /**
     * Initialize core simulation components
     */
    private void initializeSimulationComponents() {
        float wallRadius = 350;
        float wallThickness = 10;
        float elasticity = 1.0f;
        PVector wallCenter = new PVector(width / 2f, height / 2f);

        PVector ballPosition = new PVector(wallCenter.x + 100, wallCenter.y - wallRadius / 2);
        ball = createBall(ballPosition);

        ballRenderer = createBallRenderer();

        walls = createWalls(wallCenter, wallRadius, wallThickness, elasticity);
        collidables = new ArrayList<>(walls);

        physicsEngine = new PhysicsEngine(settings.getGravity());
        maxSizeChecker = new MaxSizeChecker(wallRadius, wallThickness);

        notePlayer = new ProcessingNotePlayer(this);
        notePlayer.initialize();
    }

    /**
     * Create and configure a ball
     */
    private Ball createBall(PVector ballPosition) {
        Ball newBall = new Ball(ballPosition, settings.getBallRadius(), settings.getBallMass());
        newBall.setStrokeThickness(settings.getBallStroke());
        newBall.setMaxSpeed(settings.getBallMaxSpeed());
        newBall.setColor(settings.getBallColor());
        return newBall;
    }

    /**
     * Create and configure a ball renderer
     */
    private BallRenderer createBallRenderer() {
        BallRenderer renderer = new BallRenderer(settings.getBallColor());
        renderer.setStrokeThickness(settings.getBallStroke());
        return renderer;
    }

    /**
     * Create walls for the simulation
     */
    private List<Wall> createWalls(PVector wallCenter, float wallRadius, float wallThickness, float elasticity) {
        List<Wall> wallList = new ArrayList<>();
        wallList.add(new CircularWall(wallCenter, wallRadius, wallThickness, elasticity));
        return wallList;
    }

    /**
     * Initialize the effect system
     */
    private void initializeEffectSystem() {
        effectSystem = new EffectSystem();

        BounceGrowthEffect bounceGrowth = new BounceGrowthEffect(settings.getGrowthAmount(), maxSizeChecker);
        effectSystem.registerEffect(bounceGrowth);

        BounceSpeedBoostEffect bounceSpeedBoost = new BounceSpeedBoostEffect(settings.getSpeedBoostFactor());
        effectSystem.registerEffect(bounceSpeedBoost);

        PVector wallCenter = new PVector(width / 2f, height / 2f);
        MaxSizeStopEffect maxSizeStop = new MaxSizeStopEffect(wallCenter, maxSizeChecker,
                settings.getGrowthAmount(), settings.getShouldStop(),
                settings.getShouldShrink(), settings.getShrinkRate());
        maxSizeStop.setEnforceWallBoundaryLimit(settings.isEnforceWallBoundaryLimit());
        effectSystem.registerEffect(maxSizeStop);

        BallTraceEffect ballTrace = new BallTraceEffect(
                settings.getTraceFrequency(),
                settings.getTraceLifetimeFrames(),
                settings.getPermanentTraces(),
                TARGET_FRAMERATE,
                this,
                settings.getTrailThicknessMultiplier()
        );
        effectSystem.registerEffect(ballTrace);

        BounceNoteEffect bounceNote = new BounceNoteEffect(
                notePlayer,
                settings,
                maxSizeChecker.getWallRadius(),
                settings.getBallMaxSpeed()
        );
        effectSystem.registerEffect(bounceNote);
    }

    /**
     * Create the high quality render buffer
     */
    private void createRenderBuffer() {
        renderBuffer = createGraphics(width * renderScale, height * renderScale, P2D);

        // Configure the buffer
        renderBuffer.smooth(useAntialiasing ? 8 : 0);
        renderBuffer.beginDraw();
        renderBuffer.background(backgroundBrightness);
        renderBuffer.endDraw();
    }

    @Override
    public void draw() {
        // Handle resize if requested
        if (needResize) {
            surface.setSize(newWidth, newHeight);
            needResize = false;
            createRenderBuffer();
        }

        if (highQualityRendering) {
            drawHighQuality();
        } else {
            drawStandard();
        }

        // Draw GUI
        guiManager.draw();

        // Handle recording
        if (recording) {
            recordFrame();
        }

        // Update GUI state
        guiManager.update();
    }

    /**
     * Draw using standard quality
     */
    private void drawStandard() {
        background(backgroundBrightness);
        updatePhysics();
        effectSystem.applyEffects(ball);
        renderScene(g);
    }

    /**
     * Draw using high quality rendering
     */
    private void drawHighQuality() {
        // Setup render buffer
        renderBuffer.beginDraw();
        renderBuffer.background(backgroundBrightness);
        renderBuffer.scale(renderScale);

        // Update physics
        updatePhysics();
        effectSystem.applyEffects(ball);

        // Render to buffer
        renderScene(renderBuffer);

        renderBuffer.endDraw();

        // Draw the buffer to screen
        image(renderBuffer, 0, 0, width, height);
    }

    /**
     * Draw the simulation to a specific buffer
     */
    public void drawToBuffer(PGraphics buffer) {
        updatePhysics();
        effectSystem.applyEffects(ball);
        renderScene(buffer);
    }

    /**
     * Update physics state
     */
    private void updatePhysics() {
        physicsEngine.update(ball, collidables);
    }

    /**
     * Render the scene to the specified PGraphics context
     */
    private void renderScene(PGraphics graphics) {
        BallTraceEffect traceEffect = effectSystem.getEffect(BallTraceEffect.class);
        if (traceEffect != null && traceEffect.isEnabled()) {
            traceEffect.display(this);
        }

        // Draw the ball
        graphics.pushStyle();
        if (rainbowMode) {
            float hue = (frameCount * colorSpeed * 2) % 360;
            graphics.colorMode(HSB, 360, 100, 100, 100);
            graphics.stroke(hue, 100, 100);
        } else {
            graphics.stroke(ball.getColor());
        }
        graphics.strokeWeight(ball.getStrokeThickness());
        graphics.noFill();
        graphics.ellipse(
                ball.getPosition().x,
                ball.getPosition().y,
                ball.getRadius() * 2,
                ball.getRadius() * 2
        );
        graphics.popStyle();

        // Draw velocity vector if enabled
        if (showVelocityVector) {
            graphics.pushStyle();
            graphics.stroke(255, 100, 100);
            graphics.strokeWeight(2);
            PVector vel = ball.getVelocity().copy().normalize().mult(ball.getRadius() * 1.5f);
            graphics.line(
                    ball.getPosition().x,
                    ball.getPosition().y,
                    ball.getPosition().x + vel.x,
                    ball.getPosition().y + vel.y
            );
            graphics.popStyle();
        }

        // Draw walls
        for (Wall wall : walls) {
            wall.display(this);
        }
    }

    /**
     * Record the current frame
     */
    private void recordFrame() {
        if (!recording) return;

        // Use high quality buffer for recording
        if (!highQualityRendering) {
            drawHighQuality();
        }

        // Save the current frame
        String frameFilename = String.format("%s/frames/frame_%05d.png", exportFolderPath, recordingCounter);
        renderBuffer.save(frameFilename);

        recordingCounter++;

        // Check if recording time limit reached
        float currentDuration = (millis() - recordingStartTime) / 1000.0f;
        if (currentDuration > 10) { // 10 seconds limit
            stopRecording();
        }
    }

    /**
     * Start recording frames
     */
    private void startRecording() {
        // Create a timestamped folder for this recording
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        exportFolderPath = sketchPath("export/recording_" + timestamp);

        File exportFolder = new File(exportFolderPath);
        if (!exportFolder.exists()) {
            exportFolder.mkdir();
        }

        // Create a frames subfolder
        File framesFolder = new File(exportFolderPath + "/frames");
        if (!framesFolder.exists()) {
            framesFolder.mkdir();
        }

        recordingCounter = 0;
        recording = true;
        recordingStartTime = millis();

        // Force high quality for recording
        highQualityRendering = true;

        // Save recording settings
        String[] settings = {
                "fps=" + frameRate,
                "width=" + renderBuffer.width,
                "height=" + renderBuffer.height
        };
        saveStrings(exportFolderPath + "/recording_info.txt", settings);
    }

    /**
     * Stop recording frames
     */
    private void stopRecording() {
        recording = false;

        // Create a text file with FFmpeg command for the user
        createFFmpegScript();

        System.out.println("Recording complete! " + recordingCounter + " frames saved to " + exportFolderPath);
    }

    /**
     * Create a script to convert frames to video
     */
    private void createFFmpegScript() {
        String ffmpegCommand = "ffmpeg -r " + frameRate + " -i frames/frame_%05d.png -c:v libx264 -crf 18 -pix_fmt yuv420p simulation_video.mp4";

        // Create appropriate script for the OS
        String[] scriptLines;
        String scriptName;

        if (platform == WINDOWS) {
            scriptLines = new String[] {
                    "@echo off",
                    "echo Converting frames to video...",
                    ffmpegCommand,
                    "echo Video created: simulation_video.mp4",
                    "pause"
            };
            scriptName = "create_video.bat";
        } else {
            scriptLines = new String[] {
                    "#!/bin/bash",
                    "echo Converting frames to video...",
                    ffmpegCommand,
                    "echo Video created: simulation_video.mp4"
            };
            scriptName = "create_video.sh";

            // Make the script executable on Unix systems
            try {
                File scriptFile = new File(exportFolderPath + "/" + scriptName);
                scriptFile.createNewFile();
                scriptFile.setExecutable(true);
            } catch (Exception e) {
                System.err.println("Error creating executable script: " + e.getMessage());
            }
        }

        saveStrings(exportFolderPath + "/" + scriptName, scriptLines);
    }

    /**
     * Take a screenshot of the current view
     */
    public void takeScreenshot() {
        // Create screenshots directory if needed
        File screenshotsDir = new File(sketchPath("screenshots"));
        if (!screenshotsDir.exists()) {
            screenshotsDir.mkdir();
        }

        // Save the current frame
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String filename = "screenshots/screenshot_" + timestamp + ".png";

        if (highQualityRendering) {
            renderBuffer.save(sketchPath(filename));
        } else {
            saveFrame(filename);
        }

        System.out.println("Screenshot saved: " + filename);
    }

    @Override
    public void keyPressed() {
        // Pass to GUI first
        guiManager.keyPressed();

        // Handle simulation keys
        switch (key) {
            case 'g':
                float currentGravity = physicsEngine.getGravity().y;
                updateGravity(currentGravity > 0 ? 0 : 0.2f);
                break;
            case 'r':
                resetBall();
                break;
            case 't':
                BallTraceEffect traceEffect = effectSystem.getEffect(BallTraceEffect.class);
                if (traceEffect != null) {
                    traceEffect.setEnabled(!traceEffect.isEnabled());
                }
                break;
            case 's':
                settings.setSoundEnabled(!settings.isSoundEnabled());
                System.out.println("Sound " + (settings.isSoundEnabled() ? "enabled" : "disabled"));
                break;
            case 'h':
                highQualityRendering = !highQualityRendering;
                if (highQualityRendering) {
                    createRenderBuffer();
                }
                break;
            case '1': case '2': case '3': case '0':
                int instrument = key == '0' ? 0 : (key - '1' + 1);
                settings.setBounceInstrument(instrument);
                System.out.println("Instrument set to " + instrument);
                break;
        }
    }

    @Override
    public void mousePressed() {
        // Only process mouse if GUI isn't handling it
        if (mouseX < guiManager.getSidebarWidth()) {
            return;
        }

        if (mouseButton == LEFT) {
            PVector mousePos = new PVector(mouseX, mouseY);
            PVector force = PVector.sub(mousePos, ball.getPosition());
            force.normalize().mult(2);
            ball.setVelocity(force);
        }
    }

    @Override
    public void dispose() {
        if (notePlayer != null) {
            notePlayer.dispose();
        }
        super.dispose();
    }

    //--------------------------------------------------------------------------------
    // Public methods for GUI to control simulation
    //--------------------------------------------------------------------------------

    /**
     * Reset the simulation
     */
    public void resetSimulation() {
        resetBall();
        clearAllTraces();
    }

    /**
     * Reset ball to initial state
     */
    public void resetBall() {
        PVector wallCenter = new PVector(width / 2f, height / 2f);
        ball.setPosition(new PVector(wallCenter.x, wallCenter.y - 50));
        ball.setVelocity(new PVector(0, 0));
        ball.setRadius(settings.getBallRadius());
    }

    /**
     * Apply physics settings
     */
    public void applyPhysicsSettings() {
        updateGravity(settings.getGravity());
        updateBallGrowth(settings.getGrowthAmount());
        updateSpeedBoost(settings.getSpeedBoostFactor());
        setMaxSizeStop(settings.getShouldStop());
        setMaxSizeShrink(settings.getShouldShrink(), settings.getShrinkRate());
        updateWallBoundaryLimit(settings.isEnforceWallBoundaryLimit());
    }

    /**
     * Apply ball settings
     */
    public void applyBallSettings(float radius, float mass, float stroke, int color) {
        // Update the ball
        ball.setRadius(radius);
        ball.setMass(mass);
        ball.setStrokeThickness(stroke);
        ball.setColor(color);
    }

    /**
     * Update gravity strength
     */
    public void updateGravity(float gravity) {
        physicsEngine.setGravity(gravity);
    }

    /**
     * Update ball growth amount
     */
    public void updateBallGrowth(float growthAmount) {
        BounceGrowthEffect effect = effectSystem.getEffect(BounceGrowthEffect.class);
        if (effect != null) {
            effect.setGrowthAmount(growthAmount);
        }
    }

    /**
     * Update speed boost factor
     */
    public void updateSpeedBoost(float boostFactor) {
        BounceSpeedBoostEffect effect = effectSystem.getEffect(BounceSpeedBoostEffect.class);
        if (effect != null) {
            effect.setBoostFactor(boostFactor);
        }
    }

    /**
     * Update max size radius
     */
    public void updateMaxSizeRadius(float radius) {
        maxSizeChecker.setWallRadius(radius);
    }

    /**
     * Set whether to stop at max size
     */
    public void setMaxSizeStop(boolean shouldStop) {
        MaxSizeStopEffect effect = effectSystem.getEffect(MaxSizeStopEffect.class);
        if (effect != null) {
            effect.setShouldStop(shouldStop);
        }
    }

    /**
     * Set whether to shrink at max size
     */
    public void setMaxSizeShrink(boolean shouldShrink, float shrinkRate) {
        MaxSizeStopEffect effect = effectSystem.getEffect(MaxSizeStopEffect.class);
        if (effect != null) {
            effect.setShouldShrink(shouldShrink);
            effect.setShrinkRate(shrinkRate);
        }
    }

    /**
     * Update shrink rate
     */
    public void updateShrinkRate(float shrinkRate) {
        MaxSizeStopEffect effect = effectSystem.getEffect(MaxSizeStopEffect.class);
        if (effect != null) {
            effect.setShrinkRate(shrinkRate);
        }
    }

    /**
     * Update wall boundary limit
     */
    public void updateWallBoundaryLimit(boolean enforce) {
        MaxSizeStopEffect effect = effectSystem.getEffect(MaxSizeStopEffect.class);
        if (effect != null) {
            effect.setEnforceWallBoundaryLimit(enforce);
        }
    }

    /**
     * Update trace frequency
     */
    public void updateTraceFrequency(float frequencyPerSecond) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setFrequency(frequencyPerSecond);
        }
    }

    /**
     * Update trace lifetime
     */
    public void updateTraceLifetime(int lifetimeFrames) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setTraceLifetimeFrames(lifetimeFrames);
        }
    }

    /**
     * Set permanent traces mode
     */
    public void setPermanentTraces(boolean permanent) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setPermanentTraces(permanent);
        }
    }

    /**
     * Set trace enabled state
     */
    public void setTraceEnabled(boolean enabled) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setEnabled(enabled);
        }
    }

    /**
     * Update trail thickness
     */
    public void updateTrailThickness(float thickness) {
        settings.setTrailThickness(thickness);
    }

    /**
     * Update trail thickness multiplier
     */
    public void updateTrailThicknessMultiplier(float multiplier) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setTrailThicknessMultiplier(multiplier);
        }
    }

    /**
     * Clear all traces
     */
    public void clearAllTraces() {
        // Implement this in BallTraceEffect
    }

    /**
     * Set ball max speed
     */
    public void updateBallMaxSpeed(float maxSpeed) {
        ball.setMaxSpeed(maxSpeed);
    }

    /**
     * Update audio settings
     */
    public void updateAudioSettings(boolean enabled, int instrument, boolean pitchModeRadius,
                                    boolean pitchModeVelocity, int basePitch, int pitchRange,
                                    float volume, int duration) {
        settings.setSoundEnabled(enabled);
        settings.setBounceInstrument(instrument);
        settings.setPitchModeRadius(pitchModeRadius);
        settings.setPitchModeVelocity(pitchModeVelocity);
        settings.setBasePitch(basePitch);
        settings.setPitchRange(pitchRange);
        settings.setNoteVolume(volume);
        settings.setNoteDuration(duration);

        BounceNoteEffect effect = effectSystem.getEffect(BounceNoteEffect.class);
        if (effect != null) {
            effect.setMaxBallRadius(maxSizeChecker.getWallRadius());
            effect.setMaxVelocity(settings.getBallMaxSpeed());
        }
    }

    /**
     * Enable/disable audio
     */
    public void updateAudioEnabled(boolean enabled) {
        settings.setSoundEnabled(enabled);
    }

    /**
     * Update audio instrument
     */
    public void updateAudioInstrument(int instrument) {
        settings.setBounceInstrument(instrument);
    }

    /**
     * Update audio volume
     */
    public void updateAudioVolume(float volume) {
        settings.setNoteVolume(volume);
    }

    /**
     * Update note duration
     */
    public void updateAudioNoteDuration(int duration) {
        settings.setNoteDuration(duration);
    }

    /**
     * Update pitch mode
     */
    public void updatePitchMode(boolean radiusMode, boolean velocityMode) {
        settings.setPitchModeRadius(radiusMode);
        settings.setPitchModeVelocity(velocityMode);
    }

    /**
     * Update base pitch
     */
    public void updateAudioBasePitch(int pitch) {
        settings.setBasePitch(pitch);
    }

    /**
     * Update pitch range
     */
    public void updateAudioPitchRange(int range) {
        settings.setPitchRange(range);
    }

    /**
     * Play test sound
     */
    public void playTestSound() {
        notePlayer.setInstrument(settings.getBounceInstrument());
        notePlayer.playNote(settings.getBasePitch(), settings.getNoteVolume(),
                settings.getNoteDuration());
    }

    /**
     * Update background brightness
     */
    public void updateBackgroundBrightness(int brightness) {
        backgroundBrightness = brightness;
    }

    /**
     * Enable/disable rainbow mode
     */
    public void setRainbowMode(boolean enabled) {
        rainbowMode = enabled;
    }

    /**
     * Update color cycling speed
     */
    public void updateColorSpeed(float speed) {
        colorSpeed = speed;
    }

    /**
     * Show/hide velocity vector
     */
    public void setShowVelocityVector(boolean show) {
        showVelocityVector = show;
    }

    /**
     * Set high quality rendering mode
     */
    public void setHighQualityRendering(boolean enabled) {
        highQualityRendering = enabled;
        if (highQualityRendering) {
            createRenderBuffer();
        }
    }

    /**
     * Set render scale
     */
    public void setRenderScale(int scale) {
        renderScale = scale;
        createRenderBuffer();
    }

    /**
     * Set antialiasing
     */
    public void setAntialiasing(boolean enabled) {
        useAntialiasing = enabled;
        createRenderBuffer();
    }

    /**
     * Set optimize traces option
     */
    public void setOptimizeTraces(boolean optimize) {
        // Implement trace optimization
    }

    /**
     * Toggle recording state
     */
    public void toggleRecording() {
        if (recording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    /**
     * Check if currently recording
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * Get current recording frame count
     */
    public int getRecordingFrames() {
        return recordingCounter;
    }

    /**
     * Get current recording duration in seconds
     */
    public float getRecordingDuration() {
        if (!recording) return 0;
        return (millis() - recordingStartTime) / 1000.0f;
    }

    /**
     * Set window size
     */
    public void setWindowSize(int width, int height) {
        needResize = true;
        newWidth = width;
        newHeight = height;
    }
}