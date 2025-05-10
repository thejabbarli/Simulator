package simulation.core;

import processing.core.PApplet;
import processing.core.PVector;

import simulation.config.SettingsManager;
import simulation.config.SimulationController;
import simulation.effects.BounceGrowthEffect;
import simulation.effects.BounceSpeedBoostEffect;
import simulation.effects.MaxSizeStopEffect;
import simulation.effects.BallTraceEffect;

import java.util.ArrayList;
import java.util.List;

public class SimulationApp extends PApplet {

    private SettingsManager settings;
    private SimulationController controller;

    private Ball ball;
    private List<Wall> walls;
    private List<Collidable> collidables;
    private PhysicsEngine physicsEngine;

    private BounceGrowthEffect bounceGrowth;
    private BounceSpeedBoostEffect bounceSpeedBoost;
    private MaxSizeStopEffect maxSizeStopEffect;
    private BallTraceEffect ballTraceEffect;

    public static void main(String[] args) {
        PApplet.main("simulation.core.SimulationApp");
    }

    @Override
    public void settings() {
        size(1000, 800);
    }

    @Override
    public void setup() {
        background(0);
        frameRate(60);

        // Load settings
        settings = new SettingsManager();

        // Wall setup
        float wallRadius = 200;
        float wallThickness = 10;
        float elasticity = 1.0f;
        PVector wallCenter = new PVector(width / 2f, height / 2f);

        // Ball setup
        PVector ballPosition = new PVector(wallCenter.x + 4, wallCenter.y);
        ball = new Ball(
                ballPosition,
                settings.getBallRadius(),
                settings.getBallMass(),
                settings.getBallColor()
        );
        ball.setStrokeThickness(settings.getBallStroke());
        ball.setMaxSpeed(settings.getBallMaxSpeed());

        // Walls
        walls = new ArrayList<>();
        walls.add(new CircularWall(wallCenter, wallRadius, wallThickness, elasticity));

        // Collidables
        collidables = new ArrayList<>(walls);

        // Physics engine
        physicsEngine = new PhysicsEngine(settings.getGravity());

        // Max size limiter
        maxSizeStopEffect = new MaxSizeStopEffect(
                wallRadius,
                wallThickness,
                settings.getShouldStop(),
                settings.getShouldShrink(),
                settings.getShrinkRate(),
                settings.isEnforceWallBoundaryLimit()
        );

        // Effects
        bounceGrowth = new BounceGrowthEffect(
                settings.getGrowthAmount(),
                maxSizeStopEffect
        );

        bounceSpeedBoost = new BounceSpeedBoostEffect(settings.getSpeedBoostFactor());

        ballTraceEffect = new BallTraceEffect(
                settings.getTraceFrequency(),
                settings.getTraceLifetimeFrames(),
                settings.getPermanentTraces(),
                frameRate
        );

        // Controller
        controller = new SimulationController(
                settings,
                ball,
                bounceGrowth,
                bounceSpeedBoost,
                maxSizeStopEffect,
                ballTraceEffect
        );
    }


    @Override
    public void draw() {
        background(0);

        // Optional: live GUI update hook
        controller.applyAllSettings();

        // Physics update
        physicsEngine.update(ball, collidables);

        // Apply effects
        for (Collidable c : collidables) {
            bounceGrowth.apply(ball, c);
            bounceSpeedBoost.apply(ball, c);
        }

        CircularWall cw = (CircularWall) walls.get(0);
        maxSizeStopEffect.apply(ball);

        // Trace effect
        ballTraceEffect.update(ball, this);
        ballTraceEffect.display(this);

        // Render
        ball.display(this);
        for (Wall wall : walls) {
            wall.display(this);
        }
    }
}
