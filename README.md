# TetrisJFX - JavaFX Tetris Game

## GitHub Repository
**GitHub:** https://github.com/rajul-kk/CW2025.git

## Compilation Instructions

### Prerequisites
- Java Development Kit (JDK) 23 or higher
- Apache Maven 3.6.0 or higher
- JavaFX 21.0.6 (automatically downloaded via Maven)

### Step-by-Step Compilation Guide

1. **Clone the repository** (if not already done):
   ```bash
   git clone https://github.com/rajul-kk/CW2025.git
   cd CW2025
   ```

2. **Navigate to the project directory**:
   ```bash
   cd CW2025Test
   ```

3. **Compile the project using Maven**:
   ```bash
   mvn clean compile
   ```

4. **Run the application**:
   ```bash
   mvn javafx:run
   ```
   
   Alternatively, you can use the Maven wrapper:
   ```bash
   ./mvnw clean compile
   ./mvnw javafx:run
   ```
   
   On Windows:
   ```bash
   mvnw.cmd clean compile
   mvnw.cmd javafx:run
   ```

5. **Build a JAR file** (optional):
   ```bash
   mvn clean package
   ```
   The JAR file will be created in the `target` directory.

### Dependencies
All dependencies are automatically managed by Maven and specified in `pom.xml`:
- **JavaFX Controls** (21.0.6) - For UI components
- **JavaFX FXML** (21.0.6) - For FXML-based UI layouts
- **JUnit Jupiter** (5.12.1) - For unit testing (test scope)

### Special Settings
- **Java Version**: The project requires Java 23 (source and target set to 23 in `pom.xml`)
- **Main Class**: `com.comp2042.app.Launcher`
- **Resource Files**: Font files (`digital.ttf`), images, and FXML layouts are located in `src/main/resources/`

---

## Implemented and Working Properly

### Core Game Mechanics
1. **Ghost Piece Preview**
   - Visual preview showing where the current piece will land
   - Semi-transparent ghost piece displayed on the game board
   - Updates in real-time as the piece moves or rotates

2. **Hold/Swap Functionality**
   - Press 'C' to hold the current piece
   - Swaps between held piece and current piece
   - Can only hold once per piece placement (prevents abuse)
   - Visual hold box displays the held piece

3. **Next Piece Queue**
   - Shows the next 3 pieces that will appear
   - Helps with strategic planning
   - Uses 7-bag randomizer for fair distribution

4. **7-Bag Randomizer**
   - Ensures fair piece distribution
   - Every 7 pieces contains one of each piece type
   - Prevents long droughts of specific pieces

5. **Hard Drop**
   - Press Spacebar to instantly drop the piece to the bottom
   - Awards bonus points (2 points per row dropped)
   - Triggers screen shake effect on impact

6. **Scoring System**
   - Points for soft drop (1 point per cell moved down manually)
   - Points for hard drop (2 points per row)
   - Bonus points for line clears (50 × lines²)

7. **Level System**
   - Level increases every 10 lines cleared
   - Game speed increases with each level using Modern Tetris Guideline formula
   - Level, lines cleared, and current speed displayed on screen
   - Drop interval calculated as: `(0.8 - ((level - 1) * 0.01))^(level - 1)` seconds

8. **High Score Tracking**
   - Separate high scores for Classic and Phantom modes
   - High scores saved to files (`highscore.txt` and `highscore_phantom.txt`)
   - Automatically updates when new high score is achieved

9. **Phantom Mode**
    - Special game mode where locked blocks fade to invisible after placement
    - Creates a challenging gameplay experience
    - Separate high score tracking for phantom mode

### Visual Effects and Animations
10. **Lock Pulse Animation**
    - Blocks pulse when they lock into place
    - Scale animation provides visual feedback

11. **Board Shake Effect**
    - Screen shakes when a piece hard drops
    - Adds impact and excitement to the gameplay

12. **Flash Effect**
    - All blocks flash when rows are cleared
    - Helps player re-orient after line clears

13. **Illumination Effect**
    - Area around newly locked blocks briefly illuminates
    - 2x2 radius highlight effect

### User Interface
14. **Main Menu**
    - Start game option
    - Game mode selection (Classic/Phantom)
    - Exit option

15. **Pause Menu**
    - Press ESC to pause/resume
    - Options: Resume, New Game, Controls, Exit
    - Modal dialog that pauses game state

16. **Controls Dialog**
    - Displays all keyboard controls
    - Accessible from pause menu
    - Organized by category (Movement, Game Controls, Menu Shortcuts)

