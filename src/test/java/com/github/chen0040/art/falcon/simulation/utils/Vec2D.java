package com.github.chen0040.art.falcon.simulation.utils;

import java.io.Serializable;

/**
 * Created by chen0469 on 9/30/2015 0030.
 */
public class Vec2D implements Cloneable, Serializable {
    private double X;
    private double Y;

    public double getX() { return X ; }
    public double getY() { return Y; }

    public void setX(double X) { this.X = X; }
    public void setY(double Y) { this.Y = Y; }

    public Vec2D(double X, double Y){
        this.X = X;
        this.Y = Y;
    }

    public int getDimension(){
        return 2;
    }

    public Vec2D(){
        X = 0;
        Y = 0;
    }

    public void decY(){
        Y--;
    }

    public void decX(){
        X--;
    }

    public void incX(){
        X++;
    }

    public void incY(){
        Y++;
    }

    public double distance(Vec2D b) {
        double dX = X - b.X;
        double dY = Y - b.Y;
        return Math.sqrt(dX * dX + dY * dY);
    }

    public boolean equals(int X, int Y){
        return this.X == X && this.Y == Y;
    }

    public Vec2D minus(Vec2D rhs){
        return new Vec2D(this.X - rhs.X, this.Y - rhs.Y);
    }

    public void copy(Vec2D rhs){
        X = rhs.X;
        Y = rhs.Y;
    }

    @Override
    public Object clone(){
        return new Vec2D(X, Y);
    }

    @Override
    public boolean equals(Object obj){
        if(obj != null && obj instanceof Vec2D){
            Vec2D rhs = (Vec2D)obj;
            return X == rhs.X && Y == rhs.Y;
        }
        return false;
    }

    @Override
    public String toString(){
        return "("+X+", "+Y+")";
    }
}
