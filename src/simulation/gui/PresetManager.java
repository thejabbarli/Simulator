package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import simulation.config.SettingsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages simulation presets - saving, loading, and managing configurations
 */
public class PresetManager {
    private final PApplet applet;
    private final ControlP5 cp5;
    private final SettingsManager settings;
    private final int sidebarWidth;
    private final int margin;

    private List<String> presetNames = new ArrayList<>();
    private String currentPresetName = "Default";
    private ScrollableList presetList;
    private Textfield presetNameField;
    private boolean presetModified = false;

    // UI styling constants
    private static final int COLOR_BACKGROUND = 0xFF303030;
    private static final int COLOR_HEADER = 0xFF00B4D8;
    private static final int COLOR_TEXT = 0xFFEEEEEE;
    private static final int CONTROL_HEIGHT = 24;
    private static final int PANEL_WIDTH = 300;
    private static final int BUTTON_WIDTH = 120;
    private static final String PRESETS_DIRECTORY = "presets";
    private static final String PRESETS_FILE = "presets/presets.json";

    /**
     * Create a new preset manager
     * @param applet The PApplet instance
     * @param cp5 The ControlP5 instance
     * @param settings The settings manager
     * @param sidebarWidth Width of the sidebar
     * @param margin Margin around controls
     */
    public PresetManager(PApplet applet, ControlP5 cp5, SettingsManager settings,
                         int sidebarWidth, int margin) {
        this.applet = applet;
        this.cp5 = cp5;
        this.settings = settings;
        this.sidebarWidth = sidebarWidth;
        this.margin = margin;

        // Ensure presets directory exists
        File presetDir = new File(applet.dataPath(PRESETS_DIRECTORY));
        if (!presetDir.exists()) {
            presetDir.mkdir();
        }

        initializeUI();
        loadPresetsList();
    }

    private void initializeUI() {
        int groupX = sidebarWidth + margin;
        int groupY = 50;
        int groupWidth = PANEL_WIDTH;
        int groupHeight = 400;

        // Create preset group
        Group presetGroup = cp5.addGroup("presetGroup")
                .setPosition(groupX, groupY)
                .setWidth(groupWidth)
                .setBackgroundHeight(groupHeight)
                .setBackgroundColor(COLOR_BACKGROUND)
                .setLabel("Simulation Presets")
                .setTab("PresetSettings")
                .disableCollapse();

        // Create preset list
        presetList = cp5.addScrollableList("presetList")
                .setPosition(margin, 30)
                .setSize(groupWidth - margin * 2, 150)
                .setBarHeight(CONTROL_HEIGHT)
                .setItemHeight(25)
                .setLabel("Available Presets")
                .setGroup(presetGroup)
                .setType(ScrollableList.LIST)
                .onDoublePress(event -> {
                    int index = (int) presetList.getValue();
                    if (index >= 0 && index < presetNames.size()) {
                        loadPreset(presetNames.get(index));
                    }
                });

        // Preset name field
        presetNameField = cp5.addTextfield("presetNameField")
                .setPosition(margin, 200)
                .setSize(groupWidth - margin * 2 - BUTTON_WIDTH - 10, CONTROL_HEIGHT)
                .setLabel("Preset Name")
                .setGroup(presetGroup)
                .setText(currentPresetName);

        // Save button
        cp5.addButton("savePresetButton")
                .setPosition(margin + presetNameField.getWidth() + 10, 200)
                .setSize(BUTTON_WIDTH, CONTROL_HEIGHT)
                .setLabel("Save Preset")
                .setGroup(presetGroup)
                .onPress(event -> saveCurrentPreset());

        // Load button
        cp5.addButton("loadPresetButton")
                .setPosition(margin, 250)
                .setSize(BUTTON_WIDTH, CONTROL_HEIGHT + 10)
                .setLabel("Load Selected")
                .setGroup(presetGroup)
                .onPress(event -> {
                    int index = (int) presetList.getValue();
                    if (index >= 0 && index < presetNames.size()) {
                        loadPreset(presetNames.get(index));
                    }
                });

        // Delete button
        cp5.addButton("deletePresetButton")
                .setPosition(margin + BUTTON_WIDTH + 10, 250)
                .setSize(BUTTON_WIDTH, CONTROL_HEIGHT + 10)
                .setLabel("Delete Selected")
                .setGroup(presetGroup)
                .onPress(event -> {
                    int index = (int) presetList.getValue();
                    if (index >= 0 && index < presetNames.size()) {
                        deletePreset(presetNames.get(index));
                    }
                });

        // Export all button
        cp5.addButton("exportAllPresetsButton")
                .setPosition(margin, 320)
                .setSize(BUTTON_WIDTH, CONTROL_HEIGHT + 10)
                .setLabel("Export All")
                .setGroup(presetGroup)
                .onPress(event -> exportAllPresets());

        // Import button
        cp5.addButton("importPresetsButton")
                .setPosition(margin + BUTTON_WIDTH + 10, 320)
                .setSize(BUTTON_WIDTH, CONTROL_HEIGHT + 10)
                .setLabel("Import Presets")
                .setGroup(presetGroup)
                .onPress(event -> importPresets());
    }

