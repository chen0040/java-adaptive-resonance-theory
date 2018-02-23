package com.github.chen0040.art.falcon.simulation.utils;

import java.io.Serializable;

/**
 * Created by chen0469 on 9/30/2015 0030.
 */
public class Vec2I implements Cloneable, Serializable {
    private int X;
    private int Y;

    public int getX() { return X ; }
    public int getY() { return Y; }

    public void setX(int X) { this.X = X; }
    public void setY(int Y) { this.Y = Y; }

    public Vec2I(int X, int Y){
        this.X = X;
        this.Y = Y;
    }

    public Vec2I(){
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

    public int getRange(Vec2I b) {
        int range;
        int[] d = new int[2];

        d[0] = Math.abs( X - b.X );
        d[1] = Math.abs( Y - b.Y );
        range = Math.max( d[0], d[1] );
        return( range );
    }

    public boolean equals(int X, int Y){
        return this.X == X && this.Y == Y;
    }

    public Vec2I minus(Vec2I rhs){
        return new Vec2I(this.X - rhs.X, this.Y - rhs.Y);
    }

    public void copy(Vec2I rhs){
        X = rhs.X;
        Y = rhs.Y;
    }

    @Override
    public Object clone(){
        return new Vec2I(X, Y);
    }

    @Override
    public boolean equals(Object obj){
        if(obj != null && obj instanceof Vec2I){
            Vec2I rhs = (Vec2I)obj;
            return X == rhs.X && Y == rhs.Y;
        }
        return false;
    }

    @Override
    public String toString(){
        return "("+X+", "+Y+")";
    }
}
