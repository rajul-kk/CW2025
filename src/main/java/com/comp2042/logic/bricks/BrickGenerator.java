package com.comp2042.logic.bricks;

/**
 * Interface for generating Tetris bricks (pieces).
 * 
 * <p>This interface defines methods for obtaining bricks, including the current
 * brick and preview bricks for the next piece queue. Implementations should
 * provide fair distribution of pieces, typically using a 7-bag randomizer.
 * 
 * <p>The generator supports peeking at future bricks without consuming them,
 * which is essential for displaying the next piece queue.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see RandomBrickGenerator
 * @see Brick
 */
public interface BrickGenerator {

    /**
     * Gets the next brick and removes it from the generator.
     * 
     * <p>This method consumes the brick, meaning subsequent calls will
     * return different bricks. This is used when a new piece spawns.
     * 
     * @return The next brick to appear
     */
    Brick getBrick();

    /**
     * Gets the first next brick without consuming it.
     * 
     * <p>This method allows peeking at the next piece without removing
     * it from the queue. Used for displaying the next piece preview.
     * 
     * @return The first next brick (will be returned by next getBrick() call)
     */
    Brick getNextBrick();

    /**
     * Gets the second next brick without consuming it.
     * 
     * <p>This method allows peeking at the second future piece without
     * removing it from the queue. Used for displaying the next piece queue.
     * 
     * @return The second next brick
     */
    Brick getSecondNextBrick();

    /**
     * Gets the third next brick without consuming it.
     * 
     * <p>This method allows peeking at the third future piece without
     * removing it from the queue. Used for displaying the next piece queue.
     * 
     * @return The third next brick
     */
    Brick getThirdNextBrick();
}
