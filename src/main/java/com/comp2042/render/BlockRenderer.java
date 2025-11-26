package com.comp2042.render;

import com.comp2042.model.Block;
import com.comp2042.model.GameConstants;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for rendering blocks to different containers.
 * Handles drawing falling blocks, ghost blocks, and preview blocks.
 */
public class BlockRenderer {
    
    private final List<Node> ghostNodes = new ArrayList<>();
    private GridPane ghostGridPane;
    
    /**
     * Context object for rendering operations to reduce parameter count.
     */
    private static class RenderingContext {
        final GridPane gridPane;
        final List<javafx.scene.Node> nodeList;
        final BlockStyle style;
        
        RenderingContext(GridPane gridPane, List<javafx.scene.Node> nodeList, BlockStyle style) {
            this.gridPane = gridPane;
            this.nodeList = nodeList;
            this.style = style;
        }
    }
    
    
    /**
     * Renders a block to a GridPane (for falling blocks and ghost blocks on the game board).
     * 
     * @param shape The block shape data (2D array)
     * @param xPos The X position on the board
     * @param yPos The Y position on the board
     * @param gridPane The GridPane to render to
     * @param nodeList List to store the created nodes (for later removal)
     * @param style The rendering style (NORMAL or GHOST)
     */
    public void renderToGridPane(int[][] shape, int xPos, int yPos, 
                                  GridPane gridPane, List<javafx.scene.Node> nodeList, 
                                  BlockStyle style) {
        if (shape == null || gridPane == null) {
            return;
        }
        
        RenderingContext context = new RenderingContext(gridPane, nodeList, style);
        for (int i = 0; i < shape.length; i++) {
            renderRowToGridPane(shape[i], i, xPos, yPos, context);
        }
    }
    
    /**
     * Renders a single row of the block shape to the GridPane.
     * 
     * @param row The row data from the shape array
     * @param rowIndex The index of the row in the shape
     * @param xPos The X position on the board
     * @param yPos The Y position on the board
     * @param context The rendering context containing gridPane, nodeList, and style
     */
    private void renderRowToGridPane(int[] row, int rowIndex, int xPos, int yPos,
                                     RenderingContext context) {
        for (int j = 0; j < row.length; j++) {
            if (row[j] != 0) {
                renderCellToGridPane(row[j], rowIndex, j, xPos, yPos, context);
            }
        }
    }
    
    /**
     * Renders a single cell of the block to the GridPane.
     * 
     * @param colorCode The color code for the cell
     * @param rowIndex The row index in the shape
     * @param colIndex The column index in the shape
     * @param xPos The X position on the board
     * @param yPos The Y position on the board
     * @param context The rendering context containing gridPane, nodeList, and style
     */
    private void renderCellToGridPane(int colorCode, int rowIndex, int colIndex, int xPos, int yPos,
                                      RenderingContext context) {
        Rectangle rectangle = createRectangle(colorCode, GameConstants.BRICK_SIZE, context.style);
        
        int gridColumn = xPos + colIndex;
        int gridRow = yPos + rowIndex - GameConstants.HIDDEN_ROW_OFFSET;
        
        if (isValidGridPosition(gridRow, gridColumn)) {
            context.gridPane.add(rectangle, gridColumn, gridRow);
            if (context.nodeList != null) {
                context.nodeList.add(rectangle);
            }
        }
    }
    
