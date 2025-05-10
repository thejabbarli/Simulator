package simulation.effects;

import java.util.ArrayList;
import java.util.List;
import simulation.core.Ball;

public class EffectSystem {
    private final List<BallEffect> effects = new ArrayList<>();

    public void registerEffect(BallEffect effect) {
        effects.add(effect);
    }

    public void unregisterEffect(BallEffect effect) {
        effects.remove(effect);
    }

    public void applyEffects(Ball ball) {
        for (BallEffect effect : effects) {
            if (effect.isEnabled()) {
                effect.apply(ball);
            }
        }
    }

    // Optional: retrieve specific effect by class type
    @SuppressWarnings("unchecked")
    public <T extends BallEffect> T getEffect(Class<T> effectClass) {
        for (BallEffect effect : effects) {
            if (effectClass.isInstance(effect)) {
                return (T) effect;
            }
        }
        return null;
    }
}