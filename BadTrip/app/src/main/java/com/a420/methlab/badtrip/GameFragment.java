package com.a420.methlab.badtrip;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_ACCELEROMETER;

/** @brief Schnittstelle zwischen Handy, Nutzer und Spieler

 Das "C" in "MVC-Pattern". Verwaltet Model und View(Screen) sowie Input über
 Accelerometer und Touchscreen.

 Der Gameloop wird über einen Timer Implementiert, in dessen Tickmethode dann
 erst das Modell und danach der Bildschirm aktualisiert werden.
 */

public class GameFragment extends Fragment implements SensorEventListener, View.OnTouchListener {

    //Used for motion sensing
    SensorManager SM;
    Sensor accelerometer;

    private BluetoothConnectionService btService = null;

    private Model gameModel;
    private Screen gameScreen;

    /*
    Ingame Objects:
     */
    private Spaceship ship;
    private Asteroid asteroid;

    CountDownTimer gameLooper;
    int tickCounter = 0; //used in loop for shooting

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SM = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelerometer = SM.getDefaultSensor(TYPE_ACCELEROMETER);
        SM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        /*
        backgroudn theme music:
         */
        SoundManager.getInstance().playBackgroundMusic();

        /*
        create model
         */
        gameModel = new Model();

        /*
        create View to be drawn
         */
        gameScreen = new Screen(getActivity().getApplicationContext());
        gameScreen.setModel(gameModel);
        gameScreen.setOnTouchListener(this);

        /*
        Objects to be Displayed, add to model
         */
        ship = new Spaceship(400, 500, 0, 5, gameModel);
        asteroid = new Asteroid(800, 100, (float) Math.PI, 10, gameModel);

        gameModel.addMovable(ship);
        gameModel.addMovable(asteroid);

        /*
        Gameloop updates Model, then redraws it each frame
         */
        final MainActivity act = (MainActivity) getActivity();
        gameLooper = new CountDownTimer(Integer.MAX_VALUE, 1) {

            @Override
            public void onTick(long l) {
                tickCounter++;
                if (tickCounter % 10 == 0 && ship.isAlive)
                {
                    Log.d("onTick","tickcounter: " +tickCounter);
                    ship.shoot();
                    //change intervall later!!
                }


                gameModel.update();      //update Positions of each object
                gameScreen.invalidate(); //redraw View

                if(ship.isAlive == false)
                    act.switchToGameOver(gameModel.getScore());
            }

            @Override
            public void onFinish() {
                //Sollte nie passieren
            }
        };

        /*
        Start Gameloop:
         */
        gameLooper.start();

        return gameScreen;
    }

    public void setBtService(BluetoothConnectionService btService) {
        this.btService = btService;
    }

    /**
     * @brief SensorChanged
     * Sensor Change wird vom SensorEventListener aufgerufen wenn sich das Handy neigt.
     *steuert die Geschwindigkeit des Raumschiffes mit Spaceship.setSpeed()
     * @param sensorEvent
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        /*
        if the accaleration sensor changes, the speed of the ship changes accordingly:
         */
        Log.d("MainActivity.java", "XAccel: " + sensorEvent.values[0] + " YAccel: " + sensorEvent.values[1] + " ZAccel: " + sensorEvent.values[2]);
        ship.setSpeed(Math.abs(10 - sensorEvent.values[1]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //not used
    }

    /**
     * @brief onTouch
     * Tochlistener setzt Orientation des Spaceships zu den X und Y Werten der Touchposition
     * benutzt dazu Spaceship.rotateTowards()
     * @param view
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        /*
        gets called by the on touch listener
        the player ship rotates towards to point touched on the screen
         */
        Log.d("MainActivity.java", "X: " + motionEvent.getX() + " Y: " + motionEvent.getY());
        Log.d("GameFragment", "hasMessages: " + btService.hasMessages());
        while(btService.hasMessages()) {
            Log.d("GameFragment", btService.pollMessages());
        }

        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                ship.rotateTowards(motionEvent.getX(), motionEvent.getY());
        }

        return true;
    }
}
