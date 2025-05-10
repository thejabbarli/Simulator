package simulation.effects;

import simulation.core.Ball;

public interface BallEffect {
    void apply(Ball ball);
    boolean isEnabled();
    void setEnabled(boolean enabled);
}