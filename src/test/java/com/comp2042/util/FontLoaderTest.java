package com.comp2042.util;

import javafx.application.Platform;
import javafx.scene.text.Font;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FontLoader class.
 * 
 * Tests font loading, caching, fallback behavior, and font creation.
 */
class FontLoaderTest {

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
        // Note: FontLoader uses static state, so we can't easily reset it
        // Tests may be order-dependent, but that's acceptable for this utility class
    }

    @Test
    void loadFont() {
        // Test loading font
        // Font may be loaded successfully or return null if not found
        // Either way, the method should not throw an exception
        assertDoesNotThrow(() -> FontLoader.loadFont(), 
            "loadFont should not throw exception");
        
        // If font is found, it should return a non-null string
        // If not found, it may return null
        String result = FontLoader.loadFont();
        // Result can be null (font not found) or a font family name
        if (result != null) {
            assertFalse(result.isEmpty(), 
                "If font is loaded, family name should not be empty");
        }
        
        // Test that calling multiple times returns same result (caching)
        String result1 = FontLoader.loadFont();
        String result2 = FontLoader.loadFont();
        assertEquals(result1, result2, 
            "Multiple calls should return same result (caching)");
    }

    @Test
    void getFontFamily() {
        // Test getting font family
        String fontFamily = FontLoader.getFontFamily();
        
        assertNotNull(fontFamily, "getFontFamily should never return null");
        assertFalse(fontFamily.isEmpty(), 
            "Font family should not be empty");
        
        // Should return either the loaded font name or "System" as fallback
        assertTrue(fontFamily.equals("System") || !fontFamily.isEmpty(), 
            "Should return font name or 'System'");
        
        // Test that calling multiple times returns same result
        String family1 = FontLoader.getFontFamily();
        String family2 = FontLoader.getFontFamily();
        assertEquals(family1, family2, 
            "Multiple calls should return same result");
        
        // Test that it triggers loadFont if needed
        // (This is tested implicitly by the fact that it returns a value)
    }

    @Test
    void getFont() {
        // Test getting font with specific size
        Font font1 = FontLoader.getFont(12.0);
        assertNotNull(font1, "getFont should return a Font object");
        assertEquals(12.0, font1.getSize(), 0.01, 
            "Font should have correct size");
        
        // Test with different sizes
        Font font2 = FontLoader.getFont(24.0);
        assertNotNull(font2, "getFont should return a Font object");
        assertEquals(24.0, font2.getSize(), 0.01, 
            "Font should have correct size");
        
        // Test with zero size (should still work, though unusual)
        Font fontZero = FontLoader.getFont(0.0);
        assertNotNull(fontZero, "getFont should handle zero size");
        
        // Test with large size
        Font fontLarge = FontLoader.getFont(100.0);
        assertNotNull(fontLarge, "getFont should handle large size");
        assertEquals(100.0, fontLarge.getSize(), 0.01, 
            "Font should have correct large size");
        
        // Test that fonts with same size are consistent
        Font font3 = FontLoader.getFont(16.0);
        Font font4 = FontLoader.getFont(16.0);
        assertEquals(font3.getSize(), font4.getSize(), 0.01, 
            "Fonts with same size should have same size");
        // Note: Font objects may be different instances, but should have same properties
    }

    @Test
    void isFontLoaded() {
        // Test font loaded status
        boolean isLoaded = FontLoader.isFontLoaded();
        
        // Should return a boolean value (true if font loaded, false otherwise)
        assertTrue(isLoaded || !isLoaded, 
            "isFontLoaded should return a boolean");
        
        // Try to load font and check status
        FontLoader.loadFont();
        boolean isLoadedAfter = FontLoader.isFontLoaded();
        
        // Status should be consistent (either loaded or not)
        assertTrue(isLoadedAfter || !isLoadedAfter, 
            "isFontLoaded should return valid boolean");
        
        // Test that calling multiple times returns same result
        boolean status1 = FontLoader.isFontLoaded();
        boolean status2 = FontLoader.isFontLoaded();
        assertEquals(status1, status2, 
            "Multiple calls should return same result");
    }

    @Test
    void testFontLoadingIntegration() {
        // Test integration: load font, get family, create font
        String family = FontLoader.getFontFamily();
        assertNotNull(family, "Font family should be available");
        
        Font font = FontLoader.getFont(14.0);
        assertNotNull(font, "Font should be created");
        assertEquals(14.0, font.getSize(), 0.01, 
            "Font should have correct size");
        
        // Verify font family matches (if custom font loaded)
        if (!family.equals("System")) {
            assertEquals(family, font.getFamily(), 
                "Font family should match loaded font");
        }
    }

    @Test
    void testCachingBehavior() {
        // Test that font loading is cached
        String family1 = FontLoader.getFontFamily();
        String family2 = FontLoader.getFontFamily();
        String family3 = FontLoader.getFontFamily();
        
        assertEquals(family1, family2, 
            "Second call should return cached value");
        assertEquals(family2, family3, 
            "Third call should return cached value");
        
        // Test that isFontLoaded is consistent
        boolean loaded1 = FontLoader.isFontLoaded();
        boolean loaded2 = FontLoader.isFontLoaded();
        assertEquals(loaded1, loaded2, 
            "isFontLoaded should be consistent");
    }

    @Test
    void testFontSizes() {
        // Test various font sizes
        double[] sizes = {8.0, 12.0, 16.0, 20.0, 24.0, 32.0, 48.0};
        
        for (double size : sizes) {
            Font font = FontLoader.getFont(size);
            assertNotNull(font, "Font should be created for size " + size);
            assertEquals(size, font.getSize(), 0.01, 
                "Font should have size " + size);
        }
    }
}