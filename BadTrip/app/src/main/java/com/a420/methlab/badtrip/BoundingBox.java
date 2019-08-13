package com.a420.methlab.badtrip;

/**
 * BoundingBox for Movable Objects
 */

public class BoundingBox
{
    protected float width, height;
    protected float px, py;

    BoundingBox(float px, float py, float width, float height)
    {
        this.px = px;
        this.py = py;
        this.width = width;
        this.height = height;
    }
}
