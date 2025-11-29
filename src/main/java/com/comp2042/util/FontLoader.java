package com.comp2042.util;

import javafx.scene.text.Font;

import java.net.URL;

/**
 * Utility class for loading and managing the custom digital font.
 * 
 * <p>This class handles loading the {@code digital.ttf} font file from resources
 * and provides methods to access the font family name and create Font objects
 * with specific sizes.
 * 
 * <p>The font is loaded lazily on first access and cached for subsequent use.
 * If the font cannot be loaded, the class falls back to the system font.
 * 
 * <p>The font loading process:
 * <ol>
 *   <li>First attempts to load via InputStream (more reliable for JAR files)</li>
 *   <li>Falls back to URL-based loading if InputStream fails</li>
 *   <li>Falls back to system font if both methods fail</li>
 * </ol>
 * 
 * <p>This class cannot be instantiated. All methods are static.
 * 
 * @author Rajul Kabir
 * @version 1.0
 */
public class FontLoader {
    
    private static String fontFamilyName = null;
    private static boolean fontLoaded = false;

    public static String loadFont() {
        if (fontFamilyName != null) {
            return fontFamilyName; // Already loaded
        }
        
        // Try loading font using InputStream (more reliable)
        try (java.io.InputStream fontStream = FontLoader.class.getClassLoader().getResourceAsStream("digital.ttf")) {
            if (fontStream != null) {
                Font loadedFont = Font.loadFont(fontStream, 12);
                if (loadedFont != null) {
                    fontFamilyName = loadedFont.getFamily();
                    fontLoaded = true;
                    System.out.println("Font loaded successfully. Family name: '" + fontFamilyName + "'");
                    // Verify the font is in the available families
                    boolean isAvailable = javafx.scene.text.Font.getFamilies().contains(fontFamilyName);
                    System.out.println("Font is available in system: " + isAvailable);
                    if (!isAvailable) {
                        System.err.println("Warning: Font loaded but not found in available families!");
                        System.err.println("Trying to find similar font names...");
                        for (String family : Font.getFamilies()) {
                            if (family.toLowerCase().contains("digital") || family.toLowerCase().contains("let's go digital")) {
                                System.out.println("Found similar font: " + family);
                            }
                        }
                    }
                    return fontFamilyName;
                } else {
                    System.err.println("Failed to load font - Font.loadFont returned null");
                }
            } else {
                System.err.println("Font resource stream is null - digital.ttf not found");
            }
        } catch (Exception e) {
            System.err.println("Exception loading font: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Fallback: Try URL method
        try {
            URL fontUrl = FontLoader.class.getClassLoader().getResource("digital.ttf");
            if (fontUrl != null) {
                Font loadedFont = Font.loadFont(fontUrl.toExternalForm(), 12);
                if (loadedFont != null) {
                    fontFamilyName = loadedFont.getFamily();
                    fontLoaded = true;
                    System.out.println("Font loaded successfully via URL. Family name: '" + fontFamilyName + "'");
                    return fontFamilyName;
                }
            }
        } catch (Exception e) {
            System.err.println("Exception loading font via URL: " + e.getMessage());
        }
        
        return null;
    }

    public static String getFontFamily() {
        if (fontFamilyName == null) {
            loadFont();
        }
        if (fontFamilyName != null) {
            return fontFamilyName;
        }
        // Try to find the font by checking available families
        for (String family : Font.getFamilies()) {
            if (family.toLowerCase().contains("digital") || family.toLowerCase().contains("let's go digital")) {
                fontFamilyName = family;
                System.out.println("Found font by name matching: " + family);
                return family;
            }
        }
        return "System"; // Fallback to system font
    }
    
    public static Font getFont(double size) {
        String family = getFontFamily();
        System.out.println("FontLoader.getFont(" + size + ") - Using family: '" + family + "'");
        if (family != null && !family.equals("System")) {
            Font font = Font.font(family, size);
            System.out.println("  - Created font: " + font);
            return font;
        }
        System.err.println("  - WARNING: Using default font (family was null or System)");
        return Font.font(size); // Return default font if custom font not available
    }
    

    public static boolean isFontLoaded() {
        return fontLoaded && fontFamilyName != null;
    }
}

