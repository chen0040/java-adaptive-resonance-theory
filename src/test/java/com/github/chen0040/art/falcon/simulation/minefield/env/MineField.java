package com.github.chen0040.art.falcon.simulation.minefield.env;

import com.github.chen0040.art.falcon.simulation.utils.RewardType;
import com.github.chen0040.art.falcon.simulation.utils.Vec2I;

import java.util.List;

/**
 * Created by chen0469 on 9/30/2015 0030.
 */
public class MineField {
    private  int size=16;
    private  int numMines=10;
    public final boolean binarySonar = false;

    public RewardType rewardType = RewardType.Exp;
    private int numVehicles;

    public Vec2I target;

    private int[][] vehicle_signals;
    private int[][] mines;

    private AutonomousVehicle[] vehicles;
    private int[] minSteps;

    public int getSize(){
        return size;
    }

    public MineField(int size, int numMines, int numAgents) {
        refreshMaze(size, numMines, numAgents);
    }

    private int[][] createSquare(int size, int value) {
        int[][] tiles = new int[size][];
        for (int i = 0; i < size; ++i) {
            tiles[i] = new int[size];
            for(int j=0; j < size; ++j) {
                tiles[i][j] = value;
            }
        }

        return tiles;
    }

    public boolean hasAgent(int x, int y){
        return vehicle_signals[x][y] >= 0;
    }

    public boolean hasMine(int x, int y){
        return mines[x][y] == 1;
    }

    public void updateVehicleState(AutonomousVehicle vehicle){
        Vec2I prevPosition = vehicle.getPrevPosition();

        vehicle_signals[prevPosition.getX()][prevPosition.getY()] = -1;

        if(isHitMine(vehicle)) {
            vehicle.notifyHitMine();
        } else if(isHitTarget(vehicle)) {
            vehicle.notifyHitTarget();
        } else {
            vehicle_signals[vehicle.getX()][vehicle.getY()] = vehicle.getId();
        }
    }

    public boolean isOutOfField(Vec2I pos){
        return pos.getX() < 0  ||  pos.getY() < 0;
    }

    public double getReward(int vehicleId, boolean immediate){
        return getReward(vehicles[vehicleId].getCurrentPosition(), immediate);
    }

    public double getReward(Vec2I pos, boolean immediate)
    {
        if(target.equals(pos) ) // reach target
        {
            return 1;
        }

        if(isOutOfField(pos)) return -1;
        if(hasMine(pos.getX(), pos.getY()))  return 0;

        if( immediate) {
            if (rewardType == RewardType.Linear) {
                int r = pos.getRange(target);
                if (r > 10) r = 10;
                return 1.0 - (double)r / 10.0; //adjust intermediate reward
            } else
                return 1.0 / (1 + pos.getRange(target)); //adjust intermediate reward
        }
        return 0.0; //no intermediate reward
    }

    public double getReward(AutonomousVehicle vehicle, int d, boolean immediate)
    {
        Vec2I next_pos = vehicle.virtual_move(this, d);
        double r = getReward(next_pos, immediate);
        return r;
    }

    public void moveTarget()
    {
        Vec2I new_pos = new Vec2I();
        int b;
        do {
            b = ( int )( Math.random() * size );
            new_pos = targetTestMove(b);
        } while( !isTargetPositionValid(new_pos) );
        move_target( b );
        return;
    }

    public boolean isTargetPositionValid(Vec2I pos)
    {
        int x = pos.getX();
        int y = pos.getY();

        if( ( x < 0 ) || ( x >= size ) )
            return( false );
        if( ( y < 0 ) || ( y >= size ) )
            return( false );
        if( hasAgent(x, y) )
            return( false );
        return !hasMine(x, y);
    }

    public Vec2I targetTestMove(int d)
    {
        int[] new_pos = new int[2];

        new_pos[0] = target.getX();
        new_pos[1] = target.getY();
        switch( d )
        {
            case 0:
                new_pos[1]--;
                break;
            case 1:
                new_pos[0]++;
                new_pos[1]--;
                break;
            case 2:
                new_pos[0]++;
                break;
            case 3:
                new_pos[0]++;
                new_pos[1]++;
                break;
            case 4:
                new_pos[1]++;
                break;
            case 5:
                new_pos[0]--;
                new_pos[1]++;
                break;
            case 6:
                new_pos[0]--;
                break;
            case 7:
                new_pos[0]--;
                new_pos[1]--;
                break;
            default:
                break;
        }
        return new Vec2I(new_pos[0], new_pos[1]);
    }

