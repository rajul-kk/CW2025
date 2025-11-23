package com.comp2042;

import com.comp2042.logic.bricks.Brick;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    @SuppressWarnings("unused")
    private Block currentBlock;
    private Block nextBlock1;
    private Block nextBlock2;
    private Block nextBlock3;

    private Brick heldBrick;
    private int heldRotation;
    private boolean canHold = true;
    
    private int highScore = 0;

    public GameController(GuiController c) {
        viewGuiController = c;
        
        // Load high score from file on startup
        highScore = HighScoreManager.loadHighScore();
        viewGuiController.updateHighScore(highScore);
        
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        refreshBlockReferences();
        initializeNextBlocks();
        heldBrick = null;
        heldRotation = 0;
        canHold = true;
    }
    
    /**
     * Checks if the current score exceeds the high score and updates it if necessary.
     */
    private void checkAndUpdateHighScore() {
        int currentScore = board.getScore().scoreProperty().getValue();
        if (currentScore > highScore) {
            highScore = currentScore;
            viewGuiController.updateHighScore(highScore);
            HighScoreManager.saveHighScore(highScore);
        }
    }

    /**
     * Calculates the Y position where the current block would land (ghost position).
     * Simulates dropping the block without actually moving it.
     * @return The Y position where the block would land, or current Y if already at bottom
     */
    public int calculateGhostY() {
        ViewData currentView = board.getViewData();
        if (currentView == null) {
            return 0;
        }
        
        int[][] boardMatrix = board.getBoardMatrix();
        int[][] brickShape = currentView.getBrickData();
        int currentX = currentView.getxPosition();
        int currentY = currentView.getyPosition();
        
        // Simulate dropping by moving down until collision
        int ghostY = currentY;
        while (!MatrixOperations.intersect(boardMatrix, brickShape, currentX, ghostY + 1)) {
            ghostY++;
            // Safety check to prevent infinite loop
            if (ghostY >= boardMatrix.length) {
                break;
            }
        }
        
        return ghostY;
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            // Get the block's position and shape before merging
            ViewData lockedBlockData = board.getViewData();
            
            board.mergeBrickToBackground();
            
            // Animate the lock effect
            viewGuiController.animateLockBlock(lockedBlockData);
            
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                checkAndUpdateHighScore();
            }
            
            // Refresh background to show locked blocks before checking for game over
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            
            // Create new brick and check for immediate collision (game over)
            if (board.createNewBrick()) {
                // Immediate collision detected - game over, don't draw the colliding block
                viewGuiController.gameOver();
                return new DownData(clearRow, null); // Return null ViewData to prevent drawing
            } else {
                // No collision - proceed normally
                rotateBlockQueue();
                refreshBlockReferences();
                canHold = true; // Allow hold again when a block locks and new one appears
            }

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
                checkAndUpdateHighScore();
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        if (!canHold) {
            return board.getViewData(); // Already held this turn, return current view
        }

        SimpleBoard simpleBoard = (SimpleBoard) board;
        Brick currentBrick = simpleBoard.getCurrentBrick();
        int currentRotation = simpleBoard.getCurrentRotation();

        if (heldBrick == null) {
            // First time holding - just store the current brick and get a new one
            heldBrick = currentBrick;
            heldRotation = currentRotation;
            board.createNewBrick();
            refreshBlockReferences();
            rotateBlockQueue();
            canHold = false;
        } else {
            // Swap the held brick with the current brick
            Brick tempBrick = heldBrick;
            int tempRotation = heldRotation;
            heldBrick = currentBrick;
            heldRotation = currentRotation;
            board.setBrick(tempBrick, tempRotation);
            refreshBlockReferences();
            canHold = false;
        }

        // Update the hold box UI
        Block holdBlock = new Block(heldBrick.getShapeMatrix().get(heldRotation));
        viewGuiController.drawHoldBlock(holdBlock);

        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        refreshBlockReferences();
        initializeNextBlocks();
        heldBrick = null;
        heldRotation = 0;
        canHold = true;
        viewGuiController.drawHoldBlock(null); // Clear hold box
    }

    private void refreshBlockReferences() {
        ViewData viewData = board.getViewData();
        if (viewData == null) {
            return;
        }
        currentBlock = new Block(viewData.getBrickData());
    }

    private void initializeNextBlocks() {
        ViewData viewData = board.getViewData();
        if (viewData != null) {
            nextBlock1 = new Block(viewData.getNextBrickData());
            nextBlock2 = new Block(board.getSecondNextBrickData());
            nextBlock3 = new Block(board.getThirdNextBrickData());
            viewGuiController.drawNextBlock1(nextBlock1);
            viewGuiController.drawNextBlock2(nextBlock2);
            viewGuiController.drawNextBlock3(nextBlock3);
        }
    }

    private void rotateBlockQueue() {
        // After board.createNewBrick(), the board has consumed nextBlock1
        // So currentBlock = nextBlock1 (what the board just got)
        // Rotate the queue: nextBlock1 = nextBlock2, nextBlock2 = nextBlock3, generate new nextBlock3
        ViewData viewData = board.getViewData();
        if (viewData != null) {
            currentBlock = nextBlock1; // The block that was just consumed
            nextBlock1 = nextBlock2; // Move nextBlock2 to nextBlock1
            nextBlock2 = nextBlock3; // Move nextBlock3 to nextBlock2
            nextBlock3 = new Block(board.getThirdNextBrickData()); // Generate new nextBlock3
            // Update the preview panes
            viewGuiController.drawNextBlock1(nextBlock1);
            viewGuiController.drawNextBlock2(nextBlock2);
            viewGuiController.drawNextBlock3(nextBlock3);
        }
    }

    public void dropInstant() {
        // Track the number of rows dropped for scoring
        int rowsDropped = 0;
        
        // Keep moving the block down until it hits the bottom/collision
        while (board.moveBrickDown()) {
            // Increment counter for each successful move down
            rowsDropped++;
        }
        
        // Block has hit the bottom - trigger screen shake effect
        viewGuiController.shakeBoard();
        
        // Calculate and award drop distance score (2 points per row for Hard Drop)
        if (rowsDropped > 0) {
            int dropScore = rowsDropped * 2;
            board.getScore().add(dropScore);
            checkAndUpdateHighScore();
        }
        
        // Block has hit the bottom, now lock it and spawn next one
        // Get the block's position and shape before merging
        ViewData lockedBlockData = board.getViewData();
        
        board.mergeBrickToBackground();
        
        // Animate the lock effect
        viewGuiController.animateLockBlock(lockedBlockData);
        
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            checkAndUpdateHighScore();
            // Show the score notification popup
            viewGuiController.showScoreNotification(clearRow);
        }
        
        // Refresh background to show locked blocks before checking for game over
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        
        // Create new brick and check for immediate collision (game over)
        if (board.createNewBrick()) {
            // Immediate collision detected - game over, don't draw the colliding block
            viewGuiController.gameOver();
            return; // Exit early, don't draw the colliding block
        } else {
            // No collision - proceed normally
            rotateBlockQueue();
            refreshBlockReferences();
            canHold = true; // Allow hold again when a block locks and new one appears
            viewGuiController.refreshBrick(board.getViewData());
        }
    }
}
