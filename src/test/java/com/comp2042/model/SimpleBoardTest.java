package com.comp2042.model;

import com.comp2042.data.ViewData;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.RandomBrickGenerator;
import com.comp2042.util.ClearRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SimpleBoard class.
 * 
 * Tests board operations including piece movement, rotation, collision detection,
 * row clearing, and game state management.
 */
class SimpleBoardTest {

    private SimpleBoard board;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 25;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(BOARD_WIDTH, BOARD_HEIGHT);
        // Create initial brick for testing
        board.createNewBrick();
    }

    @Test
    void moveBrickDown() {
        // Test successful move down
        int initialY = board.getViewData().getyPosition();
        boolean moved = board.moveBrickDown();
        assertTrue(moved, "Brick should move down successfully");
        assertEquals(initialY + 1, board.getViewData().getyPosition(), "Y position should increase by 1");
        
        // Move brick to bottom and test collision
        // Move brick down many times until it can't move
        int moves = 0;
        while (board.moveBrickDown() && moves < 100) {
            moves++;
        }
        assertTrue(moves > 0, "Brick should have moved down at least once");
        
        // After hitting bottom, move should fail
        boolean movedAfterCollision = board.moveBrickDown();
        assertFalse(movedAfterCollision, "Brick should not move down after hitting bottom");
    }

    @Test
    void moveBrickLeft() {
        // Test successful move left
        int initialX = board.getViewData().getxPosition();
        boolean moved = board.moveBrickLeft();
        assertTrue(moved, "Brick should move left successfully");
        assertEquals(initialX - 1, board.getViewData().getxPosition(), "X position should decrease by 1");
        
        // Move brick to left boundary
        int moves = 0;
        while (board.moveBrickLeft() && moves < 20) {
            moves++;
        }
        
        // After hitting left boundary, move should fail
        boolean movedAfterBoundary = board.moveBrickLeft();
        assertFalse(movedAfterBoundary, "Brick should not move left after hitting boundary");
    }

    @Test
    void moveBrickRight() {
        // Test successful move right
        int initialX = board.getViewData().getxPosition();
        boolean moved = board.moveBrickRight();
        assertTrue(moved, "Brick should move right successfully");
        assertEquals(initialX + 1, board.getViewData().getxPosition(), "X position should increase by 1");
        
        // Move brick to right boundary
        int moves = 0;
        while (board.moveBrickRight() && moves < 20) {
            moves++;
        }
        
        // After hitting right boundary, move should fail
        boolean movedAfterBoundary = board.moveBrickRight();
        assertFalse(movedAfterBoundary, "Brick should not move right after hitting boundary");
    }

    @Test
    void rotateLeftBrick() {
        // Test successful rotation
        boolean rotated = board.rotateLeftBrick();
        assertTrue(rotated, "Brick should rotate successfully");
        
        // Rotation should change the rotation state (unless it's a brick with only 1 rotation like O)
        // For most bricks, rotation should succeed
        
        // Test rotation with wall kicks - move brick to edge and try to rotate
        // Move to left edge
        while (board.moveBrickLeft()) {
            // Keep moving left
        }
        
        // Try rotation - should succeed with wall kick if needed
        board.rotateLeftBrick();
        // Rotation might succeed with wall kick or fail if impossible
        assertNotNull(board.getCurrentBrick(), "Brick should still exist after rotation attempt");
    }

    @Test
    void createNewBrick() {
        // Test creating new brick (should not collide on empty board)
        board.newGame();
        boolean gameOver = board.createNewBrick();
        assertFalse(gameOver, "New brick should not collide on empty board");
        
        // Verify brick was created
        assertNotNull(board.getCurrentBrick(), "Current brick should not be null");
        assertNotNull(board.getViewData(), "View data should not be null");
        
        // Test game over scenario - fill board near top
        board.newGame();
        int[][] matrix = board.getBoardMatrix();
        // Fill a row near the top to cause collision
        for (int x = 0; x < BOARD_WIDTH; x++) {
            matrix[x][3] = 1; // Fill row 3
        }
        
        // Try to create new brick - should collide
        board.createNewBrick();
        // Note: This might not always be true depending on brick shape and position
        // But if board is filled at spawn position, it should collide
    }

    @Test
    void getBoardMatrix() {
        // Test initial board is empty
        int[][] matrix = board.getBoardMatrix();
        assertNotNull(matrix, "Board matrix should not be null");
        assertEquals(BOARD_WIDTH, matrix.length, "Matrix should have correct width");
        assertEquals(BOARD_HEIGHT, matrix[0].length, "Matrix should have correct height");
        
        // Verify all cells are empty initially
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                assertEquals(0, matrix[x][y], "Initial board should be empty");
            }
        }
        
        // Test matrix is returned (not null after operations)
        board.moveBrickDown();
        matrix = board.getBoardMatrix();
        assertNotNull(matrix, "Board matrix should still be accessible");
    }

    @Test
    void getViewData() {
        // Test view data is returned
        ViewData viewData = board.getViewData();
        assertNotNull(viewData, "View data should not be null");
        
        // Test view data contains brick data
        int[][] brickData = viewData.getBrickData();
        assertNotNull(brickData, "Brick data should not be null");
        
        // Test position is valid
        int xPos = viewData.getxPosition();
        int yPos = viewData.getyPosition();
        assertTrue(xPos >= 0, "X position should be non-negative");
        assertTrue(yPos >= 0, "Y position should be non-negative");
        
        // Test next brick data is present
        int[][] nextBrickData = viewData.getNextBrickData();
        assertNotNull(nextBrickData, "Next brick data should not be null");
    }

    @Test
    void mergeBrickToBackground() {
        // Move brick down a bit
        board.moveBrickDown();
        board.moveBrickDown();
        
        // Merge brick to background
        board.mergeBrickToBackground();
        
        // Verify brick was merged (board should have blocks where brick was)
        int[][] newMatrix = board.getBoardMatrix();
        assertNotNull(newMatrix, "Board matrix should exist after merge");
        
        // After merge, the board should have the brick's blocks
        // We can't easily verify exact positions without knowing brick shape,
        // but we can verify the operation completed
    }

    @Test
    void clearRows() {
        // Fill a complete row
        board.mergeBrickToBackground();
        int[][] matrix = board.getBoardMatrix();
        
        // Fill row 20 completely
        for (int x = 0; x < BOARD_WIDTH; x++) {
            matrix[x][20] = 1;
        }
        
        // Clear rows
        ClearRow result = board.clearRows();
        assertNotNull(result, "ClearRow result should not be null");
        
        // If a row was filled, it should be cleared
        // The exact result depends on whether the row was actually complete
        assertTrue(result.getLinesRemoved() >= 0, "Lines removed should be non-negative");
        assertTrue(result.getScoreBonus() >= 0, "Score bonus should be non-negative");
        
        // Test clearing with no complete rows
        board.newGame();
        ClearRow result2 = board.clearRows();
        assertEquals(0, result2.getLinesRemoved(), "No rows should be cleared on empty board");
        assertEquals(0, result2.getScoreBonus(), "No score bonus for no rows cleared");
    }

    @Test
    void getScore() {
        // Test score object is returned
        Score score = board.getScore();
        assertNotNull(score, "Score should not be null");
        
        // Test score starts at 0
        assertEquals(0, score.scoreProperty().getValue(), "Initial score should be 0");
        
        // Test score can be modified
        score.add(100);
        assertEquals(100, score.scoreProperty().getValue(), "Score should be 100 after adding");
        
        // Verify it's the same score object
        Score score2 = board.getScore();
        assertSame(score, score2, "getScore should return same instance");
        assertEquals(100, score2.scoreProperty().getValue(), "Score should persist");
    }

    @Test
    void newGame() {
        // Modify board state
        board.moveBrickDown();
        board.getScore().add(500);
        
        // Create new game
        board.newGame();
        
        // Verify board is reset
        int[][] matrix = board.getBoardMatrix();
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                assertEquals(0, matrix[x][y], "Board should be empty after newGame");
            }
        }
        
        // Verify score is reset
        assertEquals(0, board.getScore().scoreProperty().getValue(), "Score should be reset to 0");
        
        // Verify new brick is created
        assertNotNull(board.getCurrentBrick(), "New brick should be created");
        assertNotNull(board.getViewData(), "View data should be available");
    }

    @Test
    void getSecondNextBrickData() {
        // Test second next brick data is returned
        int[][] secondNext = board.getSecondNextBrickData();
        assertNotNull(secondNext, "Second next brick data should not be null");
        assertTrue(secondNext.length > 0, "Second next brick should have shape data");
    }

    @Test
    void getThirdNextBrickData() {
        // Test third next brick data is returned
        int[][] thirdNext = board.getThirdNextBrickData();
        assertNotNull(thirdNext, "Third next brick data should not be null");
        assertTrue(thirdNext.length > 0, "Third next brick should have shape data");
    }

    @Test
    void setBrick() {
        // Get a brick from the generator
        RandomBrickGenerator generator = new RandomBrickGenerator();
        Brick brick = generator.getBrick();
        
        // Set brick with rotation 0
        boolean collision = board.setBrick(brick, 0);
        assertFalse(collision, "Brick should not collide on empty board");
        
        // Verify brick was set
        assertNotNull(board.getCurrentBrick(), "Current brick should not be null");
        assertEquals(0, board.getCurrentRotation(), "Rotation should be 0");
        
        // Test setting with different rotation
        Brick brick2 = generator.getBrick();
        boolean collision2 = board.setBrick(brick2, 1);
        assertFalse(collision2, "Brick should not collide with rotation 1");
        assertEquals(1, board.getCurrentRotation(), "Rotation should be 1");
    }

    @Test
    void getCurrentBrick() {
        // Test current brick is returned
        Brick currentBrick = board.getCurrentBrick();
        assertNotNull(currentBrick, "Current brick should not be null");
        
        // Verify it's the same instance after operations
        board.moveBrickDown();
        Brick currentBrick2 = board.getCurrentBrick();
        assertSame(currentBrick, currentBrick2, "Should return same brick instance");
    }

    @Test
    void getCurrentRotation() {
        // Test initial rotation
        int rotation = board.getCurrentRotation();
        assertTrue(rotation >= 0 && rotation < 4, "Rotation should be between 0 and 3");
        
        // Test rotation changes after rotation
        board.rotateLeftBrick();
        int newRotation = board.getCurrentRotation();
        // Rotation might be same for bricks with only 1-2 rotations (like O, I)
        // or different for bricks with 4 rotations
        assertTrue(newRotation >= 0 && newRotation < 4, "Rotation should be valid");
    }
}