package simulation.core;

public class BallStateManager {
    private boolean justBounced = false;
    private boolean locked = false;

    public void markBounce() {
        this.justBounced = true;
    }

    public boolean hasJustBounced() {
        return justBounced;
    }

    public void resetBounceFlag() {
        this.justBounced = false;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }
}
