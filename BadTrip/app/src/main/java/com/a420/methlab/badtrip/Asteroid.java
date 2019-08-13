package com.a420.methlab.badtrip;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Space;

/** @brief Diese Klasse bildet die Asteroiden ab

Modelliert Asteroiden als Spielobjekte.
 */

public class Asteroid extends Movable {

    /** @brief Von Movable erweiterter Konstruktor

     @see Movable::Movable()
     */
    public Asteroid(float xStart, float yStart, float orientation, float speed, Model model)
    {
        super(xStart,yStart, orientation, speed, model);
        super.size  = 3;
    }

    /** @brief Konstruktor mit variabler Asteroidengröße

     @param size Die neue Größe des Asteroiden
     @param boundingradius Der neue Radius des Kollisionsrings

     @see Movable::Movable()
     */
    public Asteroid(float xStart, float yStart, float orientation, float speed, Model model, int size, float boundingradius)
    {
        super(xStart,yStart, orientation, speed, model);
        super.size = size;
        boundingCircle.setRadius(boundingradius);
    }



    @Override
    public void renderShape(Canvas c, Paint p)
    {
        super.renderShape(c, p);
        //c.drawCircle(positionx,positiony,size * 10, p);
    }

    /** @brief Von Movable erweiterte Aktualisierungsmethode

    Verwendet altes Verhalten weiter. Erkennt jetzt auch Bildschirmränder
     */
    @Override
    public void update() {
        super.update();
        stayInsideCanvas();
    }

    /** @brief Sorgt dafür, dass Asteroiden am Bildschirmrand zerstört werden
     */
    public void stayInsideCanvas()
    {
        int displaywidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int displayheight = Resources.getSystem().getDisplayMetrics().heightPixels;
        Log.d("Asteroid.java", "stayInsideCavas: " + displaywidth + ";" + displayheight);
        if (positionx < -50)
        {
            this.isAlive = false;
        } else if (positionx > displaywidth + 50)
        {
            this.isAlive = false;
        } else if (positiony < -50)
        {
            this.isAlive = false;
        }else if (positiony > displayheight + 50)
        {
            this.isAlive = false;
        }
    }

    /** @brief Von Movable erweiterte Kollisionsabfrage

    Kollisionen mit Kugeln zerstören einen Asteroiden und erzeugen zwei neue, kleinere Asteroiden.
     */
    @Override
    public void collidesWith(Movable other) {
        /*
        gets called in update of Model
         */

        /*
        collides with bullet:
         */
        if(other.isAlive)
        {
            if (other instanceof Bullet)
            {
                SoundManager.getInstance().playExplodeSound(); //plays crack apart sound
                isAlive = false;
                model.addPoints(300 / size);
                if (size > 1)
                {
                    model.addMovable(new Asteroid(positionx, positiony, (float) (Math.random() * Math.PI * 2), speed, model, size - 1, boundingCircle.getRadius()/2));
                    model.addMovable(new Asteroid(positionx, positiony, (float) (Math.random() * Math.PI * 2), speed, model, size - 1, boundingCircle.getRadius()/2));

                }
            }
        }

        /*
        collides with spaceship:
         */

        if(other instanceof Spaceship)
        {
            isAlive = false;
        }


    }
}
