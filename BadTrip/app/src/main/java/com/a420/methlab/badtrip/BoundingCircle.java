package com.a420.methlab.badtrip;

import android.util.Log;

/** @brief Kollisionsringe

Implementiert Kollsionsringe. Diese haben eine Spielfeldposition und einen Radius.
Mithilfe dieser Informationen können Ueberlagerungen von zwei Kreisen und somit
deren Kollisionen erkannt werden.

Jedes Object erhält einen BoundingCircle im Konstruktor von Movable
 */

public class BoundingCircle {
    private float px, py, radius;

    public BoundingCircle(float px, float py, float radius) {
        this.px = px;
        this.py = py;
        this.radius = radius;
    }

    /**
     * @brief setPosition
     * setzt die Position des BoundingCircles auf die Übergebene Position.
     * Wird immer auf die aktuelle Position des Objectes gesetzt
     * @param newX neue PositionX
     * @param newY neue PositionY
     */
    public void setPosition(float newX, float newY) {
        px = newX;
        py = newY;
    }

    /**
     * checkt ob zwei BoundingCircle überlappen
     * @param other
     * @return überlappen
     */
    public boolean overlaps(BoundingCircle other) {
        //Calculate distance between circles using the pythagorean theorem
        float dX = other.px - px;
        float dY = other.py - py;
        float distance = (float)Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

        //When Distance is less than the sum of the radii, two circles intersect
        return other.radius + this.radius > distance;
    }

    /**
     * @brief getRadius
     * @return radius des BoundingCircle
     */
    public float getRadius() {
        return radius;
    }

    /**
     * @brief setRadius
     * setzt den radius des Bounding circles neu
     * @param newRadius
     */
    public void setRadius(float newRadius) {
        radius = newRadius;
    }
}
