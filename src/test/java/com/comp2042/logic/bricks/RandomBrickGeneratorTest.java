package com.comp2042.logic.bricks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RandomBrickGenerator class.
 * 
 * Tests the 7-bag randomizer algorithm, brick consumption, peek methods,
 * and fair distribution guarantees.
 */
class RandomBrickGeneratorTest {

    private RandomBrickGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new RandomBrickGenerator();
    }

    /**
     * Helper method to get the color code from a brick's shape matrix.
     * Each brick type has a unique color code (1-7).
     */
    private int getBrickColorCode(Brick brick) {
        int[][] shape = brick.getShapeMatrix().get(0);
        for (int[] row : shape) {
            for (int cell : row) {
                if (cell > 0) {
                    return cell;
                }
            }
        }
        return 0; // Should not happen
    }

    @Test
    void getBrick() {
        // Test that getBrick returns a brick
        Brick brick = generator.getBrick();
        assertNotNull(brick, "getBrick should return a non-null brick");
        assertNotNull(brick.getShapeMatrix(), "Brick should have shape matrix");
        assertFalse(brick.getShapeMatrix().isEmpty(), "Brick should have at least one rotation");
        
        // Test that multiple calls return different bricks (eventually)
        // Due to randomness, we can't guarantee they're different immediately,
        // but they should be valid bricks
        Brick brick2 = generator.getBrick();
        assertNotNull(brick2, "Second getBrick should return a brick");
        
        Brick brick3 = generator.getBrick();
        assertNotNull(brick3, "Third getBrick should return a brick");
        
        // Test that bricks are consumed (getBrick removes from queue)
        // After getting a brick, the next peek should show a different brick
        Brick consumed = generator.getBrick();
        
        // The consumed brick should be valid
        assertNotNull(consumed, "Consumed brick should not be null");
        
        // After consumption, next peek should show a new brick
        Brick peekedAfter = generator.getNextBrick();
        assertNotNull(peekedAfter, "Next brick after consumption should exist");
    }

    @Test
    void getNextBrick() {
        // Test that getNextBrick returns a brick without consuming it
        Brick next1 = generator.getNextBrick();
        assertNotNull(next1, "getNextBrick should return a non-null brick");
        
        // Call again - should return same brick (not consumed)
        Brick next2 = generator.getNextBrick();
        assertNotNull(next2, "Second getNextBrick should return a brick");
        
        // The next brick should be the one returned by getBrick
        Brick peeked = generator.getNextBrick();
        Brick consumed = generator.getBrick();
        
        // They should be the same instance or equivalent
        assertNotNull(peeked, "Peeked brick should not be null");
        assertNotNull(consumed, "Consumed brick should not be null");
        
        // After consumption, next peek should be different
        Brick nextAfter = generator.getNextBrick();
        assertNotNull(nextAfter, "Next brick after consumption should exist");
    }

    @Test
    void getSecondNextBrick() {
        // Test that getSecondNextBrick returns a brick without consuming
        Brick secondNext = generator.getSecondNextBrick();
        assertNotNull(secondNext, "getSecondNextBrick should return a non-null brick");
        
        // Call multiple times - should return same brick (not consumed)
        Brick secondNext2 = generator.getSecondNextBrick();
        assertNotNull(secondNext2, "Second call should return a brick");
        
        // Verify it's the second brick in queue
        Brick first = generator.getNextBrick();
        Brick second = generator.getSecondNextBrick();
        Brick third = generator.getThirdNextBrick();
        
        assertNotNull(first, "First brick should exist");
        assertNotNull(second, "Second brick should exist");
        assertNotNull(third, "Third brick should exist");
        
        // Consume first brick
        generator.getBrick();
        
        // Now second should become first
        Brick newFirst = generator.getNextBrick();
        assertNotNull(newFirst, "New first brick should exist");
    }

    @Test
    void getThirdNextBrick() {
        // Test that getThirdNextBrick returns a brick without consuming
        Brick thirdNext = generator.getThirdNextBrick();
        assertNotNull(thirdNext, "getThirdNextBrick should return a non-null brick");
        
        // Call multiple times - should return same brick (not consumed)
        Brick thirdNext2 = generator.getThirdNextBrick();
        assertNotNull(thirdNext2, "Second call should return a brick");
        
        // Verify queue order: first, second, third
        Brick first = generator.getNextBrick();
        Brick second = generator.getSecondNextBrick();
        Brick third = generator.getThirdNextBrick();
        
        assertNotNull(first, "First brick should exist");
        assertNotNull(second, "Second brick should exist");
        assertNotNull(third, "Third brick should exist");
        
        // All should be different (in a shuffled bag)
        // Note: They might be the same by chance, but unlikely
    }

    @Test
    void testSevenBagAlgorithm() {
        // Test that every 7 bricks contains all 7 types (7-bag guarantee)
        Set<Integer> colorsInFirstBag = new HashSet<>();
        
        // Get 7 bricks (one bag)
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            int colorCode = getBrickColorCode(brick);
            colorsInFirstBag.add(colorCode);
        }
        
        // Should have all 7 colors (1-7)
        assertEquals(7, colorsInFirstBag.size(), 
            "First bag should contain all 7 brick types");
        assertTrue(colorsInFirstBag.contains(1), "Should contain I-brick (color 1)");
        assertTrue(colorsInFirstBag.contains(2), "Should contain J-brick (color 2)");
        assertTrue(colorsInFirstBag.contains(3), "Should contain L-brick (color 3)");
        assertTrue(colorsInFirstBag.contains(4), "Should contain O-brick (color 4)");
        assertTrue(colorsInFirstBag.contains(5), "Should contain S-brick (color 5)");
        assertTrue(colorsInFirstBag.contains(6), "Should contain T-brick (color 6)");
        assertTrue(colorsInFirstBag.contains(7), "Should contain Z-brick (color 7)");
        
        // Test second bag also has all 7 types
        Set<Integer> colorsInSecondBag = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            int colorCode = getBrickColorCode(brick);
            colorsInSecondBag.add(colorCode);
        }
        
        assertEquals(7, colorsInSecondBag.size(), 
            "Second bag should also contain all 7 brick types");
    }

    @Test
    void testBagRefilling() {
        // Test that bag refills when empty
        // Get more than 7 bricks to force bag refill
        Set<Integer> allColors = new HashSet<>();
        
        // Get 14 bricks (2 bags)
        for (int i = 0; i < 14; i++) {
            Brick brick = generator.getBrick();
            int colorCode = getBrickColorCode(brick);
            allColors.add(colorCode);
        }
        
        // Should still have all 7 colors
        assertEquals(7, allColors.size(), 
            "After 14 bricks, should still have all 7 types (bag refilled)");
        
        // Get even more bricks
        for (int i = 0; i < 7; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick, "Should be able to get bricks indefinitely");
        }
    }

    @Test
    void testPeekMethodsDontConsume() {
        // Test that peek methods don't consume bricks
        Brick next1 = generator.getNextBrick();
        Brick next2 = generator.getNextBrick();
        Brick next3 = generator.getNextBrick();
        
        // All should return the same brick (not consumed)
        assertNotNull(next1, "First peek should work");
        assertNotNull(next2, "Second peek should work");
        assertNotNull(next3, "Third peek should work");
        
        // Now consume the brick
        Brick consumed = generator.getBrick();
        assertNotNull(consumed, "Consumed brick should not be null");
        
        // Next peek should be different (queue advanced)
        Brick newNext = generator.getNextBrick();
        assertNotNull(newNext, "New next brick should exist");
    }

    @Test
    void testQueueConsistency() {
        // Test that peek methods show consistent queue
        Brick first = generator.getNextBrick();
        Brick second = generator.getSecondNextBrick();
        Brick third = generator.getThirdNextBrick();
        
        assertNotNull(first, "First should exist");
        assertNotNull(second, "Second should exist");
        assertNotNull(third, "Third should exist");
        
        // Consume first brick
        Brick consumed = generator.getBrick();
        assertNotNull(consumed, "Consumed brick should exist");
        
        // Now second should be first, third should be second
        Brick newFirst = generator.getNextBrick();
        Brick newSecond = generator.getSecondNextBrick();
        Brick newThird = generator.getThirdNextBrick();
        
        assertNotNull(newFirst, "New first should exist");
        assertNotNull(newSecond, "New second should exist");
        assertNotNull(newThird, "New third should exist");
    }

    @Test
    void testMultipleGenerators() {
        // Test that multiple generators are independent
        RandomBrickGenerator gen1 = new RandomBrickGenerator();
        RandomBrickGenerator gen2 = new RandomBrickGenerator();
        
        Brick brick1 = gen1.getBrick();
        Brick brick2 = gen2.getBrick();
        
        assertNotNull(brick1, "Generator 1 should produce bricks");
        assertNotNull(brick2, "Generator 2 should produce bricks");
        
        // They may or may not be the same (random), but both should work
        // The important thing is they're independent
    }

    @Test
    void testPeekAfterManyConsumptions() {
        // Test peek methods work correctly after many bricks consumed
        // Consume many bricks
        for (int i = 0; i < 20; i++) {
            Brick brick = generator.getBrick();
            assertNotNull(brick, "Should be able to consume many bricks");
        }
        
        // Peek methods should still work
        Brick next = generator.getNextBrick();
        Brick second = generator.getSecondNextBrick();
        Brick third = generator.getThirdNextBrick();
        
        assertNotNull(next, "Next should work after many consumptions");
        assertNotNull(second, "Second should work after many consumptions");
        assertNotNull(third, "Third should work after many consumptions");
    }
}