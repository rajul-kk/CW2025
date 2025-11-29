package com.comp2042.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * Manages background music playback for the Tetris game.
 * Handles menu music and game mode-specific music with proper cleanup.
 * 
 * <p>This class provides a centralized way to manage music playback,
 * ensuring only one track plays at a time and resources are properly
 * released when switching between scenes or modes.
 * 
 * @author Rajul Kabir
 * @version 1.0
 */
public class MusicManager {
    
    private static MusicManager instance;
    private MediaPlayer currentPlayer;
    
    // Music file paths
    private static final String MENU_MUSIC = "11 - Ode.mp3";
    private static final String CLASSIC_MUSIC = "03. A-Type Music (Korobeiniki).mp3";
    private static final String PHANTOM_MUSIC = "02 - TECHNOTRIS.mp3";
    
    // Volume settings
    private static final double NORMAL_VOLUME = 0.5;
    private static final double PAUSED_VOLUME = 0.15; // Much quieter when paused
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private MusicManager() {
    }
    
    /**
     * Gets the singleton instance of MusicManager.
     * 
     * @return The MusicManager instance
     */
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    
    /**
     * Loads and plays a music file from the resources folder.
     * Stops any currently playing music before starting the new track.
     * 
     * @param musicFile The name of the music file in the resources folder
     * @param loop Whether the music should loop continuously
     */
    private void playMusic(String musicFile, boolean loop) {
        // Stop any currently playing music
        stopMusic();
        
        try {
            URL musicUrl = getClass().getClassLoader().getResource(musicFile);
            if (musicUrl == null) {
                System.err.println("Warning: Music file not found: " + musicFile);
                return;
            }
            
            Media media = new Media(musicUrl.toString());
            currentPlayer = new MediaPlayer(media);
            
            if (loop) {
                currentPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            
            // Set volume to normal volume
            currentPlayer.setVolume(NORMAL_VOLUME);
            
            currentPlayer.setOnError(() -> {
                System.err.println("Error playing music: " + musicFile);
                if (currentPlayer.getError() != null) {
                    System.err.println("Error details: " + currentPlayer.getError().getMessage());
                }
            });
            
            currentPlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to load music file: " + musicFile);
            e.printStackTrace();
        }
    }
    
    /**
     * Plays the main menu music.
     * The music will loop continuously.
     */
    public void playMenuMusic() {
        playMusic(MENU_MUSIC, true);
    }
    
    /**
     * Plays the classic game mode music.
     * The music will loop continuously.
     */
    public void playClassicMusic() {
        playMusic(CLASSIC_MUSIC, true);
    }
    
    /**
     * Plays the phantom game mode music.
     * The music will loop continuously.
     */
    public void playPhantomMusic() {
        playMusic(PHANTOM_MUSIC, true);
    }
    
    /**
     * Stops the currently playing music and releases resources.
     */
    public void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
    }
    
    /**
     * Pauses the currently playing music.
     * Can be resumed with {@link #resumeMusic()}.
     */
    public void pauseMusic() {
        if (currentPlayer != null) {
            currentPlayer.pause();
        }
    }
    
    /**
     * Resumes the paused music.
     */
    public void resumeMusic() {
        if (currentPlayer != null) {
            currentPlayer.play();
        }
    }
    
    /**
     * Sets the volume for the currently playing music.
     * 
     * @param volume Volume level between 0.0 (mute) and 1.0 (maximum)
     */
    public void setVolume(double volume) {
        if (currentPlayer != null) {
            currentPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
        }
    }
    
    /**
     * Checks if music is currently playing.
     * 
     * @return true if music is playing, false otherwise
     */
    public boolean isPlaying() {
        return currentPlayer != null && currentPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    
    /**
     * Lowers the volume for pause menu.
     * Music continues playing but at a reduced volume.
     */
    public void lowerVolumeForPause() {
        if (currentPlayer != null) {
            currentPlayer.setVolume(PAUSED_VOLUME);
        }
    }
    
    /**
     * Restores the normal volume after resuming from pause.
     */
    public void restoreVolumeFromPause() {
        if (currentPlayer != null) {
            currentPlayer.setVolume(NORMAL_VOLUME);
        }
    }
}

