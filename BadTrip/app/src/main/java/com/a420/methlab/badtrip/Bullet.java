package com.a420.methlab.badtrip;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * Bullet:
 */

/**
 * @brief
 * Bullet Klasse erweitert Movable
 * bleibt im Bildschirm
 * checkt collision mit allem außer einem Spaceship
 *
 */
public class Bullet extends Movable {

    public Bullet(float xStart, float yStart, float orientation, float speed, Model model)
    {
        super(xStart, yStart, orientation, speed, model);
        boundingCircle.setRadius(10);
    }

    @Override
    public void renderShape(Canvas c, Paint p)
    {
        super.renderShape(c, p);
        //c.drawCircle(positionx,positiony,10, p);
    }

    @Override
    public void update() {
        super.update();
        stayInsideCanvas();
    }

    /**
     * @brief collidesWith(Movable other)
     * check die Kollision einer Kugel mimt allem Außer einer Kugel und einem Spaceship
     * bei treffer wird die Kugel zerstört mit isAlive = false
     * @param other Spielobjekt mit dem die Kollision aufgetreten ist.
     */
    @Override
    public void collidesWith(Movable other) {
        /*
        gets called in update of Model
         */

        if(other.isAlive) {
            if(!(other instanceof Bullet) && !(other instanceof Spaceship))
                isAlive = false;
        }
    }

    /**
     * @brief stayInsidecanvas
     *
     * zerstöre Bullet außerhalb des Canvas
     */
    public void stayInsideCanvas()
    {
        int displaywidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int displayheight = Resources.getSystem().getDisplayMetrics().heightPixels;
        Log.d("Bullet.java", "stayInsideCavas: " + displaywidth + ";" + displayheight);
        if (positionx < 0) {
            this.isAlive = false;
        } else if (positionx > displaywidth) {
            this.isAlive = false;
        } else if (positiony < 0) {
            this.isAlive = false;
        }else if (positiony > displayheight) {
            this.isAlive = false;
        }
    }
}