    /**
     * Load the list of available presets
     */
    private void loadPresetsList() {
        presetNames.clear();

        // Check if presets file exists
        File presetsFile = new File(applet.dataPath(PRESETS_FILE));
        if (presetsFile.exists()) {
            try {
                JSONArray presetsArray = applet.loadJSONArray(PRESETS_FILE);
                for (int i = 0; i < presetsArray.size(); i++) {
                    JSONObject preset = presetsArray.getJSONObject(i);
                    presetNames.add(preset.getString("name"));
                }
            } catch (Exception e) {
                System.err.println("Error loading presets list: " + e.getMessage());
            }
        }

        // Always ensure we have at least a default preset
        if (presetNames.isEmpty()) {
            presetNames.add("Default");
            saveCurrentPreset();
        }

        // Update the UI list
        updatePresetListUI();
    }

    /**
     * Update the preset list in the UI
     */
    private void updatePresetListUI() {
        presetList.clear(); // Use clear() instead of clearItems()
        for (int i = 0; i < presetNames.size(); i++) {
            presetList.addItem(presetNames.get(i), i);

            // Highlight current preset
            if (presetNames.get(i).equals(currentPresetName)) {
                presetList.setValue(i);
            }
        }
    }

    /**
     * Save the current settings as a preset
     */
    private void saveCurrentPreset() {
        String presetName = presetNameField.getText().trim();
        if (presetName.isEmpty()) {
            presetName = "Preset_" + System.currentTimeMillis();
            presetNameField.setText(presetName);
        }

        currentPresetName = presetName;

        // Create JSON for this preset
        JSONObject presetJson = new JSONObject();
        presetJson.setString("name", presetName);
        presetJson.setJSONObject("settings", settings.toJSON());

        // Load existing presets
        JSONArray presetsArray;
        File presetsFile = new File(applet.dataPath(PRESETS_FILE));
        if (presetsFile.exists()) {
            presetsArray = applet.loadJSONArray(PRESETS_FILE);
        } else {
            presetsArray = new JSONArray();
        }

        // Check if preset with this name already exists
        boolean found = false;
        for (int i = 0; i < presetsArray.size(); i++) {
            JSONObject existing = presetsArray.getJSONObject(i);
            if (existing.getString("name").equals(presetName)) {
                presetsArray.setJSONObject(i, presetJson);
                found = true;
                break;
            }
        }

        // If not found, add as new
        if (!found) {
            presetsArray.append(presetJson);
            presetNames.add(presetName);
        }

        // Save to file
        applet.saveJSONArray(presetsArray, PRESETS_FILE);
        presetModified = false;

        updatePresetListUI();
    }

