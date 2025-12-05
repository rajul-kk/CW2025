package com.comp2042.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ViewData class.
 * 
 * Tests data encapsulation, defensive copying, and position getters.
 */
class ViewDataTest {

    @Test
    void getBrickData() {
        // Create test data
        int[][] brickData = {
            {1, 1},
            {1, 1}
        };
        int[][] nextBrickData = {
            {2, 2, 2}
        };
        
        ViewData viewData = new ViewData(brickData, 5, 10, nextBrickData);
        
        // Test retrieval
        int[][] retrieved = viewData.getBrickData();
        assertNotNull(retrieved, "getBrickData should not return null");
        assertEquals(2, retrieved.length, "Brick data should have correct height");
        assertEquals(2, retrieved[0].length, "Brick data should have correct width");
        assertEquals(1, retrieved[0][0], "Brick data should preserve values");
        
        // Test defensive copying - modify retrieved data
        retrieved[0][0] = 999;
        int[][] retrieved2 = viewData.getBrickData();
        assertEquals(1, retrieved2[0][0], 
            "Original brick data should not be affected by modification");
        
        // Test multiple retrievals return independent copies
        int[][] copy1 = viewData.getBrickData();
        int[][] copy2 = viewData.getBrickData();
        copy1[0][0] = 888;
        assertNotEquals(copy1[0][0], copy2[0][0], 
            "Multiple retrievals should return independent copies");
    }

    @Test
    void getxPosition() {
        // Test various X positions
        int[][] brickData = {{1}};
        int[][] nextBrickData = {{2}};
        
        ViewData viewData1 = new ViewData(brickData, 0, 0, nextBrickData);
        assertEquals(0, viewData1.getxPosition(), "X position should be 0");
        
        ViewData viewData2 = new ViewData(brickData, 5, 0, nextBrickData);
        assertEquals(5, viewData2.getxPosition(), "X position should be 5");
        
        ViewData viewData3 = new ViewData(brickData, 10, 0, nextBrickData);
        assertEquals(10, viewData3.getxPosition(), "X position should be 10");
        
        // Test negative position (edge case)
        ViewData viewData4 = new ViewData(brickData, -1, 0, nextBrickData);
        assertEquals(-1, viewData4.getxPosition(), "X position should handle negative values");
    }

    @Test
    void getyPosition() {
        // Test various Y positions
        int[][] brickData = {{1}};
        int[][] nextBrickData = {{2}};
        
        ViewData viewData1 = new ViewData(brickData, 0, 0, nextBrickData);
        assertEquals(0, viewData1.getyPosition(), "Y position should be 0");
        
        ViewData viewData2 = new ViewData(brickData, 0, 5, nextBrickData);
        assertEquals(5, viewData2.getyPosition(), "Y position should be 5");
        
        ViewData viewData3 = new ViewData(brickData, 0, 20, nextBrickData);
        assertEquals(20, viewData3.getyPosition(), "Y position should be 20");
        
        // Test negative position (edge case)
        ViewData viewData4 = new ViewData(brickData, 0, -1, nextBrickData);
        assertEquals(-1, viewData4.getyPosition(), "Y position should handle negative values");
    }

    @Test
    void getNextBrickData() {
        // Create test data
        int[][] brickData = {
            {1, 1}
        };
        int[][] nextBrickData = {
            {2, 2, 2},
            {0, 2, 0}
        };
        
        ViewData viewData = new ViewData(brickData, 0, 0, nextBrickData);
        
        // Test retrieval
        int[][] retrieved = viewData.getNextBrickData();
        assertNotNull(retrieved, "getNextBrickData should not return null");
        assertEquals(2, retrieved.length, "Next brick data should have correct height");
        assertEquals(3, retrieved[0].length, "Next brick data should have correct width");
        assertEquals(2, retrieved[0][0], "Next brick data should preserve values");
        
        // Test defensive copying - modify retrieved data
        retrieved[0][0] = 999;
        int[][] retrieved2 = viewData.getNextBrickData();
        assertEquals(2, retrieved2[0][0], 
            "Original next brick data should not be affected by modification");
        
        // Test multiple retrievals return independent copies
        int[][] copy1 = viewData.getNextBrickData();
        int[][] copy2 = viewData.getNextBrickData();
        copy1[0][0] = 888;
        assertNotEquals(copy1[0][0], copy2[0][0], 
            "Multiple retrievals should return independent copies");
    }

    @Test
    void testImmutability() {
        // Test that ViewData is immutable
        int[][] brickData = {{1, 2}, {3, 4}};
        int[][] nextBrickData = {{5, 6}};
        
        ViewData viewData = new ViewData(brickData, 5, 10, nextBrickData);
        
        // Get values multiple times
        int x1 = viewData.getxPosition();
        int y1 = viewData.getyPosition();
        int[][] brick1 = viewData.getBrickData();
        
        int x2 = viewData.getxPosition();
        int y2 = viewData.getyPosition();
        int[][] brick2 = viewData.getBrickData();
        
        assertEquals(x1, x2, "X position should be consistent");
        assertEquals(y1, y2, "Y position should be consistent");
        assertArrayEquals(brick1, brick2, "Brick data should be consistent");
    }

    @Test
    void testConstructor() {
        // Test constructor with various values
        int[][] brickData = {{1}};
        int[][] nextBrickData = {{2}};
        
        // Valid construction
        assertDoesNotThrow(() -> new ViewData(brickData, 0, 0, nextBrickData), 
            "Should construct with valid data");
        assertDoesNotThrow(() -> new ViewData(brickData, 10, 20, nextBrickData), 
            "Should construct with different positions");
        
        // Test with empty arrays
        int[][] emptyBrick = new int[0][0];
        int[][] emptyNext = new int[0][0];
        ViewData viewData = new ViewData(emptyBrick, 0, 0, emptyNext);
        assertNotNull(viewData, "Should construct with empty arrays");
        assertEquals(0, viewData.getBrickData().length, "Empty brick data should work");
    }
}