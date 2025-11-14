package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    @SuppressWarnings("unused")
    private Block currentBlock;
    private Block nextBlock1;
    private Block nextBlock2;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        refreshBlockReferences();
        initializeNextBlocks();
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.createNewBrick()) {
                viewGuiController.gameOver();
            } else {
                rotateBlockQueue();
                refreshBlockReferences();
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
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        refreshBlockReferences();
        initializeNextBlocks();
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
            viewGuiController.drawNextBlock1(nextBlock1);
            viewGuiController.drawNextBlock2(nextBlock2);
        }
    }

    private void rotateBlockQueue() {
        // After board.createNewBrick(), the board has consumed nextBlock1
        // So currentBlock = nextBlock1 (what the board just got)
        // Update nextBlock1 from the board's next brick (which was nextBlock2)
        ViewData viewData = board.getViewData();
        if (viewData != null) {
            currentBlock = nextBlock1; // The block that was just consumed
            nextBlock1 = new Block(viewData.getNextBrickData()); // This is what was nextBlock2
            nextBlock2 = new Block(board.getSecondNextBrickData()); // Generate new nextBlock2
            // Update the preview panes
            viewGuiController.drawNextBlock1(nextBlock1);
            viewGuiController.drawNextBlock2(nextBlock2);
        }
    }
}
