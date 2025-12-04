package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LevelManager class.
 * 
 * Tests level progression, drop interval calculations, and lines cleared tracking.
 */
class LevelManagerTest {

    private LevelManager levelManager;

    @BeforeEach
    void setUp() {
        levelManager = new LevelManager();
    }

    @Test
    void testInitialState() {
        assertEquals(1, levelManager.getLevel(), "Initial level should be 1");
        assertEquals(0, levelManager.getTotalLinesCleared(), "Initial lines cleared should be 0");
        assertEquals(1000, levelManager.getDropInterval(), "Initial drop interval should be 1000ms");
    }

    @Test
    void reset() {
        // Add some lines to change state
        levelManager.addLinesCleared(15);
        assertEquals(2, levelManager.getLevel(), "Level should be 2 after 15 lines");
        assertTrue(levelManager.getTotalLinesCleared() > 0, "Lines cleared should be greater than 0");
        
        // Reset and verify initial state
        levelManager.reset();
        assertEquals(1, levelManager.getLevel(), "Level should reset to 1");
        assertEquals(0, levelManager.getTotalLinesCleared(), "Lines cleared should reset to 0");
        assertEquals(1000, levelManager.getDropInterval(), "Drop interval should reset to 1000ms");
    }

    @Test
    void addLinesCleared() {
        // Test no level increase
        LevelManager.LevelUpdateResult result1 = levelManager.addLinesCleared(5);
        assertEquals(1, result1.level(), "Level should remain 1");
        assertEquals(5, result1.totalLinesCleared(), "Total lines should be 5");
        assertEquals(1000, result1.dropInterval(), "Drop interval should remain 1000ms");
        assertFalse(result1.levelIncreased(), "Level should not have increased");
        
        // Test level increase
        LevelManager.LevelUpdateResult result2 = levelManager.addLinesCleared(10);
        assertEquals(2, result2.level(), "Level should increase to 2");
        assertEquals(15, result2.totalLinesCleared(), "Total lines should be 15");
        assertTrue(result2.levelIncreased(), "Level should have increased");
        assertTrue(result2.dropInterval() < 1000, "Drop interval should decrease for level 2");
        
        // Test zero/negative lines
        LevelManager.LevelUpdateResult result3 = levelManager.addLinesCleared(0);
        assertEquals(2, result3.level(), "Level should remain 2");
        assertEquals(15, result3.totalLinesCleared(), "Lines should remain 15");
        assertFalse(result3.levelIncreased(), "Level should not increase");
        
        LevelManager.LevelUpdateResult result4 = levelManager.addLinesCleared(-5);
        assertEquals(2, result4.level(), "Level should remain 2");
        assertEquals(15, result4.totalLinesCleared(), "Lines should remain 15");
        assertFalse(result4.levelIncreased(), "Level should not increase");
    }

    @Test
    void getLevel() {
        assertEquals(1, levelManager.getLevel(), "Initial level should be 1");
        
        levelManager.addLinesCleared(10);
        assertEquals(2, levelManager.getLevel(), "Level should be 2 after 10 lines");
        
        levelManager.addLinesCleared(20);
        assertEquals(4, levelManager.getLevel(), "Level should be 4 after 30 total lines");
        
        levelManager.reset();
        assertEquals(1, levelManager.getLevel(), "Level should reset to 1");
    }

    @Test
    void getTotalLinesCleared() {
        assertEquals(0, levelManager.getTotalLinesCleared(), "Initial lines should be 0");
        
        levelManager.addLinesCleared(5);
        assertEquals(5, levelManager.getTotalLinesCleared(), "Should be 5 after adding 5");
        
        levelManager.addLinesCleared(10);
        assertEquals(15, levelManager.getTotalLinesCleared(), "Should be 15 after adding 10 more");
        
        levelManager.reset();
        assertEquals(0, levelManager.getTotalLinesCleared(), "Should reset to 0");
    }

