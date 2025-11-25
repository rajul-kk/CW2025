package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> bag;
    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    public RandomBrickGenerator() {
        // Initialize the bag
        bag = new ArrayList<>();
        refillBag();
        
        // Pre-populate the next bricks queue for peeking (need at least 3 for peek methods)
        ensureNextBricksQueue(3);
    }

    /**
     * Refills the bag with one of each brick type and shuffles it.
     * This ensures fair distribution - every 7 blocks will contain all types.
     */
    private void refillBag() {
        bag.clear();
        // Add one of each brick type to the bag (create new instances)
        bag.add(new IBrick());
        bag.add(new JBrick());
        bag.add(new LBrick());
        bag.add(new OBrick());
        bag.add(new SBrick());
        bag.add(new TBrick());
        bag.add(new ZBrick());
        // Shuffle the bag to randomize the order
        Collections.shuffle(bag);
    }

    /**
     * Ensures the nextBricks queue has at least the specified number of bricks for peeking ahead.
     * Bricks are drawn from the bag, and the bag is refilled when empty.
     */
    private void ensureNextBricksQueue(int minSize) {
        while (nextBricks.size() < minSize) {
            if (bag.isEmpty()) {
                refillBag();
            }
            // Remove from index 0 (first item) and add to queue
            nextBricks.add(bag.remove(0));
        }
    }

    @Override
    public Brick getBrick() {
        // Ensure we have at least one brick in the queue
        ensureNextBricksQueue(1);
        
        // Remove and return the first item from the queue (which came from the bag)
        Brick brick = nextBricks.poll();
        
        // Ensure the peek queue is maintained for future peeks
        ensureNextBricksQueue(3);
        
        return brick;
    }

    @Override
    public Brick getNextBrick() {
        ensureNextBricksQueue(1);
        return nextBricks.peek();
    }

    @Override
    public Brick getSecondNextBrick() {
        ensureNextBricksQueue(2);
        Object[] bricksArray = nextBricks.toArray();
        return (Brick) bricksArray[1];
    }

    @Override
    public Brick getThirdNextBrick() {
        ensureNextBricksQueue(3);
        Object[] bricksArray = nextBricks.toArray();
        return (Brick) bricksArray[2];
    }
}
