package com.comp2042.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Block class.
 * 
 * Tests immutability, equality, hashing, and defensive copying of block shapes.
 */
class BlockTest {

    @Test
    void shape() {
        // Test basic shape retrieval
        int[][] originalShape = {
            {1, 1},
            {1, 1}
        };
        
        Block block = new Block(originalShape);
        int[][] retrievedShape = block.shape();
        
        assertNotNull(retrievedShape, "Shape should not be null");
        assertEquals(2, retrievedShape.length, "Shape should have correct height");
        assertEquals(2, retrievedShape[0].length, "Shape should have correct width");
        assertEquals(1, retrievedShape[0][0], "Shape should preserve values");
        
        // Test immutability - modifying retrieved shape shouldn't affect block
        retrievedShape[0][0] = 999;
        int[][] retrievedShape2 = block.shape();
        assertEquals(1, retrievedShape2[0][0], "Shape should return defensive copy");
        
        // Test immutability - modifying original shouldn't affect block
        originalShape[0][0] = 888;
        int[][] retrievedShape3 = block.shape();
        assertEquals(1, retrievedShape3[0][0], "Block should not be affected by original modification");
    }

    @Test
    void testEquals() {
        // Test same reference
        int[][] shape = {{1, 1}, {1, 1}};
        Block block1 = new Block(shape);
        assertEquals(block1, block1, "Block should equal itself");
        
        // Test same content
        int[][] shape1 = {
            {1, 2},
            {3, 4}
        };
        int[][] shape2 = {
            {1, 2},
            {3, 4}
        };
        Block block2 = new Block(shape1);
        Block block3 = new Block(shape2);
        assertEquals(block2, block3, "Blocks with same content should be equal");
        
        // Test different content
        int[][] shape3 = {
            {5, 6},
            {7, 8}
        };
        Block block4 = new Block(shape3);
        assertNotEquals(block2, block4, "Blocks with different content should not be equal");
        
        // Test null
        assertNotEquals(block1, null, "Block should not equal null");
        
        // Test different class
        assertNotEquals(block1, "not a block", "Block should not equal different class");
        
        // Test with different array references but same content
        int[][] shape4 = new int[2][2];
        shape4[0][0] = 1;
        shape4[0][1] = 2;
        shape4[1][0] = 3;
        shape4[1][1] = 4;
        Block block5 = new Block(shape4);
        assertEquals(block2, block5, "Blocks with same content but different references should be equal");
    }

    @Test
    void testHashCode() {
        // Test same content produces same hash code
        int[][] shape1 = {
            {1, 2, 3},
            {4, 5, 6}
        };
        int[][] shape2 = {
            {1, 2, 3},
            {4, 5, 6}
        };
        
        Block block1 = new Block(shape1);
        Block block2 = new Block(shape2);
        
        assertEquals(block1.hashCode(), block2.hashCode(), 
            "Blocks with same content should have same hash code");
        
        // Test hash code consistency
        int hashCode1 = block1.hashCode();
        int hashCode2 = block1.hashCode();
        assertEquals(hashCode1, hashCode2, "Hash code should be consistent");
        
        // Test hash code is not null (should be an integer)
        assertNotNull(block1.hashCode(), "Hash code should not be null");
        
        // Test equals and hashCode contract
        Block block3 = new Block(shape1);
        if (block1.equals(block3)) {
            assertEquals(block1.hashCode(), block3.hashCode(), 
                "Equal blocks must have equal hash codes");
        }
    }

    @Test
    void testToString() {
        // Test basic toString
        int[][] shape = {
            {1, 2},
            {3, 4}
        };
        
        Block block = new Block(shape);
        String toString = block.toString();
        
        assertNotNull(toString, "toString should not return null");
        assertTrue(toString.contains("Block"), "toString should contain 'Block'");
        
        // Test with different shapes
        int[][] shape2 = {
            {0, 0, 0},
            {1, 1, 1}
        };
        Block block2 = new Block(shape2);
        String toString2 = block2.toString();
        assertNotNull(toString2, "toString should work for different shapes");
        assertTrue(toString2.contains("Block"), "toString should contain 'Block'");
        
        // Test with single cell
        int[][] singleCell = {{7}};
        Block block3 = new Block(singleCell);
        String toString3 = block3.toString();
        assertNotNull(toString3, "toString should work for single cell");
    }
}