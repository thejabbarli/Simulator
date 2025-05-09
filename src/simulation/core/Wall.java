package simulation.core;

import processing.core.PApplet;

public abstract class Wall implements Collidable {
    protected final float thickness;

    protected Wall(float thickness) {
        this.thickness = thickness;
    }

    public float getThickness() {
        return thickness;
    }

    public abstract void display(PApplet app);
}
