package simulation.core;

import processing.core.PApplet;
import processing.core.PVector;

import simulation.effects.BounceGrowthEffect;
import simulation.effects.BounceSpeedBoostEffect;
import simulation.effects.MaxSizeStopEffect;

import java.util.ArrayList;
import java.util.List;

public class SimulationApp extends PApplet {

    private BounceGrowthEffect bounceGrowth;
    private BounceSpeedBoostEffect bounceSpeedBoost;
    private MaxSizeStopEffect maxSizeStopEffect;

    private Ball ball;
    private List<Wall> walls;
    private List<Collidable> collidables;
    private PhysicsEngine physicsEngine;

    public static void main(String[] args) {
        PApplet.main("simulation.core.SimulationApp");
    }

    @Override
    public void settings() {
        size(1000, 800);  // ✅ Increased screen size
    }

    @Override
    public void setup() {
        background(0);
        frameRate(60);

        // Ball properties
        float ballRadius = 30;
        float ballStroke = 5;

        // Wall properties
        float wallRadius = 200;
        float wallThickness = 10;
        float elasticity = 1.0f;

        // ✅ Perfectly centered wall
        PVector wallCenter = new PVector(width / 2f, height / 2f);

        // ✅ Ball slightly off-centered (to cause bounce)
        PVector ballPosition = new PVector(wallCenter.x + 4, wallCenter.y);

        ball = new Ball(ballPosition, ballRadius, 1.0f, color(255, 0, 255));
        ball.setMaxSpeed(20);

        walls = new ArrayList<>();
        walls.add(new CircularWall(wallCenter, wallRadius, wallThickness, elasticity));

        collidables = new ArrayList<>(walls);
        physicsEngine = new PhysicsEngine(0.5f);

        // Modular effects
        bounceGrowth = new BounceGrowthEffect();
        bounceSpeedBoost = new BounceSpeedBoostEffect(1.05f);
        maxSizeStopEffect = new MaxSizeStopEffect(
                250,     // Max radius to stop or shrink
                true,    // Should stop on max size
                true,    // Should shrink
                0.5f     // Shrink rate
        );
    }

    @Override
    public void draw() {
        background(0);

        // Core physics
        physicsEngine.update(ball, collidables);

        // Apply modular behaviors
        for (Collidable c : collidables) {
            bounceGrowth.apply(ball, c);
            bounceSpeedBoost.apply(ball, c);
        }

        // Collision-aware max size logic
        CircularWall cw = (CircularWall) walls.get(0);
        maxSizeStopEffect.apply(ball, cw.getRadius(), cw.getThickness());

        // Render
        ball.display(this);
        for (Wall wall : walls) {
            wall.display(this);
        }
    }
}
