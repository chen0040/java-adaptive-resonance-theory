package com.github.chen0040.art.falcon.simulation.minefield.env;

import com.github.chen0040.art.falcon.simulation.utils.Vec2I;
import com.github.chen0040.art.falcon.simulation.utils.VehicleState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen0469 on 9/30/2015 0030.
 */
public class AutonomousVehicle {
    private Vec2I currentPosition = new Vec2I();
    private Vec2I prevPosition = new Vec2I();
    private int currentBearing;
    private int prevBearing;
    private VehicleState state;
    private List<Vec2I> path = new ArrayList<Vec2I>();
    private int step;

    private int id;

    public Vec2I getCurrentPosition(){
        return currentPosition;
    }

    public Vec2I getPrevPosition(){
        return prevPosition;
    }

    public int getRange(Vec2I target){
        return currentPosition.getRange(target);
    }

    public int getStep(){
        return step;
    }

    public int getX(){
        return currentPosition.getX();
    }

    public int getY(){
        return currentPosition.getY();
    }

    public void setX(int X){
        currentPosition.setX(X);
    }

    public void setY(int Y){
        currentPosition.setY(Y);
    }

    public AutonomousVehicle(int agentId){
        this.state = VehicleState.Active;
        this.id = agentId;
    }

    public void initBearing(Vec2I target){
        currentBearing = adjustBearing(getTargetBearing(target));
        prevBearing = currentBearing;
    }

    public int getId(){
        return id;
    }

    public int getCurrentBearing(){
        return currentBearing;
    }

    public int adjustBearing( int old_bearing )
    {
        if( ( old_bearing == 1 ) || ( old_bearing == 7 ) )
            return( 0 );
        if( ( old_bearing == 3 ) || ( old_bearing == 5 ) )
            return( 4 );
        return( old_bearing );
    }

    public int getTargetBearing(Vec2I target)
    {
        if( ( getX() < 0 ) || ( getY() < 0 ) )
            return( 0 );

        Vec2I d = target.minus(currentPosition);

        if( d.getX() == 0 && d.getY() < 0 )
            return( 0 );
        if( d.getX() > 0 && d.getY() < 0 )
            return( 1 );
        if( d.getX() > 0 && d.getY() == 0 )
            return( 2 );
        if( d.getX() > 0 && d.getY() > 0 )
            return( 3 );
        if( d.getX() == 0 && d.getY() > 0 )
            return( 4 );
        if( d.getX() < 0 && d.getY() > 0 )
            return( 5 );
        if( d.getX() < 0 && d.getY() == 0 )
            return( 6 );
        if( d.getX() < 0 && d.getY() < 0 )
            return( 7 );
        return( 0 );
    }

    public boolean isAtPosition(Vec2I position){
        return currentPosition.equals(position);
    }

    public void activate(){
        state = VehicleState.Active;
        prevPosition.copy(currentPosition);
    }

    public boolean isActive(){
        return state == VehicleState.Active;
    }

    public void notifyConflicting(){
        state = VehicleState.Conflicting;
    }

    public boolean isConflicting(){
        return state == VehicleState.Conflicting;
    }