17. **Game Over Screen**
    - Displays when game ends
    - Options to start new game or exit

18. **Visual Polish**
    - Custom digital font for retro aesthetic
    - Gradient-filled blocks with borders
    - Grid lines on game board
    - Styled UI elements with CSS

---

## Implemented but Not Working Properly

The following features have been implemented but are not functioning correctly or have known issues:

*(No features currently listed in this category - all implemented features are working as expected.)*

---


## Features Not Implemented

The following features found in modern Tetris games (e.g., Tetris Guideline) were not implemented:

1. **Super Rotation System (SRS)**
   - **Why not implemented**: The game uses a basic rotation system without wall kicks or floor kicks. SRS requires complex collision detection and multiple rotation attempts, which would require significant refactoring of the rotation logic.

2. **T-Spin Detection and Scoring**
   - **Why not implemented**: T-spin detection requires analyzing the board state after rotation to determine if a T-piece is in a "spin" position. This feature was deemed non-essential for core gameplay and would require additional scoring logic and UI indicators.

3. **Combo System**
   - **Why not implemented**: While line clears work correctly, there's no tracking of consecutive line clears or bonus scoring for combos. This would require maintaining game state history and additional scoring calculations.

4. **All-Clear Bonus**
   - **Why not implemented**: Detecting when the entire board is cleared would require checking the board state after every line clear. This feature was considered a nice-to-have rather than essential.

5. **Lock Delay and Lock Reset**
   - **Why not implemented**: Modern Tetris includes a delay before a piece locks, allowing players to make last-second adjustments. This would require implementing a timer system and modifying the lock detection logic.

6. **Infinite Spin Prevention**
   - **Why not implemented**: The current system allows pieces to rotate in place if there's space. Modern Tetris prevents infinite spinning by limiting rotations in the same position.

7. **Multiplayer Support**
   - **Why not implemented**: Multiplayer would require network programming, game state synchronization, and significant architectural changes. This was beyond the scope of a single-player Tetris implementation.

8. **Replay System**
   - **Why not implemented**: Recording and replaying games would require saving all game events and states, which would add significant complexity and storage requirements.

9. **Sound Effects and Music**
   - **Why not implemented**: Audio was not included in the project requirements. Adding sound would require audio libraries and asset management.

---

## New Java Classes

The following classes were newly created for this assignment:

### Rendering and Display
1. **`BlockRenderer`** (`com.comp2042.render`)
   - Purpose: Handles rendering of blocks to different containers (game board, preview panes)
   - Location: `src/main/java/com/comp2042/render/BlockRenderer.java`
   - Description: Provides methods to render blocks with different styles (normal, ghost) and manages visual properties like colors, gradients, and borders.

2. **`BoardDisplayManager`** (`com.comp2042.render`)
   - Purpose: Manages the display matrix (Rectangle[][]) that represents the game board visually
   - Location: `src/main/java/com/comp2042/render/BoardDisplayManager.java`
   - Description: Initializes and updates the visual representation of the game board, handles rectangle styling, and detects newly locked blocks for visual effects.

3. **`GameEffects`** (`com.comp2042.render`)
   - Purpose: Handles visual effects and animations for the game UI
   - Location: `src/main/java/com/comp2042/render/GameEffects.java`
   - Description: Provides animations including lock pulse, board shake, flash effects, and area illumination. Separates animation logic from the main controller.

4. **`PhantomManager`** (`com.comp2042.render`)
   - Purpose: Manages phantom mode functionality where blocks fade after locking
   - Location: `src/main/java/com/comp2042/render/PhantomManager.java`
   - Description: Controls phantom mode state and applies fade transitions to locked blocks in phantom mode.

### Game Logic
5. **`LevelManager`** (`com.comp2042.model`)
   - Purpose: Manages level progression and lines cleared tracking
   - Location: `src/main/java/com/comp2042/model/LevelManager.java`
   - Description: Calculates current level based on lines cleared, updates drop interval using Modern Tetris Guideline formula, and provides level update results.

6. **`HighScoreManager`** (`com.comp2042.model`)
   - Purpose: Handles high score persistence for both Classic and Phantom modes
   - Location: `src/main/java/com/comp2042/model/HighScoreManager.java`
   - Description: Loads and saves high scores to separate files for each game mode. Uses static methods for file I/O operations.