    /**
     * Load a preset by name
     * @param presetName The name of the preset to load
     */
    public void loadPreset(String presetName) {
        // Check for unsaved changes
        if (presetModified) {
            // TODO: Prompt user to save changes
        }

        File presetsFile = new File(applet.dataPath(PRESETS_FILE));
        if (presetsFile.exists()) {
            try {
                JSONArray presetsArray = applet.loadJSONArray(PRESETS_FILE);
                for (int i = 0; i < presetsArray.size(); i++) {
                    JSONObject preset = presetsArray.getJSONObject(i);
                    if (preset.getString("name").equals(presetName)) {
                        JSONObject settingsJson = preset.getJSONObject("settings");
                        settings.fromJSON(settingsJson);
                        currentPresetName = presetName;
                        presetNameField.setText(presetName);
                        presetModified = false;

                        // Update UI to match loaded settings
                        updateUIFromSettings();
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading preset: " + e.getMessage());
            }
        }

        System.err.println("Preset not found: " + presetName);
    }

    /**
     * Load a preset by index in the presets list
     * @param index The index of the preset to load
     */
    public void loadPresetByIndex(int index) {
        if (index >= 0 && index < presetNames.size()) {
            loadPreset(presetNames.get(index));
        }
    }

    /**
     * Delete a preset by name
     * @param presetName The name of the preset to delete
     */
    private void deletePreset(String presetName) {
        if (presetNames.size() <= 1) {
            System.out.println("Cannot delete the only preset");
            return;
        }

        File presetsFile = new File(applet.dataPath(PRESETS_FILE));
        if (presetsFile.exists()) {
            try {
                JSONArray presetsArray = applet.loadJSONArray(PRESETS_FILE);
                JSONArray newPresetsArray = new JSONArray();

                for (int i = 0; i < presetsArray.size(); i++) {
                    JSONObject preset = presetsArray.getJSONObject(i);
                    if (!preset.getString("name").equals(presetName)) {
                        newPresetsArray.append(preset);
                    }
                }

                applet.saveJSONArray(newPresetsArray, PRESETS_FILE);
                presetNames.remove(presetName);

                // If we deleted the current preset, load the first available one
                if (presetName.equals(currentPresetName) && presetNames.size() > 0) {
                    loadPreset(presetNames.get(0));
                }

                updatePresetListUI();
            } catch (Exception e) {
                System.err.println("Error deleting preset: " + e.getMessage());
            }
        }
    }

    /**
     * Export all presets to an external file
     */
    private void exportAllPresets() {
        String timestamp = PApplet.nf(applet.year(), 4) + PApplet.nf(applet.month(), 2) +
                PApplet.nf(applet.day(), 2) + "_" + PApplet.nf(applet.hour(), 2) +
                PApplet.nf(applet.minute(), 2);
        String exportPath = "presets/export_" + timestamp + ".json";

        File presetsFile = new File(applet.dataPath(PRESETS_FILE));
        if (presetsFile.exists()) {
            try {
                applet.saveJSONArray(applet.loadJSONArray(PRESETS_FILE), exportPath);
                System.out.println("Presets exported to: " + exportPath);
            } catch (Exception e) {
                System.err.println("Error exporting presets: " + e.getMessage());
            }
        }
    }

    /**
     * Import presets from an external file
     */
    private void importPresets() {
        // Note: In a real implementation, you would use a file selector dialog
        // For simplicity, we'll just look for import.json in the data directory
        String importPath = "presets/import.json";
        File importFile = new File(applet.dataPath(importPath));

        if (importFile.exists()) {
            try {
                JSONArray importedPresets = applet.loadJSONArray(importPath);
                JSONArray currentPresets = applet.loadJSONArray(PRESETS_FILE);

                // Add all imported presets that don't conflict
                for (int i = 0; i < importedPresets.size(); i++) {
                    JSONObject importedPreset = importedPresets.getJSONObject(i);
                    String name = importedPreset.getString("name");

                    // Check for name conflicts
                    boolean conflict = false;
                    for (int j = 0; j < currentPresets.size(); j++) {
                        if (currentPresets.getJSONObject(j).getString("name").equals(name)) {
                            conflict = true;
                            break;
                        }
                    }

                    // If no conflict or renamed, add it
                    if (!conflict) {
                        currentPresets.append(importedPreset);
                        presetNames.add(name);
                    } else {
                        // Rename the preset by appending a timestamp
                        String newName = name + "_" + System.currentTimeMillis();
                        importedPreset.setString("name", newName);
                        currentPresets.append(importedPreset);
                        presetNames.add(newName);
                    }
                }

                applet.saveJSONArray(currentPresets, PRESETS_FILE);
                updatePresetListUI();

                System.out.println("Imported " + importedPresets.size() + " presets");
            } catch (Exception e) {
                System.err.println("Error importing presets: " + e.getMessage());
            }
        } else {
            System.err.println("Import file not found: " + importPath);
        }
    }

    /**
     * Update UI controls to match the current settings
     */
    private void updateUIFromSettings() {
        for (ControllerInterface<?> controller : cp5.getAll()) {
            if (controller instanceof Slider) {
                Slider slider = (Slider) controller;
                String name = slider.getName();

                switch (name) {
                    case "ballRadius":
                        slider.setValue(settings.getBallRadius());
                        break;
                    case "ballMass":
                        slider.setValue(settings.getBallMass());
                        break;
                    case "ballStroke":
                        slider.setValue(settings.getBallStroke());
                        break;
                    case "gravity":
                        slider.setValue(settings.getGravity());
                        break;
                    case "growthAmount":
                        slider.setValue(settings.getGrowthAmount());
                        break;
                    case "speedBoostFactor":
                        slider.setValue(settings.getSpeedBoostFactor());
                        break;
                }
            }

            if (controller instanceof Toggle) {
                Toggle toggle = (Toggle) controller;
                String name = toggle.getName();

                switch (name) {
                    case "permanentTraces":
                        toggle.setState(settings.getPermanentTraces());
                        break;
                    case "traceEnabled":
                        toggle.setState(true); // or settings.getTraceEnabled() if you have it
                        break;
                    case "shouldStop":
                        toggle.setState(settings.getShouldStop());
                        break;
                    case "shouldShrink":
                        toggle.setState(settings.getShouldShrink());
                        break;
                    case "enforceWallBoundary":
                        toggle.setState(settings.isEnforceWallBoundaryLimit());
                        break;
                    case "rainbowMode":
                        toggle.setState(true); // or settings.getRainbowMode()
                        break;
                }
            }
        }
    }



    /**
     * Mark the current preset as modified
     */
    public void markModified() {
        presetModified = true;
    }

    /**
     * Draw the preset manager UI
     */
    public void draw() {
        // Draw panel header
        applet.fill(COLOR_HEADER);
        applet.noStroke();
        applet.rect(sidebarWidth, 0, applet.width - sidebarWidth, 40);

        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(18);
        applet.text("Preset Management", sidebarWidth + (applet.width - sidebarWidth) / 2, 20);

        // Draw preset info
        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.LEFT, PApplet.CENTER);
        applet.textSize(14);
        String statusText = "Current Preset: " + currentPresetName;
        if (presetModified) {
            statusText += " (Modified)";
        }
        applet.text(statusText, sidebarWidth + PANEL_WIDTH + margin * 2, 70);

        // Draw help text
        applet.textSize(12);
        String helpText = "• Double-click a preset to load it\n";
        helpText += "• F1-F8 keys can be used to quickly load the first 8 presets\n";
        helpText += "• Save your changes before loading a different preset\n";
        helpText += "• Export your presets to share with others";

        applet.text(helpText, sidebarWidth + PANEL_WIDTH + margin * 2, 100);
    }

    /**
     * Update the preset manager state
     */
    public void update() {
        // Check if settings have changed since last save
        if (!presetModified) {
            // Ideally, we'd compare current settings to saved settings
            // For now, we'll mark modified whenever a setting changes
            // This is handled by controller event callbacks
        }
    }
}