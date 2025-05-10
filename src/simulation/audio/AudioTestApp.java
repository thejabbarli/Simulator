package simulation.audio;

import processing.core.PApplet;

/**
 * Simple utility class to test if audio is working properly
 */
public class AudioTestApp extends PApplet {
    private NotePlayer notePlayer;
    private int currentInstrument = 0;
    private int octave = 4; // Middle C octave
    private boolean playing = false;

    public static void main(String[] args) {
        PApplet.main("simulation.audio.AudioTestApp");
    }

    @Override
    public void settings() {
        size(600, 400);
    }

    @Override
    public void setup() {
        background(50);
        textAlign(CENTER, CENTER);
        textSize(18);

        // Initialize audio system
        notePlayer = new ProcessingNotePlayer(this);
        notePlayer.initialize();
    }

    @Override
    public void draw() {
        background(50);

        // Display instructions
        fill(255);
        text("Audio Test Utility", width/2, 40);
        text("Press keys 0-9 or A-G to play notes", width/2, 80);
        text("Press UP/DOWN to change octave: " + octave, width/2, 120);
        text("Press 1-4 to change instrument: " + NoteUtility.getInstrumentName(currentInstrument), width/2, 160);

        // Draw keyboard
        drawKeyboard();
    }

    private void drawKeyboard() {
        int keyWidth = 30;
        int keyHeight = 100;
        int startX = width/2 - (7 * keyWidth / 2);
        int startY = height - 150;

        // White keys
        fill(255);
        stroke(0);
        for (int i = 0; i < 7; i++) {
            rect(startX + i * keyWidth, startY, keyWidth, keyHeight);

            // Note names (C, D, E, F, G, A, B)
            fill(0);
            text(noteNameForIndex(i), startX + i * keyWidth + keyWidth/2, startY + keyHeight - 20);
            fill(255);
        }

        // Black keys
        fill(0);
        for (int i = 0; i < 7; i++) {
            if (i != 2 && i != 6) { // No black keys after E and B
                rect(startX + i * keyWidth + keyWidth * 0.75f, startY, keyWidth * 0.5f, keyHeight * 0.6f);
            }
        }
    }

    private String noteNameForIndex(int index) {
        String[] notes = {"C", "D", "E", "F", "G", "A", "B"};
        return notes[index];
    }

    @Override
    public void keyPressed() {
        int pitch = -1;

        // Number keys play octave notes
        if (key >= '0' && key <= '9') {
            pitch = 12 * (octave + 1) + (key - '0'); // Map 0-9 to notes in the octave
        }

        // Letters A-G play specific notes
        if (key >= 'a' && key <= 'g') {
            int[] offsets = {9, 11, 0, 2, 4, 5, 7}; // A, B, C, D, E, F, G offsets from C
            pitch = 12 * octave + offsets[key - 'a'];
        }

        if (key == CODED) {
            if (keyCode == UP) {
                octave = min(8, octave + 1);
            } else if (keyCode == DOWN) {
                octave = max(0, octave - 1);
            }
        }

        // Instrument selection with number keys 1-4
        if (key == '1') currentInstrument = 0; // Sine
        if (key == '2') currentInstrument = 1; // Triangle
        if (key == '3') currentInstrument = 2; // Saw
        if (key == '4') currentInstrument = 3; // Square

        // Play the note
        if (pitch >= 0 && pitch <= 87 && !playing) {
            playing = true;
            notePlayer.setInstrument(currentInstrument);
            notePlayer.playNote(pitch, 0.5f, 300);

            // Print note info
            println("Playing " + NoteUtility.pitchToNoteName(pitch) +
                    " (pitch " + pitch + ") using " +
                    NoteUtility.getInstrumentName(currentInstrument));

            // Set a timer to reset the playing flag
            new Thread(() -> {
                try {
                    Thread.sleep(300);
                    playing = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void dispose() {
        if (notePlayer != null) {
            notePlayer.dispose();
        }
        super.dispose();
    }
}