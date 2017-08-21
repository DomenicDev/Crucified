package de.gamedevbaden.crucified.appstates.paging;

/**
 * The whole game world is separated into several chunks where each chunk
 * has its own chunk position in the world. This ChunkPosition does not
 * have anything to do with the real world space position of the chunk.
 * Chunks are aligned like a grid, a 2D array basically. The ChunkPosition
 * represents the position in this grid.
 */
public class ChunkPosition {

    private int x;
    private int y;

    ChunkPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
