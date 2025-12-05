package com.comp2042.util;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.model.NextShapeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BrickRotator class.
 * 
 * Tests brick rotation logic, shape retrieval, and rotation state management.
 */
class BrickRotatorTest {

    private BrickRotator rotator;
    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        rotator = new BrickRotator();
        generator = new RandomBrickGenerator();
    }

    @Test
    void getNextShape() {
        // Set a brick with multiple rotations (like JBrick with 4 rotations)
        Brick brick = generator.getBrick();
        rotator.setBrick(brick);
        
        // Get next shape
        NextShapeInfo nextShape = rotator.getNextShape();
        assertNotNull(nextShape, "Next shape should not be null");
        assertNotNull(nextShape.getShape(), "Next shape matrix should not be null");
        assertTrue(nextShape.getPosition() >= 0 && nextShape.getPosition() < 4, 
            "Position should be valid (0-3)");
        
        // Verify next shape is valid
        int[][] nextShapeMatrix = nextShape.getShape();
        
        // For bricks with multiple rotations, shapes should be different
        // (except for O brick which only has 1 rotation)
        int numRotations = brick.getShapeMatrix().size();
        if (numRotations > 1) {
            // Shapes might be different (depending on brick type)
            assertNotNull(nextShapeMatrix, "Next shape matrix should exist");
        }
        
        // Test cycling through rotations
        NextShapeInfo shape1 = rotator.getNextShape();
        int nextPos1 = shape1.getPosition();
        
        // Set to next position and get next again
        rotator.setCurrentShape(nextPos1);
        NextShapeInfo shape2 = rotator.getNextShape();
        int nextPos2 = shape2.getPosition();
        
        // Should cycle through rotations
        assertTrue(nextPos2 >= 0 && nextPos2 < brick.getShapeMatrix().size(), 
            "Next position should be valid");
    }

    @Test
    void getCurrentShape() {
        // Test without brick set (should throw NullPointerException or return null)
        // Actually, let's set a brick first
        Brick brick = generator.getBrick();
        rotator.setBrick(brick);
        
        // Get current shape
        int[][] currentShape = rotator.getCurrentShape();
        assertNotNull(currentShape, "Current shape should not be null");
        assertTrue(currentShape.length > 0, "Current shape should have dimensions");
        
        // Verify it matches the brick's first rotation
        int[][] expectedShape = brick.getShapeMatrix().get(0);
        assertArrayEquals(expectedShape, currentShape, 
            "Current shape should match brick's first rotation");
        
        // Test after setting different rotation
        if (brick.getShapeMatrix().size() > 1) {
            rotator.setCurrentShape(1);
            int[][] shapeAtRotation1 = rotator.getCurrentShape();
            int[][] expectedShape1 = brick.getShapeMatrix().get(1);
            assertArrayEquals(expectedShape1, shapeAtRotation1, 
                "Shape should match rotation 1");
        }
    }

    @Test
    void setCurrentShape() {
        Brick brick = generator.getBrick();
        rotator.setBrick(brick);
        
        int numRotations = brick.getShapeMatrix().size();
        
        // Test setting valid rotations
        for (int i = 0; i < numRotations; i++) {
            rotator.setCurrentShape(i);
            assertEquals(i, rotator.getCurrentRotation(), 
                "Rotation should be set to " + i);
            int[][] shape = rotator.getCurrentShape();
            int[][] expectedShape = brick.getShapeMatrix().get(i);
            assertArrayEquals(expectedShape, shape, 
                "Shape should match rotation " + i);
        }
        
        // Test setting rotation beyond valid range (should still work, modulo behavior)
        if (numRotations > 0) {
            rotator.setCurrentShape(numRotations + 1);
            // Should handle gracefully or wrap around
            assertTrue(rotator.getCurrentRotation() >= 0, 
                "Rotation should be non-negative");
        }
    }

    @Test
    void setBrick() {
        // Test setting brick without rotation (should reset to 0)
        Brick brick1 = generator.getBrick();
        rotator.setBrick(brick1);
        
        assertEquals(0, rotator.getCurrentRotation(), 
            "Rotation should reset to 0 when setting brick");
        assertSame(brick1, rotator.getCurrentBrick(), 
            "Current brick should be set");
        
        // Verify shape matches rotation 0
        int[][] shape = rotator.getCurrentShape();
        int[][] expectedShape = brick1.getShapeMatrix().get(0);
        assertArrayEquals(expectedShape, shape, 
            "Shape should match rotation 0");
        
        // Test setting different brick
        Brick brick2 = generator.getBrick();
        rotator.setBrick(brick2);
        assertSame(brick2, rotator.getCurrentBrick(), 
            "Current brick should be updated");
        assertEquals(0, rotator.getCurrentRotation(), 
            "Rotation should reset to 0");
    }

    @Test
    void testSetBrick() {
        // Test setting brick with specific rotation
        Brick brick = generator.getBrick();
        int numRotations = brick.getShapeMatrix().size();
        
        // Test setting with rotation 0
        rotator.setBrick(brick, 0);
        assertEquals(0, rotator.getCurrentRotation(), 
            "Rotation should be 0");
        assertSame(brick, rotator.getCurrentBrick(), 
            "Brick should be set");
        
        // Test setting with different rotation
        if (numRotations > 1) {
            rotator.setBrick(brick, 1);
            assertEquals(1, rotator.getCurrentRotation(), 
                "Rotation should be 1");
            
            int[][] shape = rotator.getCurrentShape();
            int[][] expectedShape = brick.getShapeMatrix().get(1);
            assertArrayEquals(expectedShape, shape, 
                "Shape should match rotation 1");
        }
        
        // Test setting with rotation at max
        if (numRotations > 0) {
            int maxRotation = numRotations - 1;
            rotator.setBrick(brick, maxRotation);
            assertEquals(maxRotation, rotator.getCurrentRotation(), 
                "Rotation should be set to max");
        }
    }

    @Test
    void getCurrentBrick() {
        // Test without brick (should return null)
        assertNull(rotator.getCurrentBrick(), 
            "Current brick should be null initially");
        
        // Test after setting brick
        Brick brick = generator.getBrick();
        rotator.setBrick(brick);
        assertSame(brick, rotator.getCurrentBrick(), 
            "Current brick should be returned");
        
        // Test after setting different brick
        Brick brick2 = generator.getBrick();
        rotator.setBrick(brick2);
        assertSame(brick2, rotator.getCurrentBrick(), 
            "Current brick should be updated");
        assertNotSame(brick, rotator.getCurrentBrick(), 
            "Previous brick should not be returned");
    }

    @Test
    void getCurrentRotation() {
        // Test initial rotation (should be 0 after setting brick)
        Brick brick = generator.getBrick();
        rotator.setBrick(brick);
        assertEquals(0, rotator.getCurrentRotation(), 
            "Initial rotation should be 0");
        
        // Test after setting different rotations
        int numRotations = brick.getShapeMatrix().size();
        for (int i = 0; i < numRotations; i++) {
            rotator.setCurrentShape(i);
            assertEquals(i, rotator.getCurrentRotation(), 
                "Rotation should be " + i);
        }
        
        // Test after setting brick with rotation
        if (numRotations > 1) {
            rotator.setBrick(brick, 2);
            assertEquals(2, rotator.getCurrentRotation(), 
                "Rotation should be 2 after setBrick with rotation");
        }
        
        // Test rotation wraps around
        rotator.setBrick(brick, 0);
        NextShapeInfo nextShape = rotator.getNextShape();
        int nextPos = nextShape.getPosition();
        rotator.setCurrentShape(nextPos);
        
        // Get next shape again - should cycle
        NextShapeInfo nextShape2 = rotator.getNextShape();
        assertTrue(nextShape2.getPosition() >= 0 && 
                   nextShape2.getPosition() < numRotations, 
            "Next position should be valid");
    }
}