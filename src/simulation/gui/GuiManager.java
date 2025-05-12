package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PFont;
import simulation.config.SettingsManager;
import simulation.core.SimulationApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI manager that handles the creation and management of all UI components
 */
public class GuiManager {
    private final PApplet applet;
    private final ControlP5 cp5;
    private final SettingsManager settings;
    private final SimulationApp simulationApp;

    private PFont uiFont;
    private PFont headerFont;

    private final List<SettingsPanel> panels = new ArrayList<>();
    private PresetManager presetManager;
    private RenderController renderController;

    private boolean guiVisible = true;
    private Tab mainTab;
    private Tab ballTab;
    private Tab physicsTab;
    private Tab effectsTab;
    private Tab audioTab;
    private Tab renderTab;
    private Tab presetsTab;

    // Constants for UI layout
    private static final int SIDEBAR_WIDTH = 220;
    private static final int PANEL_MARGIN = 10;
    private static final int HEADER_HEIGHT = 40;
    private static final int TAB_HEIGHT = 30;
    private static final int CONTROL_HEIGHT = 24;
    private static final int CONTROL_SPACING = 6;
    private static final int COLOR_BACKGROUND = 0xFF232323;
    private static final int COLOR_PANEL = 0xFF303030;
    private static final int COLOR_ACCENT = 0xFF00B4D8;
    private static final int COLOR_TEXT = 0xFFEEEEEE;
    private static final int COLOR_TAB_ACTIVE = 0xFF00B4D8;
    private static final int COLOR_TAB_INACTIVE = 0xFF555555;
    private static final int KEY_F1 = 112;
    private static final int KEY_F8 = 119;

    /**
     * Create a new GUI manager
     * @param applet The PApplet instance (Processing sketch)
     * @param settings The settings manager
     * @param simulationApp The main simulation app
     */
    public GuiManager(PApplet applet, SettingsManager settings, SimulationApp simulationApp) {
        this.applet = applet;
        this.settings = settings;
        this.simulationApp = simulationApp;

        // Initialize ControlP5
        cp5 = new ControlP5(applet);
        setupFonts();
        setupTabs();
        setupPanels();
        styleUI();

        // Create managers
        presetManager = new PresetManager(applet, cp5, settings, SIDEBAR_WIDTH, PANEL_MARGIN);
        renderController = new RenderController(applet, cp5, simulationApp, SIDEBAR_WIDTH, PANEL_MARGIN);
    }

    private void setupFonts() {
        uiFont = applet.createFont("Arial", 14);
        headerFont = applet.createFont("Arial Bold", 18);
        cp5.setFont(uiFont);
    }

    private void setupTabs() {
        // Create tabs for different setting categories
        mainTab = cp5.getTab("default");
        mainTab.setLabel("Overview");
        mainTab.setHeight(TAB_HEIGHT);

        ballTab = cp5.addTab("BallSettings");
        ballTab.setLabel("Ball");
        ballTab.setHeight(TAB_HEIGHT);

        physicsTab = cp5.addTab("PhysicsSettings");
        physicsTab.setLabel("Physics");
        physicsTab.setHeight(TAB_HEIGHT);

        effectsTab = cp5.addTab("EffectsSettings");
        effectsTab.setLabel("Effects");
        effectsTab.setHeight(TAB_HEIGHT);

        audioTab = cp5.addTab("AudioSettings");
        audioTab.setLabel("Audio");
        audioTab.setHeight(TAB_HEIGHT);

        renderTab = cp5.addTab("RenderSettings");
        renderTab.setLabel("Rendering");
        renderTab.setHeight(TAB_HEIGHT);

        presetsTab = cp5.addTab("PresetSettings");
        presetsTab.setLabel("Presets");
        presetsTab.setHeight(TAB_HEIGHT);
    }

