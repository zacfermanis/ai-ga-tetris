package com.fermanis.aitetris;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.fermanis.aitetris.ProjectConstants.getResStream;
import static com.fermanis.aitetris.ProjectConstants.getResURL;

/*This class loads, plays, and manages sound effects and
 * music for Tetris4j. The sound URL's are hardcoded
 * into this class and is loaded statically at runtime.*/
public class SoundManager {

    /*This represents the list of sounds available.*/
    public static enum Sounds {
        // sound/tetris.midi
        TETRIS_THEME,

        // sound/soundfall.wav
        FALL,

        // sound/soundrotate.wav
        ROTATE,

        // sound/soundclear.wav
        CLEAR,

        // sound/soundtetris.wav
        TETRIS,

        // sound/sounddie.wav
        DIE;
    }

    // do we even play music at all?
    public static final boolean PLAY_MUSIC = true;

    private Sequencer midiseq; //Midi sequencer, plays the music.

    private InputStream tetheme; //Tetris theme (midi-inputstream).

    //The collection of
    //sound effects used.
    private AudioClip sx1, sx2, sx3, sx4, sx5;

    private static SoundManager soundmanager = null;
    //Reference of the SoundManager.

    /*Since this class locks certain system resources, it's
     * best to only have one instance of this class. If an
     * instance of SoundManager already exists, this replaces
     * that with a new instance.*/
    public static SoundManager getSoundManager() {
        soundmanager = new SoundManager();
        return soundmanager;
    }

    //private initializer method.
    private SoundManager() {
        try {
            Resource tetrisTheme = new ClassPathResource("sound/tetris.midi");
            tetheme = tetrisTheme.getInputStream();
            Resource soundFall = new ClassPathResource("sound/soundfall.wav");
            sx1 = Applet.newAudioClip(soundFall.getURL());
            Resource soundRotate = new ClassPathResource("sound/soundrotate.wav");
            sx2 = Applet.newAudioClip(soundRotate.getURL());
            Resource soundTetris = new ClassPathResource("sound/soundtetris.wav");
            sx3 = Applet.newAudioClip(soundTetris.getURL());
            Resource soundClear = new ClassPathResource("sound/soundclear.wav");
            sx4 = Applet.newAudioClip(soundClear.getURL());
            Resource soundDie = new ClassPathResource("sound/sounddie.wav");
            sx5 = Applet.newAudioClip(soundDie.getURL());
        } catch (Exception e) {
            throw new RuntimeException("Cannot load sound.");
        }
    }

    /*Plays a sound. Sounds should be short because once this
     * is called again, the previous sound teminates and
     * the new sound starts.*/
    public synchronized void sfx(Sounds s) {
        if (!PLAY_MUSIC) return;

        switch (s) {
            case FALL:
                sx1.play();
                break;
            case ROTATE:
                sx2.play();
                break;
            case TETRIS:
                sx3.play();
                break;
            case CLEAR:
                sx4.play();
                break;
            case DIE:
                sx5.play();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    /*Plays a music track. Currently the only track
     * is the default MIDI track (theme song).*/
    public synchronized void music(Sounds s) {
        if (!PLAY_MUSIC) return;

        if (s == null) {
            midiseq.close();
            return;
        } else if (s == Sounds.TETRIS_THEME) {

            try {
                midiseq = MidiSystem.getSequencer();
                midiseq = MidiSystem.getSequencer();
                midiseq.open();
                //Sometimes throws MidiUnavailableException.
                midiseq.setSequence(MidiSystem.getSequence(tetheme));
                midiseq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                midiseq.start();
            } catch (Exception e) {
                throw new RuntimeException("Cannot play MIDI.");
            }

        } else throw new IllegalArgumentException();
    }

}