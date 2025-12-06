package com.comp2042.controller;

import com.comp2042.data.DownData;
import com.comp2042.data.MoveEvent;
import com.comp2042.data.ViewData;

/**
 * Interface for handling game input events.
 * 
 * <p>This interface defines methods that respond to player input actions
 * such as moving, rotating, and holding pieces. Implementations of this
 * interface process these events and return updated game state data.
 * 
 * <p>The interface is used to decouple input handling from game logic,
 * allowing the {@link InputHandler} to communicate with the game controller
 * without tight coupling.
 * 
 * @author Rajul Kabir
 * @version 1.0
 * @see InputHandler
 * @see GameController
 * @see MoveEvent
 * @see DownData
 * @see ViewData
 */
public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    ViewData onHoldEvent(MoveEvent event);

    void createNewGame();
}