    private void setupPanels() {
        // Initialize all setting panels
        panels.add(new BallSettingsPanel(applet, cp5, settings, simulationApp, ballTab, SIDEBAR_WIDTH, PANEL_MARGIN));
        panels.add(new PhysicsSettingsPanel(applet, cp5, settings, simulationApp, physicsTab, SIDEBAR_WIDTH, PANEL_MARGIN));
        panels.add(new EffectsSettingsPanel(applet, cp5, settings, simulationApp, effectsTab, SIDEBAR_WIDTH, PANEL_MARGIN));
        panels.add(new AudioSettingsPanel(applet, cp5, settings, simulationApp, audioTab, SIDEBAR_WIDTH, PANEL_MARGIN));
        panels.add(new RenderingSettingsPanel(applet, cp5, settings, simulationApp, renderTab, SIDEBAR_WIDTH, PANEL_MARGIN));
    }

    private void styleUI() {
        // Set global UI styles
        cp5.setColorBackground(COLOR_BACKGROUND);
        cp5.setColorForeground(COLOR_ACCENT);
        cp5.setColorActive(COLOR_ACCENT);
        // Set label color for each controller manually
        // Or use this global approach if your ControlP5 version supports it:
        cp5.setColorCaptionLabel(COLOR_TEXT);

        // Style tabs
        ControllerList tabs = cp5.getWindow().getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = (Tab) tabs.get(i);
            tab.setColorBackground(COLOR_TAB_INACTIVE);
            tab.setColorActive(COLOR_TAB_ACTIVE);
            tab.getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);
        }

    }

    /**
     * Draw the GUI components
     */
    public void draw() {
        if (!guiVisible) return;

        applet.pushStyle();

        // Draw sidebar background
        applet.fill(COLOR_PANEL);
        applet.noStroke();
        applet.rect(0, TAB_HEIGHT, SIDEBAR_WIDTH, applet.height - TAB_HEIGHT);

        // Draw header in sidebar
        applet.fill(COLOR_ACCENT);
        applet.rect(0, TAB_HEIGHT, SIDEBAR_WIDTH, HEADER_HEIGHT);
        applet.fill(COLOR_TEXT);
        applet.textFont(headerFont);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.text("Simulation Controls", SIDEBAR_WIDTH / 2, TAB_HEIGHT + HEADER_HEIGHT / 2);

        // Draw current panel
        for (SettingsPanel panel : panels) {
            if (panel.isVisible()) {
                panel.draw();
            }
        }

        // Draw preset manager and render controller if on appropriate tabs
        if (cp5.getWindow().getCurrentTab() == presetsTab) {
            presetManager.draw();
        } else if (cp5.getWindow().getCurrentTab() == renderTab) {
            renderController.draw();
        }

        // Draw status bar
        drawStatusBar();

        applet.popStyle();
    }

    private void drawStatusBar() {
        int statusBarHeight = 24;
        applet.fill(COLOR_PANEL);
        applet.noStroke();
        applet.rect(0, applet.height - statusBarHeight, applet.width, statusBarHeight);

        applet.fill(COLOR_TEXT);
        applet.textFont(uiFont);
        applet.textAlign(PApplet.LEFT, PApplet.CENTER);

        // Display FPS and other stats
        String statusText = "FPS: " + Math.round(applet.frameRate);
        statusText += " | Press 'H' to toggle GUI";

        applet.text(statusText, PANEL_MARGIN, applet.height - statusBarHeight / 2);
    }

    /**
     * Toggle GUI visibility
     */
    public void toggleVisibility() {
        guiVisible = !guiVisible;
        cp5.setVisible(guiVisible);
    }

    /**
     * Handle key press events for GUI shortcuts
     */
    public void keyPressed() {
        if (applet.key == 'h' || applet.key == 'H') {
            toggleVisibility();
        }

        // Handle preset shortcuts (F1-F8 for loading presets)
        if (applet.key == PApplet.CODED) {
            if (applet.keyCode >= KEY_F1 && applet.keyCode <= KEY_F8) {
                int presetIndex = applet.keyCode - KEY_F1;
                presetManager.loadPresetByIndex(presetIndex);
            }

        }
    }

    /**
     * Update all panels
     */
    public void update() {
        for (SettingsPanel panel : panels) {
            panel.update();
        }
        presetManager.update();
        renderController.update();
    }

    // Add this method to the GuiManager class
    public int getSidebarWidth() {
        return SIDEBAR_WIDTH;
    }
}