    public void move_target( int d )
    {
        switch( d )
        {
            case 0:
                target.decY();
                break;
            case 1:
                target.incX();
                target.decY();
                break;
            case 2:
                target.incX();
                break;
            case 3:
                target.incX();
                target.incY();
                break;
            case 4:
                target.incY();
                break;
            case 5:
                target.decX();
                target.incY();
                break;
            case 6:
                target.decX();
                break;
            case 7:
                target.decX();
                target.decY();
                break;
            default:
                break;
        }
    }

    public double[] getSonar(int vehicleId){
        return vehicles[vehicleId].getSonar(this);
    }

    public double[] getAVSonar(int vehicleId){
        return vehicles[vehicleId].getAVSonar(this);
    }

    public void refreshMaze(int size, int numMines, int numAgents) {
        this.size = size;
        this.numMines = numMines;
        this.numVehicles = numAgents;

        target = new Vec2I();
        vehicle_signals = createSquare(size, -1);
        mines = createSquare(size, 0);

        vehicles = new AutonomousVehicle[numVehicles];
        minSteps = new int[numVehicles];
        for(int k = 0; k < numVehicles; k++ ) {
            vehicles[k] = new AutonomousVehicle(k);
        }

        int x, y;
        for(int k = 0; k < numVehicles; k++ ) {
            do {
                x = (int) (Math.random()*size);
                vehicles[k].setX(x);
                y = (int) (Math.random()*size);
                vehicles[k].setY(y);
            } while(hasAgent(x, y));

            vehicle_signals[x][y] = vehicles[k].getId();

            vehicles[k].activate();
        }

        do {
            x = (int) (Math.random()*size);
            target.setX(x);
            y = (int) (Math.random()*size);
            target.setY(y);
        } while (hasAgent(x, y));

        for( int i = 0; i < numMines; i++ ) {
            do {
                x = ( int )( Math.random() * size );
                y = ( int )( Math.random() * size );
            } while( hasAgent(x, y) || hasMine(x, y) || target.equals(x, y) );
            mines[x][y] = 1;
        }

        for( int k = 0; k < numVehicles; k++ ) {
            vehicles[k].initBearing(target);
            minSteps[k] = vehicles[k].getCurrentPosition().getRange(target);
        }
    }

    public int getMinStep(int vehicleId){
        return minSteps[vehicleId];
    }

    public void setConflict(int i, int j)
    {
        AutonomousVehicle vehicle_i = vehicles[i];
        AutonomousVehicle vehicle_j = vehicles[j];

        int X = vehicle_i.getX();
        int Y = vehicle_i.getY();

        vehicle_signals[X][Y] = -1;
        vehicle_i.notifyConflicting();
        vehicle_j.notifyConflicting();
    }

    public boolean checkConflict(int i) {
        AutonomousVehicle vehicle = vehicles[i];
        if( vehicle.isAtPosition(target) )
            return false;
        if( vehicle.isConflicting())
            return true;
        if( isOutOfField(vehicle.getCurrentPosition()))
            return false;
        for(int k = 0; k < numVehicles; k++ ) {
            if( k == i ) continue;
            if( vehicles[k].isAtPosition(vehicle.getCurrentPosition()) ) {
                setConflict(i, k);
                return true;
            }
        }
        return false;
    }

    public boolean isActive(int vehicleId){
        return vehicles[vehicleId].isActive();
    }

    public boolean checkConflict(int vehicleId, Vec2I pos, boolean actual)
    {
        for(int k = 0; k < numVehicles; k++ )
        {
            if(k == vehicleId) continue;
            if(vehicles[k].isAtPosition(pos)) {
                if(actual) {
                    setConflict( vehicleId, k );
                }
                return true;
            }
        }
        return false;
    }

    public AutonomousVehicle getAgent(int x, int y){
        int vehicleId = vehicle_signals[x][y];
        if(vehicleId == -1) return null;
        return vehicles[vehicleId];
    }


