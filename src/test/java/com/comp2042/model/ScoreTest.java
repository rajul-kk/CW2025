package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Score class.
 * 
 * Tests score management, addition, reset, and JavaFX property binding.
 */
class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    @AfterEach
    void tearDown() {
        // Cleanup if needed - Score doesn't require cleanup
    }

    @Test
    void scoreProperty() {
        // Test property is not null
        IntegerProperty property = score.scoreProperty();
        assertNotNull(property, "Score property should not be null");
        
        // Test property returns same instance
        IntegerProperty property2 = score.scoreProperty();
        assertSame(property, property2, "scoreProperty() should return same instance");
        
        // Test initial value
        assertEquals(0, property.getValue(), "Initial score should be 0");
        
        // Test property reflects changes
        score.add(100);
        assertEquals(100, property.getValue(), "Property should reflect score changes");
        
        score.add(50);
        assertEquals(150, property.getValue(), "Property should reflect additional changes");
        
        score.reset();
        assertEquals(0, property.getValue(), "Property should reflect reset");
        
        // Test property is observable (JavaFX property behavior)
        assertTrue(property instanceof IntegerProperty, "Property should be an IntegerProperty");
    }

    @Test
    void add() {
        // Test adding positive values
        score.add(100);
        assertEquals(100, score.scoreProperty().getValue(), "Score should be 100 after adding 100");
        
        score.add(50);
        assertEquals(150, score.scoreProperty().getValue(), "Score should be 150 after adding 50 more");
        
        // Test adding zero
        score.add(0);
        assertEquals(150, score.scoreProperty().getValue(), "Adding zero should not change score");
        
        // Test adding negative values
        score.add(-50);
        assertEquals(100, score.scoreProperty().getValue(), "Score should be 100 after adding -50");
        
        // Test adding large values
        score.reset();
        score.add(1000000);
        assertEquals(1000000, score.scoreProperty().getValue(), "Should handle large values");
        
        // Test multiple additions
        score.reset();
        score.add(10);
        score.add(20);
        score.add(30);
        score.add(40);
        assertEquals(100, score.scoreProperty().getValue(), "Should accumulate multiple additions");
        
        // Test adding negative to zero
        score.reset();
        score.add(-100);
        assertEquals(-100, score.scoreProperty().getValue(), "Score can go negative");
        
        // Test adding after reset
        score.reset();
        score.add(200);
        assertEquals(200, score.scoreProperty().getValue(), "Should be able to add after reset");
    }

    @Test
    void reset() {
        // Test reset from positive score
        score.add(500);
        assertEquals(500, score.scoreProperty().getValue(), "Score should be 500");
        score.reset();
        assertEquals(0, score.scoreProperty().getValue(), "Score should reset to 0");
        
        // Test reset from negative score
        score.add(-50);
        assertEquals(-50, score.scoreProperty().getValue(), "Score should be -50");
        score.reset();
        assertEquals(0, score.scoreProperty().getValue(), "Reset should set score to 0 even from negative");
        
        // Test reset when already at zero
        score.reset();
        assertEquals(0, score.scoreProperty().getValue(), "Resetting at 0 should keep score at 0");
        
        // Test multiple resets
        score.add(100);
        score.reset();
        score.add(200);
        score.reset();
        score.add(300);
        score.reset();
        assertEquals(0, score.scoreProperty().getValue(), "Multiple resets should work correctly");
        
        // Test reset then add
        score.add(100);
        score.reset();
        score.add(50);
        assertEquals(50, score.scoreProperty().getValue(), "Should be able to add after reset");
    }
}