    /**
     * Renders a block to a Pane (for preview blocks like next blocks and hold block).
     * 
     * @param shape The block shape data (2D array)
     * @param pane The Pane to render to
     * @param style The rendering style (should be PREVIEW)
     */
    public void renderToPane(int[][] shape, Pane pane, BlockStyle style) {
        if (shape == null || pane == null) {
            return;
        }
        
        pane.getChildren().clear();
        
        int offsetX = (int) (pane.getPrefWidth() - shape[0].length * GameConstants.PREVIEW_BRICK_SIZE) / 2;
        int offsetY = (int) (pane.getPrefHeight() - shape.length * GameConstants.PREVIEW_BRICK_SIZE) / 2;
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    Rectangle rectangle = createRectangle(shape[i][j], GameConstants.PREVIEW_BRICK_SIZE, style);
                    rectangle.setLayoutX((double) offsetX + j * GameConstants.PREVIEW_BRICK_SIZE);
                    rectangle.setLayoutY((double) offsetY + i * GameConstants.PREVIEW_BRICK_SIZE);
                    pane.getChildren().add(rectangle);
                }
            }
        }
    }
    
    /**
     * Creates a styled rectangle based on the color code and style.
     */
    private Rectangle createRectangle(int colorCode, int size, BlockStyle style) {
        Rectangle rectangle = new Rectangle(size, size);
        
        switch (style) {
            case GHOST -> configureGhostStyle(rectangle);
            case PREVIEW -> configurePreviewStyle(rectangle, colorCode);
            case NORMAL -> configureNormalStyle(rectangle, colorCode);
        }
        
        return rectangle;
    }
    
    private void configureNormalStyle(Rectangle rectangle, int colorCode) {
        rectangle.setFill(BlockRenderer.getFillColor(colorCode));
        rectangle.setStroke(BlockRenderer.getBorderColor(colorCode));
        rectangle.setStrokeType(StrokeType.INSIDE);
        rectangle.setStrokeWidth(1.5);
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }
    
    private void configurePreviewStyle(Rectangle rectangle, int colorCode) {
        rectangle.setFill(BlockRenderer.getFillColor(colorCode));
        rectangle.setStroke(BlockRenderer.getBorderColor(colorCode));
        rectangle.setStrokeType(StrokeType.INSIDE);
        rectangle.setStrokeWidth(1.0);
        rectangle.setArcHeight(5);
        rectangle.setArcWidth(5);
    }
    
    private void configureGhostStyle(Rectangle rectangle) {
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.color(1, 1, 1, 0.5));
        rectangle.setStrokeType(StrokeType.INSIDE);
        rectangle.setStrokeWidth(2);
        rectangle.getStrokeDashArray().addAll(5d, 5d);
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
    }
    
    private boolean isValidGridPosition(int row, int column) {
        return row >= 0 && column >= 0 && column < GameConstants.BOARD_WIDTH;
    }
    
    /**
     * Gets the fill color for a given color code.
     * Public static method for use in other classes (e.g., for background rendering).
     */
    public static Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                // AQUA - Cyan gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(0, 255, 255)),
                    new Stop(0.3, Color.rgb(0, 200, 255)),
                    new Stop(0.7, Color.rgb(0, 150, 200)),
                    new Stop(1.0, Color.rgb(0, 100, 150)));
                break;
            case 2:
                // BLUEVIOLET - Purple gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(138, 43, 226)),
                    new Stop(0.3, Color.rgb(120, 30, 200)),
                    new Stop(0.7, Color.rgb(100, 20, 170)),
                    new Stop(1.0, Color.rgb(80, 10, 140)));
                break;
            case 3:
                // DARKGREEN - Green gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(0, 200, 0)),
                    new Stop(0.3, Color.rgb(0, 150, 0)),
                    new Stop(0.7, Color.rgb(0, 100, 0)),
                    new Stop(1.0, Color.rgb(0, 60, 0)));
                break;
            case 4:
                // YELLOW - Yellow gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(255, 255, 0)),
                    new Stop(0.3, Color.rgb(255, 220, 0)),
                    new Stop(0.7, Color.rgb(220, 180, 0)),
                    new Stop(1.0, Color.rgb(180, 140, 0)));
                break;
            case 5:
                // RED - Red gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(255, 0, 0)),
                    new Stop(0.3, Color.rgb(220, 0, 0)),
                    new Stop(0.7, Color.rgb(180, 0, 0)),
                    new Stop(1.0, Color.rgb(140, 0, 0)));
                break;
            case 6:
                // BEIGE - Orange gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(255, 200, 150)),
                    new Stop(0.3, Color.rgb(255, 180, 120)),
                    new Stop(0.7, Color.rgb(220, 150, 100)),
                    new Stop(1.0, Color.rgb(180, 120, 80)));
                break;
            case 7:
                // BURLYWOOD - Brown gem
                returnPaint = new LinearGradient(0, 0, 1, 1, true, null,
                    new Stop(0.0, Color.rgb(222, 184, 135)),
                    new Stop(0.3, Color.rgb(200, 160, 110)),
                    new Stop(0.7, Color.rgb(170, 130, 90)),
                    new Stop(1.0, Color.rgb(140, 100, 70)));
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }
    
    /**
     * Gets the border color for a given color code.
     * Public static method for use in other classes (e.g., for background rendering).
     */
    public static Color getBorderColor(int i) {
        switch (i) {
            case 1: // AQUA
                return Color.rgb(150, 255, 255);
            case 2: // BLUEVIOLET
                return Color.rgb(180, 100, 255);
            case 3: // DARKGREEN
                return Color.rgb(100, 255, 100);
            case 4: // YELLOW
                return Color.rgb(255, 255, 150);
            case 5: // RED
                return Color.rgb(255, 150, 150);
            case 6: // BEIGE
                return Color.rgb(255, 220, 180);
            case 7: // BURLYWOOD
                return Color.rgb(240, 200, 160);
            default:
                return Color.WHITE;
        }
    }
    
    /**
     * Initializes ghost block management with the GridPane to render to.
     * Must be called before using ghost block methods.
     * 
     * @param gridPane The GridPane where ghost blocks will be rendered
     */
    public void initializeGhostManagement(GridPane gridPane) {
        this.ghostGridPane = gridPane;
        clearGhost();
    }
    
    /**
     * Draws a ghost piece (outline with dotted border) showing where the block will land.
     * Automatically clears any existing ghost before drawing the new one.
     * 
     * @param block The block shape to draw
     * @param xPos The X position of the block
     * @param ghostY The Y position where the ghost should appear (calculated landing position)
     */
    public void drawGhost(Block block, int xPos, int ghostY) {
        if (ghostGridPane == null) {
            return; // Ghost management not initialized
        }
        
        // Remove old ghost nodes
        clearGhost();
        
        if (block == null) {
            return;
        }
        
        int[][] shape = block.getShape();
        
        // Render the ghost block
        renderToGridPane(shape, xPos, ghostY, ghostGridPane, ghostNodes, BlockStyle.GHOST);
    }
    
    /**
     * Clears the ghost piece from the display.
     */
    public void clearGhost() {
        if (ghostGridPane == null) {
            return; // Ghost management not initialized
        }
        
        for (Node node : ghostNodes) {
            ghostGridPane.getChildren().remove(node);
        }
        ghostNodes.clear();
    }
    
    /**
     * Enum for different block rendering styles.
     */
    public enum BlockStyle {
        NORMAL,   // Normal falling block with colors
        GHOST,    // Ghost block with transparent fill and dotted outline
        PREVIEW   // Preview block for next/hold panes
    }
}

