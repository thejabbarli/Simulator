package simulation.gui;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import simulation.core.SimulationApp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controls rendering modes and quality settings for the simulation
 */
public class RenderController {
    private final PApplet applet;
    private final ControlP5 cp5;
    private final SimulationApp simulationApp;
    private final int sidebarWidth;
    private final int margin;

    private Group renderGroup;
    private Group frameExportGroup;
    private PGraphics renderBuffer;
    private boolean highQualityMode = false;
    private boolean recordingFrames = false;
    private int recordingCounter = 0;
    private String exportFolderPath;
    private int renderScale = 1;
    private int renderWidth;
    private int renderHeight;
    private int framesPerSecond = 60;
    private int recordingDuration = 5; // seconds

    // UI styling constants
    private static final int COLOR_BACKGROUND = 0xFF303030;
    private static final int COLOR_HEADER = 0xFF00B4D8;
    private static final int COLOR_TEXT = 0xFFEEEEEE;
    private static final int COLOR_RECORD = 0xFFFF4444;
    private static final int CONTROL_HEIGHT = 24;
    private static final int PANEL_WIDTH = 300;

    /**
     * Create a new render controller
     * @param applet The PApplet instance
     * @param cp5 The ControlP5 instance
     * @param simulationApp The simulation app
     * @param sidebarWidth Width of the sidebar
     * @param margin Margin around controls
     */
    public RenderController(PApplet applet, ControlP5 cp5, SimulationApp simulationApp,
                            int sidebarWidth, int margin) {
        this.applet = applet;
        this.cp5 = cp5;
        this.simulationApp = simulationApp;
        this.sidebarWidth = sidebarWidth;
        this.margin = margin;

        // Create export directory if it doesn't exist
        File exportDir = new File(applet.sketchPath("export"));
        if (!exportDir.exists()) {
            exportDir.mkdir();
        }

        initializeUI();
        createRenderBuffer();
    }

