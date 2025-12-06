package com.comp2042.util;

import com.comp2042.model.NextShapeInfo;
import com.comp2042.logic.bricks.Brick;

/**
 * Manages brick rotation state and provides rotation information.
 * 
 * <p>This class handles the rotation state of the current falling piece,
 * tracking which rotation state (0-3) is currently active and providing
 * methods to get the next rotation state for preview purposes.
 * 
 * <p>Each brick can have multiple rotation states, typically 4 (0°, 90°, 180°, 270°).
 * The rotator cycles through these states when rotation is requested.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see com.comp2042.logic.bricks.Brick
 * @see com.comp2042.model.NextShapeInfo
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    public void setBrick(Brick brick, int rotation) {
        this.brick = brick;
        this.currentShape = rotation;
    }

    public Brick getCurrentBrick() {
        return brick;
    }

    public int getCurrentRotation() {
        return currentShape;
    }

}