7. **`GameConstants`** (`com.comp2042.model`)
   - Purpose: Centralized constants for the Tetris game application
   - Location: `src/main/java/com/comp2042/model/GameConstants.java`
   - Description: Contains all project-wide constants including UI dimensions, animation timing, CSS class names, file names, and game board dimensions. Organized into logical sections for easy maintenance.

8. **`RandomBrickGenerator`** (`com.comp2042.logic.bricks`)
   - Purpose: Implements 7-bag randomizer for fair piece distribution
   - Location: `src/main/java/com/comp2042/logic/bricks/RandomBrickGenerator.java`
   - Description: Ensures every 7 pieces contains one of each piece type. Maintains a queue for peeking at next pieces.

### User Interface
9. **`PauseMenuDialog`** (`com.comp2042.ui`)
   - Purpose: Manages the pause menu dialog
   - Location: `src/main/java/com/comp2042/ui/PauseMenuDialog.java`
   - Description: Creates and manages a modal pause menu with options to resume, start new game, show controls, or exit. Decouples pause menu UI from the main controller.

10. **`ControlsDialog`** (`com.comp2042.ui`)
    - Purpose: Manages the controls dialog showing all keyboard shortcuts
    - Location: `src/main/java/com/comp2042/ui/ControlsDialog.java`
    - Description: Displays organized list of game controls grouped by category. Modal dialog accessible from pause menu.

### Application
11. **`Launcher`** (`com.comp2042.app`)
    - Purpose: Entry point for the JavaFX application
    - Location: `src/main/java/com/comp2042/app/Launcher.java`
    - Description: Launches the JavaFX application by calling `Application.launch()` with the Main class.

12. **`DownData`** (`com.comp2042.app`)
    - Purpose: Data transfer object for down event results
    - Location: `src/main/java/com/comp2042/app/DownData.java`
    - Description: Carries information about line clears and view data when a piece moves down.

---

## Modified Java Classes

The following classes from the original codebase were modified to add new functionality:

### Controllers
1. **`GameController`** (`com.comp2042.controller`)
   - **Changes Made**:
     - Added hold/swap functionality with `heldBrick` and `canHold` fields
     - Implemented ghost piece calculation with `calculateGhostY()` method
     - Integrated level system with `LevelManager`
     - Added high score tracking with `HighScoreManager`
     - Implemented hard drop functionality with `dropInstant()` method
     - Added visual effects integration
     - Modified `onDownEvent()` to handle animations and level updates
     - Added `onHoldEvent()` for hold/swap mechanics
   - **Why Modified**: Core game logic needed to support new features like hold, ghost piece, levels, and enhanced scoring.

2. **`GuiController`** (`com.comp2042.view`)
   - **Changes Made**:
     - Integrated `BoardDisplayManager` for board rendering
     - Added `GameEffects` for animations
     - Integrated `PhantomManager` for phantom mode
     - Added methods for ghost piece rendering
     - Implemented hold block display
     - Added level, lines, and speed display updates
     - Added pause menu and controls dialog management
     - Implemented phantom mode toggle
     - Added methods for visual effects (shake, flash, illuminate, lock pulse)
   - **Why Modified**: UI controller needed to coordinate all visual elements, animations, and new UI components.

### Model
3. **`SimpleBoard`** (`com.comp2042.model`)
   - **Changes Made**:
     - Added `getSecondNextBrickData()` and `getThirdNextBrickData()` methods for next piece queue
     - Added `setBrick()` method to support hold/swap functionality
     - Added `getCurrentBrick()` and `getCurrentRotation()` methods for hold system
     - Modified to use `RandomBrickGenerator` instead of basic random generation
   - **Why Modified**: Board needed to support preview queue and hold functionality, requiring access to brick generator's peek methods and ability to set specific bricks.

4. **`Board`** (Interface) (`com.comp2042.model`)
   - **Changes Made**:
     - Added `getSecondNextBrickData()` method
     - Added `getThirdNextBrickData()` method
     - Added `setBrick(Brick brick, int rotation)` method
   - **Why Modified**: Interface needed to be extended to support new features like next piece queue and hold functionality.

---

## Unexpected Problems

### 1. Phantom Mode Fade Timing Issues
**Problem**: Initially, blocks in phantom mode would fade immediately upon locking, but the fade animation would conflict with the illumination effect, causing visual glitches.

**Solution**: Implemented a callback system in `GameEffects` that allows post-illumination effects to be applied after the illumination completes. The `PhantomManager` now applies fade effects after the illumination animation finishes, ensuring smooth visual transitions.

