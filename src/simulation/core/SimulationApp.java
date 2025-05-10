package simulation.core;

import processing.core.PApplet;
import processing.core.PVector;
import simulation.config.SettingsManager;
import simulation.effects.*;
import simulation.rendering.BallRenderer;
import simulation.audio.NotePlayer;
import simulation.audio.ProcessingNotePlayer;

import java.util.ArrayList;
import java.util.List;

public class SimulationApp extends PApplet {

    private SettingsManager settings;
    private Ball ball;
    private BallRenderer ballRenderer;
    private List<Wall> walls;
    private List<Collidable> collidables;
    private PhysicsEngine physicsEngine;
    private EffectSystem effectSystem;
    private MaxSizeChecker maxSizeChecker;
    private NotePlayer notePlayer;

    private final int WINDOW_WIDTH = 1000;
    private final int WINDOW_HEIGHT = 800;
    private final int TARGET_FRAMERATE = 60;

    public static void main(String[] args) {
        PApplet.main("simulation.core.SimulationApp");
    }

    @Override
    public void settings() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public void setup() {
        background(0);
        frameRate(TARGET_FRAMERATE);
        initializeSettings();
        initializeSimulationComponents();
        initializeEffectSystem();
    }

    private void initializeSettings() {
        settings = new SettingsManager();
    }

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

    private Ball createBall(PVector ballPosition) {
        Ball newBall = new Ball(ballPosition, settings.getBallRadius(), settings.getBallMass());
        newBall.setMaxSpeed(settings.getBallMaxSpeed());
        return newBall;
    }

    private BallRenderer createBallRenderer() {
        BallRenderer renderer = new BallRenderer(settings.getBallColor());
        renderer.setStrokeThickness(settings.getBallStroke());
        return renderer;
    }

    private List<Wall> createWalls(PVector wallCenter, float wallRadius, float wallThickness, float elasticity) {
        List<Wall> wallList = new ArrayList<>();
        wallList.add(new CircularWall(wallCenter, wallRadius, wallThickness, elasticity));
        return wallList;
    }

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

    public void updateTrailThickness(float multiplier) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setTrailThicknessMultiplier(multiplier);
        }
    }

    @Override
    public void draw() {
        background(0);
        updatePhysics();
        effectSystem.applyEffects(ball);
        renderScene();
    }

    private void updatePhysics() {
        physicsEngine.update(ball, collidables);
    }

    private void renderScene() {
        BallTraceEffect traceEffect = effectSystem.getEffect(BallTraceEffect.class);
        if (traceEffect != null) {
            traceEffect.display(this);
        }

        ballRenderer.display(ball, this);
        for (Wall wall : walls) {
            wall.display(this);
        }
    }

    public void updateGravity(float gravity) {
        physicsEngine.setGravity(gravity);
    }

    public void updateBallGrowth(float growthAmount) {
        BounceGrowthEffect effect = effectSystem.getEffect(BounceGrowthEffect.class);
        if (effect != null) {
            effect.setGrowthAmount(growthAmount);
        }
    }

    public void updateSpeedBoost(float boostFactor) {
        BounceSpeedBoostEffect effect = effectSystem.getEffect(BounceSpeedBoostEffect.class);
        if (effect != null) {
            effect.setBoostFactor(boostFactor);
        }
    }

    public void updateTraceFrequency(float frequencyPerSecond) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setFrequency(frequencyPerSecond);
        }
    }

    public void updateTraceLifetime(int lifetimeFrames) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setTraceLifetimeFrames(lifetimeFrames);
        }
    }

    public void setPermanentTraces(boolean permanent) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setPermanentTraces(permanent);
        }
    }

    public void setMaxSizeStop(boolean shouldStop) {
        MaxSizeStopEffect effect = effectSystem.getEffect(MaxSizeStopEffect.class);
        if (effect != null) {
            effect.setShouldStop(shouldStop);
        }
    }

    public void setMaxSizeShrink(boolean shouldShrink, float shrinkRate) {
        MaxSizeStopEffect effect = effectSystem.getEffect(MaxSizeStopEffect.class);
        if (effect != null) {
            effect.setShouldShrink(shouldShrink);
            effect.setShrinkRate(shrinkRate);
        }
    }

    @Override
    public void keyPressed() {
        switch (key) {
            case 'g':
                float currentGravity = physicsEngine.getGravity().y;
                updateGravity(currentGravity > 0 ? 0 : 0.2f);
                break;
            case 'r':
                PVector wallCenter = new PVector(width / 2f, height / 2f);
                ball.setPosition(new PVector(wallCenter.x, wallCenter.y - 50));
                ball.setVelocity(new PVector(0, 0));
                ball.setRadius(settings.getBallRadius());
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
            case '1': case '2': case '3': case '0':
                int instrument = key == '0' ? 0 : (key - '1' + 1);
                settings.setBounceInstrument(instrument);
                System.out.println("Instrument set to " + instrument);
                break;
        }
    }

    @Override
    public void mousePressed() {
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
}
