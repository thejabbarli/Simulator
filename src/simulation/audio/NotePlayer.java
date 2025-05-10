package simulation.audio;

public interface NotePlayer {
    /**
     * Play a note with the specified pitch
     * @param pitch The pitch value (0-87 for 88 piano keys)
     * @param velocity How hard the note is played (0.0-1.0)
     * @param duration Duration in milliseconds
     */
    void playNote(int pitch, float velocity, int duration);

    /**
     * Set the instrument/sound to use
     * @param instrument The instrument ID or type
     */
    void setInstrument(int instrument);

    /**
     * Initialize the audio system
     */
    void initialize();

    /**
     * Clean up and release resources
     */
    void dispose();
}