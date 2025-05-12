package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import simulation.config.SettingsManager;
import simulation.core.SimulationApp;

/**
 * Panel for rendering settings
 */
public class RenderingSettingsPanel extends SettingsPanel {
    private Group displayGroup;
    private Group performanceGroup;

    /**
     * Create a new rendering settings panel
     */
    public RenderingSettingsPanel(PApplet applet, ControlP5 cp5, SettingsManager settings,
                                  SimulationApp simulationApp, Tab parentTab,
                                  int sidebarWidth, int margin) {
        super(applet, cp5, settings, simulationApp, parentTab, sidebarWidth, margin);
    }

    @Override
    protected void initializePanel() {
        int panelContentWidth = applet.width - sidebarWidth - (margin * 2);

        // Display Options Group
        displayGroup = createControlGroup(
                "displayGroup",
                "Display Options",
                50,
                panelContentWidth / 2 - margin,
                220
        );

        // Performance Group
        performanceGroup = createControlGroup(
                "performanceGroup",
                "Performance Options",
                50,
                panelContentWidth / 2 - margin,
                220
        );
        performanceGroup.setPosition(
                sidebarWidth + margin + panelContentWidth / 2,
                50
        );

        // Display options
        createSlider("windowWidth", "Window Width", 800, 1920, applet.width,
                displayGroup, margin, 30)
                .onChange(event -> {
                    int width = (int) event.getController().getValue();
                    // Window size changes require special handling
                    // Just store the value for now, apply when button is pressed
                });

        createSlider("windowHeight", "Window Height", 600, 1080, applet.height,
                displayGroup, margin, 70)
                .onChange(event -> {
                    int height = (int) event.getController().getValue();
                    // Window size changes require special handling
                });

        createButton("applyWindowSize", "Apply Size", displayGroup,
                margin, 110, 120, 30)
                .onPress(event -> {
                    int width = (int) cp5.getController("windowWidth").getValue();
                    int height = (int) cp5.getController("windowHeight").getValue();
                    simulationApp.setWindowSize(width, height);
                });

        createSlider("targetFrameRate", "Target FPS", 30, 144, 60,
                displayGroup, margin, 160)
                .onChange(event -> {
                    int fps = Math.max(30, (int) event.getController().getValue());
                    applet.frameRate(fps);
                });

        // Performance options
        createToggle("highQualityRender", "High Quality Rendering", false,
                performanceGroup, margin, 30)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    simulationApp.setHighQualityRendering(c.getValue() > 0.5f);
                });


        createSlider("renderScale", "Render Scale", 1, 4, 1,
                performanceGroup, margin, 70)
                .setNumberOfTickMarks(4)
                .onChange(event -> {
                    int scale = (int) event.getController().getValue();
                    simulationApp.setRenderScale(scale);
                });

        createToggle("useAntialiasing", "Anti-aliasing", true,
                performanceGroup, margin, 110)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    simulationApp.setAntialiasing(c.getValue() > 0.5f);
                });


        createToggle("optimizeTraces", "Optimize Traces", true,
                performanceGroup, margin, 150)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    simulationApp.setOptimizeTraces(c.getValue() > 0.5f);
                });


        // Add buttons for screenshot and video recording
        createButton("takeScreenshot", "Take Screenshot", null,
                sidebarWidth + margin, 300, 150, 40)
                .onPress(event -> {
                    simulationApp.takeScreenshot();
                });

        createButton("startRecording", "Start Recording", null,
                sidebarWidth + margin + 170, 300, 150, 40)
                .onPress(event -> {
                    simulationApp.toggleRecording();

                    // Update button text based on recording state
                    Button recordButton = (Button) cp5.getController("startRecording");
                    if (simulationApp.isRecording()) {
                        recordButton.setLabel("Stop Recording");
                        recordButton.setColorBackground(0xFFFF4444);
                    } else {
                        recordButton.setLabel("Start Recording");
                        recordButton.setColorBackground(COLOR_HEADER); // Or whatever color constant you've defined
                    }
                });
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
        applet.text("Rendering Settings", sidebarWidth + (applet.width - sidebarWidth) / 2, 20);

        // Draw performance metrics
        drawPerformanceMetrics();

        // Draw recording info if active
        if (simulationApp.isRecording()) {
            drawRecordingInfo();
        }
    }

    private void drawPerformanceMetrics() {
        int metricsX = sidebarWidth + margin;
        int metricsY = 360;
        int metricsWidth = applet.width - sidebarWidth - margin * 2;
        int metricsHeight = 180;

        // Background for metrics area
        applet.fill(COLOR_BACKGROUND);
        applet.stroke(COLOR_HEADER);
        applet.strokeWeight(2);
        applet.rect(metricsX, metricsY, metricsWidth, metricsHeight);

        // Draw label
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(14);
        applet.text("Performance Metrics", metricsX + metricsWidth / 2, metricsY - 15);

        // Frame rate graph
        int graphX = metricsX + margin;
        int graphY = metricsY + 30;
        int graphWidth = metricsWidth - margin * 2;
        int graphHeight = 100;

        // Graph background
        applet.fill(0);
        applet.stroke(100);
        applet.strokeWeight(1);
        applet.rect(graphX, graphY, graphWidth, graphHeight);

        // Draw FPS line
        applet.stroke(COLOR_HEADER);
        applet.strokeWeight(2);
        applet.noFill();
        applet.beginShape();

        // Get current FPS
        float fps = applet.frameRate;
        float targetFps = (int) cp5.getController("targetFrameRate").getValue();

        // Simulate some FPS history for visualization
        for (int i = 0; i < graphWidth; i++) {
            float x = graphX + i;

            // Generate a slightly varied FPS value for each point
            float fpsVariation = PApplet.sin(i * 0.1f + applet.frameCount * 0.05f) * 5;
            float fpsSample = fps + fpsVariation;

            // Ensure FPS is capped at target and has a reasonable minimum
            fpsSample = PApplet.constrain(fpsSample, 10, targetFps * 1.2f);

            // Map FPS to graph height (inverted, since y increases downward)
            float normalizedFps = fpsSample / (targetFps * 1.2f);
            float y = graphY + graphHeight - (normalizedFps * graphHeight);

            applet.vertex(x, y);
        }
        applet.endShape();

        // Draw target FPS line
        applet.stroke(200, 100, 100);
        applet.strokeWeight(1);
        float targetY = graphY + graphHeight - ((targetFps / (targetFps * 1.2f)) * graphHeight);
        applet.line(graphX, targetY, graphX + graphWidth, targetY);

        // Draw Y-axis labels
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.RIGHT, PApplet.CENTER);
        applet.textSize(10);

        applet.text("0", graphX - 5, graphY + graphHeight);
        applet.text(String.format("%.0f", targetFps), graphX - 5, targetY);
        applet.text(String.format("%.0f", targetFps * 1.2f), graphX - 5, graphY);

        // Current FPS text
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.LEFT, PApplet.CENTER);
        applet.textSize(12);

        String fpsText = String.format("Current FPS: %.1f / %.0f (Target)", fps, targetFps);
        applet.text(fpsText, graphX, graphY + graphHeight + 20);

        // Draw render quality info
        boolean highQuality = (cp5.getController("highQualityRender").getValue() > 0.5f);
        int renderScale = (int) cp5.getController("renderScale").getValue();

        applet.textAlign(PApplet.RIGHT, PApplet.CENTER);
        String qualityText = highQuality ?
                String.format("High Quality (%dx Scale)", renderScale) :
                "Standard Quality";
        applet.text(qualityText, graphX + graphWidth, graphY + graphHeight + 20);
    }

    private void drawRecordingInfo() {
        // Draw recording indicator
        applet.fill(0xFFFF4444); // Red
        applet.noStroke();
        applet.ellipse(applet.width - 20, 20, 10, 10);

        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.RIGHT, PApplet.CENTER);
        applet.textSize(12);
        applet.text("Recording...", applet.width - 40, 20);

        // Show recording stats
        applet.text(
                String.format("Frames: %d / Duration: %.1fs",
                        simulationApp.getRecordingFrames(),
                        simulationApp.getRecordingDuration()),
                applet.width - 20, 40
        );
    }

    @Override
    public void resetToDefaults() {
        // Reset to default rendering values
        cp5.getController("windowWidth").setValue(applet.width);
        cp5.getController("windowHeight").setValue(applet.height);
        cp5.getController("targetFrameRate").setValue(60);

        // For toggle controls, use setState instead of setValue
        ((Toggle)cp5.getController("highQualityRender")).setState(false);
        cp5.getController("renderScale").setValue(1);
        ((Toggle)cp5.getController("useAntialiasing")).setState(true);
        ((Toggle)cp5.getController("optimizeTraces")).setState(true);

        // Apply changes to simulation
        simulationApp.setHighQualityRendering(false);
        simulationApp.setRenderScale(1);
        simulationApp.setAntialiasing(true);
        simulationApp.setOptimizeTraces(true);
        applet.frameRate(60);
    }

    @Override
    public void update() {
        // Update settings from simulation if needed

        // Update recording button state if needed
        Button recordButton = (Button) cp5.getController("startRecording");
        if (simulationApp.isRecording() && !recordButton.getLabel().equals("Stop Recording")) {
            recordButton.setLabel("Stop Recording");
            recordButton.setColorBackground(0xFFFF4444);
        } else if (!simulationApp.isRecording() && !recordButton.getLabel().equals("Start Recording")) {
            recordButton.setLabel("Start Recording");
            recordButton.setColorBackground(COLOR_HEADER); // Or whatever color constant you've defined
        }
    }
}