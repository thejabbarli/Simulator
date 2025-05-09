    package simulation.core;

    public interface Collidable {
        boolean checkCollision(Ball ball);
        void resolveCollision(Ball ball);
    }
