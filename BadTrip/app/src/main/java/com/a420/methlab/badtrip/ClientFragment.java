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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_ACCELEROMETER;

/**
 * Created by Jonas on 08.01.18.
 */

/**
 * @brief
 * Das ClientFragment wird von der MainActivity aufgerufen, sobald im MenuFragment die Methode
 * ((MainActivity)getActivity()).switchToClient(); im Button (Client) onClickListener aufgerufen wird
 * Das ClientFragment empfängt alle Daten der gezeichneten Objekte des ServerFragments und aktualisiert mit invalidate()
 * Controller des Bluetooths, siehe auch GameFragment.class
 */
public class ClientFragment extends Fragment implements SensorEventListener, View.OnTouchListener {

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

    CountDownTimer gameLooper;
    int tickCounter = 0; //used in loop for shooting

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        SM = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelerometer = SM.getDefaultSensor(TYPE_ACCELEROMETER);
        SM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);

        /*
        background theme music:
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

        /*
        Gameloop updates Model, then redraws it each frame
         */
        gameLooper = new CountDownTimer(Integer.MAX_VALUE, 1) {

            @Override
            public void onTick(long l) {
                while(btService.hasMessages()) {
                    String curMessage = btService.pollMessages();

                    if(curMessage.contains("beginObjects")) {
                        gameModel.clearMovables();
                        Log.d("ClientFragment", "Begin object transmission.");
                    } else if(curMessage.contains("endObjects")) {
                        Log.d("ClientFragment", "End object transmission.");
                        gameModel.update();
                        gameScreen.invalidate(); //redraw View
                    } else {
                        String[] cutMessage = curMessage.split(";");
                        for (String e : cutMessage)
                            Log.d("ClientFragment", e);

                        String classString = cutMessage[0].split(" ")[1];
                        try {
                            Class<?> c = Class.forName(classString);
                            float posX = Float.parseFloat(cutMessage[1]);
                            float posY = Float.parseFloat(cutMessage[2]);
                            float orientation = Float.parseFloat(cutMessage[3]);
                            float speed = Float.parseFloat(cutMessage[4]);

                            Constructor<?> cons = c.getConstructor(Float.class, Float.class, Float.class, Float.class, Model.class);
                            Object object = cons.newInstance(posX, posY, orientation, speed, gameModel);

                            gameModel.addMovable((Movable) object);
                        } catch (ClassNotFoundException e) {
                            Log.d("ClientFragmentExcep", "Class not found: " + e.getMessage());
                        } catch (NoSuchMethodException e) {
                            Log.d("ClientFragmentExcep", "Constructor not found for " + classString + ": " + e.getMessage());
                        } catch (ExceptionInInitializerError e) {
                            Log.d("ClientFragmentExcep", "Initialization failed: " + e.getMessage());
                        } catch (InvocationTargetException e) {
                            Log.d("ClientFragmentExcep", "Exception in Constructor: " + e.getMessage());
                        } catch (Exception e) {
                            Log.d("ClientFragmentExcep", "Unhandled Exception: " + e.getMessage());
                        }
                    }
                }
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