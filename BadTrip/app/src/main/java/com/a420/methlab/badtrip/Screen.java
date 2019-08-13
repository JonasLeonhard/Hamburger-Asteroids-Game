package com.a420.methlab.badtrip;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

/**
 * @brief
 * Screen Class
 * View des MVCs, für zeichnen von Grafiken zuständig.
 * Er erzeugt die Bitmaps bitShip, bitAsteroidL, bitAsteroidM, bitAsteroidS, bitBullet, bitBackground für alle Objecte und
 * lädt diese.
 * Erhält die Position der Objecte aus der Model mit ArrayList<Movable> getMovables()
}
 */

public class Screen extends View {
    Model drawModel;
    Paint paint;

    //for translation of bitmap:
    Matrix matrix;

    //drawable Objects:
    Bitmap bitShip, bitAsteroidL, bitAsteroidM, bitAsteroidS, bitBullet, bitBackground;

    public Screen(Context context) {
        super(context);

        //
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setTextSize(48);
        paint.setTextAlign(Paint.Align.RIGHT);

        /*
        create Matrix for tranformations:
         */
        matrix = new Matrix();

        /*
        get Bitmaps from Resources
        Order:
        1. SpaceShip
        2. Asteroid size 3
        3. Asteroid size 2
        4. Asteroid size 1
        5. Bullet

         */
        bitShip = BitmapFactory.decodeResource(getResources(), R.drawable.spaceship);
        bitAsteroidL = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
        bitAsteroidM = BitmapFactory.decodeResource(getResources(), R.drawable.asteroidm);
        bitAsteroidS = BitmapFactory.decodeResource(getResources(), R.drawable.asteroids);
        bitBullet = BitmapFactory.decodeResource(getResources(), R.drawable.laser);
        //bitBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        /*
        RESIZING BITMAPS:
        Rezize current Bitmaps by currentX / scaledown | currentY /scaledown:
         */
        bitShip = resizedBitmap( bitShip, bitShip.getWidth()/12 , bitShip.getHeight()/12 );
        bitAsteroidL = resizedBitmap( bitAsteroidL, bitAsteroidL.getWidth()/15, bitAsteroidL.getHeight()/15 );
        bitAsteroidM = resizedBitmap( bitAsteroidM, bitAsteroidM.getWidth()/20, bitAsteroidM.getHeight()/20 );
        bitAsteroidS = resizedBitmap( bitAsteroidS, bitAsteroidS.getWidth()/25, bitAsteroidS.getHeight()/25 );
        bitBullet = resizedBitmap( bitBullet, bitBullet.getWidth()/30, bitBullet.getHeight()/30 );
    }

    public void setModel(Model newModel) {
        drawModel = newModel;
    }

    /*
    gets called in gameloop with invalidate() each frame
     */

    /**
     * @brief onDraw(Canvas c)
     * zeichnet die geladenen Bitmaps auf den übergebenen Canvas
     * Mittels einer Matrix translation, rotation und scale wird die Bitmap
     * auf die Orientation der movables Objecte gebracht.
     * @param c
     */
    public void onDraw(Canvas c) {
        //draw Background:
        matrix.reset();
        //c.drawBitmap(bitBackground, matrix,null);

        //draw each Object of Model object list:
        for (Movable m : drawModel.getMovables()) {


            m.renderShape(c, paint);




            //draw Spaceship:
            if(m instanceof Spaceship && m.isAlive)
            {
                matrix.reset ();

                /*
                keeps the bitmap synced with object.orientation
                translates the bitmaps middle point to movable Objects X and Y.
                 */
                matrix.postRotate( 90 + (float)Math.toDegrees(m.orientation),bitShip.getWidth()/2 ,bitShip.getHeight()/2 );
                matrix.postTranslate(m.getPosX() -bitShip.getWidth() /2, m.getPosY() -bitShip.getHeight()/2 );

                c.drawBitmap(bitShip, matrix, null);
            }

            //draw Asteroid:
            if(m instanceof Asteroid && m.isAlive)
            {
                matrix.reset();

                if(m.size == 3)
                {
                    //rotated in randomOrientation assigned at Object Creation:
                    matrix.postRotate(m.randomOrientation(), bitAsteroidL.getWidth() / 2, bitAsteroidL.getHeight() / 2);
                    matrix.postTranslate(m.getPosX() - bitAsteroidL.getWidth() / 2, m.getPosY() - bitAsteroidL.getHeight() / 2);

                    c.drawBitmap(bitAsteroidL, matrix, null);
                }

                if(m.size == 2)
                {
                    matrix.postRotate(m.randomOrientation(), bitAsteroidM.getWidth() / 2, bitAsteroidM.getHeight() / 2);
                    matrix.postTranslate(m.getPosX() - bitAsteroidM.getWidth() / 2, m.getPosY() - bitAsteroidM.getHeight() / 2);

                    c.drawBitmap(bitAsteroidM, matrix, null);
                }

                if(m.size == 1)
                {
                    matrix.postRotate(m.randomOrientation(), bitAsteroidS.getWidth() / 2, bitAsteroidS.getHeight() / 2);
                    matrix.postTranslate(m.getPosX() - bitAsteroidS.getWidth() / 2, m.getPosY() - bitAsteroidS.getHeight() / 2);

                    c.drawBitmap(bitAsteroidS, matrix, null);
                }

            }

            //draw Bullet:
            if(m instanceof  Bullet && m.isAlive)
            {
                matrix.reset();

                matrix.postRotate( (float)Math.toDegrees(m.orientation), bitBullet.getWidth()/2, bitBullet.getHeight()/2 );
                matrix.postTranslate(m.getPosX() -bitBullet.getWidth()/2, m.getPosY() -bitBullet.getHeight()/2 );

                c.drawBitmap(bitBullet, matrix, null);
            }

        }

        c.drawText(String.valueOf(drawModel.getScore()), 1070, 48, paint);
    }

    /**
     * @brief
     * skaliert übergebene Bitmaps auf newWidth, newHeight mittels Matrix transformation
     * @param bitm Bitmap
     * @param newWidth neue Breite
     * @param newHeight neue Höhe
     * @return resizedBitmap
     */
    public Bitmap resizedBitmap(Bitmap bitm, int newWidth, int newHeight) {
        /*
        resizes a given Bitmap to the given resolution.
         */
        int width = bitm.getWidth();
        int height = bitm.getHeight();

        float scaleX = ((float) newWidth) / width;
        float scaleY = ((float) newHeight) / height;

        /*
        Reset created Matrix for scaling:
         */
        matrix.reset();

        /*
        rezize
         */
        matrix.postScale(scaleX, scaleY);

        /*
        override old Bitmap with scaled version:
         */
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitm, 0, 0, width, height, matrix, false);
        bitm.recycle();

        return resizedBitmap;
    }


}
