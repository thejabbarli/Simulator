package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PVector;
import simulation.config.SettingsManager;
import simulation.core.Ball;
import simulation.core.SimulationApp;

/**
 * Panel for ball-related settings
 */
public class BallSettingsPanel extends SettingsPanel {
    private Group ballPropertiesGroup;
    private Group ballAppearanceGroup;
    private ColorPicker colorPicker;
    private Ball previewBall;

    private final int COLOR_PICKER_SIZE = 170;
    private final int PREVIEW_SIZE = 170;

    /**
     * Create a new ball settings panel
     */
    public BallSettingsPanel(PApplet applet, ControlP5 cp5, SettingsManager settings,
                             SimulationApp simulationApp, Tab parentTab,
                             int sidebarWidth, int margin) {
        super(applet, cp5, settings, simulationApp, parentTab, sidebarWidth, margin);

        // Create a preview ball for the panel
        previewBall = new Ball(new PVector(sidebarWidth + margin + 300, 200),
                settings.getBallRadius(), settings.getBallMass());
        previewBall.setStrokeThickness(settings.getBallStroke());
        previewBall.setColor(settings.getBallColor());
    }

    @Override
    protected void initializePanel() {
        int panelContentWidth = applet.width - sidebarWidth - (margin * 2);

        // Ball Properties Group
        ballPropertiesGroup = createControlGroup(
                "ballPropertiesGroup",
                "Ball Properties",
                50,
                panelContentWidth / 2 - margin,
                220
        );

        // Ball Appearance Group
        ballAppearanceGroup = createControlGroup(
                "ballAppearanceGroup",
                "Ball Appearance",
                50,
                panelContentWidth / 2 - margin,
                220
        );
        ballAppearanceGroup.setPosition(
                sidebarWidth + margin + panelContentWidth / 2,
                50
        );

        // Add sliders to properties group
        createSlider("ballRadius", "Initial Radius", 5, 100, settings.getBallRadius(),
                ballPropertiesGroup, margin, 30)
                .onChange(event -> {
                    settings.setBallRadius(event.getController().getValue());
                    previewBall.setRadius(event.getController().getValue());
                });

        createSlider("ballMass", "Mass", 0.1f, 5, settings.getBallMass(),
                ballPropertiesGroup, margin, 30 + CONTROL_HEIGHT + CONTROL_SPACING)
                .onChange(event -> {
                    settings.setBallMass(event.getController().getValue());
                    previewBall.setMass(event.getController().getValue());
                });

        createSlider("ballMaxSpeed", "Max Speed", 50, 600, settings.getBallMaxSpeed(),
                ballPropertiesGroup, margin, 30 + (CONTROL_HEIGHT + CONTROL_SPACING) * 2)
                .onChange(event -> {
                    settings.setBallMaxSpeed(event.getController().getValue());
                    previewBall.setMaxSpeed(event.getController().getValue());
                });

        // Add controls to appearance group
        createSlider("ballStroke", "Stroke Thickness", 0.1f, 20, settings.getBallStroke(),
                ballAppearanceGroup, margin, 30)
                .onChange(event -> {
                    settings.setBallStroke(event.getController().getValue());
                    previewBall.setStrokeThickness(event.getController().getValue());
                });

        // Add color picker
        colorPicker = cp5.addColorPicker("ballColorPicker")
                .setPosition(margin, 30 + CONTROL_HEIGHT + CONTROL_SPACING)
                .setSize(COLOR_PICKER_SIZE, CONTROL_HEIGHT * 3)
                .setColorValue(settings.getBallColor())
                .setLabel("Ball Color")
                .setGroup(ballAppearanceGroup)
                .setTab(parentTab);

        colorPicker.addListener(new ControlListener() {
            public void controlEvent(ControlEvent event) {
                if (event.isFrom(colorPicker)) {
                    int newColor = colorPicker.getColorValue();
                    settings.setBallColor(newColor);
                    previewBall.setColor(newColor);
                }
            }
        });

        // Add buttons
        createButton("resetBall", "Reset Ball", null,
                sidebarWidth + margin, 300, 150, 40)
                .onPress(event -> {
                    simulationApp.resetBall();
                });

        createButton("randomizeBall", "Randomize Ball", null,
                sidebarWidth + margin + 170, 300, 150, 40)
                .onPress(event -> {
                    randomizeBallProperties();
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
        applet.text("Ball Settings", sidebarWidth + (applet.width - sidebarWidth) / 2, 20);

        // Draw ball preview
        drawBallPreview();
    }

    private void drawBallPreview() {
        // Preview area
        int previewX = sidebarWidth + margin + (applet.width - sidebarWidth) / 2 - PREVIEW_SIZE / 2;
        int previewY = 360;

        // Clear the preview area first
        applet.fill(0); // Black background
        applet.stroke(COLOR_HEADER);
        applet.strokeWeight(2);
        applet.rect(previewX, previewY, PREVIEW_SIZE, PREVIEW_SIZE);

        // Draw preview label with adequate spacing
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(14);
        applet.text("Ball Preview", previewX + PREVIEW_SIZE / 2, previewY - 20);

        // Position preview ball in center of preview area
        previewBall.setPosition(new PVector(
                previewX + PREVIEW_SIZE / 2,
                previewY + PREVIEW_SIZE / 2
        ));

        // Draw the ball
        applet.pushStyle();
        applet.stroke(previewBall.getColor());
        // Make sure the stroke is visible
        float strokeWeight = Math.max(1.0f, previewBall.getStrokeThickness());
        applet.strokeWeight(strokeWeight);
        applet.noFill();
        applet.ellipse(
                previewBall.getPosition().x,
                previewBall.getPosition().y,
                previewBall.getRadius() * 2,
                previewBall.getRadius() * 2
        );
        applet.popStyle();

        // Show ball properties with adequate spacing
        String infoText = String.format(
                "Radius: %.1f | Mass: %.1f | Stroke: %.1f",
                previewBall.getRadius(),
                previewBall.getMass(),
                previewBall.getStrokeThickness()
        );

        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(12);
        applet.text(infoText, previewX + PREVIEW_SIZE / 2, previewY + PREVIEW_SIZE + 20);
    }

    @Override
    public void resetToDefaults() {
        // Reset to default ball values
        settings.setBallRadius(30f);
        settings.setBallStroke(0.1f);
        settings.setBallMass(1.0f);
        settings.setBallColor(0xFFFF00FF); // Magenta
        settings.setBallMaxSpeed(300f);

        // Update UI controls
        cp5.getController("ballRadius").setValue(settings.getBallRadius());
        cp5.getController("ballMass").setValue(settings.getBallMass());
        cp5.getController("ballStroke").setValue(settings.getBallStroke());
        colorPicker.setColorValue(settings.getBallColor());

        // Update preview ball
        previewBall.setRadius(settings.getBallRadius());
        previewBall.setMass(settings.getBallMass());
        previewBall.setStrokeThickness(settings.getBallStroke());
        previewBall.setColor(settings.getBallColor());

        // Apply to simulation
        simulationApp.applyBallSettings(
                settings.getBallRadius(),
                settings.getBallMass(),
                settings.getBallStroke(),
                settings.getBallColor()
        );
    }

    private void randomizeBallProperties() {
        // Randomize within sensible ranges
        float radius = applet.random(10, 50);
        float mass = applet.random(0.5f, 3.0f);
        float stroke = applet.random(1, 10);
        int color = applet.color(applet.random(255), applet.random(255), applet.random(255));

        // Update settings
        settings.setBallRadius(radius);
        settings.setBallMass(mass);
        settings.setBallStroke(stroke);
        settings.setBallColor(color);

        // Update preview ball
        previewBall.setRadius(radius);
        previewBall.setMass(mass);
        previewBall.setStrokeThickness(stroke);
        previewBall.setColor(color);

        // Update UI controls
        cp5.getController("ballRadius").setValue(radius);
        cp5.getController("ballMass").setValue(mass);
        cp5.getController("ballStroke").setValue(stroke);
        colorPicker.setColorValue(color);

        // Apply to simulation
        simulationApp.applyBallSettings(radius, mass, stroke, color);
    }

    @Override
    public void update() {
        // Update settings from simulation if needed
    }
}