    public double[] getSonar(MineField maze)
    {
        double[] new_sonar = new double[5];

        int x = getX();
        int y = getY();

        if(maze.isOutOfField(this.getCurrentPosition())) {
            for (int k=0; k < 5; k++) new_sonar[k] = 0;
            return new_sonar;
        }

        double[] aSonar = new double[8];

        int r = 0;
        while( y-r >= 0 && !maze.hasMine(x, y-r)) r++;
        if (r == 0) aSonar[0] = 0.0;
        else aSonar[0] = 1.0 / (double)r;

        r=0;
        while(x+r <= maze.getSize()-1 && y-r >= 0 && !maze.hasMine(x+r, y-r)) r++;
        if (r==0) aSonar[1] = 0.0;
        else aSonar[1] = 1.0 / (double)r;

        r=0;
        while (x+r <= maze.getSize()-1 && !maze.hasMine(x+r, y)) r++;
        if (r==0) aSonar[2] = 0.0;
        else aSonar[2] = 1.0 / (double)r;

        r=0;
        while (x+r <= maze.getSize()-1 && y+r <= maze.getSize()-1 && !maze.hasMine(x+r, y+r)) r++;
        if (r==0) aSonar[3] = 0.0;
        else aSonar[3] = 1.0 / (double)r;

        r=0;
        while (y+r <= maze.getSize()-1 && !maze.hasMine(x, y+r)) r++;
        if (r==0) aSonar[4] = 0.0;
        else aSonar[4] = 1.0 / (double)r;

        r=0;
        while (x-r >= 0 && y+r <= maze.getSize()-1 && !maze.hasMine(x-r, y+r)) r++;
        if (r==0) aSonar[5] = 0.0;
        else aSonar[5] = 1.0 / (double)r;

        r=0;
        while (x-r>=0 && !maze.hasMine(x-r, y)) r++;
        if (r==0) aSonar[6] = 0.0;
        else aSonar[6] = 1.0 / (double)r;

        r=0;
        while (x-r >= 0 && y-r >= 0 && !maze.hasMine(x-r, y-r)) r++;
        if (r==0) aSonar[7] = 0.0;
        else aSonar[7] = 1.0 / (double)r;

        for (int k=0; k < 5; k++) {
            new_sonar[k] = aSonar[(currentBearing + 6 + k) % 8];
            if (maze.binarySonar)
                if (new_sonar[k] < 1)
                    new_sonar[k]=0; // binary sonar signal
        }

        return new_sonar;
    }

    public double[] getAVSonar(MineField maze)
    {
        double [] new_av_sonar = new double[5];

        int x = getX();
        int y = getY();

        if(x < 0 || y < 0) {
            for (int k=0; k<5; k++)
                new_av_sonar[k] = 0;
            return new_av_sonar;
        }

        double[] aSonar = new double[8];

        int r = 0;
        while( y-r>=0 && (this == maze.getAgent(x, y-r) || !maze.hasAgent(x, y-r)) ) r++;
        if (r==0) aSonar[0] = 0.0;
        else aSonar[0] = 1.0 / (double)r;

        r=0;
        while (x+r <= maze.getSize()-1 && y-r>=0 && (this == maze.getAgent(x+r, y-r) || !maze.hasAgent(x+r, y-r) ) ) r++;
        if (r==0) aSonar[1] = 0.0;
        else aSonar[1] = 1.0 / (double)r;

        r=0;
        while (x+r <= maze.getSize()-1 && (this == maze.getAgent(x+r, y) || !maze.hasAgent(x+r, y))) r++;
        if (r==0) aSonar[2] = 0.0;
        else aSonar[2] = 1.0 / (double)r;

        r=0;
        while (x+r<=maze.getSize()-1 && y+r<=maze.getSize()-1 && (this == maze.getAgent(x+r, y+r) || !maze.hasAgent(x+r, y+r))) r++;
        if (r==0) aSonar[3] = 0.0;
        else aSonar[3] = 1.0 / (double)r;

        r=0;
        while (y+r<=maze.getSize()-1 && (this == maze.getAgent(x, y+r) || !maze.hasAgent(x, y+r))) r++;
        if (r==0) aSonar[4] = 0.0;
        else aSonar[4] = 1.0 / (double)r;

        r=0;
        while (x-r>=0 && y+r<=maze.getSize()-1 && (this == maze.getAgent(x-r, y+r) || !maze.hasAgent(x-r, y+r))) r++;
        if (r==0) aSonar[5] = 0.0;
        else aSonar[5] = 1.0 / (double)r;

        r=0;
        while (x-r>=0 && (this == maze.getAgent(x-r, y) || !maze.hasAgent(x-r, y))) r++;
        if (r==0) aSonar[6] = 0.0;
        else aSonar[6] = 1.0 / r;

        r=0;
        while (x-r>=0 && y-r>=0 && (this == maze.getAgent(x-r, y-r) || !maze.hasAgent(x-r, y-r))) r++;
        if (r==0) aSonar[7] = 0.0;
        else aSonar[7] = 1.0 / (double) r;

        for (int k=0; k<5; k++) {
            new_av_sonar[k] = aSonar[(currentBearing + 6 + k) % 8];
            if(maze.binarySonar)
                if( new_av_sonar[k] < 1 )
                    new_av_sonar[k] = 0; // binary sonar signal
        }

        return new_av_sonar;
    }

