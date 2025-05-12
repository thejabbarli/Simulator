package simulation;

import processing.core.PApplet;
import simulation.core.SimulationApp;

/**
 * Launcher class for the bounce simulation
 */
public class BounceSimulationLauncher {

    /**
     * Main entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Launching Bounce Simulation...");

        // Set Processing rendering options
        System.setProperty("sun.java2d.opengl", "true"); // Enable OpenGL acceleration

        // Launch the simulation
        PApplet.main(new String[]{"--present", SimulationApp.class.getName()});
    }
}