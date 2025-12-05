package com.comp2042.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for MatrixOperations class.
 * 
 * Tests matrix operations including intersection detection, copying, merging,
 * row clearing, and deep copying.
 */
class MatrixOperationsTest {

    @Test
    void intersect() {
        // Create a matrix with some filled cells
        int[][] matrix = new int[5][5];
        matrix[3][2] = 1; // Fill one cell
        
        // Create a brick that doesn't intersect
        int[][] brick1 = {
            {1, 1},
            {1, 1}
        };
        assertFalse(MatrixOperations.intersect(matrix, brick1, 0, 0), 
            "Brick at (0,0) should not intersect");
        
        // Create a brick that intersects with filled cell
        int[][] brick2 = {
            {1, 1},
            {1, 1}
        };
        assertTrue(MatrixOperations.intersect(matrix, brick2, 1, 2), 
            "Brick at (1,2) should intersect with filled cell at (2,3)");
        
        // Test out of bounds - left side
        assertTrue(MatrixOperations.intersect(matrix, brick1, -1, 0), 
            "Brick going out of bounds left should intersect");
        
        // Test out of bounds - right side
        assertTrue(MatrixOperations.intersect(matrix, brick1, 4, 0), 
            "Brick going out of bounds right should intersect");
        
        // Test out of bounds - bottom
        assertTrue(MatrixOperations.intersect(matrix, brick1, 0, 4), 
            "Brick going out of bounds bottom should intersect");
        
        // Test empty brick (all zeros)
        int[][] emptyBrick = {
            {0, 0},
            {0, 0}
        };
        assertFalse(MatrixOperations.intersect(matrix, emptyBrick, 0, 0), 
            "Empty brick should not intersect");
    }

    @Test
    void copy() {
        // Create original matrix
        int[][] original = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        
        // Copy the matrix
        int[][] copied = MatrixOperations.copy(original);
        
        // Verify they have same values
        assertArrayEquals(original, copied, "Copied matrix should have same values");
        
        // Verify it's a deep copy - modifying copy shouldn't affect original
        copied[0][0] = 999;
        assertEquals(1, original[0][0], "Original should not be affected by copy modification");
        assertEquals(999, copied[0][0], "Copy should be modified");
        
        // Test with empty matrix
        int[][] empty = new int[0][0];
        int[][] emptyCopy = MatrixOperations.copy(empty);
        assertNotNull(emptyCopy, "Empty matrix copy should not be null");
        assertEquals(0, emptyCopy.length, "Empty matrix copy should have length 0");
        
        // Test with jagged array
        int[][] jagged = {
            {1, 2},
            {3, 4, 5},
            {6}
        };
        int[][] jaggedCopy = MatrixOperations.copy(jagged);
        assertArrayEquals(jagged[0], jaggedCopy[0], "Jagged array should copy correctly");
        assertArrayEquals(jagged[1], jaggedCopy[1], "Jagged array should copy correctly");
        assertArrayEquals(jagged[2], jaggedCopy[2], "Jagged array should copy correctly");
    }

    @Test
    void merge() {
        // Create base matrix
        int[][] filledFields = new int[5][5];
        filledFields[0][0] = 1;
        filledFields[1][1] = 2;
        
        // Create brick to merge
        int[][] brick = {
            {3, 3},
            {3, 3}
        };
        
        // Merge at position (1, 1)
        int[][] merged = MatrixOperations.merge(filledFields, brick, 1, 1);
        
        // Verify original is not modified
        assertEquals(1, filledFields[0][0], "Original should not be modified");
        assertEquals(2, filledFields[1][1], "Original should not be modified");
        
        // Verify merged result
        assertEquals(1, merged[0][0], "Original cell should remain");
        assertEquals(3, merged[1][1], "Brick should be merged at (1,1)");
        assertEquals(3, merged[1][2], "Brick should be merged at (1,2)");
        assertEquals(3, merged[2][1], "Brick should be merged at (2,1)");
        assertEquals(3, merged[2][2], "Brick should be merged at (2,2)");
        
        // Test merging at origin
        int[][] mergedAtOrigin = MatrixOperations.merge(filledFields, brick, 0, 0);
        assertEquals(3, mergedAtOrigin[0][0], "Brick should merge at origin");
        assertEquals(3, mergedAtOrigin[0][1], "Brick should merge at origin");
        
        // Test merging empty brick
        int[][] emptyBrick = {
            {0, 0},
            {0, 0}
        };
        int[][] mergedEmpty = MatrixOperations.merge(filledFields, emptyBrick, 0, 0);
        assertEquals(1, mergedEmpty[0][0], "Empty brick should not overwrite");
    }

