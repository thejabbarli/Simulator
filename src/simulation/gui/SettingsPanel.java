package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import simulation.config.SettingsManager;
import simulation.core.SimulationApp;

/**
 * Abstract base class for all settings panels in the GUI
 */
public abstract class SettingsPanel {
    protected final PApplet applet;
    protected final ControlP5 cp5;
    protected final SettingsManager settings;
    protected final SimulationApp simulationApp;
    protected final Tab parentTab;
    protected final int sidebarWidth;
    protected final int margin;

    // UI styling constants
    protected static final int COLOR_BACKGROUND = 0xFF303030;
    protected static final int COLOR_HEADER = 0xFF00B4D8;
    protected static final int COLOR_TEXT = 0xFFEEEEEE;
    protected static final int CONTROL_HEIGHT = 24;
    protected static final int CONTROL_SPACING = 6;
    protected static final int GROUP_SPACING = 15;
    protected static final int SLIDER_WIDTH = 150;
    protected static final int TOGGLE_WIDTH = 100;

    /**
     * Create a new settings panel
     * @param applet The PApplet instance
     * @param cp5 The ControlP5 instance
     * @param settings The settings manager
     * @param simulationApp The simulation app
     * @param parentTab The parent tab
     * @param sidebarWidth Width of the sidebar
     * @param margin Margin around controls
     */
    public SettingsPanel(PApplet applet, ControlP5 cp5, SettingsManager settings,
                         SimulationApp simulationApp, Tab parentTab,
                         int sidebarWidth, int margin) {
        this.applet = applet;
        this.cp5 = cp5;
        this.settings = settings;
        this.simulationApp = simulationApp;
        this.parentTab = parentTab;
        this.sidebarWidth = sidebarWidth;
        this.margin = margin;

        initializePanel();
    }

    /**
     * Initialize the panel controls
     */
    protected abstract void initializePanel();

    /**
     * Draw any custom elements of the panel
     */
    public abstract void draw();

    /**
     * Update panel state
     */
    public abstract void update();

    /**
     * Check if the panel is currently visible
     * @return True if the panel should be displayed
     */
    public boolean isVisible() {
        return cp5.getWindow().getCurrentTab() == parentTab;
    }

    /**
     * Create a group for organizing controls
     * @param name Group identifier
     * @param label Display name
     * @param y Y position
     * @param width Width of the group
     * @param height Height of the group
     * @return The created group
     */
    protected Group createControlGroup(String name, String label, int y, int width, int height) {
        Group group = cp5.addGroup(name)
                .setPosition(sidebarWidth + margin, y)
                .setWidth(width)
                .setBackgroundHeight(height)
                .setBackgroundColor(COLOR_BACKGROUND)
                .setLabel(label)
                .setTab(parentTab)
                .disableCollapse();

        return group;
    }

    /**
     * Create a slider with standard styling
     * @param name Slider identifier
     * @param label Display label
     * @param min Minimum value
     * @param max Maximum value
     * @param defaultValue Default value
     * @param group Parent group (or null for no group)
     * @param x X position (relative to group if in a group)
     * @param y Y position (relative to group if in a group)
     * @return The created slider
     */
    protected Slider createSlider(String name, String label, float min, float max,
                                  float defaultValue, ControllerGroup<?> group, int x, int y) {
        Slider slider = cp5.addSlider(name)
                .setRange(min, max)
                .setValue(defaultValue)
                .setPosition(x, y)
                .setSize(SLIDER_WIDTH, CONTROL_HEIGHT)
                .setLabel(label);

        if (group != null) {
            slider.setGroup(group);
        }

        if (parentTab != null) {
            slider.setTab(parentTab.getName());
        }

        return slider;
    }

    /**
     * Create a toggle button with standard styling
     * @param name Toggle identifier
     * @param label Display label
     * @param defaultValue Default state
     * @param group Parent group (or null for no group)
     * @param x X position (relative to group if in a group)
     * @param y Y position (relative to group if in a group)
     * @return The created toggle
     */
    protected Toggle createToggle(String name, String label, boolean defaultValue,
                                  ControllerGroup<?> group, int x, int y) {
        Toggle toggle = cp5.addToggle(name)
                .setState(defaultValue)
                .setPosition(x, y)
                .setSize(TOGGLE_WIDTH, CONTROL_HEIGHT)
                .setLabel(label)
                .setTab(parentTab.getName());

        if (group != null) {
            toggle.setGroup(group);
        }

        return toggle;
    }

    /**
     * Create a dropdown with standard styling
     * @param name Dropdown identifier
     * @param label Display label
     * @param items List of items
     * @param defaultIndex Default selected index
     * @param group Parent group (or null for no group)
     * @param x X position (relative to group if in a group)
     * @param y Y position (relative to group if in a group)
     * @return The created dropdown
     */
    protected DropdownList createDropdown(String name, String label, String[] items,
                                          int defaultIndex, ControllerGroup<?> group, int x, int y) {
        DropdownList dropdown = cp5.addDropdownList(name)
                .setPosition(x, y)
                .setSize(SLIDER_WIDTH, 120)
                .setItemHeight(20)
                .setBarHeight(CONTROL_HEIGHT)
                .setLabel(label)
                .setTab(parentTab.getName());

        for (int i = 0; i < items.length; i++) {
            dropdown.addItem(items[i], i);
        }

        dropdown.setValue(defaultIndex);

        if (group != null) {
            dropdown.setGroup(group);
        }

        return dropdown;
    }

    /**
     * Create a button with standard styling
     * @param name Button identifier
     * @param label Display label
     * @param group Parent group (or null for no group)
     * @param x X position (relative to group if in a group)
     * @param y Y position (relative to group if in a group)
     * @param width Button width
     * @param height Button height
     * @return The created button
     */
    protected Button createButton(String name, String label, ControllerGroup<?> group,
                                  int x, int y, int width, int height) {
        Button button = cp5.addButton(name)
                .setPosition(x, y)
                .setSize(width, height)
                .setLabel(label)
                .setTab(parentTab.getName());

        if (group != null) {
            button.setGroup(group);
        }

        return button;
    }
}