package simulation.config;

import simulation.core.Ball;
import simulation.effects.*;

public class SimulationController {

    private final SettingsManager settings;

    private final Ball ball;
    private final BounceGrowthEffect growthEffect;
    private final BounceSpeedBoostEffect speedBoostEffect;
    private final MaxSizeStopEffect maxSizeStopEffect;
    private final BallTraceEffect traceEffect;

    public SimulationController(SettingsManager settings,
                                Ball ball,
                                BounceGrowthEffect growthEffect,
                                BounceSpeedBoostEffect speedBoostEffect,
                                MaxSizeStopEffect maxSizeStopEffect,
                                BallTraceEffect traceEffect) {

        this.settings = settings;
        this.ball = ball;
        this.growthEffect = growthEffect;
        this.speedBoostEffect = speedBoostEffect;
        this.maxSizeStopEffect = maxSizeStopEffect;
        this.traceEffect = traceEffect;
    }

    // Simulate GUI updating values
    public void applyAllSettings() {
        // Ball
        ball.setRadius(settings.getBallRadius());
        ball.setStrokeThickness(settings.getBallStroke());
        ball.setMass(settings.getBallMass());
        ball.setColor(settings.getBallColor());
        ball.setMaxSpeed(settings.getBallMaxSpeed());

        // Effects
        growthEffect.setGrowthAmount(settings.getGrowthAmount());
        speedBoostEffect.setBoostFactor(settings.getSpeedBoostFactor());

        maxSizeStopEffect.setMaxRadius(settings.getMaxSizeRadius());
        maxSizeStopEffect.setShouldStop(settings.getShouldStop());
        maxSizeStopEffect.setShouldShrink(settings.getShouldShrink());
        maxSizeStopEffect.setShrinkRate(settings.getShrinkRate());

        traceEffect.setFrequency(settings.getTraceFrequency());
        traceEffect.setTraceLifetimeFrames(settings.getTraceLifetimeFrames());
        traceEffect.setPermanentTraces(settings.getPermanentTraces());
    }
}
