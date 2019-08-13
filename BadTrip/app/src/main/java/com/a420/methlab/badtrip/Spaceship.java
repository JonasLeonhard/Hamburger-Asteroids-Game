package com.a420.methlab.badtrip;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/** @brief Diese Klasse bildet die Raumschiffe ab

Modelliert die Raumschiffe als Spielobjekte.
Zusätzlich hat ein Raumschiff noch Lebenspunkte und eine Grafik zum Zeichnen.
Kantenerkennung und eine eigene Kollisionsabfrage sind hinzugekommen.
 */


public class Spaceship extends Movable {

    int hitpoints = 3;
    private Bitmap shipmodel;

    /** @brief Von Movable erweiterter Konstruktor

     @see Movable::Movable()
     */
    public Spaceship(float xStart, float yStart, float orientation, float speed, Model model)
    {
        super(xStart, yStart, orientation, speed, model);
        boundingCircle.setRadius(60);
        /*
        Spaceship grafik einbinden:
         */

        //shipmodel = BitmapFactory.decodeResource(getResources(), R.drawable.fhfl_map);
    }

    @Override
    public void renderShape(Canvas c, Paint p)
    {
        super.renderShape(c, p);
        //c.drawCircle(positionx ,positiony,10, p);
        //Log.d("Spaceship.java", "renderShape: " + positiony +";" + positiony);
    }

    /** @brief Von Movable erweiterte Aktualisierungsmethode

    Verwendet altes Verhalten weiter. Erkennt jetzt auch Bildschirmränder
     */
    @Override
    public void update() {
        super.update();
        stayInsideCanvas();
    }

    /** @brief Sorgt dafür, dass Raumschiffe den Bildschirmrand respektieren

    Raumschiffe, die dabei sind, den Bildschirm zu verlassen, werden an dessen Kanten festgehalten.
     */
    public void stayInsideCanvas()
    {
        int displaywidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int displayheight = Resources.getSystem().getDisplayMetrics().heightPixels;
        Log.d("Movable.java", "stayInsideCavas: " + displaywidth + ";" + displayheight);
        if (positionx < 0) {
            positionx = displaywidth;
        }

        if (positionx > displaywidth) {
            positionx = 0;
        }

        if (positiony < 0) {
            positiony = displayheight;
        }

        if (positiony > displayheight) {
            positiony = 0;
        }
    }

    /** @brief Lässt Raumschiffe Raketen abfeuern

    Erzeugt ein neues Spielobjekt und fügt es dem selben Game-Model hinzu, indem sich das schießende Spielobjekt befindet.
    Raketen starten von der Objektposition und übernehmen dessen Blickrichtung. Die Geschwindigkeit wird relativ zur Objektgeschwindigkeit erhöht.
     */
    public void shoot()
    {

        model.addMovable(new Bullet(positionx, positiony, orientation, speed + 20, model));
        SoundManager.getInstance().playLaserSound(); //shoot sound
    }

    /** @brief Von Movable erweiterte Kollisionsabfrage

    Kollisionen mit Asteroiden senken die Lebenspunkte.
    Sind die Lebenspunkte bei 0 angelangt, stirbt das Raumschiff.
     */
    @Override
    public void collidesWith(Movable other) {
        /*
        gets called in update() of Model
        checks collisio of ship with asteroid
        removes asteroid  - removes 1 hitpoint upon hit
         */

        if(other.isAlive) {
            if(!(other instanceof Bullet) && !(other instanceof Spaceship))
            {
                SoundManager.getInstance().playExplodeSound(); //plays crack apart sound
                hitpoints--;
            }
            // isAlive = false;
        }

        if(hitpoints<=0)
        {
            this.isAlive = false;
            SoundManager.getInstance().playExplodeSound(); //spaceship dies sound
            SoundManager.getInstance().stopBackgroundMusic();

        }
    }



}
