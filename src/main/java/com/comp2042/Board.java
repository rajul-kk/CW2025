package com.comp2042;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    int[][] getSecondNextBrickData();

    int[][] getThirdNextBrickData();

    boolean setBrick(com.comp2042.logic.bricks.Brick brick, int rotation);
}
