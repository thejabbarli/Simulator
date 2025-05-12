package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PVector;
import simulation.config.SettingsManager;
import simulation.core.SimulationApp;

/**
 * Panel for visual effects settings
 */
public class EffectsSettingsPanel extends SettingsPanel {
    private Group traceGroup;
    private Group visualGroup;

    // For trace preview
    private float[] previewX = new float[20];
    private float[] previewY = new float[20];
    private int[] previewLifetime = new int[20];
    private float[] previewRadius = new float[20];

    /**
     * Create a new effects settings panel
     */
    public EffectsSettingsPanel(PApplet applet, ControlP5 cp5, SettingsManager settings,
                                SimulationApp simulationApp, Tab parentTab,
                                int sidebarWidth, int margin) {
        super(applet, cp5, settings, simulationApp, parentTab, sidebarWidth, margin);

        // Initialize preview points for trace visualization
        initializePreviewPoints();
    }

    private void initializePreviewPoints() {
        for (int i = 0; i < previewX.length; i++) {
            previewX[i] = applet.random(applet.width);
            previewY[i] = applet.random(applet.height);
            previewLifetime[i] = (int) applet.random(1, 10);
            previewRadius[i] = applet.random(10, 40);
        }
    }

    @Override
    protected void initializePanel() {
        int panelContentWidth = applet.width - sidebarWidth - (margin * 2);

        // Ball Trace Group
        traceGroup = createControlGroup(
                "traceGroup",
                "Ball Trace Settings",
                50,
                panelContentWidth / 2 - margin,
                280
        );

        // Visual Effects Group
        visualGroup = createControlGroup(
                "visualGroup",
                "Visual Effects",
                50,
                panelContentWidth / 2 - margin,
                220
        );
        visualGroup.setPosition(
                sidebarWidth + margin + panelContentWidth / 2,
                50
        );

        // Add trace controls
        createToggle("traceEnabled", "Enable Traces", true,
                traceGroup, margin, 30)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    simulationApp.setTraceEnabled(c.getValue() > 0.5f);
                });


        createSlider("traceFrequency", "Trace Frequency", 1, 20, settings.getTraceFrequency() / 100,
                traceGroup, margin, 70)
                .onChange(event -> {
                    float frequency = event.getController().getValue() * 100;
                    settings.setTraceFrequency(frequency);
                    simulationApp.updateTraceFrequency(frequency);
                });

        createSlider("traceLifetime", "Trace Lifetime", 1, 60, settings.getTraceLifetimeFrames(),
                traceGroup, margin, 110)
                .onChange(event -> {
                    int lifetime = (int) event.getController().getValue();
                    settings.setTraceLifetimeFrames(lifetime);
                    simulationApp.updateTraceLifetime(lifetime);
                });

        createToggle("permanentTraces", "Permanent Traces", settings.getPermanentTraces(),
                traceGroup, margin, 150)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    settings.setPermanentTraces(c.getValue() > 0.5f);
                    simulationApp.setPermanentTraces(c.getValue() > 0.5f);
                });


        createSlider("trailThickness", "Trail Thickness", 1, 20, settings.getTrailThickness(),
                traceGroup, margin, 190)
                .onChange(event -> {
                    settings.setTrailThickness(event.getController().getValue());
                    simulationApp.updateTrailThickness(event.getController().getValue());
                });

        createSlider("trailThicknessMultiplier", "Thickness Multiplier", 0.1f, 3, settings.getTrailThicknessMultiplier(),
                traceGroup, margin, 230)
                .onChange(event -> {
                    settings.setTrailThicknessMultiplier(event.getController().getValue());
                    simulationApp.updateTrailThicknessMultiplier(event.getController().getValue());
                });

        // Add visual effect controls
        // This section could include color schemes, animations, etc.
        createSlider("backgroundBrightness", "Background Brightness", 0, 255, 0,
                visualGroup, margin, 30)
                .onChange(event -> {
                    simulationApp.updateBackgroundBrightness((int) event.getController().getValue());
                });

        createToggle("rainbowMode", "Rainbow Mode", true,
                visualGroup, margin, 70)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    simulationApp.setRainbowMode(c.getValue() > 0.5f);
                });


        createSlider("colorSpeed", "Color Cycling Speed", 0.5f, 5, 1,
                visualGroup, margin, 110)
                .onChange(event -> {
                    simulationApp.updateColorSpeed(event.getController().getValue());
                });

        createToggle("showBallVelocity", "Show Velocity Vector", false,
                visualGroup, margin, 150)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    simulationApp.setShowVelocityVector(c.getValue() > 0.5f);
                });


        // Add effects buttons
        createButton("clearTraces", "Clear All Traces", null,
                sidebarWidth + margin, 350, 150, 40)
                .onPress(event -> {
                    simulationApp.clearAllTraces();
                });

        createButton("resetEffects", "Reset Effects", null,
                sidebarWidth + margin + 170, 350, 150, 40)
                .onPress(event -> {
                    resetEffectsSettings();
                });
    }

    @Override
    public void resetToDefaults() {
        // Already implemented as resetEffectsSettings()
        resetEffectsSettings();
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
        applet.text("Effects Settings", sidebarWidth + (applet.width - sidebarWidth) / 2, 20);

        // Draw trace preview
        drawTracePreview();
    }

    private void drawTracePreview() {
        int previewAreaX = sidebarWidth + margin;
        int previewAreaY = 410;
        int previewWidth = applet.width - sidebarWidth - margin * 2;
        int previewHeight = 180;


        // Background for preview area
        applet.fill(0); // Black background to simulate simulation
        applet.stroke(COLOR_HEADER);
        applet.strokeWeight(2);
        applet.rect(previewAreaX, previewAreaY, previewWidth, previewHeight);

        // Draw label
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(14);
        applet.text("Trace Effect Preview", previewAreaX + previewWidth / 2, previewAreaY - 15);

        // Draw trace settings info
        String traceInfo = String.format(
                "Frequency: %.1f per second | Lifetime: %d frames | %s",
                settings.getTraceFrequency() / 100,
                settings.getTraceLifetimeFrames(),
                settings.getPermanentTraces() ? "Permanent" : "Temporary"
        );

        applet.textSize(12);
        applet.text(traceInfo, previewAreaX + previewWidth / 2, previewAreaY + previewHeight + 15);

        // Draw simulated traces
        boolean tracesEnabled = (cp5.getController("traceEnabled").getValue() > 0.5f);
        if (tracesEnabled) {
            // Update trace positions for animation
            updatePreviewTraces();

            // Draw traces
            for (int i = 0; i < previewX.length; i++) {
                if (previewLifetime[i] > 0 || settings.getPermanentTraces()) {
                    // Calculate color based on rainbow mode
                    int traceColor;
                    boolean rainbowMode = (cp5.getController("rainbowMode").getValue() > 0.5f);

                    if (rainbowMode) {
                        float hue = (applet.frameCount * 2 + i * 10) % 360;
                        applet.colorMode(PApplet.HSB, 360, 100, 100);
                        traceColor = applet.color(hue, 100, 100);
                        applet.colorMode(PApplet.RGB, 255);
                    } else {
                        traceColor = applet.color(255);
                    }

                    float thickness = settings.getTrailThickness() * settings.getTrailThicknessMultiplier();

                    // Draw the trace
                    applet.stroke(traceColor);
                    applet.strokeWeight(thickness);
                    applet.noFill();
                    applet.ellipse(previewX[i], previewY[i], previewRadius[i] * 2, previewRadius[i] * 2);
                }
            }
        }
    }

    private void updatePreviewTraces() {
        // Update trace positions and lifetimes
        for (int i = 0; i < previewX.length; i++) {
            // Decrease lifetime
            if (!settings.getPermanentTraces()) {
                previewLifetime[i]--;

                // Reset if expired
                if (previewLifetime[i] <= 0) {
                    resetPreviewTrace(i);
                }
            }

            // Move trace point slightly for animation
            previewX[i] += applet.random(-1, 1);
            previewY[i] += applet.random(-1, 1);

            // Keep within bounds
            int previewAreaX = sidebarWidth + margin;
            int previewAreaY = 410;
            int previewAreaWidth = applet.width - sidebarWidth - margin * 2;
            int previewAreaHeight = 180;

            previewX[i] = PApplet.constrain(previewX[i],
                    previewAreaX + previewRadius[i],
                    previewAreaX + previewAreaWidth - previewRadius[i]);
            previewY[i] = PApplet.constrain(previewY[i],
                    previewAreaY + previewRadius[i],
                    previewAreaY + previewAreaHeight - previewRadius[i]);
        }

        // Add a new trace periodically
        if (applet.frameCount % 15 == 0) {
            // Find an expired trace to reuse
            for (int i = 0; i < previewX.length; i++) {
                if (previewLifetime[i] <= 0) {
                    resetPreviewTrace(i);
                    break;
                }
            }
        }
    }

    private void resetPreviewTrace(int index) {
        int previewAreaX = sidebarWidth + margin;
        int previewAreaY = 410;
        int previewAreaWidth = applet.width - sidebarWidth - margin * 2;
        int previewAreaHeight = 180;

        previewX[index] = applet.random(previewAreaX + 20, previewAreaX + previewAreaWidth - 20);
        previewY[index] = applet.random(previewAreaY + 20, previewAreaY + previewAreaHeight - 20);
        previewLifetime[index] = (int) applet.random(settings.getTraceLifetimeFrames() * 0.5f,
                settings.getTraceLifetimeFrames() * 1.5f);
        previewRadius[index] = applet.random(10, 35);
    }

    private void resetEffectsSettings() {
        // Reset to default effects values
        settings.setTraceFrequency(2500f);
        settings.setTraceLifetimeFrames(2);
        settings.setPermanentTraces(false);
        settings.setTrailThickness(10.0f);
        settings.setTrailThicknessMultiplier(1.0f);

        // Update UI controls
        cp5.getController("traceEnabled").setValue(1);
        cp5.getController("traceFrequency").setValue(settings.getTraceFrequency() / 100);
        cp5.getController("traceLifetime").setValue(settings.getTraceLifetimeFrames());
        cp5.getController("permanentTraces").setValue(settings.getPermanentTraces() ? 1 : 0);
        cp5.getController("trailThickness").setValue(settings.getTrailThickness());
        cp5.getController("trailThicknessMultiplier").setValue(settings.getTrailThicknessMultiplier());
        cp5.getController("backgroundBrightness").setValue(0);
        cp5.getController("rainbowMode").setValue(1);
        cp5.getController("colorSpeed").setValue(1);
        cp5.getController("showBallVelocity").setValue(0);

        // Apply to simulation
        simulationApp.updateTraceFrequency(settings.getTraceFrequency());
        simulationApp.updateTraceLifetime(settings.getTraceLifetimeFrames());
        simulationApp.setPermanentTraces(settings.getPermanentTraces());
        simulationApp.updateTrailThickness(settings.getTrailThickness());
        simulationApp.updateTrailThicknessMultiplier(settings.getTrailThicknessMultiplier());
        simulationApp.setTraceEnabled(true);
        simulationApp.updateBackgroundBrightness(0);
        simulationApp.setRainbowMode(true);
        simulationApp.updateColorSpeed(1);
        simulationApp.setShowVelocityVector(false);
    }

    @Override
    public void update() {
        // Update settings from simulation if needed
    }
}