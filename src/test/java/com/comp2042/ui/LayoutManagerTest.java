package com.comp2042.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LayoutManager class.
 * 
 * Tests layout binding functionality, including width, height, and size
 * bindings with and without scale factors.
 */
class LayoutManagerTest {

    private Scene testScene;
    private LayoutManager layoutManager;

    @BeforeAll
    static void initJavaFX() {
        // Initialize JavaFX toolkit if not already initialized
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX already initialized, ignore
        }
    }

    @BeforeEach
    void setUp() {
        // Create a test scene with known dimensions
        Pane root = new Pane();
        testScene = new Scene(root, 800, 600);
        layoutManager = new LayoutManager(testScene);
    }

    @Test
    void bindWidth() {
        // Test binding width without scale factor (direct binding)
        Pane pane = new Pane();
        layoutManager.bindWidth(pane);
        
        // Verify binding exists
        assertTrue(pane.prefWidthProperty().isBound(), 
            "Pane width should be bound to scene width");
        
        // Note: We can't directly set scene width (it's private), but we can verify
        // the binding exists and will respond to scene size changes
        
        // Test with null pane (should not throw)
        assertDoesNotThrow(() -> layoutManager.bindWidth(null), 
            "bindWidth should handle null pane gracefully");
    }

    @Test
    void testBindWidth() {
        // Test binding width with scale factor
        Pane pane = new Pane();
        double scaleFactor = 0.8;
        layoutManager.bindWidth(pane, scaleFactor);
        
        // Verify binding exists
        assertTrue(pane.prefWidthProperty().isBound(), 
            "Pane width should be bound to scene width with scale factor");
        
        // Test with different scale factors
        Pane pane2 = new Pane();
        layoutManager.bindWidth(pane2, 0.5);
        assertTrue(pane2.prefWidthProperty().isBound(), 
            "Should bind with 0.5 scale factor");
        
        Pane pane3 = new Pane();
        layoutManager.bindWidth(pane3, 1.0);
        assertTrue(pane3.prefWidthProperty().isBound(), 
            "Should bind with 1.0 scale factor");
        
        // Test with null scale factor (should bind directly)
        Pane pane4 = new Pane();
        layoutManager.bindWidth(pane4, null);
        assertTrue(pane4.prefWidthProperty().isBound(), 
            "Should bind directly when scale factor is null");
        
        // Test with null pane (should not throw)
        assertDoesNotThrow(() -> layoutManager.bindWidth(null, 0.8), 
            "bindWidth should handle null pane gracefully");
    }

    @Test
    void bindHeight() {
        // Test binding height without scale factor (direct binding)
        Pane pane = new Pane();
        layoutManager.bindHeight(pane);
        
        // Verify binding exists
        assertTrue(pane.prefHeightProperty().isBound(), 
            "Pane height should be bound to scene height");
        
        // Note: We can't directly set scene height (it's private), but we can verify
        // the binding exists and will respond to scene size changes
        
        // Test with null pane (should not throw)
        assertDoesNotThrow(() -> layoutManager.bindHeight(null), 
            "bindHeight should handle null pane gracefully");
    }

    @Test
    void testBindHeight() {
        // Test binding height with scale factor
        Pane pane = new Pane();
        double scaleFactor = 0.75;
        layoutManager.bindHeight(pane, scaleFactor);
        
        // Verify binding exists
        assertTrue(pane.prefHeightProperty().isBound(), 
            "Pane height should be bound to scene height with scale factor");
        
        // Test with different scale factors
        Pane pane2 = new Pane();
        layoutManager.bindHeight(pane2, 0.6);
        assertTrue(pane2.prefHeightProperty().isBound(), 
            "Should bind with 0.6 scale factor");
        
        Pane pane3 = new Pane();
        layoutManager.bindHeight(pane3, 1.0);
        assertTrue(pane3.prefHeightProperty().isBound(), 
            "Should bind with 1.0 scale factor");
        
        // Test with null scale factor (should bind directly)
        Pane pane4 = new Pane();
        layoutManager.bindHeight(pane4, null);
        assertTrue(pane4.prefHeightProperty().isBound(), 
            "Should bind directly when scale factor is null");
        
        // Test with null pane (should not throw)
        assertDoesNotThrow(() -> layoutManager.bindHeight(null, 0.75), 
            "bindHeight should handle null pane gracefully");
    }

    @Test
    void bindSize() {
        // Test binding both width and height without scale factors
        Pane pane = new Pane();
        layoutManager.bindSize(pane);
        
        // Verify both bindings exist
        assertTrue(pane.prefWidthProperty().isBound(), 
            "Pane width should be bound");
        assertTrue(pane.prefHeightProperty().isBound(), 
            "Pane height should be bound");
        
        // Test with null pane (should not throw)
        assertDoesNotThrow(() -> layoutManager.bindSize(null), 
            "bindSize should handle null pane gracefully");
    }

    @Test
    void testBindSize() {
        // Test binding both width and height with scale factors
        Pane pane = new Pane();
        Double widthScale = 0.8;
        Double heightScale = 0.9;
        layoutManager.bindSize(pane, widthScale, heightScale);
        
        // Verify both bindings exist
        assertTrue(pane.prefWidthProperty().isBound(), 
            "Pane width should be bound with scale factor");
        assertTrue(pane.prefHeightProperty().isBound(), 
            "Pane height should be bound with scale factor");
        
        // Test with different scale factors
        Pane pane2 = new Pane();
        layoutManager.bindSize(pane2, 0.5, 0.5);
        assertTrue(pane2.prefWidthProperty().isBound(), 
            "Should bind width with 0.5 scale");
        assertTrue(pane2.prefHeightProperty().isBound(), 
            "Should bind height with 0.5 scale");
        
        // Test with null scale factors (should bind directly)
        Pane pane3 = new Pane();
        layoutManager.bindSize(pane3, null, null);
        assertTrue(pane3.prefWidthProperty().isBound(), 
            "Should bind width directly when scale is null");
        assertTrue(pane3.prefHeightProperty().isBound(), 
            "Should bind height directly when scale is null");
        
        // Test with mixed null/not-null scale factors
        Pane pane4 = new Pane();
        layoutManager.bindSize(pane4, 0.8, null);
        assertTrue(pane4.prefWidthProperty().isBound(), 
            "Should bind width with scale factor");
        assertTrue(pane4.prefHeightProperty().isBound(), 
            "Should bind height directly");
        
        Pane pane5 = new Pane();
        layoutManager.bindSize(pane5, null, 0.9);
        assertTrue(pane5.prefWidthProperty().isBound(), 
            "Should bind width directly");
        assertTrue(pane5.prefHeightProperty().isBound(), 
            "Should bind height with scale factor");
        
        // Test with null pane (should not throw)
        assertDoesNotThrow(() -> layoutManager.bindSize(null, 0.8, 0.9), 
            "bindSize should handle null pane gracefully");
    }
}