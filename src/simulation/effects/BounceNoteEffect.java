package simulation.effects;

import simulation.audio.NotePlayer;
import simulation.audio.NoteUtility;
import simulation.core.Ball;
import simulation.config.SettingsManager;

public class BounceNoteEffect implements BallEffect {
    private final NotePlayer notePlayer;
    private final SettingsManager settings;
    private boolean enabled = true;
    private float maxBallRadius;
    private float maxVelocity;
    private boolean debug = false;

    public BounceNoteEffect(NotePlayer notePlayer, SettingsManager settings, float maxBallRadius, float maxVelocity) {
        this.notePlayer = notePlayer;
        this.settings = settings;
        this.maxBallRadius = maxBallRadius;
        this.maxVelocity = maxVelocity;
    }

    @Override
    public void apply(Ball ball) {
        if (!enabled || !settings.isSoundEnabled()) {
            return;
        }

        // Only play sound on actual bounce events
        if (ball.hasJustBounced()) {
            int pitch = calculatePitch(ball);
            float velocity = settings.getNoteVolume();
            int duration = settings.getNoteDuration();

            if (debug) {
                System.out.printf("🎵 Playing note %s (pitch %d) | Instrument: %s | Volume: %.2f | Duration: %dms\n",
                        NoteUtility.pitchToNoteName(pitch), pitch,
                        NoteUtility.getInstrumentName(settings.getBounceInstrument()),
                        velocity, duration);
            }

            notePlayer.setInstrument(settings.getBounceInstrument());
            notePlayer.playNote(pitch, velocity, duration);
        }
    }

    /**
     * Calculate the pitch based on the selected mode (radius or velocity)
     */
    private int calculatePitch(Ball ball) {
        int basePitch = settings.getBasePitch();
        int range = settings.getPitchRange();

        // Default pitch (if no modes are enabled)
        int pitch = basePitch;

        if (settings.isPitchModeRadius()) {
            // Map radius to pitch: larger radius = lower pitch
            float normalizedRadius = Math.min(1.0f, ball.getRadius() / maxBallRadius);
            pitch = basePitch - Math.round(normalizedRadius * range);

            if (debug) {
                System.out.printf("Radius-based pitch: ball radius=%.2f, max=%.2f, normalized=%.2f\n",
                        ball.getRadius(), maxBallRadius, normalizedRadius);
            }
        } else if (settings.isPitchModeVelocity()) {
            // Map velocity magnitude to pitch: faster = higher pitch
            float speed = ball.getVelocity().mag();
            float normalizedSpeed = Math.min(1.0f, speed / maxVelocity);
            pitch = basePitch + Math.round(normalizedSpeed * range);

            if (debug) {
                System.out.printf("Velocity-based pitch: speed=%.2f, max=%.2f, normalized=%.2f\n",
                        speed, maxVelocity, normalizedSpeed);
            }
        }

        // Ensure the pitch is within 0-87 range (88 keys)
        return Math.max(0, Math.min(87, pitch));
    }

    public void setMaxBallRadius(float maxRadius) {
        this.maxBallRadius = maxRadius;
    }

    public void setMaxVelocity(float maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}