package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PVector;
import simulation.audio.NoteUtility;
import simulation.config.SettingsManager;
import simulation.core.SimulationApp;

/**
 * Panel for audio-related settings
 */
public class AudioSettingsPanel extends SettingsPanel {
    private Group audioGroup;
    private Group pitchGroup;

    private int[] pianoKeysMidi = new int[12]; // For visualization
    private boolean[] keyPressed = new boolean[12];
    private int[] noteMapping = new int[88]; // Store the pitch mapping for visualization

    /**
     * Create a new audio settings panel
     */
    public AudioSettingsPanel(PApplet applet, ControlP5 cp5, SettingsManager settings,
                              SimulationApp simulationApp, Tab parentTab,
                              int sidebarWidth, int margin) {
        super(applet, cp5, settings, simulationApp, parentTab, sidebarWidth, margin);

        // Initialize piano keys
        for (int i = 0; i < pianoKeysMidi.length; i++) {
            pianoKeysMidi[i] = settings.getBasePitch() + i;
        }

        // Initialize note mapping
        updateNoteMapping();
    }

    @Override
    protected void initializePanel() {
        int panelContentWidth = applet.width - sidebarWidth - (margin * 2);

        // Audio Group
        audioGroup = createControlGroup(
                "audioGroup",
                "Audio Properties",
                50,
                panelContentWidth / 2 - margin,
                220
        );

        // Pitch Control Group
        pitchGroup = createControlGroup(
                "pitchGroup",
                "Pitch Control",
                50,
                panelContentWidth / 2 - margin,
                220
        );
        pitchGroup.setPosition(
                sidebarWidth + margin + panelContentWidth / 2,
                50
        );

        // Audio enabled toggle
        createToggle("soundEnabled", "Sound Enabled", settings.isSoundEnabled(),
                audioGroup, margin, 30)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    settings.setSoundEnabled(c.getValue() > 0.5f);
                    simulationApp.updateAudioEnabled(c.getValue() > 0.5f);
                });


        // Instrument selection
        String[] instruments = {"Sine Wave", "Triangle Wave", "Saw Wave", "Square Wave"};
        createDropdown("bounceInstrument", "Instrument", instruments,
                settings.getBounceInstrument(), audioGroup, margin, 70)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    int instrument = (int) c.getValue();
                    settings.setBounceInstrument(instrument);
                    simulationApp.updateAudioInstrument(instrument);
                });


        // Volume slider
        createSlider("noteVolume", "Volume", 0, 1, settings.getNoteVolume(),
                audioGroup, margin, 110)
                .onChange(event -> {
                    settings.setNoteVolume(event.getController().getValue());
                    simulationApp.updateAudioVolume(event.getController().getValue());
                });

        // Note duration slider
        createSlider("noteDuration", "Note Duration (ms)", 50, 500, settings.getNoteDuration(),
                audioGroup, margin, 150)
                .onChange(event -> {
                    settings.setNoteDuration((int) event.getController().getValue());
                    simulationApp.updateAudioNoteDuration((int) event.getController().getValue());
                });

        // Pitch mode toggles
        createToggle("pitchModeRadius", "Pitch by Radius", settings.isPitchModeRadius(),
                pitchGroup, margin, 30)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    settings.setPitchModeRadius(c.getValue() > 0.5f);

                    // If radius mode enabled, disable velocity mode
                    if (c.getValue() > 0.5f) {
                        settings.setPitchModeVelocity(false);
                        cp5.getController("pitchModeVelocity").setValue(0);
                    }

                    simulationApp.updatePitchMode(
                            settings.isPitchModeRadius(),
                            settings.isPitchModeVelocity()
                    );

                    updateNoteMapping();
                });

        createToggle("pitchModeVelocity", "Pitch by Velocity", settings.isPitchModeVelocity(),
                pitchGroup, margin, 70)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    settings.setPitchModeVelocity(c.getValue() > 0.5f);

                    // If velocity mode enabled, disable radius mode
                    if (c.getValue() > 0.5f) {
                        settings.setPitchModeRadius(false);
                        cp5.getController("pitchModeRadius").setValue(0);
                    }

                    simulationApp.updatePitchMode(
                            settings.isPitchModeRadius(),
                            settings.isPitchModeVelocity()
                    );

                    updateNoteMapping();
                });

        // Base pitch slider
        createSlider("basePitch", "Base Pitch", 12, 72, settings.getBasePitch(),
                pitchGroup, margin, 110)
                .onChange(event -> {
                    settings.setBasePitch((int) event.getController().getValue());
                    simulationApp.updateAudioBasePitch((int) event.getController().getValue());
                    updatePianoKeys();
                    updateNoteMapping();
                });

        // Pitch range slider
        createSlider("pitchRange", "Pitch Range", 1, 48, settings.getPitchRange(),
                pitchGroup, margin, 150)
                .onChange(event -> {
                    settings.setPitchRange((int) event.getController().getValue());
                    simulationApp.updateAudioPitchRange((int) event.getController().getValue());
                    updateNoteMapping();
                });

        // Test sound button
        createButton("testSound", "Test Sound", null,
                sidebarWidth + margin, 300, 150, 40)
                .onPress(event -> {
                    simulationApp.playTestSound();
                });

        // Reset audio button
        createButton("resetAudio", "Reset Audio", null,
                sidebarWidth + margin + 170, 300, 150, 40)
                .onPress(event -> {
                    resetAudioSettings();
                });
    }

    private void updatePianoKeys() {
        for (int i = 0; i < pianoKeysMidi.length; i++) {
            pianoKeysMidi[i] = settings.getBasePitch() + i;
        }
    }

    private void updateNoteMapping() {
        for (int i = 0; i < noteMapping.length; i++) {
            if (settings.isPitchModeRadius()) {
                // Map radius to pitch (larger radius = lower pitch)
                float normalizedRadius = i / (float) noteMapping.length;
                noteMapping[i] = settings.getBasePitch() - Math.round(normalizedRadius * settings.getPitchRange());
            } else if (settings.isPitchModeVelocity()) {
                // Map velocity to pitch (higher velocity = higher pitch)
                float normalizedVelocity = i / (float) noteMapping.length;
                noteMapping[i] = settings.getBasePitch() + Math.round(normalizedVelocity * settings.getPitchRange());
            } else {
                // Default mapping (centered around base pitch)
                noteMapping[i] = settings.getBasePitch();
            }

            // Ensure within valid range (0-87)
            noteMapping[i] = Math.max(0, Math.min(87, noteMapping[i]));
        }
    }

    @Override
    public void draw() {
        if (!isVisible()) return;

        // Draw panel header
        applet.fill(COLOR_HEADER);
        applet.noStroke();
        applet.rect(sidebarWidth, 0, applet.width - sidebarWidth, 40);

        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(18);
        applet.text("Audio Settings", sidebarWidth + (applet.width - sidebarWidth) / 2, 20);

        // Draw piano keyboard visualization
        drawPianoVisualization();

        // Draw pitch mapping visualization
        drawPitchMappingVisualization();
    }

    private void drawPianoVisualization() {
        int pianoX = sidebarWidth + margin;
        int pianoY = 360;
        int whiteKeyWidth = 24;
        int whiteKeyHeight = 100;
        int blackKeyWidth = 16;
        int blackKeyHeight = 60;

        // Background for piano area
        applet.fill(COLOR_BACKGROUND);
        applet.stroke(COLOR_HEADER);
        applet.strokeWeight(2);
        int pianoWidth = whiteKeyWidth * 7 + margin * 2;
        applet.rect(pianoX, pianoY, pianoWidth, whiteKeyHeight + margin * 2);

        // Draw label
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(14);
        applet.text("Base Pitch: " + NoteUtility.pitchToNoteName(settings.getBasePitch()),
                pianoX + pianoWidth / 2, pianoY - 15);

        // Draw white keys
        applet.noStroke();
        for (int i = 0; i < 7; i++) {
            int noteIndex = 0;
            switch (i) {
                case 0: noteIndex = 0; break; // C
                case 1: noteIndex = 2; break; // D
                case 2: noteIndex = 4; break; // E
                case 3: noteIndex = 5; break; // F
                case 4: noteIndex = 7; break; // G
                case 5: noteIndex = 9; break; // A
                case 6: noteIndex = 11; break; // B
            }

            // Check if key is active in our current pitch range
            boolean isActive = pianoKeysMidi[noteIndex] >= 0 &&
                    pianoKeysMidi[noteIndex] <= 87;

            // Highlight key if in base pitch range
            if (isActive) {
                applet.fill(255);
                if (keyPressed[noteIndex]) {
                    applet.fill(200, 230, 255);
                }
            } else {
                applet.fill(150);
            }

            applet.rect(pianoX + margin + i * whiteKeyWidth, pianoY + margin,
                    whiteKeyWidth - 1, whiteKeyHeight);

            // Draw note name
            applet.fill(0);
            applet.textAlign(PApplet.CENTER, PApplet.BOTTOM);
            applet.textSize(10);
            String[] noteNames = {"C", "D", "E", "F", "G", "A", "B"};
            applet.text(noteNames[i],
                    pianoX + margin + i * whiteKeyWidth + whiteKeyWidth/2,
                    pianoY + margin + whiteKeyHeight - 5);

            // Show MIDI note number
            if (isActive) {
                applet.textSize(8);
                applet.text(pianoKeysMidi[noteIndex] + "",
                        pianoX + margin + i * whiteKeyWidth + whiteKeyWidth/2,
                        pianoY + margin + whiteKeyHeight - 18);
            }
        }

        // Draw black keys
        for (int i = 0; i < 7; i++) {
            if (i != 2 && i != 6) { // No black keys after E and B
                int noteIndex = 0;
                switch (i) {
                    case 0: noteIndex = 1; break; // C#
                    case 1: noteIndex = 3; break; // D#
                    case 3: noteIndex = 6; break; // F#
                    case 4: noteIndex = 8; break; // G#
                    case 5: noteIndex = 10; break; // A#
                }

                // Check if key is active
                boolean isActive = pianoKeysMidi[noteIndex] >= 0 &&
                        pianoKeysMidi[noteIndex] <= 87;

                if (isActive) {
                    applet.fill(0);
                    if (keyPressed[noteIndex]) {
                        applet.fill(100, 150, 200);
                    }
                } else {
                    applet.fill(80);
                }

                applet.rect(pianoX + margin + i * whiteKeyWidth + whiteKeyWidth - blackKeyWidth/2,
                        pianoY + margin,
                        blackKeyWidth, blackKeyHeight);

                // Show MIDI note number if active
                if (isActive) {
                    applet.fill(200);
                    applet.textAlign(PApplet.CENTER, PApplet.CENTER);
                    applet.textSize(8);
                    applet.text(pianoKeysMidi[noteIndex] + "",
                            pianoX + margin + i * whiteKeyWidth + whiteKeyWidth - blackKeyWidth/2 + blackKeyWidth/2,
                            pianoY + margin + 20);
                }
            }
        }

        // Simulate random key presses for visualization
        simulateKeyPresses();
    }

    private void simulateKeyPresses() {
        // Reset all keys
        for (int i = 0; i < keyPressed.length; i++) {
            keyPressed[i] = false;
        }

        // Randomly press some keys
        if (applet.frameCount % 30 == 0) {
            int key = (int) applet.random(0, keyPressed.length);
            keyPressed[key] = true;
        }
    }

    private void drawPitchMappingVisualization() {
        int visualizationX = sidebarWidth + margin + 250;
        int visualizationY = 360;
        int visualizationWidth = applet.width - sidebarWidth - margin * 2 - 250;
        int visualizationHeight = 140;

        // Background for visualization area
        applet.fill(COLOR_BACKGROUND);
        applet.stroke(COLOR_HEADER);
        applet.strokeWeight(2);
        applet.rect(visualizationX, visualizationY, visualizationWidth, visualizationHeight);

        // Draw label
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(14);
        String mappingTitle = "Pitch Mapping: ";
        if (settings.isPitchModeRadius()) {
            mappingTitle += "by Radius";
        } else if (settings.isPitchModeVelocity()) {
            mappingTitle += "by Velocity";
        } else {
            mappingTitle += "Fixed";
        }

        applet.text(mappingTitle,
                visualizationX + visualizationWidth / 2, visualizationY - 15);

        // Draw mapping
        if (settings.isPitchModeRadius() || settings.isPitchModeVelocity()) {
            // Draw axis
            applet.stroke(150);
            applet.strokeWeight(1);

            // X axis (parameter)
            applet.line(
                    visualizationX + margin,
                    visualizationY + visualizationHeight - margin,
                    visualizationX + visualizationWidth - margin,
                    visualizationY + visualizationHeight - margin
            );

            // Y axis (pitch)
            applet.line(
                    visualizationX + margin,
                    visualizationY + margin,
                    visualizationX + margin,
                    visualizationY + visualizationHeight - margin
            );

            // X axis label
            applet.fill(COLOR_TEXT);
            applet.textAlign(PApplet.CENTER, PApplet.TOP);
            applet.textSize(12);
            applet.text(
                    settings.isPitchModeRadius() ? "Radius" : "Velocity",
                    visualizationX + visualizationWidth / 2,
                    visualizationY + visualizationHeight - margin + 5
            );

            // Y axis label
            applet.pushMatrix();
            applet.translate(
                    visualizationX + margin - 15,
                    visualizationY + visualizationHeight / 2
            );
            applet.rotate(-PApplet.HALF_PI);
            applet.text("Pitch", 0, 0);
            applet.popMatrix();

            // Draw mapping line
            applet.stroke(COLOR_HEADER);
            applet.strokeWeight(2);
            applet.noFill();
            applet.beginShape();

            int numPoints = 40;
            float graphWidth = visualizationWidth - margin * 2;
            float graphHeight = visualizationHeight - margin * 2;

            for (int i = 0; i < numPoints; i++) {
                float x = visualizationX + margin + (i / (float)(numPoints - 1)) * graphWidth;

                // Get the corresponding note from the mapping
                int noteIndex = (int)((i / (float)(numPoints - 1)) * (noteMapping.length - 1));
                int pitch = noteMapping[noteIndex];

                // Map pitch to graph height (0-87 range to graph height)
                float normalizedPitch = 1.0f - (pitch - settings.getBasePitch() + settings.getPitchRange()) /
                        (float)(settings.getPitchRange() * 2);
                normalizedPitch = PApplet.constrain(normalizedPitch, 0, 1);
                float y = visualizationY + margin + normalizedPitch * graphHeight;

                applet.vertex(x, y);
            }
            applet.endShape();

            // Draw annotations
            for (int i = 0; i <= 4; i++) {
                float x = visualizationX + margin + (i / 4.0f) * graphWidth;
                applet.stroke(150);
                applet.strokeWeight(1);
                applet.line(
                        x,
                        visualizationY + visualizationHeight - margin,
                        x,
                        visualizationY + visualizationHeight - margin + 5
                );

                applet.fill(COLOR_TEXT);
                applet.textAlign(PApplet.CENTER, PApplet.TOP);
                applet.textSize(10);

                if (settings.isPitchModeRadius()) {
                    float percentage = i * 25;
                    applet.text(
                            percentage + "%",
                            x,
                            visualizationY + visualizationHeight - margin + 5
                    );
                } else {
                    float percentage = i * 25;
                    applet.text(
                            percentage + "%",
                            x,
                            visualizationY + visualizationHeight - margin + 5
                    );
                }
            }

            // Draw pitch markers
            for (int i = -2; i <= 2; i++) {
                int pitch = settings.getBasePitch() + (i * (settings.getPitchRange() / 2));
                if (pitch >= 0 && pitch <= 87) {
                    float normalizedPitch = 1.0f - (pitch - settings.getBasePitch() + settings.getPitchRange()) /
                            (float)(settings.getPitchRange() * 2);
                    normalizedPitch = PApplet.constrain(normalizedPitch, 0, 1);
                    float y = visualizationY + margin + normalizedPitch * graphHeight;

                    applet.stroke(150);
                    applet.strokeWeight(1);
                    applet.line(
                            visualizationX + margin - 5,
                            y,
                            visualizationX + margin,
                            y
                    );

                    applet.fill(COLOR_TEXT);
                    applet.textAlign(PApplet.RIGHT, PApplet.CENTER);
                    applet.textSize(10);
                    applet.text(
                            NoteUtility.pitchToNoteName(pitch),
                            visualizationX + margin - 8,
                            y
                    );
                }
            }
        } else {
            // Fixed pitch mode
            applet.fill(COLOR_TEXT);
            applet.textAlign(PApplet.CENTER, PApplet.CENTER);
            applet.textSize(14);
            applet.text(
                    "Fixed pitch: " + NoteUtility.pitchToNoteName(settings.getBasePitch()),
                    visualizationX + visualizationWidth / 2,
                    visualizationY + visualizationHeight / 2
            );
        }
    }

    private void resetAudioSettings() {
        // Reset to default audio values
        settings.setSoundEnabled(true);
        settings.setBounceInstrument(0);
        settings.setNoteVolume(0.5f);
        settings.setNoteDuration(150);
        settings.setPitchModeRadius(true);
        settings.setPitchModeVelocity(false);
        settings.setBasePitch(48); // A4
        settings.setPitchRange(36);

        // Update UI controls
        cp5.getController("soundEnabled").setValue(settings.isSoundEnabled() ? 1 : 0);
        cp5.getController("bounceInstrument").setValue(settings.getBounceInstrument());
        cp5.getController("noteVolume").setValue(settings.getNoteVolume());
        cp5.getController("noteDuration").setValue(settings.getNoteDuration());
        cp5.getController("pitchModeRadius").setValue(settings.isPitchModeRadius() ? 1 : 0);
        cp5.getController("pitchModeVelocity").setValue(settings.isPitchModeVelocity() ? 1 : 0);
        cp5.getController("basePitch").setValue(settings.getBasePitch());
        cp5.getController("pitchRange").setValue(settings.getPitchRange());

        // Apply to simulation
        simulationApp.updateAudioSettings(
                settings.isSoundEnabled(),
                settings.getBounceInstrument(),
                settings.isPitchModeRadius(),
                settings.isPitchModeVelocity(),
                settings.getBasePitch(),
                settings.getPitchRange(),
                settings.getNoteVolume(),
                settings.getNoteDuration()
        );

        updatePianoKeys();
        updateNoteMapping();
    }

    @Override
    public void update() {
        // Update settings from simulation if needed
    }
}