    public void turn(int d)
    {
        int bearing = currentBearing;
        bearing = ( bearing + d ) % 8;
        currentBearing = bearing;
    }

    private void decX(){
        currentPosition.decX();
    }

    private void incX(){
        currentPosition.incX();
    }

    private void decY(){
        currentPosition.decY();
    }

    private void incY(){
        currentPosition.incY();
    }

    public int move(MineField maze, int d) {
        if (maze.isOutOfField(this.getCurrentPosition()))
            return -1;

        prevPosition.copy(currentPosition);
        prevBearing = currentBearing;

        currentBearing = ( currentBearing + d + 8 ) % 8;

        switch (currentBearing) {
            case 0:
                if (getY() > 0) decY();
                else return -1;
                break;
            case 1:
                if (getX() < maze.getSize()-1 && getY() > 0) {
                    incX();
                    decY();
                }
                else return( -1 );
                break;
            case 2:
                if (getX() < maze.getSize()-1) incX();
                else return -1;
                break;
            case 3:
                if (getX() < maze.getSize()-1 && getY() < maze.getSize()-1) {
                    incX();
                    incY();
                }
                else return( -1 );
                break;
            case 4:
                if (getY() < maze.getSize()-1) incY();
                else return -1;
                break;
            case 5:
                if (getX() > 0 && getY() < maze.getSize()-1) {
                    decX();
                    incY();
                }
                else return -1;
                break;
            case 6:
                if (getX() > 0) decX();
                else return -1;
                break;
            case 7:
                if (getX() > 0 && getY() > 0) {
                    decX();
                    decY();
                }
                else return( -1 );
                break;
            default: break;
        }

        maze.updateVehicleState(this);

        step++;
        path.add((Vec2I) getCurrentPosition().clone());

        return (1);
    }

    public void notifyHitMine(){
        state = VehicleState.HitMine;
    }

    public void notifyHitTarget(){
        state = VehicleState.HitTarget;
    }

    public Vec2I virtual_move(MineField maze, int d)
    {
        int bearing = ( currentBearing + d + 8 ) % 8;

        int[] res = new int[2];
        res[0] = getX();
        res[1] = getY();

        switch( bearing )
        {
            case 0:
                if( res[1] > 0 )
                    res[1]--;
                break;
            case 1:
                if( ( res[0] < maze.getSize() - 1 ) && ( res[1] > 0 ) )
                {
                    res[0]++;
                    res[1]--;
                }
                break;
            case 2:
                if( res[0] < maze.getSize() - 1 )
                    res[0]++;
                break;
            case 3:
                if( ( res[0] < maze.getSize() - 1 ) && ( res[1] < maze.getSize() - 1 ) )
                {
                    res[0]++;
                    res[1]++;
                }
                break;
            case 4:
                if( res[1] < maze.getSize() - 1 )
                    res[1]++;
                break;
            case 5:
                if( ( res[0] > 0 ) && ( res[1] < maze.getSize() - 1 ) )
                {
                    res[0]--;
                    res[1]++;
                }
                break;
            case 6:
                if( res[0] > 0 )
                    res[0]--;
                break;
            case 7:
                if( ( res[0] > 0 ) && ( res[1] > 0 ) )
                {
                    res[0]--;
                    res[1]--;
                }
                break;
            default:
                break;
        }

        return new Vec2I(res[0], res[1]);
    }

    public void undoMove(){
        this.currentBearing = this.prevBearing;
        currentPosition.copy(prevPosition);
    }


    public List<Vec2I> getPath() {
        return path;
    }

    public boolean isHitTarget() {
        return state == VehicleState.HitTarget;
    }

    public boolean isHitMine(){
        return state == VehicleState.HitMine;
    }
}