    @Test
    void getDropInterval() {
        // Level 1
        assertEquals(1000, levelManager.getDropInterval(), "Level 1 should have 1000ms");
        
        // Level 2: (0.8 - ((2-1) * 0.01))^(2-1) = 0.79^1 = 0.79 seconds = 790ms
        levelManager.addLinesCleared(10);
        int level2Interval = levelManager.getDropInterval();
        int expectedLevel2 = (int) (0.79 * 1000);
        assertEquals(expectedLevel2, level2Interval, "Level 2 drop interval should be ~790ms");
        assertTrue(level2Interval < 1000, "Level 2 should be faster than level 1");
        
        // Level 3: (0.8 - ((3-1) * 0.01))^(3-1) = 0.78^2 = 0.6084 seconds = 608ms
        levelManager.addLinesCleared(10);
        int level3Interval = levelManager.getDropInterval();
        int expectedLevel3 = (int) (0.6084 * 1000);
        assertEquals(expectedLevel3, level3Interval, "Level 3 drop interval should be ~608ms");
        assertTrue(level3Interval < level2Interval, "Level 3 should be faster than level 2");
        
        // Test minimum clamp at high levels
        levelManager.addLinesCleared(1000); // Level 101
        int highLevelInterval = levelManager.getDropInterval();
        assertTrue(highLevelInterval >= 10, "Should be clamped to minimum of 10ms");
    }

    @Test
    void testLevelCalculationBoundaries() {
        // Test exact boundaries
        levelManager.addLinesCleared(0);
        assertEquals(1, levelManager.getLevel(), "0 lines = level 1");
        
        levelManager.reset();
        levelManager.addLinesCleared(9);
        assertEquals(1, levelManager.getLevel(), "9 lines = level 1");
        
        levelManager.reset();
        levelManager.addLinesCleared(10);
        assertEquals(2, levelManager.getLevel(), "10 lines = level 2");
        
        levelManager.reset();
        levelManager.addLinesCleared(19);
        assertEquals(2, levelManager.getLevel(), "19 lines = level 2");
        
        levelManager.reset();
        levelManager.addLinesCleared(20);
        assertEquals(3, levelManager.getLevel(), "20 lines = level 3");
    }

    @Test
    void testLevelUpdateResult() {
        LevelManager.LevelUpdateResult result = levelManager.addLinesCleared(10);
        
        // Test record accessors
        assertEquals(2, result.level(), "Level should be accessible");
        assertEquals(10, result.totalLinesCleared(), "Total lines should be accessible");
        assertTrue(result.levelIncreased(), "Level increased flag should be accessible");
        assertTrue(result.dropInterval() > 0, "Drop interval should be accessible");
        
        // Test equality
        LevelManager.LevelUpdateResult result2 = new LevelManager.LevelUpdateResult(
            result.level(),
            result.totalLinesCleared(),
            result.dropInterval(),
            result.levelIncreased()
        );
        assertEquals(result, result2, "Records with same values should be equal");
        assertEquals(result.hashCode(), result2.hashCode(), "Equal records should have same hash code");
    }

    @Test
    void testMultipleLevelIncreases() {
        // Jump multiple levels at once
        LevelManager.LevelUpdateResult result = levelManager.addLinesCleared(30);
        assertEquals(4, result.level(), "Should jump to level 4");
        assertEquals(30, result.totalLinesCleared(), "Total should be 30");
        assertTrue(result.levelIncreased(), "Level should have increased");
    }

    @Test
    void testDropIntervalProgression() {
        // Verify that drop interval decreases as level increases
        int level1Interval = levelManager.getDropInterval();
        
        levelManager.addLinesCleared(10); // Level 2
        int level2Interval = levelManager.getDropInterval();
        assertTrue(level2Interval < level1Interval, "Level 2 should be faster than level 1");
        
        levelManager.addLinesCleared(10); // Level 3
        int level3Interval = levelManager.getDropInterval();
        assertTrue(level3Interval < level2Interval, "Level 3 should be faster than level 2");
    }
}
