package com.soft.nikola.softprojekat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by DJ David on 9/4/2016.
 */
public class Motion implements Serializable{

    String motionName;
    ArrayList<Point> points;
    double cena;


    public Motion(String motionName, ArrayList<Point> points, double cena) {
        this.motionName = motionName;
        this.points = points;
        this.cena = cena;
    }


    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public String getMotionName() {
        return motionName;
    }


    public void setMotionName(String motionName) {
        this.motionName = motionName;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }
}
