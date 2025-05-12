package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import simulation.config.SettingsManager;
import simulation.core.SimulationApp;

/**
 * Panel for physics-related settings
 */
public class PhysicsSettingsPanel extends SettingsPanel {
    private Group physicsGroup;
    private Group bounceGroup;

    /**
     * Create a new physics settings panel
     */
    public PhysicsSettingsPanel(PApplet applet, ControlP5 cp5, SettingsManager settings,
                                SimulationApp simulationApp, Tab parentTab,
                                int sidebarWidth, int margin) {
        super(applet, cp5, settings, simulationApp, parentTab, sidebarWidth, margin);
    }

    @Override
    protected void initializePanel() {
        int panelContentWidth = applet.width - sidebarWidth - (margin * 2);

        // Physics Group
        physicsGroup = createControlGroup(
                "physicsGroup",
                "Physics Properties",
                50,
                panelContentWidth / 2 - margin,
                180
        );

        // Bounce Effects Group
        bounceGroup = createControlGroup(
                "bounceGroup",
                "Bounce Effects",
                50,
                panelContentWidth / 2 - margin,
                280
        );
        bounceGroup.setPosition(
                sidebarWidth + margin + panelContentWidth / 2,
                50
        );

        // Add sliders to physics group
        createSlider("gravity", "Gravity", 0, 2, settings.getGravity(),
                physicsGroup, margin, 30)
                .onChange(event -> {
                    settings.setGravity(event.getController().getValue());
                    simulationApp.updateGravity(event.getController().getValue());
                });

        createSlider("ballMaxSpeed", "Max Ball Speed", 50, 600, settings.getBallMaxSpeed(),
                physicsGroup, margin, 30 + CONTROL_HEIGHT + CONTROL_SPACING)
                .onChange(event -> {
                    settings.setBallMaxSpeed(event.getController().getValue());
                    simulationApp.updateBallMaxSpeed(event.getController().getValue());
                });

        createToggle("enforceWallBoundary", "Enforce Wall Boundary",
                settings.isEnforceWallBoundaryLimit(),
                physicsGroup, margin, 30 + (CONTROL_HEIGHT + CONTROL_SPACING) * 2)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    settings.setEnforceWallBoundaryLimit(c.getValue() > 0.5f);
                    simulationApp.updateWallBoundaryLimit(c.getValue() > 0.5f);
                });


        // Add controls to bounce group
        createSlider("growthAmount", "Growth per Bounce", 0, 5, settings.getGrowthAmount(),
                bounceGroup, margin, 30)
                .onChange(event -> {
                    settings.setGrowthAmount(event.getController().getValue());
                    simulationApp.updateBallGrowth(event.getController().getValue());
                });

        createSlider("speedBoostFactor", "Speed Boost Factor", 1, 1.1f, settings.getSpeedBoostFactor(),
                bounceGroup, margin, 30 + CONTROL_HEIGHT + CONTROL_SPACING)
                .onChange(event -> {
                    settings.setSpeedBoostFactor(event.getController().getValue());
                    simulationApp.updateSpeedBoost(event.getController().getValue());
                });

        createSlider("maxSizeRadius", "Max Size Radius", 50, 350, settings.getMaxSizeRadius(),
                bounceGroup, margin, 30 + (CONTROL_HEIGHT + CONTROL_SPACING) * 2)
                .onChange(event -> {
                    settings.setMaxSizeRadius(event.getController().getValue());
                    simulationApp.updateMaxSizeRadius(event.getController().getValue());
                });

        createToggle("shouldStop", "Stop at Max Size", settings.getShouldStop(),
                bounceGroup, margin, 30 + (CONTROL_HEIGHT + CONTROL_SPACING) * 3)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    settings.setShouldStop(c.getValue() > 0.5f);
                    simulationApp.setMaxSizeStop(c.getValue() > 0.5f);
                });


        createToggle("shouldShrink", "Shrink at Max Size", settings.getShouldShrink(),
                bounceGroup, margin, 30 + (CONTROL_HEIGHT + CONTROL_SPACING) * 4)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    settings.setShouldShrink(c.getValue() > 0.5f);
                    simulationApp.setMaxSizeShrink(c.getValue() > 0.5f, settings.getShrinkRate());
                });


        createSlider("shrinkRate", "Shrink Rate", 0.1f, 5, settings.getShrinkRate(),
                bounceGroup, margin, 30 + (CONTROL_HEIGHT + CONTROL_SPACING) * 5)
                .onChange(event -> {
                    settings.setShrinkRate(event.getController().getValue());
                    simulationApp.updateShrinkRate(event.getController().getValue());
                });

        // Add reset physics button
        createButton("resetPhysics", "Reset Physics", null,
                sidebarWidth + margin, 350, 150, 40)
                .onPress(event -> {
                    resetToDefaultPhysics();
                });

        // Add apply button
        createButton("applyPhysics", "Apply Settings", null,
                sidebarWidth + margin + 170, 350, 150, 40)
                .onPress(event -> {
                    simulationApp.applyPhysicsSettings();
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
        applet.text("Physics Settings", sidebarWidth + (applet.width - sidebarWidth) / 2, 20);

        // Draw physics visualization
        drawPhysicsVisualization();
    }

    private void drawPhysicsVisualization() {
        int visualizationX = sidebarWidth + margin;
        int visualizationY = 420;
        int visualizationWidth = applet.width - sidebarWidth - margin * 2;
        int visualizationHeight = 180;

        // Background for visualization area
        applet.fill(COLOR_BACKGROUND);
        applet.stroke(COLOR_HEADER);
        applet.strokeWeight(2);
        applet.rect(visualizationX, visualizationY, visualizationWidth, visualizationHeight);

        // Draw label
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(14);
        applet.text("Physics Visualization",
                visualizationX + visualizationWidth / 2, visualizationY - 15);

        // Draw gravity arrow
        int centerX = visualizationX + visualizationWidth / 4;
        int centerY = visualizationY + visualizationHeight / 2;
        float gravityStrength = settings.getGravity();
        int arrowHeight = (int)(gravityStrength * 50);

        applet.stroke(255, 200, 50);
        applet.strokeWeight(2);
        applet.line(centerX, centerY - 30, centerX, centerY + 30 + arrowHeight);

        // Arrow head
        applet.line(centerX, centerY + 30 + arrowHeight, centerX - 5, centerY + 25 + arrowHeight);
        applet.line(centerX, centerY + 30 + arrowHeight, centerX + 5, centerY + 25 + arrowHeight);

        // Gravity label
        applet.fill(255, 200, 50);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(12);
        applet.text("Gravity", centerX, centerY - 40);
        applet.text(String.format("%.2f", gravityStrength), centerX, centerY + 50 + arrowHeight);

        // Draw bounce growth visualization
        int bounceX = visualizationX + visualizationWidth * 3 / 4;
        int bounceY = visualizationY + visualizationHeight / 2;
        float growthAmount = settings.getGrowthAmount();
        float speedBoost = settings.getSpeedBoostFactor();

        // Base circle
        applet.stroke(50, 200, 255);
        applet.strokeWeight(2);
        applet.noFill();
        applet.ellipse(bounceX, bounceY, 50, 50);

        // Growth circles
        for (int i = 1; i <= 3; i++) {
            float radius = 50 + (growthAmount * 10 * i);
            applet.stroke(50, 200, 255, 255 - (i * 50));
            applet.ellipse(bounceX, bounceY, radius, radius);
        }

        // Bounce label
        applet.fill(50, 200, 255);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(12);
        applet.text("Bounce Growth", bounceX, bounceY - 60);
        applet.text(String.format("+%.1f per bounce", growthAmount), bounceX, bounceY - 45);
        applet.text(String.format("Speed: %.3fx", speedBoost), bounceX, bounceY + 60);
    }

    private void resetToDefaultPhysics() {
        // Reset to default physics values
        settings.setGravity(0.7f);
        settings.setGrowthAmount(1.1f);
        settings.setSpeedBoostFactor(1.012f);
        settings.setMaxSizeRadius(250f);
        settings.setShouldStop(true);
        settings.setShouldShrink(true);
        settings.setShrinkRate(0.5f);
        settings.setEnforceWallBoundaryLimit(true);

        // Update UI controls
        cp5.getController("gravity").setValue(settings.getGravity());
        cp5.getController("growthAmount").setValue(settings.getGrowthAmount());
        cp5.getController("speedBoostFactor").setValue(settings.getSpeedBoostFactor());
        cp5.getController("maxSizeRadius").setValue(settings.getMaxSizeRadius());
        cp5.getController("shouldStop").setValue(settings.getShouldStop() ? 1 : 0);
        cp5.getController("shouldShrink").setValue(settings.getShouldShrink() ? 1 : 0);
        cp5.getController("shrinkRate").setValue(settings.getShrinkRate());
        cp5.getController("enforceWallBoundary").setValue(settings.isEnforceWallBoundaryLimit() ? 1 : 0);

        // Apply to simulation
        simulationApp.applyPhysicsSettings();
    }

    @Override
    public void update() {
        // Update settings from simulation if needed
    }
}