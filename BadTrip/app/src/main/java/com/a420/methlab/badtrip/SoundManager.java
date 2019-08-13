package com.a420.methlab.badtrip;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;


/**
 * @brief
 * SoundManager Klasse handled Soundeffekte und Hintergrund Musik.
 *
 * Sie benutzt Soundpool für Soundeffekte und MediaPlayer für die Hintergrund Musik.
 * Mit SoundManager.initializeSoundmanager(getApplicationContext()); wird der SoundManager initialisiert.
 * Mit SoundManager.getInstance().playExplodeSound(); wird ein Sound gespielt
 */

public class SoundManager {

    private static SoundManager soundManager;
    private static SoundPool soundPool;

    int laser_Sound, explode_Sound, button_Sound;

    boolean loadlaser = false, loadexplode = false, loadbutton1 = false;

    private static MediaPlayer backPlayer, menuPlayer;

    public SoundManager() {
            /* 
            create Soundpool Object with  maximum Number of simultaneous stream '15',
            Stream Type = STREAM_MUSIC used by games, sample rate converter value '0' 

            recommended is using builder, but everything below Lollipop doesnt support builder.
             */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            soundPool = (new SoundPool.Builder()).setMaxStreams(15).build();
        } else {
            soundPool = new SoundPool(15, AudioManager.STREAM_MUSIC, 0);
        }


        //to check when loading is complete:
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundID, int status) {
                if (status == 0) {
                    switch (soundID) {
                        case 1:
                            loadlaser = true;
                            break;
                        case 2:
                            loadexplode = true;
                            break;

                        case 3:
                            loadbutton1 = true;
                    }
                }
            }
        });
    }

    /**
     * @brief loadfiles
     * läd die Soundeffecte in den Soundpool und die Hintergrund music in den MediaPlayer
     * @param context
     */
    public void loadfiles(Context context) {
        /* 
        Method loads inital sounds for ingame Soundeffecs into a Soundpool object     loads files into memory 
        */
        laser_Sound = soundPool.load(context, R.raw.laser, 0);
        explode_Sound = soundPool.load(context, R.raw.explode, 0);
        button_Sound = soundPool.load(context, R.raw.explode, 0);

        backPlayer = MediaPlayer.create(context, R.raw.backgroundost);
        backPlayer.setLooping(true);
        backPlayer.setVolume(0.7f, 0.7f);

        menuPlayer = MediaPlayer.create(context, R.raw.backgroundost);
        menuPlayer.setLooping(true);
        menuPlayer.setVolume(0.7f, 0.7f);

        // load other sounds here
    }

    /**
     * @brief
     * wird benutzt um den shoot() sound zu definieren.
     * Hat eine niedrigere Priorität beim spielen als andere Sounds, da der Schuss sound Konstant spielt
     */
    public void playLaserSound() {
        /*
        sound, volumeleft, volumeright, priority '0', repeats '0', playback rate '1.3': 

        priority '0'
         */
        if (loadlaser)
            soundPool.play(laser_Sound, 0.6F, 0.6F, 0, 0, 1.3F);
    }

    /**
     * @brief
     * Spielt Explode Sound per Soundpool.
     */
    public void playExplodeSound() {
        /*
        priority '1'
         */
        if (loadexplode)
            soundPool.play(explode_Sound, 1.0F, 1.0F, 1, 0, 2.0F);
    }

    /**
     * @Brief
     * spielt den Button sound beim Klicken eines Buttons per Soundpool
     */
    public void playButton1Sound()
    {
        if(loadbutton1)
            soundPool.play(button_Sound, 1.0F, 1.0F, 1, 0, 2.0F);
    }

    /**
     * @Brief
     * Startet den MediaPlayer mit der Backgroundtheme Musik.
     */
    public void playBackgroundMusic() {
        backPlayer.start();
    }

    /**
     * @Brief
     * Stoppt den MediaPlayer mit der Hintergrund Musik.
     */
    public void stopBackgroundMusic() {
        backPlayer.stop();
    }

    /**
     * @Biref
     * Startet den Menü Musik mit dem MediaPlayer.
     */
    public void playMenuMusic(){ menuPlayer.start();}

    /**
     * @Brief
     * stoppt die Menü Musik mit dem MediaPlayer
     */
    public void stopMenuMusic() {menuPlayer.stop();}

    /**
     * @Brief
     * initialisiert den Soundmanager und lädt alle Soundfiles
     * @param context
     */
    public static void initializeSoundmanager(Context context) {
        /*
        gets called in Main Acitity to initialize a instance of Soundmanager,
        and preload soundfiles to be called later:
         */
        SoundManager soundManager = getInstance();
        soundManager.loadfiles(context);
    }

    /**
     * @Brief
     * return die derzeitige Instanz des Soundmanagers
     * @return
     */
    public static synchronized SoundManager getInstance() {
        /*
        returns soundmanager to play files in classes
        gets called in spaceship.shoot(), spaceship.x
         */
        if (soundManager == null) {
            soundManager = new SoundManager();
        }
        return soundManager;
    }


}
