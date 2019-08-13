package com.a420.methlab.badtrip;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;

/** @brief Diese Klasse bildet die Spielmechaniken ab

Sie ist das "M" in "MVC-Pattern". In dieser Klasse werden die im Spiel befindlichen
Objekte verwaltet.

AddList ist cool, weil man so Objekte hinzufügen kann, ohne den Frameaktuellen
Spielzustand zu verändern. Ist eine Art Puffer, Kondensator oder Staudamm
 */

public class Model {
    private ArrayList<Movable> movables;

    private ArrayList<Movable> addList;

    float nextAsteroidIn;
    int score;

    /** @brief Ein Konstruktor
     */
    public Model() {
        movables = new ArrayList<Movable>();
        addList = new ArrayList<Movable>();

        nextAsteroidIn = 1;
        score = 0;
    }

    /** @brief
     Teil des Gameloops, der den Spielzustand Aktualisierungsmethode

    Aktualisiert Spielobjekte, spawnt Asteroiden, ermittelt Kollisionen
     @see Movable::update()
     Prüft, ob Objekte kollidieren

    Vergleicht die Kollisionskörper zweier Objekte. Wenn sie kollidieren, wird
    jedes der beiden Objekte benachrichtigt und erhält eine Referenz auf den
    Kollisionspartner. @see Movable::collidesWith()
    Spawns asteroids that fly into the screen randomly:

     */
    public void update() {

        /*
        spawns asteroids that fly into the screen randomly:
         */

        //spawn Asteroid ever 1/0.02 update() calls
        nextAsteroidIn -= 0.02;
        if(nextAsteroidIn <= 0) {
            float width = Resources.getSystem().getDisplayMetrics().widthPixels;
            float height = Resources.getSystem().getDisplayMetrics().heightPixels;
            float newX = (float)Math.random() * width;
            float newY = (float)Math.random() * height;
            if(Math.abs(newX - width) / width < Math.abs(newY - height) / height) {
                newX = 0;
            } else
                newY = 0;

            //give asteroid a random orientation and speed:
            float newOrientation = (float)(Math.random() * 360 );
            float newSpeed = (float)(Math.random() * 2.5) + 2.5f;

            //add random asteroid to addlist to be drawn:
            addMovable(new Asteroid(newX, newY, (float)Math.toDegrees(newOrientation), newSpeed, this));

            nextAsteroidIn = 1;
        }

        /*
        Detect collisions of each object: calls collidesWith()
         */
        for (Movable m1 : movables) {
            for (Movable m2 : movables) {
                if (m1 != m2) {
                    if (m1.boundingCircle.overlaps(m2.boundingCircle)) {
                        m1.collidesWith(m2);
                        //Log.d("Model.java", "Got a collision!");
                    }
                }
            }
        }

        /*
        Update Game Objects
        */
        for (Movable m : movables) {
            m.update();
        }

        /*
        Remove Object if not alive
*/
        for(int i = movables.size()-1; i >= 0; i--)
        {
            //Log.d("Model.java", "Check object " + i + ": " + movables.get(i).isAlive);
            if(!movables.get(i).isAlive)
            {
                movables.remove(i);
                Log.d("Model.java", "update: remove object " );
            }
        }
        //Log.d("Model.java", "update: isalive.remove " +movables.size());


        //Add objects from addlist
        for(Movable nM: addList)
            movables.add(nM);
        addList.clear();
    }

    /** @callergraph
     */
    public void addMovable(Movable m) {
        addList.add(m);
    }

    /** @callergraph
     */
    public ArrayList<Movable> getMovables() {
        return movables;
    }

    /** @callergraph
     */
    public void clearMovables() { movables.clear(); }

    /** @callergraph
     */
    public void addPoints(int amount) {
        score += amount;
    }

    /** @callergraph
     */
    public int getScore() {
        return score;
    }
}
