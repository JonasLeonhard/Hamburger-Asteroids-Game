package com.a420.methlab.badtrip;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

/** @brief Diese Klasse bildet die Spielobjekte ab

Modelliert bewegliche Spielobjekte wie Asteroiden und Raumschiffe.
Speichert Position, Richtung und Geschwindigkeit der Objekte.

Enthält Methoden zur Kollisionserkennung.

 */
public class Movable {

    protected float positionx, positiony, speed, orientation;
    protected float dX, dY;
    protected Random random;

    public int size , randompick;
    public boolean isAlive;
    public BoundingCircle boundingCircle;


    Paint linePaint = new Paint();

    Model model;

    /** @brief Konstruktor

    Erzeugt ein Spielobjekt. Die übergebenen Variablen werden entsprechenden Attributen zugewiesen

     @param[in] xStart X-Koodinate der Spawn-Position
     @param[in] yStart Y-Koodinate der Spawn-Position
     @param[in] orientation Ursprüngliche Blickrichtung
     @param[in] speed Ursprüngliche Geschwindigkeit
     @param[in] model Referenz auf das Game-Model, in welchem das Spielobjekt existiert
     */
    Movable(float xStart, float yStart, float orientation, float speed, Model model) {
        this.positionx = xStart;
        this.positiony = yStart;
        this.orientation = orientation;
        this.speed = speed;

        dX = speed * (float) Math.cos(orientation);
        dY = speed * (float) Math.sin(orientation);

        isAlive = true;

        boundingCircle = new BoundingCircle(positionx, positiony, 50);

        //asigns the Object a Random Value between 0-360 for Bitmap rotation:
        random = new Random();
        randompick = random.nextInt(360);

/*
set orientation line indicator
 */
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLACK);

        this.model = model;
    }

    /** @brief Aktualiserungsmethode

    Rechnet den Objektzustand einen Frame weiter. Das Objekt wird bewegt.
     */
    public void update() {
        /*
        Objects go towards orientation
         */

        positionx += dX;
        positiony += dY;

        boundingCircle.setPosition(positionx, positiony);
    }

    /** @brief Getter-Methode für die X-Koordinate der Objektposition*/
    public void setSpeed(float newSpeed) {
        speed = newSpeed;
        dX = speed * (float) Math.cos(orientation);
        dY = speed * (float) Math.sin(orientation);
    }

    /** @brief Getter-Methode für die Y-Koordinate der Objektposition*/
    public void setOrientation(float newOrientation) {
        orientation = newOrientation;
        dX = speed * (float) Math.cos(orientation);
        dY = speed * (float) Math.sin(orientation);
    }

    public float getPosX() {
        return positionx;
    }

    public float getPosY() {
        return positiony;
    }

    public float getOrientation() { return orientation; }

    public float getSpeed() { return speed; }

    public void renderShape(Canvas c, Paint p)
    {
        /*
        render orientation line indicatior
         */
        c.drawLine(positionx, positiony, positionx + 10 * dX, positiony + 10 * dY, linePaint);
        c.drawCircle(positionx, positiony, boundingCircle.getRadius(), linePaint);


    }

    /** @brief Kollisionsabfrage

    Diese Methode wird aufgerufen, sobald ein Spielobjekt mit einem anderen kollidiert.
    Sie bietet die Möglichkeit, abgeleiteten Klassen individuelles Kollisionsverhalten zu verleihen.

     @param other Spielobjekt mit dem die Kollision aufgetreten ist.

     @callergraph
     @see Model::update()
     */
    public void collidesWith(Movable other) {
        //Overide in subclass
    }


    /** @brief sets orientation of object towards goalX, goalY

    Nach Aufruf dieser Methode blickt das Objekt auf den Zielpunkt.

     @param[in] goalX X-Koordinate des Zielpunktes
     @param[in] goalY Y-Koordinate des Zielpunktes

     @see MainActivity::onTouch()
     @see Asteroid::collidesWith()
     @callergraph
     */
    public void rotateTowards(float goalX, float goalY) {
        /*
        sets orientation of object towards goalX, goalY
        gets called in MaintActivity onTouch()
        gets called in Asteroid collidesWith()
         */
        float dX = goalX - positionx;
        float dY = goalY - positiony;

        Log.d("Spaceship.java", "posX: " + positionx + " posY: " + positiony + " goalX: " + goalX + " goalY: " + goalY);

        //this.setOrientation((float)Math.tan(dX / dY));  //set orientation of spaceship toward given point

        this.setOrientation((float)Math.atan2(dY, dX));
        //change this.orientation towards given goalX and goalY:

    }

    /** @brief Gibt die Ausrichtung eines Asteroiden zurück

    Used in Screen to set Asteroid Bitmap random rotation
    returns random Number between 0 - 360 once

     */
    public float randomOrientation()
    {
        return randompick;
    }

}
