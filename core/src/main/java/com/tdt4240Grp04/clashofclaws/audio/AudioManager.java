package com.tdt4240Grp04.clashofclaws.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private static final String PREFS_NAME  = "ClashOfClawsSettings";
    private static final String KEY_SOUND   = "soundEnabled";
    private static final String KEY_SVOL    = "soundVolume";
    private static final String KEY_MUSIC   = "musicEnabled";
    private static final String KEY_MVOL    = "musicVolume";

    private static AudioManager instance;

    private Preferences prefs;
    private boolean soundEnabled;
    private float   soundVolume;
    private boolean musicEnabled;
    private float   musicVolume;

    private Music currentMusic;

    private AudioManager() {
        prefs        = Gdx.app.getPreferences(PREFS_NAME);
        soundEnabled = prefs.getBoolean(KEY_SOUND, true);
        soundVolume  = prefs.getFloat(KEY_SVOL, 1.0f);
        musicEnabled = prefs.getBoolean(KEY_MUSIC, true);
        musicVolume  = prefs.getFloat(KEY_MVOL, 0.8f);
    }

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    // --- Sound effects ---

    public void playSound(Sound sound) {
        if (sound != null && soundEnabled) sound.play(soundVolume);
    }

    public boolean isSoundEnabled() { return soundEnabled; }
    public float   getSoundVolume() { return soundVolume; }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        prefs.putBoolean(KEY_SOUND, enabled);
        prefs.flush();
    }

    public void setSoundVolume(float volume) {
        soundVolume = volume;
        prefs.putFloat(KEY_SVOL, volume);
        prefs.flush();
    }

    // --- Music ---

    public void playMusic(Music music) {
        if (currentMusic != null && currentMusic.isPlaying()) currentMusic.stop();
        currentMusic = music;
        if (music != null && musicEnabled) {
            music.setVolume(musicVolume);
            music.setLooping(true);
            music.play();
        }
    }

    public void stopMusic() {
        if (currentMusic != null) currentMusic.stop();
    }

    public boolean isMusicEnabled() { return musicEnabled; }
    public float   getMusicVolume() { return musicVolume; }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        prefs.putBoolean(KEY_MUSIC, enabled);
        prefs.flush();
        if (currentMusic != null) {
            if (enabled) { currentMusic.setVolume(musicVolume); currentMusic.play(); }
            else         { currentMusic.stop(); }
        }
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        prefs.putFloat(KEY_MVOL, volume);
        prefs.flush();
        if (currentMusic != null && musicEnabled) currentMusic.setVolume(volume);
    }
}
