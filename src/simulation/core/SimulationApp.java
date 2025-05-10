package simulation.core;

import processing.core.PApplet;
import processing.core.PVector;
import simulation.config.SettingsManager;
import simulation.effects.*;
import simulation.rendering.BallRenderer;

import java.util.ArrayList;
import java.util.List;

public class SimulationApp extends PApplet {

    // Configuration
    private SettingsManager settings;

    // Core simulation objects
    private Ball ball;
    private BallRenderer ballRenderer;
    private List<Wall> walls;
    private List<Collidable> collidables;
    private PhysicsEngine physicsEngine;

    // Effect system
    private EffectSystem effectSystem;
    private MaxSizeChecker maxSizeChecker;

    // Window configuration
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

        // Initialize core components
        initializeSettings();
        initializeSimulationComponents();
        initializeEffectSystem();
    }

    private void initializeSettings() {
        // Load settings - this would be expanded for GUI integration
        settings = new SettingsManager();
    }

    private void initializeSimulationComponents() {
        // Setup wall parameters
        float wallRadius = 200;
        float wallThickness = 10;
        float elasticity = 1.0f;
        PVector wallCenter = new PVector(width / 2f, height / 2f);

        // Create ball
        PVector ballPosition = new PVector(wallCenter.x + 30, wallCenter.y - wallRadius / 2);
        ball = createBall(ballPosition);

        // Create ball renderer
        ballRenderer = createBallRenderer();

        // Create walls and collidables
        walls = createWalls(wallCenter, wallRadius, wallThickness, elasticity);
        collidables = new ArrayList<>(walls);

        // Create physics engine
        physicsEngine = new PhysicsEngine(settings.getGravity());

        // Create size checker
        maxSizeChecker = new MaxSizeChecker(wallRadius, wallThickness);
    }

    private Ball createBall(PVector ballPosition) {
        Ball newBall = new Ball(
                ballPosition,
                settings.getBallRadius(),
                settings.getBallMass()
        );
        newBall.setMaxSpeed(settings.getBallMaxSpeed());
        return newBall;
    }

    private BallRenderer createBallRenderer() {
        BallRenderer renderer = new BallRenderer(settings.getBallColor());
        renderer.setStrokeThickness(settings.getBallStroke());
        return renderer;
    }

    private List<Wall> createWalls(PVector wallCenter, float wallRadius,
                                   float wallThickness, float elasticity) {
        List<Wall> wallList = new ArrayList<>();
        wallList.add(new CircularWall(wallCenter, wallRadius, wallThickness, elasticity));
        return wallList;
    }

    private void initializeEffectSystem() {
        effectSystem = new EffectSystem();

        // Register growth effect
        BounceGrowthEffect bounceGrowth = new BounceGrowthEffect(
                settings.getGrowthAmount(),
                maxSizeChecker
        );
        effectSystem.registerEffect(bounceGrowth);

        // Register speed boost effect
        BounceSpeedBoostEffect bounceSpeedBoost = new BounceSpeedBoostEffect(
                settings.getSpeedBoostFactor()
        );
        effectSystem.registerEffect(bounceSpeedBoost);

        // Register max size effect
        PVector wallCenter = new PVector(width / 2f, height / 2f);
        MaxSizeStopEffect maxSizeStop = new MaxSizeStopEffect(
                wallCenter,
                maxSizeChecker,
                settings.getGrowthAmount(),
                settings.getShouldStop(),
                settings.getShouldShrink(),
                settings.getShrinkRate()
        );
        effectSystem.registerEffect(maxSizeStop);

        // Register trace effect with default thickness
        BallTraceEffect ballTrace = new BallTraceEffect(
                settings.getTraceFrequency(),
                settings.getTraceLifetimeFrames(),
                settings.getPermanentTraces(),
                TARGET_FRAMERATE,
                this  // Pass the PApplet instance
        );
        effectSystem.registerEffect(ballTrace);
    }

    public void updateTrailThickness(float multiplier) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setTrailThicknessMultiplier(multiplier);
        }
    }

    // In SimulationApp class
    /*public void updateTrailThickness(float thickness) {
        BallTraceEffect effect = effectSystem.getEffect(BallTraceEffect.class);
        if (effect != null) {
            effect.setTrailThickness(thickness);
        }
    }*/




    @Override
    public void draw() {
        // Clear background
        background(0);

        // Update physics
        updatePhysics();

        // Apply all effects
        effectSystem.applyEffects(ball);

        // Render scene
        renderScene();
    }

    private void updatePhysics() {
        physicsEngine.update(ball, collidables);
    }

    private void renderScene() {
        // Render traces first (behind everything else)
        BallTraceEffect traceEffect = effectSystem.getEffect(BallTraceEffect.class);
        if (traceEffect != null) {
            traceEffect.display(this);
        }

        // Render ball
        ballRenderer.display(ball, this);

        // Render walls
        for (Wall wall : walls) {
            wall.display(this);
        }
    }

    // Methods to support runtime configuration changes - these would be connected to GUI

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

    // Keyboard/mouse handlers could be added here to provide interactive control
    // before the GUI is implemented

    @Override
    public void keyPressed() {
        // Example keyboard controls - these could be expanded or customized
        switch (key) {
            case 'g':
                // Toggle gravity
                float currentGravity = physicsEngine.getGravity().y;
                updateGravity(currentGravity > 0 ? 0 : 0.2f);
                break;
            case 'r':
                // Reset ball
                PVector wallCenter = new PVector(width / 2f, height / 2f);
                ball.setPosition(new PVector(wallCenter.x, wallCenter.y - 50));
                ball.setVelocity(new PVector(0, 0));
                ball.setRadius(settings.getBallRadius());
                break;
            case 't':
                // Toggle traces
                BallTraceEffect traceEffect = effectSystem.getEffect(BallTraceEffect.class);
                if (traceEffect != null) {
                    traceEffect.setEnabled(!traceEffect.isEnabled());
                }
                break;
        }
    }

    @Override
    public void mousePressed() {
        // Give the ball a push in the direction of mouse click
        if (mouseButton == LEFT) {
            PVector mousePos = new PVector(mouseX, mouseY);
            PVector force = PVector.sub(mousePos, ball.getPosition());
            force.normalize().mult(2); // Adjust multiplier for force strength
            ball.setVelocity(force);
        }
    }
}