    public int getTargetBearing(int vehicleId) {
        return vehicles[vehicleId].getTargetBearing(target);
    }

    public int getCurrentBearing(int vehicleId) {
        return vehicles[vehicleId].getCurrentBearing();
    }

    public double getTargetRange( int i )
    {
        return 1.0 / (1 + vehicles[i].getRange(target));
    }

    // return true if the move still keeps the vehicle within the field
    public boolean withinField(int vehicleId, int d) {
        AutonomousVehicle vehicle = vehicles[vehicleId];
        int testBearing;

        testBearing = (vehicle.getCurrentBearing() + d + 8 ) % 8;
        switch (testBearing) {
            case 0:
                if (vehicle.getY() > 0)
                    return (true);
                break;
            case 1:
                if (vehicle.getX() < size-1 && vehicle.getY() > 0)
                    return( true );
                break;
            case 2:
                if (vehicle.getX() < size-1) return (true);
                break;
            case 3:
                if (vehicle.getX() < size-1 && vehicle.getY() < size-1)
                    return( true );
                break;
            case 4:
                if (vehicle.getY() < size-1)
                    return( true );
                break;
            case 5:
                if (vehicle.getX() > 0 && vehicle.getY() < size-1)
                    return (true);
                break;
            case 6:
                if (vehicle.getX() > 0)
                    return( true );
                break;
            case 7:
                if (vehicle.getX() > 0 && vehicle.getY() > 0)
                    return( true );
                break;
            default: break;
        }
        return (false);
    }

    public void turn(int vehicleId, int b){
        AutonomousVehicle vehicle = vehicles[vehicleId];
        vehicle.turn(b);
    }

    public boolean isHitMine(int vehicleId){
        AutonomousVehicle vehicle = vehicles[vehicleId];
        return isHitMine(vehicle);
    }

    public boolean isHitMine(AutonomousVehicle vehicle){
        if(vehicle.isHitMine()){
            return true;
        }
        return hasMine(vehicle.getX(), vehicle.getY());
    }

    public boolean willHitMine(int vehicleId, int d){
        Vec2I pos = vehicles[vehicleId].virtual_move(this, d);
        return hasMine(pos.getX(), pos.getY());
    }

    public boolean willHitTarget(int vehicleId, int d){
        Vec2I pos = vehicles[vehicleId].virtual_move(this, d);
        return target.equals(pos);
    }

    public boolean isHitTarget(int vehicleId){
        AutonomousVehicle vehicle = vehicles[vehicleId];
        if(vehicle.isHitTarget()){
            return true;
        }
        return isHitTarget(vehicle);
    }

    public boolean isHitTarget(AutonomousVehicle vehicle){
        return target.equals(vehicle.getCurrentPosition());
    }

    public int move(int vehicleId, int d){
        AutonomousVehicle vehicle = vehicles[vehicleId];
        return vehicle.move(this, d);
    }

    public boolean isConflicting(int vehicleId) {
        AutonomousVehicle vehicle = vehicles[vehicleId];
        return vehicle.isConflicting();
    }

    public double getRange(int vehicleId) {
        return vehicles[vehicleId].getRange(target);
    }

    public int[][] getCurrentPositions() {
        int[][] positions = new int[numVehicles][];
        for(int i=0; i < numVehicles; ++i){
            positions[i] = new int[2];
            positions[i][0] = vehicles[i].getX();
            positions[i][1] = vehicles[i].getY();
        }
        return positions;
    }

    public int[] getTargetPosition() {
        int[] position = new int[2];
        position[0] = target.getX();
        position[1] = target.getY();

        return position;
    }

    public int getMine(int i, int j) {
        return mines[i][j];
    }

    public int[][] getPath(int vehicleId) {
        List<Vec2I> path = vehicles[vehicleId].getPath();
        int[][] path2 = new int[path.size()][];
        for(int i=0; i < path2.length; ++i){
            int[] path2comp = new int[2];
            Vec2I point = path.get(i);
            path2comp[0] = point.getX();
            path2comp[1] = point.getY();
            path2[i] = path2comp;
        }
        return path2;
    }
}
