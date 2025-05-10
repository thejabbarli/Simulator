package simulation.audio;

/**
 * Utility class for musical note operations
 */
public class NoteUtility {

    private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    /**
     * Converts a MIDI pitch value (0-87) to a human-readable note name with octave
     * @param pitch The MIDI pitch value (0-87)
     * @return A string like "A4" for middle A (pitch 48)
     */
    public static String pitchToNoteName(int pitch) {
        if (pitch < 0 || pitch > 87) {
            return "Invalid";
        }

        // MIDI pitch 0 is C-1, 12 is C0, etc.
        int octave = (pitch / 12) - 1;
        int noteIndex = pitch % 12;

        return NOTE_NAMES[noteIndex] + octave;
    }

    /**
     * Converts a MIDI pitch to its frequency in Hz
     * @param pitch The MIDI pitch value (0-87)
     * @return The frequency in Hz
     */
    public static float pitchToFrequency(int pitch) {
        // A4 (pitch 48) = 440Hz
        return (float) (440.0 * Math.pow(2, (pitch - 48) / 12.0));
    }

    /**
     * Returns a descriptive name for the instrument
     * @param instrument The instrument ID
     * @return A string describing the waveform
     */
    public static String getInstrumentName(int instrument) {
        switch(instrument) {
            case 0: return "Sine Wave";
            case 1: return "Triangle Wave";
            case 2: return "Saw Wave";
            case 3: return "Square Wave";
            default: return "Unknown";
        }
    }
}