    @Test
    void checkRemoving() {
        // Create matrix with one complete row
        int[][] matrix = new int[5][5];
        // Fill row 2 completely
        for (int j = 0; j < 5; j++) {
            matrix[2][j] = 1;
        }
        // Add some blocks in other rows
        matrix[0][0] = 1;
        matrix[1][1] = 2;
        matrix[3][2] = 3;
        matrix[4][4] = 4;
        
        ClearRow result = MatrixOperations.checkRemoving(matrix);
        
        assertEquals(1, result.getLinesRemoved(), "Should clear 1 row");
        assertEquals(50, result.getScoreBonus(), "1 line = 50 * 1² = 50 points");
        
        // Verify cleared row is removed and others shifted down
        // Row 2 was cleared, so rows 0, 1, 3, 4 should shift down
        // The algorithm fills from bottom, so:
        // - Row 4 goes to position 4
        // - Row 3 goes to position 3
        // - Row 1 goes to position 2
        // - Row 0 goes to position 1
        // - Position 0 stays empty (0)
        int[][] newMatrix = result.getNewMatrix();
        assertEquals(0, newMatrix[0][0], "Top row should be empty after shift");
        assertEquals(1, newMatrix[1][0], "Original row 0 should shift down");
        assertEquals(2, newMatrix[2][1], "Original row 1 should shift down");
        assertEquals(3, newMatrix[3][2], "Original row 3 should stay in place");
        assertEquals(4, newMatrix[4][4], "Original row 4 should stay in place");
        
        // Test with multiple complete rows
        int[][] matrix2 = new int[5][5];
        // Fill rows 1 and 3 completely
        for (int j = 0; j < 5; j++) {
            matrix2[1][j] = 1;
            matrix2[3][j] = 2;
        }
        matrix2[0][0] = 5;
        matrix2[2][2] = 6;
        matrix2[4][4] = 7;
        
        ClearRow result2 = MatrixOperations.checkRemoving(matrix2);
        assertEquals(2, result2.getLinesRemoved(), "Should clear 2 rows");
        assertEquals(200, result2.getScoreBonus(), "2 lines = 50 * 2² = 200 points");
        
        // Test with no complete rows
        int[][] matrix3 = new int[3][3];
        matrix3[0][0] = 1;
        matrix3[1][1] = 2;
        matrix3[2][2] = 3;
        
        ClearRow result3 = MatrixOperations.checkRemoving(matrix3);
        assertEquals(0, result3.getLinesRemoved(), "Should clear 0 rows");
        assertEquals(0, result3.getScoreBonus(), "0 lines = 0 points");
        
        // Verify matrix is unchanged when no rows cleared
        int[][] newMatrix3 = result3.getNewMatrix();
        assertEquals(1, newMatrix3[0][0], "Matrix should be unchanged");
        assertEquals(2, newMatrix3[1][1], "Matrix should be unchanged");
        assertEquals(3, newMatrix3[2][2], "Matrix should be unchanged");
        
        // Test with all rows complete (Tetris!)
        int[][] matrix4 = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix4[i][j] = 1;
            }
        }
        
        ClearRow result4 = MatrixOperations.checkRemoving(matrix4);
        assertEquals(4, result4.getLinesRemoved(), "Should clear 4 rows");
        assertEquals(800, result4.getScoreBonus(), "4 lines = 50 * 4² = 800 points");
        
        // Verify all rows cleared
        int[][] newMatrix4 = result4.getNewMatrix();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                assertEquals(0, newMatrix4[i][j], "All rows should be cleared");
            }
        }
    }

    @Test
    void deepCopyList() {
        // Create list of matrices
        List<int[][]> original = new ArrayList<>();
        original.add(new int[][]{{1, 2}, {3, 4}});
        original.add(new int[][]{{5, 6}, {7, 8}});
        original.add(new int[][]{{9, 10}});
        
        // Deep copy the list
        List<int[][]> copied = MatrixOperations.deepCopyList(original);
        
        // Verify same size
        assertEquals(original.size(), copied.size(), "Copied list should have same size");
        
        // Verify same values
        assertArrayEquals(original.get(0), copied.get(0), "First matrix should match");
        assertArrayEquals(original.get(1), copied.get(1), "Second matrix should match");
        assertArrayEquals(original.get(2), copied.get(2), "Third matrix should match");
        
        // Verify it's a deep copy - modifying copy shouldn't affect original
        copied.get(0)[0][0] = 999;
        assertEquals(1, original.get(0)[0][0], "Original should not be affected");
        assertEquals(999, copied.get(0)[0][0], "Copy should be modified");
        
        // Test with empty list
        List<int[][]> empty = new ArrayList<>();
        List<int[][]> emptyCopy = MatrixOperations.deepCopyList(empty);
        assertNotNull(emptyCopy, "Empty list copy should not be null");
        assertEquals(0, emptyCopy.size(), "Empty list copy should have size 0");
        
        // Verify list structure is independent
        copied.add(new int[][]{{11, 12}});
        assertEquals(3, original.size(), "Original list size should not change");
        assertEquals(4, copied.size(), "Copied list should have new element");
    }

    @Test
    void testIntersectEdgeCases() {
        // Test with single cell brick
        int[][] matrix = new int[3][3];
        int[][] singleCell = {{1}};
        
        assertFalse(MatrixOperations.intersect(matrix, singleCell, 1, 1), 
            "Single cell brick in middle should not intersect");
        assertTrue(MatrixOperations.intersect(matrix, singleCell, -1, 0), 
            "Single cell brick out of bounds should intersect");
        assertTrue(MatrixOperations.intersect(matrix, singleCell, 0, 3), 
            "Single cell brick out of bounds should intersect");
        
        // Test with large brick
        int[][] largeBrick = new int[10][10];
        largeBrick[5][5] = 1;
        assertTrue(MatrixOperations.intersect(matrix, largeBrick, 0, 0), 
            "Large brick should intersect with small matrix");
    }

    @Test
    void testMergeEdgeCases() {
        // Test merging at different positions
        int[][] matrix = new int[5][5];
        int[][] brick = {{1, 2}, {3, 4}};
        
        // Merge at top-left
        int[][] merged1 = MatrixOperations.merge(matrix, brick, 0, 0);
        assertEquals(1, merged1[0][0], "Should merge at (0,0)");
        assertEquals(2, merged1[0][1], "Should merge at (0,1)");
        
        // Merge at bottom-right
        int[][] merged2 = MatrixOperations.merge(matrix, brick, 3, 3);
        assertEquals(1, merged2[3][3], "Should merge at (3,3)");
        assertEquals(4, merged2[4][4], "Should merge at (4,4)");
        
        // Test merging overlapping bricks
        int[][] matrix3 = new int[3][3];
        matrix3[0][0] = 5;
        int[][] brick3 = {{1}};
        int[][] merged3 = MatrixOperations.merge(matrix3, brick3, 0, 0);
        assertEquals(1, merged3[0][0], "Brick should overwrite existing cell");
    }

    @Test
    void testCheckRemovingEdgeCases() {
        // Test with empty matrix
        int[][] empty = new int[3][3];
        ClearRow result = MatrixOperations.checkRemoving(empty);
        assertEquals(0, result.getLinesRemoved(), "Empty matrix should clear 0 rows");
        assertEquals(0, result.getScoreBonus(), "Empty matrix should give 0 points");
        
        // Test with single row matrix
        int[][] singleRow = {{1, 1, 1}};
        ClearRow result2 = MatrixOperations.checkRemoving(singleRow);
        assertEquals(1, result2.getLinesRemoved(), "Single complete row should be cleared");
        assertEquals(50, result2.getScoreBonus(), "Should give 50 points");
        
        // Test with partial row (should not clear)
        int[][] partial = new int[3][3];
        partial[1][0] = 1;
        partial[1][1] = 1;
        // Row 1 is not complete
        ClearRow result3 = MatrixOperations.checkRemoving(partial);
        assertEquals(0, result3.getLinesRemoved(), "Partial row should not be cleared");
    }

    @Test
    void testCopyImmutability() {
        // Test that copy creates independent arrays
        int[][] original = {{1, 2}, {3, 4}};
        int[][] copy = MatrixOperations.copy(original);
        
        // Modify nested array
        copy[0] = new int[]{9, 9};
        assertEquals(1, original[0][0], "Original nested array should not change");
        assertEquals(2, original[0][1], "Original nested array should not change");
        assertEquals(9, copy[0][0], "Copy nested array should change");
    }
}
