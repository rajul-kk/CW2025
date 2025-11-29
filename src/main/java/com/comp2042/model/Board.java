package com.comp2042.model;

import com.comp2042.data.ViewData;
import com.comp2042.util.ClearRow;

/**
 * Interface representing the game board for a Tetris game.
 * 
 * <p>This interface defines the core operations for managing the game board,
 * including piece movement, rotation, locking, and row clearing. It provides
 * methods to interact with the game state and retrieve information about
 * the current board and pieces.
 * 
 * <p>Implementations of this interface should manage:
 * <ul>
 *   <li>The game board matrix (2D array representing occupied cells)</li>
 *   <li>The current falling piece and its position</li>
 *   <li>Piece generation and rotation</li>
 *   <li>Collision detection</li>
 *   <li>Row clearing and scoring</li>
 * </ul>
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see SimpleBoard
 */
public interface Board {

    /**
     * Attempts to move the current piece down by one cell.
     * 
     * @return true if the move was successful, false if the piece cannot move down (collision)
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the current piece left by one cell.
     * 
     * @return true if the move was successful, false if the piece cannot move left (collision or boundary)
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the current piece right by one cell.
     * 
     * @return true if the move was successful, false if the piece cannot move right (collision or boundary)
     */
    boolean moveBrickRight();

    /**
     * Attempts to rotate the current piece counter-clockwise.
     * 
     * @return true if the rotation was successful, false if the piece cannot rotate (collision)
     */
    boolean rotateLeftBrick();

    /**
     * Creates a new piece at the top of the board.
     * 
     * <p>This method generates a new piece from the brick generator and places it
     * at the default starting position. If the piece immediately collides with
     * existing blocks, the game is over.
     * 
     * @return true if the new piece collides immediately (game over), false otherwise
     */
    boolean createNewBrick();

    /**
     * Gets the current game board matrix.
     * 
     * <p>The matrix represents the state of all locked blocks on the board.
     * Each cell contains either 0 (empty) or a color code (1-7) representing
     * a locked block.
     * 
     * @return A 2D array representing the game board state
     */
    int[][] getBoardMatrix();

    /**
     * Gets the view data for the current falling piece.
     * 
     * <p>This includes the piece's shape, position, and the next piece preview.
     * 
     * @return ViewData containing the current piece information
     */
    ViewData getViewData();

    /**
     * Merges the current falling piece into the board background.
     * 
     * <p>This method is called when a piece can no longer move down.
     * The piece becomes part of the locked board state.
     */
    void mergeBrickToBackground();

    /**
     * Clears any completed rows and returns information about the clear operation.
     * 
     * <p>This method checks for fully filled rows, removes them, shifts
     * remaining blocks down, and calculates the score bonus.
     * 
     * @return ClearRow object containing information about cleared rows and score bonus
     */
    ClearRow clearRows();

    /**
     * Gets the score object for this game.
     * 
     * @return The Score object managing the game's score
     */
    Score getScore();

    /**
     * Resets the board to start a new game.
     * 
     * <p>This clears the board matrix, resets the score, and creates a new starting piece.
     */
    void newGame();

    /**
     * Gets the shape data for the second next piece in the queue.
     * 
     * <p>This is used for displaying the next piece preview queue.
     * 
     * @return A 2D array representing the second next piece's shape
     */
    int[][] getSecondNextBrickData();

    /**
     * Gets the shape data for the third next piece in the queue.
     * 
     * <p>This is used for displaying the next piece preview queue.
     * 
     * @return A 2D array representing the third next piece's shape
     */
    int[][] getThirdNextBrickData();

    /**
     * Sets a specific brick as the current piece with a given rotation.
     * 
     * <p>This method is primarily used for the hold/swap functionality,
     * allowing a previously held piece to be placed on the board.
     * 
     * @param brick The brick to set as the current piece
     * @param rotation The rotation state (0-3) for the brick
     * @return true if the brick collides immediately at the starting position, false otherwise
     */
    boolean setBrick(com.comp2042.logic.bricks.Brick brick, int rotation);
}
