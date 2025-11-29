package com.comp2042.data;

import com.comp2042.util.ClearRow;

/**
 * Data transfer object that encapsulates the result of a down movement event.
 * 
 * <p>This immutable class is used to return information from the game controller
 * when a piece moves down. It contains:
 * <ul>
 *   <li>Information about any rows that were cleared (if any)</li>
 *   <li>The updated view data for the current piece</li>
 * </ul>
 * 
 * <p>If a piece locks and no rows are cleared, {@code clearRow} may be null.
 * If the game is over, {@code viewData} may be null.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see com.comp2042.controller.GameController
 * @see ClearRow
 * @see ViewData
 */
public final class DownData {
    /** Information about cleared rows, or null if no rows were cleared. */
    private final ClearRow clearRow;
    
    /** Updated view data for the current piece, or null if game is over. */
    private final ViewData viewData;

    /**
     * Creates a new DownData object.
     * 
     * @param clearRow Information about cleared rows, or null if none
     * @param viewData Updated view data for the current piece, or null if game over
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Gets the clear row information.
     * 
     * @return Information about cleared rows, or null if no rows were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }

    /**
     * Gets the updated view data.
     * 
     * @return Updated view data for the current piece, or null if game is over
     */
    public ViewData getViewData() {
        return viewData;
    }
}
