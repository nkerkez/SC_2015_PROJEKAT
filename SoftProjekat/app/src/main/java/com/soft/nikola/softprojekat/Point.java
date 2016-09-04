package com.soft.nikola.softprojekat;

import java.io.Serializable;

/**
 * Created by DJ David on 9/4/2016.
 */
public class Point implements Serializable{

    private double x ;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