    private void initializeUI() {
        int groupX = sidebarWidth + margin;
        int groupY = 50;
        int groupWidth = PANEL_WIDTH;
        int groupHeight = 220;

        // Create render quality group
        renderGroup = cp5.addGroup("renderGroup")
                .setPosition(groupX, groupY)
                .setWidth(groupWidth)
                .setBackgroundHeight(groupHeight)
                .setBackgroundColor(COLOR_BACKGROUND)
                .setLabel("Render Quality")
                .setTab("RenderSettings")
                .disableCollapse();

        // High quality toggle
        cp5.addToggle("highQualityMode")
                .setPosition(margin, 30)
                .setSize(50, 20)
                .setLabel("High Quality Mode")
                .setGroup(renderGroup)
                .setValue(highQualityMode)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    highQualityMode = c.getValue() > 0.5f;
                    if (highQualityMode) {
                        createRenderBuffer();
                    }
                });


        // Render scale slider
        cp5.addSlider("renderScale")
                .setPosition(margin, 70)
                .setSize(200, CONTROL_HEIGHT)
                .setRange(1, 4)
                .setValue(renderScale)
                .setNumberOfTickMarks(4)
                .setLabel("Render Scale")
                .setGroup(renderGroup)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    renderScale = (int) c.getValue();
                    createRenderBuffer();
                });


        // Anti-aliasing toggle
        cp5.addToggle("antiAliasing")
                .setPosition(margin, 110)
                .setSize(50, 20)
                .setLabel("Anti-aliasing")
                .setGroup(renderGroup)
                .setValue(true);

        // Frame rate slider
        cp5.addSlider("frameRate")
                .setPosition(margin, 150)
                .setSize(200, CONTROL_HEIGHT)
                .setRange(30, 120)
                .setValue(framesPerSecond)
                .setLabel("Target Frame Rate")
                .setGroup(renderGroup)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    framesPerSecond = (int) c.getValue();
                    applet.frameRate(framesPerSecond);
                });


        // Create frame export group
        frameExportGroup = cp5.addGroup("frameExportGroup")
                .setPosition(groupX, groupY + groupHeight + margin)
                .setWidth(groupWidth)
                .setBackgroundHeight(220)
                .setBackgroundColor(COLOR_BACKGROUND)
                .setLabel("Frame Export")
                .setTab("RenderSettings")
                .disableCollapse();

        // Recording duration slider
        cp5.addSlider("recordingDuration")
                .setPosition(margin, 30)
                .setSize(200, CONTROL_HEIGHT)
                .setRange(1, 30)
                .setValue(recordingDuration)
                .setLabel("Recording Duration (seconds)")
                .setGroup(frameExportGroup)
                .onChange(event -> {
                    Controller c = (Controller) event.getController();
                    recordingDuration = (int) c.getValue();
                });


        // Export resolution dropdown
        DropdownList resolutionDropdown = cp5.addDropdownList("exportResolution")
                .setPosition(margin, 70)
                .setSize(200, 120)
                .setBarHeight(CONTROL_HEIGHT)
                .setItemHeight(20)
                .setLabel("Export Resolution")
                .setGroup(frameExportGroup);

        resolutionDropdown.addItem("Same as Window", 0);
        resolutionDropdown.addItem("720p (1280x720)", 1);
        resolutionDropdown.addItem("1080p (1920x1080)", 2);
        resolutionDropdown.addItem("1440p (2560x1440)", 3);
        resolutionDropdown.setValue(0);

        // Include audio toggle
        cp5.addToggle("includeAudio")
                .setPosition(margin, 120)
                .setSize(50, 20)
                .setLabel("Include Audio")
                .setGroup(frameExportGroup)
                .setValue(true);

        // Start recording button
        cp5.addButton("startRecording")
                .setPosition(margin, 160)
                .setSize(200, 40)
                .setLabel("Start Recording")
                .setGroup(frameExportGroup)
                .onPress(event -> toggleRecording());
    }

    private void createRenderBuffer() {
        renderWidth = applet.width * renderScale;
        renderHeight = applet.height * renderScale;
        renderBuffer = applet.createGraphics(renderWidth, renderHeight, PApplet.P2D);

        // Set up the render buffer
        renderBuffer.smooth(8); // High quality anti-aliasing
        renderBuffer.beginDraw();
        renderBuffer.background(0);
        renderBuffer.endDraw();
    }

    /**
     * Toggle recording state
     */
    private void toggleRecording() {
        if (recordingFrames) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    /**
     * Start recording frames
     */
    private void startRecording() {
        // Create a timestamped folder for this recording
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        exportFolderPath = applet.sketchPath("export/recording_" + timestamp);

        File exportFolder = new File(exportFolderPath);
        if (!exportFolder.exists()) {
            exportFolder.mkdir();
        }

        // Create a frames subfolder
        File framesFolder = new File(exportFolderPath + "/frames");
        if (!framesFolder.exists()) {
            framesFolder.mkdir();
        }

        recordingCounter = 0;
        recordingFrames = true;

        // Update button label
        Button recordButton = (Button) cp5.getController("startRecording");
        recordButton.setLabel("Stop Recording");
        recordButton.setColorBackground(COLOR_RECORD);

        // Force high quality mode when recording
        cp5.getController("highQualityMode").setValue(1);
        highQualityMode = true;
        createRenderBuffer();

        // Save recording settings to a properties file
        String[] settings = {
                "fps=" + framesPerSecond,
                "duration=" + recordingDuration,
                "width=" + renderWidth,
                "height=" + renderHeight,
                "includeAudio=" + cp5.getController("includeAudio").getValue()
        };
        applet.saveStrings(exportFolderPath + "/recording_info.txt", settings);

        // Reset the simulation for clean recording
        simulationApp.resetSimulation();
    }

    /**
     * Stop recording frames
     */
    private void stopRecording() {
        recordingFrames = false;

        // Update button label
        Button recordButton = (Button) cp5.getController("startRecording");
        recordButton.setLabel("Start Recording");
        recordButton.setColorBackground(COLOR_HEADER);

        // Create a text file with FFmpeg command for the user
        createFFmpegScript();
    }

    /**
     * Create a script to convert frames to video
     */
    private void createFFmpegScript() {
        boolean includeAudio = cp5.getController("includeAudio").getValue() > 0.5f;

        String ffmpegCommand = "ffmpeg -r " + framesPerSecond + " -i frames/frame_%05d.png -c:v libx264 -crf 18 -pix_fmt yuv420p";

        if (includeAudio) {
            ffmpegCommand += " -i audio.wav -c:a aac -b:a 192k";
        }

        ffmpegCommand += " simulation_video.mp4";

        // Create a script file appropriate for the OS
        String[] scriptLines;
        String scriptName;

        if (PApplet.platform == PApplet.WINDOWS) {
            scriptLines = new String[] {
                    "@echo off",
                    "echo Converting frames to video...",
                    ffmpegCommand,
                    "echo Video created: simulation_video.mp4",
                    "pause"
            };
            scriptName = "create_video.bat";
        } else {
            scriptLines = new String[] {
                    "#!/bin/bash",
                    "echo Converting frames to video...",
                    ffmpegCommand,
                    "echo Video created: simulation_video.mp4"
            };
            scriptName = "create_video.sh";

            // Make the script executable on Unix systems
            try {
                File scriptFile = new File(exportFolderPath + "/" + scriptName);
                scriptFile.createNewFile();
                scriptFile.setExecutable(true);
            } catch (Exception e) {
                System.err.println("Error creating executable script: " + e.getMessage());
            }
        }

        applet.saveStrings(exportFolderPath + "/" + scriptName, scriptLines);

        // Show a message to the user
        System.out.println("Recording complete! " + recordingCounter + " frames saved to " + exportFolderPath);
        System.out.println("Use the " + scriptName + " script in that folder to create a video.");
    }

    /**
     * Draw the render controller UI
     */
    public void draw() {
        // Draw panel header
        applet.fill(COLOR_HEADER);
        applet.noStroke();
        applet.rect(sidebarWidth, 0, applet.width - sidebarWidth, 40);

        applet.fill(COLOR_TEXT);
        applet.textAlign(PApplet.CENTER, PApplet.CENTER);
        applet.textSize(18);
        applet.text("Rendering Settings", sidebarWidth + (applet.width - sidebarWidth) / 2, 20);

        // Draw recording status if active
        if (recordingFrames) {
            applet.fill(COLOR_RECORD);
            applet.noStroke();
            applet.ellipse(applet.width - 20, 20, 10, 10);

            applet.fill(COLOR_TEXT);
            applet.textAlign(PApplet.RIGHT, PApplet.CENTER);
            applet.textSize(14);

            // Calculate progress
            int totalFrames = framesPerSecond * recordingDuration;
            float progress = (float) recordingCounter / totalFrames;
            int secondsElapsed = recordingCounter / framesPerSecond;

            String recordingStatus = String.format("Recording: %d/%d frames (%.1f%%) - %ds/%ds",
                    recordingCounter, totalFrames, progress * 100, secondsElapsed, recordingDuration);

            applet.text(recordingStatus, applet.width - 40, 20);
        }

        // Draw rendering info
        if (highQualityMode) {
            applet.fill(COLOR_TEXT);
            applet.textAlign(PApplet.LEFT, PApplet.CENTER);
            applet.textSize(14);

            String renderInfo = String.format(
                    "High Quality Mode: Scale %dx (%dx%d) - %d FPS",
                    renderScale, renderWidth, renderHeight, framesPerSecond
            );

            applet.text(renderInfo, sidebarWidth + PANEL_WIDTH + margin * 2, 70);

            // Draw performance stats
            applet.textSize(12);
            String performanceInfo = String.format(
                    "Frame Time: %.1f ms",
                    1000.0f / applet.frameRate
            );

            applet.text(performanceInfo, sidebarWidth + PANEL_WIDTH + margin * 2, 100);
        }
    }

    /**
     * Update the render controller state
     */
    public void update() {
        if (recordingFrames) {
            recordFrame();

            // Check if recording duration is complete
            int totalFrames = framesPerSecond * recordingDuration;
            if (recordingCounter >= totalFrames) {
                stopRecording();
            }
        }
    }

    /**
     * Render a frame in high quality
     */
    public void renderHighQuality() {
        if (!highQualityMode) return;

        // Set up the render buffer
        renderBuffer.beginDraw();
        renderBuffer.background(0);

        // Scale everything to match the render buffer size
        float scaleX = (float) renderWidth / applet.width;
        float scaleY = (float) renderHeight / applet.height;
        renderBuffer.scale(scaleX, scaleY);

        // Render the simulation to the buffer
        simulationApp.drawToBuffer(renderBuffer);

        renderBuffer.endDraw();

        // Display the render buffer scaled back to window size
        applet.image(renderBuffer, 0, 0, applet.width, applet.height);
    }

    /**
     * Record a frame to disk
     */
    private void recordFrame() {
        if (!recordingFrames) return;

        // Save the current frame
        String frameFilename = String.format("%s/frames/frame_%05d.png", exportFolderPath, recordingCounter);
        renderBuffer.save(frameFilename);

        recordingCounter++;
    }

    /**
     * Check if high quality mode is enabled
     * @return True if high quality rendering is enabled
     */
    public boolean isHighQualityMode() {
        return highQualityMode;
    }

    /**
     * Get the render buffer for high quality rendering
     * @return The PGraphics render buffer
     */
    public PGraphics getRenderBuffer() {
        return renderBuffer;
    }
}