package simulation.audio;

import processing.core.PApplet;
import processing.sound.SinOsc;
import processing.sound.TriOsc;
import processing.sound.SawOsc;
import processing.sound.SqrOsc;

public class ProcessingNotePlayer implements NotePlayer {
    private final PApplet applet;
    private SinOsc sinOsc;
    private TriOsc triOsc;
    private SawOsc sawOsc;
    private SqrOsc sqrOsc;
    private int currentInstrument = 0;
    private boolean isInitialized = false;
    private boolean isPlaying = false;
    private Thread noteStopThread = null;

    // Constants for instrument selection
    public static final int SINE_WAVE = 0;
    public static final int TRIANGLE_WAVE = 1;
    public static final int SAW_WAVE = 2;
    public static final int SQUARE_WAVE = 3;

    public ProcessingNotePlayer(PApplet applet) {
        this.applet = applet;
    }

    @Override
    public void initialize() {
        if (isInitialized) return;

        try {
            // Initialize all oscillators
            sinOsc = new SinOsc(applet);
            triOsc = new TriOsc(applet);
            sawOsc = new SawOsc(applet);
            sqrOsc = new SqrOsc(applet);

            // Set initial amplitude to 0 (silent)
            sinOsc.amp(0);
            triOsc.amp(0);
            sawOsc.amp(0);
            sqrOsc.amp(0);

            isInitialized = true;
            System.out.println("🎵 Audio system initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing audio system: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void playNote(int pitch, float velocity, int duration) {
        if (!isInitialized) {
            System.err.println("⚠️ Audio system not initialized, trying to initialize...");
            initialize();
            if (!isInitialized) {
                System.err.println("❌ Failed to initialize audio system");
                return;
            }
        }

        // Stop any currently playing note
        stopNote();

        // Convert pitch (0-87) to frequency in Hz
        float frequency = NoteUtility.pitchToFrequency(pitch);

        try {
            // Set frequency and volume based on instrument
            switch (currentInstrument) {
                case SINE_WAVE:
                    sinOsc.freq(frequency);
                    sinOsc.amp(velocity);
                    sinOsc.play();
                    break;
                case TRIANGLE_WAVE:
                    triOsc.freq(frequency);
                    triOsc.amp(velocity);
                    triOsc.play();
                    break;
                case SAW_WAVE:
                    sawOsc.freq(frequency);
                    sawOsc.amp(velocity);
                    sawOsc.play();
                    break;
                case SQUARE_WAVE:
                    sqrOsc.freq(frequency);
                    sqrOsc.amp(velocity);
                    sqrOsc.play();
                    break;
            }

            isPlaying = true;

            // Schedule note stop using a separate thread
            if (noteStopThread != null && noteStopThread.isAlive()) {
                noteStopThread.interrupt();
            }

            noteStopThread = new Thread(() -> {
                try {
                    Thread.sleep(duration);
                    stopNote();
                } catch (InterruptedException e) {
                    // Thread was interrupted, probably to play a new note
                }
            });
            noteStopThread.start();

        } catch (Exception e) {
            System.err.println("❌ Error playing note: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopNote() {
        if (!isPlaying) return;

        try {
            switch (currentInstrument) {
                case SINE_WAVE:
                    sinOsc.stop();
                    break;
                case TRIANGLE_WAVE:
                    triOsc.stop();
                    break;
                case SAW_WAVE:
                    sawOsc.stop();
                    break;
                case SQUARE_WAVE:
                    sqrOsc.stop();
                    break;
            }
            isPlaying = false;
        } catch (Exception e) {
            System.err.println("❌ Error stopping note: " + e.getMessage());
        }
    }

    @Override
    public void setInstrument(int instrument) {
        if (instrument >= 0 && instrument <= 3) {
            this.currentInstrument = instrument;
        }
    }

    @Override
    public void dispose() {
        if (!isInitialized) return;

        try {
            stopNote();

            // Additional cleanup
            if (sinOsc != null) sinOsc.stop();
            if (triOsc != null) triOsc.stop();
            if (sawOsc != null) sawOsc.stop();
            if (sqrOsc != null) sqrOsc.stop();

            System.out.println("🎵 Audio system disposed");
        } catch (Exception e) {
            System.err.println("❌ Error disposing audio system: " + e.getMessage());
        }
    }
}