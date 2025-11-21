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

    public GameController(GuiController c) {
        viewGuiController = c;
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
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            } else {
                rotateBlockQueue();
                refreshBlockReferences();
                canHold = true; // Allow hold again when a block locks and new one appears
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
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
        // Keep moving the block down until it hits the bottom/collision
        while (board.moveBrickDown()) {
            // Continue moving down
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
        }
        if (board.createNewBrick()) {
            viewGuiController.gameOver();
        } else {
            rotateBlockQueue();
            refreshBlockReferences();
            canHold = true; // Allow hold again when a block locks and new one appears
        }

        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.refreshBrick(board.getViewData());
    }
}
