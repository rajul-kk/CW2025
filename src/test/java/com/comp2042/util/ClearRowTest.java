package com.comp2042.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ClearRow class.
 * 
 * Tests row clearing data encapsulation, defensive copying, and score bonus calculation.
 */
class ClearRowTest {

    @Test
    void getLinesRemoved() {
        // Test with 0 lines removed
        int[][] emptyMatrix = new int[10][20];
        ClearRow clearRow0 = new ClearRow(0, emptyMatrix, 0);
        assertEquals(0, clearRow0.getLinesRemoved(), 
            "Should return 0 for no lines removed");
        
        // Test with 1 line removed
        int[][] matrix1 = new int[10][20];
        ClearRow clearRow1 = new ClearRow(1, matrix1, 50);
        assertEquals(1, clearRow1.getLinesRemoved(), 
            "Should return 1 for one line removed");
        
        // Test with multiple lines removed
        ClearRow clearRow2 = new ClearRow(2, matrix1, 200);
        assertEquals(2, clearRow2.getLinesRemoved(), 
            "Should return 2 for two lines removed");
        
        // Test with 4 lines removed (Tetris!)
        ClearRow clearRow4 = new ClearRow(4, matrix1, 800);
        assertEquals(4, clearRow4.getLinesRemoved(), 
            "Should return 4 for four lines removed");
        
        // Test maximum valid value
        ClearRow clearRowMax = new ClearRow(4, matrix1, 800);
        assertEquals(4, clearRowMax.getLinesRemoved(), 
            "Should handle maximum lines (4)");
    }

    @Test
    void getNewMatrix() {
        // Test with empty matrix
        int[][] emptyMatrix = new int[10][20];
        ClearRow clearRow = new ClearRow(0, emptyMatrix, 0);
        int[][] retrievedMatrix = clearRow.getNewMatrix();
        
        assertNotNull(retrievedMatrix, "Retrieved matrix should not be null");
        assertEquals(10, retrievedMatrix.length, 
            "Matrix should have correct width");
        assertEquals(20, retrievedMatrix[0].length, 
            "Matrix should have correct height");
        
        // Test defensive copying - modify retrieved matrix
        retrievedMatrix[0][0] = 999;
        int[][] retrievedMatrix2 = clearRow.getNewMatrix();
        assertEquals(0, retrievedMatrix2[0][0], 
            "Original matrix should not be affected by modification");
        
        // Test with filled matrix
        int[][] filledMatrix = new int[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                filledMatrix[x][y] = (x + y) % 7 + 1;
            }
        }
        
        ClearRow clearRow2 = new ClearRow(1, filledMatrix, 50);
        int[][] retrievedFilled = clearRow2.getNewMatrix();
        
        assertNotNull(retrievedFilled, "Retrieved filled matrix should not be null");
        assertEquals(5, retrievedFilled.length, 
            "Filled matrix should have correct width");
        assertEquals(5, retrievedFilled[0].length, 
            "Filled matrix should have correct height");
        
        // Verify values are copied
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                assertEquals(filledMatrix[x][y], retrievedFilled[x][y], 
                    "Matrix values should match at (" + x + "," + y + ")");
            }
        }
        
        // Test multiple retrievals return independent copies
        int[][] copy1 = clearRow2.getNewMatrix();
        int[][] copy2 = clearRow2.getNewMatrix();
        copy1[0][0] = 999;
        assertNotEquals(copy1[0][0], copy2[0][0], 
            "Multiple retrievals should return independent copies");
    }

    @Test
    void getScoreBonus() {
        // Test score bonus calculation: 50 × lines²
        int[][] matrix = new int[10][20];
        
        // 0 lines = 50 × 0² = 0
        ClearRow clearRow0 = new ClearRow(0, matrix, 0);
        assertEquals(0, clearRow0.getScoreBonus(), 
            "0 lines should give 0 bonus");
        
        // 1 line = 50 × 1² = 50
        ClearRow clearRow1 = new ClearRow(1, matrix, 50);
        assertEquals(50, clearRow1.getScoreBonus(), 
            "1 line should give 50 bonus");
        
        // 2 lines = 50 × 2² = 200
        ClearRow clearRow2 = new ClearRow(2, matrix, 200);
        assertEquals(200, clearRow2.getScoreBonus(), 
            "2 lines should give 200 bonus");
        
        // 3 lines = 50 × 3² = 450
        ClearRow clearRow3 = new ClearRow(3, matrix, 450);
        assertEquals(450, clearRow3.getScoreBonus(), 
            "3 lines should give 450 bonus");
        
        // 4 lines (Tetris!) = 50 × 4² = 800
        ClearRow clearRow4 = new ClearRow(4, matrix, 800);
        assertEquals(800, clearRow4.getScoreBonus(), 
            "4 lines should give 800 bonus");
        
        // Test that score bonus is independent of matrix content
        int[][] differentMatrix = new int[5][5];
        ClearRow clearRowSame = new ClearRow(2, differentMatrix, 200);
        assertEquals(200, clearRowSame.getScoreBonus(), 
            "Score bonus should be same for same number of lines");
    }

    @Test
    void testImmutability() {
        // Test that ClearRow is immutable - values don't change
        int[][] matrix = new int[10][20];
        ClearRow clearRow = new ClearRow(2, matrix, 200);
        
        // Get values multiple times
        int lines1 = clearRow.getLinesRemoved();
        int bonus1 = clearRow.getScoreBonus();
        
        int lines2 = clearRow.getLinesRemoved();
        int bonus2 = clearRow.getScoreBonus();
        
        assertEquals(lines1, lines2, "Lines removed should be consistent");
        assertEquals(bonus1, bonus2, "Score bonus should be consistent");
    }

    @Test
    void testConstructor() {
        // Test constructor with various values
        int[][] matrix = new int[10][20];
        
        // Valid construction
        assertDoesNotThrow(() -> new ClearRow(0, matrix, 0), 
            "Should construct with 0 lines");
        assertDoesNotThrow(() -> new ClearRow(4, matrix, 800), 
            "Should construct with 4 lines");
        
        // Test with null matrix (should be allowed by constructor)
        // Note: This might throw NullPointerException when getNewMatrix() is called
        ClearRow clearRowNull = new ClearRow(1, null, 50);
        assertEquals(1, clearRowNull.getLinesRemoved(), 
            "Should store lines removed even with null matrix");
    }
}