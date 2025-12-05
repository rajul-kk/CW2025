package com.comp2042.data;

import com.comp2042.util.ClearRow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DownData class.
 * 
 * Tests data encapsulation for down movement events, including cleared rows
 * and updated view data.
 */
class DownDataTest {

    @Test
    void getClearRow() {
        // Test with null clearRow (no rows cleared)
        int[][] brickData = {{1}};
        int[][] nextBrickData = {{2}};
        ViewData viewData = new ViewData(brickData, 5, 10, nextBrickData);
        
        DownData downData1 = new DownData(null, viewData);
        assertNull(downData1.getClearRow(), 
            "getClearRow should return null when no rows cleared");
        
        // Test with ClearRow object (rows were cleared)
        int[][] matrix = new int[10][20];
        ClearRow clearRow = new ClearRow(2, matrix, 200);
        DownData downData2 = new DownData(clearRow, viewData);
        
        ClearRow retrieved = downData2.getClearRow();
        assertNotNull(retrieved, "getClearRow should return ClearRow when rows cleared");
        assertEquals(2, retrieved.getLinesRemoved(), 
            "ClearRow should have correct lines removed");
        assertEquals(200, retrieved.getScoreBonus(), 
            "ClearRow should have correct score bonus");
        
        // Test that same instance is returned
        ClearRow retrieved2 = downData2.getClearRow();
        assertSame(retrieved, retrieved2, 
            "getClearRow should return same instance");
    }

    @Test
    void getViewData() {
        // Test with null viewData (game over)
        ClearRow clearRow = new ClearRow(0, new int[10][20], 0);
        
        DownData downData1 = new DownData(clearRow, null);
        assertNull(downData1.getViewData(), 
            "getViewData should return null when game is over");
        
        // Test with ViewData object (game continues)
        int[][] brickData = {
            {1, 1},
            {1, 1}
        };
        int[][] nextBrickData = {{2}};
        ViewData viewData = new ViewData(brickData, 5, 10, nextBrickData);
        
        DownData downData2 = new DownData(null, viewData);
        ViewData retrieved = downData2.getViewData();
        
        assertNotNull(retrieved, "getViewData should return ViewData when game continues");
        assertEquals(5, retrieved.getxPosition(), 
            "ViewData should have correct X position");
        assertEquals(10, retrieved.getyPosition(), 
            "ViewData should have correct Y position");
        assertNotNull(retrieved.getBrickData(), 
            "ViewData should have brick data");
        
        // Test that same instance is returned
        ViewData retrieved2 = downData2.getViewData();
        assertSame(retrieved, retrieved2, 
            "getViewData should return same instance");
    }

    @Test
    void testBothNull() {
        // Test with both null (edge case)
        DownData downData = new DownData(null, null);
        
        assertNull(downData.getClearRow(), 
            "ClearRow should be null");
        assertNull(downData.getViewData(), 
            "ViewData should be null");
    }

    @Test
    void testBothPresent() {
        // Test with both present (normal case)
        int[][] matrix = new int[10][20];
        ClearRow clearRow = new ClearRow(1, matrix, 50);
        
        int[][] brickData = {{1}};
        int[][] nextBrickData = {{2}};
        ViewData viewData = new ViewData(brickData, 3, 7, nextBrickData);
        
        DownData downData = new DownData(clearRow, viewData);
        
        assertNotNull(downData.getClearRow(), 
            "ClearRow should be present");
        assertNotNull(downData.getViewData(), 
            "ViewData should be present");
        
        assertEquals(1, downData.getClearRow().getLinesRemoved(), 
            "ClearRow should have correct data");
        assertEquals(3, downData.getViewData().getxPosition(), 
            "ViewData should have correct data");
    }

    @Test
    void testImmutability() {
        // Test that DownData is immutable
        ClearRow clearRow = new ClearRow(2, new int[10][20], 200);
        int[][] brickData = {{1}};
        int[][] nextBrickData = {{2}};
        ViewData viewData = new ViewData(brickData, 5, 10, nextBrickData);
        
        DownData downData = new DownData(clearRow, viewData);
        
        // Get values multiple times
        ClearRow cr1 = downData.getClearRow();
        ViewData vd1 = downData.getViewData();
        
        ClearRow cr2 = downData.getClearRow();
        ViewData vd2 = downData.getViewData();
        
        assertSame(cr1, cr2, "ClearRow should be consistent");
        assertSame(vd1, vd2, "ViewData should be consistent");
    }

    @Test
    void testConstructor() {
        // Test constructor with various combinations
        int[][] matrix = new int[10][20];
        ClearRow clearRow = new ClearRow(1, matrix, 50);
        int[][] brickData = {{1}};
        int[][] nextBrickData = {{2}};
        ViewData viewData = new ViewData(brickData, 0, 0, nextBrickData);
        
        // Valid constructions
        assertDoesNotThrow(() -> new DownData(null, null), 
            "Should construct with both null");
        assertDoesNotThrow(() -> new DownData(clearRow, null), 
            "Should construct with ClearRow and null ViewData");
        assertDoesNotThrow(() -> new DownData(null, viewData), 
            "Should construct with null ClearRow and ViewData");
        assertDoesNotThrow(() -> new DownData(clearRow, viewData), 
            "Should construct with both present");
    }
}