### 2. Ghost Piece Rendering Conflicts
**Problem**: The ghost piece would sometimes render on top of locked blocks or not update correctly when pieces moved quickly.

**Solution**: Implemented a node list tracking system in `BlockRenderer` to properly manage ghost piece nodes. Ghost nodes are cleared before rendering new ones, and the rendering order ensures ghost pieces appear behind locked blocks but above the board background.

### 3. Hold Functionality State Management
**Problem**: The hold system initially allowed infinite holding, which could be exploited. Also, the hold state wasn't properly reset when starting a new game.

**Solution**: Added a `canHold` flag that prevents holding more than once per piece placement. The flag is reset when a new piece spawns after locking. Also ensured hold state is cleared in `createNewGame()` method.

### 4. Level System Speed Calculation
**Problem**: The initial level system used a simple linear speed increase, which didn't match modern Tetris guidelines and became too fast too quickly.

**Solution**: Implemented the Modern Tetris Guideline formula for drop interval calculation: `(0.8 - ((level - 1) * 0.01))^(level - 1)` seconds. This provides a more balanced progression that starts slow and accelerates appropriately.

### 5. High Score File Persistence
**Problem**: High scores weren't persisting between game sessions, and there was no separation between Classic and Phantom mode scores.

**Solution**: Created `HighScoreManager` with separate file handling for each mode (`highscore.txt` and `highscore_phantom.txt`). Added proper file I/O with error handling and file existence checks.

### 6. Animation Performance Issues
**Problem**: Multiple simultaneous animations (lock pulse, flash, shake) caused frame rate drops and visual stuttering.

**Solution**: Optimized animations by using JavaFX's built-in animation classes efficiently, ensuring animations don't overlap unnecessarily, and using parallel transitions where appropriate. Also limited the number of simultaneous animations.

### 7. Board Display Matrix Synchronization
**Problem**: The display matrix (Rectangle[][]) would sometimes get out of sync with the game board matrix, causing visual artifacts.

**Solution**: Implemented `BoardDisplayManager` to centralize all display matrix operations. Added proper initialization and refresh methods that ensure the display matrix always matches the game board state.

### 8. Next Piece Queue Display
**Problem**: The next piece queue would show incorrect pieces or not update when pieces were consumed.

**Solution**: Implemented a proper queue rotation system in `GameController.rotateBlockQueue()` that tracks the relationship between the board's brick generator and the displayed preview blocks. The queue is properly maintained when pieces are consumed.

### 9. Hard Drop Cooldown
**Problem**: Players could spam hard drop, causing multiple rapid drops and potential game state issues.

**Solution**: Implemented a cooldown system using timestamps to prevent hard drop from being triggered too frequently (300ms cooldown). This prevents accidental double-drops and maintains game flow.

### 10. FXML Resource Loading
**Problem**: FXML files sometimes failed to load, especially when running from JAR files, due to incorrect resource paths.

**Solution**: Used `getClass().getClassLoader().getResource()` for consistent resource loading that works both in development and in packaged JAR files. Ensured all resource paths are relative to the resources directory.

### 11. Digital Font Loading Issues
**Problem**: The custom digital font (`digital.ttf`) was not loading correctly when the application started. The font would fail to load when using direct URL paths, especially in different execution environments (IDE vs JAR file). This caused the UI to fall back to system fonts, breaking the retro aesthetic design.

**Solution**: Created `FontLoader` utility class that uses `getResourceAsStream()` to load the font via InputStream (more reliable for JAR files), with a fallback to URL-based loading. It caches the loaded font family name and provides helper methods for easy access. The font is loaded early in the application lifecycle (in `Main.start()`) before any UI elements are created, ensuring the font is available when needed.

### 12. Grid Lines Disappearing When Blocks Lock
**Problem**: Grid lines disappeared when blocks locked, especially in phantom mode. Visual effects were setting empty cells' opacity to 0.0, making the grid invisible.

**Solution**: Modified `PhantomManager.applyPostIlluminationEffect()` and `BoardDisplayManager.setRectangleData()` to maintain opacity at 1.0 for empty cells, ensuring grid lines remain visible in both Classic and Phantom modes.

---

## Additional Notes

- The game uses JavaFX for the user interface and follows the MVC (Model-View-Controller) architecture pattern.
- All visual effects use JavaFX animations for smooth performance.
- The codebase is organized into logical packages: `model`, `view`, `controller`, `render`, `ui`, `logic`, `data`, and `util`.
- The project follows Java naming conventions and includes Javadoc comments for major classes and methods.

